package com.aware.plugin.upmc.mj;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.upmc.mj.activities.Constants;
import com.aware.plugin.upmc.mj.activities.MJ_Survey;
import com.aware.utils.Aware_Plugin;
import com.aware.utils.Scheduler;

import org.json.JSONException;

import java.util.Objects;

public class Plugin extends Aware_Plugin {

    public static final String SCHEDULE_MORNING_MJ = "schedule_mj_morning";
    public static final String SCHEDULE_AFTENOON_MJ = "schedule_mj_afternoon";
    public static final String SCHEDULE_EVENING_MJ = "schedule_mj_evening";
    public static final String ACTION_MJ_MORNING = "ACTION_MJ_MORNING";
    public static final String ACTION_MJ_AFTERNOON = "ACTION_MJ_AFTEROON";
    public static final String ACTION_MJ_EVENING = "ACTION_MJ_EVENING";
    public static final String ACTION_MJ_SELF_START = "ACTION_MJ_SELF_START";
    public static final String ACTION_MJ_SELF_END = "ACTION_MJ_SELF_END";
    public static final String ACTION_2HR_ALARM = "ACTION_2HR_ALARM";
    public static final String UPMC_CHANNEL_ID = "UPMC_CHANNEL_ID";
    public static final String UPMC_CHANNEL_NAME = "UPMC";
    public static final int UPMC_NOTIFICATIONS = 424242;
    public static final String ACTION_USER_INIT_END = "action_user_init_end";

    public static final String ACTION_MJ_SELF_START_COMPLETED = "ACTION_MJ_SELF_START_COMPLETED";
    public static final String ACTION_MJ_SELF_END_COMPLETED = "ACTION_MJ_SELF_END_COMPLETED";

    public Notification.Builder srNotifBuilder;
    public NotificationCompat.Builder srNotifCompatBuilder;

    /**
     * Shown in the lock screen for self-reports
     */
    public static final int UPMC_PERSISTENT_NOTIFICATION = 123456789;

    public static final String ACTION_MJ_NOTIFICATION_EXPIRED = "ACTION_MJ_NOTIFICATION_EXPIRED";

    @Override
    public void onCreate() {
        super.onCreate();

        TAG = "AWARE::" + getResources().getString(R.string.app_name);

        //Set sync adapter authority
        AUTHORITY = Provider.getAuthority(this);

        /*
         * Enable this plugin's notification channel for Android O devices
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String channel_descriptiopn = "UPMC notifications channel";
            int channel_importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(UPMC_CHANNEL_ID, UPMC_CHANNEL_NAME, channel_importance);
            mChannel.setDescription(channel_descriptiopn);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(mChannel);
        }

        showSelfReportNotif();

        //Super class Aware_Plugin will handle check for permissions and set PERMISSIONS_OK to true inside onStartCommand. If we don't it keeps asking the permissions.
    }


    public void showSelfReportNotif() {

        /*
         * Create persistent notification to do self-reports
         */
        Intent selfReport = new Intent(this, MJ_Survey.class).setAction(Plugin.ACTION_MJ_SELF_START);
        PendingIntent onTapSelf = PendingIntent.getActivity(this, 0, selfReport, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            srNotifBuilder = new Notification.Builder(getApplicationContext(), UPMC_CHANNEL_ID);
            srNotifBuilder.setSmallIcon(R.drawable.ic_action_esm)
                    .setContentTitle("UPMC MJ - Self-Report")
                    .setContentText("Self-report MJ")
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .setContentIntent(onTapSelf);
            assert notificationManager != null;
            notificationManager.notify(UPMC_PERSISTENT_NOTIFICATION, srNotifBuilder.build());

        }
        else {

            srNotifCompatBuilder = new NotificationCompat.Builder(getApplicationContext(), UPMC_CHANNEL_ID);
            srNotifCompatBuilder.setSmallIcon(R.drawable.ic_action_esm)
                    .setContentTitle("UPMC MJ - Self-Report")
                    .setContentText("Self-report MJ")
                    .setOnlyAlertOnce(true)
                    .setOngoing(true)
                    .setContentIntent(onTapSelf);
            assert notificationManager != null;
            notificationManager.notify(UPMC_PERSISTENT_NOTIFICATION, srNotifCompatBuilder.build());
        }


    }


    public void showStartNotif() {
        Intent selfReport = new Intent(this, MJ_Survey.class).setAction(Plugin.ACTION_MJ_SELF_START);
        PendingIntent onTapSelf = PendingIntent.getActivity(this, 0, selfReport, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            srNotifBuilder.setContentIntent(onTapSelf);
            assert notificationManager != null;
            notificationManager.notify(UPMC_PERSISTENT_NOTIFICATION, srNotifBuilder.build());
        }
        else {
            srNotifCompatBuilder.setContentIntent(onTapSelf);
            assert notificationManager != null;
            notificationManager.notify(UPMC_PERSISTENT_NOTIFICATION, srNotifCompatBuilder.build());
        }

    }


