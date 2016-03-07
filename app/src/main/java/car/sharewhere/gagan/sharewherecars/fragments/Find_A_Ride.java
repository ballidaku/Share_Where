package car.sharewhere.gagan.sharewherecars.fragments;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import car.sharewhere.gagan.Async_Thread.Super_AsyncTask;
import car.sharewhere.gagan.Async_Thread.Super_AsyncTask_Interface;
import car.sharewhere.gagan.Location.GetCurrentLocation;
import car.sharewhere.gagan.Location.Location_Notifier;
import car.sharewhere.gagan.WebServices.GetCityHelperG_;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.model.Latlng_data;
import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.sharewherecars.Ride_Details;
import car.sharewhere.gagan.utills.AppLocationService;
import car.sharewhere.gagan.utills.Bean_Ride_Details;
import car.sharewhere.gagan.utills.CircleTransform;
import car.sharewhere.gagan.utills.ConnectivityDetector;
import car.sharewhere.gagan.utills.Utills_G;

public class Find_A_Ride extends FragmentG implements View.OnClickListener  , Location_Notifier
{
    TextView             txt_date;
    ImageView            img_date;
    AutoCompleteTextView autocompleteTo, autocompleteFrom;
    ListView             list_find_ride;
    FloatingActionButton fab_search;

    Calendar c;
    int      mYear, mMonth, mDay;

    String get_photos_pref, photos_show, current_date, customername, customerID, format, current_time;

    ConnectivityDetector cd;
    SharedPreferences    preferences;
//    ProgressDialog       dialog;
    Snackbar             snackbar;
    CoordinatorLayout    coordinatorLayout;
    AppLocationService   appLocationService;

    GetCityHelperG_ GET_CITY_G;
    String strng_lat_from  = "0.0";
    String strng_lat_to    = "0.0";
    String strng_long_from = "0.0";
    String strng_long_to   = "0.0";
    View view;

    MidPointAdapter adater;
    String          my_customerID;


    private GetCurrentLocation  google_location;

    public Find_A_Ride()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        view = inflater.inflate(R.layout.test_find, container, false);

        setActionBar(view, "Find a Ride");
        findViewbyID(view);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        my_customerID = preferences.getString("CustomerId", null);
        //        my_customer_name = preferences.getString("first_name", null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        /**
         * @initialisations
         */

