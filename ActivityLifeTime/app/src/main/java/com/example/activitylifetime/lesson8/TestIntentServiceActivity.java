package com.example.activitylifetime.lesson8;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.activitylifetime.R;

import java.util.Random;

public class TestIntentServiceActivity extends AppCompatActivity {

    private LinearLayout ll_container;
    private final BroadcastReceiver forSumReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.i("TAG", "onReceive ()");
                    if (intent.getAction() == Constans.ACTION_RESULT) {
                        int a = intent.getIntExtra(Constans.A, 0);
                        int result = intent.getIntExtra(Constans.RESULT, 0);
                        Log.i("TAG", "onReceive --result:" + result);
                        handleResult(a, result);
                    }
                }
            };

    public static class Constans {
        public static final String ACTION_FOR_SUM = "action_sum";
        public static final String ACTION_RESULT = "action_result";
        public static final String RESULT = "result";
        public static final String A = "a";
        public static final String B = "b";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_intent_service);
        ll_container = findViewById(R.id.ll_container);
        Log.i("TEST", "MainActivity:" + android.os.Process.myTid());
        registerBroadcast();
        Button button = findViewById(R.id.testIntentButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTask(v);
            }
        });
    }

    private void handleResult(int a, int result) {
        TextView textView = ll_container.findViewWithTag(a);
        String old = textView.getText().toString();
        String newText = old.replaceAll("  正在计算中...", result + "  计算Success");
        textView.setText(newText);
    }

    private void registerBroadcast() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constans.ACTION_RESULT);
        registerReceiver(forSumReceiver, intentFilter);
    }

    private int a = 1;

    public void addTask(View view) {
        int b = new Random().nextInt(101) + 1;
        MyIntentService.startMyIntentService(this, a, b);
        TextView textView = new TextView(this);
        textView.setText(a + "+" + b + "= " + "  正在计算中...");
        textView.setTag(a);
        ll_container.addView(textView);
        a++;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(forSumReceiver);
    }
}
