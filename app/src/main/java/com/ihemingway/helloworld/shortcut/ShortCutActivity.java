package com.ihemingway.helloworld.shortcut;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ihemingway.helloworld.R;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnNeverAskAgain;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

//@RuntimePermissions
//快捷方式不是运行时权限 无法通过运行时权限的方式检测权限是否存在
public class ShortCutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        ShortCutActivityPermissionsDispatcher.addShortCutWithPermissionCheck(this);
        addShortCut();
    }


//    @NeedsPermission(Manifest.permission.INSTALL_SHORTCUT)
    public void addShortCut(){
        ShortCutUtils.addShortCut(this,"快捷方式",R.mipmap.ic_launcher_round, TestShortCutActivity.class);
        Toast.makeText(this,"添加成功",Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

//    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    @OnNeverAskAgain(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    void onPermissionDeny(){
//        Toast.makeText(this,"无权限",Toast.LENGTH_SHORT).show();
//    }
//
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        ShortCutActivityPermissionsDispatcher.onRequestPermissionsResult(this,requestCode,grantResults);
//    }
}
