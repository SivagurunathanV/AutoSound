package com.betadevels.autosound.activities;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.Configuration;
import com.betadevels.autosound.R;
import com.betadevels.autosound.adapters.TriggerCardsAdapter;
import com.betadevels.autosound.models.Trigger;
import com.betadevels.autosound.models.TriggerInstance;
import com.betadevels.autosound.DAOs.TriggerDAO;
import com.betadevels.autosound.resources.Constants;

import java.lang.ref.WeakReference;
import java.util.List;

public class MainActivity extends AppCompatActivity
{
    private static final String TAG = "MainActivity";
    RecyclerView recyclerView;
    TriggerCardsAdapter triggerCardsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Configuration dbConfiguration = new Configuration.Builder( this ).setDatabaseName( "AutoSound.db" ).addModelClasses(Trigger.class, TriggerInstance.class).create();
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
                    Intent launchAddNewTriggerIntent = new Intent( getBaseContext(), AddTriggerActivity.class );
                    startActivityForResult(launchAddNewTriggerIntent, Constants.ADD_TRIGGER_ACTIVITY_RC );
                }
            });
        }

        recyclerView = (RecyclerView) findViewById( R.id.recycler_view );
        if (recyclerView != null)
        {
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setItemAnimator(new DefaultItemAnimator());

            triggerCardsAdapter = new TriggerCardsAdapter( this, new WeakReference<>(recyclerView), TriggerDAO.getAllTriggers());
            recyclerView.setAdapter(triggerCardsAdapter);

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper( createHelperCallback() );
            itemTouchHelper.attachToRecyclerView( recyclerView );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
                triggerCardsAdapter.delete( adapterPosition );
                Toast.makeText(MainActivity.this, "Trigger deleted!", Toast.LENGTH_SHORT).show();
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
}
