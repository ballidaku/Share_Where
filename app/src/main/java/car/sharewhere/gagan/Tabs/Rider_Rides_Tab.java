package car.sharewhere.gagan.Tabs;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import car.sharewhere.gagan.Chat.Chat_Database;
import car.sharewhere.gagan.WebServices.Asnychronus_notifier;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.WebServices.Json_AsnycTask;
import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.sharewherecars.Ride_Details;
import car.sharewhere.gagan.utills.CircleTransform;
import car.sharewhere.gagan.utills.ConnectivityDetector;
import car.sharewhere.gagan.utills.Getter_setter;

/**
 Created by ameba on 12/1/16. */
public class Rider_Rides_Tab extends Fragment implements Asnychronus_notifier
{
    SharedPreferences.Editor editor;
    ListView                 list_home_recent;
    TextView                 txt_no_ride;
    ImageView                img_driver_img, img_vw_reload;
    SharedPreferences preferences;
    String            customerID, customername, format, current_time, current_date_strng, custom_vehicle_select, custom_vehicle_type, custom_vehicle_name, flag_to_send;
    ConnectivityDetector cd;
    HashMap<String, String> data_home = new HashMap<>();
    CoordinatorLayout coordinatorlayout;
    Snackbar          snackbar;
    ProgressDialog    dialog;
    Button            btn_ofer_ride;
    Date              current_date;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    MidPointAdapter adater;
    List<Getter_setter> feedItemList     = new ArrayList<>();
    ArrayList<String>   array_image      = new ArrayList<String>();
    ArrayList<String>   array_name       = new ArrayList<String>();
    ArrayList<String>   array_number     = new ArrayList<String>();
    ArrayList<String>   array_customerId = new ArrayList<String>();
    ArrayList<String>   array_riderID    = new ArrayList<String>();
    ArrayList<String>   array_driverId   = new ArrayList<String>();
    ArrayList<String>   array_flag       = new ArrayList<String>();
    ArrayList<String>   array_requestID  = new ArrayList<String>();

