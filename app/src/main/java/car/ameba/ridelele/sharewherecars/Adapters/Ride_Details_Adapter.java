package car.ameba.ridelele.sharewherecars.Adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import car.ameba.ridelele.Async_Thread.Super_AsyncTask;
import car.ameba.ridelele.Async_Thread.Super_AsyncTask_Interface;
import car.ameba.ridelele.Chat.Chat_Activity;
import car.ameba.ridelele.Chat.Chat_Database;
import car.ameba.ridelele.utills.GlobalConstants;
import car.ameba.gagan.sharewherecars.R;
import car.ameba.ridelele.sharewherecars.Ride_Details;
import car.ameba.ridelele.utills.Utills_G;

/**
 Created by sharan on 15/2/16. */
public class Ride_Details_Adapter extends BaseAdapter
{
    Context con;
    String  my_customerID, my_customer_name, gcm_lat, gcm_long, RoundTrip, IsRegulerBasis;
    ArrayList<HashMap<String, String>> list;
    SharedPreferences                  preferences;

    Utills_G utills_g = new Utills_G();
    Chat_Database chat_database;

    public Ride_Details_Adapter(Context con, ArrayList<HashMap<String, String>> list, String IsRegulerBasis, String RoundTrip)
    {
        this.con = con;
        this.list = list;
        //        tripID = list.get(0).get("TripId");

        chat_database = new Chat_Database(con);

        this.IsRegulerBasis = IsRegulerBasis;
        this.RoundTrip = RoundTrip;

        preferences = PreferenceManager.getDefaultSharedPreferences(con);

        my_customerID = preferences.getString("CustomerId", null);
        my_customer_name = preferences.getString("first_name", null);

        gcm_lat = preferences.getString("current_lat", "0.0");
        gcm_long = preferences.getString("current_long", "0.0");

    }

    @Override
    public int getCount()
    {
        return list.size();
    }

