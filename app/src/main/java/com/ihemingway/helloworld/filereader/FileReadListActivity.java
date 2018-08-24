package com.ihemingway.helloworld.filereader;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ihemingway.helloworld.R;

public class FileReadListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_read_list);

        findViewById(R.id.tvTbs).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FileReadListActivity.this, FileReaderActivity.class);
                startActivity(intent);
            }
        });


        findViewById(R.id.tvPoi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FileReadListActivity.this, ReadOfficeActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.tvPoiToLocal).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(FileReadListActivity.this, ReadOfficeLocalActivity.class);
                startActivity(intent);
            }
        });
    }
}
