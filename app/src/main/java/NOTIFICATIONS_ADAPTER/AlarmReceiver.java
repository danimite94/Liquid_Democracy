package NOTIFICATIONS_ADAPTER;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String CUSTOM_INTENT = "com.test.intent.action.ALARM";

    NotificationScheduler notificationScheduler;
    @Override
    public void onReceive(Context context, Intent intent) {
        /* enqueue the job */
        MyJobIntentService.enqueueWork(context, intent);

        Log.d("checker alarm","just fired");
    }

    public void cancelAlarm(Context context) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent pending = PendingIntent.getBroadcast(context, 0, new Intent(context, AlarmReceiver.class), 0);

        /* cancel any pending alarm */
        alarm.cancel(getPendingIntent(context));
    }

    public void setAlarm(Context context) {
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        /* fire the broadcast */
        alarm.set(AlarmManager.RTC_WAKEUP, 0, getPendingIntent(context));
    }


    private static PendingIntent getPendingIntent(Context context) {
        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        alarmIntent.setAction(CUSTOM_INTENT);

        return PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_CANCEL_CURRENT);
    }
}