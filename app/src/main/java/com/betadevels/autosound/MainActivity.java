package com.betadevels.autosound;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    AudioManager audioManager;
    AlarmManager alarmManager;
    static TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if( fab != null )
        {
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent launchAddNewTriggerIntent = new Intent( getBaseContext(), AddTriggerActivity.class );
                    startActivity( launchAddNewTriggerIntent );
                }
            });
        }

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        textView = (TextView) findViewById(R.id.textView);

        Button setTriggerButton = (Button)findViewById(R.id.set_trigger_btn);

        if( setTriggerButton != null )
        {
            setTriggerButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent( getBaseContext(), AlarmReceiver.class );
                    PendingIntent pendingIntent = PendingIntent.getBroadcast( getBaseContext(), 123321, intent, PendingIntent.FLAG_UPDATE_CURRENT );

                    Calendar cal = Calendar.getInstance();
                    // add 5 minutes to the calendar object
                    cal.add(Calendar.SECOND, 15);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                    Log.i(TAG, "onCreate: Trigger scheduled");
                    Snackbar.make( v, "Trigger scheduled", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    new CountDownTimer( 15000, 1000)
                    {
                        @Override
                        public void onTick(long millisUntilFinished)
                        {
                            textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                        }

                        @Override
                        public void onFinish()
                        {
                            textView.setText("Done!");
                        }
                    }.start();
                }
            });
        }

        Button updateTriggerButton = (Button)findViewById( R.id.update_trigger_btn );
        if( updateTriggerButton != null )
        {
            updateTriggerButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent( getBaseContext(), AlarmReceiver.class );
                    PendingIntent pendingIntent = PendingIntent.getBroadcast( getBaseContext(), 123321, intent, PendingIntent.FLAG_UPDATE_CURRENT );

                    Calendar cal = Calendar.getInstance();
                    // add 5 minutes to the calendar object
                    cal.add(Calendar.SECOND, 30);

                    alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
                    Log.i(TAG, "onCreate: Trigger updated");
                    Snackbar.make( v, "Trigger updated", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    new CountDownTimer( 30000, 1000)
                    {
                        @Override
                        public void onTick(long millisUntilFinished)
                        {
                            textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                        }

                        @Override
                        public void onFinish()
                        {
                            textView.setText("Done!");
                        }
                    }.start();
                }
            });
        }

        Button cancelTriggerButton = (Button) findViewById( R.id.cancel_trigger_btn );
        if( cancelTriggerButton != null )
        {
            cancelTriggerButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent( getBaseContext(), AlarmReceiver.class );
                    PendingIntent pendingIntent = PendingIntent.getBroadcast( getBaseContext(), 123321, intent, PendingIntent.FLAG_CANCEL_CURRENT );

                    alarmManager.cancel( pendingIntent );
                    Log.i(TAG, "onCreate: Trigger cancelled");
                    Snackbar.make( v, "Trigger cancelled", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                    new CountDownTimer( 30000, 1000)
                    {
                        @Override
                        public void onTick(long millisUntilFinished)
                        {
                            textView.setText("seconds remaining: " + millisUntilFinished / 1000);
                        }

                        @Override
                        public void onFinish()
                        {
                            textView.setText("Done!");
                        }
                    }.start();
                }
            });
        }


//        Switch silentSwitch = (Switch) findViewById(R.id.silent_switch);
//        silentSwitch.setChecked( false );
//        silentSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if( isChecked )
//            {
//                Intent intent = new Intent( getBaseContext(), AlarmReceiver.class );
//                PendingIntent pendingIntent = PendingIntent.getBroadcast( getBaseContext(), 0, intent, 0 );
//
//                Calendar cal = Calendar.getInstance();
//                // add 5 minutes to the calendar object
//                cal.add(Calendar.SECOND, 15);
//
//                alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);
//                Snackbar.make( buttonView, "Alarm scheduled", Snackbar.LENGTH_LONG).setAction("Action", null).show();
//            }
//                else
//                {
//                    audioManager.setRingerMode( AudioManager.RINGER_MODE_NORMAL );
//                }
//            Log.i(TAG, "onCheckedChanged: " + isChecked);
//        });
//
//        Switch muteMediaSwitch = (Switch) findViewById(R.id.mute_media_switch);
//        muteMediaSwitch.setChecked( false );
//        muteMediaSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//            {
//                if( isChecked )
//                {
//                    audioManager.setStreamVolume( AudioManager.STREAM_MUSIC, -100, AudioManager.FLAG_SHOW_UI);
//                }
//                else
//                {
//                    audioManager.setStreamVolume( AudioManager.STREAM_MUSIC, 100, AudioManager.FLAG_SHOW_UI);
//                }
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
