package com.aware.plugin.upmc.mj.activities;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.aware.plugin.upmc.mj.Plugin;
import com.aware.plugin.upmc.mj.R;

import static com.aware.plugin.upmc.mj.Plugin.ACTION_MJ_NOTIFICATION_EXPIRED;
import static com.aware.plugin.upmc.mj.Plugin.UPMC_CHANNEL_ID;
import static com.aware.plugin.upmc.mj.Plugin.UPMC_NOTIFICATIONS;

/**
 * Created by Grace on 14/12/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("MJAPP", "Alarm Received");

        if(intent.hasExtra(Constants.ALARM_COMM)) {
            Log.d(Constants.TAG, "Alarm Received: " + intent.getIntExtra(Constants.ALARM_COMM, -1));
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(Constants.ALARM_LOCAL_RECEIVER_INTENT_FILTER));
        }


        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Wake Up");
        wl.acquire(6000);
        final Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(3000);


        Intent mj_survey = new Intent(context, MJ_Survey.class);
        mj_survey.setAction(Plugin.ACTION_USER_INIT_END);
        mj_survey.putExtra(Constants.EMA_SENT_KEY, System.currentTimeMillis());
        PendingIntent onClick = PendingIntent.getActivity(context, 0, mj_survey, PendingIntent.FLAG_UPDATE_CURRENT);
        showNotification(context, "UPMC MJ - End of Use", "Finish your survey", onClick, 5);

    }



    private void showNotification(Context context, String title, String description, PendingIntent onClick, int expires_hours) {
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);
        mBuilder.setSmallIcon(R.drawable.ic_action_esm)
                .setContentTitle(title)
                .setContentText(description)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true)
                .setContentIntent(onClick);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            mBuilder.setChannelId(UPMC_CHANNEL_ID);

        mNotificationManager.notify(UPMC_NOTIFICATIONS, mBuilder.build());

        if (expires_hours > 0) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent expiredBroadcast = new Intent(context, Plugin.MJNotificationObserver.class);
            expiredBroadcast.setAction(ACTION_MJ_NOTIFICATION_EXPIRED);
            expiredBroadcast.putExtra("type", "user_initiated");
            PendingIntent alarmPending = PendingIntent.getBroadcast(context, 0, expiredBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (expires_hours * 60 * 60 * 1000), alarmPending); //expire the notification after x hours.
        }
    }

}
