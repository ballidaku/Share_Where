package car.sharewhere.gagan.Location;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class GetCurrentLocation implements ConnectionCallbacks, OnConnectionFailedListener, LocationListener
{

    // LogCat tag
    private static final String TAG = GetCurrentLocation.class.getSimpleName();

    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;

    public static Location mLastLocation;

    // Google client to interact with Google API
    public GoogleApiClient mGoogleApiClient;

    // boolean flag to toggle periodic location updates
    private boolean mRequestingLocationUpdates = false;

    private LocationRequest   mLocationRequest;
    private Location_Notifier listner;

    // Location updates intervals in sec
    private static int UPDATE_INTERVAL = 10000; // 10 sec
    private static int FATEST_INTERVAL = 5000; // 5 sec
    private static int DISPLACEMENT    = 10; // 10 meters

    Activity con;

    public GetCurrentLocation(Activity con)
    {
        this.con = con;

        if (checkPlayServices())
        {

            // Building the GoogleApi client
            buildGoogleApiClient();

            createLocationRequest();
        }
    }

    public void setOnResultsListener(Location_Notifier listener)
    {
        this.listner = listener;
    }

    /**
     Method to display the location on UI
     */
    private void displayLocation()
    {

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null)
        {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

        }
        else
        {

            Log.e("Locati", "wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww");

        }
    }

    /**
     Method to toggle periodic location updates
     */
    private void togglePeriodicLocationUpdates()
    {
        if (!mRequestingLocationUpdates)
        {

            mRequestingLocationUpdates = true;

            // Starting the location updates
            startLocationUpdates();

            Log.d(TAG, "Periodic location updates started!");

        }
        else
        {

            mRequestingLocationUpdates = false;

            // Stopping the location updates
            stopLocationUpdates();

            Log.d(TAG, "Periodic location updates stopped!");
        }
    }

    /**
     Creating google api client object
     */
    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(con).addConnectionCallbacks(this).addOnConnectionFailedListener(this).addApi(LocationServices.API).build();
    }

    /**
     Creating location request object
     */
    protected void createLocationRequest()
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

        displayLocation();
    }

    /**
     Method to verify google play services on the device
     */
    public boolean checkPlayServices()
    {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(con);
        if (resultCode != ConnectionResult.SUCCESS)
        {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode))
            {
                GooglePlayServicesUtil.getErrorDialog(resultCode, con, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }
            else
            {
                Toast.makeText(con, "This device is not supported.", Toast.LENGTH_LONG).show();

            }
            return false;
        }
        return true;
    }

    /**
     Starting the location updates
     */
    public void startLocationUpdates()
    {

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

    }

    /**
     Stopping location updates
     */
    public void stopLocationUpdates()
    {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    /**
     Google api callback methods
     */
    @Override
    public void onConnectionFailed(ConnectionResult result)
    {
        Log.e("hhhhhhhhhhhhhhhhhhhhhhhhhhhhh", "SSSSSSSSSSSS");
        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnected(Bundle arg0)
    {
        // Once connected with google api, get the location
        Log.e("Connected", "Connectedddddddddddddddd");
        /*mLastLocation=null;*/
        displayLocation();
        startLocationUpdates();

    }

    @Override
    public void onConnectionSuspended(int arg0)
    {
        Log.e("COnnnSuspend", "SSSSSSSSSSSS");

        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location)
    {
        // Assign the new location
        mLastLocation = location;
        if (mLastLocation != null)
            listner.LOCATION_NOTIFIER(mLastLocation);
        displayLocation();
        stopLocationUpdates();
    }

}
