package com.betadevels.autosound.DAOs;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by susindaran.e on 13/09/16.
 */

@Table( name = "sound_trigger_instance", id = BaseColumns._ID )
public class TriggerInstance extends Model
{
    @Column( name = "trigger_id", notNull = true )
    public long triggerId;
}
