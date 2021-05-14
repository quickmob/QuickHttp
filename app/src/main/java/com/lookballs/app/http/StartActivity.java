package com.lookballs.app.http;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;


public class StartActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void startBtn(View view) {
        startActivity(new Intent(StartActivity.this, MainActivity.class));
    }
}
