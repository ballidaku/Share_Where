package car.sharewhere.gagan.utills;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import org.json.JSONObject;

import java.util.HashMap;

import car.sharewhere.gagan.Chat.Chat_Activity;
import car.sharewhere.gagan.Chat.Chat_Database;
import car.sharewhere.gagan.Tabs.Driver_Rides_Tab;
import car.sharewhere.gagan.Tabs.Rider_Rides_Tab;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.sharewherecars.MainActivity;
import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.sharewherecars.Registeration;
import car.sharewhere.gagan.sharewherecars.Ride_Details;
import car.sharewhere.gagan.sharewherecars.fragments.My_Rides;
import car.sharewhere.gagan.sharewherecars.fragments.OfferA_a_Ride;

/**
 Created by ameba on 10/11/15. */
public class GCMIntentService extends GCMBaseIntentService
{

    private static String trip_id              = "";
    private static String customer_name        = "";
    private static String customer_pic         = "";
    private static String customer_sender_id   = "";
    private static String customer_flag        = "";
    private static String customer_requestID   = "";
    private static String Status               = "";
    private static String customer_message     = "";
    private static String customer_rider_id    = "";
    private static String customer_driver_id   = "";
    private static String customer_latitude    = "";
    private static String customer_longitude   = "";
    private static String customer_leavingfrom = "";
    private static String customer_leavingto   = "";
    private static String customer_mobile      = "";
    private static String DepartureDate        = "";
    private static int    notificatn_counter   = 0;
    Bundle bun;
    boolean can_generate_notification = true;

    Context con;

    Chat_Database     database;
    SharedPreferences preferences;
    String            my_customerID;
    int message_noti_id = 1000;
    int request_noti_id = 2000;

    long message_count = 0;

    long trip_count   = 0;
    long sender_count = 0;

    //Ride Requests
    long ride_request_count      = 0;
    long ride_request_trip_count = 0;

    boolean is_myrides_opened = false;
    boolean is_offerride_opened = false;
    boolean is_ridedetails_opened = false;
    boolean is_chat_opened = false;


    String other_customer_id;

    public GCMIntentService()
    {

        super("1056688974676");

    }

    @Override
    protected void onRegistered(Context context, String registrationId)
    {
    }

    @Override
    protected void onUnregistered(Context context, String registrationId)
    {
    }

