package com.ihemingway.helloworld.test;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Parcelable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ihemingway.helloworld.MainActivity;
import com.ihemingway.helloworld.R;

public class ShortCutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShortCutUtils.addShortCut(this,"TestShortCut",R.mipmap.ic_launcher_round,MainActivity.class);
        Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

}
