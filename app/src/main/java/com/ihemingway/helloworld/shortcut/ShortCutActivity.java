package com.ihemingway.helloworld.shortcut;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ihemingway.helloworld.R;

public class ShortCutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ShortCutUtils.addShortCut(this,"快捷方式",R.mipmap.ic_launcher_round, TestShortCutActivity.class);
        Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

}
