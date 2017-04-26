package com.lq.zcoder.morethreaddownload.download;

import android.os.AsyncTask;
import android.util.Log;

import com.lq.zcoder.morethreaddownload.entity.FileInfo;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description : 从服务器获取一些相关信息,比如  文件是否支持Range,(多线程下载嘛)
 */

public class GetInfoTask extends AsyncTask<String, Void, FileInfo> {
    private final String TAG = "GetInfoTask";
    private static FileInfo mFileInfo = new FileInfo();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected FileInfo doInBackground(String... params) {
        HttpURLConnection connection = null;
        InputStream is = null;
        URL url = null;
        try {
            url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            if (mFileInfo.getSize() == 0) {
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    mFileInfo.setSize(connection.getContentLength());
                }
            } else {
                connection.setRequestProperty("Range", "bytes=" + mFileInfo.getSize() / 2 + "-");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) {
                    mFileInfo.setSupportRange(true);
                }
            }

            Log.e(TAG, "doInBackground: " + mFileInfo.toString());
            return mFileInfo;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return null;
    }
}
