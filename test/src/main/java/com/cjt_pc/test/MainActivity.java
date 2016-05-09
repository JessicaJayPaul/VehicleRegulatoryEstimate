package com.cjt_pc.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View main = getLayoutInflater().inflate(R.layout.activity_main, null);
        View test = getLayoutInflater().inflate(R.layout.test, (ViewGroup) main, false);
        setContentView(test);
    }
}
