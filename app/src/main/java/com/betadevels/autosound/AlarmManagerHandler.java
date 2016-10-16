package com.betadevels.autosound;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.betadevels.autosound.resources.Constants;

import java.util.Calendar;

/**
 * Created by susindaran.e on 16/10/16.
 */

public class AlarmManagerHandler
{
    private AlarmManager alarmManager;
    private Context baseContext;

    public AlarmManagerHandler( AlarmManager alarmManager, Context baseContext )
    {
        this.alarmManager = alarmManager;
        this.baseContext = baseContext;
    }

    public void setAlarm(long requestCode, boolean isRepeat, Calendar calendar, String ringerMode, int ringerVolume, int mediaVolume,
                                int alarmVolume)
    {
        Intent intent = new Intent( baseContext, AlarmReceiver.class );
        intent.putExtra( Constants.RINGER_MODE_BUNDLE_NAME, ringerMode );
        intent.putExtra( Constants.VOLUMES_BUNDLE_NAME, new int[]{ ringerVolume, mediaVolume, alarmVolume } );

        PendingIntent pendingIntent = PendingIntent.getBroadcast( baseContext, (int) requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT );

        if( isRepeat )
        {
            alarmManager.setRepeating( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent );
        }
        else
        {
            alarmManager.set( AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent );
        }
    }

    public void cancelAlarm( int requestCode )
    {
        Intent intent = new Intent( baseContext, AlarmReceiver.class );
        PendingIntent pendingIntent = PendingIntent.getBroadcast( baseContext, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT );
        alarmManager.cancel( pendingIntent );
    }

}