        c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        String dt = mYear + "-" + mMonth + "-" + mDay;
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Set your date format
            Date currentDt = null;
            currentDt = sdf.parse(dt);
            current_date = sdf.format(currentDt);
        }
        catch (Exception ex)
        {

        }

        cd = new ConnectivityDetector(getActivity());
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        get_photos_pref = preferences.getString("photos", null);
        customerID = preferences.getString("CustomerId", null);
        customername = preferences.getString("first_name", null);

        if (get_photos_pref == null)
        {
            photos_show = "yes";
        }
        else if (get_photos_pref.equals("yes"))
        {
            photos_show = "yes";
        }
        else if (get_photos_pref.equals("no"))
        {
            photos_show = "no";
        }

        appLocationService = new AppLocationService(getActivity());

        google_location = new GetCurrentLocation(getActivity());
        google_location.setOnResultsListener(this);
        if (cd.isConnectingToInternet() && google_location.mGoogleApiClient != null)
        {
            google_location.mGoogleApiClient.connect();

        }
        else
        {
            Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_LONG).show();
        }

        return view;
    }

    @Override
    public void LOCATION_NOTIFIER(Location latlng)
    {
         String address_current = "";
         String lat, longitude;

        if (cd.isConnectingToInternet())
        {

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
//            Log.e("location details are ", "latitude   " + latlng.getLatitude() + "  longitutde " + latlng.getLongitude());

        }
        else
        {
            Toast.makeText(getActivity(), "No internet connection!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.img_date_cancel:

                txt_date.setText("");

                break;

            case R.id.llay_date:

                Calendar c = Calendar.getInstance();
                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(getActivity(), new mDateSetListener_depart(), mYear, mMonth, mDay);
                dialog.show();

                break;
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();

        String current_city = preferences.getString("current_city", null);
        String current_lat  = preferences.getString("current_lat", null);
        String current_long = preferences.getString("current_long", null);
        if (current_city != null)
        {
            if (current_city.length() > 0)
            {
                autocompleteFrom.setText(current_city);
                strng_lat_from = current_lat;
                strng_long_from = current_long;
                autocompleteFrom.setSelection(autocompleteFrom.getText().toString().length());
            }
        }

        if (autocompleteTo.getText().toString().length() < 1)
        {
            HitService_ByLat_Long();
        }

        method_current_time();
    }

    private void method_current_time()
    {
        StringBuilder buildr_curnt_time;
        Calendar      cal    = Calendar.getInstance();
        int           minute = cal.get(Calendar.MINUTE);
        int           hour   = cal.get(Calendar.HOUR_OF_DAY);
        if (hour == 0)
        {
            hour += 12;
            format = "AM";
        }
        else if (hour == 12)
        {
            format = "PM";
        }
        else if (hour > 12)
        {
            hour -= 12;
            format = "PM";
        }
        else
        {
            format = "AM";
        }
        int length = (int) (Math.log10(minute) + 1);
        if (length == 1)
        {
            buildr_curnt_time = new StringBuilder().append(hour).append(":").append("0" + minute).append(" ").append(format);

        }
        else
        {
            buildr_curnt_time = new StringBuilder().append(hour).append(":").append(minute).append(" ").append(format);

        }
        if (minute <= 9)
        {
            buildr_curnt_time = new StringBuilder().append(hour).append(" : ").append("0" + minute).append(" ").append(format);

        }
        current_time = buildr_curnt_time.toString();
        Log.e("time==", current_time);

    }

    /**
     @ViewById
     */
    private void findViewbyID(View view)
    {
        autocompleteTo = (AutoCompleteTextView) view.findViewById(R.id.autocompleteTo);
        autocompleteFrom = (AutoCompleteTextView) view.findViewById(R.id.autocompleteFrom);
        txt_date = (TextView) view.findViewById(R.id.txt_date);
        fab_search = (FloatingActionButton) view.findViewById(R.id.fab_search);
        list_find_ride = (ListView) view.findViewById(R.id.list_find_ride);
        img_date = (ImageView) view.findViewById(R.id.img_date);

        coordinatorLayout = (CoordinatorLayout) view.findViewById(R.id.coordinatorLayout);

        view.findViewById(R.id.llay_date).setOnClickListener(this);
        view.findViewById(R.id.img_date_cancel).setOnClickListener(this);

        GET_CITY_G = new GetCityHelperG_(getActivity(), R.layout.layout_autocomplete);
        autocompleteFrom.setAdapter(GET_CITY_G);
        autocompleteTo.setAdapter(GET_CITY_G);

        autocompleteFrom.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (event.getRawX() >= (autocompleteFrom.getRight() - autocompleteFrom.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
                    {
                        autocompleteFrom.setText("");
                        strng_lat_from = "0.0";
                        strng_long_from = "0.0";

                        return true;
                    }
                }
                return false;
            }
        });
        autocompleteTo.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    if (event.getRawX() >= (autocompleteTo.getRight() - autocompleteTo.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()))
                    {
                        autocompleteTo.setText("");
                        strng_lat_to = "0.0";
                        strng_long_to = "0.0";
                        return true;
                    }
                }
                return false;
            }
        });

        //
        autocompleteFrom.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id)
            {

                Latlng_data resultList = GET_CITY_G.set_LAT_Lng_value(index);

                strng_lat_from = String.valueOf(resultList.getLat());
                strng_long_from = String.valueOf(resultList.getLng());

            }
        });

        autocompleteTo.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id)
            {

                Latlng_data resultList = GET_CITY_G.set_LAT_Lng_value(index);
                strng_lat_to = String.valueOf(resultList.getLat());
                strng_long_to = String.valueOf(resultList.getLng());

            }
        });

        list_find_ride.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Bean_Ride_Details bean_ride_details = list_sharan.get(position);

                //                Intent is = new Intent(getActivity(), Car_owner_Activity.class);

                Intent is = new Intent(getActivity(), Ride_Details.class);

                is.putExtra(GlobalConstants.KeyNames.TripId.toString(), bean_ride_details.getTripId());
                is.putExtra(GlobalConstants.KeyNames.CustomerId.toString(), bean_ride_details.getCustomerId());

                startActivity(is);
                getActivity().overridePendingTransition(0, R.anim.push_down_out);
            }
        });

        /**
         * @SearcRideClick
         */
        fab_search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                InputMethodManager imm = (InputMethodManager) getActivity().
                          getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (autocompleteTo.getText().toString().length() <= 0)
                {
                    snackbar_method_with_view("Enter Destination", v);
                }
                else if (autocompleteFrom.getText().toString().length() <= 0)
                {
                    snackbar_method_with_view("Enter leaving point", v);
                }
                else if (strng_lat_from.equals("0.0") || strng_lat_from.equals("0") || strng_lat_from.equals(""))
                {
                    snackbar_method_with_view("Select leaving point from dropdown only", v);
                }
                else if (strng_lat_to.equals("0.0") || strng_lat_to.equals("0") || strng_lat_to.equals(""))
                {
                    snackbar_method_with_view("Select leaving point from dropdown only", v);

                }
                else if (txt_date.getText().toString().length() <= 0)
                {
                    snackbar_method_with_view("Enter date to search for", v);

                }
                else
                {

                    HitService_FindRide();
                }
            }
        });
    }

    /**
     @DateDialog
     */
    class mDateSetListener_depart implements DatePickerDialog.OnDateSetListener
    {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            int    mYear  = year;
            int    mMonth = monthOfYear + 1;
            int    mDay   = dayOfMonth;
            String dt     = mYear + "-" + mMonth + "-" + mDay;
            try
            {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // Set your date format
                Date currentDt = null;
                currentDt = sdf.parse(dt);
                String date = sdf.format(currentDt);
                txt_date.setText(date);
            }
            catch (Exception ex)
            {

            }

        }
    }

    /**
     @HitServiceToSearch
     */
    private void HitService_FindRide()
    {

        HashMap<String, String> data_find_ride = new HashMap<>();

        data_find_ride.put("LeavingFrom", autocompleteFrom.getText().toString());
        data_find_ride.put("LeavingTo", autocompleteTo.getText().toString());
        data_find_ride.put("LeavingFromLat", strng_lat_from);
        data_find_ride.put("LeavingFromLong", strng_long_from);
        data_find_ride.put("LeavingToLat", strng_lat_to);
        data_find_ride.put("LeavingToLong", strng_long_to);
        data_find_ride.put("DepartureDate", txt_date.getText().toString().trim());
        data_find_ride.put("CustomerId", customerID);

        if (!cd.isConnectingToInternet())
        {
            snackbar_method("No internet connection!");

        }
        else
        {
            /*Json_AsnycTask task = new Json_AsnycTask(getActivity(), GlobalConstants.FIND_A_RIDE, GlobalConstants.POST_SERVICE_METHOD1, data_find_ride);
            task.setOnResultsListener(this);
            task.execute();
            dialog = ProgressDialog.show(getActivity(), "", "Loading. Please wait...", true);
            dialog.setCancelable(false);
            dialog.show();*/

            Find_Ride_Details(GlobalConstants.FIND_A_RIDE, data_find_ride);
        }
    }

    private void HitService_ByLat_Long()
    {
        if (!cd.isConnectingToInternet())
        {
            snackbar_method("No internet connection!");

        }
        else
        {
            String url = "http://112.196.34.42:9091/Trip/GetTripByLatLong?Latitude=" + preferences.getString("current_lat", "0.0") +
                      "&Longitude=" +preferences.getString("current_long", "0.0") + "&CustomerId=" + customerID;
            Log.e("url==", url);

          /*  Json_AsnycTask task = new Json_AsnycTask(getActivity(), "http://112.196.34.42:9091/Trip/GetTripByLatLong?Latitude=" + track.getLatitude() +
                      "&Longitude=" + track.getLongitude() + "&CustomerId=" + customerID, GlobalConstants.GET_SERVICE_METHOD1, null);
            task.setOnResultsListener(this);
            task.execute();*/

            HashMap<String, String> data_find_ride = new HashMap<>();

            Find_Ride_Details(url, data_find_ride);

        }
    }

    //*********************************************************Find Ride Details**************************************************************

    public void Find_Ride_Details(String url, HashMap<String, String> map)
    {

        if (map.isEmpty())
        {
            GlobalConstants.execute(new Super_AsyncTask(getActivity(), url, new Super_AsyncTask_Interface()
            {
                @Override
                public void onTaskCompleted(String output)
                {
                    response(output);
                }
            }, true));
        }
        else
        {

            GlobalConstants.execute(new Super_AsyncTask(getActivity(), map, url, new Super_AsyncTask_Interface()
            {
                @Override
                public void onTaskCompleted(String output)
                {
                    response(output);
                }
            }, true));
        }

    }

    ArrayList<Bean_Ride_Details> list_sharan = new ArrayList<>();

    public void response(String output)
    {
        try
        {

            JSONObject object = new JSONObject(output);
            if (object.getString("Status").equalsIgnoreCase("success"))
            {

                JSONArray array = object.getJSONArray("Message");

                ArrayList<Bean_Ride_Details> local_list_sharan = new ArrayList<>();

                for (int i = 0; i < array.length(); i++)
                {
                    JSONObject object2 = array.getJSONObject(i);

                    Bean_Ride_Details bean_ride_details = new Bean_Ride_Details();

                    bean_ride_details.setCreatedOn(object2.getString("CreatedOn"));
                    bean_ride_details.setCustomerContactNo(object2.getString("CustomerContactNo"));
                    bean_ride_details.setCustomerPhoto(object2.getString("CustomerPhoto"));
                    bean_ride_details.setCustomerId(object2.getString("CustomerId"));
                    bean_ride_details.setCustomerName(object2.getString("CustomerName"));
                    bean_ride_details.setDepartureDate(object2.getString("DepartureDate"));
                    bean_ride_details.setDepartureTime(object2.getString("DepartureTime"));

                    bean_ride_details.setDescription(object2.getString("Description"));
                    bean_ride_details.setIsActive(object2.getString("IsActive"));
                    bean_ride_details.setIsRegulerBasis(object2.getString("IsRegulerBasis"));
                    bean_ride_details.setLeavingDate(object2.getString("LeavingDate"));
                    bean_ride_details.setLeavingFrom(object2.getString("LeavingFrom"));
                    bean_ride_details.setLeavingTo(object2.getString("LeavingTo"));

                    bean_ride_details.setNoOfSeats(object2.getString("NoOfSeats"));
                    bean_ride_details.setRatePerSeat(object2.getString("RatePerSeat"));
                    bean_ride_details.setRegulerDays(object2.getString("RegulerDays"));

                    bean_ride_details.setRequestAcceptModelList(object2.getString("RequestAcceptModelList"));
                    bean_ride_details.setRoundTrip(object2.getString("RoundTrip"));
                    bean_ride_details.setRegulerDays(object2.getString("RegulerDays"));

                    bean_ride_details.setTripId(object2.getString("TripId"));
                    bean_ride_details.setVehicleName(object2.getString("VehicleName"));
                    bean_ride_details.setVehicleNo(object2.getString("VehicleNo"));
                    bean_ride_details.setVehicleType(object2.getString("VehicleType"));

                    bean_ride_details.setReturnDate(object2.getString("ReturnDate"));
                    bean_ride_details.setReturnTime(object2.getString("ReturnTime"));

                    bean_ride_details.setTripStatus(object2.getString("TripStatus"));

                    // this request id we will get it from RequestAcceptModelList in bean class
                    // bean_ride_details.setRequestId(object2.getString("RequestId"));

                    //                    bean_ride_details.setRouteModelList(object2.getString("RouteModelList"));

                    local_list_sharan.add(bean_ride_details);

                }

                if (list_sharan.isEmpty())
                {
                    list_sharan = local_list_sharan;
                    adater = new MidPointAdapter(list_sharan);
                    list_find_ride.setAdapter(adater);

                }
                else
                {
                    list_sharan = local_list_sharan;
                    adater.addData(list_sharan);
                    adater.notifyDataSetChanged();
                }

                //                adater.notifyDataSetChanged();
                GlobalConstants.setListViewHeightBasedOnItems(list_find_ride, list_sharan.size());

            }
            else
            {
                GlobalConstants.show_Toast(object.getString("Message"), getActivity().getApplicationContext());
            }
        }
        catch (Exception ex)
        {
            Log.e("Exception is", ex.toString());
        }
    }

    //**********************************************************************************************************************

    private class MidPointAdapter extends BaseAdapter
    {
        ArrayList<Bean_Ride_Details> local_list;

        public MidPointAdapter(ArrayList<Bean_Ride_Details> local_list)
        {
            super();

            this.local_list = local_list;

        }

        @Override
        public int getCount()
        {
            return local_list.size();
        }

        @Override
        public Object getItem(int position)
        {
            return null;
        }

        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        @Override
        public boolean hasStableIds()
        {
            return super.hasStableIds();
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View           v        = inflater.inflate(R.layout.custom_list_find_ride2, parent, false);

            TextView  txt_day             = (TextView) v.findViewById(R.id.txt_day);
            TextView  txt_from            = (TextView) v.findViewById(R.id.txt_from);
            TextView  txt_to              = (TextView) v.findViewById(R.id.txt_to);
            TextView  txt_txt_driver_name = (TextView) v.findViewById(R.id.txt_driver_name);
            ImageView img_driver_img      = (ImageView) v.findViewById(R.id.img_driver_img);
            ImageView img_phn             = (ImageView) v.findViewById(R.id.img_phn);

            TextView  txt_car_name     = (TextView) v.findViewById(R.id.txt_car_name);
            TextView  txt_flag         = (TextView) v.findViewById(R.id.txt_flag);
            ImageView img_vehicle      = (ImageView) v.findViewById(R.id.img_man);
            TextView  txt_vw_ride_type = (TextView) v.findViewById(R.id.txt_vw_ride_type);
            v.findViewById(R.id.txtv_message_count).setVisibility(View.GONE);

            v.findViewById(R.id.rel_my_ride_edit).setVisibility(View.GONE);

            final Bean_Ride_Details bean_ride_details = local_list.get(position);

            // txt_day.setText(bean_ride_details.getDepartureDate() + "     " + bean_ride_details.getDepartureTime());

            txt_txt_driver_name.setText(bean_ride_details.getCustomerName());

            if (bean_ride_details.getLeavingFrom() != null)
            {
                txt_from.setText(bean_ride_details.getLeavingFrom());
            }
            if (bean_ride_details.getLeavingTo() != null)
            {
                txt_to.setText(bean_ride_details.getLeavingTo());
            }

            if (bean_ride_details.getIsRegulerBasis() != null)
            {
                String is_regular_basis = bean_ride_details.getIsRegulerBasis().equals("false") ? "Ride Type" + "\n" + "One Time" : "Riding Type" + "\n" + "Daily";
                txt_vw_ride_type.setText(is_regular_basis);

                String date_day_str= bean_ride_details.getIsRegulerBasis().equals("false")? bean_ride_details.getDepartureDate() : bean_ride_details.getRegulerDays();
                //      Log.e("date_day_str ","date_day_str  "+date_day_str+"   "+feedItem.getLeaving_date()+"  gggg   "+txt_vw_ride_type.getText().toString().equalsIgnoreCase("Ride Type"+ "\n" +"  One Time") );

                txt_day.setText(date_day_str+"     "+  bean_ride_details.getDepartureTime());
            }

            txt_car_name.setText("Type : " + bean_ride_details.getVehicleType() + "\n" + "Name : " + bean_ride_details.getVehicleName() + "\n" + "Number : " + bean_ride_details.getVehicleNo());

            int id = bean_ride_details.getVehicleType().equals("Bike") ? R.mipmap.bike : R.mipmap.car;
            img_vehicle.setBackgroundResource(id);

            ArrayList<HashMap<String, String>> Accept_model_list = bean_ride_details.getRequestAcceptModelList();

            for (int i = 0; i < Accept_model_list.size(); i++)
            {
                if (Accept_model_list.get(i).get("CustomerId").equals(my_customerID))
                {
                    if (Accept_model_list.get(i).get("Flag").equals("1"))
                    {
                        txt_flag.setText("Accepted");
                    }
                    if (Accept_model_list.get(i).get("Flag").equals("5"))
                    {
                        txt_flag.setText("Pending");
                    }
                }
            }

            img_phn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (bean_ride_details.getCustomerContactNo() != null)
                    {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + bean_ride_details.getCustomerContactNo()));
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
            if (bean_ride_details.getCustomerPhoto() != null)
            {
                Picasso.with(getActivity()).load(bean_ride_details.getCustomerPhoto()).
                          transform(new CircleTransform()).into(img_driver_img);
            }
            return v;
        }

        public void addData(ArrayList<Bean_Ride_Details> local_list)
        {
            this.local_list = local_list;
        }

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

    private void snackbar_method_with_view(String text, View view)
    {
        snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG).setAction("Ok", new View.OnClickListener()
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