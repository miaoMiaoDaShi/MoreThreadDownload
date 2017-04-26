package com.lq.zcoder.morethreaddownload.entity;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description :
 */

public class FileInfo {
    //数据库键位
    public final static String KEY_FILENAME = "filename";
    public final static String KEY_URL = "key_url";
    public final static String KEY_SUPPORTRANGE = "key_supportRange";
    public final static String KEY_SIZE = "key_size";
    public final static String KEY_PATH = "key_path";
    public final static String KEY_THREADCOUNT = "key_threadcount";


    private String FileName;
    private String url;
    private Boolean supportRange;
    private long size;
    private String path;


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFileName() {
        return FileName;
    }

    public void setFileName(String fileName) {
        FileName = fileName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getSupportRange() {
        return supportRange;
    }

    public void setSupportRange(Boolean supportRange) {
        this.supportRange = supportRange;
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "FileName='" + FileName + '\'' +
                ", url='" + url + '\'' +
                ", supportRange=" + supportRange +
                ", size=" + size +
                '}';
    }
}