    Chat_Database chat_database;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.tab_my_ride, container, false);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();
        customerID = preferences.getString("CustomerId", null);
        customername = preferences.getString("first_name", null);

        chat_database = new Chat_Database(getActivity());

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        cd = new ConnectivityDetector(getActivity());
        adater = new MidPointAdapter();

        findviewbyID(view);

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

        txt_no_ride.setText(R.string.home_new_No_ride_as_rider);

        btn_ofer_ride.setVisibility(View.GONE);

        img_vw_reload.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HitServiceEarlierSearc();
            }
        });

        list_home_recent.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {

                //                Intent is = new Intent(getActivity(), Car_owner_Activity.class);
                Intent is = new Intent(getActivity(), Ride_Details.class);

                Getter_setter feedItem = feedItemList.get(position);

                is.putExtra(GlobalConstants.KeyNames.TripId.toString(), feedItem.getTrip_id());
                is.putExtra(GlobalConstants.KeyNames.CustomerId.toString(), preferences.getString("CustomerId", null));

                startActivity(is);
                getActivity().overridePendingTransition(0, R.anim.push_down_out);
            }
        });
    }

    @Override
    public void onResume()
    {
        super.onResume();

        method_current_time();
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

    /**
     @HitService(AllRides)
     */
    private void HitServiceEarlierSearc()
    {

        data_home.put("RiderId", customerID);

        if (!cd.isConnectingToInternet())
        {
            snackbar_method("No internet connection!");
        }
        else
        {
            Json_AsnycTask task = new Json_AsnycTask(getActivity(), "http://112.196.34.42:9091/Trip/GetRiderDetail", GlobalConstants.POST_SERVICE_METHOD1, data_home);
            task.setOnResultsListener(this);
            task.execute();
            dialog = ProgressDialog.show(getActivity(), "", "Fetching your rides. Please wait...", true);
            dialog.setCancelable(true);
            dialog.show();
        }
    }

    @Override
    public void onResultsSucceeded_Get_Method1(JSONObject result) {}

    @Override
    public void onResultsSucceeded_Get_Method2(JSONObject result)
    {

    }

    @Override
    public void onResultsSucceeded_Post_Method1(JSONObject result)
    {
        Log.e("response rider", "Login==" + result);
        {
            dialog.dismiss();

            if (result != null)
            {
                if (result.length() == 0)
                {
                    list_home_recent.setVisibility(View.GONE);
                }
                if (result.optString("Message").equals("Internal Server Error."))
                {
                    snackbar_method("Try Again!! Internal server error");

                }
                else if (result.optString("Status").equals("success"))
                {

                    list_home_recent.setVisibility(View.VISIBLE);
                    txt_no_ride.setVisibility(View.GONE);
                    img_vw_reload.setVisibility(View.GONE);
                    btn_ofer_ride.setVisibility(View.GONE);
                    array_image.clear();
                    array_name.clear();
                    array_number.clear();
                    array_driverId.clear();
                    array_riderID.clear();
                    array_customerId.clear();
                    array_flag.clear();
                    array_requestID.clear();

                    try
                    {
                        JSONArray arr = result.getJSONArray("Message");
                        if (arr.length() == 0)
                        {
                            snackbar_method("No Ride to show");

                            list_home_recent.setVisibility(View.GONE);
                            txt_no_ride.setVisibility(View.VISIBLE);
                            img_vw_reload.setVisibility(View.VISIBLE);
                            btn_ofer_ride.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            feedItemList.clear();
                            for (int i = 0; i < arr.length(); i++)
                            {
                                list_home_recent.setVisibility(View.VISIBLE);

                                JSONObject jsonobject = arr.getJSONObject(i);
                                Getter_setter item = new Getter_setter();

                                item.setLeaving_date(jsonobject.optString("DepartureDate"));

                                item.setLeaving_to(jsonobject.optString("LeavingTo"));
                                item.setVehicle_name(jsonobject.optString("VehicleName"));
                                item.setVehicle_type(jsonobject.optString("VehicleType"));

                                item.setLeaving_time(jsonobject.optString("DepartureTime"));
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

                                item.setCustomer_name(jsonobject.optString("CustomerName"));
                                item.setImage(jsonobject.optString("CustomerPhoto"));
                                item.setLeaving_from(jsonobject.optString("LeavingFrom"));
                                item.setMobile_number(jsonobject.optString("CustomerMobileNo"));
                                item.setTrip_id(jsonobject.optString("TripId"));

                                array_requestID.add(jsonobject.optString("RequestId"));
                                array_flag.add(jsonobject.optString("RequestStatus"));
                                array_riderID.add(jsonobject.optString("RequestSendBy"));
                                array_driverId.add(jsonobject.optString("RequestSendTo"));

                                if (jsonobject.has("RequestAcceptModelList"))
                                {
                                    JSONArray arr2 = jsonobject.getJSONArray("RequestAcceptModelList");

                                    Log.e("arr length", arr2.length() + "");

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
                        }
                        adater = new MidPointAdapter();
                        list_home_recent.setAdapter(adater);
                        adater.notifyDataSetChanged();
                        GlobalConstants.setListViewHeightBasedOnItems(list_home_recent, feedItemList.size());
                    }
                    catch (Exception ex)
                    {
                        snackbar_method(ex.toString());
                    }

                }
                else if (result.optString("Status").equals("error"))
                {
                    snackbar_method("No Ride to show");
                }
            }
            else
            {
                snackbar_method("No Ride to show");
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
        public MidPointAdapter()
        {
            super();
        }

        @Override
        public int getCount()
        {
            return feedItemList.size();
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

            TextView txt_from         = (TextView) v.findViewById(R.id.txt_from);
            TextView txt_to           = (TextView) v.findViewById(R.id.txt_to);
            TextView txt_driver_name  = (TextView) v.findViewById(R.id.txt_driver_name);
            TextView txt_vw_ride_type = (TextView) v.findViewById(R.id.txt_vw_ride_type);
            TextView txt_car_name     = (TextView) v.findViewById(R.id.txt_car_name);
            TextView txtv_message_count     = (TextView) v.findViewById(R.id.txtv_message_count);
            v.findViewById(R.id.txt_flag).setVisibility(View.GONE);

            img_driver_img = (ImageView) v.findViewById(R.id.img_driver_img);
            ImageView img_vehicle = (ImageView) v.findViewById(R.id.img_man);

            ImageView img_phn = (ImageView) v.findViewById(R.id.img_phn);

            LinearLayout rel_my_ride_edit = (LinearLayout) v.findViewById(R.id.rel_my_ride_edit);

            v.findViewById(R.id.btn_edit).setVisibility(View.GONE);
            v.findViewById(R.id.btn_del).setVisibility(View.GONE);

            txt_vw_ride_type.setVisibility(View.VISIBLE);
            rel_my_ride_edit.setVisibility(View.VISIBLE);

            final Getter_setter feedItem = feedItemList.get(position);

            long count =chat_database.get_unread_messages_count(feedItem.getTrip_id(), "");
            if(count>0)
            {
                txtv_message_count.setText(""+count);
            }
            else
            {
                txtv_message_count.setVisibility(View.GONE);
            }


//            txt_day.setText(feedItem.getLeaving_date() + "     " + feedItem.getLeaving_time());

            if (feedItem.getImage() != null)
            {
                Picasso.with(getActivity()).load(feedItem.getImage()).
                          transform(new CircleTransform()).into(img_driver_img);
            }

            txt_driver_name.setText(feedItem.getCustomer_name());

            if (feedItem.getIs_regular() != null)
            {
                String is_regular_basis = feedItem.getIs_regular().equals("false") ? "Ride Type" + "\n" + "One Time" : "Riding Type" + "\n" + "Daily";
                txt_vw_ride_type.setText(is_regular_basis);
                Log.e("Riders",""+feedItem.getRegulardays());
                String date_day_str= feedItem.getIs_regular().equals("false")? feedItem.getLeaving_date() : feedItem.getRegulardays();
                Log.e("Rider ","date_day_str "+date_day_str+" "+feedItem.getLeaving_date()+" gggg "+txt_vw_ride_type.getText().toString().equalsIgnoreCase("Ride Type"+ "\n" +" One Time") );

                txt_day.setText(date_day_str+" "+ feedItem.getLeaving_time());
            }

            if (feedItem.getLeaving_from() != null)
            {
                txt_from.setText((feedItem.getLeaving_from()));
            }
            if (feedItem.getLeaving_to() != null)
            {
                txt_to.setText((feedItem.getLeaving_to()));
            }

            txt_car_name.setText("Type : " + feedItem.getVehicle_type() + "\n" + "Name : " + feedItem.getVehicle_name() + "\n" + "Number : " + feedItem.getVehicle_number());

            int id = feedItem.getVehicle_type().equals("Bike") ? R.mipmap.bike : R.mipmap.car;
            img_vehicle.setBackgroundResource(id);

            img_phn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (feedItem.getMobile_number() != null)
                    {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + feedItem.getModel_CustomerMobileNo()));
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

        /*    if (feedItem.getLeaving_date() != null)
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
                    date_backend = sdf.parse(feedItem.getLeaving_date());
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
                if (date_backend.before(current_date))
                {

                    //                    txt_txt_driver_name.setPaintFlags(txt_txt_driver_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txt_day.setPaintFlags(txt_day.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txt_from.setPaintFlags(txt_from.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txt_to.setPaintFlags(txt_to.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txt_car_name.setPaintFlags(txt_car_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    txt_vw_ride_type.setPaintFlags(txt_car_name.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
            }*/

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
        snackbar = Snackbar.make(coordinatorlayout, text, Snackbar.LENGTH_LONG).setAction("Ok", new View.OnClickListener()
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
