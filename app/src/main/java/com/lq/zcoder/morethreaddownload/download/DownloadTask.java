package com.lq.zcoder.morethreaddownload.download;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.lq.zcoder.morethreaddownload.entity.FileInfo;
import com.lq.zcoder.morethreaddownload.entity.ThreadInfo;
import com.lq.zcoder.morethreaddownload.listener.DownloadListener;
import com.lq.zcoder.morethreaddownload.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description :
 */

public class DownloadTask extends AsyncTask<Void, Integer, File> {
    private final String TAG = "DownloadTask";
    private Downloader mDownloader;
    private RandomAccessFile mRandomAccessFile;
    private DownloadListener mDownloadListener;
    private FileInfo mFileInfo;
    private File mFile;
    private List<ThreadInfo> mThreadInfos;
    private List<ThreadTask> mThreadTasks;
    private final int WHAT_PROGRESS = 0x11;
    private final int WHAT_DONE = 0x12;

    //是否暂停了下载
    private boolean isPause;

    //开启的线程的个数
    private int mThreadCount;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "handleMessage: ");
            switch (msg.what) {
                //下载中
                case WHAT_PROGRESS:
                    if (mDownloadListener != null) {
                        int currentPosition = 0;
                        //循环获取每个下载的任务的进度
                        for (int i = 0; i < mThreadInfos.size(); i++) {
                            currentPosition += mThreadInfos.get(i).getDonePosition();
                            saveThreadInfo(mThreadInfos.get(i));
                        }
                        //当前完成的大小大于或者等于文件大小,下载完成
                        if (currentPosition >= mFileInfo.getSize()) {
                            Log.e(TAG, "handleMessage: 下载完成");
                            mHandler.sendEmptyMessage(WHAT_DONE);

                        } else if (isPause) {
                            //暂停下,不做任何事
                        } else {
                            sendEmptyMessageDelayed(WHAT_PROGRESS, 50);
                        }
                        publishProgress(currentPosition);
                    }
                    break;
                //下载成功
                case WHAT_DONE:
                    if (mDownloadListener != null) {
                        mDownloadListener.onSuccess(mFile);
                    }
                    removeAllInfo(mFileInfo);
                    break;
            }


        }
    };

    private void saveThreadInfo(ThreadInfo threadInfo) {

    }


    public DownloadTask(Downloader mDownloader) {
        this.mDownloader = mDownloader;
        this.mDownloadListener = mDownloader.getListener();
        mThreadCount = mDownloader.getThreadCount();
        mFileInfo = new FileInfo();
        mFileInfo.setPath(mDownloader.getPath());
        mFileInfo.setFileName(mDownloader.getFileName());
        mFileInfo.setUrl(mDownloader.getUrl());
        mThreadInfos = new ArrayList<>(mDownloader.getThreadCount());
        mThreadTasks = new ArrayList<>(mDownloader.getThreadCount());
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if ((getNetFileInfo()) != null) {
            mFileInfo.setSize(getNetFileInfo().getSize());
            //文件存在且完整,回调存在,并取消下载,直接返回
            Log.e(TAG, "onPreExecute: "+mFileInfo.toString() );
            if (FileUtils.isFull(mFileInfo.getPath(), mFileInfo.getFileName(), mFileInfo.getSize())) {
                mDownloadListener.onIsExist();
                cancel(true);
                return;

            }//如果文件存在
            else if (FileUtils.isExist(mFileInfo.getPath(), mFileInfo.getFileName())) {
                //文件不完整
                if (!FileUtils.isFull(mFileInfo.getPath(), mFileInfo.getFileName(), mFileInfo.getSize())) {
                    return;
                }
            } else {
                //文件不存在进入
                mFile = createRandomAccessFile(mFileInfo.getPath(), mFileInfo.getFileName());
            }

        } else {
            Log.e(TAG, "onPreExecute: ");
            if (mDownloadListener != null) {
                mDownloadListener.onError(new Throwable("下载失败"));
            }
        }
    }


    //创建随机访问文件
    private File createRandomAccessFile(String parent, String child) {
        File file = null;
        try {
            file = new File(parent, child);
            RandomAccessFile randomAccessFile = FileUtils.createRandomAccessFile(file);
            randomAccessFile.setLength(mFileInfo.getSize());
            if (mDownloadListener != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mDownloadListener.onStartDownload((int) mFileInfo.getSize());
                    }
                });

            }
            Log.e(TAG, "onPreExecute: 创建文件成功");

            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }


    @Override
    protected File doInBackground(Void... params) {
        //线程信息
        List<ThreadInfo> threadInfos = new ArrayList<>();
        //
        List<Long> donePositions = new ArrayList<>();
        FileInfo fileInfo = null;
        long blockSize = 0;
        long fileLength = 0;
        int threadCount = 0;
        long startPosition;
        //如果前面取消了下载,直接return
        if (isCancelled()) {
            return null;
        }
        //尝试读取数据库

        //查询文件信息
        fileInfo = queryFileInfo(mFileInfo);
        if (fileInfo == null) {
            threadCount = mThreadCount;
            fileInfo = mFileInfo;
            //保存当前文件信息
            saveFileInfo(fileInfo);
            //创建随机访问文件
            createRandomAccessFile(fileInfo.getPath(), fileInfo.getFileName());

        } else {
            //查询线程信息
            threadInfos = queryThreadInfo(mFileInfo);
            if (threadInfos != null) {
                threadCount = threadInfos.size();
                for (int i = 0; i < threadCount; i++) {
                    donePositions.add(threadInfos.get(i).getDonePosition());
                }
            }
        }

        fileLength = fileInfo.getSize();
        //每个线程下载的大小
        blockSize = fileLength % threadCount == 0 ? fileLength / threadCount : fileLength / threadCount + 1;

        for (int i = 0; i < threadCount; i++) {
            ThreadInfo threadInfo;
            try {
                if (threadCount != threadInfos.size()) {
                    threadInfo = new ThreadInfo();
                    startPosition = i * blockSize;
                    threadInfo.setStartPosition(startPosition);
                    threadInfo.setUrl(fileInfo.getUrl());
                    threadInfo.setId(i);
                    threadInfo.setEndPosition((i+1)*blockSize-1);
                } else {
                    threadInfo = threadInfos.get(i);
                    startPosition = threadInfo.getStartPosition();
                }
                RandomAccessFile randomAccessFile = new RandomAccessFile(new File(fileInfo.getPath(), fileInfo.getFileName()), "rwd");
                randomAccessFile.seek(startPosition);
                ThreadTask threadTask = new ThreadTask(randomAccessFile, threadInfo, fileInfo);
                threadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mDownloader);
                mThreadTasks.add(threadTask);
                mThreadInfos.add(threadInfo);
                Log.e(TAG, "doInBackground: " + mThreadInfos.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mHandler.sendEmptyMessage(WHAT_PROGRESS);
        return mFile;
    }

    //从数据获取下载文件相关的线程的信息
    private List<ThreadInfo> queryThreadInfo(FileInfo mFileInfo) {
        return null;
    }

    //从数据获取文件的信息
    private FileInfo queryFileInfo(FileInfo mFileInfo) {
        return null;
    }

    //下载成功.移除该文件的下载记录
    private void removeAllInfo(FileInfo mFileInfo) {

    }

    private void saveFileInfo(FileInfo mFileInfo) {

    }


    //进度
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mDownloadListener != null) {
            mDownloadListener.onProgress(values[0]);
        }

    }


    //该方法可以获取文件的大小,和是否支持多线程下载
    private FileInfo getNetFileInfo() {
        try {
            Log.e(TAG, "getNetFileInfo 准备获取文件信息_from server ");
            //两次任务是为了分别获取长度和是否支持RANGE
            new GetInfoTask().execute(mFileInfo.getUrl()).get();
            return new GetInfoTask().execute(mFileInfo.getUrl()).get();

        } catch (InterruptedException e) {
            Log.e(TAG, "getNetFileInfo: " + e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.e(TAG, "getNetFileInfo: " + e);
            e.printStackTrace();
        }
        return null;

    }


    //取消下载
    public void cancel() {
        Log.e(TAG, "onCancelled: ");
        isPause = true;
        for (ThreadTask threadTask : mThreadTasks) {
            threadTask.cancel(true);
        }
    }
}
