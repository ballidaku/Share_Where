package car.sharewhere.gagan.sharewherecars;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

public class ProfileActivity extends AppCompatActivity
{
    FloatingActionButton fab;

    LinearLayout layoutContainerAll;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setActionBar();
        findViewbyID();



        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Snackbar.make(view, "tap detail to edit.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


    }


    private void findViewbyID()
    {
        layoutContainerAll = (LinearLayout) findViewById(R.id.layoutContainerAll);
        fab = (FloatingActionButton) findViewById(R.id.fab);


    }



    private void setActionBar()
    {
        try
        {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
                {
                    supportFinishAfterTransition();
                }
                else
                {
                    finish();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