    @Override
    protected void onMessage(Context context, Intent intent)
    {

        database = new Chat_Database(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        my_customerID = preferences.getString("CustomerId", "");

        can_generate_notification = true;

        //Messages
        trip_count = 0;
        sender_count = 0;

        //Ride Request
        ride_request_count = 0;
        ride_request_trip_count = 0;

        //My Rides
        is_myrides_opened = preferences.getBoolean("is_myride_opened", false);
        Log.e("IS My Rides Opened In GCM", "" + is_myrides_opened);

        //Offer Rides
        is_offerride_opened = preferences.getBoolean("is_offerride_opened", false);
        Log.e("IS Offer Rides Opened In GCM", "" + is_offerride_opened);

        //Ride Details
        is_ridedetails_opened=preferences.getBoolean("is_ridedetails_opened", false);
        Log.e("IS Ride Details Opened In GCM", "" + is_ridedetails_opened);

        // Chat Activity
        is_chat_opened=preferences.getBoolean("is_chat_opened", false);
        Log.e("IS Chat Activity Opened In GCM", "" + is_chat_opened);


        try
        {
            Log.e("GCM++++++++++++++++++++++", "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            notificatn_counter++;
            this.con = context;
            bun = intent.getExtras();

            final String message_new = bun.getString("message");
            Log.e("message_gcm", message_new);

            final JSONObject jsonNoTi = new JSONObject(message_new);

            customer_name = jsonNoTi.optString("CustomerName");
            trip_id = jsonNoTi.optString("TripId");
            customer_pic = jsonNoTi.optString("CustomerPhoto");
            customer_sender_id = jsonNoTi.optString("CustomerId");
            customer_flag = jsonNoTi.optString("Flag");
            customer_requestID = jsonNoTi.optString("RequestId");
            Status = jsonNoTi.optString("Status");
            customer_message = jsonNoTi.optString("Message");
            customer_latitude = jsonNoTi.optString("Latitude");
            customer_longitude = jsonNoTi.optString("Longitude");
            customer_leavingfrom = jsonNoTi.optString("LeavingFrom");
            customer_leavingto = jsonNoTi.optString("LeavingTo");
            customer_mobile = jsonNoTi.optString("CustomerMoboleNo");
            customer_driver_id = jsonNoTi.optString("DriverId");
            customer_rider_id = jsonNoTi.optString("RiderId");
            DepartureDate = jsonNoTi.optString("DepartureDate");

            //*******************************************  Messages **********************************************************

            if (Status.equals("msg"))
            {
                HashMap<String, String> map = new HashMap<>();
                map.put("message_id", customer_requestID);

                if (customer_flag.equals("Driver") && my_customerID.equals(customer_driver_id))
                {
                    map.put("sender_id", customer_rider_id);
                    map.put("reciever_id", customer_driver_id);
                }
                else if (customer_flag.equals("Rider") && my_customerID.equals(customer_rider_id))
                {
                    map.put("sender_id", customer_driver_id);
                    map.put("reciever_id", customer_rider_id);
                }

                String msg = customer_message.replace(customer_message.substring(0, customer_message.indexOf(":") + 2), "");

                Log.e("msg", "" + msg);

                map.put("message", msg);
                map.put("tripId", trip_id);

                other_customer_id = customer_flag.equals("Rider") ? customer_driver_id : customer_rider_id;

                boolean is_chat_open_with_conditions = is_chat_opened
                          && Chat_Activity.other_user_id.equalsIgnoreCase(other_customer_id)
                          && Chat_Activity.TripId.equalsIgnoreCase(trip_id);

                boolean is_ridedetails_open_with_condition=false;
                try
                {

                    if (is_ridedetails_opened== true)
                    {
                        is_ridedetails_open_with_condition = Ride_Details.TripID.equalsIgnoreCase(trip_id);
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    is_ridedetails_open_with_condition = false;
                }

                if (is_chat_open_with_conditions)  // If Chat Screen is opened
                {
                    map.put("message_status", "R");
                    database.save_message(map);

                    Utills_G.refresh_chat_BroadcastReceiver(context);
                    can_generate_notification = false;
                }
                else if (is_ridedetails_open_with_condition) //If Ride details is opened
                {
                    map.put("message_status", "UR");
                    database.save_message(map);

                    refresh_Ride_Details(context);

                }
                else if (is_myrides_opened) //If My Rides is opened
                {
                    map.put("message_status", "UR");
                    database.save_message(map);

                    if (Driver_Rides_Tab.is_drivertab_visible)
                    {
                        refresh_Driver_Tab(context);
                    }
                    else if (Rider_Rides_Tab.is_ridertab_visible)
                    {
                        refresh_Rider_Tab(context);
                    }

                }
                else if(is_offerride_opened) //If Offer Ride is opened
                {
                    map.put("message_status", "UR");
                    database.save_message(map);

                    refresh_OfferRide(context);
                }
                else
                {
                    map.put("message_status", "UR");
                    database.save_message(map);
                }

                trip_count = database.get_trip_count();
                sender_count = database.get_sender_count(my_customerID);
                message_count = database.get_unread_messages_count("", "");

                Log.e("Get Trip Count", "" + trip_count);
                Log.e("Get Sender Count", "" + sender_count);


                if (message_count > 1)
                {
                    customer_message = message_count + " unread messages from " + sender_count + " chats";
                }

            }

            //***********************************************************************************************************

            // **************************************************New Request*****************************************************************


            if (customer_flag.equals("5"))  // New Request
            {

                HashMap<String, String> map = new HashMap<>();

                map.put("CustomerId", customer_sender_id);
                map.put("CustomerMoboleNo", customer_mobile);
                map.put("CustomerName", customer_name);
                map.put("CustomerPhoto", customer_pic);
                map.put("DepartureDate", DepartureDate);
                map.put("DriverId", customer_driver_id);
                map.put("Flag", customer_flag);
                map.put("LeavingFrom", customer_leavingfrom);
                map.put("LeavingTo", customer_leavingto);
                map.put("Message", customer_message);
                map.put("RequestId", customer_requestID);
                map.put("RiderId", customer_rider_id);
                map.put("TripId", trip_id);
                map.put("request_status", "UR");

                database.save_requests(map);

                ride_request_count = database.get_unread_request_count();
                ride_request_trip_count = database.get_trip_request_count();

                if (ride_request_count > 1)
                {
                    customer_message = ride_request_count + " Ride Requests.";
                }
                else
                {
                    customer_message = customer_name + " requested to ride along with you.";
                }

                if (is_myrides_opened == true && Driver_Rides_Tab.is_drivertab_visible) //If My Rides is opened
                {
                    refresh_Driver_Tab(context);

                }


                Log.e("is_offerride_opened in GCMIntent",""+is_offerride_opened);
                if(is_offerride_opened) //If Offer Ride is opened
                {
                    refresh_OfferRide(context);
                }

            }

            //******************************************************************************************************************************


            if (preferences.getBoolean("notification_on_off", true) == true && can_generate_notification)
            {
                generateNotification(context, customer_message);
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        catch (Error e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDeletedMessages(Context context, int total)
    {

        generateNotification(context, customer_message);
    }

    @Override
    public void onError(Context context, String errorId)
    {

    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId)
    {

        return super.onRecoverableError(context, errorId);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void generateNotification(Context context, String message)
    {
        int icon = R.mipmap.ic_launcher;

        try
        {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String title = "Share Where";



            // 1 Accept

            // 2 Decline

            // 5 New Request

            // 6 cancel by rider

            // 7 cancel by driver


            String get_email_id = preferences.getString("email_id", null);
            Intent notificationIntent = null;

            if (customer_flag.matches("1|2|5|6|7"))
            {
                //                boolean is_ridedetails_opened = preferences.getBoolean("is_ridedetails_opened", false) && Ride_Details.TripID.equalsIgnoreCase(trip_id);

                boolean is_ridedetails_open_with_condition=false;

                if (is_ridedetails_opened == true)
                {
                    is_ridedetails_open_with_condition = Ride_Details.TripID.equalsIgnoreCase(trip_id);
                }

                if (is_ridedetails_open_with_condition) //If Rides Details is opened
                {
                    refresh_Ride_Details(context);
                }

                if (customer_flag.equals("5"))  // 5 New Request
                {
                    notificationIntent = new Intent(context, MainActivity.class);
                }
                else
                {
                    notificationIntent = new Intent(context, Ride_Details.class);
                }


                if(customer_flag.equals("6") || customer_flag.equals("7"))
                {
                    database.delete_chat(trip_id,customer_flag.equals("6")?customer_rider_id:customer_driver_id);
                }

                notificationIntent.putExtra(GlobalConstants.KeyNames.TripId.toString(), trip_id);
                notificationIntent.putExtra(GlobalConstants.KeyNames.CustomerId.toString(), customer_sender_id);
                notificationIntent.putExtra(GlobalConstants.KeyNames.fromWhere.toString(), GlobalConstants.KeyNames.Notification.toString());
            }
            else if (Status.equalsIgnoreCase("msg"))
            {


                if (message_count > 1 && trip_count > 1 && sender_count > 1)
                {
                    notificationIntent = new Intent(context, MainActivity.class);
                }
                else if (trip_count == 1 && sender_count == 1)
                {
                    notificationIntent = new Intent(context, Chat_Activity.class);
                }
                else
                {
                    notificationIntent = new Intent(context, Ride_Details.class);
                    notificationIntent.putExtra(GlobalConstants.KeyNames.CustomerId.toString(), other_customer_id);
                }

                notificationIntent.putExtra(GlobalConstants.KeyNames.RiderId.toString(), customer_rider_id);
                notificationIntent.putExtra(GlobalConstants.KeyNames.DriverId.toString(), customer_driver_id);
                notificationIntent.putExtra(GlobalConstants.KeyNames.RequestId.toString(), customer_requestID);
                notificationIntent.putExtra(GlobalConstants.KeyNames.CustomerPhoto.toString(), customer_pic);
                notificationIntent.putExtra(GlobalConstants.KeyNames.TripId.toString(), trip_id);
                notificationIntent.putExtra(GlobalConstants.KeyNames.fromWhere.toString(), GlobalConstants.KeyNames.Notification.toString());
            }
            else if (get_email_id != null)
            {
                notificationIntent = new Intent(context, MainActivity.class);
            }
            else
            {
                notificationIntent = new Intent(context, Registeration.class);
            }

            notificationIntent.putExtra("gcm_intent", "gcm_intent");
            notificationIntent.putExtra("customer_pic", customer_pic);
            notificationIntent.putExtra("customer_name", customer_name);
            notificationIntent.putExtra("customer_trip_id_gcm", trip_id);
            notificationIntent.putExtra("customer_sender_id_gcm", customer_sender_id);
            notificationIntent.putExtra("customer_flag", customer_flag);
            notificationIntent.putExtra("customer_Message", customer_message);
            notificationIntent.putExtra("customer_requestID", customer_requestID);
            notificationIntent.putExtra("customer_Latitude", customer_latitude);
            notificationIntent.putExtra("customer_Longitude", customer_longitude);
            notificationIntent.putExtra("customer_from", customer_leavingfrom);
            notificationIntent.putExtra("customer_to", customer_leavingto);
            notificationIntent.putExtra("customer_mobile", customer_mobile);
            notificationIntent.putExtra(GlobalConstants.KeyNames.Status.toString(), Status);
            notificationIntent.putExtra("customer_driver_id", customer_driver_id);
            notificationIntent.putExtra("customer_rider_id", customer_rider_id);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            if (defaultSound == null)
            {
                defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                if (defaultSound == null)
                {
                    defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                }
            }
            NotificationCompat.InboxStyle style = new NotificationCompat.InboxStyle();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context).
                      setContentTitle(title).
                      setContentText(message).
                      setContentIntent(intent).
                      setSmallIcon(icon).
                      setLights(Color.MAGENTA, 1, 2).
                      setAutoCancel(true).
                      setStyle(style).setSound(defaultSound);

            builder.setNumber(notificatn_counter);

          /*  if (!customer_flag.matches("1|2|5|6|7"))
            {
                builder.setOngoing(true);  // user cannot clear the notification if it is true
            }*/

            Notification not = new NotificationCompat.BigTextStyle(builder).bigText(message).build();

            //            	not.number=count++;

            int counter = 0;
            if (customer_flag.equals("5"))
            {
                counter = request_noti_id;
            }
            else if (Status.equalsIgnoreCase("msg"))
            {
                counter = message_noti_id;
            }
            else
            {
                counter = notificatn_counter;
            }

            notificationManager.notify(counter, not);
            //            notificationManager.notify(0, not);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        catch (Error e)
        {
            e.printStackTrace();
        }

    }

    private void refresh_Ride_Details(Context context)
    {
        Utills_G.rideDetailsBroadcastReceiver(context);
    }

    private void refresh_Driver_Tab(Context context)
    {
        Utills_G.refresh_DriverTab_BroadcastReceiver(context);
    }

    private void refresh_Rider_Tab(Context context)
    {
        Utills_G.refresh_RiderTab_BroadcastReceiver(context);
    }


    private void refresh_OfferRide(Context context)
    {
        Utills_G.refresh_OfferRide_BroadcastReceiver(context);
    }

}