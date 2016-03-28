package car.ameba.ridelele.Location;

import android.app.Activity;
import android.location.Location;

import car.ameba.ridelele.utills.ConnectivityDetector;
import car.ameba.ridelele.utills.Utills_G;

/**
 Created by sharan on 3/3/16. */
public class GiveMeLocationS implements Location_Notifier
{
    Activity             activity;
    Location_Interface   location_interface;
    GetCurrentLocation   google_location;
    ConnectivityDetector cd;

    public GiveMeLocationS(Activity activity, Location_Interface location_interface)
    {
        this.activity = activity;
        this.location_interface = location_interface;

        cd = new ConnectivityDetector(activity);

        if (cd.isConnectingToInternet())
        {

            google_location = new GetCurrentLocation(activity);
            google_location.setOnResultsListener(this);

            if (google_location.mGoogleApiClient != null)
            {
                google_location.mGoogleApiClient.connect();

            }

        }
        else
        {
            Utills_G.showToast("No internet connection!", activity, false);
        }
    }

    @Override
    public void LOCATION_NOTIFIER(Location latlng)
    {
        location_interface.onTaskCompleted(latlng);

    }
}
