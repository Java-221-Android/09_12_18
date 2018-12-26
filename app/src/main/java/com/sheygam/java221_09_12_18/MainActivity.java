package com.sheygam.java221_09_12_18;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button notifyFirstBtn, notifySecondBtn;
    private NotificationManager manager;
    OneTimeWorkRequest workRequest = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("MyChannel","My Super Channel",NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("My super notification channel!");
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);

        }
        notifyFirstBtn = findViewById(R.id.notifyFirstBtn);
        notifySecondBtn = findViewById(R.id.notifySecondBtn);
        notifyFirstBtn.setOnClickListener(this);
        notifySecondBtn.setOnClickListener(this);
        findViewById(R.id.startBtn)
                .setOnClickListener(this);
        findViewById(R.id.stopBtn)
                .setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.notifyFirstBtn:
                NotificationCompat.Builder builder =
                        new NotificationCompat.Builder(this,"MyChannel")
                        .setSmallIcon(R.drawable.ic_notification)
                        .setContentTitle("My title")
                        .setContentText("My text")
                        .setContentInfo("My content info")
                        .setNumber(23);
//                        .setProgress(100,25,false);
                Notification notification = builder.build();
                manager.notify(1,notification);
                break;
            case R.id.notifySecondBtn:
                Intent intent = new Intent(this, MainActivity.class);
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                Notification n2 = new NotificationCompat.Builder(this, "MyChannel")
                        .setContentText("Second text")
                        .setLargeIcon(bitmap)
                        .setContentTitle("Second title")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true)
                        .addAction(R.drawable.ic_notification,"Cancel",null)
                        .setSmallIcon(R.drawable.ic_notification)
                        .build();
                manager.notify(2,n2);
                break;
            case R.id.startBtn:
                Data data = new Data.Builder()
                        .putString("NAME","Vasya")
                        .build();
//                PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(MyWorker.class,15,TimeUnit.MINUTES).build();
                workRequest =
                        new OneTimeWorkRequest.Builder(MyWorker.class)
//                                .setInitialDelay(30,TimeUnit.SECONDS)
                                .setInputData(data)
                                .build();
                WorkManager.getInstance().enqueue(workRequest);
                WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.getId())
                        .observe(this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(@Nullable WorkInfo workInfo) {
                                Log.d("MY_TAG", "onChanged: " + workInfo.getState());
                                Log.d("MY_TAG", "onChanged: " + workInfo.getOutputData().getString("DATA"));
                            }
                        });
                break;
            case R.id.stopBtn:
                WorkManager.getInstance().cancelWorkById(workRequest.getId());
                break;
        }
    }
}
