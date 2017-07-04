package com.betadevels.autosound.activities;

import android.app.AlarmManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.betadevels.autosound.AlarmManagerHandler;
import com.betadevels.autosound.DAOs.TriggerDAO;
import com.betadevels.autosound.DAOs.TriggerInstanceDAO;
import com.betadevels.autosound.R;
import com.betadevels.autosound.models.Trigger;
import com.betadevels.autosound.layouts.AutoSpaceFlowLayout;
import com.betadevels.autosound.models.TriggerInstance;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

import org.joda.time.LocalDateTime;

import java.util.Calendar;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;

public class AddTriggerActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener,
        RadialTimePickerDialogFragment.OnTimeSetListener
{
    private static final String DATE_PICKER_TAG = "DATE_PICKER_FRAGMENT";
    private static final String TIME_PICKER_TAG = "TIME_PICKER_FRAGMENT";
    private static final String TAG = "AddTriggerActivity";

    private AutoSpaceFlowLayout autoSpaceFlowLayout;
    private CheckBox[] weekDaysCheckBoxes = new CheckBox[7];
    private Button setDateButton;
    private Button setTimeButton;
    private Button cancelButton;
    private Button createButton;
    private Switch repeatSwitch;
    private Spinner ringerModeSpinner;
    private SeekBar ringerVolumeSeekBar;
    private SeekBar mediaVolumeSeekBar;
    private SeekBar alarmVolumeSeekBar;
    private TextView ringerVolumeText;

    AlarmManagerHandler alarmManagerHandler;
    CalendarDatePickerDialogFragment datePicker;
    RadialTimePickerDialogFragment timePicker;
    private boolean isDateSet = false;
    private boolean isTimeSet = false;
    private int year, month, day, hourOfDay, minute;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trigger);
        ActionBar actionBar = getSupportActionBar();
        if( actionBar != null )
        {
            actionBar.setDisplayHomeAsUpEnabled( true );
        }

        //TODO: Load saved data if any present

        if( alarmManagerHandler == null )
        {
            alarmManagerHandler = new AlarmManagerHandler( (AlarmManager)getSystemService( ALARM_SERVICE ), getBaseContext() );
        }

        repeatSwitch = (Switch) findViewById( R.id.repeat_switch );
        boolean isChecked = false;
        if( repeatSwitch != null )
        {
            isChecked = repeatSwitch.isChecked();
            repeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
            {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
                {
                    for( CheckBox checkBox : weekDaysCheckBoxes )
                    {
                        if( checkBox != null )
                        {
                            checkBox.setEnabled( isChecked );
                        }
                    }
                    setDateButton.setEnabled( !isChecked );

                    if( !isChecked )
                    {
                        autoSpaceFlowLayout.setBackground( null );
                    }
                    else
                    {
                        setDateButton.setError( null );
                    }
                }
            });
        }

        autoSpaceFlowLayout = (AutoSpaceFlowLayout) findViewById( R.id.days_week_group );
        if (autoSpaceFlowLayout != null)
        {
            for( int i = 0; i < autoSpaceFlowLayout.getChildCount(); i++ )
            {
                weekDaysCheckBoxes[i] = ( CheckBox )autoSpaceFlowLayout.getChildAt( i );
                weekDaysCheckBoxes[i].setEnabled( isChecked );
            }
        }

        setDateButton = (Button) findViewById( R.id.set_date_btn );
        if (setDateButton != null)
        {
            setDateButton.setEnabled( !isChecked );
            setDateButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if( datePicker == null )
                    {
                        Calendar endRange = Calendar.getInstance();
                        endRange.add( Calendar.YEAR, 1 );
                        datePicker = new CalendarDatePickerDialogFragment();
                        datePicker.setDateRange( new MonthAdapter.CalendarDay(Calendar.getInstance() ), new MonthAdapter.CalendarDay( endRange ) );
                        datePicker.setOnDateSetListener(AddTriggerActivity.this);
                    }
                    datePicker.show( getSupportFragmentManager(), DATE_PICKER_TAG );
                }
            });
        }

        setTimeButton = (Button) findViewById( R.id.set_time_btn );
        if( setTimeButton != null )
        {
            setTimeButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if( timePicker == null )
                    {
                        timePicker = new RadialTimePickerDialogFragment();
                        timePicker.setOnTimeSetListener(AddTriggerActivity.this);
                    }
                    timePicker.show(getSupportFragmentManager(), TIME_PICKER_TAG);
                }
            });
        }

        ringerModeSpinner = (Spinner) findViewById( R.id.ringer_mode_spnr );
        if( ringerModeSpinner != null )
        {
            ringerModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
            {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                {
                    String selectedRingerMode = parent.getItemAtPosition( position ).toString();
                    Log.i(TAG, "onItemSelected: Selected Ringer mode : " + selectedRingerMode);
                    if( ringerVolumeSeekBar != null && ringerVolumeText != null )
                    {
                        boolean isNormalModeSelected = selectedRingerMode.equalsIgnoreCase("Normal");
                        ringerVolumeSeekBar.setEnabled( isNormalModeSelected );
                        ringerVolumeSeekBar.setVisibility(isNormalModeSelected ? View.VISIBLE : View.GONE);
                        ringerVolumeText.setVisibility( isNormalModeSelected ? View.VISIBLE : View.GONE );
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent)
                {
                    //Nothing to do here!
                }
            });
        }

        ringerVolumeText = (TextView) findViewById( R.id.ringer_volume_txt );
        ringerVolumeSeekBar = (SeekBar) findViewById( R.id.ringer_volume_skbar );
        mediaVolumeSeekBar = (SeekBar) findViewById( R.id.media_volume_skbar );
        alarmVolumeSeekBar = (SeekBar) findViewById( R.id.alarm_volume_skbar );
        if( ringerVolumeSeekBar != null && ringerVolumeText != null )
        {
            boolean isNormalModeSelected = ringerModeSpinner.getSelectedItem().toString().equalsIgnoreCase("Normal");
            ringerVolumeSeekBar.setEnabled( isNormalModeSelected );
            ringerVolumeSeekBar.setVisibility(isNormalModeSelected ? View.VISIBLE : View.GONE);
            ringerVolumeText.setVisibility( isNormalModeSelected ? View.VISIBLE : View.GONE );
        }

        createButton = (Button) findViewById( R.id.create_btn );
        if( createButton != null )
        {
            createButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    boolean isFormValid = true;
                    if( repeatSwitch.isChecked() )
                    {
                        int boxesChecked = 0;
                        for( CheckBox checkBox : weekDaysCheckBoxes )
                        {
                            boxesChecked += checkBox.isChecked() ? 1 : 0;
                        }

                        if( boxesChecked == 0 )
                        {
                            if( autoSpaceFlowLayout != null )
                            {
                                autoSpaceFlowLayout.setBackground( ContextCompat.getDrawable( getBaseContext(), R.drawable.shape_roundedrect_redborder) );
                            }
                            isFormValid = false;
                        }
                        else if( autoSpaceFlowLayout != null )
                        {
                            autoSpaceFlowLayout.setBackground( null );
                        }
                    }
                    else if( !isDateSet )
                    {
                        isFormValid = false;
                        setDateButton.setError( "Date not set" );
                    }

                    if( !isTimeSet )
                    {
                        isFormValid = false;
                        setTimeButton.setError( "Time not set" );
                    }

                    if( isFormValid )
                    {
                        String ringerMode = ringerModeSpinner.getSelectedItem().toString();
                        int ringerVolume = ringerVolumeSeekBar.getProgress();
                        int mediaVolume = mediaVolumeSeekBar.getProgress();
                        int alarmVolume = alarmVolumeSeekBar.getProgress();
                        Trigger trigger = TriggerDAO.create( repeatSwitch.isChecked(), weekDaysCheckBoxes,
                                year, month, day, hourOfDay, minute, ringerMode, ringerVolume, mediaVolume, alarmVolume);

                        if( repeatSwitch.isChecked() )
                        {
                            for( int i = 0; i < weekDaysCheckBoxes.length; i++ )
                            {
                                if( weekDaysCheckBoxes[i].isChecked() )
                                {
                                    Calendar calendar = Calendar.getInstance();

                                    //If the day has already past this week, then schedule it from
                                    //next week.
                                    if( calendar.get( Calendar.DAY_OF_WEEK ) > i + 1 )
                                    {
                                        calendar.add( Calendar.WEEK_OF_YEAR, 1 );
                                    }

                                    calendar.set( Calendar.DAY_OF_WEEK, i + 1 );
                                    calendar.set( Calendar.HOUR_OF_DAY, hourOfDay );
                                    calendar.set( Calendar.MINUTE, minute );

                                    //If the day is today but the time has past, then schedule it
                                    //from next week.
                                    if( calendar.compareTo( Calendar.getInstance() ) < 0 )
                                    {
                                        calendar.add( Calendar.WEEK_OF_YEAR, 1 );
                                    }

                                    Log.i(TAG, "onClick: Setting alarm for Calendar : " + new LocalDateTime( calendar ).toString());
                                    TriggerInstance triggerInstance = TriggerInstanceDAO.create( trigger.getId() );
                                    alarmManagerHandler.setAlarm( triggerInstance.getId().intValue(), true, calendar,
                                            ringerMode, ringerVolume, mediaVolume, alarmVolume );
                                }
                            }
                        }
                        else
                        {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set( year, month, day, hourOfDay, minute);
                            TriggerInstance triggerInstance = TriggerInstanceDAO.create( trigger.getId() );
                            alarmManagerHandler.setAlarm( triggerInstance.getId().intValue(), false, calendar, ringerMode, ringerVolume,
                                    mediaVolume, alarmVolume);
                        }

                        setResult( RESULT_OK );
                        finish();
                    }
                }
            });
        }

        cancelButton = (Button) findViewById( R.id.cancel_btn );
        if( cancelButton != null )
        {
            cancelButton.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    setResult( RESULT_CANCELED );
                    finish();
                }
            });
        }

        createTourGuide();
    }

    public void createTourGuide()
    {
        ChainTourGuide repeatSwitchStep = ChainTourGuide.init( this )
                .setToolTip(new ToolTip().setTitle("Repeat Weekly")
                        .setDescription("Enable this switch if you want your trigger to be repeated weekly").
                                setGravity( Gravity.BOTTOM | Gravity.CENTER ))
                .playLater( repeatSwitch );

        ChainTourGuide weekDaysLayoutStep = ChainTourGuide.init( this )
                .setToolTip(new ToolTip().setTitle("Days of Week")
                        .setDescription("If you have switched on the repeat button above, select the days of the week here for the trigger to be activated on").
                                setGravity( Gravity.BOTTOM | Gravity.CENTER ))
                .playLater( autoSpaceFlowLayout );

        ChainTourGuide setDateStep = ChainTourGuide.init( this )
                .setToolTip(new ToolTip().setTitle("Set Date")
                        .setDescription("If you want the trigger to be a one time activity, disable the repeat switch above and set the date here").
                                setGravity( Gravity.END ))
                .playLater( setDateButton );

        ChainTourGuide setTimeStep = ChainTourGuide.init( this )
                .setToolTip(new ToolTip().setTitle("Set Time")
                        .setDescription("Set the time for the trigger").
                                setGravity( Gravity.START ))
                .playLater( setTimeButton );

        ChainTourGuide setRingerStep = ChainTourGuide.init( this )
                .setToolTip(new ToolTip().setTitle("Set Ringer mode")
                        .setDescription("Select the ringer mode to be set by the trigger").
                                setGravity( Gravity.TOP ))
                .playLater( ringerModeSpinner );

        ChainTourGuide setVolumesStep = ChainTourGuide.init( this )
                .setToolTip(new ToolTip().setTitle("Set Volumes")
                        .setDescription("Set the volumes of different streams here").
                                setGravity( Gravity.TOP ))
                .playLater( ringerVolumeSeekBar );

        ChainTourGuide createStep = ChainTourGuide.init( this )
                .setToolTip(new ToolTip().setTitle("Create Trigger")
                        .setDescription("When you are done, click on create to schedule the trigger").
                                setGravity( Gravity.TOP | Gravity.START ))
                .playLater( createButton );

        Sequence sequence = new Sequence.SequenceBuilder().add(repeatSwitchStep, weekDaysLayoutStep, setDateStep, setTimeStep, setRingerStep, setVolumesStep, createStep)
                .setDefaultOverlay(new Overlay())
                .setDefaultPointer(new Pointer())
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        ChainTourGuide.init( this ).playInSequence( sequence );
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth)
    {
        setDateButton.setText( dayOfMonth + "/" + ( monthOfYear + 1 ) + "/" + year );
        setDateButton.setError( null );
        isDateSet = true;
        this.year = year;
        this.month = monthOfYear;
        this.day = dayOfMonth;
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute)
    {
        String timePrint = hourOfDay + ":" + minute;
        setTimeButton.setText( timePrint );
        setTimeButton.setError( null );
        isTimeSet = true;
        this.hourOfDay = hourOfDay;
        this.minute = minute;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        CalendarDatePickerDialogFragment datePicker = (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag(DATE_PICKER_TAG);
        if (datePicker != null)
        {
            datePicker.setOnDateSetListener( this );
        }

        RadialTimePickerDialogFragment timePicker = (RadialTimePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag(TIME_PICKER_TAG );
        if( timePicker != null )
        {
            timePicker.setOnTimeSetListener( this );
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        //TODO: Save inputs
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch( item.getItemId() )
        {
            case android.R.id.home:
                Log.i(TAG, "onOptionsItemSelected: Back button selected");
                setResult( RESULT_CANCELED );
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
