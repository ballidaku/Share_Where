package car.sharewhere.gagan.sharewherecars.fragments;

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
import android.widget.Toast;

import car.sharewhere.gagan.GPS_Classes.GetCurrentLocation;
import car.sharewhere.gagan.GPS_Classes.Location_Notifier;
import car.sharewhere.gagan.GPS_Classes.ProgressDialogWoody;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.sharewherecars.RegularBasis;
import car.sharewhere.gagan.utills.ConnectivityDetector;
import car.sharewhere.gagan.utills.Utills_G;

public class OfferA_a_Ride extends FragmentG implements Location_Notifier
{
    RelativeLayout rel_just_once, rel_regular_basis;
//    private ProgressDialogWoody dailog;
    private GetCurrentLocation  google_location;
    ConnectivityDetector cd;

    public OfferA_a_Ride()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_offer_a_a__ride, container, false);
        setActionBar(view, "Offer A Ride");
        findViewById(view);

        cd = new ConnectivityDetector(getActivity());
        /******** To get the location by using google api client***********/
//        dailog = new ProgressDialogWoody(getActivity());

        google_location = new GetCurrentLocation(getActivity());
        google_location.setOnResultsListener(this);
        if (cd.isConnectingToInternet() && google_location.mGoogleApiClient != null)
        {
            google_location.mGoogleApiClient.connect();
//            dailog.show();
        }
        else
        {
            Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
        }

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

    @Override
    public void LOCATION_NOTIFIER(Location latlng)
    {



        if (cd.isConnectingToInternet())
        {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            address_current = Utills_G.address(getActivity(), latlng.getLatitude(), latlng.getLongitude());
            lat = GlobalConstants.latitude(latlng.getLatitude());
            longitude = GlobalConstants.longitude(latlng.getLongitude());

            address_current = address_current.trim();
            address_current = address_current.replace(" ", "");
            Log.e("address replace", "" + address_current);

            address_current.replace(System.getProperty("line.separator"), "");
            Log.e("add line replace", "" + address_current);

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
        else
        {
            Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_LONG).show();
        }
    }
}