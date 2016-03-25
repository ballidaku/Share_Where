package car.sharewhere.gagan.sharewherecars;

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
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import car.sharewhere.gagan.utills.GlobalConstants;
import car.sharewhere.gagan.WebServices.Json_AsnycTask;
import car.sharewhere.gagan.model.Latlng_data;
import car.sharewhere.gagan.utills.ConnectivityDetector;

public class RegularBasis extends AppCompatActivity implements Asnychronus_notifier, View.OnClickListener
{

    AutoCompleteTextView autocompleteTo, autocompleteFrom, autocomplete_new_entry_midpnt;
    Button set, btn_select_ride, btn_cancel_ride, btn_line_select, btn_line_cancel;
    TextView txt_depart_tym, txt_return_tytm, txt_depart_set_tym, txt_return_set_tym, txt_days_data;
    TextView txt_car_top, txtvwcar_name, tvw_car_ype, txt_vw_txt_carnmbr, btn_single_trip, btn_round_trip;
    ImageView img_depart_tym, img_add_days;
    LinearLayout rel_return;
    EditText     edt_car_name, edt_car_number, txt_number_Seats, txt_rate_per_seat, edt_car_type;
    static String static_string = "";
    private Calendar calendar;
    private int      year, month, day;
    ImageView img_add_mid_pnt, img_clk_depart, img_return_calender, img_return_clk;
    ConnectivityDetector cd;
    SharedPreferences    preferences;
    String               customerID, get_trip_id, tripID;
    private TimePicker timePicker;
    private String format = "";
    StringBuilder strng_time_depart, strng_time_return;
    LinearLayout regulr_return_lnr, lnr_depart_time_regular, lnr_depart_date, lnr_clk_depart, lnr_main_days, lnr_rate, lnr_vw_seat_nmbr;
    HashMap<String, String> data_ofer_ride = new HashMap<>();
    StringBuilder date_bulider;
    private RadioGroup  radioGroup;
    private RadioButton radioButton;
    ProgressDialog dialog;
    RelativeLayout lnr_vehicle_nmber;
    //    ListView       list_midpoint;
    ArrayList<String> array_midPoints          = new ArrayList<>();
    ArrayList<String> array_midPoints_to_show  = new ArrayList<>();
    ArrayList<String> array_midPoints_add_city = new ArrayList<>();
    String Points_to_post, auto_from, auto_to, get_intent_depart_time, get_intent_return_time, get_intent_depart_date;
    private CoordinatorLayout coordinatorLayout;
    Snackbar        snackbar;
    Date            time_return;
    GetCityHelperG_ GET_CITY_G;
    String strng_lat_from  = "0.0";
    String strng_lat_to    = "0.0";
    String strng_long_from = "0.0";
    String strng_long_to   = "0.0";
    String strngmid_lat    = "0.0";
    String strng_mid_long  = "0.0";

