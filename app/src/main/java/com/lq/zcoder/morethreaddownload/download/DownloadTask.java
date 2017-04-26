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

    //下载地址
    private String mUrl;
    //存储位置
    private String mPath;
    //开启的线程的个数
    private int mThreadCount;
    //文件的名字
    private String mFileName;

    //文件大小
    private long mFileLenght;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.e(TAG, "handleMessage: ");
            switch (msg.what) {
                case WHAT_PROGRESS:
                    if (mDownloadListener != null) {
                        int currentPosition = 0;
                        for (int i = 0; i < mThreadInfos.size(); i++) {
                            currentPosition += mThreadInfos.get(i).getDonePosition();
                        }
                        if (currentPosition >= mFileInfo.getSize()) {
                            Log.e(TAG, "handleMessage: 下载完成");
                            mHandler.sendEmptyMessage(WHAT_DONE);
                        } else if (isPause) {

                        } else {
                            sendEmptyMessageDelayed(WHAT_PROGRESS, 50);
                        }
                        mDownloadListener.onProgress(currentPosition);
                    }
                    break;
                case WHAT_DONE:
                    if (mDownloadListener != null) {
                        mDownloadListener.onSuccess(mFile);
                    }
                    break;
            }


        }
    };

    public DownloadTask(Downloader mDownloader) {
        this.mDownloader = mDownloader;
        this.mDownloadListener = mDownloader.getListener();
        mThreadCount = mDownloader.getThreadCount();
        mFileName = mDownloader.getFileName();
        mPath = mDownloader.getPath();
        mUrl = mDownloader.getUrl();
        mThreadInfos = new ArrayList<>(mDownloader.getThreadCount());
        mThreadTasks = new ArrayList<>(mDownloader.getThreadCount());
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if ((mFileInfo = getNetFileInfo()) != null) {
            mFileLenght = mFileInfo.getSize();
            if (FileUtils.isFull(mPath, mFileName, mFileLenght)) {
                mDownloadListener.onIsExist();
                cancel();
                return;
            }
            try {
                mFile = new File(mPath, mFileName);
                mRandomAccessFile = FileUtils.createFile(mFile);
                mRandomAccessFile.setLength(mFileLenght);
                if (mDownloadListener != null) {
                    mDownloadListener.onStartDownload((int) mFileLenght);
                    Log.e(TAG, "onPreExecute: 设置最大进度" + mFileLenght);
                }
                Log.e(TAG, "onPreExecute: 创建文件成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "onPreExecute: ");
            if (mDownloadListener != null) {
                mDownloadListener.onError(new Throwable("下载失败"));
            }
            return;
        }
    }


    @Override
    protected File doInBackground(Void... params) {
        Log.e(TAG, "doInBackground: 线程" + mThreadCount);
        if(isCancelled()){
            return null;
        }
        long startPosition;
        long block = mFileLenght % mThreadCount == 0 ? mFileLenght / mThreadCount : mFileLenght / mThreadCount + 1;
        for (int i = 0; i < mThreadCount; i++) {
            try {
                startPosition = i * block;
                RandomAccessFile randomAccessFile = new RandomAccessFile(mFile, "rwd");
                randomAccessFile.seek(startPosition);
                ThreadInfo threadInfo = new ThreadInfo();
                threadInfo.setBlockSize(block);
                threadInfo.setStartPosition(startPosition);
                threadInfo.setUrl(mUrl);
                threadInfo.setId(i);
                ThreadTask threadTask = new ThreadTask(randomAccessFile, threadInfo, mFileInfo);
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


    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        if (mDownloadListener != null) {
            mDownloadListener.onProgress(values[0]);
        }

    }

    private FileInfo getNetFileInfo() {
        try {
            Log.e(TAG, "getNetFileInfo 准备获取文件信息_from server ");
            //两次任务是为了分别获取长度和是否支持RANGE

            mFileInfo = new GetInfoTask().execute(mUrl).get();
            mFileInfo.setPath(mPath);
            mFileInfo.setFileName(mFileName);
            return new GetInfoTask().execute(mUrl).get();

        } catch (InterruptedException e) {
            Log.e(TAG, "getNetFileInfo: " + e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            Log.e(TAG, "getNetFileInfo: " + e);
            e.printStackTrace();
        }
        return null;

    }

    private boolean isPause;

    public void cancel() {
        Log.e(TAG, "onCancelled: ");
        isPause = true;
        for (ThreadTask threadTask : mThreadTasks) {
            threadTask.cancel(true);
        }
    }
}
