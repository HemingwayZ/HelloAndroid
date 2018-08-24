package com.ihemingway.helloworld.filereader;

import android.Manifest;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ihemingway.helloworld.R;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsReaderView;

import java.io.File;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;


@RuntimePermissions
public class FileReaderActivity extends AppCompatActivity {

    private TbsReaderView tbsReaderView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_reader);
//        QbSdk.initX5Environment(FileReaderActivity.this, new QbSdk.PreInitCallback() {
//            @Override
//            public void onCoreInitFinished() {
//
//            }
//
//            @Override
//            public void onViewInitFinished(boolean b) {
//
//            }
//        });


        RelativeLayout linearLayout = findViewById(R.id.ll_container);

        tbsReaderView = new TbsReaderView(this, new TbsReaderView.ReaderCallback() {
            @Override
            public void onCallBackAction(Integer integer, Object o, Object o1) {

            }
        });
        linearLayout.addView(tbsReaderView, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

//        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        tbsReaderView.setLayoutParams(layoutParams);

//        String fileName = "file:///android_asset/testWord.docx";/sdcard/testWord.docx
        findViewById(R.id.btnOpen).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileReaderActivityPermissionsDispatcher.openOfficeWithPermissionCheck(FileReaderActivity.this);
            }
        });


    }

    public void openUrl(){
        String fileName = Environment.getExternalStorageDirectory().getPath() + "/test_word2.docx";
        File file = new File(fileName);
        if (!file.exists()) {
            Toast.makeText(this, "File is not exist!!!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "File is exist!", Toast.LENGTH_SHORT).show();
        }
        boolean isCanOpen = tbsReaderView.preOpen("docx", false);
        if (isCanOpen) {
            Bundle bundle = new Bundle();
            bundle.putString(TbsReaderView.KEY_FILE_PATH, file.getPath());
            bundle.putString(TbsReaderView.KEY_TEMP_PATH, Environment.getExternalStorageDirectory().getPath());
            bundle.putBoolean(TbsReaderView.IS_BAR_SHOWING,true);
            tbsReaderView.openFile(bundle);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        tbsReaderView.onStop();
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    void openOffice() {
        openUrl();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        FileReaderActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnPermissionDenied(Manifest.permission.READ_PHONE_STATE)
    void onDenied() {
    }

    @OnNeverAskAgain(Manifest.permission.READ_PHONE_STATE)
    void onNeverAskAgain() {
    }
}
