package com.lq.zcoder.morethreaddownload.entity;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description : 下载线程相关
 */

public class ThreadInfo {
    private int id;
    private String url;
    private long startPosition;
    private long endPosition;
    private long donePosition;
    private long blockSize;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getBlockSize() {
        return blockSize;
    }

    public void setBlockSize(long blockSize) {
        this.blockSize = blockSize;
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
                ", blockSize=" + blockSize +
                '}';
    }
}