    @Override
    public Object getItem(int position)
    {
        return list.size();
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
        LayoutInflater inflater = LayoutInflater.from(con);
        View           v        = inflater.inflate(R.layout.custom_ride_details, parent, false);

        TextView  txt_txt_driver_name = (TextView) v.findViewById(R.id.txt_driver_name);
        TextView  txt_from            = (TextView) v.findViewById(R.id.txt_from);
        TextView  txt_to              = (TextView) v.findViewById(R.id.txt_to);
        TextView  txt_cancel          = (TextView) v.findViewById(R.id.txt_cancel);
        TextView  txt_chat            = (TextView) v.findViewById(R.id.txt_chat);
        ImageView img_driver_img      = (ImageView) v.findViewById(R.id.img_driver_img);
        TextView  txtv_message_count  = (TextView) v.findViewById(R.id.txtv_message_count);

        final TextView btn_one   = (TextView) v.findViewById(R.id.btn_one);
        final TextView btn_two   = (TextView) v.findViewById(R.id.btn_two);
        final TextView btn_three = (TextView) v.findViewById(R.id.btn_three);

        RelativeLayout rel_decline = (RelativeLayout) v.findViewById(R.id.rel_decline);

        ( v.findViewById(R.id.rel_dest_ds)).setVisibility(View.GONE);;
        ImageView      img_phn     = (ImageView) v.findViewById(R.id.img_phn);

        txt_cancel.setPaintFlags(txt_cancel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        btn_three.setVisibility(View.GONE);
        txt_chat.setVisibility(View.GONE);
        txt_cancel.setVisibility(View.GONE);

        long count = chat_database.get_unread_messages_count(list.get(position).get(GlobalConstants.KeyNames.TripId.toString()), list.get(position).get(GlobalConstants.KeyNames.CustomerId.toString()));
        if (count > 0)
        {
            txtv_message_count.setText("" + count);
        }
        else
        {
            txtv_message_count.setVisibility(View.GONE);
        }

        String flag = list.get(position).get("Flag");

        txt_txt_driver_name.setText(list.get(position).get("CustomerName"));

        utills_g.setRoundImage(con, img_driver_img, list.get(position).get("CustomerPhoto"));

        if (flag.equals("0") || flag.equals("5"))
        {
            //No Request
            btn_one.setText("Decline");
            btn_two.setText("Accept");
        }
        else if (flag.equals("3") || flag.equals("2"))
        {
            //Already Decline/Cancel
            txt_chat.setVisibility(View.VISIBLE);
            rel_decline.setVisibility(View.GONE);
            txt_chat.setText("Your request declined");
            btn_one.setText("OK");
        }
        else if (flag.equals("1"))
        {
            //Accepted
            txt_cancel.setVisibility(View.VISIBLE);
            btn_one.setText("Ask location on map");
            btn_two.setText("Message");
        }

        img_phn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (list.get(position).get("CustomerMobileNo") != null)
                {
                    ///call number
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + list.get(position).get("CustomerMobileNo")));
                    try
                    {
                        con.startActivity(intent);
                    }
                    catch (android.content.ActivityNotFoundException ex)
                    {
                        ex.printStackTrace();
                    }
                }

            }
        });
        txt_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                HitService_Request(position, "CancelByDriver");
            }
        });

      /*  btn_three.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(con, MapActivity.class);
                con.startActivity(i);
            }
        });*/

        btn_one.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                if (btn_one.getText().toString().equals("Decline"))
                {
                    HitService_Request(position, "Decline"); //it sends message to rider that driver decline your req and rider will not able to send request again

                }
                /*else if (btn_one.getText().toString().equals("SEND"))
                {

                    HitService_Request(list.get(position).get("RiderID"), list.get(position).get("RequestId"), "Accept");
                }*/
                else if (btn_one.getText().toString().equals("Ask location on map"))
                {

                    HitService_Adress_message(position, my_customer_name + " wants to locate you on map.", "request");

                }

            }
        });
        rel_decline.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (btn_two.getText().toString().equals("Accept"))
                {
                    HitService_Request(position, "Accept");

                }
                else if (btn_two.getText().toString().equals("Message") /*|| btn_two.getText().toString().equals("Send")*/)
                {
                    Intent i = new Intent(con, Chat_Activity.class);
                    i.putExtra(GlobalConstants.KeyNames.RiderId.toString(), list.get(position).get(GlobalConstants.KeyNames.CustomerId.toString()));
                    i.putExtra(GlobalConstants.KeyNames.DriverId.toString(), list.get(position).get(GlobalConstants.KeyNames.DriverId.toString()));
                    i.putExtra(GlobalConstants.KeyNames.CustomerPhoto.toString(), list.get(position).get(GlobalConstants.KeyNames.CustomerPhoto.toString()));
                    i.putExtra(GlobalConstants.KeyNames.TripId.toString(), list.get(position).get(GlobalConstants.KeyNames.TripId.toString()));
                    i.putExtra(GlobalConstants.KeyNames.RequestId.toString(), list.get(position).get(GlobalConstants.KeyNames.RequestId.toString()));
                    con.startActivity(i);

                }
            }
        });

        return v;
    }

    //**********************************************************************************************************************

    public void HitService_Request(final int position, final String request_type)
    {
        HashMap<String, String> map = new HashMap<>();

        map.put("TripId", list.get(position).get("TripId"));
        if (request_type.equalsIgnoreCase("Decline"))
        {
            Log.e("CHeckDecline", "if decline if decline");
            map.put("CustomerId", list.get(position).get("CustomerId"));
        }
        else
        {
            map.put("CustomerId", request_type.equals("CancelByDriver") | request_type.equals("Accept") ? list.get(position).get("CustomerId") : my_customerID);

        }
        // map.put("CustomerId", request_type.equals("CancelByDriver") | request_type.equals("Accept") ? list.get(position).get("CustomerId") : my_customerID);
        map.put("RequestType", request_type);
        map.put("RequestID", list.get(position).get("RequestId"));
        map.put("IsRegulerBasis", IsRegulerBasis);
        map.put("RoundTrip", RoundTrip);

        String url = GlobalConstants.Url+"Trip/TripRequest";

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

                        if (request_type.equalsIgnoreCase("Decline") || request_type.equalsIgnoreCase("Accept"))
                        {

                            chat_database.delete_trip_request(list.get(position).get("RequestId"),"");
                        }
                        else if(request_type.equals("CancelByDriver"))
                        {
                            chat_database.delete_chat(list.get(position).get("TripId"),list.get(position).get("CustomerId"));
                        }
                        ((Ride_Details) con).refresh();

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

    private void HitService_Adress_message(int position, String Message, String status)
    {
        HashMap<String, String> map = new HashMap<>();

        map.put("RiderId", list.get(position).get("CustomerId"));
        map.put("DriverId", list.get(position).get("DriverId"));
        map.put("TripId", list.get(position).get("TripId"));
        map.put("Message", Message);
        map.put("Latitude", gcm_lat);
        map.put("Longitude", gcm_long);
        map.put("Flag", "Rider");
        map.put("Status", status);
        map.put("RequestId", list.get(position).get("RequestId"));

        Log.e("Sharan HitService_Adress_message", "" + map);

        String url = GlobalConstants.Url+"Trip/LocationShare";

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




}
