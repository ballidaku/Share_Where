package car.sharewhere.gagan.sharewherecars;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

/**
 * Created by ameba on 2/12/15.
 */
public class LicenceDummy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_about_us);
        setActionBar();
    }


    private void setActionBar()
    {
        try
        {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            TextView txt_toolbar=(TextView)toolbar.findViewById(R.id.txt_titleTV);

            txt_toolbar.setText("Licence and Agreements");
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
            case android.R.id.home:

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
                {
                    supportFinishAfterTransition();
                }
                else
                {
                    LicenceDummy.this.finish();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
