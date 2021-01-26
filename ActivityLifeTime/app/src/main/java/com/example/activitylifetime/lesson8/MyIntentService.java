package com.example.activitylifetime.lesson8;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class MyIntentService extends IntentService {
    public MyIntentService() {        //必须实现父类的构造方法
        super("MyIntentService");
    }

    @Override
    public void onCreate() {
        Log.i("TEST", "onCreate()");
        super.onCreate();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.i("TEST", "onStart()");
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("TEST", "onStartCommand()");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i("TEST", "onBind()");
        return super.onBind(intent);
    }

    @Override
    public void onDestroy() {
        Log.i("TEST", "onDestroy()");
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("TEST", "onHandleIntent():" + android.os.Process.myTid());
        if (intent != null) {
            String action = intent.getAction();
            if (TestIntentServiceActivity.Constans.ACTION_FOR_SUM.equals(action)) {
                int a = intent.getIntExtra(TestIntentServiceActivity.Constans.A, 0);
                int b = intent.getIntExtra(TestIntentServiceActivity.Constans.B, 0);
                int result = a + b;
                Log.i("TEST", "result: " + result);
                handleResult(a, result);
            }
        }
    }

    private void handleResult(int a, int result) {
        try {            //模拟计算耗时
            Thread.sleep(1000);
            Intent intent = new Intent(TestIntentServiceActivity.Constans.ACTION_RESULT);
            intent.putExtra(TestIntentServiceActivity.Constans.RESULT, result);
            intent.putExtra(TestIntentServiceActivity.Constans.A, a);
            sendBroadcast(intent);
        } catch (InterruptedException e) {
            e.printStackTrace();
            ;
        }
    }

    public static void startMyIntentService(TestIntentServiceActivity context, int a, int b) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(TestIntentServiceActivity.Constans.ACTION_FOR_SUM);
        intent.putExtra(TestIntentServiceActivity.Constans.A, a);
        intent.putExtra(TestIntentServiceActivity.Constans.B, b);
        context.startService(intent);
    }

}

