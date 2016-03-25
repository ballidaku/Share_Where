package car.sharewhere.gagan.Tabs;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import car.sharewhere.gagan.Async_Thread.Super_AsyncTask;
import car.sharewhere.gagan.Async_Thread.Super_AsyncTask_Interface;
import car.sharewhere.gagan.Chat.Chat_Database;
import car.sharewhere.gagan.WebServices.Asnychronus_notifier;
import car.sharewhere.gagan.utills.GlobalConstants;
import car.sharewhere.gagan.WebServices.Json_AsnycTask;
import car.sharewhere.gagan.sharewherecars.Just_Once_offerRide;
import car.sharewhere.gagan.sharewherecars.MainActivity;
import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.sharewherecars.RegularBasis;
import car.sharewhere.gagan.sharewherecars.Ride_Details;
import car.sharewhere.gagan.utills.CircleTransform;
import car.sharewhere.gagan.utills.ConnectivityDetector;
import car.sharewhere.gagan.utills.Getter_setter;
import car.sharewhere.gagan.utills.Utills_G;

/**
 Created by ameba on 12/1/16. */
public class Driver_Rides_Tab extends Fragment implements Asnychronus_notifier
{

    SharedPreferences.Editor editor;
    ListView                 list_home_recent;
    TextView                 txt_no_ride;
    ImageView                img_driver_img, img_vw_reload;
    SharedPreferences preferences;
    String            customerID, my_customerID, format, current_time, current_date_strng;
    ConnectivityDetector cd;

    CoordinatorLayout coordinatorlayout;
//    Snackbar          snackbar;
    //    ProgressDialog    dialog;
    Button            btn_ofer_ride;
    Date              current_date;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    MidPointAdapter adater;
    List<Getter_setter> list = new ArrayList<>();

    ArrayList<String> array_image = new ArrayList<>();
    Chat_Database chat_database;
    public static boolean is_drivertab_visible = false;

    Context con;

    // Trip id with there corresponding reuest counts
    HashMap<String, String> trip_request_map;

    Animation animRotate;
    View view;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        view = inflater.inflate(R.layout.tab_my_ride, container, false);
        con = getActivity();

        chat_database = new Chat_Database(con);

        trip_request_map = new HashMap<>();

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        customerID = preferences.getString("CustomerId", null);
        my_customerID = preferences.getString("CustomerId", null);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        cd = new ConnectivityDetector(getActivity().getApplicationContext());

        findviewbyID(view);

        //       Log.e("Chat All Data", "...." + chat_database.get_chat_all_data());
        //        Log.e("Data count","...." + chat_database.get_unread_messages_count("1010", "212"));

        method_current_time();

        animRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate);

        return view;
    }

    /**
     @ids
     */
    public void findviewbyID(View v)
    {
        list_home_recent = (ListView) v.findViewById(R.id.list_home_recent);
        coordinatorlayout = (CoordinatorLayout) v.findViewById(R.id.coordinatorLayout);
        txt_no_ride = (TextView) v.findViewById(R.id.txt_no_ride);
        img_vw_reload = (ImageView) v.findViewById(R.id.img_vw_reload);
        btn_ofer_ride = (Button) v.findViewById(R.id.btn_ofr_ride);

        txt_no_ride.setText(R.string.home_new_No_ride);

        btn_ofer_ride.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getActivity(), MainActivity.class);
                startActivity(i);
            }
        });

        img_vw_reload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //                HitServiceEarlierSearc();
                refresh();

            }
        });

        list_home_recent.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                if (!cd.isConnectingToInternet())
                {
                    snackbar_method("No internet connection!");
                }
                else
                {

                    //                Intent        is       = new Intent(getActivity(), Car_owner_Activity.class);
                    Intent is = new Intent(getActivity(), Ride_Details.class);
                    //                    Intent is = new Intent(getActivity(), Chat_Activity.class);

                    is.putExtra(GlobalConstants.KeyNames.TripId.toString(), list.get(position).getTrip_id());
                    is.putExtra(GlobalConstants.KeyNames.CustomerId.toString(), preferences.getString("CustomerId", null));
                    Log.e("DriversId", "  " + preferences.getString("CustomerId", null));

                    startActivity(is);
                    getActivity().overridePendingTransition(0, R.anim.push_down_out);
                }
            }
        });
    }

    @Override
    public void setMenuVisibility(final boolean visible)
    {
        super.setMenuVisibility(visible);
        if (visible)
        {
            is_drivertab_visible=true;
        }
        else
        {
            is_drivertab_visible=false;
        }


        Log.e("Driver Tab",""+is_drivertab_visible);
    }



    public void refresh()
    {
        trip_request_map=chat_database.get_tripid_unread_req_count_data();
        HitServiceEarlierSearc();
    }

    private void method_current_time()
    {
        StringBuilder buildr_curnt_time;
        Calendar      cal = Calendar.getInstance();
        current_date = cal.getTime();
        int minute = cal.get(Calendar.MINUTE);
        int hour   = cal.get(Calendar.HOUR);
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
        current_date_strng = sdf.format(cal.getTime());
        Log.e("time==", current_time);

    }

    //***************************************************Delete Particular Ride***************************************************

    public void Delete_Service(final String TripID)
    {

        String url = GlobalConstants.Url+"Trip/DeleteTrip?TripId=" + TripID;

        GlobalConstants.execute(new Super_AsyncTask(getActivity(), url, new Super_AsyncTask_Interface()
        {
            @Override
            public void onTaskCompleted(String output)
            {
                try
                {

                    JSONObject object = new JSONObject(output);
                    if (object.getString("Status").equalsIgnoreCase("success"))
                    {

//                        snackbar_method(object.getString("Message"));

                        Utills_G.showToast(object.getString("Message"),getActivity(),false);

                        chat_database.delete_chat(TripID, "");
                        chat_database.delete_trip_request("",TripID);

                        HitServiceEarlierSearc();

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
        }, true));

    }

//    *******************************************************************************************************************************

    /**
     @HitService(AllRides)
     */
    private void HitServiceEarlierSearc()
    {
        //        data_home.put("CustomerId", customerID);

        if (!cd.isConnectingToInternet())
        {
            show_message("Your internet connection seems to be disable.");
        }
        else
        {
            Json_AsnycTask task = new Json_AsnycTask(getActivity(), GlobalConstants.Url+"Trip/GetAllTripByCustomerID?CustomerId=" + customerID, GlobalConstants.GET_SERVICE_METHOD1, null);
            task.setOnResultsListener(this);
            task.execute();

            show_message("Fetching your rides. Please wait...");

            /*dialog = ProgressDialog.show(getActivity(), "", "Fetching your rides. Please wait...", true);
            dialog.setCancelable(true);
            dialog.show();*/
        }
    }

    @Override
    public void onResultsSucceeded_Get_Method1(JSONObject result)
    {
        Log.e("response home get trip", "Login==" + result);
        {
            /*dialog.dismiss();*/
//            Log.e("response 1 driver test", "Login==" + result);
            if (result != null)
            {

                if (result.optString("Message").equals("Internal Server Error."))
                {
                    show_message("Try Again!! Internal server error");
//                    snackbar_method("Try Again!! Internal server error");

                }
                else if (result.optString("Status").equals("success"))
                {


                    array_image.clear();
                    try
                    {
                        JSONArray arr = result.getJSONArray("Message");
                        if (arr.length() == 0)
                        {
//                            snackbar_method("No Ride to show");

                            list_home_recent.setVisibility(View.GONE);
                            txt_no_ride.setVisibility(View.VISIBLE);
                            img_vw_reload.setVisibility(View.VISIBLE);
                            btn_ofer_ride.setVisibility(View.VISIBLE);

                           img_vw_reload.clearAnimation();

                            list.clear();

                            show_message("You have no rides to show.");

                        }
                        else
                        {

                            list_home_recent.setVisibility(View.VISIBLE);
                            txt_no_ride.setVisibility(View.GONE);
                            img_vw_reload.setVisibility(View.GONE);
                            btn_ofer_ride.setVisibility(View.GONE);


                            List<Getter_setter> feedItemList = new ArrayList<>();

                            for (int i = 0; i < arr.length(); i++)
                            {

                                JSONObject jsonobject = arr.getJSONObject(i);

                                Getter_setter item = new Getter_setter();

                                item.setCustomer_name(jsonobject.optString("CustomerName"));
                                item.setLeaving_date(jsonobject.optString("DepartureDate"));
                                item.setLeaving_from(jsonobject.optString("LeavingFrom"));
                                item.setLeaving_to(jsonobject.optString("LeavingTo"));
                                item.setVehicle_name(jsonobject.optString("VehicleName"));
                                item.setVehicle_type(jsonobject.optString("VehicleType"));
                                item.setImage(jsonobject.optString("CustomerPhoto"));
                                item.setTrip_id(jsonobject.optString("TripId"));
                                item.setLeaving_time(jsonobject.optString("DepartureTime"));
                                item.setMobile_number(jsonobject.optString("CustomerContactNo"));
                                item.setMid_point(jsonobject.optString("Points"));
                                item.setVehicle_description(jsonobject.optString("Description"));
                                item.setReturn_time(jsonobject.optString("ReturnTime"));
                                item.setRound(jsonobject.optString("RoundTrip"));
                                item.setReturn_date(jsonobject.optString("ReturnDate"));
                                item.setSeats_available(jsonobject.optString("NoOfSeats"));
                                item.setRate_per_seat(jsonobject.optString("RatePerSeat"));
                                item.setIs_regular(jsonobject.optString("IsRegulerBasis"));
                                item.setRegulardays(jsonobject.optString("RegulerDays"));
                                item.setVehicle_number(jsonobject.optString("VehicleNo"));

                                if (jsonobject.has("RequestAcceptModelList"))
                                {
                                    JSONArray arr2 = jsonobject.getJSONArray("RequestAcceptModelList");

                                    //                                    Log.e("arr length", arr2.length() + "");

                                    ArrayList<HashMap<String, String>> child_list = new ArrayList<>();

                                    for (int j = 0; j < arr2.length(); j++)
                                    {
                                        HashMap<String, String> map = new HashMap<>();

                                        JSONObject jsonobject2 = arr2.getJSONObject(j);

                                        map.put("CustomerPhoto", jsonobject2.getString("CustomerPhoto"));
                                        map.put("Flag", jsonobject2.getString("Flag"));
                                        map.put("RiderID", jsonobject2.getString("CustomerId"));
                                        map.put("DriverId", jsonobject2.getString("DriverId"));
                                        map.put("RequestId", jsonobject2.getString("RequestId"));
                                        map.put("CustomerMobileNo", jsonobject2.getString("CustomerMobileNo"));
                                        map.put("CustomerName", jsonobject2.getString("CustomerName"));
                                        //                                            map.put("CustomerName", jsonobject2.getString("CustomerName"));

                                        child_list.add(map);
                                    }
                                    item.setChild_list(child_list);

                                }

                                feedItemList.add(item);
                            }

                            notify(feedItemList);
                        }

                        //                        GlobalConstants.setListViewHeightBasedOnItems(list_home_recent, feedItemList.size());
                    }
                    catch (Exception ex)
                    {
                        snackbar_method(ex.toString());
                    }

                }
                else if (result.optString("Status").equals("error"))
                {

                    show_message("Error in connecting. Please try later.");
                }
            }
            else
            {
                show_message("Error in connecting. Please try later.");
            }
        }
    }

    public void notify(List<Getter_setter> feedItemList)
    {


        if (list.size() == 0)
        {
            list=feedItemList;
            adater = new MidPointAdapter(feedItemList);
            list_home_recent.setAdapter(adater);
        }
        else
        {
            list=feedItemList;
            adater.set_data(feedItemList);
            adater.notifyDataSetInvalidated();
        }

    }

    @Override
    public void onResultsSucceeded_Get_Method2(JSONObject result)
    {

    }

    @Override
    public void onResultsSucceeded_Post_Method1(JSONObject result)
    {
        Log.e("response home del trip", "del==" + result);
//        dialog.dismiss();
        if (result != null)
        {
            if (result.optString("Status").equals("success"))
            {
                HitServiceEarlierSearc();
                snackbar_method("Ride successfully deleted");
            }
            else
            {
                snackbar_method("Try Again!!");
            }
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
     @ListAdapter
     */
    private class MidPointAdapter extends BaseAdapter
    {

        List<Getter_setter> local_list;

        public MidPointAdapter(List<Getter_setter> feedItemList)
        {
            super();
            local_list = feedItemList;
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

            TextView txt_day = (TextView) v.findViewById(R.id.txt_day);

            TextView txt_from = (TextView) v.findViewById(R.id.txt_from);
            TextView txt_to   = (TextView) v.findViewById(R.id.txt_to);
            v.findViewById(R.id.txt_driver_name).setVisibility(View.GONE);
            TextView txt_vw_ride_type   = (TextView) v.findViewById(R.id.txt_vw_ride_type);
            TextView txt_car_name       = (TextView) v.findViewById(R.id.txt_car_name);
            TextView txtv_message_count = (TextView) v.findViewById(R.id.txtv_message_count);
            TextView txtv_request_count = (TextView) v.findViewById(R.id.txtv_request_count);
            v.findViewById(R.id.txt_flag).setVisibility(View.GONE);

            img_driver_img = (ImageView) v.findViewById(R.id.img_driver_img);
            ImageView img_vehicle = (ImageView) v.findViewById(R.id.img_man);

            v.findViewById(R.id.img_phn).setVisibility(View.GONE);

            LinearLayout rel_my_ride_edit = (LinearLayout) v.findViewById(R.id.rel_my_ride_edit);

            FloatingActionButton btn_edit = (FloatingActionButton) v.findViewById(R.id.btn_edit);
            FloatingActionButton btn_del  = (FloatingActionButton) v.findViewById(R.id.btn_del);

            img_driver_img.setVisibility(View.GONE);

            txt_vw_ride_type.setVisibility(View.VISIBLE);
            rel_my_ride_edit.setVisibility(View.VISIBLE);

            final Getter_setter feedItem = local_list.get(position);

            //    txt_day.setText(feedItem.getLeaving_date()+"     "+ feedItem.getLeaving_time());

            long count = chat_database.get_unread_messages_count(feedItem.getTrip_id(), "");
            if (count > 0)
            {
                txtv_message_count.setText("" + count);
            }
            else
            {
                txtv_message_count.setVisibility(View.GONE);
            }



            if(trip_request_map.get(feedItem.getTrip_id()) != null)
            {
                long req_count = Long.parseLong(trip_request_map.get(feedItem.getTrip_id()));

                Log.e("req_count", "" + req_count);
                if (req_count > 0)
                {
                    txtv_request_count.setText("" + req_count);
                }
                else
                {
                    txtv_request_count.setVisibility(View.GONE);
                }

            }
            else
            {
                txtv_request_count.setVisibility(View.GONE);
            }






            if (feedItem.getIs_regular() != null)
            {
                String is_regular_basis = feedItem.getIs_regular().equals("false") ? "Ride Type" + "\n" + "One Time" : "Riding Type" + "\n" + "Daily";
                txt_vw_ride_type.setText(is_regular_basis);

                String date_day_str = feedItem.getIs_regular().equals("false") ? feedItem.getLeaving_date() : feedItem.getRegulardays();
                //      Log.e("date_day_str ","date_day_str  "+date_day_str+"   "+feedItem.getLeaving_date()+"  gggg   "+txt_vw_ride_type.getText().toString().equalsIgnoreCase("Ride Type"+ "\n" +"  One Time") );

                txt_day.setText(date_day_str + "     " + feedItem.getLeaving_time());
            }

            if (feedItem.getLeaving_from() != null)
            {
                txt_from.setText(feedItem.getLeaving_from());
            }
            if (feedItem.getLeaving_to() != null)
            {
                txt_to.setText((feedItem.getLeaving_to()));
            }

            txt_car_name.setText("Type : " + feedItem.getVehicle_type() + "\n" + "Name : " + feedItem.getVehicle_name() + "\n" + "Number : " + feedItem.getVehicle_number());

            int id = feedItem.getVehicle_type().equals("Bike") ? R.mipmap.bike : R.mipmap.car;
            img_vehicle.setBackgroundResource(id);

            if (feedItem.getImage() != null)
            {
                Picasso.with(getActivity()).load(feedItem.getImage()).
                          transform(new CircleTransform()).into(img_driver_img);
            }

            //            if (feedItem.getLeaving_date() != null)
            if (feedItem.getIs_regular().equals("false"))
            {

                Date current_date = null;
                Date date_backend = null;

                try
                {
                    current_date = sdf.parse(current_date_strng);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }

                try
                {
                    //                    Log.e("FEEDITEM date_backend ","Leaving Date"+feedItem.getLeaving_date());
                    date_backend = sdf.parse(feedItem.getLeaving_date());
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
                                Log.e("date_backend",".. "+date_backend);// Date of leavingDate
                                Log.e("current_date",".."+current_date);
                 Log.e("current_date"," current_date"+date_backend.before(current_date));
                if (date_backend.before(current_date))
                {

                    //                    txt_txt_driver_name.setPaintFlags(txt_txt_driver_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txt_day.setPaintFlags(txt_day.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txt_from.setPaintFlags(txt_from.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txt_to.setPaintFlags(txt_to.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txt_car_name.setPaintFlags(txt_car_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txt_vw_ride_type.setPaintFlags(txt_car_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }

            btn_del.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    final Dialog dialog = new Dialog(getActivity());
                    dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.mobile_custom_verify);
                    dialog.setCancelable(false);

                    TextView       text       = (TextView) dialog.findViewById(R.id.text);
                    Button         ok         = (Button) dialog.findViewById(R.id.ok);
                    Button         cancel     = (Button) dialog.findViewById(R.id.cancel);
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

                            Delete_Service(feedItem.getTrip_id());
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
            });

            btn_edit.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Getter_setter feedItem = local_list.get(position);
                    Intent        is       = null;
                    Log.e("feedItem.regular()", feedItem.getIs_regular());
                    if (feedItem.getIs_regular().equals("false"))
                    {
                        is = new Intent(getActivity(), Just_Once_offerRide.class);
                    }
                    else
                    {
                        is = new Intent(getActivity(), RegularBasis.class);
                        is.putExtra(GlobalConstants.Regular_Days, feedItem.getRegulardays());
                    }

                    is.putExtra(GlobalConstants.Name, feedItem.getCustomer_name());
                    is.putExtra(GlobalConstants.KeyNames.TripId.toString(), feedItem.getTrip_id());
                    is.putExtra(GlobalConstants.Leaving_From, feedItem.getLeaving_from());
                    is.putExtra(GlobalConstants.Leaving_To, feedItem.getLeaving_to());
                    is.putExtra(GlobalConstants.Leaving_Date, feedItem.getLeaving_date());
                    is.putExtra(GlobalConstants.Image, feedItem.getImage());
                    is.putExtra(GlobalConstants.Mobile_Number, feedItem.getMobile_number());
                    //is.putExtra(GlobalConstants.Points, feedItem.getMid_point());

                    try
                    {
                        is.putExtra(GlobalConstants.Points, feedItem.getMid_point());
                    }
                    catch (Exception ex)
                    {
                        is.putExtra(GlobalConstants.Points, "null");
                    }

                    is.putExtra("vehicle_type", feedItem.getVehicle_description());

                    is.putExtra("vehicle_name", feedItem.getVehicle_name());

                    is.putExtra("vehicle_select", feedItem.getVehicle_type());

                    is.putExtra(GlobalConstants.Round_Trip, feedItem.getRound());
                    try
                    {
                        is.putExtra(GlobalConstants.Return_Date, feedItem.getReturn_date());
                    }
                    catch (Exception ex)
                    {
                        is.putExtra(GlobalConstants.Return_Date, "null");
                    }
                    try
                    {
                        is.putExtra(GlobalConstants.Return_Time, feedItem.getReturn_time());
                    }
                    catch (Exception ex)
                    {
                        is.putExtra(GlobalConstants.Return_Time, "null");
                    }

                    is.putExtra(GlobalConstants.Available_Seats, feedItem.getSeats_available());
                    is.putExtra(GlobalConstants.Rate_seat, feedItem.getRate_per_seat());
                    is.putExtra(GlobalConstants.Leaving_Time, feedItem.getLeaving_time());
                    is.putExtra(GlobalConstants.Is_Regular, feedItem.getIs_regular());
                    is.putExtra(GlobalConstants.Vehicle_number, feedItem.getVehicle_number());

                    startActivity(is);
                    getActivity().overridePendingTransition(0, R.anim.push_down_out);
                }
            });



            return v;
        }

        public void set_data(List<Getter_setter> _data)
        {
            this.local_list = _data;
        }
    }

    /**
     @snackbarMethod
     */
    private void snackbar_method(String text)
    {
      /*  snackbar = Snackbar.make(coordinatorlayout, text, Snackbar.LENGTH_LONG).setAction("Ok", new View.OnClickListener()
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
        snackbar.show();*/

        Snackbar.make(coordinatorlayout,text,Snackbar.LENGTH_LONG).show();

        Log.e("Snackbar","Snackbar Driver");
    }

    DriverRides_BroadcastReceiver receiver;
    boolean is_receiver_registered = false;

    @Override
    public void onResume()
    {
        super.onResume();

        //
        refresh();


        if(!is_receiver_registered)
        {
            if(receiver==null)
            {

                receiver=new DriverRides_BroadcastReceiver();
            }
            getActivity().registerReceiver(receiver, new IntentFilter(Utills_G.refresh_drivertab));
            is_receiver_registered=true;
        }
    }


    @Override
    public void onDestroy()
    {
        super.onDestroy();

        if (receiver != null)
        {
            getActivity().unregisterReceiver(receiver);
        }
    }

    private class DriverRides_BroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            Log.e("Reached"," EEEEEEEEEE");
            refresh();
        }
    }


    public void show_message(String message)
    {
        if(list.size() == 0)
        {
            txt_no_ride.setText(message);

            if(message.equalsIgnoreCase("Fetching your rides. Please wait..."))
            {
                img_vw_reload.startAnimation(animRotate);
            }

        }
        else
        {
            if(message.equalsIgnoreCase("Fetching your rides. Please wait..."))
            {
                snackbar_method("Refreshing your rides. Please wait...");
            }
            else
            {
                snackbar_method(message);
            }

        }
    }
}
