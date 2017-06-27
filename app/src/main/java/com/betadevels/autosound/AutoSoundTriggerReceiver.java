package com.betadevels.autosound;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.betadevels.autosound.resources.Constants;
import com.betadevels.autosound.resources.Utilities;

public class AutoSoundTriggerReceiver extends BroadcastReceiver
{
    private static final String TAG = "AutoSoundTriggReceiver";
    private AudioManager audioManager;
    private NotificationManager notificationManager;
    private Constants.RingerMode ringerMode;
    private int ringerVolume;
    private int mediaVolume;
    private int alarmVolume;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(TAG, "onCheckedChanged: Receiver start");

        ringerMode = Constants.RingerMode.valueOf( intent.getStringExtra( Constants.RINGER_MODE_BUNDLE_NAME ) );
        int[] volumes = intent.getIntArrayExtra(Constants.VOLUMES_BUNDLE_NAME);

        Log.i(TAG, "onReceive: Volumes : " + volumes[0] + ", " + volumes[1] + ", " + volumes[2]);

        if( audioManager == null )
        {
            audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        }

        if( notificationManager == null )
        {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        audioManager.setRingerMode( ringerMode.getValue() );

        ringerVolume = Utilities.percentOf( audioManager.getStreamMaxVolume( AudioManager.STREAM_RING ), volumes[0] );
        int notificationVolume = Utilities.percentOf(audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION), volumes[0]);
        mediaVolume = Utilities.percentOf( audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC ), volumes[1] );
        alarmVolume = Utilities.percentOf( audioManager.getStreamMaxVolume( AudioManager.STREAM_ALARM ), volumes[2] );

        if( ringerMode == Constants.RingerMode.Normal )
        {
            audioManager.setStreamVolume( AudioManager.STREAM_RING, ringerVolume, AudioManager.FLAG_VIBRATE );
            audioManager.setStreamVolume( AudioManager.STREAM_NOTIFICATION, notificationVolume, AudioManager.FLAG_VIBRATE );
        }

        audioManager.setStreamVolume( AudioManager.STREAM_MUSIC, mediaVolume, AudioManager.FLAG_VIBRATE );
        audioManager.setStreamVolume( AudioManager.STREAM_ALARM, alarmVolume, AudioManager.FLAG_VIBRATE );

        NotificationCompat.Builder notificationBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder( context )
                .setSmallIcon( R.drawable.ic_notification )
                .setContentTitle( "AutoSound Triggered" )
                .setContentText( "Ringer mode set to " + ringerMode.toString() )
                .setAutoCancel(true);

        notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
        notificationBuilder.setCustomBigContentView( setupCustomView( context ) );

        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify( 1010, notification );

        Log.i(TAG, "onCheckedChanged: Receiver end");
    }

    public RemoteViews setupCustomView( Context context )
    {
        RemoteViews remoteViews = new RemoteViews( context.getPackageName(), R.layout.notification);

        //Setting title and content of notification's CustomView
        remoteViews.setTextViewText( R.id.notification_title_txt, "AutoSound Triggered" );
        remoteViews.setTextViewText( R.id.notification_content_txt, "Ringer mode set to " + ringerMode.toString() );

        //Setting volume ProgressBars
        if( ringerMode != Constants.RingerMode.Normal )
        {
            remoteViews.setViewVisibility( R.id.ringer_volume_tv, View.GONE );
            remoteViews.setViewVisibility( R.id.ringer_volume_pbar, View.GONE );
        }
        else
        {
            remoteViews.setProgressBar( R.id.ringer_volume_pbar, audioManager.getStreamMaxVolume( AudioManager.STREAM_RING ),
                    ringerVolume, false);
        }

        remoteViews.setProgressBar( R.id.media_volume_pbar, audioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC ),
                mediaVolume, false);
        remoteViews.setProgressBar( R.id.alarm_volume_pbar, audioManager.getStreamMaxVolume( AudioManager.STREAM_ALARM ),
                alarmVolume, false);

        return remoteViews;
    }

}
