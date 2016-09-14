package com.betadevels.autosound.models;

import com.activeandroid.query.Select;
import com.betadevels.autosound.DAOs.Trigger;

import java.util.List;

/**
 * Created by susindaran.e on 13/09/16.
 */

public class TriggerDAO
{
    public static List<Trigger> getAllTriggers()
    {
        return new Select().from( Trigger.class ).execute();
    }
}
