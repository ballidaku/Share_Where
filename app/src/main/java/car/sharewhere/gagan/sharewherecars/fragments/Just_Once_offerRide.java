package car.sharewhere.gagan.sharewherecars.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import car.sharewhere.gagan.WebServices.Asnychronus_notifier;
import car.sharewhere.gagan.WebServices.GetCityHelperG_;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.WebServices.Json_AsnycTask;
import car.sharewhere.gagan.model.Latlng_data;
import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.utills.ConnectivityDetector;

public class Just_Once_offerRide extends AppCompatActivity implements Asnychronus_notifier, AdapterView.OnItemClickListener, View.OnClickListener
{
    TextView txt_departure_date, txt_return_date, txt_departure_time, txt_return_time, txt_car_top, txtvwcar_name, tvw_car_ype, txt_vw_txt_carnmbr, btn_single_trip, btn_round_trip;
    Calendar calendar;
    String format = "";
    AutoCompleteTextView autocompleteTo, autocompleteFrom, autocomplete_new_entry_midpnt;
    TimePicker    timePicker;
    StringBuilder strng_time_depart, curent_time, strng_time_return;
    int year, month, day;
    Button set, btn_select_ride, btn_cancel_ride, btn_line_select, btn_line_cancel;
    HashMap<String, String> data_ofer_ride = new HashMap<>();
    ConnectivityDetector cd;
    SharedPreferences    preferences;
    static String static_string = "";
    RelativeLayout rel_return, lnr_vehicle_nmber;
    ImageView img_add_mid_pnt, img_calender_depart, img_clk_depart, img_return_calender, img_return_clk;
    EditText edt_car_name, edt_car_number, txt_number_Seats, txt_rate_per_seat, edt_car_type;
    RadioGroup     radioGroup;
    RadioButton    radioButton;
    ProgressDialog dialog;
    StringBuilder  datebuilder;
    LinearLayout   lnr_depart_date, lnr_return_clk, lnr_main_days, lnr_rate, lnr_clk_depart, lnr_return_date, lnr_vw_seat_nmbr, lnr_depart_time_regular;
    int curent_year, current_mnth, current_day;
    Date date_depart, date_return, time_depart, current_time_date, timeCompare_depart, timeCompare_return, test;
    Date depart_date = null;
    Date return_date = null;
    Date date2       = null;

