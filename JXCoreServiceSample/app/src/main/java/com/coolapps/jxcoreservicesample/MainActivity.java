package com.coolapps.jxcoreservicesample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;
import android.view.View;

import com.coolapps.JXCoreService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClick(View v)
    {
        JXCoreService.start(this.getApplicationContext(), null, null);
        Toast.makeText(getApplicationContext(), "JXCore Service started", Toast.LENGTH_LONG).show();
    }
}
