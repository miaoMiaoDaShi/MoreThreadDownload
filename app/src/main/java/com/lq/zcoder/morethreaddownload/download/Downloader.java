package com.lq.zcoder.morethreaddownload.download;

import com.lq.zcoder.morethreaddownload.listener.DownloadListener;
/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description :
 */

public class Downloader {
    //下载地址
    private String url;
    //存储位置
    private String path;
    //开启的线程的个数
    private int threadCount;
    //文件的名字
    private String fileName;
    //监听器
    private DownloadListener listener;

    public DownloadListener getListener() {
        return listener;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public int getThreadCount() {
        return threadCount;
    }

    public String getFileName() {
        return fileName;
    }

    private Downloader(Builder builder) {
        url = builder.url;
        fileName = builder.fileName;
        path = builder.path;
        threadCount = builder.threadCount;
        listener = builder.listener;
    }

    public static class Builder {
        //下载地址
        private String url;
        //存储位置
        private String path;
        //开启的线程的个数
        private int threadCount;
        //文件的名字
        private String fileName;
        //监听器
        private DownloadListener listener;

        public Builder setListener(DownloadListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setThreadCount(int threadCount) {
            this.threadCount = threadCount;
            return this;
        }

        public Builder setFileName(String fileName) {
            this.fileName = fileName;
            return this;
        }

        public Downloader build() {
            return new Downloader(this);

        }
    }
}
