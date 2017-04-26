package com.lq.zcoder.morethreaddownload.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lq.zcoder.morethreaddownload.entity.FileInfo;
import com.lq.zcoder.morethreaddownload.entity.ThreadInfo;

/**
 * Created by Zcoder
 * Email : 1340751953@qq.com
 * Time :  2017/4/25
 * Description :
 */

public class DBHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "download.db";
    private final static int VERSION = 1;
    private final String CREATE = "CREATE TABLE IF NOT EXISTS ";
    private final String TB_FILEINFO = "tb_fileinfo";
    private final String TB_THREADINFO = "tb_threadinfo";

    public DBHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE + TB_FILEINFO + "(_id integer PRIMARY KEY," + FileInfo.KEY_URL + " TEXT," + FileInfo.KEY_PATH + " TEXT," + FileInfo.KEY_FILENAME + " TEXT," + FileInfo.KEY_SIZE + " TEXT," + FileInfo.KEY_THREADCOUNT + " TEXT," + FileInfo.KEY_SUPPORTRANGE + " TEXT" + ");");
        db.execSQL(CREATE + TB_THREADINFO + "(_id integer PRIMARY KEY," + ThreadInfo.KEY_URL + " TEXT," + ThreadInfo.KEY_STARTPOSITION + " TEXT," + ThreadInfo.KEY_ENDPOSITION + " TEXT," + ThreadInfo.KEY_DONEPOSITION + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