    String sunday  = "";
    String monday  = "";
    String tuesday = "";
    String wedndy  = "";
    String thursdy = "";
    String fridy   = "";
    String strdy   = "";
    Intent       get_intent;
    //    MidPointAdapter adater;
    TextView     txtv_currency;
    Context      con;
    LinearLayout lay_mid_point;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.just_once_final);

        con = this;

        setActionBar();
        findViewbyID();
        get_intent = getIntent();

        View view = this.getCurrentFocus();

        if (view != null)
        {
            hide_keyboard(view);
        }

        /**
         * @initialisations
         */
        preferences = PreferenceManager.getDefaultSharedPreferences(RegularBasis.this);
        customerID = preferences.getString("CustomerId", null);

        cd = new ConnectivityDetector(RegularBasis.this);
        //        adater = new MidPointAdapter();

        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        date_bulider = new StringBuilder().append(year).append("-").append(month + 1).append("-").append(day).append(" ");
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        addListenerOnButton();

        txtv_currency.setText(preferences.getString("currency_symbol", "\u20B9"));

    }

    private void setActionBar()
    {
        try
        {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            TextView txt_toolbar = (TextView) toolbar.findViewById(R.id.txt_titleTV);
            txt_toolbar.setText("Regular Basis");
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

    /**
     @ViewById
     */
    private void findViewbyID()
    {
        autocompleteTo = (AutoCompleteTextView) findViewById(R.id.autocompleteTo_just);
        autocompleteFrom = (AutoCompleteTextView) findViewById(R.id.autocompleteFrom_just);
        autocomplete_new_entry_midpnt = (AutoCompleteTextView) findViewById(R.id.autocomplete_new_mid_point);
        btn_single_trip = (TextView) findViewById(R.id.btn_single_trip);
        btn_round_trip = (TextView) findViewById(R.id.btn_round_trip);

        txt_depart_tym = (TextView) findViewById(R.id.txtvw_depart_date);
        txt_return_tytm = (TextView) findViewById(R.id.txtvw_depart_time);

        txt_days_data = (TextView) findViewById(R.id.txt_days_data);
        txt_depart_set_tym = (TextView) findViewById(R.id.txt_departure_date);
        txt_return_set_tym = (TextView) findViewById(R.id.txt_departure_time);
        img_depart_tym = (ImageView) findViewById(R.id.depart_calender_img);
        img_add_days = (ImageView) findViewById(R.id.img_add_days);
        rel_return = (LinearLayout) findViewById(R.id.rel_return);
        edt_car_name = (EditText) findViewById(R.id.edt_car_name);
        edt_car_type = (EditText) findViewById(R.id.edt_car_type);
        edt_car_number = (EditText) findViewById(R.id.edt_car_number);
        txt_number_Seats = (EditText) findViewById(R.id.txt_seat_number);
        txt_rate_per_seat = (EditText) findViewById(R.id.txt_rate_perseat);
        btn_select_ride = (Button) findViewById(R.id.btn_select);
        btn_cancel_ride = (Button) findViewById(R.id.btn_cancel);
        btn_line_select = (Button) findViewById(R.id.line_select);
        btn_line_cancel = (Button) findViewById(R.id.line_cancel);
        lnr_vw_seat_nmbr = (LinearLayout) findViewById(R.id.lnr_vw_seat_nmbr);
        lnr_rate = (LinearLayout) findViewById(R.id.lnr_rate);
        img_add_mid_pnt = (ImageView) findViewById(R.id.img_add_midpnt);
        regulr_return_lnr = (LinearLayout) findViewById(R.id.regulr_return_lnr);
        //For depart time
        lnr_depart_date = (LinearLayout) findViewById(R.id.lnr_depart_date);
        //For Return time
        lnr_clk_depart = (LinearLayout) findViewById(R.id.lnr_clk_depart);

        lnr_main_days = (LinearLayout) findViewById(R.id.lnr_main_days);
        lnr_depart_time_regular = (LinearLayout) findViewById(R.id.lnr_depart_time_regular);
        img_clk_depart = (ImageView) findViewById(R.id.clk_depart);
        //img_return_clk = (ImageView) findViewById(R.id.img_return_clk);
        //img_return_calender = (ImageView) findViewById(R.id.img_return_calendr);
        txt_car_top = (TextView) findViewById(R.id.car_info_txt_top);
        radioGroup = (RadioGroup) findViewById(R.id.radiogrp);
        tvw_car_ype = (TextView) findViewById(R.id.txt_v_car_tye);
        txt_vw_txt_carnmbr = (TextView) findViewById(R.id.txt_vw_txt_carnmbr);
        txtvwcar_name = (TextView) findViewById(R.id.txtcar_name_info);
        lnr_vehicle_nmber = (RelativeLayout) findViewById(R.id.lnr_vehicle_nmber);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        txtv_currency = (TextView) findViewById(R.id.txtv_currency);

        lay_mid_point = (LinearLayout) findViewById(R.id.lay_mid_point);

        //        list_midpoint = (ListView) findViewById(R.id.list_mid);

        txt_depart_tym.setText("Departure Time");
        txt_depart_set_tym.setHint("Set time");
        txt_return_tytm.setText("Return Time");
        txt_return_set_tym.setHint("Set time");
        img_depart_tym.setImageResource(R.mipmap.clock);
        rel_return.setVisibility(View.GONE);

        autocompleteFrom.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                final int DRAWABLE_LEFT   = 0;
                final int DRAWABLE_TOP    = 1;
                final int DRAWABLE_RIGHT  = 2;
                final int DRAWABLE_BOTTOM = 3;

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
                final int DRAWABLE_LEFT   = 0;
                final int DRAWABLE_TOP    = 1;
                final int DRAWABLE_RIGHT  = 2;
                final int DRAWABLE_BOTTOM = 3;

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

        GET_CITY_G = new GetCityHelperG_(RegularBasis.this, R.layout.layout_autocomplete);
        autocompleteFrom.setAdapter(GET_CITY_G);
        autocompleteTo.setAdapter(GET_CITY_G);
        autocomplete_new_entry_midpnt.setAdapter(GET_CITY_G);

        //
        //        // Setting an item click listener for the AutoCompleteTextView dropdown list
        autocompleteFrom.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int index, long id)
            {

                Latlng_data resultList = GET_CITY_G.set_LAT_Lng_value(index);

                strng_lat_from = String.valueOf(resultList.getLat());
                strng_long_from = String.valueOf(resultList.getLng());
                //    Log.e("lat long values", "" + resultList.getLat() + "  hgfhjg " + resultList.getLng());

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

                Latlng_data resultList = GET_CITY_G.set_LAT_Lng_value(index);
                strngmid_lat = String.valueOf(resultList.getLat());
                strng_mid_long = String.valueOf(resultList.getLat());
                if (((autocomplete_new_entry_midpnt.getText().toString().trim().length() > 0) && (!(strngmid_lat.equalsIgnoreCase("0.0")) || !(strng_mid_long.equalsIgnoreCase("0.0")))))
                {
                    method_add_mid_pnt();
                }
                else
                {
                    //  snackbar_method("Enter mid points to commit");
                    snackbar_method("Enter specific mid stations to commit");
                }

            }
        });

        edt_car_type.setFocusable(false);
        edt_car_type.setFocusableInTouchMode(false);

        edt_car_type.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                showdialogcartype();
            }
        });

        txt_depart_set_tym.setOnClickListener(this);
        img_add_days.setOnClickListener(this);
        btn_round_trip.setOnClickListener(this);
        btn_single_trip.setOnClickListener(this);
        img_add_mid_pnt.setOnClickListener(this);
        btn_select_ride.setOnClickListener(this);
        btn_cancel_ride.setOnClickListener(this);
        lnr_rate.setOnClickListener(this);
        lnr_vw_seat_nmbr.setOnClickListener(this);

        //        /**
        //         * @DepartTime
        //         */

        /*Return time*/
        lnr_clk_depart.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (txt_depart_set_tym.getText().toString().length() > 1)
                {
                    final Dialog dialog = new Dialog(RegularBasis.this);
                    dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog);
                    Button cancel = (Button) dialog.findViewById(R.id.btn_cancel);
                    set = (Button) dialog.findViewById(R.id.btn_set);
                    timePicker = (TimePicker) dialog.findViewById(R.id.timePicker1);
                    calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int min = calendar.get(Calendar.MINUTE);

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
                else
                {
                    snackbar_method("Enter depart time first");
                }
            }

            public void setTime(View view)
            {
                int hour = timePicker.getCurrentHour();
                int min  = timePicker.getCurrentMinute();
                showTime(hour, min);
            }

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

                int length = (int) (Math.log10(min) + 1);

                if (length == 1)
                {
                    strng_time_return = new StringBuilder().append(hour).append(":").append("0" + min).append(" ").append(format);
                }
                else
                {
                    strng_time_return = new StringBuilder().append(hour).append(":").append(min).append(" ").append(format);
                }
                if (min == 0)
                {
                    strng_time_return = new StringBuilder().append(hour).append(":").append("00").append(" ").append(format);
                }

                SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm aa");
                try
                {
                    time_return = dateFormat.parse(String.valueOf(strng_time_return));
                }
                catch (Exception ex)
                {

                }

                SimpleDateFormat sdf                = new SimpleDateFormat("hh:mm aa");
                Date             timeCompare_depart = null;
                Date             timeCompare_return = null;
                try
                {
                    timeCompare_depart = sdf.parse(txt_depart_set_tym.getText().toString());
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    timeCompare_return = sdf.parse(strng_time_return.toString());
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if (timeCompare_return.after(timeCompare_depart))
                    {
                        //   Log.e("log1 ", ".." + strng_time_return);
                        txt_return_set_tym.setText(strng_time_return);
                    }
                    else
                    {
                        snackbar_method("Return time has to me more than depart time");
                        txt_return_set_tym.setText("");
                    }
                }
                catch (Exception ex)
                {

                    // Log.e("Exception ", "" + ex.toString());
                }
            }
        });
