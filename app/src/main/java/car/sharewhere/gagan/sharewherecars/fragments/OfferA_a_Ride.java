package car.sharewhere.gagan.sharewherecars.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import car.sharewhere.gagan.Location.GiveMeLocationS;
import car.sharewhere.gagan.Location.Location_Interface;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.sharewherecars.Just_Once_offerRide;
import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.sharewherecars.RegularBasis;
import car.sharewhere.gagan.utills.Utills_G;

public class OfferA_a_Ride extends FragmentG
{
    RelativeLayout rel_just_once, rel_regular_basis;
    SharedPreferences preferences;

    Context con;

    public OfferA_a_Ride()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_offer_a_a__ride, container, false);
        setActionBar(view, "Offer A Ride");
        findViewById(view);

        con = getActivity();

        preferences = PreferenceManager.getDefaultSharedPreferences(con);

        GetLocation();

        return view;
    }

    private void findViewById(View view)
    {
        rel_just_once = (RelativeLayout) view.findViewById(R.id.rel_just_once);
        rel_regular_basis = (RelativeLayout) view.findViewById(R.id.regular_basis);

        rel_just_once.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(getActivity(), Just_Once_offerRide.class);
                startActivity(i);
            }
        });

        rel_regular_basis.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(getActivity(), RegularBasis.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

    }

    public String address_current = "";
    public String lat, longitude;

    public void takeLocation(Location latlng)
    {
        address_current = Utills_G.address(con, latlng.getLatitude(), latlng.getLongitude());
        lat = GlobalConstants.latitude(latlng.getLatitude());
        longitude = GlobalConstants.longitude(latlng.getLongitude());

        address_current = address_current.trim();
        address_current = address_current.replace(" ", "");
        //            Log.e("address replace", "" + address_current);

        address_current.replace(System.getProperty("line.separator"), "");
        //            Log.e("add line replace", "" + address_current);

        SharedPreferences.Editor editor = preferences.edit();
        if (!address_current.equals(""))
        {
            editor.putString("current_city", address_current);
        }

        editor.putString("current_lat", lat);
        editor.putString("current_long", longitude);
        editor.apply();
        Log.e("location details are ", "latitude   " + latlng.getLatitude() + "  longitutde " + latlng.getLongitude());

    }

    //**************************************************************************************************************

    private void GetLocation()
    {

        new GiveMeLocationS(getActivity(), new Location_Interface()
        {
            @Override
            public void onTaskCompleted(Location output)
            {
                try
                {
                    Log.e("Sharan Latitude", "" + output.getLatitude());
                    Log.e("Sharan Longitude", "" + output.getLongitude());
                    takeLocation(output);

                }
                catch (Exception ex)
                {
                    Log.e("Exception is", ex.toString());
                }
            }
        });

    }
}