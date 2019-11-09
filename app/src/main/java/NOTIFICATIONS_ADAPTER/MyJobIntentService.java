package NOTIFICATIONS_ADAPTER;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;
import android.util.Log;

public class MyJobIntentService extends JobIntentService {

    public static final String TAG = "VotationService";
    public static final int JOB_channel21ID = 1000;
    public static final int JOB_channel22ID = 1000;
    public static final int JOB_channel23ID = 1000;
    public static final int JOB_channel24ID = 1000;
    public static final int JOB_channel25ID = 1000;


    public static void enqueueWork(Context context, Intent work){
        enqueueWork(context,MyJobIntentService .class,JOB_channel21ID,work);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate:");

    }

    @Override
    public void onDestroy() {
        // STOP YOUR TASKS
        super.onDestroy();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.d(TAG,"onHandleWork:");

        NotificationScheduler notificationScheduler = new NotificationScheduler();

        //1 line code to trigger notification
//        notificationScheduler.create_notification(intent, MainActivity.class);
        //"You have 5 unwatched videos", "Watch them now?");

    }



}

