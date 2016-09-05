package com.betadevels.autosound;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.betadevels.autosound.layout.AutoSpaceFlowLayout;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;

public class AddTriggerActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener,
        RadialTimePickerDialogFragment.OnTimeSetListener
{
    private static final String DATE_PICKER_TAG = "DATE_PICKER_FRAGMENT";
    private static final String TIME_PICKER_TAG = "TIME_PICKER_FRAGMENT";
    private static final String TAG = "AddTriggerActivity";

    private CheckBox[] weekDaysCheckBoxes = new CheckBox[7];
    private Button setDateButton;
    private Button setTimeButton;
    private Button cancelButton;
    private Button createButton;
    private Switch repeatSwitch;
    private Spinner ringerModeSpinner;
    private SeekBar ringerVolumeSeekBar;
    private TextView ringerVolumeText;

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
                            setDateButton.setEnabled( !isChecked );
                        }
                    }
                }
            });
        }

        AutoSpaceFlowLayout autoSpaceFlowLayout = (AutoSpaceFlowLayout) findViewById( R.id.days_week_group );
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
                        datePicker = new CalendarDatePickerDialogFragment();
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
                            Snackbar.make( weekDaysCheckBoxes[6], "Select day(s) for trigger!", Snackbar.LENGTH_INDEFINITE )
                                    .setAction("Dismiss", new View.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            Log.i(TAG, "onClick: SnackBar clicked!");
                                        }
                                    })
                                    .show();
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
                        //TODO: Save the trigger in Database and set alarm for the trigger
                    }
                }
            });
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth)
    {
        String datePrint = "Year : " + year + " Month : " + monthOfYear + " Day : " + dayOfMonth;
        Log.i(TAG, "onDateSet: "+datePrint);

        setDateButton.setText(dayOfMonth + "/" + monthOfYear+"/"+year);
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
        Log.i(TAG, "onTimeSet: " + timePrint);

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
}
