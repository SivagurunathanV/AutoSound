package com.betadevels.autosound.DAOs;

import com.activeandroid.query.Select;
import com.betadevels.autosound.models.Trigger;

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

    public static void delete( long triggerId )
    {
        Trigger.delete( Trigger.class, triggerId );
    }
}
