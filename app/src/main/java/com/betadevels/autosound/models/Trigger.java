package com.betadevels.autosound.models;

import android.provider.BaseColumns;
import android.widget.CheckBox;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.betadevels.autosound.utils.Constants;

import org.joda.time.LocalDateTime;

import java.util.Date;

@Table( name = "sound_trigger", id = BaseColumns._ID)
public class Trigger extends Model
{
    @Column( name = "is_repeat", notNull = true)
    public boolean isRepeat;

    @Column( name = "days_of_week", notNull = false)
    public String daysOfWeek;

    @Column( name = "trigger_time", notNull = false)
    public String triggerTime;

    @Column( name = "trigger_date_time", notNull = false)
    public Date triggerDateTime;

    @Column( name = "ringer_mode", notNull = true)
    public Constants.RingerMode ringerMode = Constants.RingerMode.Normal;

    @Column( name = "ringer_volume", notNull = false)
    public int ringerVolume;

    @Column( name = "media_volume", notNull = true)
    public int mediaVolume;

    @Column( name = "alarm_volume", notNull = true)
    public int alarmVolume;

    public Trigger() { }

    public Trigger( boolean isRepeat, CheckBox[] daysOfWeekCheckBoxes, int year, int month, int day,
                    int hour, int minute, String ringerMode, int ringerVolume, int mediaVolume,
                    int alarmVolume)
    {
        this.isRepeat = isRepeat;

        if( isRepeat )
        {
            StringBuilder stringBuilder = new StringBuilder();
            for( CheckBox checkBox : daysOfWeekCheckBoxes )
            {
                stringBuilder.append( checkBox.isChecked() ? "1" : "0" );
            }
            this.daysOfWeek = stringBuilder.toString();
            this.triggerTime = hour + ":" + minute;
        }
        else
        {
            this.triggerDateTime = new LocalDateTime( year, month, day, hour, minute).toDate();
        }
        this.ringerMode = Constants.RingerMode.valueOf( ringerMode );
        this.ringerVolume = ringerVolume;
        this.mediaVolume = mediaVolume;
        this.alarmVolume = alarmVolume;
    }
}
