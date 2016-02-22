package car.sharewhere.gagan.sharewherecars.Adapters;

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
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import car.sharewhere.gagan.Async_Thread.Super_AsyncTask;
import car.sharewhere.gagan.Async_Thread.Super_AsyncTask_Interface;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.sharewherecars.Ride_Details;
import car.sharewhere.gagan.utills.Utills_G;

/**
 Created by sharan on 15/2/16. */
public class Ride_Details_Adapter extends BaseAdapter
{
    Context con;
    String   my_customerID,my_customer_name, gcm_lat, gcm_long,RoundTrip,IsRegulerBasis;
    ArrayList<HashMap<String, String>> list;
    SharedPreferences                  preferences;
    Utills_G utills_g = new Utills_G();

    public Ride_Details_Adapter(Context con, ArrayList<HashMap<String, String>> list,String IsRegulerBasis,String RoundTrip)
    {
        this.con = con;
        this.list = list;
//        tripID = list.get(0).get("TripId");

        this.IsRegulerBasis=IsRegulerBasis;
        this.RoundTrip=RoundTrip;

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

        final Button btn_one   = (Button) v.findViewById(R.id.btn_one);
        final Button btn_two   = (Button) v.findViewById(R.id.btn_two);
        final Button btn_three = (Button) v.findViewById(R.id.btn_three);


        RelativeLayout             rel_Top_cancel      = (RelativeLayout) v.findViewById(R.id.rel_Top_cancel);
        RelativeLayout             rel_dest_ds         = (RelativeLayout) v.findViewById(R.id.rel_dest_ds);
        ImageView             img_phn           = (ImageView) v.findViewById(R.id.img_phn);
        final AutoCompleteTextView dialog_autocomplete = (AutoCompleteTextView) v.findViewById(R.id.dialog_autocomplete);

        txt_cancel.setPaintFlags(txt_cancel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        dialog_autocomplete.setVisibility(View.GONE);
        btn_three.setVisibility(View.GONE);
        txt_chat.setVisibility(View.GONE);
        txt_cancel.setVisibility(View.GONE);
        rel_dest_ds.setVisibility(View.GONE);
        dialog_autocomplete.setThreshold(1);

   /*     dialog_autocomplete.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                if (editable.length()>0)
                {
                    dialog_autocomplete.setError(null);
                }

            }
        });*/

        String flag = list.get(position).get("Flag");

        txt_txt_driver_name.setText(list.get(position).get("CustomerName"));

        utills_g.setRoundImage(con, img_driver_img, list.get(position).get("CustomerPhoto"));

        if (flag.equals("0") || flag.equals("5"))
        {
            //No Request
            btn_one.setText("Accept");
            btn_two.setText("Decline");
        }
        else if (flag.equals("3") || flag.equals("2"))
        {
            //Already Decline/Cancel
            txt_chat.setVisibility(View.VISIBLE);
            btn_two.setVisibility(View.GONE);
            txt_chat.setText("Your request declined");
            btn_one.setText("OK");
        }
        else if (flag.equals("1"))
        {
            //Accepted
            txt_cancel.setVisibility(View.VISIBLE);
            dialog_autocomplete.setVisibility(View.VISIBLE);
            btn_one.setText("Ask location on map");
            btn_two.setText("Where are you?");
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
        rel_Top_cancel.setOnClickListener(new View.OnClickListener()
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

                if (btn_one.getText().toString().equals("Accept"))
                {

                    HitService_Request(position, "Accept");
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
        btn_two.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (btn_two.getText().toString().equals("Decline"))
                {

                    HitService_Request(position,"Decline");
                }
                else if (btn_two.getText().toString().equals("Where are you?") /*|| btn_two.getText().toString().equals("Send")*/)
                {
                    if (dialog_autocomplete.getText().toString().trim().length() > 0)
                    {
                        // request_type = "Where are you";
                        HitService_Adress_message(position,my_customer_name + " : " +  dialog_autocomplete.getText().toString().trim(),"msg");
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

        return v;
    }

    //**********************************************************************************************************************

    public void HitService_Request(int position, String request_type)
    {
        HashMap<String, String> map = new HashMap<>();

        map.put("TripId", list.get(position).get("TripId"));
        map.put("CustomerId", request_type.equals("CancelByDriver") | request_type.equals("Accept")? list.get(position).get("CustomerId"): my_customerID);
        map.put("RequestType", request_type);
        map.put("RequestID", list.get(position).get("RequestId"));
        map.put("IsRegulerBasis", IsRegulerBasis);
        map.put("RoundTrip", RoundTrip);

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

                        ((Ride_Details)con).refresh();

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

    private void HitService_Adress_message(int position, String Message,String status)
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

        String url = "http://112.196.34.42:9091/Trip/LocationShare";

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
