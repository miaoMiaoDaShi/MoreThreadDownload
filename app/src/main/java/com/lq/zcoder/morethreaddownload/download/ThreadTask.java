package com.lq.zcoder.morethreaddownload.download;

import android.os.AsyncTask;
import android.util.Log;

import com.lq.zcoder.morethreaddownload.entity.FileInfo;
import com.lq.zcoder.morethreaddownload.entity.ThreadInfo;
import com.lq.zcoder.morethreaddownload.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description : 执行下载的每个线程
 */

public class ThreadTask extends AsyncTask<Downloader, Integer, Integer> {
    private final String TAG = "ThreadTask";
    private RandomAccessFile mRandomAccessFile;
    private FileInfo mFileInfo;
    private ThreadInfo mThreadInfo;


    public ThreadTask(RandomAccessFile mRandomAccessFile, ThreadInfo threadInfo, FileInfo mFileInfo) {
        this.mRandomAccessFile = mRandomAccessFile;
        this.mThreadInfo = threadInfo;
        this.mFileInfo = mFileInfo;
        Log.e(TAG, "ThreadTask: "+mThreadInfo.getId() );
    }

    @Override
    protected Integer doInBackground(Downloader... params) {
        Log.e(TAG, "doInBackground: ");
        HttpURLConnection connection = null;
        InputStream is = null;
        int doneLenght = 0;
        try {
            URL url = new URL(mThreadInfo.getUrl());
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestProperty("Range", "bytes=" + mThreadInfo.getStartPosition() + "-");
            if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                Log.e(TAG, "doInBackground: 请求成功" );
                is = connection.getInputStream();
                byte bytes[] = new byte[1024];
                doneLenght = 0;
                long readLenght;
                while (!isCancelled()&&doneLenght < mThreadInfo.getBlockSize() && (readLenght = is.read(bytes)) != -1) {
                    mRandomAccessFile.write(bytes, 0, (int) readLenght);
                    doneLenght += readLenght;
                    mThreadInfo.setDonePosition(doneLenght);
                    Log.e(TAG, "doInBackground: 下载中" );
                }
                Log.e(TAG, "doInBackground: 线程完成"+mThreadInfo.toString() );
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return doneLenght;
    }

}
