package com.betadevels.autosound.resources;

/**
 * Created by susindaran.e on 13/09/16.
 */
public class Constants
{
    public enum RingerMode
    {
        Normal(2), Vibrate(1), Silent(0);
        private final int value;

        RingerMode( int value )
        {
            this.value = value;
        }

        public int getValue()
        {
            return value;
        }
    }

    public static final int ADD_TRIGGER_ACTIVITY_RC = 1111;

    public static final String RINGER_MODE_BUNDLE_NAME = "RINGER_MODE";
    public static final String VOLUMES_BUNDLE_NAME = "VOLUMES";
}
