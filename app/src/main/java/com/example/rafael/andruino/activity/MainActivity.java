package com.example.rafael.andruino.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.example.rafael.andruino.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openSockectActivity(View v) {
        Intent intent = new Intent(this, SocketActivity.class);
        startActivity(intent);
    }

    public void openPublishSubscribeActivity(View v) {
        Intent intent = new Intent(this, PubnubActivity.class);
        startActivity(intent);
    }
}