    public void showEndNotif() {
        Intent selfReport = new Intent(this, MJ_Survey.class).setAction(Plugin.ACTION_MJ_SELF_END);
        PendingIntent onTapSelf = PendingIntent.getActivity(this, 0, selfReport, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            srNotifBuilder.setContentIntent(onTapSelf);
            assert notificationManager != null;
            notificationManager.notify(UPMC_PERSISTENT_NOTIFICATION, srNotifBuilder.build());
        }
        else {
            srNotifCompatBuilder.setContentIntent(onTapSelf);
            assert notificationManager != null;
            notificationManager.notify(UPMC_PERSISTENT_NOTIFICATION, srNotifCompatBuilder.build());
        }
    }




    //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if(intent!=null && intent.getAction()!=null)
            Log.d(Constants.TAG, "Plugin:onStartCommand " + intent.getAction());

        if (PERMISSIONS_OK) {

            DEBUG = Aware.getSetting(this, Aware_Preferences.DEBUG_FLAG).equals("true");

            //Initialize our plugin's settings
            Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, true);

            //Enable our plugin's sync-adapter to upload the data to the server if part of a study
            if (Aware.getSetting(this, Aware_Preferences.FREQUENCY_WEBSERVICE).length() >= 0 && !Aware.isSyncEnabled(this, Provider.getAuthority(this)) && Aware.isStudy(this) && getApplicationContext().getPackageName().equalsIgnoreCase("com.aware.phone") || getApplicationContext().getResources().getBoolean(R.bool.standalone)) {
                ContentResolver.setIsSyncable(Aware.getAWAREAccount(this), Provider.getAuthority(this), 1);
                ContentResolver.setSyncAutomatically(Aware.getAWAREAccount(this), Provider.getAuthority(this), true);
                ContentResolver.addPeriodicSync(
                        Aware.getAWAREAccount(this),
                        Provider.getAuthority(this),
                        Bundle.EMPTY,
                        Long.parseLong(Aware.getSetting(this, Aware_Preferences.FREQUENCY_WEBSERVICE)) * 60
                );
            }

