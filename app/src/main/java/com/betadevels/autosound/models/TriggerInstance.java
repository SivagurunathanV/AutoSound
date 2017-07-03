package com.betadevels.autosound.models;

import android.provider.BaseColumns;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

@Table( name = "sound_trigger_instance", id = BaseColumns._ID )
public class TriggerInstance extends Model
{
    @Column( name = "trigger_id", notNull = true )
    public long triggerId;
}
