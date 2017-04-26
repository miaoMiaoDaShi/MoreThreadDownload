package com.lq.zcoder.morethreaddownload.exception;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description : 下载的异常类(运行时)
 */
public class DownloadException extends RuntimeException {
    public DownloadException(String message) {
        super(message);
    }
}
