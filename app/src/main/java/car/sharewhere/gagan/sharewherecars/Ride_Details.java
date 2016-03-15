package car.sharewhere.gagan.sharewherecars;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import car.sharewhere.gagan.Async_Thread.Super_AsyncTask;
import car.sharewhere.gagan.Async_Thread.Super_AsyncTask_Interface;
import car.sharewhere.gagan.Chat.Chat_Activity;
import car.sharewhere.gagan.Chat.Chat_Database;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.sharewherecars.Adapters.Ride_Details_Adapter;
import car.sharewhere.gagan.utills.Bean_Ride_Details;

import car.sharewhere.gagan.utills.CircleTransform;
import car.sharewhere.gagan.utills.Utills_G;

/**
 Created by sharan on 15/2/16. */
public class Ride_Details extends AppCompatActivity implements View.OnClickListener
{

    Context           con;
    SharedPreferences preferences;
    String            customerID, my_customer_name;

    public static String my_customerID,TripID;

    TextView txt_day_date,txt_driver_name, txt_car_name, txt_mobile_nmber, txt_leaving_from, txt_leaving_to, txt_return_time, txt_departure_date, txt_depart_time, txt_return_date, txt_days, txt_toolbar, txt_gcm_cancel, txt_locatn_send;
    TextView txt_total_seats, txt_seat_rate, txt_vehicle_type, txt_vehicle_name, txt_vehicle_number,txtv_message_count;
    ImageView    profile_img,imgv_call;

    LinearLayout lnr_mid_point, lnr_date, lnr_time, lay_mid_points;

     CardView cardv_profile,cardv_departure_details,cardv_return_details,cardv_find_regular/*,cardv_users_request*/;


    CheckBox chk_sunday, chk_monday, chk_tuesday, chk_wednsday, chk_thursday, chk_Friday, chk_saturday;
    TextView         btn_request;
    TextView btn_decline;
    RelativeLayout rel_decline;
    ListView       list_my_rides_users;
    String fromWhere = "";
    Chat_Database chat_database;

    //*************************************New ********************************


