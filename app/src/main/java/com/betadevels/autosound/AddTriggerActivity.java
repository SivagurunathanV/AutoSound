package com.betadevels.autosound;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;

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
    private Switch repeatSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trigger);

        repeatSwitch = (Switch) findViewById( R.id.repeat_switch );
        boolean isChecked = false;
        if (repeatSwitch != null)
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
                    CalendarDatePickerDialogFragment datePicker = new CalendarDatePickerDialogFragment();
                    datePicker.setOnDateSetListener( AddTriggerActivity.this );
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
                    RadialTimePickerDialogFragment timePicker = new RadialTimePickerDialogFragment();
                    timePicker.setOnTimeSetListener( AddTriggerActivity.this );
                    timePicker.show( getSupportFragmentManager(), TIME_PICKER_TAG );
                }
            });
        }
    }

    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth)
    {
        String datePrint = "Year : " + year + " Month : " + monthOfYear + " Day : " + dayOfMonth;
        Log.i(TAG, "onDateSet: "+datePrint);
        Snackbar.make( setDateButton, datePrint, Snackbar.LENGTH_SHORT )
                .setAction("Action", null)
                .show();
        setDateButton.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
    }

    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute)
    {
        String timePrint = hourOfDay + ":" + minute;
        Log.i(TAG, "onTimeSet: " + timePrint);
        Snackbar.make( setTimeButton, timePrint, Snackbar.LENGTH_LONG )
                .setAction("Action", null)
                .show();
        setTimeButton.setText( timePrint );
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
