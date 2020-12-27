package com.example.activitylifetime;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.activitylifetime.lauchmode.Activity1;

public class TestJavaActivity extends AppCompatActivity {
    private static final String TAG = "TestJavaActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_java);
        Button button = findViewById(R.id.activity_java_button);
        button.setOnClickListener(new MyOnClickListener() {
            @Override
            public void onClick(View v) {
                super.onClick(v);
                Intent intent = new Intent(TestJavaActivity.this, Activity1.class);
                startActivity(intent);
            }
        });
    }

    public void onButtonClick(@Nullable View v) {
        Log.i(TAG, "yep yep yep");
        Intent intent = new Intent(TestJavaActivity.this, Activity1.class);
        startActivity(intent);
    }

    static class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.i(TAG, "jiayou buyao fangqi");
        }
    }
}