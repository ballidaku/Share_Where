package car.sharewhere.gagan.sharewherecars;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by ameba on 5/1/16.
 */
public class MapActivity extends AppCompatActivity {
    static final LatLng TutorialsPoint = new LatLng(21, 57);
    private GoogleMap googleMap;
    double latitude;
    double longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_activity);
        setActionBar();

        Log.e("Map actvitiy","MMMMMMMMMMMMM");
        //====Get Intent ======
        Intent i = getIntent();
        final String title=i.getStringExtra("title");
        String lat=i.getStringExtra("latitude_");
        String longi=i.getStringExtra("longitude_");
        latitude=Double.parseDouble(lat);
        longitude=Double.parseDouble(longi);
        Log.e("Map Title","MMMMMMMMMMMMM"+title);
        //=======================
        try {
            if (googleMap == null) {
                googleMap = ((MapFragment) getFragmentManager().
                        findFragmentById(R.id.map)).getMap();
            }
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
           googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
               View markerView = null;

               @Override
               public View getInfoWindow(Marker marker) {
                   markerView = getLayoutInflater().inflate(
                           R.layout.marker_layout, null);
                   TextView tvTitle = ((TextView) markerView
                           .findViewById(R.id.tv_title));
                   tvTitle.setText(title);
                   return markerView;
               }

               @Override
               public View getInfoContents(Marker marker) {
                   return null;
               }
           });

            MarkerOptions marker = new MarkerOptions().position(new LatLng(latitude, longitude));

             googleMap.addMarker(marker).showInfoWindow();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude,longitude), 15);

            googleMap.moveCamera(cameraUpdate);
        } catch (Exception e) {
            Toast.makeText(MapActivity.this, "Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
        }
    }

    private void setActionBar() {
        try {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            TextView txt_toolbar = (TextView) toolbar.findViewById(R.id.txt_titleTV);
            txt_toolbar.setText("Location");
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                    supportFinishAfterTransition();
                } else {
                    MapActivity.this.finish();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}