package com.ihemingway.helloworld.shortcut;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ihemingway.helloworld.R;

public class TestShortCutActivity extends AppCompatActivity {

    private static final String TAG = TestShortCutActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_short_cut);
        Intent intent = getIntent();
        String action = intent.getAction();
        Log.d(TAG,"action"+action);


        Log.d(TAG,"action2 "+intent.getStringExtra("action"));
        Log.d(TAG,"action"+action);

    }
}
