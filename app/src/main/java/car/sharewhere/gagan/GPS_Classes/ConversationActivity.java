package car.sharewhere.gagan.GPS_Classes;

import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import car.sharewhere.gagan.sharewherecars.R;


public class ConversationActivity extends AppCompatActivity implements  Location_Notifier
{
    ProgressDialogWoody dailog;
    GetCurrentLocation google_location;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        dailog = new ProgressDialogWoody(ConversationActivity.this);

        google_location=new GetCurrentLocation(ConversationActivity.this);
        google_location.setOnResultsListener(this);
        if (google_location.mGoogleApiClient != null)
        {
            google_location.mGoogleApiClient.connect();
            dailog.show();
        }


        setContentView(R.layout.fragment_offer_a_a__ride);


    }



    @Override
    public void LOCATION_NOTIFIER(Location latlng) {
        dailog.hide();
        try {

            Update_Location(latlng.getLatitude(), latlng.getLongitude());
        }
        catch (Exception ex){
            Log.e("Exception is",ex.toString());
        }

    }

    public void Update_Location(Double latitude,Double longitutde){
        dailog.show();



    }

}
