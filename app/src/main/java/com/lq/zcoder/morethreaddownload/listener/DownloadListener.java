package com.lq.zcoder.morethreaddownload.listener;

import java.io.File;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description : 下载器的监听器
 */

public interface DownloadListener {
    //开始下载
    void onStartDownload(int total);
    //下载中
    void onProgress(int progress);
    //下载成功
    void onSuccess(File file);
    //下载错误
    void onError(Throwable e);
    //文件存在
    void onIsExist();
}
