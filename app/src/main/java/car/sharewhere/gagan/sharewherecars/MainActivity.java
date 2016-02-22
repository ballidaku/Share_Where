package car.sharewhere.gagan.sharewherecars;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import car.sharewhere.gagan.WebServices.Asnychronus_notifier;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.WebServices.Json_AsnycTask;
import car.sharewhere.gagan.sharewherecars.fragments.AboutUs;
import car.sharewhere.gagan.sharewherecars.fragments.Find_A_Ride;
import car.sharewhere.gagan.sharewherecars.fragments.My_Rides;
import car.sharewhere.gagan.sharewherecars.fragments.OfferA_a_Ride;
import car.sharewhere.gagan.sharewherecars.fragments.Setting;
import car.sharewhere.gagan.utills.CircleTransform;
import car.sharewhere.gagan.utills.ConnectivityDetector;
import car.sharewhere.gagan.utills.TransitionHelper;
import car.sharewhere.gagan.utills.Utills_G;

public class MainActivity extends AppCompatActivity implements Asnychronus_notifier
{

    DrawerLayout             mDrawerLayout;
    NavigationView           navigationView;
    SharedPreferences        preferences;
    SharedPreferences.Editor editor;

    String pref_photopath, name_header, customerID, mobile_verify_code, gcm_flag, gcm_pic, gcm_leaving_from, gcm_leaving_to, gcm_name, gcm_trip_id, gcm_customer_id, gcm_mobile, gcm_requestID, gcm_reider_id, gcm_driver_id, gcm_message, gcm_lat, gcm_long, gcm_req_or_send, lat_to_send, long_to_send, gcm_driver_rider, city_name_to_send, map_locate_intent_string;

    static ImageView img_header_profile;
    static TextView  txt_header_name;
    ConnectivityDetector cd;
    HashMap<String, String> data_mobile = new HashMap<>();
    View header;
    boolean doubleBackToExitPressedOnce = false;
    String  my_customer_name            = "null";
    Intent            i;
    CoordinatorLayout coordinatorLayout;
    Snackbar          snackbar;

    ArrayList<String> array_auto = new ArrayList<String>();
    ProgressDialog progress;
//    GPSTracker     track;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cd = new ConnectivityDetector(MainActivity.this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        i = getIntent();
//        track = new GPSTracker(MainActivity.this);
        array_auto.add("I have reached");
        array_auto.add("When will you reach here");
        array_auto.add("I will be there in 10 mins");

        /**
         * @DrawerContents
         */

        int                       width  = getResources().getDisplayMetrics().widthPixels / 2;
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        params.width = width;
        navigationView.setLayoutParams(params);
        setupDrawerContent(navigationView);
        header = getLayoutInflater().inflate(R.layout.nav_header, navigationView, false);
        navigationView.addHeaderView(header);

        /**
         * @SharedPref
         */
        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        editor = preferences.edit();

        my_customer_name = preferences.getString("first_name", null);
        pref_photopath = preferences.getString("photo_path", null);
        name_header = preferences.getString("first_name", null);
        customerID = preferences.getString("CustomerId", null);
        mobile_verify_code = preferences.getString("mobile_code", null);
        lat_to_send = preferences.getString("current_lat", "0.0");
        long_to_send = preferences.getString("current_long", "0.0");

        Log.e("LAT", "LLLL" + lat_to_send);
        Log.e("LONG", "LLLL" + long_to_send);

        try
        {
            if(getIntent().getStringExtra(GlobalConstants.KeyNames.fromWhere.toString()).equals(GlobalConstants.KeyNames.Notification.toString()))
            {
                displayView(R.id.nav_offer_ride);
            }
        }
        catch (Exception e)
        {
            displayView(R.id.nav_home);
            e.printStackTrace();
        }

