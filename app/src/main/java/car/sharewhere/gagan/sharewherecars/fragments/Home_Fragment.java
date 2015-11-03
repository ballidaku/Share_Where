package car.sharewhere.gagan.sharewherecars.fragments;


import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.utills.GetCurrentLocation;

public class Home_Fragment extends FragmentG implements OnMyLocationChangeListener
{


    private GoogleMap googleMap;

    private static MapFragment mapFragment;


    private Bundle mBundle;
    private Location currentLocation;

    public Home_Fragment()
    {

    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
//		mapFragment.onLowMemory();
    }


    private void onResumeFunction()
    {
        updateLocation();

    }


    @Override
    public void onResume()
    {
        super.onResume();
        if (mapFragment == null)
        {
            initializeMapFragment();
        }

        setUpMapIfNeeded();

    }

    @Override
    public void onPause()
    {
        super.onPause();
//		mapFragment.onPause();

    }


    @Override
    public void onDestroy()
    {
//		mapFragment.onDestroy();

        // responseHandler = null;
        super.onDestroy();
    }

    private static View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

//		View rootView = inflater.inflate(R.layout.activity_map_activity, container, false);
        if (rootView != null)
        {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try
        {
            rootView = inflater.inflate(R.layout.activity_map_activity, container, false);
        }
        catch (Exception e)
        {
        /* map is already there, just return view as it is G */
        }
        setActionBar(rootView,"ShareWhere");


        initializeMapFragment();

        return rootView;
    }





    private void initializeMapFragment()
    {
        try
        {
            mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        }
        catch (Exception | Error e)
        {
            e.printStackTrace();
            if (mapFragment == null)
            {
                mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
            }
        }

        if (mapFragment == null)
        {
            mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        }
    }


    private void setUpMapIfNeeded()
    {
        if (googleMap == null)
        {
            if (mapFragment != null)
            {
                mapFragment.getMapAsync(new OnMapReadyCallback()
                {
                    @Override
                    public void onMapReady(GoogleMap gMap)
                    {
                        googleMap = gMap;
                        setUpMap();
                        onResumeFunction();
                    }
                });
            }
        }
        else
        {
            setUpMap();
            onResumeFunction();
        }


    }


    private void updateLocation()
    {
        try
        {
            if (GetCurrentLocation.mLastLocation != null)
            {
                currentLocation = GetCurrentLocation.mLastLocation;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void setUpMap()
    {

//		Marker markr = googleMap.addMarker(new MarkerOptions().position(markerLoc).draggable(false).title("").snippet("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
//		markr.showInfoWindow();

        if (googleMap != null)
        {
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationChangeListener(this);


            if (currentLocation == null)
            {
                updateLocation();
            }
            if (currentLocation != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 12.0f));
            else
                updateLocation();


        }

    }


    @Override
    public void onMyLocationChange(Location location)
    {

        // CameraUpdate myLoc = CameraUpdateFactory.newCameraPosition(new
        // CameraPosition.Builder().target(new LatLng(location.getLatitude(),
        // location.getLongitude())).zoom(6).build());
        // googleMap.moveCamera(myLoc);
        // googleMap.setOnMyLocationChangeListener(null);

        // GetAddress(location.getLatitude(), location.getLongitude());
        // GetAddressByString("7 phase");
        // //CameraUpdateFactory.newLatLngZoom(markerLoc, 12.0f)//

        if (currentLocation == null)
        {
            currentLocation = location;
        }

    }



}
