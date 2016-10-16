package com.betadevels.autosound;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.betadevels.autosound.resources.Constants;
import com.betadevels.autosound.resources.Utilities;

/**
 * Created by susindaran.e on 31/08/16.
 */
public class AlarmReceiver extends BroadcastReceiver
{
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        //TODO: Get settings from intent's bundle extras
        Log.i(TAG, "onCheckedChanged: Receiver start");

        Constants.RingerMode ringerMode = Constants.RingerMode.valueOf( intent.getStringExtra( Constants.RINGER_MODE_BUNDLE_NAME ) );
        int[] volumes = intent.getIntArrayExtra(Constants.VOLUMES_BUNDLE_NAME);

        Log.i(TAG, "onReceive: Volumes : " + volumes[0] + ", " + volumes[1] + ", " + volumes[2]);

        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode( ringerMode.getValue() );


        if( ringerMode == Constants.RingerMode.Normal )
        {
            audioManager.setStreamVolume( AudioManager.STREAM_RING,
                    Utilities.percentOf( audioManager.getStreamMaxVolume( AudioManager.STREAM_RING ), volumes[0] ),
                    AudioManager.FLAG_SHOW_UI );
            audioManager.setStreamVolume( AudioManager.STREAM_NOTIFICATION,
                    Utilities.percentOf( audioManager.getStreamMaxVolume( AudioManager.STREAM_NOTIFICATION ), volumes[0] ),
                    AudioManager.FLAG_SHOW_UI );
        }

        audioManager.setStreamVolume( AudioManager.STREAM_MUSIC,
                Utilities.percentOf( audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC ), volumes[1] ),
                AudioManager.FLAG_SHOW_UI );
        audioManager.setStreamVolume( AudioManager.STREAM_ALARM,
                Utilities.percentOf( audioManager.getStreamMaxVolume( AudioManager.STREAM_ALARM ), volumes[2] ),
                AudioManager.FLAG_SHOW_UI );

        //Issuing notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder( context )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("AutoSound triggered")
                .setContentText("Ringer mode set to " + ringerMode.toString())
                .setAutoCancel(true);

        notificationManager.notify( 1010, notificationBuilder.build() );

        Log.i(TAG, "onCheckedChanged: Receiver end");
    }

}
