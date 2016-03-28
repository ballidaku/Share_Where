package car.ameba.ridelele.utills;

import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import car.ameba.ridelele.Async_Thread.Super_AsyncTask;
import car.ameba.ridelele.Chat.Chat_Database;

/**
 Created by ameba on 4/11/15. */
public class GlobalConstants
{
//   Local
//    public static final String Url = "http://112.196.34.42:9091/";


//    Live
    public static final String Url = "http://169.45.133.92:8032/";

    public static final String SENDER_ID = "469372194993";

    public static final String GET_SERVICE_METHOD1  = "get1";
    public static final String GET_SERVICE_METHOD2  = "get2";
    public static final String POST_SERVICE_METHOD1 = "post1";
    public static final String POST_SERVICE_METHOD2 = "post2";
    public static final String POST_SERVICE_METHOD3 = "post3";
    public static final String POST_SERVICE_METHOD4 = "post4";
    public static final String POST_SERVICE_METHOD5 = "post5";

    public static final String MESSAGE         = "Message";
    public static final String Name            = "name";
    public static final String Leaving_From    = "leaving_from";
    public static final String Leaving_To      = "leaving_to";
    public static final String Leaving_Date    = "leaving_date";
    public static final String Leaving_Time    = "DepartureTime";
    public static final String Return_Time     = "ReturnTime";
    public static final String Return_Date     = "ReturnDate";
    public static final String Round_Trip      = "RoundTrip";
    public static final String Image           = "image";
    public static final String Mobile_Number   = "mobile";
    public static final String Points          = "points";
    public static final String Is_Regular      = "regular";
    public static final String Flag            = "flag";
    public static final String Regular_Days    = "RegulerDays";
    public static final String Available_Seats = "available_seat";
    public static final String Rate_seat       = "rate";
    public static final String Vehicle_number  = "vehicle_number";
   /* public static String lat="0.0";
    public static  String longitude="0.0";*/

    public static Toast t;
    Chat_Database chat_database;

    public static void setListViewHeightBasedOnItems(ListView target_Listview, int limit) // LIMIT 0 FOR SHOWING ALLL CONTENTS
    {
        if (limit == 0)
        {
            ListAdapter listAdapter = target_Listview.getAdapter();
            if (listAdapter != null)
            {
                int numberOfItems = listAdapter.getCount();
                int totalItemsHeight = 0;
                for (int itemPos = 0; itemPos < numberOfItems; itemPos++)
                {
                    View item = listAdapter.getView(itemPos, null, target_Listview);
                    item.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    totalItemsHeight += item.getMeasuredHeight();
                }

                int totalDividersHeight = target_Listview.getDividerHeight() * (numberOfItems - 1);

                ViewGroup.LayoutParams params = target_Listview.getLayoutParams();
                params.height = totalItemsHeight + totalDividersHeight+100;
                target_Listview.setLayoutParams(params);
                target_Listview.requestLayout();
            }
        }
        else
        {
            ListAdapter listAdapter = target_Listview.getAdapter();
            if (listAdapter != null)
            {
                int numberOfItems = listAdapter.getCount();
                int totalItemsHeight = 0;
                for (int itemPos = 0; itemPos < numberOfItems; itemPos++)
                {
                    if (itemPos < limit)
                    {
                        View item = listAdapter.getView(itemPos, null, target_Listview);
                        item.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        totalItemsHeight += item.getMeasuredHeight();
                    }
                }
                int totalDividersHeight = target_Listview.getDividerHeight() * (numberOfItems - 1);
                ViewGroup.LayoutParams params = target_Listview.getLayoutParams();
                params.height = totalItemsHeight + totalDividersHeight;
                target_Listview.setLayoutParams(params);
                target_Listview.requestLayout();
            }
        }
    }



/*    public static String latitude(double LATITUDE) {
        lat = String.valueOf(LATITUDE);
        return lat;
    }
    public static String longitude(double LONGITUDE) {
        longitude = String.valueOf(LONGITUDE);
        return longitude;
    }*/

    public static void show_Toast(String text, Context con)
    {
        if (t != null)
        {
            t.cancel();
        }
        t = Toast.makeText(con, text, Toast.LENGTH_SHORT);

        t.show();
    }

    public static void execute(Super_AsyncTask asyncTask)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
        {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else
        {
            asyncTask.execute();
        }

    }


    public void logout(Context con)
    {

        /*chat_database=new Chat_Database(con);
        chat_database.delete_database(con);*/
        con.deleteDatabase("chat_database.db");



        // Clear all notification
        NotificationManager nMgr = (NotificationManager) con.getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

    }




    public enum KeyNames
    {
        fromWhere, Notification, CustomerName, CustomerPhoto, CustomerMobileNo, CustomerId, Flag, TripId, DriverId, RequestId, Driver, Rider, RiderId, Messages, Status
    }

}
