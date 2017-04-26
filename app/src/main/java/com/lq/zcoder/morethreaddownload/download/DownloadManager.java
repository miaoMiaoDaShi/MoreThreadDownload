package com.lq.zcoder.morethreaddownload.download;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.lq.zcoder.morethreaddownload.exception.DownloadException;
import com.lq.zcoder.morethreaddownload.listener.DownloadListener;
import com.lq.zcoder.morethreaddownload.utils.FileUtils;

import java.io.File;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description :
 */

public class DownloadManager implements DownloadListener {
    private final String TAG = "DownloadManager";
    private static Downloader mDownloader;
    //下载地址
    private String mUrl;
    //存储位置
    private String mPath;
    //开启的线程的个数
    private int mThreadCount;
    //文件的名字
    private String mFileName;

    //下载监听器
    private DownloadListener mDownloadListener;
    //控制任务分发
    private DownloadTask mDownloadTask;
    //默认的下载线程的个数
    private final int DEFAULT_THREAD_COUNT = 3;


    //装载下载器
    public static DownloadManager setup(Downloader downloader) {
        mDownloader = downloader;
        return Single.Single;
    }

    private static class Single {
        private static DownloadManager Single = new DownloadManager();
    }

    public DownloadManager() {
        initDownload();
    }


    //根据用户传入的数据,
    //如果传入空,默认对其进行设置
    private void initDownload() {
        //预先存储数据
        mFileName = mDownloader.getFileName();
        mPath = mDownloader.getPath();
        mUrl = mDownloader.getUrl();
        mThreadCount = mDownloader.getThreadCount();
        mDownloadListener = mDownloader.getListener();
        if (TextUtils.isEmpty(mUrl)) {
            throw new DownloadException("not have downloadUrl");
        }
        //根据传入的链接,获取文件的名字
        String link[] = mUrl.split("/");
        if (TextUtils.isEmpty(mFileName)) {
            mFileName = link[link.length - 1];
        }
        if (TextUtils.isEmpty(mPath)) {
            mPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        if (mThreadCount == 0) {
            mThreadCount = DEFAULT_THREAD_COUNT;
        }
        if (mDownloadListener == null) {
            mDownloadListener = this;
        }

        //重新组装下载器
        mDownloader = new Downloader
                .Builder()
                .setFileName(mFileName)
                .setUrl(mUrl)
                .setPath(mPath)
                //内部消化,,,
                .setListener(mDownloadListener)
                .setThreadCount(mThreadCount)
                .build();
    }

    //停止下载/暂停
    public void stop() {
        mDownloadTask.cancel();
    }

    //开始下载
    public void start() {

        mDownloadTask = new DownloadTask(mDownloader);
        mDownloadTask.execute();
    }

    @Override
    public void onStartDownload(int total) {
        Log.e(TAG, "onStartDownload: ");
    }

    @Override
    public void onProgress(int progress) {
        Log.e(TAG, "onProgress: ");
    }

    @Override
    public void onSuccess(File file) {
        Log.e(TAG, "onSuccess: ");
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "onError: ");
    }

    @Override
    public void onIsExist() {

    }
}
