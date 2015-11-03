package car.sharewhere.gagan.utills;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;


/**
 * Created by GagaN on 8/14/2015.
 */
public class GetCurrentLocation implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener
{

    // LogCat tag
    // private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 60000;

    public static Location mLastLocation;

    // Google client to interact with Google API
    private GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest mLocationRequest;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 30000; // 30 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT = 50; // 50 meters


    Context con;

    public GetCurrentLocation(Context con)
    {
        this.con = con;

        mRequestingLocationUpdates = true;
        // First we need to check availability of play services

    }


    public void onStart()
    {

        if (mGoogleApiClient != null)
        {
            mGoogleApiClient.connect();
        }


    }


    public void onResume()
    {

        checkPlayServices();

        // Resuming the periodic location updates
        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates)
        {
            startLocationUpdates();
        }
        // togglePeriodicLocationUpdates();
    }


    public void onStop()
    {
        if (mGoogleApiClient.isConnected())
        {
            mGoogleApiClient.disconnect();
        }
    }

    public void onPause()
    {
        stopLocationUpdates();
    }

    /**
     * Method to display the location on UI
     */
    public Location displayLocation()
    {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null)
        {
            /*double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            Log.e("SSSSSSSSSSSSSSSSSSSSSSSSSSSSS"+latitude + "", "" + longitude);*/
            return mLastLocation;
        }
        else
        {
//            displayLocation();
            //lblLocation .setText("(Couldn't get the location. Make sure location is enabled on the device)");
        }

        return null;
    }

    /**
     * Method to toggle periodic location updates
     */
    public void togglePeriodicLocationUpdates()
    {
        if (!mRequestingLocationUpdates)
        {
            // Changing the button text
            //  btnStartLocationUpdates .setText("Stop location update");

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            // Log.d(TAG, "Periodic location updates started!");

        }
        else
        {
            // Changing the button text
            // btnStartLocationUpdates .setText("Start location update");

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            //  Log.d(TAG, "Periodic location updates stopped!");
        }


    }

    /**
     * Creating google api client object
     */
    public synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(con)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creating location request object
     */
    public void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    /**
     * Method to verify google play services on the device
     */
    public boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(con);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) con, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(con, "This device is not supported.", Toast.LENGTH_LONG)
                        .show();
                ((Activity) con).finish();
            }
            return false;
        }
        return true;
    }

    /**
     * Starting the location updates
     */
    protected void startLocationUpdates()
    {

        try
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Stopping location updates
     */
    protected void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     * Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        Log.e("GetCurrentLocation", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0)
    {

        startLocationUpdates();
        // Once connected with google api, get the location
        displayLocation();

    }

    @Override
    public void onConnectionSuspended(int arg0)
    {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        // Assign the new location
        mLastLocation = location;

//        ((MainActivity)con).update_my_location(mLastLocation);


        // Displaying the new location on UI
        displayLocation();
    }


    public Runnable GpsFinder = new Runnable()
    {
        public void run()
        {

            try
            {
                Location tempLoc = displayLocation();

                handler.postDelayed(GpsFinder, GlobalConstants.timeSCreenActive);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    };

    private Handler handler = new Handler();


}