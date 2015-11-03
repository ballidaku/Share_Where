package car.sharewhere.gagan.sharewherecars.fragments;

import android.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import car.sharewhere.gagan.sharewherecars.R;

/**
 * Created by gagandeep on 9/10/15.
 */
public class FragmentG extends Fragment
{


    public void setActionBar(View rootView,String Title)
    {
        try
        {
            final Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar);
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            android.support.v7.app.ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.ic_menu);
            actionBar.setTitle(Title);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


}
