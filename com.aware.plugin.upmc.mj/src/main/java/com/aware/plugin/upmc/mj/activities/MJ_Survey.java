package com.aware.plugin.upmc.mj.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
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
    private JSONObject fingerprint;

    private boolean permissions_ok = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(Constants.TAG, "onCreate()");
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
        } else {
            if (!Aware.IS_CORE_RUNNING) {
                //This initialises the core framework, assigns Device ID if it doesn't exist yet, etc.
                Intent aware = new Intent(getApplicationContext(), Aware.class);
                startService(aware);
            }
        }
    }

    private class AsyncJoin extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            Aware.joinStudy(getApplicationContext(), "https://r2d2.hcii.cs.cmu.edu/aware/dashboard/index.php/webservice/index/108/z4Q4nINGkqq8");

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
            Log.d(Constants.TAG, "Joined ok!");
            Log.d(Constants.TAG, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_LABEL));
            Log.d(Constants.TAG, Aware.getSetting(getApplicationContext(), Aware_Preferences.DEVICE_ID));
            Toast.makeText(getApplicationContext(), "Joined OK!", Toast.LENGTH_LONG).show();
            finish();
        }
    }

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

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(Constants.TAG, "onResume");

        if (!permissions_ok) {
            return; //not done yet with permissions
        }

        if (!Aware.isStudy(getApplicationContext())) {
            new AsyncJoin().execute();

        } else {
            Applications.isAccessibilityServiceActive(getApplicationContext());
            Aware.isBatteryOptimizationIgnored(getApplicationContext(), "com.aware.plugin.upmc.mj");

            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_MJ_MORNING)) {

                Log.d(Constants.TAG, "1 if");

                //clear notification
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(Plugin.UPMC_NOTIFICATIONS);

                morning = new JSONObject();

                setContentView(R.layout.activity_mj_survey_morning);

                final EditText last24drinks = findViewById(R.id.last_24_total_drinks);
                final EditText last24cigars = findViewById(R.id.last_24_total_cigarettes);

                Button initiated_submit_1 = findViewById(R.id.initiated_submit_1);
                initiated_submit_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            updateMorning("last_24_drinks", last24drinks.getText().toString());
                            updateMorning("last_24_cigars", last24cigars.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        setContentView(R.layout.activity_mj_survey_common_mj_branching);

                        Button no = findViewById(R.id.btn_marijuana_no);
                        no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    updateMorning("last_24_mj", false);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                setContentView(R.layout.activity_mj_initiated_ratings);

                                final SeekBar craving = findViewById(R.id.rate_craving);
                                final SeekBar relaxed = findViewById(R.id.rate_relaxed);
                                final SeekBar sluggish = findViewById(R.id.rate_sluggish);
                                final SeekBar fogging = findViewById(R.id.rate_foggy);
                                final SeekBar anxious = findViewById(R.id.rate_anxious);
                                final SeekBar sad = findViewById(R.id.rate_sad);

                                Button finalSubmit = findViewById(R.id.initiated_submit_1);
                                finalSubmit.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        try {
                                            updateMorning("rate_craving", craving.getProgress());
                                            updateMorning("rate_relaxed", relaxed.getProgress());
                                            updateMorning("rate_sluggish", sluggish.getProgress());
                                            updateMorning("rate_foggy", fogging.getProgress());
                                            updateMorning("rate_anxious", anxious.getProgress());
                                            updateMorning("rate_sad", sad.getProgress());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        saveMorning(false);

                                        Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();

                                        startMUSE();

                                        finish();
                                    }
                                });

                                Button cancel = findViewById(R.id.initiated_cancel_1);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveMorning(true);
                                        finish();
                                    }
                                });
                            }
                        });

                        Button yes = findViewById(R.id.btn_marijuana_yes);
                        yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {
                                    updateMorning("last_24_mj", true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                setContentView(R.layout.activity_mj_survey_marijuana_positive);

                                final DatePicker lastDay = findViewById(R.id.last_date_mj);
                                final TimePicker lastTime = findViewById(R.id.last_time_mj);

                                final CheckBox joint = findViewById(R.id.check_joint);
                                final CheckBox pipe = findViewById(R.id.check_pipe);
                                final CheckBox bong = findViewById(R.id.check_bong);
                                final CheckBox blunt = findViewById(R.id.check_blunt);

                                final CheckBox other = findViewById(R.id.check_other);
                                final EditText other_desc = findViewById(R.id.check_other_input);
                                other_desc.addTextChangedListener(new TextWatcher() {
                                    @Override
                                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                    }

                                    @Override
                                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                                        if (s.length() == 0) other.setChecked(false);
                                        else other.setChecked(true);
                                    }

                                    @Override
                                    public void afterTextChanged(Editable s) {
                                    }
                                });

                                final EditText mj_amount = findViewById(R.id.how_much_mj);

                                Button initiated_submit_3 = findViewById(R.id.initiated_submit_3);
                                initiated_submit_3.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        try {
                                            updateMorning("last_day_mj", lastDay.getDayOfMonth() + "-" + lastDay.getMonth() + "-" + lastDay.getYear());
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                                updateMorning("last_time_mj", lastTime.getHour() + ":" + lastTime.getMinute());
                                            else
                                                updateMorning("last_time_mj", lastTime.getCurrentHour() + ":" + lastTime.getCurrentMinute());
                                            updateMorning("used_joint", joint.isChecked());
                                            updateMorning("used_pipe", pipe.isChecked());
                                            updateMorning("used_bong", bong.isChecked());
                                            updateMorning("used_blunt", blunt.isChecked());
                                            updateMorning("used_other", other.isChecked());
                                            updateMorning("other_desc", other_desc.getText().toString());
                                            updateMorning("used_amount", mj_amount.getText().toString());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        setContentView(R.layout.activity_mj_initiated_ratings);

                                        final SeekBar craving = findViewById(R.id.rate_craving);
                                        final SeekBar relaxed = findViewById(R.id.rate_relaxed);
                                        final SeekBar sluggish = findViewById(R.id.rate_sluggish);
                                        final SeekBar fogging = findViewById(R.id.rate_foggy);
                                        final SeekBar anxious = findViewById(R.id.rate_anxious);
                                        final SeekBar sad = findViewById(R.id.rate_sad);

                                        Button finalSubmit = findViewById(R.id.initiated_submit_1);
                                        finalSubmit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                try {
                                                    updateMorning("rate_craving", craving.getProgress());
                                                    updateMorning("rate_relaxed", relaxed.getProgress());
                                                    updateMorning("rate_sluggish", sluggish.getProgress());
                                                    updateMorning("rate_foggy", fogging.getProgress());
                                                    updateMorning("rate_anxious", anxious.getProgress());
                                                    updateMorning("rate_sad", sad.getProgress());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                saveMorning(false);

                                                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();

                                                startMUSE();

                                                finish();
                                            }
                                        });

                                        Button cancel = findViewById(R.id.initiated_cancel_1);
                                        cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                saveMorning(true);
                                                finish();
                                            }
                                        });
                                    }
                                });

                                Button cancel = findViewById(R.id.initiated_cancel_3);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveMorning(true);
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });

                Button cancel = findViewById(R.id.cancel);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveMorning(true);
                        finish();
                    }
                });

            } else if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_MJ_EVENING)) {

                Log.d(Constants.TAG, "2 if");


                //clear notification
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(Plugin.UPMC_NOTIFICATIONS);

                evening = new JSONObject();

                setContentView(R.layout.activity_mj_survey_common_mj_branching);

                Button no = findViewById(R.id.btn_marijuana_no);
                no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            updateEvening("last_24_mj", false);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        setContentView(R.layout.activity_mj_initiated_ratings);

                        final SeekBar craving = findViewById(R.id.rate_craving);
                        final SeekBar relaxed = findViewById(R.id.rate_relaxed);
                        final SeekBar sluggish = findViewById(R.id.rate_sluggish);
                        final SeekBar fogging = findViewById(R.id.rate_foggy);
                        final SeekBar anxious = findViewById(R.id.rate_anxious);
                        final SeekBar sad = findViewById(R.id.rate_sad);

                        Button finalSubmit = findViewById(R.id.initiated_submit_1);
                        finalSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {
                                    updateEvening("rate_craving", craving.getProgress());
                                    updateEvening("rate_relaxed", relaxed.getProgress());
                                    updateEvening("rate_sluggish", sluggish.getProgress());
                                    updateEvening("rate_foggy", fogging.getProgress());
                                    updateEvening("rate_anxious", anxious.getProgress());
                                    updateEvening("rate_sad", sad.getProgress());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                saveEvening(false);

                                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();

                                startMUSE();

                                finish();
                            }
                        });

                        Button cancel = findViewById(R.id.initiated_cancel_1);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveEvening(true);
                                finish();
                            }
                        });
                    }
                });

                Button yes = findViewById(R.id.btn_marijuana_yes);
                yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        try {
                            updateEvening("last_24_mj", true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        setContentView(R.layout.activity_mj_survey_marijuana_positive);

                        final DatePicker lastDay = findViewById(R.id.last_date_mj);
                        final TimePicker lastTime = findViewById(R.id.last_time_mj);

                        final CheckBox joint = findViewById(R.id.check_joint);
                        final CheckBox pipe = findViewById(R.id.check_pipe);
                        final CheckBox bong = findViewById(R.id.check_bong);
                        final CheckBox blunt = findViewById(R.id.check_blunt);

                        final CheckBox other = findViewById(R.id.check_other);
                        final EditText other_desc = findViewById(R.id.check_other_input);
                        other_desc.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if (s.length() == 0) other.setChecked(false);
                                else other.setChecked(true);
                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                            }
                        });

                        final EditText mj_amount = findViewById(R.id.how_much_mj);

                        Button initiated_submit_3 = findViewById(R.id.initiated_submit_3);
                        initiated_submit_3.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                try {
                                    updateEvening("last_day_mj", lastDay.getDayOfMonth() + "-" + lastDay.getMonth() + "-" + lastDay.getYear());
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        updateEvening("last_time_mj", lastTime.getHour() + ":" + lastTime.getMinute());
                                    else
                                        updateEvening("last_time_mj", lastTime.getCurrentHour() + ":" + lastTime.getCurrentMinute());
                                    updateEvening("used_joint", joint.isChecked());
                                    updateEvening("used_pipe", pipe.isChecked());
                                    updateEvening("used_bong", bong.isChecked());
                                    updateEvening("used_blunt", blunt.isChecked());
                                    updateEvening("used_other", other.isChecked());
                                    updateEvening("other_desc", other_desc.getText().toString());
                                    updateEvening("used_amount", mj_amount.getText().toString());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                setContentView(R.layout.activity_mj_initiated_ratings);

                                final SeekBar craving = findViewById(R.id.rate_craving);
                                final SeekBar relaxed = findViewById(R.id.rate_relaxed);
                                final SeekBar sluggish = findViewById(R.id.rate_sluggish);
                                final SeekBar fogging = findViewById(R.id.rate_foggy);
                                final SeekBar anxious = findViewById(R.id.rate_anxious);
                                final SeekBar sad = findViewById(R.id.rate_sad);

                                Button initiated_submit_1 = findViewById(R.id.initiated_submit_1);
                                initiated_submit_1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        try {
                                            updateEvening("rate_craving", craving.getProgress());
                                            updateEvening("rate_relaxed", relaxed.getProgress());
                                            updateEvening("rate_sluggish", sluggish.getProgress());
                                            updateEvening("rate_foggy", fogging.getProgress());
                                            updateEvening("rate_anxious", anxious.getProgress());
                                            updateEvening("rate_sad", sad.getProgress());
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        setContentView(R.layout.activity_mj_survey_evening);

                                        final SeekBar rate_solving_problems = findViewById(R.id.rate_solving_problems);
                                        final SeekBar rate_remembering = findViewById(R.id.rate_remembering);
                                        final SeekBar rate_attention = findViewById(R.id.rate_attention);
                                        final SeekBar rate_concentration = findViewById(R.id.rate_concentrating);

                                        Button finalSubmit = findViewById(R.id.initiated_submit_1);
                                        finalSubmit.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    updateEvening("rate_solving_problems", rate_solving_problems.getProgress());
                                                    updateEvening("rate_remembering", rate_remembering.getProgress());
                                                    updateEvening("rate_attention", rate_attention.getProgress());
                                                    updateEvening("rate_concentration", rate_concentration.getProgress());
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }

                                                saveEvening(false);

                                                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();

                                                startMUSE();

                                                finish();
                                            }
                                        });

                                        Button cancel = findViewById(R.id.cancel);
                                        cancel.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                saveEvening(true);
                                                finish();
                                            }
                                        });
                                    }
                                });

                                Button cancel = findViewById(R.id.initiated_cancel_1);
                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveEvening(true);
                                        finish();
                                    }
                                });
                            }
                        });

                        Button cancel = findViewById(R.id.initiated_cancel_3);
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                saveEvening(true);
                                finish();
                            }
                        });
                    }
                });
            } else if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_USER_INIT_END)) {
                Log.d(Constants.TAG, "ACTION_USER_INIT_END");
                showUISurvey1();
            } else {

                // start 3 hour alarm part.
                Log.d(Constants.TAG, "3 if");
                //clear notification
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.cancel(Plugin.UPMC_NOTIFICATIONS);
                fingerprint = new JSONObject();
                setContentView(R.layout.activity_mj_user_initiated);
                final DatePicker mj_start_date = findViewById(R.id.u1_start_date_mj);
                final TimePicker mj_start_time = findViewById(R.id.ui_start_time_mj);
                final SeekBar rate_how_high = findViewById(R.id.ui_rate_how_high);
                final SeekBar rate_mj_craving = findViewById(R.id.ui_rate_craving);
                final CheckBox used_alcohol = findViewById(R.id.ui_checkBox1);
                final CheckBox used_tobacco = findViewById(R.id.ui_checkBox2);
                final CheckBox used_caffeine = findViewById(R.id.ui_checkBox3);
                final CheckBox used_other = findViewById(R.id.ui_check_other);
                final EditText used_other_dec = findViewById(R.id.ui_check_other_input);
                used_other_dec.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        used_other.setChecked(true);
                    }
                });
                final RadioGroup socialMJ = findViewById(R.id.ui_social_context);

                Button initiated_submit_2 = findViewById(R.id.ui_initiated_submit_2);
                initiated_submit_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(Constants.TAG, "MJ_SURVEY:Ui survey");
                        try {
                            updateFingerprint("mj_start_date", mj_start_date.getDayOfMonth() + "-" + mj_start_date.getMonth() + "-" + mj_start_date.getYear());
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                updateFingerprint("last_time_mj", mj_start_time.getHour() + ":" + mj_start_time.getMinute());
                            else
                                updateFingerprint("last_time_mj", mj_start_time.getCurrentHour() + ":" + mj_start_time.getCurrentMinute());
                            updateFingerprint("rate_how_high", rate_how_high.getProgress());
                            updateFingerprint("rate_mj_craving", rate_mj_craving.getProgress());
                            updateFingerprint("used_alcohol", used_alcohol.isChecked());
                            updateFingerprint("used_tobacco", used_tobacco.isChecked());
                            updateFingerprint("used_caffeine", used_caffeine.isChecked());
                            updateFingerprint("used_other", used_other.isChecked());
                            updateFingerprint("used_other_desc", used_other_dec.getText().toString());
                            if (socialMJ.getCheckedRadioButtonId() != -1)
                                updateFingerprint("social_context", findViewById(socialMJ.getCheckedRadioButtonId()).getTag().toString());
                            else
                                updateFingerprint("social_context", "NA");

                            saveFingerprint(false);
                            Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
                            start3HrAlarm();
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });


//                        @Override
//                        public void onClick(View v) {
//
//                            try {
//                                updateFingerprint("rate_how_high", rate_how_high.getProgress());
//                                updateFingerprint("used_alcohol", used_alcohol.isChecked());
//                                updateFingerprint("used_tobacco", used_tobacco.isChecked());
//                                updateFingerprint("used_caffeine", used_caffeine.isChecked());
//                                updateFingerprint("used_other", used_other.isChecked());
//                                updateFingerprint("used_other_desc", used_other_dec.getText().toString());
//                                if (socialMJ.getCheckedRadioButtonId() != -1)
//                                    updateFingerprint("social_context", findViewById(socialMJ.getCheckedRadioButtonId()).getTag().toString());
//                                else
//                                    updateFingerprint("social_context", "NA");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            setContentView(R.layout.activity_mj_survey_marijuana_positive);
//
//                            final DatePicker lastDay = findViewById(R.id.last_date_mj);
//                            final TimePicker lastTime = findViewById(R.id.last_time_mj);
//
//                            final CheckBox joint = findViewById(R.id.check_joint);
//                            final CheckBox pipe = findViewById(R.id.check_pipe);
//                            final CheckBox bong = findViewById(R.id.check_bong);
//                            final CheckBox blunt = findViewById(R.id.check_blunt);
//                            final CheckBox other = findViewById(R.id.check_other);
//                            final EditText other_desc = findViewById(R.id.check_other_input);
//                            other_desc.addTextChangedListener(new TextWatcher() {
//                                @Override
//                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                                }
//
//                                @Override
//                                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                    if (s.length() == 0) other.setChecked(false);
//                                    else other.setChecked(true);
//                                }
//
//                                @Override
//                                public void afterTextChanged(Editable s) {
//                                }
//                            });
//
//                            final EditText mj_amount = findViewById(R.id.how_much_mj);
//
//                            Button initiated_submit_3 = findViewById(R.id.initiated_submit_3);
//                            initiated_submit_3.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    try {
//                                        updateFingerprint("last_day_mj", lastDay.getDayOfMonth() + "-" + lastDay.getMonth() + "-" + lastDay.getYear());
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                                            updateFingerprint("last_time_mj", lastTime.getHour() + ":" + lastTime.getMinute());
//                                        else
//                                            updateFingerprint("last_time_mj", lastTime.getCurrentHour() + ":" + lastTime.getCurrentMinute());
//                                        updateFingerprint("used_joint", joint.isChecked());
//                                        updateFingerprint("used_pipe", pipe.isChecked());
//                                        updateFingerprint("used_bong", bong.isChecked());
//                                        updateFingerprint("used_blunt", blunt.isChecked());
//                                        updateFingerprint("used_other", other.isChecked());
//                                        updateFingerprint("other_desc", other_desc.getText().toString());
//                                        updateFingerprint("used_amount", mj_amount.getText().toString());
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    setContentView(R.layout.activity_mj_initiated_ratings);
//
//                                    final SeekBar craving = findViewById(R.id.rate_craving);
//                                    final SeekBar relaxed = findViewById(R.id.rate_relaxed);
//                                    final SeekBar sluggish = findViewById(R.id.rate_sluggish);
//                                    final SeekBar fogging = findViewById(R.id.rate_foggy);
//                                    final SeekBar anxious = findViewById(R.id.rate_anxious);
//                                    final SeekBar sad = findViewById(R.id.rate_sad);
//
//                                    Button initiated_submit_1 = findViewById(R.id.initiated_submit_1);
//                                    initiated_submit_1.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            try {
//                                                updateFingerprint("rate_craving", craving.getProgress());
//                                                updateFingerprint("rate_relaxed", relaxed.getProgress());
//                                                updateFingerprint("rate_sluggish", sluggish.getProgress());
//                                                updateFingerprint("rate_foggy", fogging.getProgress());
//                                                updateFingerprint("rate_anxious", anxious.getProgress());
//                                                updateFingerprint("rate_sad", sad.getProgress());
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                            saveFingerprint(false);
//
//                                            Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
//
//                                            startMUSE();
//
//                                            finish();
//                                        }
//                                    });
//
//                                    Button cancel = findViewById(R.id.initiated_cancel_1);
//                                    cancel.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            saveFingerprint(true);
//                                            finish();
//                                        }
//                                    });
//                                }
//                            });
//
//                            Button cancel = findViewById(R.id.initiated_cancel_3);
//                            cancel.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    saveFingerprint(true);
//                                    finish();
//                                }
//                            });
//                        }
//                    });
//
//                    Button cancel = findViewById(R.id.initiated_cancel_2);
//                    cancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            saveFingerprint(true);
//                            finish();
//                        }
//                    });
            }
        }
    }


