package car.ameba.ridelele.sharewherecars.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import car.ameba.ridelele.Chat.Chat_Database;
import car.ameba.ridelele.sharewherecars.Just_Once_offerRide;
import car.ameba.ridelele.sharewherecars.MainActivity;
import car.ameba.gagan.sharewherecars.R;
import car.ameba.ridelele.sharewherecars.RegularBasis;
import car.ameba.ridelele.utills.Utills_G;

public class OfferA_a_Ride extends FragmentG implements View.OnClickListener
{

    LinearLayout      llay_ride_request;
    SharedPreferences preferences;

    TextView txtv_request_message/*,txtv_request_message_heading*/;

    Context       con;
    Chat_Database chat_database;// = new Chat_Database()




    OfferRide_BroadcastReceiver receiver;
    boolean is_receiver_registered = false;


    public OfferA_a_Ride()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.fragment_offer_a_a__ride, container, false);
        setActionBar(view, "Offer A Ride");
        findViewById(view);

        con = getActivity();

        chat_database = new Chat_Database(con);

        preferences = PreferenceManager.getDefaultSharedPreferences(con);

        return view;
    }

    private void findViewById(View view)
    {
        (view.findViewById(R.id.rel_just_once)).setOnClickListener(this);
        (view.findViewById(R.id.regular_basis)).setOnClickListener(this);

        (llay_ride_request = (LinearLayout) view.findViewById(R.id.llay_ride_request)).setOnClickListener(this);

        txtv_request_message = (TextView) view.findViewById(R.id.txtv_request_message);
//        txtv_request_message_heading = (TextView) view.findViewById(R.id.txtv_request_message_heading);

    }


    @Override
    public void onResume()
    {
        super.onResume();
        preferences.edit().putBoolean("is_offerride_opened", true).apply();

        refresh();

        if(!is_receiver_registered)
        {
            if(receiver==null)
            {

                receiver=new OfferRide_BroadcastReceiver();
            }
            getActivity().registerReceiver(receiver,new IntentFilter(Utills_G.refresh_OfferRide));
            is_receiver_registered=true;
        }

    }

    @Override
    public void onPause()
    {
        super.onPause();
        preferences.edit().putBoolean("is_offerride_opened", false).apply();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        preferences.edit().putBoolean("is_offerride_opened", false).apply();

        if(receiver != null)
        {
            getActivity().unregisterReceiver(receiver);
        }
    }


    private class OfferRide_BroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            Log.e("Reached"," EEEEEEEEEE");
            refresh();
        }
    }

    private void refresh()
    {
        get_umread_request();
    }

    private void get_umread_request()
    {
        //        chat_database.delete_trip_request("1001");
        //Log.e("Get all request data", "" + chat_database.get_request_all_data());
        //        Log.e("Get all request data", "" + chat_database.get_tripid_unread_req_count_data());

        long message_count = chat_database.get_unread_messages_count("", "");
        long request_count = chat_database.get_unread_request_count();

        Log.e("Message count in Offer Ride", "" + message_count);
        Log.e("Request count in Offer Ride", "" + request_count);

        //        Log.e("Request data",""+chat_database.get_request_all_data());

        if (request_count == 0 && message_count==0)
        {
            llay_ride_request.setVisibility(View.GONE);
        }
        else
        {
            llay_ride_request.setVisibility(View.VISIBLE);

            String req_data=request_count==0?"":"Ride Request ( " + request_count + " )";
            String msg_data=message_count==0?"":"Messages Recieved ( " + message_count + " )";


            String data=req_data.isEmpty()?""+msg_data:req_data+"\n"+msg_data;


            txtv_request_message.setText(data);
        }
    }

    //**************************************************************************************************************
/*
    public void takeLocation(Location latlng)
    {
        String address_current = Utills_G.address(con, latlng.getLatitude(), latlng.getLongitude()).trim();

        SharedPreferences.Editor editor = preferences.edit();
        if (!address_current.equals(""))
        {
            editor.putString("current_city", address_current);
        }

        editor.putString("current_lat", String.valueOf(latlng.getLatitude()));
        editor.putString("current_long", String.valueOf(latlng.getLongitude()));
        editor.apply();
        //        Log.e("location details are ", "latitude   " + latlng.getLatitude() + "  longitutde " + latlng.getLongitude());

    }*/

    //**************************************************************************************************************

/*    private void GetLocation()
    {

        new GiveMeLocationS(getActivity(), new Location_Interface()
        {
            @Override
            public void onTaskCompleted(Location output)
            {
                try
                {
                    Log.e("Sharan Latitude", "" + output.getLatitude());
                    Log.e("Sharan Longitude", "" + output.getLongitude());
                    takeLocation(output);

                }
                catch (Exception ex)
                {
                    Log.e("Exception is", ex.toString());
                }
            }
        });

    }*/

    //**************************************************************************************************************


    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.rel_just_once:

                Intent i = new Intent(getActivity(), Just_Once_offerRide.class);
                startActivity(i);

                break;

            case R.id.regular_basis:

                Intent ii = new Intent(getActivity(), RegularBasis.class);
                startActivity(ii);

                break;

            case R.id.llay_ride_request:

                ((MainActivity) getActivity()).displayView(R.id.nav_offer_ride);

                break;
        }
    }
}