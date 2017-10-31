package com.aware.plugin.upmc.mj.activities;

import android.Manifest;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
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
import com.aware.utils.Scheduler;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!Aware.IS_CORE_RUNNING) {
            Intent aware = new Intent(this, Aware.class);
            startService(aware);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        ArrayList<String> REQUIRED_PERMISSIONS = new ArrayList<>();
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.ACCESS_FINE_LOCATION);
        REQUIRED_PERMISSIONS.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_CALL_LOG);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_CONTACTS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_SMS);
        REQUIRED_PERMISSIONS.add(Manifest.permission.READ_PHONE_STATE);
        REQUIRED_PERMISSIONS.add(Manifest.permission.RECORD_AUDIO);

        boolean permissions_ok = true;
        for (String p : REQUIRED_PERMISSIONS) {
            if (PermissionChecker.checkSelfPermission(this, p) != PermissionChecker.PERMISSION_GRANTED) {
                permissions_ok = false;
                break;
            }
        }

        if (permissions_ok) {

            if (!Aware.isStudy(this)) {

                Aware.joinStudy(this, "https://r2d2.hcii.cs.cmu.edu/aware/dashboard/index.php/webservice/index/108/z4Q4nINGkqq8");

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
                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.activity_recognition");

                Aware.setSetting(getApplicationContext(), com.aware.plugin.device_usage.Settings.STATUS_PLUGIN_DEVICE_USAGE, true);
                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.device_usage");

                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.STATUS_GOOGLE_FUSED_LOCATION, true);
                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.FREQUENCY_GOOGLE_FUSED_LOCATION, 300);
                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.MAX_FREQUENCY_GOOGLE_FUSED_LOCATION, 300);
                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.ACCURACY_GOOGLE_FUSED_LOCATION, 102);
                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.FALLBACK_LOCATION_TIMEOUT, 20);
                Aware.setSetting(getApplicationContext(), com.aware.plugin.google.fused_location.Settings.LOCATION_SENSITIVITY, 5);
                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.google.fused_location");

                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.studentlife.audio_final");
                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.fitbit");

                //last one to be started is the app itself (which is on itself a plugin too)
                Aware.startPlugin(getApplicationContext(), "com.aware.plugin.upmc.mj");

                //Ask accessibility to be activated
                Applications.isAccessibilityServiceActive(getApplicationContext());

                //Ask doze to be disabled
                Aware.isBatteryOptimizationIgnored(getApplicationContext(), getPackageName());

            } else {
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

                if (Scheduler.getSchedule(this, Plugin.SCHEDULE_FINGERPRING_MJ) == null) {
                    try {
                        Scheduler.Schedule afternoon = new Scheduler.Schedule(Plugin.SCHEDULE_FINGERPRING_MJ)
                                .addHour(15)
                                .addMinute(0)
                                .setActionType(Scheduler.ACTION_TYPE_SERVICE)
                                .setActionIntentAction(Plugin.ACTION_MJ_FINGERPRINT)
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

                if (getIntent() != null && getIntent().getAction() != null && getIntent().getAction().equalsIgnoreCase(Plugin.ACTION_MJ_MORNING)) {

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
                } else {

                    //clear notification
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(Plugin.UPMC_NOTIFICATIONS);

                    fingerprint = new JSONObject();

                    setContentView(R.layout.activity_mj_initiated_last);

                    final SeekBar rate_how_high = findViewById(R.id.rate_how_high);
                    final CheckBox used_alcohol = findViewById(R.id.checkBox1);
                    final CheckBox used_tobacco = findViewById(R.id.checkBox2);
                    final CheckBox used_caffeine = findViewById(R.id.checkBox3);
                    final CheckBox used_other = findViewById(R.id.check_other);
                    final EditText used_other_dec = findViewById(R.id.check_other_input);
                    used_other_dec.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            used_other.setChecked(true);
                        }
                    });
                    final RadioGroup socialMJ = findViewById(R.id.social_context);

                    Button initiated_submit_2 = findViewById(R.id.initiated_submit_2);
                    initiated_submit_2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                updateFingerprint("rate_how_high", rate_how_high.getProgress());
                                updateFingerprint("used_alcohol", used_alcohol.isChecked());
                                updateFingerprint("used_tobacco", used_tobacco.isChecked());
                                updateFingerprint("used_caffeine", used_caffeine.isChecked());
                                updateFingerprint("used_other", used_other.isChecked());
                                updateFingerprint("used_other_desc", used_other_dec.getText().toString());
                                if (socialMJ.getCheckedRadioButtonId() != -1)
                                    updateFingerprint("social_context", findViewById(socialMJ.getCheckedRadioButtonId()).getTag().toString());
                                else
                                    updateFingerprint("social_context", "NA");
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
                                        updateFingerprint("last_day_mj", lastDay.getDayOfMonth() + "-" + lastDay.getMonth() + "-" + lastDay.getYear());
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                            updateFingerprint("last_time_mj", lastTime.getHour() + ":" + lastTime.getMinute());
                                        else
                                            updateFingerprint("last_time_mj", lastTime.getCurrentHour() + ":" + lastTime.getCurrentMinute());
                                        updateFingerprint("used_joint", joint.isChecked());
                                        updateFingerprint("used_pipe", pipe.isChecked());
                                        updateFingerprint("used_bong", bong.isChecked());
                                        updateFingerprint("used_blunt", blunt.isChecked());
                                        updateFingerprint("used_other", other.isChecked());
                                        updateFingerprint("other_desc", other_desc.getText().toString());
                                        updateFingerprint("used_amount", mj_amount.getText().toString());
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
                                                updateFingerprint("rate_craving", craving.getProgress());
                                                updateFingerprint("rate_relaxed", relaxed.getProgress());
                                                updateFingerprint("rate_sluggish", sluggish.getProgress());
                                                updateFingerprint("rate_foggy", fogging.getProgress());
                                                updateFingerprint("rate_anxious", anxious.getProgress());
                                                updateFingerprint("rate_sad", sad.getProgress());
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            saveFingerprint(false);

                                            Toast.makeText(getApplicationContext(), "Thanks!", Toast.LENGTH_LONG).show();

                                            startMUSE();

                                            finish();
                                        }
                                    });

                                    Button cancel = findViewById(R.id.initiated_cancel_1);
                                    cancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            saveFingerprint(true);
                                            finish();
                                        }
                                    });
                                }
                            });

                            Button cancel = findViewById(R.id.initiated_cancel_3);
                            cancel.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    saveFingerprint(true);
                                    finish();
                                }
                            });
                        }
                    });

                    Button cancel = findViewById(R.id.initiated_cancel_2);
                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveFingerprint(true);
                            finish();
                        }
                    });
                }
            }
        } else {
            Intent permissions = new Intent(this, PermissionsHandler.class);
            permissions.putExtra(PermissionsHandler.EXTRA_REQUIRED_PERMISSIONS, REQUIRED_PERMISSIONS);
            permissions.putExtra(PermissionsHandler.EXTRA_REDIRECT_ACTIVITY, getPackageName() + "/" + getClass().getName());
            permissions.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(permissions);
        }
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
