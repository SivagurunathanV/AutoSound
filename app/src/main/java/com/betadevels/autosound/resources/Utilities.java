package com.betadevels.autosound.resources;

/**
 * Created by susindaran.e on 18/09/16.
 */
public class Utilities
{
    public static String[] daysOfWeeks = new String[]{ "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY" };

    public static String getDayString( int day, int numberOfCharacters )
    {
        return day < daysOfWeeks.length ? numberOfCharacters <= daysOfWeeks[day].length() ? daysOfWeeks[day].substring( 0, numberOfCharacters) : daysOfWeeks[day] : null;
    }

    public static String attachSuperscriptToNumber( int number )
    {
        String result = Integer.toString( number );
        switch( number < 20 ? number : number % 10 )
        {
            case 1: return result + "st";
            case 2: return result + "nd";
            case 3: return result + "rd";
            default: return result + "th";
        }
    }
}
