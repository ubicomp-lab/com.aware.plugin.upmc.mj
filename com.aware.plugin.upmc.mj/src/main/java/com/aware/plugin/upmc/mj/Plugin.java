package com.aware.plugin.upmc.mj;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.plugin.upmc.mj.activities.MJ_Survey;
import com.aware.utils.Aware_Plugin;

public class Plugin extends Aware_Plugin {

    public static final String SCHEDULE_MORNING_MJ = "schedule_mj_morning";
    public static final String SCHEDULE_EVENING_MJ = "schedule_mj_evening";
    public static final String SCHEDULE_FINGERPRING_MJ = "schedule_mj_fingerprint";

    public static final String ACTION_MJ_MORNING = "ACTION_MJ_MORNING";
    public static final String ACTION_MJ_EVENING = "ACTION_MJ_EVENING";
    public static final String ACTION_MJ_FINGERPRINT = "ACTION_MJ_FINGERPRINT";
    public static final String UPMC_CHANNEL_ID = "UPMC_CHANNEL_ID";
    public static final int UPMC_NOTIFICATIONS = 424242;

    @Override
    public void onCreate() {
        super.onCreate();

        TAG = "AWARE::" + getResources().getString(R.string.app_name);

        //Set sync adapter authority
        AUTHORITY = Provider.getAuthority(this);

        /**
         * Enable this plugin's notification channel for Android O devices
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            CharSequence channel_name = "UPMC";
            String channel_descriptiopn = "UPMC notifications channel";
            int channel_importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(UPMC_CHANNEL_ID, channel_name, channel_importance);
            mChannel.setDescription(channel_descriptiopn);
            mChannel.enableLights(true);
            mChannel.enableVibration(true);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    //This function gets called every 5 minutes by AWARE to make sure this plugin is still running.
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

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

            //Initialise AWARE instance in plugin
            Aware.startAWARE(this);
        }

        /**
         * Survey in the morning. Called from the Scheduler
         */
        if (intent != null && intent.getAction() != null && intent.getAction().equalsIgnoreCase(ACTION_MJ_MORNING)) {
            Intent mj_survey = new Intent(this, MJ_Survey.class);
            mj_survey.setAction(Plugin.ACTION_MJ_MORNING);
            PendingIntent onClick = PendingIntent.getActivity(getApplicationContext(), 0, mj_survey, PendingIntent.FLAG_UPDATE_CURRENT);
            showNotification("UPMC MJ - Morning", "Good morning! Tap to answer survey.", onClick);
        }

        /**
         * Survey in the evening. Called from the Scheduler
         */
        if (intent != null && intent.getAction() != null && intent.getAction().equalsIgnoreCase(ACTION_MJ_EVENING)) {
            Intent mj_survey = new Intent(this, MJ_Survey.class);
            mj_survey.setAction(Plugin.ACTION_MJ_EVENING);
            PendingIntent onClick = PendingIntent.getActivity(getApplicationContext(), 0, mj_survey, PendingIntent.FLAG_UPDATE_CURRENT);
            showNotification("UPMC MJ - Evening", "Good evening! Tap to answer survey.", onClick);
        }

        /**
         * From lock-screen, on demand. Called from the Scheduler
         */
        if (intent != null && intent.getAction() != null && intent.getAction().equalsIgnoreCase(ACTION_MJ_FINGERPRINT)) {
            Intent mj_survey = new Intent(this, MJ_Survey.class);
            mj_survey.setAction(Plugin.ACTION_MJ_FINGERPRINT);
            PendingIntent onClick = PendingIntent.getActivity(getApplicationContext(), 0, mj_survey, PendingIntent.FLAG_UPDATE_CURRENT);
            showNotification("UPMC MJ - Check up", "Just checking how you are doing. Tap to answer survey.", onClick);
        }

        return START_STICKY;
    }

    /**
     * Shows a notification to the user from our UPMC MJ plugin
     *
     * @param title
     * @param description
     * @param onClick
     */
    private void showNotification(String title, String description, PendingIntent onClick) {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());
        mBuilder.setSmallIcon(R.drawable.ic_action_esm)
                .setContentTitle(title)
                .setContentText(description)
                .setContentIntent(onClick);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            mBuilder.setChannelId(UPMC_CHANNEL_ID);

        mNotificationManager.notify(UPMC_NOTIFICATIONS, mBuilder.build());
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
}
