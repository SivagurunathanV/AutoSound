package com.betadevels.autosound.DAOs;

import com.activeandroid.query.Select;
import com.betadevels.autosound.models.TriggerInstance;

import java.util.List;

/**
 * Created by susindaran.e on 16/10/16.
 */

public class TriggerInstanceDAO
{
    public static TriggerInstance create( long triggerId )
    {
        TriggerInstance triggerInstance = new TriggerInstance();
        triggerInstance.triggerId = triggerId;

        triggerInstance.save();

        return triggerInstance;
    }

    public static List<TriggerInstance> getAllTriggerInstances( long triggerId )
    {
        return new Select().from( TriggerInstance.class ).where( "trigger_id = ?", triggerId ).execute();
    }

    public static void delete( long id )
    {
        TriggerInstance.delete( TriggerInstance.class, id );
    }
}
