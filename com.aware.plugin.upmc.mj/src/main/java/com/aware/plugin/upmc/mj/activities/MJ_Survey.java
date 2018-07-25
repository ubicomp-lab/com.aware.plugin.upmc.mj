package com.aware.plugin.upmc.mj.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aware.Applications;
import com.aware.Aware;
import com.aware.Aware_Preferences;
import com.aware.ESM;
import com.aware.plugin.upmc.mj.Plugin;
import com.aware.plugin.upmc.mj.Provider;
import com.aware.plugin.upmc.mj.R;
import com.aware.plugin.upmc.mj.Settings;
import com.aware.ui.PermissionsHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by denzilferreira on 05/10/2017.
 */

public class MJ_Survey extends AppCompatActivity {

    private JSONObject morning;
    private JSONObject evening;
    private JSONObject afternoon;
    private JSONObject self;
    private boolean isRegistered = false;
    private boolean permissions_ok = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(Constants.TAG, "onCreate()");
    }

    /*private class AsyncJoin extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {


            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_SIGNIFICANT_MOTION, true); //to make accelerometer logging less verbose.
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_BATTERY, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ESM, true); //in case we want to push ESMs from dashboard side
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_APPLICATIONS, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_NOTIFICATIONS, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LIGHT, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.THRESHOLD_LIGHT, 5);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_COMMUNICATION_EVENTS, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_CALLS, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_MESSAGES, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_SCREEN, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_NETWORK_EVENTS, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_WIFI_ONLY, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_FALLBACK_NETWORK, 6);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_WEBSERVICE, 30);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_CLEAN_OLD_DATA, 1);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_SILENT, true);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.activity_recognition.Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, true);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.activity_recognition.Settings.FREQUENCY_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, 300);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.device_usage.Settings.STATUS_PLUGIN_DEVICE_USAGE, true);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.STATUS_GOOGLE_FUSED_LOCATION, true);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.FREQUENCY_GOOGLE_FUSED_LOCATION, 300);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION, 300);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.ACCURACY_GOOGLE_FUSED_LOCATION, 102);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.FALLBACK_LOCATION_TIMEOUT, 20);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.LOCATION_SENSITIVITY, 5);

            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.activity_recognition");
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.device_usage");
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.fused_location");
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.studentlife.audio_final");
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.fitbit");

            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.upmc.mj");

            //Check accessibility service
            Applications.isAccessibilityServiceActive(getApplicationContext());
            //Check doze whitelisting
            Aware.isBatteryOptimizationIgnored(getApplicationContext(), getPackageName());
            Log.d(Constants.TAG, "Joined study");




            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d(Constants.TAG, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_LABEL));
            Log.d(Constants.TAG, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
            if(!Aware.isStudy(getApplicationContext()))
                Toast.makeText(getApplicationContext(), "Join failed, please try again!", Toast.LENGTH_LONG).show();

            else {
                Toast.makeText(getApplicationContext(), "Joined OK!", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_mj, menu);
        for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getTitle().toString().equalsIgnoreCase("Sync") && !Aware.isStudy(getApplicationContext())) {
                item.setVisible(false);
            }
        }
        return true;
    }

    @Override

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onResume();
            return true;
        }

        String title = item.getTitle().toString();
        if (title.equalsIgnoreCase("Participant")) {
            View participantInfo = getLayoutInflater().inflate(R.layout.participant_info, null);
            TextView uuid = participantInfo.findViewById(R.id.device_id);
            uuid.setText("UUID: " + Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setTitle("AWARE Device ID");
            mBuilder.setView(participantInfo);
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mBuilder.create().show();
            return true;
        }

        if (title.equalsIgnoreCase("Sync")) {
            sendBroadcast(new Intent(Aware.ACTION_AWARE_SYNC_DATA));
            return true;
        }

        return super.onOptionsItemSelected(item);

    }


    private JoinedStudy joinedObserver = new JoinedStudy();
    private class JoinedStudy extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            unregisterReceiver(joinedObserver);
            isRegistered = false;

            Aware.setSetting(getApplicationContext(), Aware_Preferences.DEBUG_FLAG, false); //enable logcat debug messages
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_SIGNIFICANT_MOTION, true); //to make accelerometer logging less verbose.
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_BATTERY, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ESM, true); //in case we want to push ESMs from dashboard side
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_APPLICATIONS, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_NOTIFICATIONS, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LIGHT, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.THRESHOLD_LIGHT, 5);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_COMMUNICATION_EVENTS, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_CALLS, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_MESSAGES, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_SCREEN, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_NETWORK_EVENTS, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_WIFI_ONLY, true);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_FALLBACK_NETWORK, 6);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_WEBSERVICE, 30);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_CLEAN_OLD_DATA, 1);
            Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_SILENT, true);

            Aware.setSetting(getApplicationContext(), Aware_Preferences.FOREGROUND_PRIORITY, true); //makes the app run with foreground priority

            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.activity_recognition.Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, true);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.activity_recognition.Settings.FREQUENCY_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, 300);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.device_usage.Settings.STATUS_PLUGIN_DEVICE_USAGE, true);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.STATUS_GOOGLE_FUSED_LOCATION, true);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.FREQUENCY_GOOGLE_FUSED_LOCATION, 300);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION, 300);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.ACCURACY_GOOGLE_FUSED_LOCATION, 102);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.FALLBACK_LOCATION_TIMEOUT, 20);
            Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.LOCATION_SENSITIVITY, 5);

            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.activity_recognition");
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.device_usage");
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.fused_location");
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.studentlife.audio_final");
            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.fitbit");

            Aware.startPlugin(getApplicationContext(), "com.aware.plugin.upmc.mj");

            //Check accessibility service
            Applications.isAccessibilityServiceActive(getApplicationContext());
            //Check doze whitelisting
            Aware.isBatteryOptimizationIgnored(getApplicationContext(), getPackageName());
            Log.d(Constants.TAG, "Joined study");

            Log.d(Constants.TAG, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_LABEL));
            Log.d(Constants.TAG, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
            if(!Aware.isStudy(getApplicationContext()))
                Toast.makeText(getApplicationContext(), "Join failed, please try again!", Toast.LENGTH_LONG).show();

            else {
                Toast.makeText(getApplicationContext(), "Joined OK!", Toast.LENGTH_LONG).show();
            }
            finish();


        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(isRegistered) unregisterReceiver(joinedObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.TAG, "onResume");

        ArrayList<String> REQUIRED_PERMISSIONS = new ArrayList<>();
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_FINE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_CALL_LOG);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_CONTACTS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_SMS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_PHONE_STATE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.RECORD_AUDIO);

        for (String p : REQUIRED_PERMISSIONS) {
            if (PermissionChecker.checkSelfPermission(this, p) != PermissionChecker.PERMISSION_GRANTED) {
                permissions_ok = false;
                break;
            }
        }

        if (!permissions_ok) {

            Intent permissions = new Intent(this, PermissionsHandler.class);
            permissions.putExtra(PermissionsHandler.EXTRA_REQUIRED_PERMISSIONS, REQUIRED_PERMISSIONS);
            permissions.putExtra(PermissionsHandler.EXTRA_REDIRECT_ACTIVITY, getPackageName() + "/" + getClass().getName());
            permissions.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(permissions);
            finish();

        } else { //all ok, continue loading AWARE etc.

            if (!Aware.IS_CORE_RUNNING) {
                //This initialises the core framework, assigns Device ID if it doesn't exist yet, etc.
                Intent aware = new Intent(getApplicationContext(), Aware.class);
                startService(aware);
            }

            if (!Aware.isStudy(getApplicationContext())) {

                IntentFilter filter = new IntentFilter(Aware.ACTION_JOINED_STUDY);
                registerReceiver(joinedObserver, filter);

                setContentView(R.layout.activity_mj_survey_main);
                Button submitButton = findViewById(R.id.join_study);
                final ProgressBar progressBar = findViewById(R.id.joining_study);
                progressBar.setVisibility(View.INVISIBLE);
                final EditText editText = findViewById(R.id.participant_label);
                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(editText.getText().length()!=0) {
                            Aware.setSetting(getApplicationContext(), Aware_Preferences.DEVICE_LABEL , editText.getText().toString(),Aware_Preferences.DEVICE_LABEL);
                            progressBar.setVisibility(View.VISIBLE);
                            //new AsyncJoin().execute();
                            Log.d(Constants.TAG, "onResume: trying to join study");
                            isRegistered = true;
                            Aware.joinStudy(getApplicationContext(), "https://r2d2.hcii.cs.cmu.edu/aware/dashboard/index.php/webservice/index/108/z4Q4nINGkqq8");
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Please enter a valid label", Toast.LENGTH_LONG).show();
                            Log.d(Constants.TAG, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
                            Log.d(Constants.TAG, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_LABEL));
                        }

                    }
                });


            } else {

                Log.d(Constants.TAG, "Settings:" + Aware.getSetting(getApplicationContext(), Settings.ACTION_MJ_SELF));

                Applications.isAccessibilityServiceActive(getApplicationContext());
                Aware.isBatteryOptimizationIgnored(getApplicationContext(), "com.aware.plugin.upmc.mj");

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }



                if(Aware.getSetting(getApplicationContext(), Settings.ACTION_MJ_SELF).length()==0)
                    Aware.setSetting(getApplicationContext(), Settings.ACTION_MJ_SELF, Plugin.ACTION_MJ_SELF_START);


                if(getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_MJ_MORNING)) {
                    Log.d(Constants.TAG, "morning survey");
                    showMorningSurvey();
                }
                else if(getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_MJ_AFTERNOON)) {
                    Log.d(Constants.TAG, "afternoon survey");
                    showAfternoonSurvey();
                }
                else if(getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_MJ_EVENING)) {
                    Log.d(Constants.TAG, "evening survey");
                    showEveningSurvey();
                }
                else if(getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_MJ_SELF_START)) {
                    Log.d(Constants.TAG, "self-report start survey");
                    showSelfReportStartSurvey();
                }
                else if(getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_MJ_SELF_END)) {
                    Log.d(Constants.TAG, "self-report end survey");
                    showSelfReportEndSurvey();
                }
                else {
                    Log.d(Constants.TAG, "self-report intent");
                    if (Aware.getSetting(getApplicationContext(), Settings.ACTION_MJ_SELF).equals(Plugin.ACTION_MJ_SELF_START)) {
                        Log.d(Constants.TAG, "self-report start survey");
                        showSelfReportStartSurvey();
                    }
                    else if (Aware.getSetting(getApplicationContext(), Settings.ACTION_MJ_SELF).equals(Plugin.ACTION_MJ_SELF_END)) {
                        Log.d(Constants.TAG, "self-report end survey");
                        showSelfReportEndSurvey();
                    }

                }
            }
        }
    }


    private void showSelfReportStartSurvey() {
        self = new JSONObject();
        setContentView(R.layout.mjs_user_start);
        Button submit = findViewById(R.id.mjs_user_start_submit);
        Button cancel = findViewById(R.id.mjs_user_start_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSelfReport(true, true);
                finish();
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker start_date = findViewById(R.id.mjs_user_start_start_date);
                TimePicker start_time = findViewById(R.id.mjs_user_start_start_time);
                SeekBar rate_craving = findViewById(R.id.mjs_user_start_rate_craving);
                SeekBar rate_high = findViewById(R.id.mjs_user_start_rate_how_high);
                EditText other = findViewById(R.id.mjs_user_start_other);
                CheckBox used_tobacco = findViewById(R.id.mjs_user_start_tobacco);
                CheckBox used_alcohol = findViewById(R.id.mjs_user_start_alcohol);
                CheckBox used_caffeine = findViewById(R.id.mjs_user_start_caffeine);
                CheckBox used_none = findViewById(R.id.mjs_user_start_none);
                RadioGroup social_context = findViewById(R.id.mjs_user_start_social_context);
                if(social_context.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!used_alcohol.isChecked() && !used_tobacco.isChecked() && !used_caffeine.isChecked() && !used_none.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(used_none.isChecked() && other.getText().length()==0) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuilder dateTimeStringBuilder = new StringBuilder();
                dateTimeStringBuilder.append(start_date.getMonth()+1)
                        .append("-")
                        .append(start_date.getDayOfMonth())
                        .append("-")
                        .append(start_date.getYear())
                        .append(" ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    dateTimeStringBuilder.append(start_time.getHour()).append(":").append(start_time.getMinute());
                else
                    dateTimeStringBuilder.append(start_time.getCurrentHour()).append(":").append(start_time.getCurrentMinute());
                try {
                    updateSelfReport(Constants.SelfReport.start_datetime, dateTimeStringBuilder.toString());
                    ArrayList<String> used_with_array = new ArrayList<>();
                    if(used_tobacco.isChecked())
                        used_with_array.add(Constants.SelfReport.tobacco);
                    if(used_alcohol.isChecked())
                        used_with_array.add(Constants.SelfReport.alcohol);
                    if(used_caffeine.isChecked())
                        used_with_array.add(Constants.SelfReport.caffeine);
                    if(used_none.isChecked())
                        used_with_array.add(other.getText().toString());
                    updateSelfReport(Constants.SelfReport.usage_while_mj_start, used_with_array.toString());
                    updateSelfReport(Constants.SelfReport.rate_craving, rate_craving.getProgress());
                    updateSelfReport(Constants.SelfReport.rate_high_start, rate_high.getProgress());
                    String social = "none";
                    switch (social_context.getCheckedRadioButtonId()) {
                        case R.id.mjs_user_start_social_alone:
                            social = Constants.SelfReport.social_alone;
                            break;
                        case R.id.mjs_user_start_social_others:
                            social = Constants.SelfReport.social_others;
                            break;
                    }
                    updateSelfReport(Constants.SelfReport.social_context_start, social);
                    Log.d(Constants.TAG, "self-report start " + self.toString());
                    saveSelfReport(true, false);
                    start2HrAlarm();
                    Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    public void showSelfReportEndSurvey() {
        setContentView(R.layout.mjs_user_end1);
        self = new JSONObject();
        final Button next = findViewById(R.id.mjs_user_end1_next);
        Button cancel = findViewById(R.id.mjs_user_end1_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSelfReport(false,true);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker end_date = findViewById(R.id.mjs_user_end1_end_date);
                TimePicker end_time = findViewById(R.id.mjs_user_end1_end_time);
                CheckBox joint = findViewById(R.id.mjs_user_end1_check_joint);
                CheckBox bowl = findViewById(R.id.mjs_user_end1_check_bowl);
                CheckBox bong = findViewById(R.id.mjs_user_end1_check_bong);
                CheckBox blunt = findViewById(R.id.mjs_user_end1_check_blunt);
                CheckBox pen = findViewById(R.id.mjs_user_end1_check_pen);
                CheckBox check_other = findViewById(R.id.mjs_user_end1_check_other);
                EditText other = findViewById(R.id.mjs_user_end1_check_other_input);
                EditText qnty = findViewById(R.id.mjs_user_end1_how_much_mj);
                RadioGroup units = findViewById(R.id.mjs_user_end1_smoke_units);
                CheckBox used_tobacco = findViewById(R.id.mjs_user_end1_tobacco);
                CheckBox used_alcohol = findViewById(R.id.mjs_user_end1_alcohol);
                CheckBox used_caffeine = findViewById(R.id.mjs_user_end1_caffeine);
                CheckBox used_none = findViewById(R.id.mjs_user_end1_none);
                if(units.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(getApplicationContext(),"Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!joint.isChecked() && !bowl.isChecked() && !bong.isChecked() && !blunt.isChecked()&& !pen.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(check_other.isChecked() && qnty.getText().length()==0) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!used_alcohol.isChecked() && !used_tobacco.isChecked() && !used_caffeine.isChecked() && !used_none.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(used_none.isChecked() && other.getText().length()==0) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }


                try {
                    StringBuilder dateTimeStringBuilder = new StringBuilder();
                    dateTimeStringBuilder.append(end_date.getMonth()+1)
                            .append("-")
                            .append(end_date.getDayOfMonth())
                            .append("-")
                            .append(end_date.getYear())
                            .append(" ");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        dateTimeStringBuilder.append(end_time.getHour()).append(":").append(end_time.getMinute());
                    else
                        dateTimeStringBuilder.append(end_time.getCurrentHour()).append(":").append(end_time.getCurrentMinute());
                    updateSelfReport(Constants.SelfReport.end_datetime, dateTimeStringBuilder.toString());
                    ArrayList<String> used_array = new ArrayList<>();
                    if(joint.isChecked())
                        used_array.add(Constants.joint);
                    if(bowl.isChecked())
                        used_array.add(Constants.bowl);
                    if(bong.isChecked())
                        used_array.add(Constants.bong);
                    if(blunt.isChecked())
                        used_array.add(Constants.blunt);
                    if(pen.isChecked())
                        used_array.add(Constants.pen);
                    if(check_other.isChecked())
                        used_array.add(other.getText().toString());
                    updateSelfReport(Constants.SelfReport.used_checkbox, used_array.toString());
                    updateSelfReport(Constants.SelfReport.used_qnty, qnty.getText());
                    String smoke_units = "none";
                    switch (units.getCheckedRadioButtonId()) {
                        case R.id.mjs_user_end1_hits:
                            smoke_units = Constants.SelfReport.hits;
                            break;
                        case R.id.mjs_user_end1_grams:
                            smoke_units = Constants.SelfReport.grams;
                            break;
                    }
                    updateSelfReport(Constants.SelfReport.units, smoke_units);
                    ArrayList<String> used_with_array = new ArrayList<>();
                    if(used_tobacco.isChecked())
                        used_with_array.add(Constants.SelfReport.tobacco);
                    if(used_alcohol.isChecked())
                        used_with_array.add(Constants.SelfReport.alcohol);
                    if(used_caffeine.isChecked())
                        used_with_array.add(Constants.SelfReport.caffeine);
                    if(used_none.isChecked())
                        used_with_array.add(other.getText().toString());
                    updateSelfReport(Constants.SelfReport.usage_while_mj_end, used_with_array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(Constants.TAG, "SelfReport " + self.toString());
                showEnd2();
            }
        });
    }


    private void showEnd2() {
        setContentView(R.layout.mjs_user_end2);
        Button next = findViewById(R.id.mjs_user_end2_next);
        Button cancel = findViewById(R.id.mjs_user_end2_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSelfReport(false, true);
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox to_cope = findViewById(R.id.mjs_user_end2_cope);
                CheckBox to_social = findViewById(R.id.mjs_user_end2_social);
                CheckBox other = findViewById(R.id.mjs_user_end2_other_reason);
                EditText other_reason = findViewById(R.id.mjs_user_end2_other_reason_input);
                CheckBox home = findViewById(R.id.mjs_user_end2_home);
                CheckBox other_home = findViewById(R.id.mjs_user_end2_other_home);
                CheckBox work = findViewById(R.id.mjs_user_end2_school);
                CheckBox other_location =  findViewById(R.id.mjs_user_end2_other_location);
                EditText other_location_input =  findViewById(R.id.mjs_user_end2_other_location_input);
                if(!to_cope.isChecked() && !to_social.isChecked() && !other.isChecked()) {
                    if(other_reason.getText().length()==0) {
                        Toast.makeText(getApplicationContext(), "Please complete the survey!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                if(!home.isChecked() && !other_home.isChecked() && !work.isChecked() &&
                        !other_location.isChecked() && !to_cope.isChecked()) {
                    if(other_location_input.getText().length()==0) {
                        Toast.makeText(getApplicationContext(), "Please complete the survey!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                try {
                    ArrayList<String> reason_array_list = new ArrayList<>();
                    if(to_cope.isChecked())
                        reason_array_list.add(Constants.SelfReport.to_cope);
                    if(to_social.isChecked())
                        reason_array_list.add(Constants.SelfReport.to_social);
                    if(other.isChecked())
                        reason_array_list.add(other_reason.getText().toString());
                    updateSelfReport(Constants.SelfReport.reason, reason_array_list.toString());
                    ArrayList<String> location_array_list = new ArrayList<>();
                    if(home.isChecked())
                        location_array_list.add(Constants.SelfReport.home);
                    if(other_home.isChecked())
                        location_array_list.add(Constants.SelfReport.other_home);
                    if(work.isChecked())
                        location_array_list.add(other_location_input.getText().toString());
                    updateSelfReport(Constants.SelfReport.location, location_array_list.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showEnd3();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSelfReport(false, true);
            }
        });
    }


    public void showEnd3() {
        setContentView(R.layout.mjs_user_end3);
        Button submit = findViewById(R.id.mjs_user_end3_submit);
        Button cancel = findViewById(R.id.mjs_user_end3_cancel);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekBar rate_relaxed = findViewById(R.id.mjs_user_end3_rate_relaxed);
                SeekBar rate_sluggish = findViewById(R.id.mjs_user_end3_rate_sluggish);
                SeekBar rate_foggy = findViewById(R.id.mjs_user_end3_rate_foggy);
                SeekBar rate_anxious = findViewById(R.id.mjs_user_end3_rate_anxious);
                SeekBar rate_sad = findViewById(R.id.mjs_user_end3_rate_sad);
                SeekBar rate_high = findViewById(R.id.mjs_user_end3_rate_how_high);
                try {
                    updateSelfReport(Constants.SelfReport.rate_relaxed, rate_relaxed.getProgress());
                    updateSelfReport(Constants.SelfReport.rate_sluggish, rate_sluggish.getProgress());
                    updateSelfReport(Constants.SelfReport.rate_foggy, rate_foggy.getProgress());
                    updateSelfReport(Constants.SelfReport.rate_anxious, rate_anxious.getProgress());
                    updateSelfReport(Constants.SelfReport.rate_sad, rate_sad.getProgress());
                    updateSelfReport(Constants.SelfReport.rate_high_3, rate_high.getProgress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_SHORT).show();
                Log.d(Constants.TAG, "SelfReport " + self.toString());
                saveSelfReport(false,false);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSelfReport(false, true);
                finish();
            }
        });
    }





    private void showMorningSurvey() {
        //clear notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(Plugin.UPMC_NOTIFICATIONS);
        setContentView(R.layout.mjs_morn1);
        morning = new JSONObject();
        final Button submit = findViewById(R.id.mjs_morn1_submit);
        Button cancel = findViewById(R.id.mjs_morn1_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveMorning(true);
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePicker last_date = findViewById(R.id.mjs_morn1_last_date_mj);
                TimePicker last_time = findViewById(R.id.mjs_morn1_last_time_mj);
                CheckBox joint = findViewById(R.id.mjs_morn1_check_joint);
                CheckBox bowl = findViewById(R.id.mjs_morn1_check_bowl);
                CheckBox bong = findViewById(R.id.mjs_morn1_check_bong);
                CheckBox blunt = findViewById(R.id.mjs_morn1_check_blunt);
                CheckBox pen = findViewById(R.id.mjs_morn1_check_pen);
                CheckBox check_other = findViewById(R.id.mjs_morn1_check_other);
                EditText other = findViewById(R.id.mjs_morn1_check_other_input);
                EditText qnty = findViewById(R.id.mjs_morn1_how_much_mj);
                RadioGroup units = findViewById(R.id.mjs_morn1_smoke_units);
                if(units.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(getApplicationContext(),"Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!joint.isChecked() && !bowl.isChecked() && !bong.isChecked() && !blunt.isChecked()&& !pen.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(check_other.isChecked() && qnty.getText().length()==0) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }


                try {
                    StringBuilder dateTimeStringBuilder = new StringBuilder();
                    dateTimeStringBuilder.append(last_date.getMonth()+1)
                            .append("-")
                            .append(last_date.getDayOfMonth())
                            .append("-")
                            .append(last_date.getYear())
                            .append(" ");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        dateTimeStringBuilder.append(last_time.getHour()).append(":").append(last_time.getMinute());
                    else
                        dateTimeStringBuilder.append(last_time.getCurrentHour()).append(":").append(last_time.getCurrentMinute());
                    updateMorning(Constants.Morning.last_datetime, dateTimeStringBuilder.toString());
                    ArrayList<String> used_array = new ArrayList<>();
                    if(joint.isChecked())
                        used_array.add(Constants.joint);
                    if(bowl.isChecked())
                        used_array.add(Constants.bowl);
                    if(bong.isChecked())
                        used_array.add(Constants.bong);
                    if(blunt.isChecked())
                        used_array.add(Constants.blunt);
                    if(pen.isChecked())
                        used_array.add(Constants.pen);
                    if(check_other.isChecked())
                        used_array.add(other.getText().toString());
                    updateMorning(Constants.Morning.used_checkbox, used_array.toString());
                    updateMorning(Constants.Morning.used_qnty, qnty.getText());
                    String smoke_units = "none";
                    switch (units.getCheckedRadioButtonId()) {
                        case R.id.mjs_morn1_hits:
                            smoke_units = Constants.Morning.hits;
                            break;
                        case R.id.mjs_morn1_grams:
                            smoke_units = Constants.Morning.grams;
                            break;
                    }
                    updateMorning(Constants.Morning.units, smoke_units);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(Constants.TAG, "Morning " + morning.toString());
                showMorn2();
            }
        });
    }

    public void showMorn2() {
        setContentView(R.layout.mjs_morn2);
        Button submit = findViewById(R.id.mjs_morn2_submit);
        Button cancel = findViewById(R.id.mjs_morn2_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMorning(true);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekBar rate_craving = findViewById(R.id.mjs_morn2_rate_craving);
                EditText last24_drinks = findViewById(R.id.mjs_morn2_last_24_total_drinks);
                EditText last24_cigs = findViewById(R.id.mjs_morn2_last_24_total_cigarettes);
                SeekBar rate_relaxed = findViewById(R.id.mjs_morn2_rate_relaxed);
                SeekBar rate_sluggish = findViewById(R.id.mjs_morn2_rate_sluggish);
                SeekBar rate_foggy = findViewById(R.id.mjs_morn2_rate_foggy);
                SeekBar rate_anxious = findViewById(R.id.mjs_morn2_rate_anxious);
                SeekBar rate_sad = findViewById(R.id.mjs_morn2_rate_sad);
                if(last24_cigs.getText().length() ==0 || last24_drinks.getText().length()==0) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    updateMorning(Constants.Morning.rate_craving, rate_craving.getProgress());
                    updateMorning(Constants.Morning.last24_cigs, last24_cigs.getText());
                    updateMorning(Constants.Morning.last24_drinks, last24_drinks.getText());
                    updateMorning(Constants.Morning.rate_relaxed, rate_relaxed.getProgress());
                    updateMorning(Constants.Morning.rate_sluggish, rate_sluggish.getProgress());
                    updateMorning(Constants.Morning.rate_foggy, rate_foggy.getProgress());
                    updateMorning(Constants.Morning.rate_anxious, rate_anxious.getProgress());
                    updateMorning(Constants.Morning.rate_sad, rate_sad.getProgress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_SHORT).show();
                Log.d(Constants.TAG, "Morning " + morning.toString());
                saveMorning(false);
                finish();
            }

        });
    }


    public void showAfternoonSurvey() {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Plugin.UPMC_NOTIFICATIONS);
        setContentView(R.layout.mjs_aftn1);
        afternoon = new JSONObject();
        Button yes = findViewById(R.id.mjs_aftn1_yes_btn);
        Button no = findViewById(R.id.mjs_aftn1_no_btn);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateAfternoon(Constants.Afternoon.have_used, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showAftn1a();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateAfternoon(Constants.Afternoon.have_used, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showAftn1b();
            }
        });
    }

    public void showAftn1a() {
        setContentView(R.layout.mjs_aftn1_a);
        Button submit = findViewById(R.id.mjs_aftn1_a_submit);
        Button cancel = findViewById(R.id.mjs_aftn1_a_cancel);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker last_date = findViewById(R.id.mjs_aftn1_a_last_date_mj);
                TimePicker last_time = findViewById(R.id.mjs_aftn1_a_last_time_mj);
                CheckBox joint = findViewById(R.id.mjs_aftn1_a_check_joint);
                CheckBox bowl = findViewById(R.id.mjs_aftn1_a_check_bowl);
                CheckBox bong = findViewById(R.id.mjs_aftn1_a_check_bong);
                CheckBox blunt = findViewById(R.id.mjs_aftn1_a_check_blunt);
                CheckBox pen = findViewById(R.id.mjs_aftn1_a_check_pen);
                CheckBox check_other = findViewById(R.id.mjs_aftn1_a_check_other);
                EditText other = findViewById(R.id.mjs_aftn1_a_check_other_input);
                EditText qnty = findViewById(R.id.mjs_aftn1_a_how_much_mj);
                CheckBox used_tobacco = findViewById(R.id.mjs_aftn1_a_tobacco);
                CheckBox used_alcohol = findViewById(R.id.mjs_aftn1_a_alcohol);
                CheckBox used_none = findViewById(R.id.mjs_aftn1_a_none);
                RadioGroup units = findViewById(R.id.mjs_aftn1_a_units);
                if(units.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(getApplicationContext(),"Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!joint.isChecked() && !bowl.isChecked() && !bong.isChecked() && !blunt.isChecked()&& !pen.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(check_other.isChecked() && qnty.getText().length()==0) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!used_tobacco.isChecked() && !used_alcohol.isChecked() && !used_none.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(check_other.isChecked() && other.getText().length()==0) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuilder dateTimeStringBuilder = new StringBuilder();
                dateTimeStringBuilder.append(last_date.getMonth()+1)
                        .append("-")
                        .append(last_date.getDayOfMonth())
                        .append("-")
                        .append(last_date.getYear())
                        .append(" ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    dateTimeStringBuilder.append(last_time.getHour()).append(":").append(last_time.getMinute());
                else
                    dateTimeStringBuilder.append(last_time.getCurrentHour()).append(":").append(last_time.getCurrentMinute());
                try {
                    updateAfternoon(Constants.Afternoon.last_datetime, dateTimeStringBuilder.toString());
                    ArrayList<String> used_array = new ArrayList<>();
                    if(joint.isChecked())
                        used_array.add(Constants.joint);
                    if(bowl.isChecked())
                        used_array.add(Constants.bowl);
                    if(bong.isChecked())
                        used_array.add(Constants.bong);
                    if(blunt.isChecked())
                        used_array.add(Constants.blunt);
                    if(pen.isChecked())
                        used_array.add(Constants.pen);
                    if(check_other.isChecked())
                        used_array.add(other.getText().toString());
                    updateAfternoon(Constants.Afternoon.used_checkbox, used_array.toString());
                    updateAfternoon(Constants.Afternoon.used_qnty, qnty.getText());
                    String smoke_units = "none";
                    switch (units.getCheckedRadioButtonId()) {
                        case R.id.mjs_aftn1_a_hits:
                            smoke_units = Constants.Afternoon.hits;
                            break;
                        case R.id.mjs_aftn1_a_grams:
                            smoke_units = Constants.Afternoon.grams;
                            break;
                    }
                    updateAfternoon(Constants.Afternoon.units, smoke_units);
                    ArrayList<String> used_with_array = new ArrayList<>();
                    if(used_tobacco.isChecked())
                        used_with_array.add(Constants.Afternoon.tobacco);
                    if(used_alcohol.isChecked())
                        used_with_array.add(Constants.Afternoon.alcohol);
                    if(used_none.isChecked())
                        used_with_array.add(Constants.Afternoon.none);
                    updateAfternoon(Constants.Afternoon.usage_while_mj, used_with_array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                saveAfternoon(false);
                Log.d(Constants.TAG, "Afternoon " + afternoon.toString());
                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_SHORT).show();
                finish();
                }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAfternoon(true);
                finish();
            }
        });
    }


    public void showAftn1b() {
        setContentView(R.layout.mjs_aftn1_b);
        Button submit = findViewById(R.id.mjs_aftn1_b_submit);
        Button cancel = findViewById(R.id.mjs_aftn1_b_cancel);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekBar rate_craving = findViewById(R.id.mjs_aftn1_b_rate_craving);
                SeekBar rate_relaxed = findViewById(R.id.mjs_aftn1_b_rate_relaxed);
                SeekBar rate_sluggish = findViewById(R.id.mjs_aftn1_b_rate_sluggish);
                SeekBar rate_foggy = findViewById(R.id.mjs_aftn1_b_rate_foggy);
                SeekBar rate_anxious = findViewById(R.id.mjs_aftn1_b_rate_anxious);
                SeekBar rate_sad = findViewById(R.id.mjs_aftn1_b_rate_sad);
                try {
                    updateAfternoon(Constants.Afternoon.rate_craving, rate_craving.getProgress());
                    updateAfternoon(Constants.Afternoon.rate_relaxed, rate_relaxed.getProgress());
                    updateAfternoon(Constants.Afternoon.rate_sluggish, rate_sluggish.getProgress());
                    updateAfternoon(Constants.Afternoon.rate_foggy, rate_foggy.getProgress());
                    updateAfternoon(Constants.Afternoon.rate_anxious, rate_anxious.getProgress());
                    updateAfternoon(Constants.Afternoon.rate_sad, rate_sad.getProgress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_SHORT).show();
                Log.d(Constants.TAG, "Afternoon " + afternoon.toString());
                saveAfternoon(false);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAfternoon(true);
                finish();
            }
        });
    }


    public void showEveningSurvey() {
        evening = new JSONObject();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(Plugin.UPMC_NOTIFICATIONS);
        setContentView(R.layout.mjs_eve1);
        Button yes = findViewById(R.id.mjs_eve1_yes_btn);
        Button no = findViewById(R.id.mjs_eve1_no_btn);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateEvening(Constants.Evening.have_used, true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showEve1a();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    updateEvening(Constants.Evening.have_used, false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showEve1b();
            }
        });

    }


    public void showEve1a() {
        setContentView(R.layout.mjs_eve1_a);
        Button submit = findViewById(R.id.mjs_eve1_a_submit);
        Button cancel = findViewById(R.id.mjs_eve1_a_cancel);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePicker last_date = findViewById(R.id.mjs_eve1_a_last_date_mj);
                TimePicker last_time = findViewById(R.id.mjs_eve1_a_last_time_mj);
                CheckBox joint = findViewById(R.id.mjs_eve1_a_check_joint);
                CheckBox bowl = findViewById(R.id.mjs_eve1_a_check_bowl);
                CheckBox bong = findViewById(R.id.mjs_eve1_a_check_bong);
                CheckBox blunt = findViewById(R.id.mjs_eve1_a_check_blunt);
                CheckBox pen = findViewById(R.id.mjs_eve1_a_check_pen);
                CheckBox check_other = findViewById(R.id.mjs_eve1_a_check_other);
                EditText other = findViewById(R.id.mjs_eve1_a_check_other_input);
                EditText qnty = findViewById(R.id.mjs_eve1_a_how_much_mj);
                CheckBox used_tobacco = findViewById(R.id.mjs_eve1_a_tobacco);
                CheckBox used_alcohol = findViewById(R.id.mjs_eve1_a_alcohol);
                CheckBox used_none = findViewById(R.id.mjs_eve1_a_none);
                RadioGroup units = findViewById(R.id.mjs_eve1_a_units);
                if(units.getCheckedRadioButtonId()==-1) {
                    Toast.makeText(getApplicationContext(),"Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!joint.isChecked() && !bowl.isChecked() && !bong.isChecked() && !blunt.isChecked()&& !pen.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(check_other.isChecked() && qnty.getText().length()==0) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!used_tobacco.isChecked() && !used_alcohol.isChecked() && !used_none.isChecked()) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(check_other.isChecked() && other.getText().length()==0) {
                    Toast.makeText(getApplicationContext(), "Please complete the survey", Toast.LENGTH_SHORT).show();
                    return;
                }

                StringBuilder dateTimeStringBuilder = new StringBuilder();
                dateTimeStringBuilder.append(last_date.getMonth()+1)
                        .append("-")
                        .append(last_date.getDayOfMonth())
                        .append("-")
                        .append(last_date.getYear())
                        .append(" ");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                    dateTimeStringBuilder.append(last_time.getHour()).append(":").append(last_time.getMinute());
                else
                    dateTimeStringBuilder.append(last_time.getCurrentHour()).append(":").append(last_time.getCurrentMinute());
                try {
                    updateEvening(Constants.Evening.last_datetime, dateTimeStringBuilder.toString());
                    ArrayList<String> used_array = new ArrayList<>();
                    if(joint.isChecked())
                        used_array.add(Constants.joint);
                    if(bowl.isChecked())
                        used_array.add(Constants.bowl);
                    if(bong.isChecked())
                        used_array.add(Constants.bong);
                    if(blunt.isChecked())
                        used_array.add(Constants.blunt);
                    if(pen.isChecked())
                        used_array.add(Constants.pen);
                    if(check_other.isChecked())
                        used_array.add(other.getText().toString());
                    updateEvening(Constants.Evening.used_checkbox, used_array.toString());
                    updateEvening(Constants.Evening.used_qnty, qnty.getText());
                    String smoke_units = "none";
                    switch (units.getCheckedRadioButtonId()) {
                        case R.id.mjs_eve1_a_hits:
                            smoke_units = Constants.Evening.hits;
                            break;
                        case R.id.mjs_eve1_a_grams:
                            smoke_units = Constants.Evening.grams;
                            break;
                    }
                    updateEvening(Constants.Evening.units, smoke_units);
                    ArrayList<String> used_with_array = new ArrayList<>();
                    if(used_tobacco.isChecked())
                        used_with_array.add(Constants.Evening.tobacco);
                    if(used_alcohol.isChecked())
                        used_with_array.add(Constants.Evening.alcohol);
                    if(used_none.isChecked())
                        used_with_array.add(Constants.Evening.none);
                    updateEvening(Constants.Evening.usage_while_mj, used_with_array.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                saveEvening(false);
                Log.d(Constants.TAG, "Evening " + evening.toString());
                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvening(true);
                finish();
            }
        });
    }

    public void showEve1b() {
        setContentView(R.layout.mjs_eve1_b);
        Button submit = findViewById(R.id.mjs_eve1_b_submit);
        Button cancel = findViewById(R.id.mjs_eve1_b_cancel);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SeekBar rate_craving = findViewById(R.id.mjs_eve1_b_rate_craving);
                SeekBar rate_relaxed = findViewById(R.id.mjs_eve1_b_rate_relaxed);
                SeekBar rate_sluggish = findViewById(R.id.mjs_eve1_b_rate_sluggish);
                SeekBar rate_foggy = findViewById(R.id.mjs_eve1_b_rate_foggy);
                SeekBar rate_anxious = findViewById(R.id.mjs_eve1_b_rate_anxious);
                SeekBar rate_sad = findViewById(R.id.mjs_eve1_b_rate_sad);
                SeekBar rate_problems = findViewById(R.id.rate_solving_problems);
                SeekBar rate_remembering = findViewById(R.id.rate_remembering);
                SeekBar rate_attention = findViewById(R.id.rate_attention);
                SeekBar rate_concentration = findViewById(R.id.rate_concentrating);

                try {
                    updateEvening(Constants.Evening.rate_craving, rate_craving.getProgress());
                    updateEvening(Constants.Evening.rate_relaxed, rate_relaxed.getProgress());
                    updateEvening(Constants.Evening.rate_sluggish, rate_sluggish.getProgress());
                    updateEvening(Constants.Evening.rate_foggy, rate_foggy.getProgress());
                    updateEvening(Constants.Evening.rate_anxious, rate_anxious.getProgress());
                    updateEvening(Constants.Evening.rate_sad, rate_sad.getProgress());
                    updateEvening(Constants.Evening.rate_problems, rate_problems.getProgress());
                    updateEvening(Constants.Evening.rate_remembering, rate_remembering.getProgress());
                    updateEvening(Constants.Evening.rate_attention, rate_attention.getProgress());
                    updateEvening(Constants.Evening.rate_concentration, rate_concentration.getProgress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_SHORT).show();
                Log.d(Constants.TAG, "Evening " + evening.toString());
                saveEvening(false);
                finish();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveEvening(true);
                finish();
            }
        });
    }


    private void start2HrAlarm() {
        Log.d(Constants.TAG, "MJ_survey:cancel2HrAlarm");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent_2hr = new Intent(this, Plugin.class).setAction(Plugin.ACTION_2HR_ALARM);
        alarmIntent_2hr.putExtra(Constants.ALARM_COMM, 2);
//        int interval = 3 * 60 * 60 * 1000; // change it to 3 hours
        int interval = 60 * 1000;
        PendingIntent alarmPendingIntent_2hr = PendingIntent.getService(this, 668, alarmIntent_2hr, 0);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, alarmPendingIntent_2hr);
    }


    private void cancel2hrAlarm() {
        Log.d(Constants.TAG, "MJ_survey:cancel2HrAlarm");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent_2hr = new Intent(this, Plugin.class).setAction(Plugin.ACTION_2HR_ALARM);
        alarmIntent_2hr.putExtra(Constants.ALARM_COMM, 2);
        PendingIntent alarmPendingIntent_2hr = PendingIntent.getService(this, 668, alarmIntent_2hr, 0);
        assert alarmManager != null;
        alarmManager.cancel(alarmPendingIntent_2hr);
    }


    //Launches the muse integration
    private void startMUSE() {
        Intent muse = new Intent(Intent.ACTION_VIEW);
        muse.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        muse.setData(Uri.parse("fourtwentystudy://"));
        getApplicationContext().startActivity(muse);
    }

    public void sendMessageToPlugin(String action) {
        Intent intent = new Intent(this, Plugin.class).setAction(action);
        startService(intent);
    }


    /**
     * Stores the user sef-report to afternoon JSON
     *
     * @param key
     * @param value
     * @throws JSONException
     */
    private void updateSelfReport(String key, Object value) throws JSONException {
        self.put(key, value);
    }

    /**
     * Save users' self-report questions to database
     *
     * @param canceled
     */
    private void saveSelfReport(boolean is_start, boolean canceled) {
        if(!canceled) {
            if(is_start)
                sendMessageToPlugin(Plugin.ACTION_MJ_SELF_START_COMPLETED);
            else {
                sendMessageToPlugin(Plugin.ACTION_MJ_SELF_END_COMPLETED);
                cancel2hrAlarm();
            }

        }
        ContentValues data = new ContentValues();
        data.put(Provider.UPMC_MJ_Data.TIMESTAMP, System.currentTimeMillis());
        data.put(Provider.UPMC_MJ_Data.DEVICE_ID, Aware.getSetting(this, Aware_Preferences.DEVICE_ID));
        data.put(Provider.UPMC_MJ_Data.QUESTION_TYPE, "self-report" + ((is_start)? "(start)" : "(end)")  + ((canceled) ? "-canceled" : ""));
        data.put(Provider.UPMC_MJ_Data.QUESTION_ANSWERS, self.toString());
        getContentResolver().insert(Provider.UPMC_MJ_Data.CONTENT_URI, data);
        if (!canceled) sendBroadcast(new Intent(ESM.ACTION_AWARE_ESM_QUEUE_COMPLETE));
    }






    /**
     * Stores the user answers to afternoon JSON
     *
     * @param key
     * @param value
     * @throws JSONException
     */
    private void updateAfternoon(String key, Object value) throws JSONException {
        afternoon.put(key, value);
    }

    /**
     * Save users' afternoon questions to database
     *
     * @param canceled
     */
    private void saveAfternoon(boolean canceled) {
        ContentValues data = new ContentValues();
        data.put(Provider.UPMC_MJ_Data.TIMESTAMP, System.currentTimeMillis());
        data.put(Provider.UPMC_MJ_Data.DEVICE_ID, Aware.getSetting(this, Aware_Preferences.DEVICE_ID));
        data.put(Provider.UPMC_MJ_Data.QUESTION_TYPE, "afternoon" + ((canceled) ? "-canceled" : ""));
        data.put(Provider.UPMC_MJ_Data.QUESTION_ANSWERS, afternoon.toString());
        getContentResolver().insert(Provider.UPMC_MJ_Data.CONTENT_URI, data);
        if (!canceled) sendBroadcast(new Intent(ESM.ACTION_AWARE_ESM_QUEUE_COMPLETE));
    }

    /**
     * Stores the user answers to evening JSON
     *
     * @param key
     * @param value
     * @throws JSONException
     */
    private void updateEvening(String key, Object value) throws JSONException {
        evening.put(key, value);
    }

    /**
     * Save users' evening questions to database
     *
     * @param canceled
     */
    private void saveEvening(boolean canceled) {
        ContentValues data = new ContentValues();
        data.put(Provider.UPMC_MJ_Data.TIMESTAMP, System.currentTimeMillis());
        data.put(Provider.UPMC_MJ_Data.DEVICE_ID, Aware.getSetting(this, Aware_Preferences.DEVICE_ID));
        data.put(Provider.UPMC_MJ_Data.QUESTION_TYPE, "evening" + ((canceled) ? "-canceled" : ""));
        data.put(Provider.UPMC_MJ_Data.QUESTION_ANSWERS, evening.toString());
        getContentResolver().insert(Provider.UPMC_MJ_Data.CONTENT_URI, data);
        if (!canceled) sendBroadcast(new Intent(ESM.ACTION_AWARE_ESM_QUEUE_COMPLETE));
    }

    /**
     * Stores the user answer to morning JSON
     *
     * @param key
     * @param value
     * @throws JSONException
     */
    private void updateMorning(String key, Object value) throws JSONException {
        morning.put(key, value);
    }

    /**
     * Save users' morning questions to database
     *
     * @param canceled
     */
    private void saveMorning(boolean canceled) {
        ContentValues data = new ContentValues();
        data.put(Provider.UPMC_MJ_Data.TIMESTAMP, System.currentTimeMillis());
        data.put(Provider.UPMC_MJ_Data.DEVICE_ID, Aware.getSetting(this, Aware_Preferences.DEVICE_ID));
        data.put(Provider.UPMC_MJ_Data.QUESTION_TYPE, "morning" + ((canceled) ? "-canceled" : ""));
        data.put(Provider.UPMC_MJ_Data.QUESTION_ANSWERS, morning.toString());
        getContentResolver().insert(Provider.UPMC_MJ_Data.CONTENT_URI, data);
        if (!canceled) sendBroadcast(new Intent(ESM.ACTION_AWARE_ESM_QUEUE_COMPLETE));
    }
}
