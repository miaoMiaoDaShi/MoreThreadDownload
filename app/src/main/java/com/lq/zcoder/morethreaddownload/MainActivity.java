package com.lq.zcoder.morethreaddownload;

import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lq.zcoder.morethreaddownload.db.DBHelper;
import com.lq.zcoder.morethreaddownload.download.DownloadManager;
import com.lq.zcoder.morethreaddownload.download.Downloader;
import com.lq.zcoder.morethreaddownload.listener.DownloadListener;
import com.lq.zcoder.morethreaddownload.utils.FileUtils;
import com.lq.zcoder.morethreaddownload.view.CircleProgressbar;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, DownloadListener {

    //圆形进度条
    private CircleProgressbar cpb;
    private Button btn_download;
    private Button btn_remove;
    private EditText txt_link;
    //是否在下载中
    private Boolean isDownload = false;
    private DownloadManager mDownloadManager;
    private String[] split;
    private String path;
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initListener();
        split = txt_link.getText().toString().split("/");
        fileName = split[split.length - 1];
        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (FileUtils.isExist(path, fileName)) {
            btn_remove.setText("文件存在,点击删除");
        } else {
            btn_remove.setClickable(false);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase writableDatabase = dbHelper.getWritableDatabase();
    }

    private void initListener() {
        btn_download.setOnClickListener(this);
        btn_remove.setOnClickListener(this);
    }

    private void initView() {
        cpb = (CircleProgressbar) findViewById(R.id.cpb);
        btn_download = (Button) findViewById(R.id.btn_download);
        btn_remove = (Button) findViewById(R.id.btn_remove);
        txt_link = (EditText) findViewById(R.id.txt_link);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            /*
            判断是不是在下载中,是就暂停
             */
            case R.id.btn_download:

                if (isDownload) {
                    mDownloadManager.stop();
                } else {
                    //下载文件
                    downloadFile();
                }
                break;
            case R.id.btn_remove:
                FileUtils.removeFile(path, fileName);
                break;
        }
    }

    private void downloadFile() {
        //下载器的组装
        Downloader downloader = new Downloader
                .Builder()
                .setUrl(txt_link.getText().toString())
                .setListener(this)
                .build();
        //装载下载器
        mDownloadManager = DownloadManager.setup(downloader);
        //下载开始
        mDownloadManager.start();
    }


    @Override
    public void onStartDownload(int total) {
        isDownload = true;
        cpb.setMaxProgress(total);
        btn_download.setText("下载中");
    }

    @Override
    public void onProgress(int progress) {
        cpb.setProgress(progress);
    }

    @Override
    public void onSuccess(File file) {
        isDownload = false;
        btn_download.setText("下载");
    }

    @Override
    public void onError(Throwable e) {
        isDownload = false;
    }

    //如果文件存在且完整,回调
    @Override
    public void onIsExist() {
        isDownload = false;
        btn_download.setClickable(false);
        btn_download.setText("文件存在");
        Toast.makeText(this, "文件存在", Toast.LENGTH_SHORT).show();
    }
}
