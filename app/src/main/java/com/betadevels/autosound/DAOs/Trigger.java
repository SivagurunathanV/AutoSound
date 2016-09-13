package com.betadevels.autosound.DAOs;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.betadevels.autosound.resources.Constants;

import java.util.Date;

/**
 * Created by susindaran.e on 13/09/16.
 */

@Table( name = "trigger", id = BaseColumns._ID)
public class Trigger extends Model
{
    @Column( name = "is_repeat", notNull = true)
    public boolean isRepeat;

    @Column( name = "days_of_week", notNull = false)
    public String daysOfWeek;

    @Column( name = "trigger_date", notNull = false)
    public Date triggerDate;

    @Column( name = "ringer_mode", notNull = true)
    public Constants.RingerMode ringerMode = Constants.RingerMode.NORMAL;

    @Column( name = "ringer_volume", notNull = false)
    public int ringerVolume;

    @Column( name = "media_volume", notNull = true)
    public int mediaVolume;

    @Column( name = "alarm_volume", notNull = true)
    public int alarmVolume;
}
