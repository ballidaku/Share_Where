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

import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.sharewherecars.MainActivity;
import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.sharewherecars.Registeration;
import car.sharewhere.gagan.sharewherecars.Ride_Details;

/**
 Created by ameba on 10/11/15. */
public class GCMIntentService extends GCMBaseIntentService
{

    private static final String TAG                           = "GCMIntentService";
    private static       String trip_id                       = "";
    private static       String customer_name                 = "";
    private static       String customer_pic                  = "";
    private static       String customer_sender_id            = "";
    private static       String customer_flag                 = "";
    private static       String customer_seats                = "";
    private static       String customer_requestID            = "";
    private static       String customer_lat_long_req_or_send = "";
    private static       String customer_message              = "";
    private static       String customer_rider_id             = "";
    private static       String customer_driver_id            = "";
    private static       String customer_latitude             = "";
    private static       String customer_longitude            = "";
    private static       String customer_leavingfrom          = "";
    private static       String customer_leavingto            = "";
    private static       String customer_mobile               = "";
    private static       int    notificatn_counter            = 0;
    Bundle bun;

    Context con;

    private static int count = 0;

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

        if (GCMRegistrar.isRegisteredOnServer(context))
        {

        }
        else
        {

        }

    }

    @Override
    protected void onMessage(Context context, Intent intent)
    {

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
            customer_lat_long_req_or_send = jsonNoTi.optString("Status");
            customer_message = jsonNoTi.optString("Message");
            customer_latitude = jsonNoTi.optString("Latitude");
            customer_longitude = jsonNoTi.optString("Longitude");
            customer_leavingfrom = jsonNoTi.optString("LeavingFrom");
            customer_leavingto = jsonNoTi.optString("LeavingTo");
            customer_mobile = jsonNoTi.optString("CustomerMoboleNo");
            customer_driver_id = jsonNoTi.optString("DriverId");
            customer_rider_id = jsonNoTi.optString("RiderId");

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String chkbox_notifctn_state = preferences.getString("notification", null);
            //            Log.e("chkbox_notifctn_   ", "message received..." + chkbox_notifctn_state);
            if (chkbox_notifctn_state == null)
            {

                generateNotification(context, customer_message);
            }
            else if (chkbox_notifctn_state.equals("no"))
            {

            }
            else if (chkbox_notifctn_state.equals("yes"))
            {
                generateNotification(context, customer_message);
            }


          /*  final JSONObject jsonNoTi = new JSONObject(message);
            flag = jsonNoTi.getString("flag");
*/

        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.e("GCm", "" + e.toString());
        }
        catch (Error e)
        {
            e.printStackTrace();
            Log.e("GCm", "IIIII" + e.toString());
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
    private static void generateNotification(Context context, String message)
    {
        int icon = R.mipmap.ic_launcher;

        try
        {

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            String title = "Share Where";





            if (customer_flag.equals("5"))
            {
                message = customer_name + " requested to ride along with you.";
            }
            if (customer_flag.equals("1")) // Accept
            {
                //  message = customer_message;
            }
            if (customer_flag.equals("2")) //Decline
            {
                message = customer_name + " declined your request";
            }
            if (customer_flag.equals("6")) // cancel by rider
            {
                // message =  customer_name + " cancelled request";
            }
            if (customer_flag.equals("7")) // cancel by driver
            {
                //  message =  customer_message;
            }
            if (customer_flag.equals("Driver") || customer_flag.equals("Rider"))
            {

            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            String get_email_id = preferences.getString("email_id", null);
            Intent notificationIntent = null;

            if (customer_flag.matches("1|2|5|6|7"))
            {
                notificationIntent = new Intent(context, Ride_Details.class);
                notificationIntent.putExtra(GlobalConstants.Trip_Id, trip_id);
                notificationIntent.putExtra(GlobalConstants.Customer_ID, customer_sender_id);
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
            notificationIntent.putExtra("customer_status_lat_long", customer_lat_long_req_or_send);
            notificationIntent.putExtra("customer_driver_id", customer_driver_id);
            notificationIntent.putExtra("customer_rider_id", customer_rider_id);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT );

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
                      setStyle(style)
                      .setSound(defaultSound);


            builder.setNumber(notificatn_counter);

            if (!customer_flag.matches("1|2|5|6|7"))
            {
                builder.setOngoing(true);
            }


            Notification not = new NotificationCompat.BigTextStyle(builder).bigText(message).build();

            //            	not.number=count++;


            notificationManager.notify(notificatn_counter, not);
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