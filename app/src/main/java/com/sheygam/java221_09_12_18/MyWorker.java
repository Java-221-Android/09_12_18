package com.sheygam.java221_09_12_18;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Data;
import androidx.work.Result;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MyWorker extends Worker {

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String name = getInputData().getString("NAME");
        Log.d("MY_TAG", "doWork: " + name);
        Log.d("MY_TAG", "doWork: start");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();

        }
        if(isStopped()){
            return Result.retry();
        }
        Log.d("MY_TAG", "doWork: end");
        Data data = new Data.Builder()
                .putString("DATA","My Data")
                .build();
        return Result.success(data);
    }
}