/*Depart time*/

        lnr_depart_date.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                final Dialog dialog = new Dialog(RegularBasis.this);
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

            public void setTime(View view)
            {
                int hour = timePicker.getCurrentHour();
                int min  = timePicker.getCurrentMinute();
                showTime(hour, min);
            }

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
                    format = "PM";
                    hour -= 12;

                }
                else
                {
                    format = "AM";
                }

                int length = (int) (Math.log10(min) + 1);

                if (length == 1)
                {
                    strng_time_depart = new StringBuilder().append(hour).append(":").append("0" + min).append(" ").append(format);
                }
                else
                {
                    strng_time_depart = new StringBuilder().append(hour).append(":").append(min).append(" ").append(format);
                }

                if (min == 0)
                {
                    strng_time_depart = new StringBuilder().append(hour).append(":").append("00").append(" ").append(format);
                }
                //Log.e("First",""+strng_time_depart.toString());
                txt_depart_set_tym.setText(strng_time_depart.toString());
            }
        });

    }

    /**
     @MethodAddMidPoint
     */
    private void method_add_mid_pnt()
    {
        if (autocomplete_new_entry_midpnt.getText().length() <= 0)
        {
            snackbar_method("Enter mid point to add first");
        }
        else
        {
            String get_text_midpnt_to_show = autocomplete_new_entry_midpnt.getText().toString();

            String get_text_midpnt = get_text_midpnt_to_show + ":" + strngmid_lat + ":" + strng_mid_long;
            if (get_text_midpnt.contains(","))
            {
                //                        get_text_midpnt.replace(",", ";");
            }

            if (get_text_midpnt_to_show != null)
            {
                if (!get_text_midpnt_to_show.equals(""))
                {
                    if (get_text_midpnt.length() > 0)
                    {

                        boolean is_this_value_exists = false;
                        for (int i = 0; i < array_midPoints_to_show.size(); i++)
                        {
                            if (array_midPoints_to_show.get(i).equalsIgnoreCase(get_text_midpnt_to_show))
                            {
                                is_this_value_exists = true;
                            }
                        }

                        if (is_this_value_exists == false)
                        {
                            array_midPoints.add(get_text_midpnt);
                            array_midPoints_to_show.add(get_text_midpnt_to_show);

                            add_mid_points(get_text_midpnt_to_show);


                         /*   adater = new MidPointAdapter();
                            list_midpoint.setAdapter(adater);
                            adater.notifyDataSetChanged();
                            GlobalConstants.setListViewHeightBasedOnItems(list_midpoint, array_midPoints_to_show.size());*/

                            autocomplete_new_entry_midpnt.setText("");

                            //Added
                            strngmid_lat = "0.0";
                            strng_mid_long = "0.0";
                        }
                        else
                        {
                            snackbar_method("This location already added.");
                        }
                    }
                    else
                    {
                        snackbar_method("Enter valid mid point to add");
                    }
                }
                else
                {
                    snackbar_method("Enter valid mid point to add");
                }
            }
            else
            {
                snackbar_method("Enter valid mid point to add");
            }
        }
    }

    /**
     @MethodCheckValidations
     */
    private void method_check_validations()
    {
        //  Log.e("log3", ".." + txt_rate_per_seat.getText().toString().trim());
        //  Log.e("RATEVALIDATION",".."+seat_RateValidation(txt_rate_per_seat.getText().toString().trim()));
        if (static_string != "round")
        {

            if (autocompleteFrom.getText().toString().length() <= 0)
            {
                snackbar_method("Enter Leaving From");
            }
            else if (strng_lat_from.equals("0.0") || strng_lat_from.equals("0") || strng_lat_from.equals(""))
            {
                snackbar_method("Select Leaving From point from dropdown only");
            }
            else if (autocompleteTo.getText().toString().length() <= 0)
            {
                snackbar_method("Enter Leaving To");
            }

            else if (strng_lat_to.equals("0.0") || strng_lat_to.equals("0") || strng_lat_to.equals(""))
            {
                snackbar_method("Select Leaving To point from dropdown only");

            }
            else if (txt_depart_set_tym.getText().toString().length() <= 0)
            {
                snackbar_method("Enter departure time");
            }
            else if (txt_number_Seats.getText().toString().length() <= 0)
            {
                snackbar_method("Enter seats available");
            }
            else if (txt_number_Seats.getText().toString().equals("0"))
            {
                snackbar_method("Seat available cannot be 0");
            }
           /* else if (txt_rate_per_seat.getText().toString().trim().length() <= 0)
            {
                snackbar_method("Enter seat cost");

            }*/
           /* else if (!seat_RateValidation(txt_rate_per_seat.getText().toString().trim()))
            {
                snackbar_method("Please enter appropriate  seat rate.");
            }*/

            else if (edt_car_name.getText().toString().length() <= 0)
            {
                snackbar_method("Enter vehicle name");

            }
            else if (edt_car_number.getText().toString().length() <= 0)
            {
                snackbar_method("Enter vehicle number");
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
        }
        else if (static_string == "round")
        {
            if (autocompleteFrom.getText().toString().length() <= 0)
            {
                snackbar_method("Enter Leaving From");
            }
            else if (strng_lat_from.equals("0.0") || strng_lat_from.equals("0") || strng_lat_from.equals(""))
            {
                snackbar_method("Select Leaving From point from dropdown only");
            }
            else if (autocompleteTo.getText().toString().length() <= 0)
            {
                snackbar_method("Enter Leaving To");
            }

            else if (strng_lat_to.equals("0.0") || strng_lat_to.equals("0") || strng_lat_to.equals(""))
            {
                snackbar_method("Select Leaving To point from dropdown only");

            }
            else if (txt_depart_set_tym.getText().toString().length() <= 0)
            {
                snackbar_method("Enter departure time");
            }
            else if (txt_return_set_tym.getText().toString().length() <= 0)
            {
                snackbar_method("Enter return time");
            }
            else if (txt_number_Seats.getText().toString().length() <= 0)
            {
                snackbar_method("Enter seats available");
            }
            else if (txt_number_Seats.getText().toString().equals("0"))
            {
                snackbar_method("Seat available cannot be 0");
            }
           /* else if (txt_rate_per_seat.getText().toString().length() <= 0)
            {
                snackbar_method("Enter seat cost");

            }
            else if (!seat_RateValidation(txt_rate_per_seat.getText().toString().trim()))
            {
                snackbar_method("Please enter appropriate  seat rate.");
            }*/

            else if (edt_car_name.getText().toString().length() <= 0)
            {
                snackbar_method("Enter vehicle name");
            }
            else if (edt_car_number.getText().toString().length() <= 0)
            {
                snackbar_method("Enter vehicle number");
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
        }
    }

    /**
     @MethodSelectDays
     */
    private void method_select_days()
    {
        final Dialog dialog = new Dialog(RegularBasis.this);
        dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_day_dialog);
        dialog.setCancelable(false);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        final CheckBox chk_sunday   = (CheckBox) dialog.findViewById(R.id.chk_sunday);
        final CheckBox chk_monday   = (CheckBox) dialog.findViewById(R.id.chk_monday);
        final CheckBox chk_tuesday  = (CheckBox) dialog.findViewById(R.id.chk_tuesday);
        final CheckBox chk_wednsday = (CheckBox) dialog.findViewById(R.id.chk_wednsday);
        final CheckBox chk_thursday = (CheckBox) dialog.findViewById(R.id.chk_thursday);
        final CheckBox chk_Friday   = (CheckBox) dialog.findViewById(R.id.chk_Friday);
        final CheckBox chk_saturday = (CheckBox) dialog.findViewById(R.id.chk_saturday);

        if (txt_days_data.getText().toString().contains("Sunday"))
        {
            chk_sunday.setChecked(true);
        }
        else
        {
            chk_sunday.setChecked(false);
        }
        if (txt_days_data.getText().toString().contains("Monday"))
        {
            chk_monday.setChecked(true);
        }
        else
        {
            chk_monday.setChecked(false);
        }
        if (txt_days_data.getText().toString().contains("Tuesday"))
        {
            chk_tuesday.setChecked(true);
        }
        else
        {
            chk_tuesday.setChecked(false);
        }
        if (txt_days_data.getText().toString().contains("Wednesday"))
        {
            chk_wednsday.setChecked(true);
        }
        else
        {
            chk_wednsday.setChecked(false);
        }
        if (txt_days_data.getText().toString().contains("Thursday"))
        {
            chk_thursday.setChecked(true);
        }
        else
        {
            chk_thursday.setChecked(false);
        }
        if (txt_days_data.getText().toString().contains("Friday"))
        {
            chk_Friday.setChecked(true);
        }
        else
        {
            chk_Friday.setChecked(false);
        }
        if (txt_days_data.getText().toString().contains("Saturday"))
        {
            chk_saturday.setChecked(true);
        }
        else
        {
            chk_saturday.setChecked(false);
        }

        Button btn_cancel = (Button) dialog.findViewById(R.id.btn_cancel);
        Button btn_set    = (Button) dialog.findViewById(R.id.btn_set);

        btn_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog.dismiss();
            }
        });

        btn_set.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (chk_sunday.isChecked())
                {
                    sunday = "Sunday";
                }
                else
                {
                    sunday = "";
                }
                if (chk_monday.isChecked())
                {
                    if (sunday != "")
                    {
                        monday = ",Monday";
                    }
                    else
                    {
                        monday = "Monday";
                    }

                }
                else
                {
                    monday = "";
                }
                if (chk_tuesday.isChecked())
                {
                    if (sunday != "" || monday != "")
                    {
                        tuesday = ",Tuesday";
                    }
                    else
                    {
                        tuesday = "Tuesday";
                    }

                }
                else
                {
                    tuesday = "";
                }
                if (chk_wednsday.isChecked())
                {
                    if (sunday != "" || monday != "" || tuesday != "")
                    {
                        wedndy = ",Wednesday";
                    }
                    else
                    {
                        wedndy = "Wednesday";
                    }

                }
                else
                {
                    wedndy = "";
                }
                if (chk_thursday.isChecked())
                {
                    if (sunday != "" || monday != "" || wedndy != "" || tuesday != "")
                    {
                        thursdy = ",Thursday";
                    }
                    else
                    {
                        thursdy = "Thursday";
                    }

                }
                else
                {
                    thursdy = "";
                }
                if (chk_Friday.isChecked())
                {
                    if (sunday != "" || monday != "" || wedndy != "" || tuesday != "" || thursdy != "")
                    {
                        fridy = ",Friday";
                    }
                    else
                    {
                        fridy = "Friday";
                    }

                }
                else
                {
                    fridy = "";
                }
                if (chk_saturday.isChecked())
                {
                    if (sunday != "" || monday != "" || wedndy != "" || tuesday != "" || thursdy != "" || fridy != "")
                    {
                        strdy = ",Saturday";
                    }
                    else
                    {
                        strdy = "Saturday";
                    }

                }
                else
                {
                    strdy = "";
                }
                txt_days_data.setText(sunday.trim() + monday.trim() + tuesday.trim() +
                          wedndy.trim() +
                          thursdy.trim() + fridy.trim() + strdy.trim());
                dialog.dismiss();
            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        array_midPoints.clear();
        array_midPoints_to_show.clear();
        array_midPoints_add_city.clear();

        String from = get_intent.getStringExtra(GlobalConstants.Leaving_From);
        String to   = get_intent.getStringExtra(GlobalConstants.Leaving_To);
        get_intent_depart_date = get_intent.getStringExtra(GlobalConstants.Leaving_Date);

        String points         = get_intent.getStringExtra(GlobalConstants.Points);
        String vehicle_type   = get_intent.getStringExtra("vehicle_type");
        String vehicle_name   = get_intent.getStringExtra("vehicle_name");
        String vehicle_select = get_intent.getStringExtra("vehicle_select");
        String is_round       = get_intent.getStringExtra(GlobalConstants.Round_Trip);
        get_intent_return_time = get_intent.getStringExtra(GlobalConstants.Return_Time);
        String available_seat = get_intent.getStringExtra(GlobalConstants.Available_Seats);
        String rate           = get_intent.getStringExtra(GlobalConstants.Rate_seat);
        get_trip_id = get_intent.getStringExtra(GlobalConstants.KeyNames.TripId.toString());
        get_intent_depart_time = get_intent.getStringExtra(GlobalConstants.Leaving_Time);
        String ragulerDays = get_intent.getStringExtra(GlobalConstants.Regular_Days);
        String VehicleNo   = get_intent.getStringExtra(GlobalConstants.Vehicle_number);
        //  Log.e("RETURN TIME","^^^^^^^^ "+get_intent_return_time);
        if (from != null)
        {
            autocompleteFrom.setText(from);
            autocompleteFrom.setSelection(autocompleteFrom.length());
        }
        if (ragulerDays != null)
        {
            txt_days_data.setText(ragulerDays);
        }
        if (VehicleNo != null)
        {
            edt_car_number.setText(VehicleNo);
        }
        if (get_intent_depart_time != null)
        {
            //   Log.e("log5",""+get_intent_depart_time);
            txt_depart_set_tym.setText(get_intent_depart_time);
        }

        if ((get_intent_return_time != null) && !get_intent_return_time.equalsIgnoreCase(""))
        {
            if (!get_intent_return_time.equalsIgnoreCase("null") && !get_intent_return_time.equalsIgnoreCase(""))
            {
                // Log.e("log2", ".." + get_intent_return_time);
                txt_return_set_tym.setText(get_intent_return_time);
            }
            else
            {
                //  txt_return_set_tym.setText("Return Time");
            }
        }
        if (to != null)
        {
            autocompleteTo.setText(to);
            autocompleteTo.setSelection(autocompleteTo.length());
            btn_select_ride.setText("SAVE CHANGES");
            btn_cancel_ride.setText("DELETE RIDE");
        }
        if (vehicle_name != null)
        {
            edt_car_name.setText(vehicle_name);
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
        //   Log.e("Regular points",""+points);
        if (points != null)
        {
            String[] mid_stationsArray = points.split(";");
            int sizeMid_stations = mid_stationsArray.length;

            //                     for (int i = 0; i < (sizeMid_stations - 1); i++)
            for (int i = 0; i < sizeMid_stations; i++)
            {
                if (i == 0)
                {
                    // Log.e("Reg Else",""+sizeMid_stations);
                    String[] stationsName_ = mid_stationsNames(mid_stationsArray[i]);
                    strng_lat_from = stationsName_[1];
                    strng_long_from = stationsName_[2];
                    //   Log.e("Reg strng_lat_from",""+strng_lat_from);
                    //   Log.e("Reg strng_long_from",""+strng_long_from);
                }
                else if (i == (sizeMid_stations - 1))
                {
                    String[] stationsName_ = mid_stationsNames(mid_stationsArray[i]);
                    strng_lat_to = stationsName_[1];
                    strng_long_to = stationsName_[2];
                    // Log.e("Reg strng_lat_to",""+strng_lat_to);
                    // Log.e("Reg strng_long_to",""+strng_long_to);
                }
                else
                {
                    array_midPoints.add(mid_stationsArray[i]);
                    // Log.e("Reg array_midPoints", "" + array_midPoints);
                    String[] stationsName_ = mid_stationsNames(mid_stationsArray[i]);
                    array_midPoints_to_show.add(stationsName_[0]);

                    add_mid_points(stationsName_[0]);
                }
            }

            //  Log.e("Regular BasisMidpoints",""+array_midPoints_to_show);
          /*  list_midpoint.setAdapter(adater);
            GlobalConstants.setListViewHeightBasedOnItems(list_midpoint, array_midPoints_to_show.size());*/

        }

        //=======Added Above============================

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
            btn_single_trip.setBackgroundResource(R.drawable.grey_border);
            txt_return_tytm.setVisibility(View.VISIBLE);
            //  Log.e("log3",""+txt_return_set_tym.getText().toString());
            txt_return_set_tym.setHint("set return time");
        }
        else if (static_string.equals("single"))
        {
            btn_single_trip.setBackgroundResource(R.mipmap.return_yellow);
            btn_round_trip.setBackgroundResource(R.drawable.grey_border);
            txt_return_tytm.setVisibility(View.GONE);
            regulr_return_lnr.setVisibility(View.GONE);
        }
        else
        {
            static_string = "single";
            btn_single_trip.setBackgroundResource(R.mipmap.return_yellow);
            btn_round_trip.setBackgroundResource(R.drawable.grey_border);
            txt_return_tytm.setVisibility(View.GONE);
            regulr_return_lnr.setVisibility(View.GONE);
        }
        btn_line_cancel.setVisibility(View.INVISIBLE);
        data_ofer_ride.put("VehicleType", "Car");
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
            Json_AsnycTask task = new Json_AsnycTask(RegularBasis.this, GlobalConstants.Url+GlobalConstants.Url+"Trip/DeleteTrip?TripId=" + get_trip_id, GlobalConstants.GET_SERVICE_METHOD1, null);
            task.setOnResultsListener(this);
            task.execute();
            dialog = ProgressDialog.show(RegularBasis.this, "", "Deleting. Please wait...", true);
            dialog.setCancelable(true);
            dialog.show();

        }
    }

    /**
     @HitServiceSaveDrive
     */
    public void HitService()
    {

        //  Log.e("HitService ","hithithi t "+array_midPoints);
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
        //  Log.e("HitService ","Points_to_post t "+Points_to_post);
        // Log.e("HitService ","Points_to_post t "+data_ofer_ride.get("Points"));
        auto_from = autocompleteFrom.getText().toString();
        auto_to = autocompleteTo.getText().toString();

        data_ofer_ride.put("CustomerId", customerID);
        data_ofer_ride.put("LeavingFrom", autocompleteFrom.getText().toString() + ":" + strng_lat_from + ":" + strng_long_from);
        data_ofer_ride.put("LeavingDate", String.valueOf(date_bulider).trim());
        data_ofer_ride.put("LeavingTime", txt_depart_set_tym.getText().toString());

        if (get_intent_depart_date != null)
        {
            data_ofer_ride.put("DepartureDate", get_intent_depart_date);
        }
        else
        {
            data_ofer_ride.put("DepartureDate", String.valueOf(date_bulider).trim());
        }
        data_ofer_ride.put("DepartureTime", txt_depart_set_tym.getText().toString());
        data_ofer_ride.put("LeavingTo", autocompleteTo.getText().toString() + ":" + strng_lat_to + ":" + strng_long_to);
        data_ofer_ride.put("RatePerSeat", txt_rate_per_seat.getText().toString());
        data_ofer_ride.put("NoOfSeats", txt_number_Seats.getText().toString());
        data_ofer_ride.put("VehicleName", edt_car_name.getText().toString());
        data_ofer_ride.put("VehicleNo", edt_car_number.getText().toString());
        data_ofer_ride.put("Description", edt_car_type.getText().toString());

        if (static_string.equals("round"))
        {
            data_ofer_ride.put("RoundTrip", "true");
        }

        else if (static_string.equals("single"))
        {
            data_ofer_ride.put("RoundTrip", "false");
        }
        data_ofer_ride.put("IsRegulerBasis", "true");
        if (btn_select_ride.getText().equals("SAVE CHANGES"))
        {
            data_ofer_ride.put("TripID", tripID);
        }
        if (txt_days_data.getText().toString() != null)
        {
            if (txt_days_data.getText().toString().length() > 4)
            {
                data_ofer_ride.put("RegulerDays", txt_days_data.getText().toString());
            }
            else
            {
                data_ofer_ride.put("RegulerDays", "Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday");
            }
        }
        else
        {
            data_ofer_ride.put("RegulerDays", "Sunday,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday");
        }

        data_ofer_ride.put("ReturnDate", String.valueOf(date_bulider));
        data_ofer_ride.put("ReturnTime", txt_return_set_tym.getText().toString());
        //   Log.e("log4",""+txt_return_set_tym.getText().toString());

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

                Log.e("data_ofer_ride ", "Update" + data_ofer_ride);
                data_ofer_ride.put("TripID", get_trip_id);
                final Json_AsnycTask tsk = new Json_AsnycTask(RegularBasis.this, GlobalConstants.Url+"Trip/UpdateTrip", GlobalConstants.POST_SERVICE_METHOD1, data_ofer_ride);
                tsk.setOnResultsListener(this);
                tsk.execute();
                dialog = ProgressDialog.show(RegularBasis.this, "", "Loading. Please wait...", true);
                dialog.setCancelable(false);
                dialog.show();
            }
            else
            {

                Log.e("data_ofer_ride ", "(((((" + data_ofer_ride);
                final Json_AsnycTask tsk = new Json_AsnycTask(RegularBasis.this, GlobalConstants.Url+"Trip/SaveTrip", GlobalConstants.POST_SERVICE_METHOD1, data_ofer_ride);
                tsk.setOnResultsListener(this);
                tsk.execute();

                dialog = ProgressDialog.show(RegularBasis.this, "", "Loading. Please wait...", true);
                dialog.setCancelable(false);
                dialog.show();
            }

        }
    }

    @Override
    public void onResultsSucceeded_Get_Method1(JSONObject result)
    {
        dialog.dismiss();
        Log.e("response 1", "Login==" + result);
        if (result != null)
        {
            if (result.optString("Message").equals("Internal Server Error."))
            {
                snackbar_method("Sorry not able to delete drive, try again");

            }
            else if (result.optString("Status").equals("success"))
            {
                Log.e("response 1", "Login==" + result);
                RegularBasis.this.finish();
                Toast.makeText(getApplicationContext(), "Trip successfully saved", Toast.LENGTH_LONG).show();

            }
        }

        else
        {
            snackbar_method("Sorry not able to delete drive, try again");
        }
    }

    @Override
    public void onResultsSucceeded_Get_Method2(JSONObject result)
    {

    }

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
                RegularBasis.this.finish();
                if (btn_select_ride.getText().toString().equals("SAVE CHANGES"))
                {
                    //  snackbar_method("Changes saved successfully");
                    Toast.makeText(getApplicationContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    // snackbar_method("Trip successfully saved");
                    Toast.makeText(getApplicationContext(), "Trip successfully saved", Toast.LENGTH_LONG).show();
                }

            }
            else
            {
                snackbar_method("Sorry not able to save drive, try again");
            }

        }
        else
        {
            snackbar_method("Sorry not able to save drive, try again");
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

    @Override
    public void onResultsSucceeded_Post_Method4(JSONObject result)
    {

    }

    @Override
    public void onResultsSucceeded_Post_Method5(JSONObject result)
    {

    }

    /**
     @CarBike(SelectionRadio_Button)
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
                    txt_number_Seats.setText("");

                    txt_vw_txt_carnmbr.setText("Bike number");
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) lnr_vehicle_nmber.getLayoutParams();
                    params.setMargins(0, 10, 0, 20);
                    lnr_vehicle_nmber.setLayoutParams(params);
                    edt_car_number.setText("");
                    edt_car_name.setText("");
                    edt_car_type.setText("");

                    txt_rate_per_seat.setText("");
                }
            }
        });
    }

    /***
     car type dialog
     */

    public void showdialogcartype()
    {
        final Dialog dialog = new Dialog(RegularBasis.this, R.style.full_screen_dialog);
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

    public void add_mid_points(String location_name)
    {

        LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.custom_midpnt, null);

        final TextView add_txt_tolist = (TextView) v.findViewById(R.id.autocmplete_mid);
        ImageView      img_cros       = (ImageView) v.findViewById(R.id.img_cros);

        add_txt_tolist.setText(location_name);

        img_cros.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                for (int i = 0; i < array_midPoints_to_show.size(); i++)
                {
                    if (array_midPoints_to_show.get(i).equalsIgnoreCase(add_txt_tolist.getText().toString().trim()))
                    {
                        array_midPoints.remove(i);
                        array_midPoints_to_show.remove(i);

                        lay_mid_point.removeViewAt(i);
                    }
                }

            }
        });

        lay_mid_point.addView(v);

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

    private void btn_round_trip_method()
    {
        btn_round_trip.setBackgroundResource(R.mipmap.return_yellow);
        btn_single_trip.setBackgroundResource(R.drawable.grey_border);
        txt_return_tytm.setVisibility(View.VISIBLE);
        regulr_return_lnr.setVisibility(View.VISIBLE);
        static_string = "round";
    }

    @Override
    public void onClick(View v)
    {

        int id = v.getId();
        switch (id)
        {

            case R.id.img_add_days:
                method_select_days();
                break;
            case R.id.btn_round_trip:
                btn_round_trip_method();
                break;
            case R.id.btn_cancel:
                hide_keyboard(v);
                btn_cancel_ride_method();
                break;
            case R.id.btn_single_trip:
                btn_singletrip();
                break;
            case R.id.btn_select:
                hide_keyboard(v);
                method_check_validations();
                break;
            case R.id.lnr_vw_seat_nmbr:
                txt_number_Seats.requestFocus();
                break;
            case R.id.lnr_rate:
                txt_rate_per_seat.requestFocus();
                break;
            default:
                break;
        }
        //================

    }

    private void btn_singletrip()
    {
        btn_round_trip.setBackgroundResource(R.drawable.grey_border);
        btn_single_trip.setBackgroundResource(R.mipmap.return_yellow);
        txt_return_tytm.setVisibility(View.GONE);
        regulr_return_lnr.setVisibility(View.GONE);
        static_string = "single";
    }

    private void btn_cancel_ride_method()
    {
        if (btn_cancel_ride.getText().toString().equals("DELETE RIDE"))
        {
            final Dialog dialog = new Dialog(RegularBasis.this);
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
            RegularBasis.this.finish();
        }
    }
/*
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

        for (int i = 0; i < array.length; i++)
        {
            st = st + array[i];
        }

        return without_decimalNumber(st);

    }*/
    //=========MidStations Name==============

    private String[] mid_stationsNames(String midStations)
    {
        String[] stationsName_ = midStations.split(":");

        return stationsName_;
    }

    //Hide Keyboard

    public void hide_keyboard(View v)
    {
        InputMethodManager imm = (InputMethodManager) con.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

}
