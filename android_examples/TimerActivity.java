package com.fletchapps.simpletimer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class TimerActivity extends AppCompatActivity implements
        TimerTaskDialogFragment.OnSelectedTimerTask, CustomEntryDialogFragment.OnCompleteEntryListener {

    // String related member variables
    private static final String TAG = "TimerActivity";
    private static final String CUSTOM_DIALOG_TAG = "CustomEntryDialogFragment";
    private static final String TIMER_TASK_DIALOG_TAG = "TimerTaskDialogFragment";

    // Final variables
    private static final int NOTIFICATION_ID = 14;

    // UI related member variables
    private Spinner hoursInput, minutesInput, secondsInput;
    private TextView timeDisplay, titleDisplay;
    private CountDownTimer timer;
    private Button clearButton;
    private ProgressBar progressBar;
    private ImageButton playPauseButton;

    // DrawerLayout related member variables
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private String mActivityTitle;
    private ListView mDrawerList;

    // information related member variables
    private int hours, minutes, seconds;
    private boolean countdownStarted = false;
    private java.text.NumberFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitleTextAppearance(getApplicationContext(), R.style.PeppermintTheme_ToolbarText);
        setSupportActionBar(toolbar);

        mDrawerList = (ListView)findViewById(R.id.navList);
        addDrawerItems();

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch(position) {
                    case 0: // Settings
                        intent = new Intent();
                        intent.setClassName(getApplicationContext(), "com.fletchapps.simpletimer.SettingsActivity");
                        startActivity(intent);
                        break;
                    case 1: // CreditsActivity
                        intent = new Intent(getApplicationContext(), CreditsActivity.class);
                        startActivity(intent);
                        break;
                    default: break;
                }
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        timeDisplay = (TextView) findViewById(R.id.time_display);

        titleDisplay = (TextView) findViewById(R.id.timer_title);
        if(getIntent().getStringExtra("title") != null) {
            titleDisplay.setText(getIntent().getStringExtra("title"));
        }

        playPauseButton = (ImageButton) findViewById(R.id.play_pause_button);
        clearButton = (Button) findViewById(R.id.clear_button);
        playPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!countdownStarted) {
                    countdown();
                } else {
                    pauseCountdown();
                }
            }
        });
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearCountdown();
                clearSpinners();
            }
        });

        hoursInput = (Spinner) findViewById(R.id.spinner_hours);
        minutesInput = (Spinner) findViewById(R.id.spinner_minutes);
        secondsInput = (Spinner) findViewById(R.id.spinner_seconds);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        timeFormat = java.text.NumberFormat.getNumberInstance();
        timeFormat.setMinimumIntegerDigits(2);
        timeFormat.setMaximumIntegerDigits(2);

        hoursInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeString = timeDisplay.getText().toString(); // 00:00:00

                // Format just the hours portion of the time
                int hoursInt = Integer.parseInt(parent.getItemAtPosition(position).toString());
                String hoursString = timeFormat.format(hoursInt);
                // Put the string back together and update the TimeView
                StringBuilder stringBuffer = new StringBuilder(timeString);
                String newTimeString = stringBuffer.replace(0,2, hoursString).toString();
                timeDisplay.setText(newTimeString);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        minutesInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeString = timeDisplay.getText().toString(); // 00:00:00

                // Format just the hours portion of the time
                int minutesInt = Integer.parseInt(parent.getItemAtPosition(position).toString());
                String minutesString = timeFormat.format(minutesInt);
                // Put the string back together and update the TimeView
                StringBuilder stringBuffer = new StringBuilder(timeString);
                String newTimeString = stringBuffer.replace(3,5, minutesString).toString();
                timeDisplay.setText(newTimeString);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        secondsInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String timeString = timeDisplay.getText().toString(); // 00:00:00

                // Format just the hours portion of the time
                int secondsInt = Integer.parseInt(parent.getItemAtPosition(position).toString());
                String secondsString = timeFormat.format(secondsInt);
                // Put the string back together and update the TimeView
                StringBuilder stringBuffer = new StringBuilder(timeString);
                String newTimeString = stringBuffer.replace(6, 8, secondsString).toString();
                timeDisplay.setText(newTimeString);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch(item.getItemId()) {
            case R.id.open_timer_task_dialog:
                openTimerTaskDialog();
                break;
            default: break;
        }

        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onSelect(TimerTask timerTask) {
        titleDisplay.setText(timerTask.getTitle());
        timeDisplay.setText(timerTask.getTask());
        countdown();
    }

    @Override
    public void onComplete(ArrayList<TimerTask> entries) {
        TimerTaskDialogFragment prev = (TimerTaskDialogFragment) getSupportFragmentManager().findFragmentByTag(TIMER_TASK_DIALOG_TAG);
        if( prev != null ) {
            prev.setData(entries);
        }
    }

    private void countdown() {
        if(timer != null) {
            timer.cancel();
        }

        getCurrentTime();
        if( hours == 0 && minutes == 0 && seconds == 0 ) {
            return;
        }
        final long millisTime = (hours * 60 * 60 + minutes * 60 + seconds) * 1000;

        progressBar.setMax((int) (millisTime / 1000));
        disableInputs();

        timer = new CountDownTimer(millisTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                decrementCounter();
                progressBar.setProgress((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                decrementCounter();
                getTotalTime();
                setTimeDisplay(timeFormat.format(hours) + ":" + timeFormat.format(minutes) + ":" + timeFormat.format(seconds));
                progressBar.setProgress(0);
                enableInputs();
                playPauseButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_circle_outline_black_425dp));
                countdownStarted = false;
                createNotification();
            }
        }.start();

        playPauseButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pause_circle_outline_black_425dp));
        countdownStarted = true;
    }

    private void pauseCountdown() {
        if(timer != null) {
            timer.cancel();
            enableInputs();
            playPauseButton.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_play_circle_outline_black_425dp));
            countdownStarted = false;
        }
    }

    private void clearCountdown() {
        titleDisplay.setText(R.string.app_name);
        setTimeDisplay("00:00:00");
        hours = 0;
        minutes = 0;
        seconds = 0;
        progressBar.setProgress(0);
    }

    private void decrementCounter() {
        getCurrentTime();

        if(seconds == 0 && minutes == 0 & hours != 0) {
            minutes = 60;
            hours--;
        }
        if(seconds == 0 && minutes != 0) {
            seconds = 60;
            minutes--;
        }
        if(seconds > 0) {
            seconds--;
        }

        setTimeDisplay(timeFormat.format(hours) + ":" + timeFormat.format(minutes) + ":" + timeFormat.format(seconds));
    }

    private void setTimeDisplay(String newtime) {
        timeDisplay.setText(newtime);
    }

    private void getCurrentTime() {
        String currentTime = timeDisplay.getText().toString();
        hours = Integer.parseInt(currentTime.substring(0, 2));
        minutes = Integer.parseInt(currentTime.substring(3, 5));
        seconds = Integer.parseInt(currentTime.substring(6, 8));
    }

    private void getTotalTime() {
        hours = Integer.parseInt(hoursInput.getSelectedItem().toString());
        minutes = Integer.parseInt(minutesInput.getSelectedItem().toString());
        seconds = Integer.parseInt(secondsInput.getSelectedItem().toString());
    }

    private void addDrawerItems() {
         String[] navArray = { "Settings", "Credits" };
         ArrayAdapter<String> mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, navArray);
         mDrawerList.setAdapter(mAdapter);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open_drawer, R.string.close_drawer) {

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                setTitle("Navigation");
                invalidateOptionsMenu();
            }

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitle(mActivityTitle);
                invalidateOptionsMenu();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    private void openTimerTaskDialog() {
        new TimerTaskDialogFragment().show(getSupportFragmentManager(), TIMER_TASK_DIALOG_TAG);
    }

    public void openCustomEntryDialog(ArrayList<TimerTask> entries) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(TIMER_TASK_DIALOG_TAG);
        if( prev != null ) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList("entries", entries);

        CustomEntryDialogFragment customEntryDialogFragment = new CustomEntryDialogFragment();
        customEntryDialogFragment.setArguments(bundle);
        customEntryDialogFragment.show(ft, CUSTOM_DIALOG_TAG);
    }

    private void enableInputs() {
        clearButton.setEnabled(true);
        hoursInput.setEnabled(true);
        minutesInput.setEnabled(true);
        secondsInput.setEnabled(true);
    }

    private void disableInputs() {
        clearButton.setEnabled(false);
        hoursInput.setEnabled(false);
        minutesInput.setEnabled(false);
        secondsInput.setEnabled(false);
    }

    private void clearSpinners() {
        hoursInput.setSelection(0);
        minutesInput.setSelection(0);
        secondsInput.setSelection(0);
    }

    private void createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.timer_done)
                .setContentTitle(getText(R.string.app_name))
                .setContentText(titleDisplay.getText().toString() + " done!")
                .setSound(Uri.parse("android.resource://" + getPackageName() + "/raw/default_sound"), AudioManager.STREAM_NOTIFICATION)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        Intent resultIntent = new Intent(getApplicationContext(), TimerActivity.class);
        resultIntent.putExtra("title", titleDisplay.getText().toString());

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
        stackBuilder.addParentStack(TimerActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent pendingResultIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingResultIntent);

        builder.setAutoCancel(true);

        NotificationManager notificationManagerCompat = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }
}
