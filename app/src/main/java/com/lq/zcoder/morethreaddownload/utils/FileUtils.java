package com.lq.zcoder.morethreaddownload.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description : 文件辅助类
 */

public class FileUtils {
    //创建随机访问文件
    public static RandomAccessFile createFile(File file){
        try {
           return new RandomAccessFile(file,"rwd");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void saveFile(InputStream inputStream) {

    }

    //文件是否存在
    public static Boolean isExist(String path,String fileName){
        File file = new File(path,fileName);
        if(file.exists()){
            return true;
        }
        return false;
    }

    //判断文件是否完整
    public static Boolean isFull(String path,String fileName,long size){
        File file = new File(path,fileName);
        if(file.exists()&&file.length()==size){
            return true;
        }

        return false;
    }
}