        /**
         * @Customize_HeaderText
         */
        img_header_profile = (ImageView) header.findViewById(R.id.imgProfilePic);
        txt_header_name = (TextView) header.findViewById(R.id.txtvUserName);
        if (name_header != null && !name_header.equals(""))
        {
            txt_header_name.setText(name_header);
        }
        if (pref_photopath != null && !pref_photopath.equals(""))
        {
            Picasso.with(this).load(pref_photopath).transform(new CircleTransform()).into(img_header_profile);
        }
        header.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                transitionToActivity(MainActivity.this, ProfileActivity.class, header.findViewById(R.id.imgProfilePic), header.findViewById(R.id.txtvUserName));
            }
        });

        getIntent_gcm_method();
        city_name_to_send = preferences.getString("current_city", txt_header_name.getText().toString());

        if (gcm_flag != null)
        {
            Log.e("flag", gcm_flag);
            showdialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                hide_keyborad();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener()
        {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem)
            {
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                displayView(menuItem.getItemId());
                return true;
            }
        });
    }

    private void getIntent_gcm_method()
    {
        gcm_flag = getIntent().getStringExtra("customer_flag");
        gcm_pic = getIntent().getStringExtra("customer_pic");
        gcm_name = getIntent().getStringExtra("customer_name");
        gcm_requestID = getIntent().getStringExtra("customer_requestID");
        gcm_trip_id = getIntent().getStringExtra("customer_trip_id_gcm");
        gcm_customer_id = getIntent().getStringExtra("customer_sender_id_gcm");
        gcm_req_or_send = getIntent().getStringExtra("customer_status_lat_long");

        gcm_lat = getIntent().getStringExtra("customer_Latitude");
        gcm_long = getIntent().getStringExtra("customer_Longitude");

        gcm_message = getIntent().getStringExtra("customer_Message");
        gcm_leaving_from = getIntent().getStringExtra("customer_from");
        gcm_leaving_to = getIntent().getStringExtra("customer_to");
        gcm_mobile = getIntent().getStringExtra("customer_mobile");
        gcm_reider_id = getIntent().getStringExtra("customer_rider_id");
        gcm_driver_id = getIntent().getStringExtra("customer_driver_id");
    }

    /**
     @DrawerClasses
     */
    private void displayView(int id)
    {
        Fragment fragment = null;
        switch (id)
        {
            case R.id.nav_home:
                fragment = new OfferA_a_Ride();

                break;

            case R.id.nav_find_ride:
                fragment = new Find_A_Ride();
                break;

            case R.id.nav_offer_ride:
                fragment = new My_Rides();
                break;

            case R.id.nav_settings:
                fragment = new Setting();
                break;

            case R.id.nav_about:
                fragment = new AboutUs();
                break;

           /* case R.id.nav_notify:
                fragment = new NotificationActivity();
                break;*/

            case R.id.nav_logout:

                Utills_G.show_dialog_msg(MainActivity.this, "Do you want to Logout ?", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Utills_G.global_dialog.dismiss();
                        try
                        {
                            FacebookSdk.sdkInitialize(MainActivity.this);
                            LoginManager.getInstance().logOut();

                            preferences.edit().remove("email_id").apply();
                            preferences.edit().remove("first_name").apply();
                            preferences.edit().remove("last_name").apply();
                            preferences.edit().remove("mobile_no").apply();
                            preferences.edit().remove("photo_path").apply();
                            preferences.edit().remove("about_me").apply();
                            preferences.edit().remove("car_info").apply();
                            preferences.edit().remove("photo_path").apply();
                            preferences.edit().remove("CustomerId").apply();
                            preferences.edit().remove("photos").apply();
                            preferences.edit().remove("mobile_verify").apply();
                            preferences.edit().remove("notification").apply();
                            preferences.edit().remove("current_city").apply();
                            preferences.edit().remove("current_lat").apply();
                            preferences.edit().remove("current_long").apply();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        MainActivity.this.finish();
                    }
                });
                break;
            default:
                break;
        }

        if (fragment != null)
        {
            android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_container, fragment);
            fragmentTransaction.commit();
        }
    }

    /**
     @OnResumeMdoabiileValioobiion
     */
    @Override
    protected void onResume()
    {
        super.onResume();
       /* if (name_header != null && !name_header.equals("") || name_header != str_null) {
            txt_header_name.setText(name_header);
        }
        if (pref_photopath != null && !pref_photopath.equals("") || pref_photopath != str_null) {
           *//* Picasso.with(this).load(pref_photopath).transform(new CircleTransform()).
                    into(img_header_profile);*//*
        }*/
    }

    private void transitionToActivity(Activity activity, Class target, View profilePic, View profileName)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false, new Pair<>(profilePic, activity.getString(R.string.profile_pic_transition)), new Pair<>(profileName, activity.getString(R.string.profile_name_transition)));
            startActivityG(activity, target, pairs);
        }
        else
        {
            startActivity(new Intent(activity, target));
        }
    }

    private void startActivityG(Activity activity, Class target, Pair<View, String>[] pairs)
    {
        Intent                i                         = new Intent(activity, target);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);
        activity.startActivity(i, transitionActivityOptions.toBundle());
    }

    /**
     @BackPress
     */
    @Override
    public void onBackPressed()
    {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START))
        {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        }
        else
        {
            if (doubleBackToExitPressedOnce)
            {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press Back again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable()
            {

                @Override
                public void run()
                {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }

    }

    /**
     @HitService(Accept/Decline)
     */
    private void HitServiceGCM(String Flag)
    {
        if (!cd.isConnectingToInternet())
        {
            snackbar_method("No internet connection!");
        }
        else
        {
            Log.e("requestID", gcm_requestID);
            data_mobile.put("TripId", gcm_trip_id);
            data_mobile.put("CustomerId", gcm_customer_id);
            data_mobile.put("RequestType", Flag);
            data_mobile.put("IsRegulerBasis", "false");
            data_mobile.put("RoundTrip", "false");
            data_mobile.put("RequestId", gcm_requestID);

            Json_AsnycTask task = new Json_AsnycTask(MainActivity.this, "http://112.196.34.42:9091/Trip/TripRequest", GlobalConstants.POST_SERVICE_METHOD1, data_mobile);
            task.setOnResultsListener(this);
            task.execute();
            progress = ProgressDialog.show(MainActivity.this, "", "Sending Request. Please wait...", true);
            progress.setCancelable(true);
            progress.show();
        }
    }

    /**
     @HitService(Adress/Message)
     */
    private void HitService_Adress_message(String adres_lat, String adres_long, String rider_id, String DriverId, String TripId, String Message, String Flag, String Status)
    {

        data_mobile.clear();
        if (!cd.isConnectingToInternet())
        {
            snackbar_method("No internet connection!");
        }
        else
        {
            data_mobile.put("RiderId", rider_id);
            data_mobile.put("DriverId", DriverId);
            data_mobile.put("TripId", TripId);
            data_mobile.put("Message", Message);
            data_mobile.put("Latitude", adres_lat);
            data_mobile.put("Longitude", adres_long);
            data_mobile.put("Flag", Flag);
            data_mobile.put("Status", Status);
            data_mobile.put("RequestId", gcm_requestID);
            Json_AsnycTask task = new Json_AsnycTask(MainActivity.this, "http://112.196.34.42:9091/Trip/LocationShare", GlobalConstants.POST_SERVICE_METHOD2, data_mobile);
            task.setOnResultsListener(this);
            task.execute();
            progress = ProgressDialog.show(MainActivity.this, "", "Sending... Please wait...", true);
            progress.setCancelable(true);
            progress.show();
        }
    }

    @Override
    public void onResultsSucceeded_Get_Method1(JSONObject result)
    {

    }

    @Override
    public void onResultsSucceeded_Get_Method2(JSONObject result)
    {

    }

    @Override
    public void onResultsSucceeded_Post_Method1(JSONObject result)
    {
        progress.dismiss();
        Log.e("response noti..", result.toString());
    }

    @Override
    public void onResultsSucceeded_Post_Method2(JSONObject result)
    {
        Log.e("response noti..lat/long", result.toString());
        progress.dismiss();
        Log.e("response noti..lat/long", result.toString());
    }

    @Override
    public void onResultsSucceeded_Post_Method3(JSONObject result)
    {

    }

    /**
     @Dialog
     */

    public void showdialog()
    {

        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_ride_details);
        if (gcm_flag.equals("Rider") || gcm_flag.equals("Driver"))
        {
            if (gcm_req_or_send.equals("msg"))
            {
                dialog.setCancelable(true);
            }
            else
            {
                dialog.setCancelable(false);
            }
        }
        else
        {
            dialog.setCancelable(false);
        }

        TextView  txt_txt_driver_name = (TextView) dialog.findViewById(R.id.txt_driver_name);
        TextView  txt_from            = (TextView) dialog.findViewById(R.id.txt_from);
        TextView  txt_to              = (TextView) dialog.findViewById(R.id.txt_to);
        TextView  txt_chat            = (TextView) dialog.findViewById(R.id.txt_chat);
        TextView  txt_cancel          = (TextView) dialog.findViewById(R.id.txt_cancel);
        ImageView img_driver_img      = (ImageView) dialog.findViewById(R.id.img_driver_img);

        final Button btn_one   = (Button) dialog.findViewById(R.id.btn_one);
        final Button btn_two   = (Button) dialog.findViewById(R.id.btn_two);
        final Button btn_three = (Button) dialog.findViewById(R.id.btn_three);

//        RelativeLayout             rel_click           = (RelativeLayout) dialog.findViewById(R.id.rel_click);
        RelativeLayout             rel_Top_cancel      = (RelativeLayout) dialog.findViewById(R.id.rel_Top_cancel);
        ImageView             img_phn           = (ImageView) dialog.findViewById(R.id.img_phn);
        final AutoCompleteTextView dialog_autocomplete = (AutoCompleteTextView) dialog.findViewById(R.id.dialog_autocomplete);

        txt_cancel.setPaintFlags(txt_cancel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

       /* ArrayAdapter<String> autocompletetextAdapter = new ArrayAdapter<String>(
                MainActivity.this,
                R.layout.layout_autocomplete, array_auto);
        dialog_autocomplete.setAdapter(autocompletetextAdapter);
        dialog_autocomplete.showDropDown();*/

        txt_cancel.setVisibility(View.GONE);
        txt_chat.setVisibility(View.GONE);
        dialog_autocomplete.setVisibility(View.GONE);
        btn_three.setVisibility(View.GONE);
        try
        {
            if (gcm_pic != null)
            {
                Picasso.with(MainActivity.this).load(gcm_pic).transform(new CircleTransform()).into(img_driver_img);
            }
        }
        catch (Exception ex)
        {

        }
        if (gcm_name != null)
        {
            txt_txt_driver_name.setText(gcm_name);
        }
        if (gcm_leaving_to != null)
        {
            txt_to.setText(gcm_leaving_to);
        }
        if (gcm_leaving_from != null)
        {
            txt_from.setText(gcm_leaving_from);
        }
        if (gcm_message != null)
        {
            if (gcm_message.trim().length() > 0)
            {
                txt_chat.setVisibility(View.VISIBLE);
                txt_chat.setText(gcm_message);
            }
        }

        if (gcm_flag != null)
        {
            Log.e("flag=", gcm_flag);

          /*  if (gcm_flag.equals("5"))
            {
                //New Request
                btn_one.setText("Accept");
                btn_two.setText("Decline");
            }
            if (gcm_flag.equals("2"))
            {
                //Request Declined
                txt_chat.setVisibility(View.VISIBLE);
                btn_two.setVisibility(View.GONE);
                txt_chat.setText("Your request declined");
                btn_one.setText("OK");

            }
            if (gcm_flag.equals("1"))
            {
                //Request Accepted
                Toast.makeText(MainActivity.this, "Your request Accepted", Toast.LENGTH_LONG).show();
                txt_cancel.setVisibility(View.VISIBLE);
                dialog_autocomplete.setVisibility(View.VISIBLE);
                dialog_autocomplete.setThreshold(1);

                btn_one.setText("Ask location on map");
                btn_two.setText("Where are you?");

            }
            if (gcm_flag.equals("6") || gcm_flag.equals("7"))
            {
                //Request Cancelled
                btn_two.setVisibility(View.GONE);
                txt_chat.setText("Your request is cancelled");
                btn_one.setText("OK");
            }*/
            if (gcm_flag.equals("8"))
            {
                //Request Cancelled
                btn_two.setVisibility(View.GONE);
                txt_chat.setText("Trip Deleted");
                btn_one.setText("OK");
            }
            if (gcm_flag.equals("Driver") || gcm_flag.equals("Rider"))
            {
                //lat/long
                txt_cancel.setVisibility(View.VISIBLE);
                if (gcm_flag.equals("Driver"))
                {
                    gcm_driver_rider = "CancelByDriver";
                }
                else
                {
                    gcm_driver_rider = "CancelByRider";
                }

                if (gcm_req_or_send != null)
                {
                    if (gcm_req_or_send.equals("request"))
                    {
                        btn_two.setVisibility(View.VISIBLE);
                        txt_chat.setVisibility(View.VISIBLE);
                        txt_chat.setText("Requested you to send your current location");
                        btn_one.setText("SEND");
                        btn_two.setText("Decline Request");
                    }
                    else if (gcm_req_or_send.contains("send"))
                    {
                        String[] separated = gcm_req_or_send.split(":");
                        map_locate_intent_string = separated[1];
                        Log.e("MapMap00000", "" + map_locate_intent_string);
                        btn_three.setVisibility(View.VISIBLE);
                        btn_two.setVisibility(View.VISIBLE);
                        txt_chat.setVisibility(View.GONE);
                        btn_one.setText("Ask location on map");
                        btn_two.setText("Where are you?");
                    }
                    else if (gcm_req_or_send.equals("msg"))
                    {
                        btn_three.setVisibility(View.GONE);
                        btn_two.setVisibility(View.VISIBLE);
                        txt_chat.setVisibility(View.VISIBLE);
                        txt_chat.setText(gcm_message);
                        dialog_autocomplete.setVisibility(View.VISIBLE);
                        btn_one.setText("Ask location on map");
                        btn_two.setText("Where are you?");
                    }
                }
            }
        }

        rel_Top_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (gcm_driver_rider != null)
                {
                    if (gcm_flag.equals("Driver"))
                    {
                        HitServiceGCM("CancelByDriver");
                    }
                    else if (gcm_flag.equals("Rider"))
                    {
                        HitServiceGCM("CancelByRider");
                    }
                }
                else
                {
                    HitServiceGCM("CancelByRider");
                }
                dialog.dismiss();

            }
        });

        img_phn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (gcm_mobile != null)
                {
                    ///call number
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + gcm_mobile));
                    try
                    {
                        startActivity(intent);
                    }
                    catch (android.content.ActivityNotFoundException ex)
                    {
                        ex.printStackTrace();
                    }
                    //                    dialog.dismiss();
                }
            }
        });

        btn_three.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                Intent i = new Intent(MainActivity.this, MapActivity.class);
                i.putExtra("title", map_locate_intent_string);
                i.putExtra("latitude_", gcm_lat);
                i.putExtra("longitude_", gcm_long);

                startActivity(i);
                dialog.dismiss();
            }
        });

        btn_one.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (btn_one.getText().toString().equals("Accept"))
                {
                    HitServiceGCM("Accept");
                    dialog.dismiss();
                }
                else if (btn_one.getText().toString().equals("Ask location on map"))
                {
                    if (gcm_flag.equals("1"))
                    {
                        HitService_Adress_message(lat_to_send, long_to_send, gcm_reider_id, gcm_driver_id, gcm_trip_id, my_customer_name + " wants to locate you on map", "Driver", "request");
                    }
                    else if (gcm_flag.equals("Driver"))
                    {
                        HitService_Adress_message(lat_to_send, long_to_send, gcm_reider_id, gcm_driver_id, gcm_trip_id, my_customer_name + " wants to locate you on map", "Rider", "request");
                    }
                    else if (gcm_flag.equals("Rider"))
                    {
                        HitService_Adress_message(lat_to_send, long_to_send, gcm_reider_id, gcm_driver_id, gcm_trip_id, my_customer_name + " wants to locate you on map", "Driver", "request");
                    }
                    dialog.dismiss();
                }
                else if (btn_one.getText().toString().equals("Requested you to send location"))
                {
                    Toast.makeText(MainActivity.this, "Tap button below to send location", Toast.LENGTH_LONG).show();
                    if (gcm_flag.equals("Driver"))
                    {
                        HitService_Adress_message(lat_to_send, long_to_send, gcm_reider_id, gcm_driver_id, gcm_trip_id, "Send to Rider", "Rider", "send" + ":" + city_name_to_send);
                    }
                    else
                    {
                        HitService_Adress_message(lat_to_send, long_to_send, gcm_reider_id, gcm_driver_id, gcm_trip_id, "Send to Driver", "Driver", "send" + ":" + city_name_to_send);
                    }
                    dialog.dismiss();
                }
                else if (btn_one.getText().toString().equals("OK"))
                {
                    dialog.dismiss();
                }
                else if (btn_one.getText().toString().equals("SEND"))
                {
                    String current_lat = preferences.getString("current_lat", "0.0");
                    String current_long = preferences.getString("current_long", "0.0");
                    if (current_long.equals("0.0") || current_lat.equals("0.0"))
                    {
                        Toast.makeText(MainActivity.this, "Not able fetch your current location, enable gps if it is not on!!", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        if (gcm_flag.equals("Driver"))
                        {
                            HitService_Adress_message(current_lat, current_long, gcm_reider_id, gcm_driver_id, gcm_trip_id, my_customer_name + " sends location, locate me on map", "Rider", "send" + ":" + city_name_to_send);
                        }
                        else
                        {
                            HitService_Adress_message(lat_to_send, long_to_send, gcm_reider_id, gcm_driver_id, gcm_trip_id, my_customer_name + " sends location, locate me on map", "Driver", "send" + ":" + city_name_to_send);
                        }
                        dialog.dismiss();
                    }
                }
            }
        });

        btn_two.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (btn_two.getText().toString().equals("Decline"))
                {
                    HitServiceGCM("Decline");
                    dialog.dismiss();
                }
                else if (btn_two.getText().toString().equals("Decline Request"))
                {
                    Toast.makeText(MainActivity.this, "You can request again", Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
                else if (btn_two.getText().toString().equals("Where are you?"))
                {
                    if (dialog_autocomplete.getVisibility() == View.VISIBLE)
                    {
                        // Its visible
                    }
                    else
                    {
                        dialog_autocomplete.setVisibility(View.VISIBLE);
                        dialog_autocomplete.requestFocus();
                        dialog_autocomplete.setSelection(dialog_autocomplete.length());
                        dialog_autocomplete.setError("Enter comment first");
                    }
                    if (dialog_autocomplete.getText().toString().trim().length() > 0)
                    {
                        if (gcm_flag.equals("Driver"))
                        {
                            HitService_Adress_message(lat_to_send, long_to_send, gcm_reider_id, gcm_driver_id, gcm_trip_id, dialog_autocomplete.getText().toString().trim(), "Rider", "msg");
                        }
                        else
                        {
                            HitService_Adress_message(lat_to_send, long_to_send, gcm_reider_id, gcm_driver_id, gcm_trip_id, dialog_autocomplete.getText().toString().trim(), "Driver", "msg");
                        }

                        dialog.dismiss();
                    }
                    else
                    {
                        dialog_autocomplete.requestFocus();
                        dialog_autocomplete.setSelection(dialog_autocomplete.length());
                        dialog_autocomplete.setError("Enter comment first");
                    }
                }
            }

        });
        dialog.show();
    }

    ///Dialog end/////////////

    public void hide_keyborad()
    {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(img_header_profile.getWindowToken(), 0);
    }

    /**
     @snackbarMethod
     */
    private void snackbar_method(String text)
    {
        snackbar = Snackbar.make(coordinatorLayout, text, Snackbar.LENGTH_LONG).setAction("Ok", new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(Color.RED);
        View     sbView   = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
