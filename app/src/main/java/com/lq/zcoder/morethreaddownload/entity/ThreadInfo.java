package com.lq.zcoder.morethreaddownload.entity;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description : 下载线程相关
 */

public class ThreadInfo {
    //数据库键位
    public final static String KEY_URL = "key_url";
    public final static String KEY_STARTPOSITION = "key_startPosition";
    public final static String KEY_ENDPOSITION = "key_endPosition";
    public final static String KEY_DONEPOSITION = "key_endPosition";

    private int id;
    private String url;
    private long startPosition;
    private long endPosition;
    private long donePosition;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(long startPosition) {
        this.startPosition = startPosition;
    }

    public long getEndPosition() {
        return endPosition;
    }

    public void setEndPosition(long endPosition) {
        this.endPosition = endPosition;
    }

    public long getDonePosition() {
        return donePosition;
    }

    public void setDonePosition(long donePosition) {
        this.donePosition = donePosition;
    }

    @Override
    public String toString() {
        return "ThreadInfo{" +
                "url='" + url + '\'' +
                ", startPosition=" + startPosition +
                ", endPosition=" + endPosition +
                ", donePosition=" + donePosition +
                '}';
    }
}