//        if (permissions_ok) {
//
//            if (!Aware.isStudy(this)) {
//
//                Aware.joinStudy(this, "https://r2d2.hcii.cs.cmu.edu/aware/dashboard/index.php/webservice/index/108/z4Q4nINGkqq8");
//
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ACCELEROMETER, true);
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_ACCELEROMETER, 200000);
//
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_SIGNIFICANT_MOTION, true); //to make accelerometer logging less verbose.
//
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_BATTERY, true);
//
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_ESM, true); //in case we want to push ESMs from dashboard side
//
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_APPLICATIONS, true);
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_NOTIFICATIONS, true);
//
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_LIGHT, true);
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.THRESHOLD_LIGHT, 5);
//
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_COMMUNICATION_EVENTS, true);
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_CALLS, true);
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_MESSAGES, true);
//
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_SCREEN, true);
//
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.STATUS_NETWORK_EVENTS, true);
//
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_WIFI_ONLY, true);
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_FALLBACK_NETWORK, 6);
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_WEBSERVICE, 30);
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.FREQUENCY_CLEAN_OLD_DATA, 1);
//                Aware.setSetting(getApplicationContext(), Aware_Preferences.WEBSERVICE_SILENT, true);
//
//                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.activity_recognition.Settings.STATUS_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, true);
//                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.activity_recognition.Settings.FREQUENCY_PLUGIN_GOOGLE_ACTIVITY_RECOGNITION, 300);
//                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.activity_recognition");
//
//                Aware.setSetting(getApplicationContext(), com.aware.plugin.device_usage.Settings.STATUS_PLUGIN_DEVICE_USAGE, true);
//                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.device_usage");
//
//                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.STATUS_GOOGLE_FUSED_LOCATION, true);
//                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.FREQUENCY_GOOGLE_FUSED_LOCATION, 300);
//                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION, 300);
//                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.ACCURACY_GOOGLE_FUSED_LOCATION, 102);
//                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.FALLBACK_LOCATION_TIMEOUT, 20);
//                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.LOCATION_SENSITIVITY, 5);
//                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.fused_location");
//
//                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.studentlife.audio_final");
//                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.fitbit");
//
//                //last one to be started is the app itself (which is on itself a plugin too)
//                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.upmc.mj");
//
//                //Ask accessibility to be activated
//                Applications.isAccessibilityServiceActive(getApplicationContext());
//
//                //Ask doze to be disabled
//                Aware.isBatteryOptimizationIgnored(getApplicationContext(), getPackageName());
//
//            } else {
//                if (Scheduler.getSchedule(this, Plugin.SCHEDULE_MORNING_MJ) == null) {
//                    try {
//                        Scheduler.Schedule morning = new Scheduler.Schedule(Plugin.SCHEDULE_MORNING_MJ)
//                                .addHour(10)
//                                .addMinute(0)
//                                .setActionType(Scheduler.ACTION_TYPE_SERVICE)
//                                .setActionIntentAction(Plugin.ACTION_MJ_MORNING)
//                                .setActionClass(getPackageName() + "/" + Plugin.class.getName());
//
//                        Scheduler.saveSchedule(getApplicationContext(), morning);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (Scheduler.getSchedule(this, Plugin.SCHEDULE_FINGERPRING_MJ) == null) {
//                    try {
//                        Scheduler.Schedule afternoon = new Scheduler.Schedule(Plugin.SCHEDULE_FINGERPRING_MJ)
//                                .addHour(15)
//                                .addMinute(0)
//                                .setActionType(Scheduler.ACTION_TYPE_SERVICE)
//                                .setActionIntentAction(Plugin.ACTION_MJ_FINGERPRINT)
//                                .setActionClass(getPackageName() + "/" + Plugin.class.getName());
//
//                        Scheduler.saveSchedule(getApplicationContext(), afternoon);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (Scheduler.getSchedule(this, Plugin.SCHEDULE_EVENING_MJ) == null) {
//                    try {
//                        Scheduler.Schedule evening = new Scheduler.Schedule(Plugin.SCHEDULE_EVENING_MJ)
//                                .addHour(20)
//                                .addMinute(0)
//                                .setActionType(Scheduler.ACTION_TYPE_SERVICE)
//                                .setActionIntentAction(Plugin.ACTION_MJ_EVENING)
//                                .setActionClass(getPackageName() + "/" + Plugin.class.getName());
//
//                        Scheduler.saveSchedule(getApplicationContext(), evening);
//
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_MJ_MORNING)) {
//
//                    Log.d(Constants.TAG, "1 if");
//
//                    //clear notification
//                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    notificationManager.cancel(Plugin.UPMC_NOTIFICATIONS);
//
//                    morning = new JSONObject();
//
//                    setContentView(R.layout.activity_mj_survey_morning);
//
//                    final EditText last24drinks = findViewById(R.id.last_24_total_drinks);
//                    final EditText last24cigars = findViewById(R.id.last_24_total_cigarettes);
//
//                    Button initiated_submit_1 = findViewById(R.id.initiated_submit_1);
//                    initiated_submit_1.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            try {
//                                updateMorning("last_24_drinks", last24drinks.getText().toString());
//                                updateMorning("last_24_cigars", last24cigars.getText().toString());
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            setContentView(R.layout.activity_mj_survey_common_mj_branching);
//
//                            Button no = findViewById(R.id.btn_marijuana_no);
//                            no.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    try {
//                                        updateMorning("last_24_mj", false);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    setContentView(R.layout.activity_mj_initiated_ratings);
//
//                                    final SeekBar craving = findViewById(R.id.rate_craving);
//                                    final SeekBar relaxed = findViewById(R.id.rate_relaxed);
//                                    final SeekBar sluggish = findViewById(R.id.rate_sluggish);
//                                    final SeekBar fogging = findViewById(R.id.rate_foggy);
//                                    final SeekBar anxious = findViewById(R.id.rate_anxious);
//                                    final SeekBar sad = findViewById(R.id.rate_sad);
//
//                                    Button finalSubmit = findViewById(R.id.initiated_submit_1);
//                                    finalSubmit.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            try {
//                                                updateMorning("rate_craving", craving.getProgress());
//                                                updateMorning("rate_relaxed", relaxed.getProgress());
//                                                updateMorning("rate_sluggish", sluggish.getProgress());
//                                                updateMorning("rate_foggy", fogging.getProgress());
//                                                updateMorning("rate_anxious", anxious.getProgress());
//                                                updateMorning("rate_sad", sad.getProgress());
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                            saveMorning(false);
//
//                                            Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
//
//                                            startMUSE();
//
//                                            finish();
//                                        }
//                                    });
//
//                                    Button cancel = findViewById(R.id.initiated_cancel_1);
//                                    cancel.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            saveMorning(true);
//                                            finish();
//                                        }
//                                    });
//                                }
//                            });
//
//                            Button yes = findViewById(R.id.btn_marijuana_yes);
//                            yes.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    try {
//                                        updateMorning("last_24_mj", true);
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    setContentView(R.layout.activity_mj_survey_marijuana_positive);
//
//                                    final DatePicker lastDay = findViewById(R.id.last_date_mj);
//                                    final TimePicker lastTime = findViewById(R.id.last_time_mj);
//
//                                    final CheckBox joint = findViewById(R.id.check_joint);
//                                    final CheckBox pipe = findViewById(R.id.check_pipe);
//                                    final CheckBox bong = findViewById(R.id.check_bong);
//                                    final CheckBox blunt = findViewById(R.id.check_blunt);
//
//                                    final CheckBox other = findViewById(R.id.check_other);
//                                    final EditText other_desc = findViewById(R.id.check_other_input);
//                                    other_desc.addTextChangedListener(new TextWatcher() {
//                                        @Override
//                                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                                        }
//
//                                        @Override
//                                        public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                            if (s.length() == 0) other.setChecked(false);
//                                            else other.setChecked(true);
//                                        }
//
//                                        @Override
//                                        public void afterTextChanged(Editable s) {
//                                        }
//                                    });
//
//                                    final EditText mj_amount = findViewById(R.id.how_much_mj);
//
//                                    Button initiated_submit_3 = findViewById(R.id.initiated_submit_3);
//                                    initiated_submit_3.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            try {
//                                                updateMorning("last_day_mj", lastDay.getDayOfMonth() + "-" + lastDay.getMonth() + "-" + lastDay.getYear());
//                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                                                    updateMorning("last_time_mj", lastTime.getHour() + ":" + lastTime.getMinute());
//                                                else
//                                                    updateMorning("last_time_mj", lastTime.getCurrentHour() + ":" + lastTime.getCurrentMinute());
//                                                updateMorning("used_joint", joint.isChecked());
//                                                updateMorning("used_pipe", pipe.isChecked());
//                                                updateMorning("used_bong", bong.isChecked());
//                                                updateMorning("used_blunt", blunt.isChecked());
//                                                updateMorning("used_other", other.isChecked());
//                                                updateMorning("other_desc", other_desc.getText().toString());
//                                                updateMorning("used_amount", mj_amount.getText().toString());
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                            setContentView(R.layout.activity_mj_initiated_ratings);
//
//                                            final SeekBar craving = findViewById(R.id.rate_craving);
//                                            final SeekBar relaxed = findViewById(R.id.rate_relaxed);
//                                            final SeekBar sluggish = findViewById(R.id.rate_sluggish);
//                                            final SeekBar fogging = findViewById(R.id.rate_foggy);
//                                            final SeekBar anxious = findViewById(R.id.rate_anxious);
//                                            final SeekBar sad = findViewById(R.id.rate_sad);
//
//                                            Button finalSubmit = findViewById(R.id.initiated_submit_1);
//                                            finalSubmit.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//
//                                                    try {
//                                                        updateMorning("rate_craving", craving.getProgress());
//                                                        updateMorning("rate_relaxed", relaxed.getProgress());
//                                                        updateMorning("rate_sluggish", sluggish.getProgress());
//                                                        updateMorning("rate_foggy", fogging.getProgress());
//                                                        updateMorning("rate_anxious", anxious.getProgress());
//                                                        updateMorning("rate_sad", sad.getProgress());
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    }
//
//                                                    saveMorning(false);
//
//                                                    Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
//
//                                                    startMUSE();
//
//                                                    finish();
//                                                }
//                                            });
//
//                                            Button cancel = findViewById(R.id.initiated_cancel_1);
//                                            cancel.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    saveMorning(true);
//                                                    finish();
//                                                }
//                                            });
//                                        }
//                                    });
//
//                                    Button cancel = findViewById(R.id.initiated_cancel_3);
//                                    cancel.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            saveMorning(true);
//                                            finish();
//                                        }
//                                    });
//                                }
//                            });
//                        }
//                    });
//
//                    Button cancel = findViewById(R.id.cancel);
//                    cancel.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            saveMorning(true);
//                            finish();
//                        }
//                    });
//
//                }
//
//
//
//
//                else if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_MJ_EVENING)) {
//
//                    Log.d(Constants.TAG, "2 if");
//
//
//                    //clear notification
//                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    notificationManager.cancel(Plugin.UPMC_NOTIFICATIONS);
//
//                    evening = new JSONObject();
//
//                    setContentView(R.layout.activity_mj_survey_common_mj_branching);
//
//                    Button no = findViewById(R.id.btn_marijuana_no);
//                    no.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            try {
//                                updateEvening("last_24_mj", false);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            setContentView(R.layout.activity_mj_initiated_ratings);
//
//                            final SeekBar craving = findViewById(R.id.rate_craving);
//                            final SeekBar relaxed = findViewById(R.id.rate_relaxed);
//                            final SeekBar sluggish = findViewById(R.id.rate_sluggish);
//                            final SeekBar fogging = findViewById(R.id.rate_foggy);
//                            final SeekBar anxious = findViewById(R.id.rate_anxious);
//                            final SeekBar sad = findViewById(R.id.rate_sad);
//
//                            Button finalSubmit = findViewById(R.id.initiated_submit_1);
//                            finalSubmit.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    try {
//                                        updateEvening("rate_craving", craving.getProgress());
//                                        updateEvening("rate_relaxed", relaxed.getProgress());
//                                        updateEvening("rate_sluggish", sluggish.getProgress());
//                                        updateEvening("rate_foggy", fogging.getProgress());
//                                        updateEvening("rate_anxious", anxious.getProgress());
//                                        updateEvening("rate_sad", sad.getProgress());
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    saveEvening(false);
//
//                                    Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
//
//                                    startMUSE();
//
//                                    finish();
//                                }
//                            });
//
//                            Button cancel = findViewById(R.id.initiated_cancel_1);
//                            cancel.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    saveEvening(true);
//                                    finish();
//                                }
//                            });
//                        }
//                    });
//
//                    Button yes = findViewById(R.id.btn_marijuana_yes);
//                    yes.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            try {
//                                updateEvening("last_24_mj", true);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            setContentView(R.layout.activity_mj_survey_marijuana_positive);
//
//                            final DatePicker lastDay = findViewById(R.id.last_date_mj);
//                            final TimePicker lastTime = findViewById(R.id.last_time_mj);
//
//                            final CheckBox joint = findViewById(R.id.check_joint);
//                            final CheckBox pipe = findViewById(R.id.check_pipe);
//                            final CheckBox bong = findViewById(R.id.check_bong);
//                            final CheckBox blunt = findViewById(R.id.check_blunt);
//
//                            final CheckBox other = findViewById(R.id.check_other);
//                            final EditText other_desc = findViewById(R.id.check_other_input);
//                            other_desc.addTextChangedListener(new TextWatcher() {
//                                @Override
//                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//                                }
//
//                                @Override
//                                public void onTextChanged(CharSequence s, int start, int before, int count) {
//                                    if (s.length() == 0) other.setChecked(false);
//                                    else other.setChecked(true);
//                                }
//
//                                @Override
//                                public void afterTextChanged(Editable s) {
//                                }
//                            });
//
//                            final EditText mj_amount = findViewById(R.id.how_much_mj);
//
//                            Button initiated_submit_3 = findViewById(R.id.initiated_submit_3);
//                            initiated_submit_3.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//
//                                    try {
//                                        updateEvening("last_day_mj", lastDay.getDayOfMonth() + "-" + lastDay.getMonth() + "-" + lastDay.getYear());
//                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                                            updateEvening("last_time_mj", lastTime.getHour() + ":" + lastTime.getMinute());
//                                        else
//                                            updateEvening("last_time_mj", lastTime.getCurrentHour() + ":" + lastTime.getCurrentMinute());
//                                        updateEvening("used_joint", joint.isChecked());
//                                        updateEvening("used_pipe", pipe.isChecked());
//                                        updateEvening("used_bong", bong.isChecked());
//                                        updateEvening("used_blunt", blunt.isChecked());
//                                        updateEvening("used_other", other.isChecked());
//                                        updateEvening("other_desc", other_desc.getText().toString());
//                                        updateEvening("used_amount", mj_amount.getText().toString());
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    setContentView(R.layout.activity_mj_initiated_ratings);
//
//                                    final SeekBar craving = findViewById(R.id.rate_craving);
//                                    final SeekBar relaxed = findViewById(R.id.rate_relaxed);
//                                    final SeekBar sluggish = findViewById(R.id.rate_sluggish);
//                                    final SeekBar fogging = findViewById(R.id.rate_foggy);
//                                    final SeekBar anxious = findViewById(R.id.rate_anxious);
//                                    final SeekBar sad = findViewById(R.id.rate_sad);
//
//                                    Button initiated_submit_1 = findViewById(R.id.initiated_submit_1);
//                                    initiated_submit_1.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//
//                                            try {
//                                                updateEvening("rate_craving", craving.getProgress());
//                                                updateEvening("rate_relaxed", relaxed.getProgress());
//                                                updateEvening("rate_sluggish", sluggish.getProgress());
//                                                updateEvening("rate_foggy", fogging.getProgress());
//                                                updateEvening("rate_anxious", anxious.getProgress());
//                                                updateEvening("rate_sad", sad.getProgress());
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//
//                                            setContentView(R.layout.activity_mj_survey_evening);
//
//                                            final SeekBar rate_solving_problems = findViewById(R.id.rate_solving_problems);
//                                            final SeekBar rate_remembering = findViewById(R.id.rate_remembering);
//                                            final SeekBar rate_attention = findViewById(R.id.rate_attention);
//                                            final SeekBar rate_concentration = findViewById(R.id.rate_concentrating);
//
//                                            Button finalSubmit = findViewById(R.id.initiated_submit_1);
//                                            finalSubmit.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    try {
//                                                        updateEvening("rate_solving_problems", rate_solving_problems.getProgress());
//                                                        updateEvening("rate_remembering", rate_remembering.getProgress());
//                                                        updateEvening("rate_attention", rate_attention.getProgress());
//                                                        updateEvening("rate_concentration", rate_concentration.getProgress());
//                                                    } catch (JSONException e) {
//                                                        e.printStackTrace();
//                                                    }
//
//                                                    saveEvening(false);
//
//                                                    Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
//
//                                                    startMUSE();
//
//                                                    finish();
//                                                }
//                                            });
//
//                                            Button cancel = findViewById(R.id.cancel);
//                                            cancel.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View v) {
//                                                    saveEvening(true);
//                                                    finish();
//                                                }
//                                            });
//                                        }
//                                    });
//
//                                    Button cancel = findViewById(R.id.initiated_cancel_1);
//                                    cancel.setOnClickListener(new View.OnClickListener() {
//                                        @Override
//                                        public void onClick(View v) {
//                                            saveEvening(true);
//                                            finish();
//                                        }
//                                    });
//                                }
//                            });
//
//                            Button cancel = findViewById(R.id.initiated_cancel_3);
//                            cancel.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    saveEvening(true);
//                                    finish();
//                                }
//                            });
//                        }
//                    });
//                }
//                else if(getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_USER_INIT_END)) {
//                    Log.d(Constants.TAG,  "ACTION_USER_INIT_END");
//
//                } else
//                    {
//
////                     setContentView(R.layout.activity_user_initiated_end_1);
////                     final DatePicker mj_end_date = findViewById(R.id.ui_end_date_mj);
////                     final TimePicker mj_end_time = findViewById(R.id.ui_end_time_mj);
////                     final CheckBox used_joint = findViewById(R.id.ui_mj_usage_c1);
////                     final CheckBox used_pipe = findViewById(R.id.ui_mj_usage_c2);
////                     final CheckBox used_bong = findViewById(R.id.ui_mj_usage_c3);
////                     final CheckBox used_blunt = findViewById(R.id.ui_mj_usage_c4);
////                     final CheckBox used_other_way = findViewById(R.id.ui_check_other_usage);
////                     final EditText other_way_input = findViewById(R.id.ui_check_other_usage_input);
////                        used_other_way.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            used_other_way.setChecked(true);
////                        }
////                    });
////
////                    final EditText how_much_usage_e1 = findViewById(R.id.ui_rate_mj_quantity);
////                    final CheckBox used_alcohol_e1  = findViewById(R.id.ui_end_checkBox1);
////                    final CheckBox used_tobacco_e1 = findViewById(R.id.ui_end_checkBox2);
////                    final CheckBox used_caffeine = findViewById(R.id.ui_end_checkBox3);
////                    final CheckBox used_other_e1 = findViewById(R.id.ui_end_check_other);
////                    final EditText used_other_dec_e1 = findViewById(R.id.ui_end_check_other_input);
////                    used_other_dec_e1.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            used_other_e1.setChecked(true);
////                        }
////                    });
////
////                    final Button end1_submit_button = findViewById(R.id.ui_initiated_submit_3);
////                    end1_submit_button.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View view) {
////
////                            try {
////                                Log.d(Constants.TAG,  mj_end_date.getDayOfMonth() + "-" + mj_end_date.getMonth() + "-" + mj_end_date.getYear());
////                                updateFingerprint("mj_end_date", mj_end_date.getDayOfMonth() + "-" + mj_end_date.getMonth() + "-" + mj_end_date.getYear());
////                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
////                                    updateFingerprint("mj_end_time", mj_end_time.getHour() + ":" + mj_end_time.getMinute());
////                                else
////                                    updateFingerprint("mj_end_time", mj_end_time.getCurrentHour() + ":" + mj_end_time.getCurrentMinute());
////
////                                updateFingerprint("used_joint",used_joint.isChecked());
////                                updateFingerprint("used_pipe",used_pipe.isChecked());
////                                updateFingerprint("used_bong",used_bong.isChecked());
////                                updateFingerprint("used_blunt", used_blunt.isChecked());
////                                if(used_other_way.isChecked()) {
////
////                                    final Button end1_cancel_button = findViewById(R.id.ui_initiated_cancel_3);
////                                    end1_cancel_button.setOnClickListener(new View.OnClickListener() {
////                                        @Override
////                                        public void onClick(View view) {
////                                            try {
////                                                updateFingerprint("used_other_way", other_way_input.getText());
////                                            } catch (JSONException e) {
////                                                e.printStackTrace();
////                                            }
////                                        }
////                                    });
////
////                                }
////                                updateFingerprint("usage_quantity",how_much_usage_e1.getText());
////                                updateFingerprint("used_alcohol", used_alcohol_e1.isChecked());
////                                updateFingerprint("used_tobacco", used_tobacco_e1.isChecked());
////                                updateFingerprint("used_caffeine", used_caffeine.isChecked());
////                                if(used_other_e1.isChecked()) {
////                                    updateFingerprint("used_other_substance", used_other_dec_e1.getText());
////                                }
////                                Log.d(Constants.TAG, "All fingerprints successful");
////                                finish();
////
////                            }
////                            catch (JSONException ex) {
////                                ex.printStackTrace();
////                            }
////                        }
////                    });
////
////
////
//
//
//
//
//
//
//
//
//
//
//                    Log.d(Constants.TAG, "3 if");
//                    //clear notification
//                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//                    notificationManager.cancel(Plugin.UPMC_NOTIFICATIONS);
//                    fingerprint = new JSONObject();
//                    setContentView(R.layout.activity_mj_user_initiated);
//                    final DatePicker mj_start_date = findViewById(R.id.u1_start_date_mj);
//                    final TimePicker mj_start_time = findViewById(R.id.ui_start_time_mj);
//                    final SeekBar rate_how_high = findViewById(R.id.ui_rate_how_high);
//                    final SeekBar rate_mj_craving = findViewById(R.id.ui_rate_craving);
//                    final CheckBox used_alcohol = findViewById(R.id.ui_checkBox1);
//                    final CheckBox used_tobacco = findViewById(R.id.ui_checkBox2);
//                    final CheckBox used_caffeine = findViewById(R.id.ui_checkBox3);
//                    final CheckBox used_other = findViewById(R.id.ui_check_other);
//                    final EditText used_other_dec = findViewById(R.id.ui_check_other_input);
//                    used_other_dec.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            used_other.setChecked(true);
//                        }
//                    });
//                    final RadioGroup socialMJ = findViewById(R.id.ui_social_context);
//
//                    Button initiated_submit_2 = findViewById(R.id.ui_initiated_submit_2);
//                    initiated_submit_2.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                                Log.d(Constants.TAG, "MJ_SURVEY:Ui survey");
//                            try {
//                                updateFingerprint("mj_start_date", mj_start_date.getDayOfMonth() + "-" + mj_start_date.getMonth() + "-" + mj_start_date.getYear());
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                                    updateFingerprint("last_time_mj", mj_start_time.getHour() + ":" + mj_start_time.getMinute());
//                                else
//                                    updateFingerprint("last_time_mj", mj_start_time.getCurrentHour() + ":" + mj_start_time.getCurrentMinute());
//                                updateFingerprint("rate_how_high", rate_how_high.getProgress());
//                                updateFingerprint("rate_mj_craving", rate_mj_craving.getProgress());
//                                updateFingerprint("used_alcohol", used_alcohol.isChecked());
//                                updateFingerprint("used_tobacco", used_tobacco.isChecked());
//                                updateFingerprint("used_caffeine", used_caffeine.isChecked());
//                                updateFingerprint("used_other", used_other.isChecked());
//                                updateFingerprint("used_other_desc", used_other_dec.getText().toString());
//                                if (socialMJ.getCheckedRadioButtonId() != -1)
//                                    updateFingerprint("social_context", findViewById(socialMJ.getCheckedRadioButtonId()).getTag().toString());
//                                else
//                                    updateFingerprint("social_context", "NA");
//
//                                saveFingerprint(false);
//                                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
//                                start3HrAlarm();
//                                finish();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                        }
//                    });
//
//
//
//
//
//
//
////                        @Override
////                        public void onClick(View v) {
////
////                            try {
////                                updateFingerprint("rate_how_high", rate_how_high.getProgress());
////                                updateFingerprint("used_alcohol", used_alcohol.isChecked());
////                                updateFingerprint("used_tobacco", used_tobacco.isChecked());
////                                updateFingerprint("used_caffeine", used_caffeine.isChecked());
////                                updateFingerprint("used_other", used_other.isChecked());
////                                updateFingerprint("used_other_desc", used_other_dec.getText().toString());
////                                if (socialMJ.getCheckedRadioButtonId() != -1)
////                                    updateFingerprint("social_context", findViewById(socialMJ.getCheckedRadioButtonId()).getTag().toString());
////                                else
////                                    updateFingerprint("social_context", "NA");
////                            } catch (JSONException e) {
////                                e.printStackTrace();
////                            }
////
////                            setContentView(R.layout.activity_mj_survey_marijuana_positive);
////
////                            final DatePicker lastDay = findViewById(R.id.last_date_mj);
////                            final TimePicker lastTime = findViewById(R.id.last_time_mj);
////
////                            final CheckBox joint = findViewById(R.id.check_joint);
////                            final CheckBox pipe = findViewById(R.id.check_pipe);
////                            final CheckBox bong = findViewById(R.id.check_bong);
////                            final CheckBox blunt = findViewById(R.id.check_blunt);
////                            final CheckBox other = findViewById(R.id.check_other);
////                            final EditText other_desc = findViewById(R.id.check_other_input);
////                            other_desc.addTextChangedListener(new TextWatcher() {
////                                @Override
////                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
////                                }
////
////                                @Override
////                                public void onTextChanged(CharSequence s, int start, int before, int count) {
////                                    if (s.length() == 0) other.setChecked(false);
////                                    else other.setChecked(true);
////                                }
////
////                                @Override
////                                public void afterTextChanged(Editable s) {
////                                }
////                            });
////
////                            final EditText mj_amount = findViewById(R.id.how_much_mj);
////
////                            Button initiated_submit_3 = findViewById(R.id.initiated_submit_3);
////                            initiated_submit_3.setOnClickListener(new View.OnClickListener() {
////                                @Override
////                                public void onClick(View v) {
////
////                                    try {
////                                        updateFingerprint("last_day_mj", lastDay.getDayOfMonth() + "-" + lastDay.getMonth() + "-" + lastDay.getYear());
////                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
////                                            updateFingerprint("last_time_mj", lastTime.getHour() + ":" + lastTime.getMinute());
////                                        else
////                                            updateFingerprint("last_time_mj", lastTime.getCurrentHour() + ":" + lastTime.getCurrentMinute());
////                                        updateFingerprint("used_joint", joint.isChecked());
////                                        updateFingerprint("used_pipe", pipe.isChecked());
////                                        updateFingerprint("used_bong", bong.isChecked());
////                                        updateFingerprint("used_blunt", blunt.isChecked());
////                                        updateFingerprint("used_other", other.isChecked());
////                                        updateFingerprint("other_desc", other_desc.getText().toString());
////                                        updateFingerprint("used_amount", mj_amount.getText().toString());
////                                    } catch (JSONException e) {
////                                        e.printStackTrace();
////                                    }
////
////                                    setContentView(R.layout.activity_mj_initiated_ratings);
////
////                                    final SeekBar craving = findViewById(R.id.rate_craving);
////                                    final SeekBar relaxed = findViewById(R.id.rate_relaxed);
////                                    final SeekBar sluggish = findViewById(R.id.rate_sluggish);
////                                    final SeekBar fogging = findViewById(R.id.rate_foggy);
////                                    final SeekBar anxious = findViewById(R.id.rate_anxious);
////                                    final SeekBar sad = findViewById(R.id.rate_sad);
////
////                                    Button initiated_submit_1 = findViewById(R.id.initiated_submit_1);
////                                    initiated_submit_1.setOnClickListener(new View.OnClickListener() {
////                                        @Override
////                                        public void onClick(View v) {
////                                            try {
////                                                updateFingerprint("rate_craving", craving.getProgress());
////                                                updateFingerprint("rate_relaxed", relaxed.getProgress());
////                                                updateFingerprint("rate_sluggish", sluggish.getProgress());
////                                                updateFingerprint("rate_foggy", fogging.getProgress());
////                                                updateFingerprint("rate_anxious", anxious.getProgress());
////                                                updateFingerprint("rate_sad", sad.getProgress());
////                                            } catch (JSONException e) {
////                                                e.printStackTrace();
////                                            }
////
////                                            saveFingerprint(false);
////
////                                            Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
////
////                                            startMUSE();
////
////                                            finish();
////                                        }
////                                    });
////
////                                    Button cancel = findViewById(R.id.initiated_cancel_1);
////                                    cancel.setOnClickListener(new View.OnClickListener() {
////                                        @Override
////                                        public void onClick(View v) {
////                                            saveFingerprint(true);
////                                            finish();
////                                        }
////                                    });
////                                }
////                            });
////
////                            Button cancel = findViewById(R.id.initiated_cancel_3);
////                            cancel.setOnClickListener(new View.OnClickListener() {
////                                @Override
////                                public void onClick(View v) {
////                                    saveFingerprint(true);
////                                    finish();
////                                }
////                            });
////                        }
////                    });
////
////                    Button cancel = findViewById(R.id.initiated_cancel_2);
////                    cancel.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            saveFingerprint(true);
////                            finish();
////                        }
////                    });
//                }
//            }
//        }
    //}


    private void start3HrAlarm() {
        Log.d(Constants.TAG, "MJ_survey:start3HrAlarm");
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent alarmIntent_3hr = new Intent(this, AlarmReceiver.class);
        alarmIntent_3hr.putExtra(Constants.ALARM_COMM, 2);
        int interval = 60 * 1000; // change it to 3 hours

//        int interval = 3 * 60 * 60 * 1000; // change it to 3 hours
        PendingIntent alarmPendingIntent_3hr = PendingIntent.getBroadcast(this, 668, alarmIntent_3hr, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, alarmPendingIntent_3hr);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + interval, alarmPendingIntent_3hr);
        }


    }

    public void showUISurvey1() {
        setContentView(R.layout.activity_user_initiated_end_1);
        fingerprint = new JSONObject();
        final DatePicker mj_end_date = findViewById(R.id.ui_end_date_mj);
        final TimePicker mj_end_time = findViewById(R.id.ui_end_time_mj);
        final CheckBox used_joint = findViewById(R.id.ui_mj_usage_c1);
        final CheckBox used_pipe = findViewById(R.id.ui_mj_usage_c2);
        final CheckBox used_bong = findViewById(R.id.ui_mj_usage_c3);
        final CheckBox used_blunt = findViewById(R.id.ui_mj_usage_c4);
        final CheckBox used_other_way = findViewById(R.id.ui_check_other_usage);
        final EditText other_way_input = findViewById(R.id.ui_check_other_usage_input);
        used_other_way.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                used_other_way.setChecked(true);
            }
        });

        final EditText how_much_usage_e1 = findViewById(R.id.ui_rate_mj_quantity);
        final CheckBox used_alcohol_e1  = findViewById(R.id.ui_end_checkBox1);
        final CheckBox used_tobacco_e1 = findViewById(R.id.ui_end_checkBox2);
        final CheckBox used_caffeine = findViewById(R.id.ui_end_checkBox3);
        final CheckBox used_other_e1 = findViewById(R.id.ui_end_check_other);
        final EditText used_other_dec_e1 = findViewById(R.id.ui_end_check_other_input);
        used_other_dec_e1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                used_other_e1.setChecked(true);
            }
        });

        final Button end1_submit_button = findViewById(R.id.ui_initiated_submit_3);
        end1_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    Log.d(Constants.TAG,  mj_end_date.getDayOfMonth() + "-" + mj_end_date.getMonth() + "-" + mj_end_date.getYear());
                    updateFingerprint("mj_end_date", mj_end_date.getDayOfMonth() + "-" + mj_end_date.getMonth() + "-" + mj_end_date.getYear());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                        updateFingerprint("mj_end_time", mj_end_time.getHour() + ":" + mj_end_time.getMinute());
                    else
                        updateFingerprint("mj_end_time", mj_end_time.getCurrentHour() + ":" + mj_end_time.getCurrentMinute());

                    updateFingerprint("used_joint",used_joint.isChecked());
                    updateFingerprint("used_pipe",used_pipe.isChecked());
                    updateFingerprint("used_bong",used_bong.isChecked());
                    updateFingerprint("used_blunt", used_blunt.isChecked());
                    if(used_other_way.isChecked()) {
                        updateFingerprint("used_other_way", other_way_input.getText());

                    }
                    updateFingerprint("usage_quantity",how_much_usage_e1.getText());
                    updateFingerprint("used_alcohol", used_alcohol_e1.isChecked());
                    updateFingerprint("used_tobacco", used_tobacco_e1.isChecked());
                    updateFingerprint("used_caffeine", used_caffeine.isChecked());
                    if(used_other_e1.isChecked()) {
                        updateFingerprint("used_other_substance", used_other_dec_e1.getText());
                    }


                    showUiSurvey2();

                }
                catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        });


        final Button end1_cancel_button = findViewById(R.id.ui_initiated_cancel_3);
        end1_cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFingerprint(false);
                finish();
            }
        });

    }



    public void showUiSurvey2() {
        setContentView(R.layout.activity_user_initiated_end_2);
        final CheckBox used_to_cope = findViewById(R.id.ui_mj_usage_r1);
        final CheckBox used_to_social = findViewById(R.id.ui_mj_usage_r2);
        final CheckBox used_for_other_reason = findViewById(R.id.ui_check_other_usage_r);
        final EditText used_for_other_reason_input = findViewById(R.id.ui_check_other_input_usage_r);
        used_for_other_reason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                used_for_other_reason.setChecked(true);
            }
        });

        final CheckBox loc_home = findViewById(R.id.ui_mj_usage_p1);
        final CheckBox loc_other_home = findViewById(R.id.ui_mj_usage_p2);
        final CheckBox loc_work = findViewById(R.id.ui_mj_usage_p3);
        final CheckBox loc_school = findViewById(R.id.ui_mj_usage_p4);
        final EditText loc_other = findViewById(R.id.ui_check_other_input_usage_p);
        final CheckBox used_other_loc = findViewById(R.id.ui_check_other_usage_p);
        used_other_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                used_other_loc.setChecked(true);
            }
        });

        Button submitbutton = findViewById(R.id.ui_initiated_submit_2r);
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    updateFingerprint("used_to_cope", used_to_cope.isChecked());
                    updateFingerprint("used_to_be_social", used_to_social.isChecked());
                    if(used_for_other_reason.isChecked()) {
                        updateFingerprint("used_other_reason", used_for_other_reason_input.getText());
                    }
                    updateFingerprint("loc_home",  loc_home.isChecked());
                    updateFingerprint("loc_other_home", loc_other_home.isChecked());
                    updateFingerprint("loc_work",  loc_work.isChecked());
                    updateFingerprint("loc_school",  loc_school.isChecked());
                    if(used_other_loc.isChecked())
                        updateFingerprint("loc_other",  loc_other.getText());

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                showUISurvey3();

            }
        });

        Button cancelButton = findViewById(R.id.ui_initiated_cancel_2r);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFingerprint(false);
                finish();

            }
        });

    }


    public void showUISurvey3() {
        setContentView(R.layout.activity_user_initiated_end_3);
        final SeekBar craving = findViewById(R.id.rate_craving_ef);
        final SeekBar relaxed = findViewById(R.id.rate_relaxed_ef);
        final SeekBar sluggish = findViewById(R.id.rate_sluggish_ef);
        final SeekBar fogging = findViewById(R.id.rate_foggy_ef);
        final SeekBar anxious = findViewById(R.id.rate_anxious_ef);
        final SeekBar sad = findViewById(R.id.rate_sad_ef);

        Button finalSubmit = findViewById(R.id.initiated_submit_1_ef);
        finalSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    updateFingerprint("rate_craving", craving.getProgress());
                    updateFingerprint("rate_relaxed", relaxed.getProgress());
                    updateFingerprint("rate_sluggish", sluggish.getProgress());
                    updateFingerprint("rate_foggy", fogging.getProgress());
                    updateFingerprint("rate_anxious", anxious.getProgress());
                    updateFingerprint("rate_sad", sad.getProgress());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();
                Log.d(Constants.TAG, "" + fingerprint.toString());
                saveFingerprint(false);
                startMUSE();
                finish();
            }
        });

        Button cancelSubmit = findViewById(R.id.initiated_cancel_1_ef);
        cancelSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveFingerprint(true);
                finish();
            }
        });


    }

    //Launches the muse integration
    private void startMUSE() {
        Intent muse = new Intent(Intent.ACTION_VIEW);
        muse.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        muse.setData(Uri.parse("fourtwentystudy://"));
        getApplicationContext().startActivity(muse);
    }

    /**
     * Stores the user answers to evening JSON
     *
     * @param key
     * @param value
     * @throws JSONException
     */
    private void updateFingerprint(String key, Object value) throws JSONException {
        fingerprint.put(key, value);
    }

    /**
     * Save users' evening questions to database
     *
     * @param canceled
     */
    private void saveFingerprint(boolean canceled) {
        ContentValues data = new ContentValues();
        data.put(Provider.UPMC_MJ_Data.TIMESTAMP, System.currentTimeMillis());
        data.put(Provider.UPMC_MJ_Data.DEVICE_ID, Aware.getSetting(this, Aware_Preferences.DEVICE_ID));
        data.put(Provider.UPMC_MJ_Data.QUESTION_TYPE, "self-report" + ((canceled) ? "-canceled" : ""));
        data.put(Provider.UPMC_MJ_Data.QUESTION_ANSWERS, fingerprint.toString());

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
