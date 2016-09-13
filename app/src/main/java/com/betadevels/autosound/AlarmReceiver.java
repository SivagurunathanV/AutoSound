package com.betadevels.autosound;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

/**
 * Created by susindaran.e on 31/08/16.
 */
public class AlarmReceiver extends BroadcastReceiver
{
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "onCheckedChanged: Receiver start");
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

        //Issuing notification
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder( context )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("AutoSound triggered")
                .setContentText("Ringer mode set to Silent")
                .setAutoCancel(true);

        notificationManager.notify( 1010, notificationBuilder.build() );

        Log.i(TAG, "onCheckedChanged: Receiver end");
    }

}
