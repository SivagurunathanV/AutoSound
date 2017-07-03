package com.betadevels.autosound.DAOs;

import android.widget.CheckBox;

import com.activeandroid.query.Select;
import com.betadevels.autosound.models.Trigger;
import com.betadevels.autosound.resources.Constants;

import org.joda.time.LocalDateTime;

import java.util.List;

public class TriggerDAO
{
    public static Trigger create(boolean isRepeat, CheckBox[] daysOfWeekCheckBoxes, int year, int month, int day,
                                 int hour, int minute, String ringerMode, int ringerVolume, int mediaVolume,
                                 int alarmVolume)
    {
        Trigger trigger = new Trigger();
        trigger.isRepeat = isRepeat;

        if( isRepeat )
        {
            StringBuilder stringBuilder = new StringBuilder();
            for( CheckBox checkBox : daysOfWeekCheckBoxes )
            {
                stringBuilder.append( checkBox.isChecked() ? "1" : "0" );
            }
            trigger.daysOfWeek = stringBuilder.toString();
            trigger.triggerTime = hour + ":" + minute;
        }
        else
        {
            trigger.triggerDateTime = new LocalDateTime( year, month + 1, day, hour, minute).toDate();
        }
        trigger.ringerMode = Constants.RingerMode.valueOf( ringerMode );
        trigger.ringerVolume = ringerVolume;
        trigger.mediaVolume = mediaVolume;
        trigger.alarmVolume = alarmVolume;

        trigger.save();

        return trigger;
    }

    public static List<Trigger> getAllTriggers()
    {
        return new Select().from( Trigger.class ).execute();
    }

    public static void delete( long triggerId )
    {
        Trigger.delete( Trigger.class, triggerId );
    }
}
