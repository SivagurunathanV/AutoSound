package com.betadevels.autosound.activities;

import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.activeandroid.Configuration.Builder;
import com.betadevels.autosound.DAOs.TriggerDAO;
import com.betadevels.autosound.R;
import com.betadevels.autosound.adapters.TriggerCardsAdapter;
import com.betadevels.autosound.models.Trigger;
import com.betadevels.autosound.models.TriggerInstance;
import com.betadevels.autosound.resources.Constants;

import java.lang.ref.WeakReference;
import java.util.List;

import tourguide.tourguide.ChainTourGuide;
import tourguide.tourguide.Overlay;
import tourguide.tourguide.Pointer;
import tourguide.tourguide.Sequence;
import tourguide.tourguide.ToolTip;
import tourguide.tourguide.TourGuide;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    private RecyclerView recyclerView;
    private TriggerCardsAdapter triggerCardsAdapter;
    private AlertDialog deleteAlertDialog;
    private int deleteTriggerPosition;

    private TourGuide mainTourGuide;
    private SharedPreferences preferences;
    private boolean mainTourGuideCompleted;
    private boolean deleteTourGuideCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration dbConfiguration = new Builder( this ).setDatabaseName( "AutoSound.db" ).addModelClasses(Trigger.class, TriggerInstance.class).create();
        ActiveAndroid.initialize(dbConfiguration);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if( fab != null )
        {
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    if( mainTourGuide != null )
                    {
                        mainTourGuide.cleanUp();
                    }

                    Intent addNewTriggerIntent = new Intent( getBaseContext(), AddTriggerActivity.class );
                    addNewTriggerIntent.putExtra("com.betadevels.autosound.MAIN_TOUR_GUIDE", mainTourGuideCompleted);
                    startActivityForResult(addNewTriggerIntent, Constants.ADD_TRIGGER_ACTIVITY_RC );
                }
            });
        }

        if( deleteAlertDialog == null )
        {
            initDeleteAlertDialog();
        }

        recyclerView = (RecyclerView) findViewById( R.id.recycler_view );
        if (recyclerView != null)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            triggerCardsAdapter = new TriggerCardsAdapter( this, new WeakReference<>(recyclerView), TriggerDAO.getAllTriggers());
            recyclerView.setAdapter(triggerCardsAdapter);
            recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout()
                {
                    Log.i(TAG, "From onGlobalLayout");
                    if(!deleteTourGuideCompleted && recyclerView.getChildCount() >= 1)
                    {
                        startDeleteTourGuide();
                        recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper( createHelperCallback() );
            itemTouchHelper.attachToRecyclerView( recyclerView );
        }

        //Creating the tour guide after the recycle view is rendered and if app is opened for first time
        preferences = preferences == null ? getPreferences(Context.MODE_PRIVATE) : preferences;
        mainTourGuideCompleted = preferences.getBoolean("MAIN_TOUR_GUIDE", false);
        deleteTourGuideCompleted = preferences.getBoolean("DELETE_TOUR_GUIDE", false);

        if( !mainTourGuideCompleted)
        {
            mainTourGuide = TourGuide.init( this )
                    .with(TourGuide.Technique.Click)
                    .setPointer(new Pointer())
                    .setToolTip(new ToolTip().setTitle("Create new Trigger")
                            .setDescription("Click on this icon to create new trigger for profile change")
                            .setGravity(Gravity.TOP | Gravity.START))
                    .setOverlay(new Overlay())
                    .playOn( fab );
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted())
        {
            displayNotificationPermissionDialog();
        }
    }

    private void startDeleteTourGuide()
    {
        View itemView = recyclerView.findViewHolderForLayoutPosition(0).itemView;
        Log.i(TAG, Boolean.toString(itemView == null));
        ChainTourGuide newTriggerStep = ChainTourGuide.init( this )
                .setToolTip(new ToolTip().setTitle("Created Trigger")
                        .setDescription("Triggers you have created will appear here. Click on a trigger to view its volume settings.").
                                setGravity( Gravity.BOTTOM | Gravity.CENTER ))
                .playLater( itemView );


        ChainTourGuide deleteCardStep = ChainTourGuide.init( this )
                .setToolTip(new ToolTip().setTitle("Delete Trigger")
                        .setDescription("You can delete a trigger by swiping it to the left.").
                                setGravity( Gravity.BOTTOM | Gravity.CENTER ))
                .playLater( itemView );

        Sequence sequence = new Sequence.SequenceBuilder().add(newTriggerStep, deleteCardStep)
                .setDefaultOverlay(new Overlay())
                .setDefaultPointer(null)
                .setContinueMethod(Sequence.ContinueMethod.Overlay)
                .build();

        ChainTourGuide.init( this ).playInSequence( sequence );

        deleteTourGuideCompleted = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("DELETE_TOUR_GUIDE", deleteTourGuideCompleted);
        editor.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //TODO: Add menu item for preferences/settings
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == Constants.ADD_TRIGGER_ACTIVITY_RC )
        {
            // Since the main tour guide takes the user to the add trigger activity
            // we are changing the preference value after we have returned from the
            // activity.
            if( !mainTourGuideCompleted )
            {
                mainTourGuideCompleted = true;
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("MAIN_TOUR_GUIDE", mainTourGuideCompleted);
                editor.apply();
            }

            if( resultCode == RESULT_OK )
            {
                List<Trigger> triggers = TriggerDAO.getAllTriggers();
                triggerCardsAdapter.insert( triggers, triggers.size() - 1 );
                Toast.makeText(MainActivity.this, "New Trigger created!", Toast.LENGTH_SHORT).show();
            }
            else if( resultCode == RESULT_CANCELED )
            {
                Log.i(TAG, "onActivityResult: New trigger creation cancelled!");
            }
        }
    }

    private ItemTouchHelper.Callback createHelperCallback()
    {

        return new ItemTouchHelper.SimpleCallback( 0, ItemTouchHelper.LEFT )
        {
            Drawable redBGDrawable;
            Drawable deleteDrawable;
            int deleteDrawableMargin;
            boolean initiated;

            private void init()
            {
                redBGDrawable = new ColorDrawable( Color.RED );
                deleteDrawable = ContextCompat.getDrawable( MainActivity.this, android.R.drawable.ic_menu_delete );
                deleteDrawable.setColorFilter( Color.WHITE, PorterDuff.Mode.SRC_ATOP );
                deleteDrawableMargin = (int) MainActivity.this.getResources().getDimension(R.dimen.ic_delete_margin);
                initiated = true;
            }

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target)
            {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                int adapterPosition = viewHolder.getAdapterPosition();

                Log.i(TAG, "onSwiped: Swiped Position : " + adapterPosition );
                deleteTriggerPosition = adapterPosition;
                deleteAlertDialog.show();
            }

            @Override
            public void onChildDrawOver(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                View itemView = viewHolder.itemView;

                if( viewHolder.getAdapterPosition() == -1 )
                {
                    return;
                }

                if( !initiated )
                {
                    init();
                }

                if( dX < 0 )
                {
                    redBGDrawable.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                    redBGDrawable.draw(c);

                    if( dX < -(deleteDrawableMargin - 1) )
                    {
                        int itemHeight = itemView.getBottom() - itemView.getTop();

                        int intrinsicWidth = deleteDrawable.getIntrinsicWidth();
                        int intrinsicHeight = deleteDrawable.getIntrinsicWidth();

                        int deleteDrawableLeft = itemView.getRight() + Math.max( -(deleteDrawableMargin + intrinsicWidth), (int) dX );
                        int deleteDrawableRight = itemView.getRight() - deleteDrawableMargin;
                        int deleteDrawableTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                        int deleteDrawableBottom = deleteDrawableTop + intrinsicHeight;

                        deleteDrawable.setBounds(deleteDrawableLeft, deleteDrawableTop, deleteDrawableRight, deleteDrawableBottom);
                        deleteDrawable.draw(c);
                    }
                }

                super.onChildDrawOver(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
    }

    private void initDeleteAlertDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this );
        alertDialogBuilder.setTitle( "Delete Trigger" )
                .setMessage( "Are you sure?" )
                .setIcon( android.R.drawable.ic_menu_delete )
                .setPositiveButton("Delete", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        triggerCardsAdapter.delete( deleteTriggerPosition );
                        Toast.makeText(MainActivity.this, "Trigger deleted!", Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        dialogInterface.dismiss();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener()
                {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface)
                    {
                        triggerCardsAdapter.notifyItemChanged( deleteTriggerPosition );
                    }
                });
        deleteAlertDialog = alertDialogBuilder.create();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void displayNotificationPermissionDialog()
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder( this );
        alertDialogBuilder.setTitle( "Permission required" )
                .setMessage( "App requires your permission to change sound settings. Provide access to the app in the next screen." )
                .setIcon( android.R.drawable.ic_dialog_alert )
                .setCancelable( false )
                .setPositiveButton("Okay", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent intent = new Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
                        startActivity(intent);
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
