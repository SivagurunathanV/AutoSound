package com.betadevels.autosound.DAOs;

import com.betadevels.autosound.models.TriggerInstance;

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
}
