package com.wlj.sate7weather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.jaeger.library.StatusBarUtil;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        setSupportActionBar(findViewById(R.id.toolBar));
//        StatusBarUtil.setTranslucent(this);
        StatusBarUtil.setTranslucentForImageView(this, 0,findViewById(R.id.toolBar));
    }
}