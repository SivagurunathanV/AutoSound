package com.betadevels.autosound.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.betadevels.autosound.models.Trigger;
import com.betadevels.autosound.R;
import com.betadevels.autosound.delegates.SwitcherDelegate;
import com.betadevels.autosound.resources.Constants;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by susindaran.e on 14/09/16.
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter
{
    private Context context;
    private SwitcherDelegate switcherDelegate;
    private List<Trigger> triggers;

    public CustomExpandableListAdapter( Context context, SwitcherDelegate switcherDelegate ,List<Trigger> triggers )
    {
        this.context = context;
        this.switcherDelegate = switcherDelegate;
        this.triggers = triggers;
    }

    public void update( List<Trigger> triggers )
    {
        this.triggers = triggers;
        notifyDataSetChanged();
    }

    @Override
    public int getGroupCount()
    {
        return triggers.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return triggers.get( groupPosition ).ringerMode == Constants.RingerMode.Normal ? 3 : 2;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return triggers.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return getVolume(groupPosition, childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        Trigger trigger = (Trigger) getGroup(groupPosition);
        final long triggerId = trigger.getId();

        if( convertView == null )
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate( R.layout.rowlayout_group, null );
        }

        StringBuilder dateTime = new StringBuilder();
        if( trigger.isRepeat )
        {
            int count = 0;
            for( int i = 0; i < trigger.daysOfWeek.length(); i++ )
            {
                if( trigger.daysOfWeek.charAt( i ) == '1' )
                {
                    if( count++ > 0 )
                    {
                        dateTime.append( "/" );
                    }
                    dateTime.append( getDay( i ) );
                }
            }
            dateTime.append( " " ).append( trigger.triggerTime );
        }
        else
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm", Locale.US);
            dateTime.append( simpleDateFormat.format( trigger.triggerDateTime ) );
        }

        TextView triggerDateTimeTextView = (TextView) convertView.findViewById( R.id.trigger_date_txt);
        triggerDateTimeTextView.setText( dateTime.toString() );

        ImageButton deleteTriggerButton = (ImageButton) convertView.findViewById( R.id.delete_trigger_btn );
        deleteTriggerButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                switcherDelegate.deleteTrigger(triggerId );
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        String volumeName = getVolumeName( groupPosition, childPosition );
        int volume = (int) getChild(groupPosition, childPosition);

        if( convertView == null )
        {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate( R.layout.rowlayout_child, null );
        }

        TextView volumeNameText = (TextView) convertView.findViewById( R.id.volume_name_txt );
        volumeNameText.setText(volumeName);

        SeekBar seekBar = (SeekBar) convertView.findViewById( R.id.volume_skbar );
        seekBar.setProgress( volume );
        seekBar.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }

    private String getVolumeName( int triggerPosition, int volumePosition )
    {
        if( triggers.get( triggerPosition ).ringerMode != Constants.RingerMode.Normal)
        {
            volumePosition++;
        }

        switch( volumePosition )
        {
            case 0:
                return "Ringer";
            case 1:
                return "Media";
            case 2:
                return "Alarm";
        }

        return null;
    }

    private int getVolume( int triggerPosition, int volumePosition )
    {
        Trigger trigger = triggers.get( triggerPosition );
        if( trigger.ringerMode != Constants.RingerMode.Normal)
        {
            volumePosition++;
        }

        switch( volumePosition )
        {
            case 0:
                return trigger.ringerVolume;
            case 1:
                return trigger.mediaVolume;
            case 2:
                return trigger.alarmVolume;
        }

        return 0;
    }

    private String getDay( int day )
    {
        switch( day )
        {
            case 0:
                return "SUN";
            case 1:
                return "MON";
            case 2:
                return "TUE";
            case 3:
                return "WED";
            case 4:
                return "THU";
            case 5:
                return "FRI";
            case 6:
                return "SAT";
        }
        return null;
    }
}
