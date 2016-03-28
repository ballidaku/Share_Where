package car.ameba.ridelele.sharewherecars;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import car.ameba.ridelele.Async_Thread.Super_AsyncTask;
import car.ameba.ridelele.Async_Thread.Super_AsyncTask_Interface;
import car.ameba.ridelele.Location.GiveMeLocationS;
import car.ameba.ridelele.Location.Location_Interface;
import car.ameba.ridelele.WebServices.Asnychronus_notifier;
import car.ameba.gagan.sharewherecars.R;
import car.ameba.ridelele.utills.GlobalConstants;
import car.ameba.ridelele.WebServices.Json_AsnycTask;
import car.ameba.ridelele.sharewherecars.fragments.AboutUs;
import car.ameba.ridelele.sharewherecars.fragments.Find_A_Ride;
import car.ameba.ridelele.sharewherecars.fragments.My_Rides;
import car.ameba.ridelele.sharewherecars.fragments.OfferA_a_Ride;
import car.ameba.ridelele.sharewherecars.fragments.Setting;
import car.ameba.ridelele.utills.CircleTransform;
import car.ameba.ridelele.utills.ConnectivityDetector;
import car.ameba.ridelele.utills.Dialogs;
import car.ameba.ridelele.utills.Utills_G;

public class MainActivity extends AppCompatActivity implements Asnychronus_notifier
{

    DrawerLayout      mDrawerLayout;
    NavigationView    navigationView;
    SharedPreferences preferences;

    String pref_photopath, name_header, customerID, mobile_verify_code, gcm_flag, gcm_pic, gcm_leaving_from, gcm_leaving_to, gcm_name, gcm_trip_id, gcm_customer_id, gcm_mobile, gcm_requestID, gcm_reider_id, gcm_driver_id, gcm_message, gcm_lat, gcm_long, gcm_driver_rider, city_name_to_send, map_locate_intent_string;

    static ImageView img_header_profile;
    static TextView  txt_header_name;
    ConnectivityDetector cd;
    HashMap<String, String> data_mobile = new HashMap<>();
    View header;
    boolean doubleBackToExitPressedOnce = false;
    String  my_customer_name            = "null";
    CoordinatorLayout coordinatorLayout;
    Snackbar          snackbar;

    ProgressDialog progress;

    //    Utills_G utills_g = new Utills_G();

    //**************************Sharan*****************************************************

    Context con;

    String status = "";

    LocationManager locationManager;

    Dialogs         dialogs         = new Dialogs();
    GlobalConstants globalConstants = new GlobalConstants();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        con = this;

        cd = new ConnectivityDetector(MainActivity.this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);



        int                       width  = getResources().getDisplayMetrics().widthPixels / 2;
        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        params.width = width + (width / 2);
        navigationView.setLayoutParams(params);
        setupDrawerContent(navigationView);
        header = getLayoutInflater().inflate(R.layout.nav_header, navigationView, false);
        navigationView.addHeaderView(header);


        preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        my_customer_name = preferences.getString("first_name", null);
        pref_photopath = preferences.getString("photo_path", null);
        name_header = preferences.getString("first_name", null);
        customerID = preferences.getString("CustomerId", null);
        mobile_verify_code = preferences.getString("mobile_code", null);

