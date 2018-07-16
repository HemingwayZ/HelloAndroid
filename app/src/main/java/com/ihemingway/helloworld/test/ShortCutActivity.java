package com.ihemingway.helloworld.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ihemingway.helloworld.R;

public class ShortCutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(ShortCutUtils.getAuthorityFromPermission(this)==null){
            Toast.makeText(this,"权限不足",Toast.LENGTH_SHORT).show();
            onBackPressed();
            return;
        }
        ShortCutUtils.addShortCut(this,"快捷方式",R.mipmap.ic_launcher_round, TestShortCutActivity.class);
        Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

}