    LinearLayout lnr_returndate,lnr_return_time,return_date_lnr;
    TextView txt_return_day_date,txt_return_date_day,txt_retrn_time,txt_return_time_;





    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_owner_profile);

        con = this;

        chat_database = new Chat_Database(con);

        preferences = PreferenceManager.getDefaultSharedPreferences(con);

        my_customerID = preferences.getString("CustomerId", null);
        my_customer_name = preferences.getString("first_name", null);

        customerID = getIntent().getStringExtra(GlobalConstants.KeyNames.CustomerId.toString());
        Log.e("Ride_Details", "   my_customerID  " + my_customerID);
        Log.e("Ride_Details", " customerID    " + customerID);
        TripID = getIntent().getStringExtra(GlobalConstants.KeyNames.TripId.toString());

        try
        {
            fromWhere = getIntent().getStringExtra(GlobalConstants.KeyNames.fromWhere.toString());
        }
        catch (Exception e)
        {
            fromWhere = "";
            e.printStackTrace();
        }

        setActionBar();
        setUpIDS();

    }

    public void refresh()
    {
        Get_Ride_Details_By_TripID();
    }

    private void setActionBar()
    {
        try
        {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            txt_toolbar = (TextView) toolbar.findViewById(R.id.txt_titleTV);
            //            txt_toolbar.setText("Owner Profile");
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void setUpIDS()
    {
        txt_driver_name = (TextView) findViewById(R.id.txt_driver_name);
        txt_day_date = (TextView) findViewById(R.id.txt_day_date);
        txt_car_name = (TextView) findViewById(R.id.txt_car_name);
        txt_mobile_nmber = (TextView) findViewById(R.id.txt_mobile_nmber);
        txt_leaving_from = (TextView) findViewById(R.id.txt_leaving_from);
        txt_leaving_to = (TextView) findViewById(R.id.txt_leaving_to);
        txt_days = (TextView) findViewById(R.id.txt_days);
        txt_locatn_send = (TextView) findViewById(R.id.txt_locatn_send);
        txt_return_time = (TextView) findViewById(R.id.txt_return_time);
        txt_return_date = (TextView) findViewById(R.id.txt_return_date);
        txt_depart_time = (TextView) findViewById(R.id.txt_depart_time);
        txt_gcm_cancel = (TextView) findViewById(R.id.txt_gcm_cancel);//Cancel request
        txt_departure_date = (TextView) findViewById(R.id.txt_departure_date);
        txt_total_seats = (TextView) findViewById(R.id.txt_total_seats);
        txt_seat_rate = (TextView) findViewById(R.id.txt_seat_rate);
        txt_vehicle_name = (TextView) findViewById(R.id.txt_vehicle_name);
        txt_vehicle_number = (TextView) findViewById(R.id.txt_vehicle_number);
        txt_vehicle_type = (TextView) findViewById(R.id.txt_vehicle_type);
        txtv_message_count     = (TextView)findViewById(R.id.txtv_message_count);

        profile_img = (ImageView) findViewById(R.id.profile_img);
        imgv_call = (ImageView) findViewById(R.id.imgv_call);

        btn_request = (TextView) findViewById(R.id.btn_request);
        btn_decline = (TextView) findViewById(R.id.btn_decline);
        rel_decline = (RelativeLayout) findViewById(R.id.rel_decline);




        lnr_date = (LinearLayout) findViewById(R.id.lnr_date);
        lnr_time = (LinearLayout) findViewById(R.id.lnr_time);

        cardv_profile = (CardView) findViewById(R.id.cardv_profile);
        cardv_departure_details = (CardView) findViewById(R.id.cardv_departure_details);
        cardv_return_details = (CardView) findViewById(R.id.cardv_return_details);
        cardv_find_regular = (CardView) findViewById(R.id.cardv_find_regular);
//        cardv_users_request = (CardView) findViewById(R.id.cardv_users_request);

        lnr_mid_point = (LinearLayout) findViewById(R.id.lnr_mid_point);


        lay_mid_points = (LinearLayout) findViewById(R.id.lay_mid_points);

        chk_sunday = (CheckBox) findViewById(R.id.chk_sunday);
        chk_monday = (CheckBox) findViewById(R.id.chk_monday);
        chk_tuesday = (CheckBox) findViewById(R.id.chk_tuesday);
        chk_wednsday = (CheckBox) findViewById(R.id.chk_wednsday);
        chk_thursday = (CheckBox) findViewById(R.id.chk_thursday);
        chk_Friday = (CheckBox) findViewById(R.id.chk_Friday);
        chk_saturday = (CheckBox) findViewById(R.id.chk_saturday);

        list_my_rides_users = (ListView) findViewById(R.id.list_my_rides_users);



        txt_gcm_cancel.setPaintFlags(txt_gcm_cancel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txt_locatn_send.setPaintFlags(txt_locatn_send.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        imgv_call.setOnClickListener(this);
        btn_request.setOnClickListener(this);
        rel_decline.setOnClickListener(this);

        txt_gcm_cancel.setOnClickListener(this);
        txt_locatn_send.setOnClickListener(this);

        // firstly all data will be hidden and will show if customer id does not belong to me

        cardv_return_details.setVisibility(View.GONE);
        cardv_profile.setVisibility(View.GONE);
        cardv_find_regular.setVisibility(View.GONE);
        cardv_departure_details.setVisibility(View.GONE);

//        cardv_users_request.setVisibility(View.GONE);

//        btn_decline.setVisibility(View.GONE);
        rel_decline.setVisibility(View.GONE);
        btn_request.setVisibility(View.GONE);




        lnr_returndate = (LinearLayout) findViewById(R.id.lnr_returndate);
        lnr_return_time = (LinearLayout) findViewById(R.id.lnr_return_time);
        return_date_lnr = (LinearLayout) findViewById(R.id.return_date_lnr);

        txt_return_day_date = (TextView) findViewById(R.id.txt_return_day_date);
        txt_return_date_day = (TextView) findViewById(R.id.txt_return_date_day);
        txt_retrn_time = (TextView) findViewById(R.id.txt_retrn_time);
        txt_return_time_ = (TextView) findViewById(R.id.txt_return_time_);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                call_back();

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        call_back();

    }

    public  void call_back()
    {

        if(fromWhere == null || fromWhere.isEmpty())
        {
            finish();
        }
        else if(fromWhere.equals(GlobalConstants.KeyNames.Notification.toString()))
        {
            Intent i=new Intent(con, MainActivity.class);
            i.putExtra(GlobalConstants.KeyNames.fromWhere.toString(),GlobalConstants.KeyNames.Notification.toString());
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
        else if(fromWhere.equals(GlobalConstants.KeyNames.Messages.toString()))
        {
            Intent i=new Intent(con, MainActivity.class);
            i.putExtra(GlobalConstants.KeyNames.fromWhere.toString(),GlobalConstants.KeyNames.Messages.toString());
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }


    }

    @Override
    public void onClick(View v)
    {

        switch (v.getId())
        {
            case R.id.imgv_call:

                if (!txt_mobile_nmber.equals("No registered number"))
                {
                    Intent in = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + txt_mobile_nmber.getText().toString()));
                    try
                    {
                        startActivity(in);
                    }
                    catch (android.content.ActivityNotFoundException ex)
                    {
                        ex.printStackTrace();
                    }
                }

                break;

            case R.id.btn_request:

                Log.e("fff", btn_request.getText().toString());

                if (btn_request.getText().toString().equals("Send Request"))
                {
                    Log.e("fff", "NewRequest");
                    HitService_Request("NewRequest",1);
                }
                else if (btn_request.getText().toString().equals("Ask location on map"))
                {
                    Log.e("fff", "Ask location on map");

                    HitService_Adress_message(my_customer_name + " wants to locate you on map.", "request");
                }
                else if (btn_request.getText().toString().equalsIgnoreCase("Response Pending"))
                {
                    Toast.makeText(con, "Response pending", Toast.LENGTH_LONG).show();
                }

                break;

            case R.id.txt_gcm_cancel:

                HitService_Request("CancelByRider",0);

                break;

            case R.id.rel_decline:

                if (btn_decline.getText().toString().equals("Message"))
                {


                    Intent i=new Intent(con, Chat_Activity.class);
                    i.putExtra(GlobalConstants.KeyNames.RiderId.toString(),my_customerID);
                    i.putExtra(GlobalConstants.KeyNames.DriverId.toString(),bean_ride_details.getCustomerId());
                    i.putExtra(GlobalConstants.KeyNames.CustomerPhoto.toString(), bean_ride_details.getCustomerPhoto());
                    i.putExtra(GlobalConstants.KeyNames.TripId.toString(), bean_ride_details.getTripId());
                    i.putExtra(GlobalConstants.KeyNames.RequestId.toString(), bean_ride_details.getRequestId());
                    con.startActivity(i);

                   /* final Dialog dialog = new Dialog(con);
                    dialog.setContentView(R.layout.car_owner_where_are_u_dialog);
                    dialog.setTitle("Type Message");
                    dialog.setCancelable(true);

                    final TextView txt_msg = (TextView) dialog.findViewById(R.id.txt_msg);
                    Button btn_dialog_send_where = (Button) dialog.findViewById(R.id.btn_dialog_send_where);
                    Button btn_dialog_dismiss = (Button) dialog.findViewById(R.id.btn_dialog_dismiss);

                    btn_dialog_dismiss.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            dialog.dismiss();
                        }
                    });

                    btn_dialog_send_where.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (txt_msg.getText().toString().trim().length() > 0)
                            {
                                HitService_Adress_message(my_customer_name + " : " + txt_msg.getText().toString().trim(), "msg");
                                dialog.dismiss();
                            }
                            else
                            {
                                txt_msg.setError("Enter comment to send");
                            }
                        }
                    });

                    dialog.show();*/
                }
                /*else if (btn_decline.getText().toString().equals("Decline"))
                {
                    HitService_Request("Decline");
                }*/
                break;
        }

    }

    public void display_data()
    {
        // Departure details will be show for both Driver and Rider
        cardv_departure_details.setVisibility(View.VISIBLE);


        if(bean_ride_details.getIsRegulerBasis().equals("false"))
        {
            if(bean_ride_details.getRoundTrip().equalsIgnoreCase("true"))
            {
                lnr_returndate.setVisibility(ViewGroup.VISIBLE);
                lnr_return_time.setVisibility(ViewGroup.VISIBLE);


                txt_return_date_day.setText(bean_ride_details.getReturnDate());
                txt_return_time_.setText(bean_ride_details.getReturnTime());
            }

            txt_departure_date.setText(bean_ride_details.getDepartureDate());

        }
        else
        {

            if(bean_ride_details.getRoundTrip().equalsIgnoreCase("true"))
            {
                lnr_returndate.setVisibility(ViewGroup.VISIBLE);
                lnr_return_time.setVisibility(ViewGroup.VISIBLE);

                String day_date_str = bean_ride_details.getRegulerDays().contains(",") ? "Return Days" : "Return Day";
                txt_return_day_date.setText(day_date_str);
                txt_return_date_day.setText(bean_ride_details.getRegulerDays());

                txt_return_time_.setText(bean_ride_details.getReturnTime());
            }

            String day_date_str = bean_ride_details.getRegulerDays().contains(",") ? "Leaving Days" : "Leaving Day";
            // Log.e("day_date_str",""+day_date_str);
            txt_day_date.setText(day_date_str);
            txt_departure_date.setText(bean_ride_details.getRegulerDays());

        }


        if (!my_customerID.equals(bean_ride_details.getCustomerId())) // if i am rider
        {
            txt_toolbar.setText("Owner Profile");

            cardv_profile.setVisibility(View.VISIBLE);

            cardv_find_regular.setVisibility(View.VISIBLE);
//            lnr_vw_phn_call.setVisibility(View.VISIBLE);

            //image
            if (!bean_ride_details.getCustomerPhoto().equals("null") || bean_ride_details.getCustomerPhoto().isEmpty())
            {
                Picasso.with(con).load(bean_ride_details.getCustomerPhoto()).
                          transform(new CircleTransform()).into(profile_img);
            }

            txt_driver_name.setText(bean_ride_details.getCustomerName());
            txt_car_name.setText(bean_ride_details.getVehicleName());

            if (bean_ride_details.getCustomerContactNo().equals("null") || bean_ride_details.getCustomerContactNo().isEmpty())
            {
                txt_mobile_nmber.setText("No registered number");
            }
            else
            {
                txt_mobile_nmber.setText(bean_ride_details.getCustomerContactNo());
            }

            if (!bean_ride_details.getRegulerDays().equals("null"))
            {
                txt_days.setText(bean_ride_details.getRegulerDays());
            }

            if (!bean_ride_details.getIsRegulerBasis().equals("true"))
            {
                cardv_find_regular.setVisibility(View.VISIBLE);
            }
            else
            {
                cardv_find_regular.setVisibility(View.GONE);
            }

            // Round trip details
            if (bean_ride_details.getRoundTrip().equals("true"))
            {

                cardv_return_details.setVisibility(View.VISIBLE);

                String[] separated = bean_ride_details.getReturnDate().split("T");
                txt_return_date.setText(separated[0]);

                txt_return_time.setText(bean_ride_details.getReturnTime());


                //HideDeparture Details Return DAte and Time LinearLayout
                lnr_returndate.setVisibility(ViewGroup.GONE);
                lnr_return_time.setVisibility(ViewGroup.GONE);

            }
            else
            {
                cardv_return_details.setVisibility(View.GONE);
            }

            if (bean_ride_details.getIsRegulerBasis().equals("true"))
            {
                cardv_find_regular.setVisibility(View.VISIBLE);
                txt_days.setText(bean_ride_details.getRegulerDays());
            }
            else
            {
                cardv_find_regular.setVisibility(View.GONE);
            }

            /*TripStatus { Accept = 1, Decline = 2, New = 3, NotRespond = 4 ,NewRequest=5,CancelByRider=6,CancelByDriver=7}*/

            boolean is_you_send_request=false;
            int flag_indes=0;

            ArrayList<HashMap<String,String>> local_list=bean_ride_details.getRequestAcceptModelList();

            for (int i = 0; i < local_list.size(); i++)
            {
                if(local_list.get(i).get("CustomerId").equals(my_customerID))
                {
                    is_you_send_request=true;
                    flag_indes=i;
                    break;
                }
            }

            if(is_you_send_request)
            {/*
                if (bean_ride_details.getTripStatus().equals("0")) // No reqest is there
                {
                    btn_request.setVisibility(View.VISIBLE);
                    btn_decline.setVisibility(View.GONE);
                    btn_request.setText("Send Request");
                }
                else*/ if (local_list.get(flag_indes).get("Flag").equals("1"))  //Accepted
            {
                    btn_request.setVisibility(View.VISIBLE);
                rel_decline.setVisibility(View.VISIBLE);
//                    btn_decline.setVisibility(View.VISIBLE);
                    txt_gcm_cancel.setVisibility(View.VISIBLE);

                    btn_request.setText("Ask location on map");
                    btn_decline.setText("Message");

                long count =chat_database.get_unread_messages_count(TripID, "");
                if(count>0)
                {
                    txtv_message_count.setVisibility(View.VISIBLE);
                    txtv_message_count.setText(""+count);
                }
                else
                {
                    txtv_message_count.setVisibility(View.GONE);
                }

                }
                else if (local_list.get(flag_indes).get("Flag").equals("3") || local_list.get(flag_indes).get("Flag").equals("2"))  // Decline/New
                {
                    btn_request.setVisibility(View.VISIBLE);
                    btn_request.setText("Send Request");
//                    btn_decline.setVisibility(View.GONE);
                    rel_decline.setVisibility(View.GONE);

                }

                else if (local_list.get(flag_indes).get("Flag").equals("5"))   //New Request send by you and its response pending
                {
                    btn_request.setVisibility(View.VISIBLE);
//                    btn_decline.setVisibility(View.GONE);
                    rel_decline.setVisibility(View.GONE);
                    btn_request.setText("Response Pending");
                }
            }
            else
            {
                txt_gcm_cancel.setVisibility(View.INVISIBLE);
                btn_request.setVisibility(View.VISIBLE);
//                btn_decline.setVisibility(View.GONE);
                rel_decline.setVisibility(View.GONE);
                btn_request.setText("Send Request");
            }



        }
        else  // If i am driver
        {
            txt_toolbar.setText("Ride Details");

//            btn_decline.setVisibility(View.GONE);
            rel_decline.setVisibility(View.GONE);
            btn_request.setVisibility(View.GONE);

            // if the driver is having riders requests

            ArrayList<HashMap<String, String>> RequestAcceptModelList_list = bean_ride_details.getRequestAcceptModelList();

            if (RequestAcceptModelList_list.size() > 0)
            {

                Log.e("RequestAcceptModelList_list.size()",""+RequestAcceptModelList_list.size());
                list_my_rides_users.setVisibility(View.VISIBLE);
//                cardv_users_request.setVisibility(View.VISIBLE);
                Ride_Details_Adapter adapter = new Ride_Details_Adapter(con, RequestAcceptModelList_list, bean_ride_details.getIsRegulerBasis(), bean_ride_details.getRoundTrip());
                list_my_rides_users.setAdapter(adapter);
                GlobalConstants.setListViewHeightBasedOnItems(list_my_rides_users,0);

            }
            else
            {
                list_my_rides_users.setVisibility(View.GONE);
            }




        }

        ArrayList<HashMap<String, String>> mid_point_list = bean_ride_details.getRouteModelList();
        lay_mid_points.removeAllViews();

        if (mid_point_list.size() > 2)
        {
            for (int i = 0; i < mid_point_list.size(); i++)
            {
                if (i != 0 && i != mid_point_list.size() - 1)
                {
                    LayoutInflater inflater = (LayoutInflater) con.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View view = inflater.inflate(R.layout.custom_mid_point, null);

                    TextView mid = (TextView) view.findViewById(R.id.txt_mid_pnt);

                    mid.setText(mid_point_list.get(i).get("Points"));

                    lay_mid_points.addView(view);

                }

            }
        }
        else
        {
            lnr_mid_point.setVisibility(View.GONE);
        }

        txt_leaving_from.setText(mid_point_list.get(0).get("Points"));
        txt_leaving_to.setText(mid_point_list.get(mid_point_list.size() - 1).get("Points"));


       /* if(bean_ride_details.getIsRegulerBasis().equals("false"))
        {

            txt_departure_date.setText(bean_ride_details.getDepartureDate());
        }
        else
        {
            String day_date_str=bean_ride_details.getRegulerDays().contains(",")?"Leaving Days":"Leaving Day";
            // Log.e("day_date_str",""+day_date_str);
            txt_day_date.setText(day_date_str);
            txt_departure_date.setText(bean_ride_details.getRegulerDays());
        }*/





        txt_depart_time.setText(bean_ride_details.getDepartureTime());
        txt_total_seats.setText(bean_ride_details.getNoOfSeats());
        txt_seat_rate.setText(preferences.getString("currency_symbol", "\u20B9") + bean_ride_details.getRatePerSeat());

        txt_vehicle_type.setText(bean_ride_details.getVehicleType());
        txt_vehicle_name.setText(bean_ride_details.getVehicleName());
        txt_vehicle_number.setText(bean_ride_details.getVehicleNo());

    }

    //*********************************************************Get Ride Details**************************************************************

    Bean_Ride_Details bean_ride_details = new Bean_Ride_Details();

    public void Get_Ride_Details_By_TripID()
    {

        String url = "http://112.196.34.42:9091/Trip/GetTripByID?TripId=" + TripID + "&CustomerId=" + customerID;

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

                        JSONObject object2 = object.getJSONObject("Message");

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

                        bean_ride_details.setRouteModelList(object2.getString("RouteModelList"));

                        display_data();
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

    public void HitService_Request(final String request_type,int refresh_screenflag)
    {

        HashMap<String, String> map = new HashMap<>();

        map.put("TripId", bean_ride_details.getTripId());
        map.put("CustomerId", my_customerID);
        map.put("RequestType", request_type);

        if(refresh_screenflag==1){
            map.put("RequestID","0");
        }
        else
        {
            map.put("RequestID", bean_ride_details.getRequestId().isEmpty() ? "" : bean_ride_details.getRequestId());
        }
        map.put("IsRegulerBasis", bean_ride_details.getIsRegulerBasis());
        map.put("RoundTrip", bean_ride_details.getRoundTrip());

        Log.e("Sharan HitService_Request", "" + map);

        String url = "http://112.196.34.42:9091/Trip/TripRequest";

        GlobalConstants.execute(new Super_AsyncTask(con, map, url, new Super_AsyncTask_Interface()
        {
            @Override
            public void onTaskCompleted(String output)
            {
                try
                {

                    JSONObject object = new JSONObject(output);
                    if (object.getString("Status").equalsIgnoreCase("success"))
                    {

                        if (request_type.equals("CancelByRider"))
                        {
                            chat_database.delete_chat(bean_ride_details.getTripId(),my_customerID);

                            finish();
                        }
                        else if(request_type.equals("NewRequest"))
                        {
                            btn_request.setText("Response Pending");
                        }

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

    private void HitService_Adress_message(String message, String status)
    {

        HashMap<String, String> map = new HashMap<>();

        map.put("RiderId", my_customerID);
        map.put("DriverId", bean_ride_details.getCustomerId());
        map.put("TripId", bean_ride_details.getTripId());
        map.put("Message", message);
        map.put("Latitude", preferences.getString("current_lat", "0.0"));
        map.put("Longitude", preferences.getString("current_long", "0.0"));
        map.put("Flag", "Driver");
        map.put("Status", status);
        map.put("RequestId", bean_ride_details.getRequestId());

        Log.e("Sharan HitService_Adress_message", "" + map);

        String url = "http://112.196.34.42:9091/Trip/LocationShare";

        GlobalConstants.execute(new Super_AsyncTask(con, map, url, new Super_AsyncTask_Interface()
        {
            @Override
            public void onTaskCompleted(String output)
            {
                try
                {
                    Log.e("Sharan Ride Details Ask Location", output);
                    JSONObject object = new JSONObject(output);
                    if (object.getString("Status").equalsIgnoreCase("success"))
                    {

                        //                        JSONObject object2 = object.getJSONObject("Message");

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

    //======Added====29 Feb===================
    RideDetails_BroadcastReceiver receiver;
    boolean is_receiver_registered = false;

    @Override
    protected void onResume()
    {

        preferences.edit().putBoolean("is_ridedetails_opened", true).apply();

        refresh();
        Log.e("onResume"," is_receiver_registered "+is_receiver_registered+"nnnnnn "+receiver);
        super.onResume();

        if(!is_receiver_registered)
        {
            if(receiver==null)
            {

                receiver=new RideDetails_BroadcastReceiver();
            }
            this.registerReceiver(receiver,new IntentFilter(Utills_G.ridedetails));
            is_receiver_registered=true;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        preferences.edit().putBoolean("is_ridedetails_opened", false).apply();
    }

    @Override
    protected void onDestroy()
    {

        preferences.edit().putBoolean("is_ridedetails_opened", false).apply();
        super.onDestroy();
        if(receiver!=null)
        {
            this.unregisterReceiver(receiver);
        }
    }

    private class RideDetails_BroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            Log.e("Reached"," EEEEEEEEEE");
            refresh();
        }
    }

}