            if (Scheduler.getSchedule(this, Plugin.SCHEDULE_MORNING_MJ) == null) {
                try {
                    Scheduler.Schedule morning = new Scheduler.Schedule(Plugin.SCHEDULE_MORNING_MJ)
                            .addHour(10)
                            .addMinute(0)
                            .setActionType(Scheduler.ACTION_TYPE_SERVICE)
                            .setActionIntentAction(Plugin.ACTION_MJ_MORNING)
                            .setActionClass(getPackageName() + "/" + Plugin.class.getName());

                    Scheduler.saveSchedule(getApplicationContext(), morning);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (Scheduler.getSchedule(this, Plugin.SCHEDULE_AFTENOON_MJ) == null) {
                try {
                    Scheduler.Schedule afternoon = new Scheduler.Schedule(Plugin.SCHEDULE_AFTENOON_MJ)
                            .addHour(15)
                            .addMinute(0)
                            .setActionType(Scheduler.ACTION_TYPE_SERVICE)
                            .setActionIntentAction(Plugin.ACTION_MJ_AFTERNOON)
                            .setActionClass(getPackageName() + "/" + Plugin.class.getName());

                    Scheduler.saveSchedule(getApplicationContext(), afternoon);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (Scheduler.getSchedule(this, Plugin.SCHEDULE_EVENING_MJ) == null) {
                try {
                    Scheduler.Schedule evening = new Scheduler.Schedule(Plugin.SCHEDULE_EVENING_MJ)
                            .addHour(20)
                            .addMinute(0)
                            .setActionType(Scheduler.ACTION_TYPE_SERVICE)
                            .setActionIntentAction(Plugin.ACTION_MJ_EVENING)
                            .setActionClass(getPackageName() + "/" + Plugin.class.getName());

                    Scheduler.saveSchedule(getApplicationContext(), evening);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /*
             * Survey in the morning. Called from the Scheduler
             */
            if (intent != null && intent.getAction() != null && intent.getAction().equalsIgnoreCase(ACTION_MJ_MORNING)) {
                Intent mj_survey = new Intent(this, MJ_Survey.class);
                mj_survey.setAction(Plugin.ACTION_MJ_MORNING);
                PendingIntent onClick = PendingIntent.getActivity(getApplicationContext(), 0, mj_survey, PendingIntent.FLAG_UPDATE_CURRENT);
                showNotification("UPMC MJ - Morning", "Good morning! We're checking in to see how you're doing.", onClick, 5, Constants.Morning.TYPE);
            }

            /*
             * Survey in the afternoon. Called from the Scheduler
             */
            if (intent != null && intent.getAction() != null && intent.getAction().equalsIgnoreCase(ACTION_MJ_AFTERNOON)) {
                Intent mj_survey = new Intent(this, MJ_Survey.class);
                mj_survey.setAction(Plugin.ACTION_MJ_AFTERNOON);
                PendingIntent onClick = PendingIntent.getActivity(getApplicationContext(), 0, mj_survey, PendingIntent.FLAG_UPDATE_CURRENT);
                showNotification("UPMC MJ - Afternoon", "Good afternoon! We're checking in to see how you're doing.", onClick, 5, Constants.Afternoon.TYPE);
            }

            /*
             * Survey in the evening. Called from the Scheduler
             */
            if (intent != null && intent.getAction() != null && intent.getAction().equalsIgnoreCase(ACTION_MJ_EVENING)) {
                Intent mj_survey = new Intent(this, MJ_Survey.class);
                mj_survey.setAction(Plugin.ACTION_MJ_EVENING);
                PendingIntent onClick = PendingIntent.getActivity(getApplicationContext(), 0, mj_survey, PendingIntent.FLAG_UPDATE_CURRENT);
                showNotification("UPMC MJ - Evening", "Evening check-in to see how you're doing.", onClick, 6, Constants.Evening.TYPE);
            }

            if(intent!=null && intent.getAction()!=null && intent.getAction().equalsIgnoreCase(ACTION_2HR_ALARM)) {
                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
                assert pm != null;
                PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Wake Up");
                wl.acquire(6000);
                final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                assert vibrator != null;
                vibrator.vibrate(3000);
                showEndNotif();
            }

            if(intent!=null && intent.getAction()!=null && intent.getAction().equalsIgnoreCase(ACTION_MJ_SELF_START_COMPLETED)) {
                showEndNotif();
            }

            if(intent!=null && intent.getAction()!=null && intent.getAction().equalsIgnoreCase(ACTION_MJ_SELF_END_COMPLETED)) {
                showStartNotif();
            }


            //Initialise AWARE instance in plugin
            Aware.startAWARE(this);
            Log.d(Constants.TAG, "Plugin:onStartCommand ends");

        }
        return START_STICKY;
    }

    /**
     * Shows a notification to the user from our UPMC MJ plugin
     *
     */
    private void showNotification(String title, String description, PendingIntent onClick, int expires_hours, String type) {

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder mBuilder = new Notification.Builder(getApplicationContext(), UPMC_CHANNEL_ID);
            mBuilder.setSmallIcon(R.drawable.ic_action_esm)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setContentIntent(onClick);
            assert mNotificationManager != null;
            mNotificationManager.notify(UPMC_NOTIFICATIONS, mBuilder.build());
        }
        else {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), UPMC_CHANNEL_ID);
            mBuilder.setSmallIcon(R.drawable.ic_action_esm)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setOnlyAlertOnce(true)
                    .setAutoCancel(true)
                    .setContentIntent(onClick);
            assert mNotificationManager != null;
            mNotificationManager.notify(UPMC_NOTIFICATIONS, mBuilder.build());
        }


        if (expires_hours > 0) {
            AlarmManager alarmManager = (AlarmManager) getApplicationContext().getSystemService(ALARM_SERVICE);
            Intent expiredBroadcast = new Intent(this, MJNotificationObserver.class);
            expiredBroadcast.setAction(ACTION_MJ_NOTIFICATION_EXPIRED);
            expiredBroadcast.putExtra("type",type);
            PendingIntent alarmPending = PendingIntent.getBroadcast(this, 0, expiredBroadcast, PendingIntent.FLAG_UPDATE_CURRENT);
            assert alarmManager != null;
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (expires_hours * 60 * 60 * 1000), alarmPending); //expire the notification after x hours.
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        //Turn off the sync-adapter if part of a study
        if (Aware.isStudy(this) && (getApplicationContext().getPackageName().equalsIgnoreCase("com.aware.phone") || getApplicationContext().getResources().getBoolean(R.bool.standalone))) {
            ContentResolver.setSyncAutomatically(Aware.getAWAREAccount(this), Provider.getAuthority(this), false);
            ContentResolver.removePeriodicSync(
                    Aware.getAWAREAccount(this),
                    Provider.getAuthority(this),
                    Bundle.EMPTY
            );
        }

        Aware.setSetting(this, Settings.STATUS_PLUGIN_TEMPLATE, false);

        //Stop AWARE instance in plugin
        Aware.stopAWARE(this);
    }

    /**
     * Broadcast receiver that will remove the UPMC survey notification once it expired
     */
    static public class MJNotificationObserver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Objects.requireNonNull(intent.getAction()).equalsIgnoreCase(ACTION_MJ_NOTIFICATION_EXPIRED)) {
                NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                assert notificationManager != null;
                notificationManager.cancel(UPMC_NOTIFICATIONS);

                if (intent.hasExtra("type")) {
                    ContentValues data = new ContentValues();
                    data.put(Provider.UPMC_MJ_Data.TIMESTAMP, System.currentTimeMillis());
                    data.put(Provider.UPMC_MJ_Data.DEVICE_ID, Aware.getSetting(context, Aware_Preferences.DEVICE_ID));
                    data.put(Provider.UPMC_MJ_Data.QUESTION_TYPE, intent.getStringExtra("type") + "-expired");
                    data.put(Provider.UPMC_MJ_Data.QUESTION_ANSWERS, "NA");
                    context.getContentResolver().insert(Provider.UPMC_MJ_Data.CONTENT_URI, data);
                }
            }
        }
    }
}
