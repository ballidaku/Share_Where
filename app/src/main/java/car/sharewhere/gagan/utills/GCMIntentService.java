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
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.sharewherecars.MainActivity;
import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.sharewherecars.Registeration;
import car.sharewhere.gagan.sharewherecars.Ride_Details;

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
    private static int    notificatn_counter   = 0;
    Bundle bun;
    boolean can_generate_notification = true;

    Context con;

    Chat_Database     database;
    SharedPreferences preferences;
    String            my_customerID;
    int message_noti_id=1000;
    long message_count=0;

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

        my_customerID = preferences.getString("CustomerId", null);

        can_generate_notification = true;

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


                String other_customer_id = customer_flag.equals("Rider") ? customer_driver_id : customer_rider_id;

                boolean is_chat_opened = preferences.getBoolean("is_chat_opened", false) && Chat_Activity.other_user_id.equalsIgnoreCase(other_customer_id) && Chat_Activity.TripId.equalsIgnoreCase(trip_id);

                if (is_chat_opened)
                {
                    map.put("message_status", "R");
                    database.save_message(map);

                    Utills_G.refresh_chat_BroadcastReceiver(context);
                    can_generate_notification = false;
                }
                else
                {
                    map.put("message_status", "UR");
                    database.save_message(map);
                }

                message_count= database.get_unread_messages_count("", "");
                if(message_count>1)
                {
                    customer_message= message_count +" unread messages";
                }
            }

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
    private  void generateNotification(Context context, String message)
    {
        int icon = R.mipmap.ic_launcher;

        try
        {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String title = "Share Where";

            if (customer_flag.equals("5"))  // New Request
            {
                message = customer_name + " requested to ride along with you.";
            }
            // 1 Accept

            // 2 Decline

            // 6 cancel by rider

            // 7 cancel by driver

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String get_email_id = preferences.getString("email_id", null);
            Intent notificationIntent = null;

            if (customer_flag.matches("1|2|5|6|7"))
            {
                Utills_G.rideDetailsBroadcastReceiver(context);

                notificationIntent = new Intent(context, Ride_Details.class);
                notificationIntent.putExtra(GlobalConstants.KeyNames.TripId.toString(), trip_id);
                notificationIntent.putExtra(GlobalConstants.KeyNames.CustomerId.toString(), customer_sender_id);
                notificationIntent.putExtra(GlobalConstants.KeyNames.fromWhere.toString(), GlobalConstants.KeyNames.Notification.toString());
            }
            else if (Status.equalsIgnoreCase("msg"))
            {
                /*notificationIntent = new Intent(context, Ride_Details.class);
                notificationIntent.putExtra(GlobalConstants.Trip_Id, trip_id);
                notificationIntent.putExtra(GlobalConstants.Customer_ID, customer_driver_id);
                notificationIntent.putExtra(GlobalConstants.KeyNames.fromWhere.toString(), GlobalConstants.KeyNames.Notification.toString());*/

                if(message_count>1)
                {
                    notificationIntent = new Intent(context,MainActivity.class );
                }
                else
                {
                    notificationIntent = new Intent(context,Chat_Activity.class );
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

            int counter=Status.equalsIgnoreCase("msg")? message_noti_id:notificatn_counter;

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

}