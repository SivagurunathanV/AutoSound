package com.betadevels.autosound.adapters;

import android.app.AlarmManager;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.betadevels.autosound.AlarmManagerHandler;
import com.betadevels.autosound.DAOs.TriggerDAO;
import com.betadevels.autosound.DAOs.TriggerInstanceDAO;
import com.betadevels.autosound.R;
import com.betadevels.autosound.models.Trigger;
import com.betadevels.autosound.models.TriggerInstance;
import com.betadevels.autosound.resources.Constants;
import com.betadevels.autosound.resources.Utilities;

import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import java.lang.ref.WeakReference;
import java.util.List;

public class TriggerCardsAdapter extends RecyclerView.Adapter<TriggerCardsAdapter.TriggerCardViewHolder>
{
    private static final String TAG = "TriggerCardsAdapter";
    private AlarmManagerHandler alarmManagerHandler;
    private WeakReference<RecyclerView> recyclerViewWeakReference;
    private List<Trigger> triggers;
    private int expandedPosition = -1;

    public class TriggerCardViewHolder extends RecyclerView.ViewHolder
    {
        public TextView triggerDateTextView, triggerTimeTextView, ringerModeTextView,ringerVolumeTextView;
        public LinearLayout triggerCardContentLayout;
        public ProgressBar ringerVolumePBar, mediaVolumePBar, alarmVolumePBar;
        public View dividerIndicatorView;

        public TriggerCardViewHolder(View itemView)
        {
            super(itemView);
            triggerDateTextView = (TextView) itemView.findViewById( R.id.trigger_date_txt);
            triggerTimeTextView = (TextView) itemView.findViewById( R.id.trigger_time_txt);
            ringerModeTextView = (TextView) itemView.findViewById( R.id.ringer_mode_txt );
            ringerVolumeTextView = (TextView) itemView.findViewById( R.id.ringer_volume_tv );
            triggerCardContentLayout = (LinearLayout) itemView.findViewById( R.id.trigger_card_content_layout );
            ringerVolumePBar = (ProgressBar) itemView.findViewById( R.id.ringer_volume_pbar );
            mediaVolumePBar = (ProgressBar) itemView.findViewById( R.id.media_volume_pbar );
            alarmVolumePBar = (ProgressBar) itemView.findViewById( R.id.alarm_volume_pbar );
            dividerIndicatorView = itemView.findViewById( R.id.cards_divider_view );
        }
    }

    public TriggerCardsAdapter(Context context, WeakReference<RecyclerView> recyclerViewWeakReference, List<Trigger> triggers)
    {
        this.alarmManagerHandler = new AlarmManagerHandler( (AlarmManager) context.getSystemService( Context.ALARM_SERVICE ), context );
        this.recyclerViewWeakReference = recyclerViewWeakReference;
        this.triggers = triggers;
    }

    public void insert( List<Trigger> triggers, int position )
    {
        this.triggers = triggers;
        notifyItemInserted( position );
    }

    public void deleteAll()
    {
        for(Trigger trigger : triggers)
        {
            TriggerDAO.delete( trigger.getId() );
            for( TriggerInstance triggerInstance : TriggerInstanceDAO.getAllTriggerInstances( trigger.getId() ) )
            {
                alarmManagerHandler.cancelAlarm( triggerInstance.getId().intValue() );
                TriggerInstanceDAO.delete( triggerInstance.getId() );
            }
        }

        triggers.clear();
        notifyDataSetChanged();
    }

    public void delete( int adapterPosition )
    {
        //Updating the expandedPosition according to card deletion.
        if( expandedPosition > -1 )
        {
            if( expandedPosition == adapterPosition )
            {
                expandedPosition = -1;
            }
            else if( expandedPosition > adapterPosition )
            {
                expandedPosition -= 1;
            }
        }
        Log.i(TAG, "onClick: ExpandedPosition : " + expandedPosition);

        Long triggerId = triggers.get(adapterPosition).getId();
        TriggerDAO.delete( triggerId );
        List<TriggerInstance> triggerInstances = TriggerInstanceDAO.getAllTriggerInstances(triggerId);
        if( alarmManagerHandler != null )
        {
            for( TriggerInstance triggerInstance : triggerInstances )
            {
                Log.i(TAG, "delete: Removing triggerInstance : " + triggerInstance.triggerId + " - " + triggerInstance.getId());
                alarmManagerHandler.cancelAlarm( triggerInstance.getId().intValue() );
                TriggerInstanceDAO.delete( triggerInstance.getId() );
            }
        }
        triggers.remove(adapterPosition);

        notifyItemRemoved(adapterPosition);
        notifyItemRangeChanged(adapterPosition, getItemCount());
    }

    @Override
    public TriggerCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.trigger_card, parent, false);
        return new TriggerCardViewHolder( itemView );
    }

    @Override
    public void onBindViewHolder(final TriggerCardViewHolder holder, final int position)
    {
        Trigger trigger = triggers.get(position);
        final int adapterPosition = holder.getAdapterPosition();

        StringBuilder date = new StringBuilder();
        StringBuilder time = new StringBuilder();
        if( trigger.isRepeat )
        {
            int count = 0;
            for( int i = 0; i < trigger.daysOfWeek.length(); i++ )
            {
                if( trigger.daysOfWeek.charAt( i ) == '1' )
                {
                    if( count++ > 0 )
                    {
                        date.append("/");
                    }
                    date.append(Utilities.getDayString(i, 3));
                }
            }
            String[] timeParts = trigger.triggerTime.split(":");
            LocalTime localTime = new LocalTime( Integer.parseInt( timeParts[0] ), Integer.parseInt( timeParts[1] ) );
            time.append(localTime.toString("hh:mm a").toUpperCase());
        }
        else
        {
            LocalDateTime localDateTime = new LocalDateTime( trigger.triggerDateTime );
            date.append( localDateTime.toString( "EEEE, MMMM" ) )
                .append( " " )
                .append( Utilities.attachSuperscriptToNumber(localDateTime.getDayOfMonth()) )
                .append( ", " )
                .append( localDateTime.getYear() );
            time.append(localDateTime.toString("hh:mm a").toUpperCase());
        }

        holder.triggerDateTextView.setText( date.toString() );
        holder.triggerTimeTextView.setText( time.toString() );
        holder.ringerModeTextView.setText( trigger.ringerMode.toString() );

        if( trigger.ringerMode == Constants.RingerMode.Normal )
        {
            holder.ringerVolumePBar.setProgress( trigger.ringerVolume );
        }
        else
        {
            holder.ringerVolumeTextView.setVisibility( View.GONE );
            holder.ringerVolumePBar.setVisibility( View.GONE );
        }

        holder.mediaVolumePBar.setProgress(trigger.mediaVolume);
        holder.alarmVolumePBar.setProgress(trigger.alarmVolume);

        final boolean isExpanded = holder.getAdapterPosition() == expandedPosition;
        holder.triggerCardContentLayout.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
        holder.itemView.setActivated(isExpanded);
        holder.itemView.setTag( holder.getAdapterPosition() );
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.i(TAG, "onClick: Clicked on position : " + adapterPosition);
                expandedPosition = isExpanded ? -1 : (int)v.getTag();
                if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
                {
                    TransitionManager.beginDelayedTransition( recyclerViewWeakReference.get() );
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return triggers.size();
    }

    //VERY IMPORTANT. Without this, card expansion goes crazy!
    @Override
    public int getItemViewType(int position)
    {
        return position;
    }
}
