package car.ameba.ridelele.Chat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import car.ameba.ridelele.Async_Thread.Super_AsyncTask;
import car.ameba.ridelele.Async_Thread.Super_AsyncTask_Interface;
import car.ameba.ridelele.utills.GlobalConstants;
import car.ameba.ridelele.sharewherecars.Adapters.Chat_sharan_Adapter;
import car.ameba.gagan.sharewherecars.R;
import car.ameba.ridelele.sharewherecars.Ride_Details;
import car.ameba.ridelele.utills.ConnectivityDetector;
import car.ameba.ridelele.utills.Dialogs;
import car.ameba.ridelele.utills.Utills_G;

public class Chat_Activity extends AppCompatActivity implements View.OnClickListener
{
    Context       con;
    ListView      listv_chat;

    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    String RiderId, other_user_photo, DriverId;
    EditText edtv_msg;

    SharedPreferences preferences;
    String            my_customerID, my_customer_name,  isDriverRider, RequestId;

    public static String TripId, other_user_id;

    ConnectivityDetector connectivityDetector;
    Dialogs dialogs   = new Dialogs();
    String  fromWhere = "";
    Chat_Database chat_database;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_);

        con = this;

        chat_database = new Chat_Database(con);

        connectivityDetector = new ConnectivityDetector(con);

        findViewById(R.id.back).setOnClickListener(this);
        listv_chat = (ListView) findViewById(R.id.listv_chat);

        findViewById(R.id.send).setOnClickListener(this);
        findViewById(R.id.smilly).setOnClickListener(this);
        edtv_msg = (EditText) findViewById(R.id.edtv_msg);

        preferences = PreferenceManager.getDefaultSharedPreferences(con);

        my_customerID = preferences.getString("CustomerId", null);
        my_customer_name = preferences.getString("first_name", null);

        RiderId = getIntent().getStringExtra(GlobalConstants.KeyNames.RiderId.toString());
        DriverId = getIntent().getStringExtra(GlobalConstants.KeyNames.DriverId.toString());
        RequestId = getIntent().getStringExtra(GlobalConstants.KeyNames.RequestId.toString());

        other_user_photo = getIntent().getStringExtra(GlobalConstants.KeyNames.CustomerPhoto.toString()); // this will be driver as well as rider

        TripId = getIntent().getStringExtra(GlobalConstants.KeyNames.TripId.toString());

        other_user_id = my_customerID.equalsIgnoreCase(RiderId) ? DriverId : RiderId;

        isDriverRider = my_customerID.equalsIgnoreCase(RiderId) ? GlobalConstants.KeyNames.Driver.toString() : GlobalConstants.KeyNames.Rider.toString();

        //        Log.e("Chat", "" + database.get_chat_data(other_user_id, TripId));

        try
        {
            fromWhere = getIntent().getStringExtra(GlobalConstants.KeyNames.fromWhere.toString());
        }
        catch (Exception e)
        {
            fromWhere = "";
            e.printStackTrace();
        }

        refresh("Reciever");

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
            Intent i=new Intent(con, Ride_Details.class);
            i.putExtra(GlobalConstants.KeyNames.fromWhere.toString(),GlobalConstants.KeyNames.Messages.toString());
            i.putExtra(GlobalConstants.KeyNames.CustomerId.toString(),other_user_id);
            i.putExtra(GlobalConstants.KeyNames.TripId.toString(),TripId);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }


    }




    public void refresh(String who)
    {
        set_chat_data(chat_database.get_chat_data(other_user_id, TripId), who);
    }

    Chat_sharan_Adapter adapter;

    public void set_chat_data(ArrayList<HashMap<String, String>> list, String who)
    {


//        Log.e("Chat Data",""+list);
        try
        {
            if (this.list.size() == 0)
            {
                this.list = list;
                adapter = new Chat_sharan_Adapter(con, list, other_user_photo);
                listv_chat.setAdapter(adapter);
            }
            else
            {
                if (who.equals("Sender"))
                {
                    adapter.add_data(list);

                }
                else
                {
                    adapter.add_dataAll(list);

                }

                adapter.notifyDataSetChanged();

            }
            listv_chat.smoothScrollToPosition(adapter.getCount());
        }
        catch (Exception e)// when no data in the message list
        {


            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.send:

                String msg = edtv_msg.getText().toString().trim();
                send_msg(msg);

                break;

            case R.id.back:

                call_back();
                break;

            case R.id.smilly:

                open_dialog();

                break;
        }
    }

    private void send_msg(String msg)
    {
        if (connectivityDetector.isConnectingToInternet())
        {
            if (!msg.isEmpty())
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("message_id", "0");
                map.put("sender_id", my_customerID);
                map.put("reciever_id", other_user_id);
                map.put("message", msg);
                map.put("tripId", TripId);
                map.put("message_status", "R");

                chat_database.save_message(map);

                edtv_msg.setText("");

                refresh("Sender");

                HitService_Adress_message(my_customer_name + " : " + msg, "msg");
            }
        }
        else
        {
            GlobalConstants.show_Toast("Please check your network.", con);
        }
    }

    RadioGroup radioGroup;

    private void open_dialog()
    {
        View.OnClickListener send = new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                int selectedId = radioGroup.getCheckedRadioButtonId();

                RadioButton radioButton = (RadioButton) dialogs.dialog.findViewById(selectedId);

                send_msg(radioButton.getText().toString());

                dialogs.dialog.dismiss();

            }
        };
        radioGroup = dialogs.send_message(con, send);
    }




    private void HitService_Adress_message(String message, String status)
    {

        HashMap<String, String> map = new HashMap<>();

        map.put("RiderId", RiderId);
        map.put("DriverId", DriverId);
        map.put("TripId", TripId);
        map.put("Message", message);
        map.put("Latitude", preferences.getString("current_lat", "0.0"));
        map.put("Longitude", preferences.getString("current_long", "0.0"));
        map.put("Flag", isDriverRider);
        map.put("Status", status);
        map.put("RequestId", RequestId);

        Log.e("Sharan HitService_Adress_message", "" + map);

        String url = GlobalConstants.Url+"Trip/LocationShare";

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
        }, false));

    }

    Refresh_chat_BroadcastReceiver receiver;
    boolean is_receiver_registered = false;

    @Override
    protected void onResume()
    {

        chat_database.change_unread_to_read(TripId,other_user_id);

//        Log.e("Chat Data",""+chat_database.get_chat_all_data());


        preferences.edit().putBoolean("is_chat_opened", true).apply();

        Log.e("onResume", " is_receiver_registered " + is_receiver_registered + "nnnnnn " + receiver);
        super.onResume();

        if (!is_receiver_registered)
        {
            if (receiver == null)
            {

                receiver = new Refresh_chat_BroadcastReceiver();
            }
            this.registerReceiver(receiver, new IntentFilter(Utills_G.refresh_chat));
            is_receiver_registered = true;
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        preferences.edit().putBoolean("is_chat_opened", false).apply();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();

//        preferences.edit().putBoolean("is_chat_opened", false).apply();

        if (receiver != null)
        {
            this.unregisterReceiver(receiver);
        }
    }

    private class Refresh_chat_BroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            refresh("Sender");
        }
    }
}
