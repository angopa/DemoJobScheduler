package com.andgopa.demojobscheduler;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class MyJobService extends JobService {

    private static final String TAG = MyJobService.class.getSimpleName();
    boolean isWorking = false;
    boolean jobCancelled = false;

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job Started");
        isWorking = true;
        startWorkOnNetworkThread(params);
        return isWorking;
    }

    private void startWorkOnNetworkThread(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                doWork(params);
            }
        }).start();
    }

    private void doWork(JobParameters params) {
        for (int i =0; i<1000; i++) {
            if (jobCancelled) {
                return;
            }
        }
        try {
            Thread.sleep(TimeUnit.SECONDS.toMillis(10));
        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "Job Finished!");
        isWorking = false;
        boolean needsReschedule = false;
        jobFinished(params, needsReschedule);
    }

    //Called if the job was cancelled before being finished
    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job Cancelled before being complete!");
        jobCancelled = true;
        boolean needReschedule = isWorking;
        jobFinished(params, needReschedule);
        return needReschedule;
    }
}