    int trip_ = 0;
    ListView          list_midpoint;
    MidPointAdapter   adater;
    ArrayList<String> array_midPoints, array_mid_pnt_to_show, array_midPoints_add_city;
    String Points_to_post, customerID, auto_from, auto_to, get_trip_id, get_intent_depart_time, get_intent_return_time;
    CoordinatorLayout coordinatorLayout;
    Snackbar          snackbar;
    GetCityHelperG_   GET_CITY_G;
    String strng_lat_from  = "0.0";
    String strng_lat_to    = "0.0";
    String strng_long_from = "0.0";
    String strng_long_to   = "0.0";
    String strngmid_lat    = "0.0";
    String strng_mid_long  = "0.0";
    //GPSTracker track;
    Intent get_intent;
    SimpleDateFormat sdf         = new SimpleDateFormat("yyyy-MM-dd");
    //For departure time
    SimpleDateFormat dateFormat1 = new SimpleDateFormat("hh:mm aa");

    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");

    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.just_once_final);

        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        /**
         * @initialisations
         */
        preferences = PreferenceManager.getDefaultSharedPreferences(Just_Once_offerRide.this);
        customerID = preferences.getString("CustomerId", null);
        editor = preferences.edit();
        array_midPoints = new ArrayList<>();
        array_mid_pnt_to_show = new ArrayList<>();
        array_midPoints_add_city = new ArrayList<>();

        adater = new MidPointAdapter();
        //track = new GPSTracker();

        findViewbyID();
        setActionBar();

        addListenerOnButton();
    }

    private void findViewbyID()
    {

        // layoutauto_mid_points = (LinearLayout) findViewById(R.id.layoutauto_mid_points);
        autocomplete_new_entry_midpnt = (AutoCompleteTextView) findViewById(R.id.autocomplete_new_mid_point);
        autocompleteTo = (AutoCompleteTextView) findViewById(R.id.autocompleteTo_just);
        autocompleteFrom = (AutoCompleteTextView) findViewById(R.id.autocompleteFrom_just);
        txt_departure_time = (TextView) findViewById(R.id.txt_departure_time);
        txt_return_time = (TextView) findViewById(R.id.txt_return_time);

        txt_number_Seats = (EditText) findViewById(R.id.txt_seat_number);
        txt_car_top = (TextView) findViewById(R.id.car_info_txt_top);
        txtvwcar_name = (TextView) findViewById(R.id.txtcar_name_info);
        txt_rate_per_seat = (EditText) findViewById(R.id.txt_rate_perseat);
        txt_departure_date = (TextView) findViewById(R.id.txt_departure_date);
        img_calender_depart = (ImageView) findViewById(R.id.depart_calender_img);
        img_clk_depart = (ImageView) findViewById(R.id.clk_depart);
        txt_return_date = (TextView) findViewById(R.id.txt_return_date);
        img_return_clk = (ImageView) findViewById(R.id.img_return_clk);
        img_return_calender = (ImageView) findViewById(R.id.img_return_calendr);
        btn_single_trip = (TextView) findViewById(R.id.btn_single_trip);
        btn_round_trip = (TextView) findViewById(R.id.btn_round_trip);
        btn_select_ride = (Button) findViewById(R.id.btn_select);
        btn_cancel_ride = (Button) findViewById(R.id.btn_cancel);
        btn_line_select = (Button) findViewById(R.id.line_select);
        btn_line_cancel = (Button) findViewById(R.id.line_cancel);
        rel_return = (RelativeLayout) findViewById(R.id.rel_return);
        //  linear_mid_point = (LinearLayout) findViewById(R.id.linear_mid);
        lnr_main_days = (LinearLayout) findViewById(R.id.lnr_main_days);

        lnr_depart_time_regular = (LinearLayout) findViewById(R.id.lnr_depart_time_regular);
        lnr_depart_date = (LinearLayout) findViewById(R.id.lnr_depart_date);
        lnr_return_clk = (LinearLayout) findViewById(R.id.lnr_return_clk);
        lnr_clk_depart = (LinearLayout) findViewById(R.id.lnr_clk_depart);
        lnr_return_date = (LinearLayout) findViewById(R.id.lnr_return_date);
        lnr_vw_seat_nmbr = (LinearLayout) findViewById(R.id.lnr_vw_seat_nmbr);
        lnr_rate = (LinearLayout) findViewById(R.id.lnr_rate);
        img_add_mid_pnt = (ImageView) findViewById(R.id.img_add_midpnt);
        edt_car_name = (EditText) findViewById(R.id.edt_car_name);

        edt_car_type = (EditText) findViewById(R.id.edt_car_type);
        edt_car_number = (EditText) findViewById(R.id.edt_car_number);

        radioGroup = (RadioGroup) findViewById(R.id.radiogrp);
        tvw_car_ype = (TextView) findViewById(R.id.txt_v_car_tye);
        txt_vw_txt_carnmbr = (TextView) findViewById(R.id.txt_vw_txt_carnmbr);
        lnr_vehicle_nmber = (RelativeLayout) findViewById(R.id.lnr_vehicle_nmber);
        list_midpoint = (ListView) findViewById(R.id.list_mid);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        lnr_main_days.setVisibility(View.GONE);

        //Initialize Calendar
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        edt_car_type.setFocusable(false);
        edt_car_type.setFocusableInTouchMode(false);

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

        img_clk_depart.setOnClickListener(this);
        edt_car_type.setOnClickListener(this);
        btn_select_ride.setOnClickListener(this);
        lnr_depart_time_regular.setOnClickListener(this);
        img_add_mid_pnt.setOnClickListener(this);
        img_calender_depart.setOnClickListener(this);
        img_return_calender.setOnClickListener(this);
        btn_round_trip.setOnClickListener(this);
        lnr_clk_depart.setOnClickListener(this);
        btn_single_trip.setOnClickListener(this);
        img_return_clk.setOnClickListener(this);
        btn_cancel_ride.setOnClickListener(this);
        lnr_depart_date.setOnClickListener(this);
        lnr_return_date.setOnClickListener(this);
        lnr_return_clk.setOnClickListener(this);
        lnr_rate.setOnClickListener(this);
        lnr_vw_seat_nmbr.setOnClickListener(this);
        autocompleteFrom.setOnItemClickListener(this);
        autocompleteTo.setOnItemClickListener(this);

        autocomplete_new_entry_midpnt.setOnItemClickListener(this);

        cd = new ConnectivityDetector(Just_Once_offerRide.this);
        preferences = PreferenceManager.getDefaultSharedPreferences(Just_Once_offerRide.this);

        GET_CITY_G = new GetCityHelperG_(Just_Once_offerRide.this, R.layout.layout_autocomplete);
        autocompleteFrom.setAdapter(GET_CITY_G);
        autocompleteTo.setAdapter(GET_CITY_G);
        autocomplete_new_entry_midpnt.setAdapter(GET_CITY_G);

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

        autocomplete_new_entry_midpnt.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id)
            {

                Log.e("Check 1", " " + arg0 + " " + arg1 + " " + index + " " + id);

                Latlng_data resultList = GET_CITY_G.set_LAT_Lng_value(index);
                strngmid_lat = String.valueOf(resultList.getLat());
                strng_mid_long = String.valueOf(resultList.getLat());
                Log.e("MidPoints lat", " " + strngmid_lat);
                Log.e("MidPoints long", " " + strng_mid_long);

                midpoint_imagemethod();

            }
        });

    }

    //For Round Trip Return Time
    private void set_return_time()
    {

        final Dialog dialog = new Dialog(Just_Once_offerRide.this);
        dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        set = (Button) dialog.findViewById(R.id.btn_set);
        timePicker = (TimePicker) dialog.findViewById(R.id.timePicker1);
        calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min  = calendar.get(Calendar.MINUTE);

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                setTime(v);
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    //For Return Time
    public void setTime(View view)
    {

        int hour = timePicker.getCurrentHour();
        int min  = timePicker.getCurrentMinute();
        showTime(hour, min);
    }

    //For Return Time
    public void showTime(int hour, int min)
    {

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
        StringBuilder setreturn_time_;
        String        d = txt_departure_date.getText().toString().trim();

        int length = (int) (Math.log10(min) + 1);
        strng_time_return = new StringBuilder().append(d).append(" ").append(hour).append(":").append("0" + min).append(" ").append(format);
        setreturn_time_ = new StringBuilder().append(hour).append(":").append("0" + min).append(" ").append(format);
        if (length == 1)
        {

            strng_time_return = new StringBuilder().append(d).append(" ").append(hour).append(":").append("0" + min).append(" ").append(format);
            setreturn_time_ = new StringBuilder().append(hour).append(":").append("0" + min).append(" ").append(format);
        }
        else
        {

            strng_time_return = new StringBuilder().append(d).append(" ").append(hour).append(":").append(min).append(" ").append(format);
            setreturn_time_ = new StringBuilder().append(hour).append(":").append(min).append(" ").append(format);
        }
        if (min == 0)
        {
            strng_time_return = new StringBuilder().append(d).append(" ").append(hour).append(":").append("00").append(" ").append(format);
            setreturn_time_ = new StringBuilder().append(hour).append(":").append("00").append(" ").append(format);
        }

        try
        {

            //           if (return_date.after(depart_date))
            if (date_return.after(date_depart))
            {
                Log.e("Return Date", "" + return_date + "  " + depart_date);
                txt_return_time.setText(setreturn_time_);
            }
            //   else if (return_date.equals(depart_date))
            else if (date_return.equals(date_depart))
            {

                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                //            Date timeCompare_depart = null;
                //            Date timeCompare_return = null;
                try
                {
                    //  timeCompare_depart = sdf.parse(txt_departure_time.getText().toString());
                    //  timeCompare_return = sdf.parse(strng_time_return.toString());
                    //   timeCompare_depart = dateFormat2.parse(txt_departure_time.getText().toString().trim());
                    Log.e("time_depart", "" + time_depart);
                    timeCompare_depart = time_depart;
                    Log.e("timeCompare_depart", "" + timeCompare_depart);
                    timeCompare_return = dateFormat2.parse(String.valueOf(strng_time_return));
                    Log.e("timeCompare_return", "" + timeCompare_return);

                    Log.e("time compare", "" + timeCompare_return + "aaaaaa" + timeCompare_depart);
                    if (timeCompare_return.after(timeCompare_depart))
                    {
                        Log.e("timecompare", "" + timeCompare_return.after(timeCompare_depart));
                        long timeDifMinutes = time_difference(2, null);

                        //again initialize trip_ variable
                        trip_ = 0;

                        if (timeDifMinutes >= 20)
                        {
                            Log.e("difference", "" + "minuted " + timeDifMinutes);
                            txt_return_time.setText(setreturn_time_);
                        }
                        else
                        {
                            snackbar_method("Return time should be 20 minutes more than depart time");
                            txt_return_time.setText("");
                        }

                    }
                    else
                    {
                        snackbar_method("Return time should be 20 minutes more than depart time");
                        txt_return_time.setText("");
                    }
                }
                catch (Exception ex)
                {
                    Log.e("Exception********", "********1" + ex.toString());
                }
            }
            else
            {
                Log.e("Else********", "********1" + setreturn_time_);
                snackbar_method("Return time should be 20 minutes more than depart time");
                //txt_return_time.setText(strng_time_return);
            }
        }
        catch (Exception ex)
        {
            Log.e("Exception", "######2" + ex.toString());
        }

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        array_midPoints.clear();
        array_mid_pnt_to_show.clear();
        get_intent = getIntent();
        String from        = get_intent.getStringExtra(GlobalConstants.Leaving_From);
        String to          = get_intent.getStringExtra(GlobalConstants.Leaving_To);
        String depart_date = get_intent.getStringExtra(GlobalConstants.Leaving_Date);
        //Log.e("LEAVING DATE","^^^^^^^^ "+depart_date);// 2016-02-17
        String points = get_intent.getStringExtra(GlobalConstants.Points);

        String vehicle_type   = get_intent.getStringExtra("vehicle_type");
        String vehicle_name   = get_intent.getStringExtra("vehicle_name");
        String vehicle_select = get_intent.getStringExtra("vehicle_select");
        String is_round       = get_intent.getStringExtra(GlobalConstants.Round_Trip);
        String return_date    = get_intent.getStringExtra(GlobalConstants.Return_Date);
        get_intent_return_time = get_intent.getStringExtra(GlobalConstants.Return_Time);
        String available_seat = get_intent.getStringExtra(GlobalConstants.Available_Seats);
        String rate           = get_intent.getStringExtra(GlobalConstants.Rate_seat);
        get_trip_id = get_intent.getStringExtra(GlobalConstants.Trip_Id);
        get_intent_depart_time = get_intent.getStringExtra(GlobalConstants.Leaving_Time);
        //        Log.e("LEAVING TIME","^^^^^^^^ "+get_intent_depart_time);//3:48 PM
        String VehicleNo = get_intent.getStringExtra(GlobalConstants.Vehicle_number);

        Log.e("RETURN DATE", "^^^^^^^^ " + return_date);//
        //        Log.e("RETURN TIME","^^^^^^^^ "+get_intent_return_time);//
        if (from != null)
        {
            autocompleteFrom.setText(from);
            autocompleteFrom.setSelection(autocompleteFrom.length());
            strng_lat_from = preferences.getString("current_lat", "0.0");
        }
        if (get_intent_depart_time != null)
        {
            txt_departure_time.setText(get_intent_depart_time);
            try
            {
                test = dateFormat1.parse(get_intent_depart_time);

                time_depart = dateFormat2.parse(depart_date.trim() + " " + get_intent_depart_time);
                Log.e("TESTTEST", "" + test + " time_depart  " + time_depart);
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                Log.e("CATCHHHH", "" + e.toString());
            }
        }
        if (to != null)
        {
            autocompleteTo.setText(to);
            btn_select_ride.setText("SAVE CHANGES");
            btn_cancel_ride.setText("DELETE RIDE");
            autocompleteTo.setSelection(autocompleteTo.length());
        }

        if (depart_date != null)
        {
            try
            {
                date_depart = sdf.parse(depart_date);
            }
            catch (Exception ex)
            {

            }
            txt_departure_date.setText(depart_date);
        }

        if (vehicle_name != null)
        {
            edt_car_name.setText(vehicle_name);
        }

        if (available_seat != null)
        {
            txt_number_Seats.setText(available_seat);
        }

        if (rate != null)
        {
            txt_rate_per_seat.setText(rate);
        }

        if (vehicle_type != null)
        {
            edt_car_type.setText(vehicle_type);
        }
        if (VehicleNo != null)
        {
            edt_car_number.setText(VehicleNo);
        }
        if (is_round != null)
        {
            if (is_round.equals("false"))
            {
                static_string = "single";
            }
            else
            {
                static_string = "round";
            }
        }
        if ((return_date != null) && !(return_date.equalsIgnoreCase("")))
        {
            if (!return_date.equalsIgnoreCase("null"))
            {
                try
                {
                    date_return = sdf.parse(return_date);
                }
                catch (Exception ex)
                {
                }
                Log.e("log2", "**" + return_date);
                txt_return_date.setText(return_date);
            }
        }

        if ((get_intent_return_time != null) && !get_intent_return_time.equalsIgnoreCase(""))
        {
            if (!get_intent_return_time.equalsIgnoreCase("null") && !get_intent_return_time.equalsIgnoreCase(""))
            {

                Log.e("enteres", "00000000000" + get_intent_return_time);
                txt_return_time.setText(get_intent_return_time);

            }
        }

        if (vehicle_select != null)
        {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            if (vehicle_select.equals("Car"))
            {
                selectedId = R.id.radiocar;
                radioGroup.check(selectedId);
                tvw_car_ype.setVisibility(View.VISIBLE);
                edt_car_type.setVisibility(View.VISIBLE);
                txt_car_top.setText("Car Information");
                txtvwcar_name.setText("Car");
                tvw_car_ype.setText("Type");
                txt_vw_txt_carnmbr.setText("Car number");
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lnr_vehicle_nmber.getLayoutParams();
                params.setMargins(0, 20, 0, 20);
                lnr_vehicle_nmber.setLayoutParams(params);
            }
            if (vehicle_select.equals("Bike"))
            {
                selectedId = R.id.radiobike;
                radioGroup.check(selectedId);
                txt_car_top.setText("Bike Information");
                txtvwcar_name.setText("Bike");
                tvw_car_ype.setVisibility(View.GONE);
                edt_car_type.setVisibility(View.GONE);
                txt_vw_txt_carnmbr.setText("Bike number");
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lnr_vehicle_nmber.getLayoutParams();
                params.setMargins(0, 10, 0, 20);
                lnr_vehicle_nmber.setLayoutParams(params);
            }
        }

        //=========Added For Midpoints============
        if (points != null)
        {
            String[] mid_stationsArray = points.split(";");
            int sizeMid_stations = mid_stationsArray.length;

            if (mid_stationsArray.length == 2)
            {

            }
            else
            {
                //                     for (int i = 0; i < (sizeMid_stations - 1); i++)
                for (int i = 0; i < sizeMid_stations; i++)
                {
                    if (i == 0)
                    {

                        String[] stationsName_ = mid_stationsNames(mid_stationsArray[i]);
                        strng_lat_from = stationsName_[1];
                        strng_long_from = stationsName_[2];
                    }
                    else if (i == (sizeMid_stations - 1))
                    {
                        String[] stationsName_ = mid_stationsNames(mid_stationsArray[i]);
                        strng_lat_to = stationsName_[1];
                        strng_long_to = stationsName_[2];
                    }
                    else
                    {
                        array_midPoints.add(mid_stationsArray[i]);

                        String[] stationsName_ = mid_stationsNames(mid_stationsArray[i]);
                        array_mid_pnt_to_show.add(stationsName_[0]);
                    }
                }

                list_midpoint.setAdapter(adater);
                GlobalConstants.setListViewHeightBasedOnItems(list_midpoint, array_mid_pnt_to_show.size());
            }
        }

        //=========Added Above============================

        if (from == null)
        {
            String current_city = preferences.getString("current_city", null);
            String current_lat = preferences.getString("current_lat", null);
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
        }

        if (static_string.equals("round"))
        {
            btn_round_trip.setBackgroundResource(R.mipmap.return_yellow);
            btn_single_trip.setBackgroundResource(R.mipmap.singletripbtn);
            rel_return.setVisibility(View.VISIBLE);
        }
        else if (static_string.equals("single"))
        {
            btn_single_trip.setBackgroundResource(R.mipmap.return_yellow);
            btn_round_trip.setBackgroundResource(R.mipmap.singletripbtn);
            rel_return.setVisibility(View.GONE);
        }
        else
        {
            static_string = "single";
            btn_single_trip.setBackgroundResource(R.mipmap.return_yellow);
            btn_round_trip.setBackgroundResource(R.mipmap.singletripbtn);
            rel_return.setVisibility(View.GONE);
        }
        btn_line_cancel.setVisibility(View.INVISIBLE);
        data_ofer_ride.put("VehicleType", "Car");

        getCurrentDate();

    }

    /**
     @GetCalenderCurrentInstance
     */
    private void getCurrentDate()
    {

        Calendar startDate = Calendar.getInstance();

        curent_year = startDate.get(Calendar.YEAR);
        current_mnth = startDate.get(Calendar.MONTH) + 1;
        current_day = startDate.get(Calendar.DAY_OF_MONTH);
        datebuilder = new StringBuilder().append(curent_year).append("-").append(current_mnth).append("-").append(current_day).append(" ");
        Log.e("current date", datebuilder.toString());

    }

    /**
     @DepartDateListner
     */
    class mDateSetListener_depart implements DatePickerDialog.OnDateSetListener
    {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            // TODO Auto-generated method stub

            int mYear  = year;
            int mMonth = monthOfYear + 1;
            int mDay   = dayOfMonth;

            int length_day = (int) (Math.log10(mDay) + 1);

            if (mYear == curent_year)
            {

                if (mMonth > current_mnth)
                {

                    String dt = mYear + "-" + mMonth + "-" + mDay;
                    try
                    {

                        date_depart = sdf.parse(dt);
                        String d = sdf.format(date_depart);
                        txt_departure_date.setText(d);

                        Log.e("date_departdddddddd", "" + date_depart);
                    }
                    catch (Exception ex)
                    {

                    }
                }
                else if (mMonth == current_mnth)
                {

                    if (mDay < current_day)
                    {
                        txt_departure_date.setText("");
                        txt_departure_date.setHint("set date");
                        txt_departure_time.setText("");
                        txt_return_time.setText("");
                        txt_return_date.setText("");
                        snackbar_method("Not a valid date");
                    }
                    else
                    {
                        String dt = mYear + "-" + mMonth + "-" + mDay;
                        try
                        {
                            date_depart = sdf.parse(dt);
                            String d = sdf.format(date_depart);
                            txt_departure_date.setText(d);
                            Log.e("date_departdddddddd", "" + date_depart);
                        }
                        catch (Exception ex)
                        {

                        }
                    }
                }
                else if (mMonth < current_mnth)
                {

                    txt_departure_date.setText("");
                    txt_departure_date.setHint("set date");
                    txt_departure_time.setText("");
                    txt_return_time.setText("");
                    txt_return_date.setText("");
                    snackbar_method("Not a valid date");
                }

            }
            else if (mYear > curent_year)
            {
                String dt = mYear + "-" + mMonth + "-" + mDay;
                try
                {
                    date_depart = sdf.parse(dt);
                    String d = sdf.format(date_depart);
                    txt_departure_date.setText(d);
                    Log.e("date_departdddddddd", "" + date_depart);

                }
                catch (Exception ex)
                {

                }
            }
            else
            {
                txt_departure_date.setText("");
                txt_departure_date.setHint("set date");
                txt_return_time.setText("");
                txt_return_date.setText("");
                txt_departure_time.setText("");
                Toast.makeText(Just_Once_offerRide.this, "Not a valid date", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     @ReturnDateListner
     */
    class mDateSetListener_return implements DatePickerDialog.OnDateSetListener
    {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            // TODO Auto-generated method stub

            int mYear  = year;
            int mMonth = monthOfYear + 1;
            int mDay   = dayOfMonth;

            if (mYear == curent_year)
            {
                if (mMonth >= current_mnth)
                {
                    if (mDay >= current_day)
                    {
                        String dt = mYear + "-" + mMonth + "-" + mDay;
                        try
                        {
                            date_return = sdf.parse(dt);
                            String d = sdf.format(date_return);
                            txt_return_date.setText(d);
                        }
                        catch (Exception ex)
                        {

                        }
                    }
                    else
                    {
                        txt_return_date.setText("");
                        txt_return_date.setHint("set date");
                        txt_return_time.setText("");
                        snackbar_method("Not a valid date");
                    }
                }
                else
                {
                    txt_return_date.setText("");
                    txt_return_date.setHint("set date");
                    txt_return_time.setText("");
                    snackbar_method("Not a valid date");
                }

            }
            else if (mYear > curent_year)
            {
                String dt = mYear + "-" + mMonth + "-" + mDay;
                try
                {
                    date_return = sdf.parse(dt);
                    String d = sdf.format(date_return);
                    txt_return_date.setText(d);
                }
                catch (Exception ex)
                {

                }
            }
            else
            {
                txt_return_date.setText("");
                txt_return_date.setHint("set date");
                snackbar_method("Not a valid date");
            }
        }
    }

    //Fetching current time for comparing departure time
    private void set_depart_time()
    {

        final Dialog dialog = new Dialog(Just_Once_offerRide.this);
        dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog);
        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button set    = (Button) dialog.findViewById(R.id.btn_set);
        timePicker = (TimePicker) dialog.findViewById(R.id.timePicker1);

        calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min  = calendar.get(Calendar.MINUTE);// + 20

        int length = (int) (Math.log10(min) + 1);

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

        String d = txt_departure_date.getText().toString();
        if (length == 1)
        {
            curent_time = new StringBuilder().append(d).append(" ").append(hour).append(":").append("0" + min).append(" ").append(format);

        }
        else
        {
            curent_time = new StringBuilder().append(d).append(" ").append(hour).append(":").append(min).append(" ").append(format);

        }
        if (min <= 9)
        {
            curent_time = new StringBuilder().append(d).append(" ").append(hour).append(":").append("0" + min).append(" ").append(format);

        }
        // SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");

        try
        {

            //current_time_date = dateFormat1.parse(String.valueOf(curent_time));
            current_time_date = dateFormat2.parse(String.valueOf(curent_time));

        }
        catch (Exception ex)
        {
            Log.e("CUUREETTTTT", "^^^" + ex.toString());
        }

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });
        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                setTime_depart(v);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void setTime_depart(View view)
    {
        int hour = timePicker.getCurrentHour();
        int min  = timePicker.getCurrentMinute();
        showTime_depart(hour, min);
    }

    public void showTime_depart(int hour, int min)
    {

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

        int length = (int) (Math.log10(min) + 1);

        String        d = txt_departure_date.getText().toString();
        StringBuilder setdepart_time_;
        if (length == 1)
        {
            strng_time_depart = new StringBuilder().append(d).append(" ").append(hour).append(":").append("0" + min).append(" ").append(format);
            setdepart_time_ = new StringBuilder().append(hour).append(":").append("0" + min).append(" ").append(format);

        }
        else
        {
            strng_time_depart = new StringBuilder().append(d).append(" ").append(hour).append(":").append(min).append(" ").append(format);
            setdepart_time_ = new StringBuilder().append(hour).append(":").append(min).append(" ").append(format);

        }
        if (min <= 9)
        {
            strng_time_depart = new StringBuilder().append(d).append(" ").append(hour).append(":").append("0" + min).append(" ").append(format);
            setdepart_time_ = new StringBuilder().append(hour).append(":").append("0" + min).append(" ").append(format);
        }

        try
        {

            time_depart = dateFormat2.parse(String.valueOf(strng_time_depart));
            test = dateFormat1.parse(String.valueOf(setdepart_time_));
            //  depart_date = sdf.parse(txt_departure_date.getText().toString().trim());

            Calendar c = Calendar.getInstance();
            String formattedDate = sdf.format(c.getTime());
            date2 = sdf.parse(formattedDate);

            //            if (depart_date.equals(date2)) {
            if (date_depart.equals(date2))
            {

                if (time_depart.before(current_time_date))
                {
                    snackbar_method("Depart time should be 20 minutes more than current time");
                    txt_departure_time.setText("");
                }
                else
                {
                    long timeDifMinutes = time_difference(1, null);

                    //again initialize trip_ variable
                    trip_ = 0;

                    if (timeDifMinutes >= 20)
                    {

                        txt_departure_time.setText(setdepart_time_);
                    }
                    else
                    {
                        snackbar_method("Depart time should be 20 minutes more than current time");
                        txt_departure_time.setText("");
                    }

                }
            }
            //            else if (depart_date.after(date2))
            else if (date_depart.after(date2))
            {

                txt_departure_time.setText(setdepart_time_);
            }
            //            else if (!depart_date.after(date2))
            else if (!date_depart.after(date2))
            {

                snackbar_method("Please update departure date.");
                txt_departure_time.setText("");
            }
            else
            {
                Log.e("Else part ******11", "");
            }
        }
        catch (Exception ex)
        {

            Log.e("Exception ******11", ex.toString());
        }

    }

    /*Difference between Departure time and Current time*/
    private long time_difference(int trip, Date datee)
    {

        Log.e("trip", "" + trip);
        Log.e("trip", "" + current_time_date);
        Log.e("trip", "" + time_depart);
        Log.e("tripDatee", "datee" + datee);
        //Time Difference Calculations Begin
        long milliSec1 = 0;
        long milliSec2 = 0;
        if (trip == 1)
        {
            milliSec1 = current_time_date.getTime();
            milliSec2 = time_depart.getTime();
        }
        else if (trip == 2)
        {
            milliSec1 = timeCompare_return.getTime();
            milliSec2 = timeCompare_depart.getTime();
        }
        else if (trip == 3)
        {
            trip_ = 0;

            Log.e("DATEEEEEEEE", "##############" + datee);
            milliSec1 = datee.getTime();
            milliSec2 = time_depart.getTime();
        }

        Log.e("milliSec1", "" + milliSec1);
        Log.e("milliSec2", "" + milliSec2);

        long timeDifInMilliSec;
        if (milliSec1 >= milliSec2)
        {
            timeDifInMilliSec = milliSec1 - milliSec2;
        }
        else
        {
            timeDifInMilliSec = milliSec2 - milliSec1;
        }

        long timeDifMinutes = timeDifInMilliSec / (60 * 1000);
        Log.e("timeDifMinutes", "" + timeDifMinutes);

        return timeDifMinutes;

    }

    private void setActionBar()
    {
        try
        {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            TextView txt_toolbar = (TextView) toolbar.findViewById(R.id.txt_titleTV);
            txt_toolbar.setText("Just Once");
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
                {
                    supportFinishAfterTransition();
                }
                else
                {
                    finish();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        Just_Once_offerRide.this.finish();
    }

    private void method_round_trip()
    {
        btn_round_trip.setBackgroundResource(R.mipmap.return_yellow);
        btn_single_trip.setBackgroundResource(R.mipmap.singletripbtn);
        rel_return.setVisibility(View.VISIBLE);
        static_string = "round";

        if (array_midPoints != null)
        {
            if (array_midPoints.size() > 0)
            {
                if (array_midPoints.size() > 1)
                {
                    Points_to_post = android.text.TextUtils.join(",", array_midPoints);
                }
                else
                {
                    String result = "";
                    for (String s : array_midPoints)
                    {
                        result += s;
                    }
                    Points_to_post = result;
                }

            }
            else
            {
                String get_mid_pnt = autocomplete_new_entry_midpnt.getText().toString();
                if (get_mid_pnt != null)
                {
                    Points_to_post = get_mid_pnt;
                }
            }
        }
        else
        {
            String get_mid_pnt = autocomplete_new_entry_midpnt.getText().toString();
            Log.e("String without mid", "get_mid_pnt " + get_mid_pnt);
            Points_to_post = get_mid_pnt;
        }

    }

    private void method_mid_point_add()
    {
        String get_text_midpnt         = autocomplete_new_entry_midpnt.getText().toString() + ":" + strngmid_lat + ":" + strng_mid_long;
        String get_text_midpnt_to_show = autocomplete_new_entry_midpnt.getText().toString();
        if (get_text_midpnt != null)
        {
            if (!get_text_midpnt.equals(""))
            {
                if (get_text_midpnt.length() > 0)
                {
                    if (get_text_midpnt.contains(","))
                    {
                    }

                    if (get_text_midpnt != null)
                    {

                        array_midPoints.add(get_text_midpnt);
                        array_mid_pnt_to_show.add(get_text_midpnt_to_show);
                        list_midpoint.setAdapter(adater);
                        adater.notifyDataSetChanged();
                        GlobalConstants.setListViewHeightBasedOnItems(list_midpoint, array_midPoints.size());
                        autocomplete_new_entry_midpnt.setText("");
                        //Added
                        strngmid_lat = "0.0";
                        strng_mid_long = "0.0";

                    }
                }
                else
                {
                    snackbar_method("Enter valid mid point to add");
                }
            }
            else
            {
                snackbar_method("Enter valid mid points to add");
            }
        }
        else
        {
            snackbar_method("Enter valid mid points to add");
        }
    }

    private void method_check_validations()
    {
        //        Log.e("RATEVALIDATION", "" + !seat_RateValidation(txt_rate_per_seat.getText().toString().trim()));
        //        Log.e("strng_lat_from", "" + strng_lat_from);
        //        // Thu Jan 01 15:48:00 GMT+05:30 1970
        //        Log.e("test", "" + test);
        //
        //        Log.e("return_date", "" + ((date_depart.equals(date_return)) && (date_depart.before(date_return))));
        Calendar cal             = Calendar.getInstance();
        Date     latest_datetime = cal.getTime();
        Date     date_latest     = null;
        //Added
        Date testlatest = null;
        //For departure time validation when current and departure dates are same
        String latest_dateStr = sdf.format(latest_datetime);
        try
        {
            date_latest = sdf.parse(latest_dateStr);
            //Added
            String str = dateFormat1.format(latest_datetime);
            testlatest = dateFormat1.parse(str);
        }
        catch (ParseException e)
        {
            Log.e("Exception e", "@@@@@@@@@@@" + e.toString());
        }
        Log.e("checking", "" + (date_depart.equals(date_latest) && !test.after(testlatest)));
        Log.e("time_depart", "" + time_depart);
        //        Log.e("latest_datetime", "" + latest_datetime);
        //        Log.e("Log check", "" + (test.after(testlatest)));
        //        Log.e("Log check", "" + (!test.after(testlatest)));
        //        Log.e("Log check", "" + ( date_depart.equals(date_latest) && !test.after(testlatest)));

        //For Single Trip
        if (static_string != "round")
        {
            if (autocompleteFrom.getText().toString().length() <= 0)
            {
                snackbar_method("Enter departure point");
            }
            else if (autocompleteTo.getText().toString().length() <= 0)
            {
                snackbar_method("Enter destination point");
            }
            else if (strng_lat_from.equals("0.0") || strng_lat_from.equals("0") || strng_lat_from.equals(""))
            {
                snackbar_method("Select leaving point from dropdown only");
            }
            else if (strng_lat_to.equals("0.0") || strng_lat_to.equals("0") || strng_lat_to.equals(""))
            {
                snackbar_method("Select leaving to from dropdown only");
            }
            else if (txt_departure_date.getText().toString().length() <= 0)
            {
                snackbar_method("Enter departure date");
            }
            else if (txt_departure_time.getText().toString().length() <= 0)
            {

                snackbar_method("Enter departure time");
            }
            else if (date_depart.equals(date_latest) && !test.after(testlatest))
            {
                Log.e("LOGGGG ", "after" + time_depart.after(latest_datetime));
                snackbar_method("Please update your departure time.");
                txt_departure_time.setText("");

            }
            //else if (depart_date.equals(date_latest) && !time_depart.after(latest_datetime)) {
            else if (date_depart.equals(date_latest) && !time_depart.after(latest_datetime))//Wed Feb 17 18:29:00 GMT+05:30 2016
            {
                Log.e("Log", "EQUA" + !(time_difference(3, latest_datetime) >= 5));

                Log.e("LOGGGG ", "after" + time_depart.after(latest_datetime));
                snackbar_method("Please update your departure time.");
                txt_departure_time.setText("");

            }
            //            else if (depart_date.equals(date_latest) && !(time_difference(3, latest_datetime) >= 5)) {
            else if (date_depart.equals(date_latest) && !(time_difference(3, latest_datetime) >= 5))
            {
                snackbar_method("Please update your departure time.");
                txt_departure_time.setText("");
            }
            //            else if (depart_date.before(date_latest)) {
            else if (date_depart.before(date_latest))
            {
                snackbar_method("Please update your departure date and time.");
                txt_departure_date.setText("");
                txt_departure_time.setText("");
            }
            else if (txt_number_Seats.getText().toString().length() <= 0)
            {
                snackbar_method("Enter seat available");
            }
            else if (txt_number_Seats.getText().toString().equals("0"))
            {
                snackbar_method("Seat available cannot be 0");
            }
            else if (txt_rate_per_seat.getText().toString().length() <= 0)
            {
                snackbar_method("Enter seat cost");
            }
            else if (!seat_RateValidation(txt_rate_per_seat.getText().toString().trim()))
            {
                snackbar_method("Please enter appropriate  seat rate.");
            }
            else if ((edt_car_name.getText().toString().trim()).length() <= 0)
            {
                snackbar_method("Enter vehicle name");
            }
            else if ((edt_car_number.getText().toString().trim()).length() <= 0)
            {
                snackbar_method("Enter vehicle number");
            }
            else if (txt_number_Seats.getText().toString().equals("0"))
            {
                snackbar_method("Seat available cannot be 0");
            }
            else if (autocomplete_new_entry_midpnt.getText().toString().length() > 0)
            {
                snackbar_method("Commit your mid point first");
            }
            else if (txt_car_top.getText().toString().equals("Bike Information"))
            {

                if (txt_number_Seats.getText().toString().equals("1"))
                {
                    HitService();
                }
                else
                {
                    snackbar_method("Seat available for bike has to be 1");
                }
            }
            else
            {
                HitService();
            }

            //For Round Trip

        }
        else if (static_string == "round")
        {
            if (autocompleteFrom.getText().toString().length() <= 0)
            {
                snackbar_method("Enter departure point");
            }
            else if (autocompleteTo.getText().toString().length() <= 0)
            {
                snackbar_method("Enter destination point");
            }
            else if (strng_lat_from.equals("0.0") || strng_lat_from.equals("0") || strng_lat_from.equals(""))
            {
                snackbar_method("Select leaving point from dropdown only");
            }
            else if (strng_lat_to.equals("0.0") || strng_lat_to.equals("0") || strng_lat_to.equals(""))
            {
                snackbar_method("Select leaving to from dropdown only");
            }
            else if (txt_departure_date.getText().toString().length() <= 0)
            {
                snackbar_method("Enter departure date");
            }
            else if (txt_departure_time.getText().toString().length() <= 0)
            {
                snackbar_method("Enter departure time");
            }
            else if (txt_return_date.getText().toString().length() <= 0)
            {
                snackbar_method("Enter return date");
            }
            else if (txt_return_time.getText().toString().length() <= 0)
            {
                snackbar_method("Enter return time");
            }
            else if (date_depart.equals(date_latest) && !test.after(testlatest))
            {
                Log.e("LOGGGG ", "after" + time_depart.after(latest_datetime));
                snackbar_method("Please update your departure and return time.");
                txt_departure_time.setText("");
                txt_return_time.setText("");

            }
            //            else if (depart_date.equals(date_latest) && !time_depart.after(latest_datetime)) {
            else if (date_depart.equals(date_latest) && !time_depart.after(latest_datetime))
            {
                Log.e("LOGGGG ", "after" + time_depart.after(latest_datetime));
                snackbar_method("Please update your departure and return time.");
                txt_departure_time.setText("");
                txt_return_time.setText("");

            }
            //            else if (depart_date.equals(date_latest) && !(time_difference(3, latest_datetime) >= 5)) {
            else if (date_depart.equals(date_latest) && !(time_difference(3, latest_datetime) >= 5))
            {
                Log.e("LOGGGG ", "(((((((((((" + (time_difference(3, latest_datetime) >= 5));
                snackbar_method("Please update your departure and return time.");
                txt_departure_time.setText("");
                txt_return_time.setText("");
            }
            //            else if (depart_date.before(date_latest)) {
            else if (date_depart.before(date_latest))
            {
                snackbar_method("Please update your date and time.");
                txt_departure_date.setText("");
                txt_departure_time.setText("");
                txt_return_date.setText("");
                txt_return_time.setText("");
            }
            else if (!(date_depart.equals(date_return)) && !(date_depart.before(date_return)))
            {
                snackbar_method("Please update your date and time.");
                txt_departure_date.setText("");
                txt_departure_time.setText("");
                txt_return_date.setText("");
                txt_return_time.setText("");
            }
            else if (txt_number_Seats.getText().toString().length() <= 0)
            {
                snackbar_method("Enter seat available");
            }
            else if (txt_number_Seats.getText().toString().equals("0"))
            {
                snackbar_method("Seat available cannot be 0");
            }
            else if (txt_rate_per_seat.getText().toString().length() <= 0)
            {
                snackbar_method("Enter seat cost");
            }
            else if (!seat_RateValidation(txt_rate_per_seat.getText().toString().trim()))
            {
                snackbar_method("Please enter appropriate  seat rate.");
            }
            else if ((edt_car_name.getText().toString().trim()).length() <= 0)
            {
                snackbar_method("Enter vehicle name");
            }
            else if ((edt_car_number.getText().toString().trim()).length() <= 0)
            {
                snackbar_method("Enter vehicle number");
            }
            else if (txt_number_Seats.getText().toString().equals("0"))
            {
                snackbar_method("Seat available cannot be 0");
            }
            else if (autocomplete_new_entry_midpnt.getText().toString().length() > 0)
            {
                snackbar_method("Commit your mid point first");
            }
            else if (txt_car_top.getText().toString().equals("Car Information"))
            {
                HitService();
            }
            else if (txt_car_top.getText().toString().equals("Bike Information"))
            {
                if (!txt_number_Seats.getText().toString().equals("1"))
                {
                    snackbar_method("Seat available for bike has to be 1");
                }
                else
                {
                    HitService();
                }
            }
            else
            {
                HitService();
            }

        }
    }

    /**
     @HitService(Delete)
     */
    private void HitService_DeleteItem()
    {

        HashMap<String, String> data_home = new HashMap<>();
        data_home.put("CustomerId", customerID);

        if (!cd.isConnectingToInternet())
        {
            snackbar_method("No internet connection!");
        }
        else
        {
            Json_AsnycTask task = new Json_AsnycTask(Just_Once_offerRide.this, "http://112.196.34.42:9091/Trip/DeleteTrip?TripId=" + get_trip_id, GlobalConstants.GET_SERVICE_METHOD1, null);
            task.setOnResultsListener(this);
            task.execute();
            dialog = ProgressDialog.show(Just_Once_offerRide.this, "", "Deleting. Please wait...", true);
            dialog.setCancelable(true);
            dialog.show();

        }
    }

    /**
     @HitService(SaveTRIP)
     */
    public void HitService()
    {
        if (array_midPoints != null)
        {

            if (array_midPoints.size() > 0)
            {
                if (array_midPoints.size() > 1)
                {
                    Points_to_post = android.text.TextUtils.join(";", array_midPoints);
                }
                else
                {
                    String result = "";
                    for (String s : array_midPoints)
                    {
                        result += s;
                    }
                    Points_to_post = result;
                }
                data_ofer_ride.put("Points", Points_to_post);
            }
        }

        auto_from = autocompleteFrom.getText().toString();
        auto_to = autocompleteTo.getText().toString();

        data_ofer_ride.put("Description", edt_car_type.getText().toString());
        data_ofer_ride.put("IsRegulerBasis", "false");
        if (static_string.equals("round"))
        {
            data_ofer_ride.put("RoundTrip", "true");
            data_ofer_ride.put("ReturnDate", txt_return_date.getText().toString());
            data_ofer_ride.put("ReturnTime", txt_return_time.getText().toString());
        }
        else
        {
            data_ofer_ride.put("RoundTrip", "false");
        }
        data_ofer_ride.put("VehicleName", edt_car_name.getText().toString());
        data_ofer_ride.put("RatePerSeat", txt_rate_per_seat.getText().toString());
        data_ofer_ride.put("DepartureDate", txt_departure_date.getText().toString());
        data_ofer_ride.put("VehicleType", tvw_car_ype.getText().toString());
        data_ofer_ride.put("LeavingDate", txt_departure_date.getText().toString());
        data_ofer_ride.put("LeavingFrom", auto_from + ":" + strng_lat_from + ":" + strng_long_from);
        data_ofer_ride.put("DepartureTime", txt_departure_time.getText().toString());
        data_ofer_ride.put("CustomerId", customerID);
        data_ofer_ride.put("NoOfSeats", txt_number_Seats.getText().toString());
        data_ofer_ride.put("VehicleNo", edt_car_number.getText().toString());
        data_ofer_ride.put("LeavingTo", auto_to + ":" + strng_lat_to + ":" + strng_long_to);
        data_ofer_ride.put("LeavingTime", txt_departure_time.getText().toString().trim());
        if (txt_car_top.getText().toString().equals("Car Information"))
        {
            data_ofer_ride.put("VehicleType", "Car");
        }
        else
        {
            data_ofer_ride.put("VehicleType", "Bike");
        }

        if (!cd.isConnectingToInternet())
        {
            snackbar_method("No internet connection!");
        }
        else
        {
            if (btn_select_ride.getText().toString().equals("SAVE CHANGES"))
            {
                data_ofer_ride.put("TripID", get_trip_id);
                final Json_AsnycTask tsk = new Json_AsnycTask(Just_Once_offerRide.this, "http://112.196.34.42:9091/Trip/UpdateTrip", GlobalConstants.POST_SERVICE_METHOD1, data_ofer_ride);
                tsk.setOnResultsListener(this);
                tsk.execute();
                dialog = ProgressDialog.show(Just_Once_offerRide.this, "", "Loading. Please wait...", true);
                dialog.setCancelable(false);
                dialog.show();
            }
            else
            {

                Log.e("Just data_ofer_ride ", "" + data_ofer_ride);
                final Json_AsnycTask tsk = new Json_AsnycTask(Just_Once_offerRide.this, GlobalConstants.OFFER_RIDE, GlobalConstants.POST_SERVICE_METHOD1, data_ofer_ride);
                tsk.setOnResultsListener(this);
                tsk.execute();
                dialog = ProgressDialog.show(Just_Once_offerRide.this, "", "Loading. Please wait...", true);
                dialog.setCancelable(false);
                dialog.show();
            }
        }
    }

    @Override
    public void onResultsSucceeded_Get_Method1(JSONObject result)
    {

        Log.e("response home del trip", "del==" + result);
        dialog.dismiss();
        if (result != null)
        {
            if (result.optString("Status").equals("success"))
            {
                Toast.makeText(getApplicationContext(), "Trip successfully saved", Toast.LENGTH_LONG).show();
                Just_Once_offerRide.this.finish();
            }
            else
            {
                snackbar_method("Try Again!!");
            }
        }
    }

    @Override
    public void onResultsSucceeded_Get_Method2(JSONObject result)
    {

    }

    /**
     @ResultHandled
     */
    @Override
    public void onResultsSucceeded_Post_Method1(JSONObject result)
    {
        dialog.dismiss();
        Log.e("response 1", "Login==" + result);
        if (result != null)
        {
            if (result.optString("Message").equals("Internal Server Error."))
            {
                snackbar_method("Try Again!! Internal server error");
            }
            else if (result.optString("Message").equals("This trip is already busy on this date."))
            {

                snackbar_method("Already you have a trip on same day, delete to add new.");
            }
            else if (result.optString("Status").equals("success"))
            {
                if (btn_select_ride.getText().toString().equals("SAVE CHANGES"))
                {
                    Just_Once_offerRide.this.finish();
                    Toast.makeText(getApplicationContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Trip successfully saved", Toast.LENGTH_LONG).show();
                    Just_Once_offerRide.this.finish();
                }

            }
            else
            {
                snackbar_method("Sorry not able to add trip, try again");
            }
        }
        else
        {
            snackbar_method("Sorry not able to add trip, try again");
        }
    }

    @Override
    public void onResultsSucceeded_Post_Method2(JSONObject result)
    {

    }

    @Override
    public void onResultsSucceeded_Post_Method3(JSONObject result)
    {

    }

    /**
     @CarBikeRadioSelection
     */
    public void addListenerOnButton()
    {

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId)
            {
                int selectedId = radioGroup.getCheckedRadioButtonId();

                radioButton = (RadioButton) findViewById(selectedId);
                if (txt_car_top.getText().toString().equals("Car Information"))
                {
                    selectedId = R.id.radiobike;
                }
                if (txt_car_top.getText().toString().equals("Bike Information"))
                {
                    selectedId = R.id.radiocar;
                }
                if (selectedId == R.id.radiocar)
                {
                    tvw_car_ype.setVisibility(View.VISIBLE);
                    edt_car_type.setVisibility(View.VISIBLE);
                    txt_car_top.setText("Car Information");
                    txtvwcar_name.setText("Car");
                    tvw_car_ype.setText("Type");
                    txt_vw_txt_carnmbr.setText("Car number");
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lnr_vehicle_nmber.getLayoutParams();
                    params.setMargins(0, 20, 0, 20);
                    lnr_vehicle_nmber.setLayoutParams(params);
                    edt_car_number.setText("");
                    edt_car_name.setText("");
                    edt_car_type.setText("");
                    txt_number_Seats.setText("");
                    txt_rate_per_seat.setText("");

                }
                else if (selectedId == R.id.radiobike)
                {
                    txt_car_top.setText("Bike Information");
                    txtvwcar_name.setText("Bike");
                    tvw_car_ype.setVisibility(View.GONE);
                    edt_car_type.setVisibility(View.GONE);
                    txt_vw_txt_carnmbr.setText("Bike number");
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lnr_vehicle_nmber.getLayoutParams();
                    params.setMargins(0, 10, 0, 20);
                    lnr_vehicle_nmber.setLayoutParams(params);
                    edt_car_number.setText("");
                    edt_car_name.setText("");
                    edt_car_type.setText("");

                    txt_number_Seats.setText("");
                    txt_rate_per_seat.setText("");
                }
            }
        });
    }

    /***
     @CarTypeDialog
     */
    public void showdialogcartype()
    {
        final Dialog dialog = new Dialog(Just_Once_offerRide.this);
        dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_car_dialog);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button set    = (Button) dialog.findViewById(R.id.btn_set);
        radioGroup = (RadioGroup) dialog.findViewById(R.id.radiogrp);

        if (edt_car_type.getText().toString().equals(""))
        {
            radioGroup.check(R.id.radio_sedan);
        }
        else if (edt_car_type.getText().toString().equals("Sedan"))
        {
            radioGroup.check(R.id.radio_sedan);
        }
        else if (edt_car_type.getText().toString().equals("Audi"))
        {
            radioGroup.check(R.id.radio_audi);
        }
        else if (edt_car_type.getText().toString().equals("Mini Cab"))
        {
            radioGroup.check(R.id.radio_minicab);
        }
        else if (edt_car_type.getText().toString().equals("MUV"))
        {
            radioGroup.check(R.id.radio_muv);
        }
        else if (edt_car_type.getText().toString().equals("SUV"))
        {
            radioGroup.check(R.id.radio_suv);
        }
        else if (edt_car_type.getText().toString().equals("Roadster"))
        {
            radioGroup.check(R.id.radio_roadster);
        }
        else if (edt_car_type.getText().toString().equals("Estate"))
        {
            radioGroup.check(R.id.radio_estate);
        }
        else if (edt_car_type.getText().toString().equals("Coupe"))
        {
            radioGroup.check(R.id.radio_coupe);
        }
        else if (edt_car_type.getText().toString().equals("HatchBack"))
        {
            radioGroup.check(R.id.radio_hatchback);
        }
        else if (edt_car_type.getText().toString().equals("Cross Over"))
        {
            radioGroup.check(R.id.radio_cross);
        }

        cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                int         selectedId  = radioGroup.getCheckedRadioButtonId();
                RadioButton radioButton = (RadioButton) dialog.findViewById(selectedId);
                edt_car_type.setText(radioButton.getText());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     @MidPointAdapter
     */

    private class MidPointAdapter extends BaseAdapter
    {
        public MidPointAdapter()
        {
            super();
        }

        @Override
        public int getCount()
        {

            //            Log.e("Get COunt","CCCCCCC"+array_midPoints.size());
            return array_midPoints.size();
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
            Log.e("Get View", "getView" + array_midPoints.size());
            LayoutInflater inflater = LayoutInflater.from(Just_Once_offerRide.this);
            View           v        = inflater.inflate(R.layout.custom_midpnt, parent, false);

            TextView  add_txt_tolist = (TextView) v.findViewById(R.id.autocmplete_mid);
            ImageView img_cros       = (ImageView) v.findViewById(R.id.img_cros);
            Log.e("Adapter", "SIZE" + array_mid_pnt_to_show.size());
            if (array_mid_pnt_to_show != null)
            {
                add_txt_tolist.setText(array_mid_pnt_to_show.get(position));
            }

            img_cros.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    array_midPoints.remove(position);
                    array_mid_pnt_to_show.remove(position);
                    notifyDataSetChanged();
                    GlobalConstants.setListViewHeightBasedOnItems(list_midpoint, array_midPoints.size());
                }
            });

            return v;
        }

        @Override
        public void notifyDataSetChanged()
        {
            super.notifyDataSetChanged();
        }

        @Override
        public void notifyDataSetInvalidated()
        {
            super.notifyDataSetInvalidated();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {

    }

    //=======
    private void departuretime()
    {
        if (txt_departure_date.getText().toString().length() > 0)
        {
            set_depart_time();
        }
        else
        {
            txt_departure_date.setText("");
            snackbar_method("Enter depart date first");
        }
    }

    //========
    private void midpoint_imagemethod()
    {
        Log.e("MidLat", "LAt  " + strngmid_lat + "LON " + strng_mid_long);
        Log.e("Midpont", "" + ((autocomplete_new_entry_midpnt.getText().toString().trim().length() > 0) && (!(strngmid_lat.equalsIgnoreCase("0.0")) || !(strng_mid_long.equalsIgnoreCase("0.0")))));
        if (((autocomplete_new_entry_midpnt.getText().toString().trim().length() > 0) && (!(strngmid_lat.equalsIgnoreCase("0.0")) || !(strng_mid_long.equalsIgnoreCase("0.0")))))
        {
            method_mid_point_add();
        }
        else
        {
            //  snackbar_method("Enter mid points to commit");
            snackbar_method("Enter specific mid stations to commit");
        }
    }

    //========Single Trip
    private void btn_singletrip_method()
    {
        btn_round_trip.setBackgroundResource(R.mipmap.singletripbtn);
        btn_single_trip.setBackgroundResource(R.mipmap.return_yellow);
        rel_return.setVisibility(View.GONE);
        static_string = "single";
    }

    //=======Depart DatePicker

    private void depart_datepicker_method()
    {
        Calendar c      = Calendar.getInstance();
        int      mYear  = c.get(Calendar.YEAR);
        int      mMonth = c.get(Calendar.MONTH);
        int      mDay   = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(Just_Once_offerRide.this, new mDateSetListener_depart(), mYear, mMonth, mDay);

        dialog.show();
    }

    //======Return DatePicker
    private void return_datepicker_method()
    {
        Calendar c      = Calendar.getInstance();
        int      mYear  = c.get(Calendar.YEAR);
        int      mMonth = c.get(Calendar.MONTH);
        int      mDay   = c.get(Calendar.DAY_OF_MONTH);
        System.out.println("the selected " + mDay);
        DatePickerDialog dialog = new DatePickerDialog(Just_Once_offerRide.this, new mDateSetListener_return(), mYear, mMonth, mDay);

        dialog.show();
    }

    //=====Return Time
    private void return_timepicker_method()
    {
        if (txt_return_date.getText().toString().length() > 0)
        {

            if (txt_departure_time.getText().toString().length() > 1)
            {
                set_return_time();
            }
            else
            {
                snackbar_method("Enter time of depart first");
            }
        }
        else
        {
            snackbar_method("Enter date of return first");
        }

    }

    //=====Cancel Button

    private void btn_cancel_method()
    {
        if (btn_cancel_ride.getText().toString().equals("DELETE RIDE"))
        {
            final Dialog dialog = new Dialog(Just_Once_offerRide.this);
            dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.mobile_custom_verify);
            dialog.setCancelable(false);

            TextView text = (TextView) dialog.findViewById(R.id.text);
            Button ok = (Button) dialog.findViewById(R.id.ok);
            Button cancel = (Button) dialog.findViewById(R.id.cancel);
            final EditText edt_mobile = (EditText) dialog.findViewById(R.id.text_mobile);
            edt_mobile.setVisibility(View.GONE);
            text.setText("Do you really want to delete your trip");

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            dialog.show();
            dialog.getWindow().setAttributes(lp);

            ok.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    dialog.dismiss();

                    HitService_DeleteItem();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {

                    dialog.dismiss();

                }
            });

            dialog.show();
        }
        else
        {
            Just_Once_offerRide.this.finish();
        }

    }

    //==============
    @Override
    public void onClick(View v)
    {
        int id = v.getId();
        switch (id)
        {
            case R.id.lnr_clk_depart:
                departuretime();
                break;
            case R.id.edt_car_type:
                showdialogcartype();
                break;
            case R.id.btn_select:
                method_check_validations();
                break;
            //            case R.id.img_add_midpnt:
            //                midpoint_imagemethod();
            //                break;
            case R.id.btn_round_trip:
                method_round_trip();
                break;

            case R.id.btn_single_trip:
                btn_singletrip_method();
                break;

            case R.id.lnr_return_date:
                return_datepicker_method();
                break;

            case R.id.lnr_depart_date:
                depart_datepicker_method();
                break;

            case R.id.lnr_vw_seat_nmbr:
                txt_number_Seats.requestFocus();
                break;

            case R.id.lnr_rate:
                txt_rate_per_seat.requestFocus();
                break;

            case R.id.btn_cancel:
                btn_cancel_method();
                break;
            default:
                break;

        }

    }

    //Added Validations for Seat Rate
    private boolean seat_RateValidation(String seat_Rate)
    {

        if (seat_Rate.contains("."))
        {
            return decmalNumber(seat_Rate);
        }
        else
        {
            return without_decimalNumber(seat_Rate);
        }

    }

    private boolean without_decimalNumber(String seat_Rate)
    {

        char char_array[] = seat_Rate.toCharArray();

        for (int i = 0; i < char_array.length; i++)
        {
            if (char_array[i] != '0')
            {
                return true;
            }

        }
        return false;
    }

    private boolean decmalNumber(String seat_Rate)
    {
        String   st    = "";
        String[] array = seat_Rate.split("\\.");
        Log.e("Contains", "" + seat_Rate.contains("."));
        Log.e("Split", "" + array.length);
        for (int i = 0; i < array.length; i++)
        {
            st = st + array[i];
        }

        Log.e("decimal Return", "" + without_decimalNumber(st) + " 55555   " + st);
        return without_decimalNumber(st);
    }
    //=========MidStations Name==============

    private String[] mid_stationsNames(String midStations)
    {
        String[] stationsName_ = midStations.split(":");

        return stationsName_;
    }

}