        try
        {
            if (getIntent().getStringExtra(GlobalConstants.KeyNames.fromWhere.toString()).equals(GlobalConstants.KeyNames.Notification.toString()) || getIntent().getStringExtra(GlobalConstants.KeyNames.fromWhere.toString()).equals(GlobalConstants.KeyNames.Messages.toString()))
            {
                displayView(R.id.nav_offer_ride);
            }
        }
        catch (Exception e)
        {
            displayView(R.id.nav_home);
            e.printStackTrace();
        }


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
                mDrawerLayout.closeDrawers();
            }
        });

        getIntent_gcm_method();
        city_name_to_send = preferences.getString("current_city", txt_header_name.getText().toString());

        if (gcm_flag != null &&
                  !status.equalsIgnoreCase("msg") &&
                  !gcm_flag.equalsIgnoreCase("5"))
        {
            Log.e("flag", gcm_flag);
            showdialog();
        }

        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener()
        {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset)
            {

            }

            @Override
            public void onDrawerOpened(View drawerView)
            {
                //                utills_g.hide_keyboard(con);
                hide_keyborad();
            }

            @Override
            public void onDrawerClosed(View drawerView)
            {
                //                utills_g.hide_keyboard(con);
                hide_keyborad();
            }

            @Override
            public void onDrawerStateChanged(int newState)
            {

            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //        utills_g.hide_keyboard(con);
        //        hide_keyborad();
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

        gcm_customer_id = getIntent().getStringExtra("customer_sender_id_gcm");

        gcm_lat = getIntent().getStringExtra("customer_Latitude");
        gcm_long = getIntent().getStringExtra("customer_Longitude");

        gcm_message = getIntent().getStringExtra("customer_Message");
        gcm_leaving_from = getIntent().getStringExtra("customer_from");
        gcm_leaving_to = getIntent().getStringExtra("customer_to");
        gcm_mobile = getIntent().getStringExtra("customer_mobile");
        gcm_reider_id = getIntent().getStringExtra("customer_rider_id");
        gcm_driver_id = getIntent().getStringExtra("customer_driver_id");

        gcm_trip_id = getIntent().getStringExtra(GlobalConstants.KeyNames.TripId.toString());
        status = getIntent().getStringExtra(GlobalConstants.KeyNames.Status.toString());
    }

    /**
     @DrawerClasses
     */
    public void displayView(int id)
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

            case R.id.nav_offer_ride: // name galat aa
                navigationView.getMenu().getItem(2).setChecked(true);
                fragment = new My_Rides();
                break;

            case R.id.nav_settings:
                fragment = new Setting();
                break;

            case R.id.nav_about:
                fragment = new AboutUs();
                break;

            case R.id.nav_logout:

                Utills_G.show_dialog_msg(MainActivity.this, "Do you want to Logout ?", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        Utills_G.global_dialog.dismiss();

                        HitSignOut_Request();
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

    private void transitionToActivity(Activity activity, Class target, View profilePic, View profileName)
    {
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false, new Pair<>(profilePic, activity.getString(R.string.profile_pic_transition)), new Pair<>(profileName, activity.getString(R.string.profile_name_transition)));
            startActivityG(activity, target, pairs);
        }
        else
        {*/
            startActivity(new Intent(activity, target));
//        }
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

            Json_AsnycTask task = new Json_AsnycTask(MainActivity.this, GlobalConstants.Url + "Trip/TripRequest", GlobalConstants.POST_SERVICE_METHOD1, data_mobile);
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
            Json_AsnycTask task = new Json_AsnycTask(MainActivity.this, GlobalConstants.Url + "Trip/LocationShare", GlobalConstants.POST_SERVICE_METHOD2, data_mobile);
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
        //        Log.e("response noti..lat/long", result.toString());
        progress.dismiss();
        Log.e("response noti..lat/long", result.toString());
    }

    @Override
    public void onResultsSucceeded_Post_Method3(JSONObject result)
    {

    }

    @Override
    public void onResultsSucceeded_Post_Method4(JSONObject result)
    {

    }

    @Override
    public void onResultsSucceeded_Post_Method5(JSONObject result)
    {

    }

    /**
     @Dialog
     */

    Dialog dialog;

    public void showdialog()
    {

        dialog = new Dialog(MainActivity.this);
        dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog.setContentView(R.layout.custom_ride_details);

        dialog.setCancelable(false);

        TextView txt_txt_driver_name = (TextView) dialog.findViewById(R.id.txt_driver_name);
        TextView txt_from            = (TextView) dialog.findViewById(R.id.txt_from);
        TextView txt_to              = (TextView) dialog.findViewById(R.id.txt_to);
        TextView txt_chat            = (TextView) dialog.findViewById(R.id.txt_chat);
        TextView txt_cancel          = (TextView) dialog.findViewById(R.id.txt_cancel);
        dialog.findViewById(R.id.txtv_message_count).setVisibility(View.GONE);
        ImageView img_driver_img = (ImageView) dialog.findViewById(R.id.img_driver_img);

        final TextView btn_one   = (TextView) dialog.findViewById(R.id.btn_one);
        final TextView btn_two   = (TextView) dialog.findViewById(R.id.btn_two);
        final TextView btn_three = (TextView) dialog.findViewById(R.id.btn_three);

        final RelativeLayout rel_decline = (RelativeLayout) dialog.findViewById(R.id.rel_decline);

        ImageView img_phn = (ImageView) dialog.findViewById(R.id.img_phn);

        txt_cancel.setPaintFlags(txt_cancel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        txt_cancel.setVisibility(View.GONE);
        txt_chat.setVisibility(View.GONE);
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

            if (gcm_flag.equals("8"))
            {
                //Request Cancelled
                rel_decline.setVisibility(View.GONE);
                txt_chat.setText(gcm_message);
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

                if (status != null)
                {
                    if (status.equals("request"))
                    {
                        rel_decline.setVisibility(View.VISIBLE);
                        txt_chat.setVisibility(View.VISIBLE);
                        txt_chat.setText("Requested you to send your current location.");

                        //                        btn_one.setText("SEND");
                        //                        btn_two.setText("Decline Request");

                        btn_one.setText("Decline Request");
                        btn_two.setText("Send");
                    }
                    else if (status.contains("send"))
                    {
                        String[] separated = status.split(":");
                        map_locate_intent_string = separated[1];
                        Log.e("MapMap00000", "" + map_locate_intent_string);

                        btn_one.setVisibility(View.GONE);
                        rel_decline.setVisibility(View.GONE);
                        btn_three.setVisibility(View.VISIBLE);

                        txt_chat.setText(gcm_message);

                        //                        btn_one.setText("Ask location on map");
                        //                        btn_two.setText("Message");
                    }

                }
            }
        }

        txt_cancel.setOnClickListener(new View.OnClickListener()
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
                        HitService_Adress_message("0.0", "0.0", gcm_reider_id, gcm_driver_id, gcm_trip_id, my_customer_name + " wants to locate you on map", "Driver", "request");
                    }
                    else if (gcm_flag.equals("Driver"))
                    {
                        HitService_Adress_message("0.0", "0.0", gcm_reider_id, gcm_driver_id, gcm_trip_id, my_customer_name + " wants to locate you on map", "Rider", "request");
                    }
                    else if (gcm_flag.equals("Rider"))
                    {
                        HitService_Adress_message("0.0", "0.0", gcm_reider_id, gcm_driver_id, gcm_trip_id, my_customer_name + " wants to locate you on map", "Driver", "request");
                    }
                    dialog.dismiss();
                }
                else if (btn_one.getText().toString().equals("Requested you to send location"))
                {
                    Toast.makeText(MainActivity.this, "Tap button below to send location", Toast.LENGTH_LONG).show();
                    if (gcm_flag.equals("Driver"))
                    {
                        HitService_Adress_message("0.0", "0.0", gcm_reider_id, gcm_driver_id, gcm_trip_id, "Send to Rider", "Rider", "send" + ":" + city_name_to_send);
                    }
                    else
                    {
                        HitService_Adress_message("0.0", "0.0", gcm_reider_id, gcm_driver_id, gcm_trip_id, "Send to Driver", "Driver", "send" + ":" + city_name_to_send);
                    }
                    dialog.dismiss();
                }
                else if (btn_one.getText().toString().equals("Decline Request"))
                {
                    dialog.dismiss();
                }
                else if (btn_one.getText().toString().equals("OK"))
                {
                    dialog.dismiss();
                }

            }
        });

        rel_decline.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (btn_two.getText().toString().equals("Decline"))
                {
                    HitServiceGCM("Decline");
                    dialog.dismiss();
                }

                else if (btn_two.getText().toString().equals("Send"))
                {

                    check_GPS();

                    //                    GetLocation();
/*
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
                            HitService_Adress_message(current_lat, current_long, gcm_reider_id, gcm_driver_id, gcm_trip_id, my_customer_name + " sends location, locate me on map", "Driver", "send" + ":" + city_name_to_send);
                        }
                        dialog.dismiss();
                    }*/
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

    //*************************************************  Check GPS  ***************************************************

    public void check_GPS()
    {
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            dialogs.turnGPSOn(con);
            //  return;
        }
        else
        {
            if (cd.isConnectingToInternet())
            {
                GetLocation();
            }
            else
            {
                Utills_G.showToast("Internet connection seems to be disable.", con, false);
            }
        }

    }

    //*********************************************** Fetching and sending location ****************************************************

    ProgressDialog fetching_location_dialog;

    private void GetLocation()
    {

        fetching_location_dialog = ProgressDialog.show(con, "", "Fetching your location.Please wait...", true);

        new GiveMeLocationS(MainActivity.this, new Location_Interface()
        {
            @Override
            public void onTaskCompleted(Location location)
            {
                try
                {

                    if (fetching_location_dialog.isShowing())
                    {
                        fetching_location_dialog.dismiss();
                    }

                    Log.e("Sharan Latitude", "" + location.getLatitude());
                    Log.e("Sharan Longitude", "" + location.getLongitude());

                    String lat = String.valueOf(location.getLatitude());
                    String lon = String.valueOf(location.getLongitude());

                    if (gcm_flag.equals("Driver"))
                    {
                        HitService_Adress_message(lat, lon, gcm_reider_id, gcm_driver_id, gcm_trip_id, my_customer_name + " sends location, locate me on map", "Rider", "send" + ":" + city_name_to_send);
                    }
                    else
                    {
                        HitService_Adress_message(lat, lon, gcm_reider_id, gcm_driver_id, gcm_trip_id, my_customer_name + " sends location, locate me on map", "Driver", "send" + ":" + city_name_to_send);
                    }
                    dialog.dismiss();

                }
                catch (Exception ex)
                {
                    Log.e("Exception is", ex.toString());
                }
            }
        });

    }

    //*************************************************** SignOut ******************************************************

    public void HitSignOut_Request()
    {

        String url = GlobalConstants.Url + "Customer/SignOut?CustomerId=" + customerID;

        GlobalConstants.execute(new Super_AsyncTask(con, url, new Super_AsyncTask_Interface()
        {
            @Override
            public void onTaskCompleted(String output)
            {
                try
                {

                    JSONObject object = new JSONObject(output);
                    if (object.getString("Status").equalsIgnoreCase("success"))
                    {

                        Log.e("SignOut", "" + output);




                        try
                        {
                            FacebookSdk.sdkInitialize(MainActivity.this);
                            LoginManager.getInstance().logOut();

                            //                            preferences.edit().clear().apply();

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

                        globalConstants.logout(con);
                        MainActivity.this.finish();

                    }
                    else
                    {
                        GlobalConstants.show_Toast(object.getString("Message"), con);
                    }
                }
                catch (Exception ex)
                {
                    Log.e("Exception is", ex.toString());
                }
            }
        }, true));

    }

    //**********************************************************************************************************************

}
