package car.sharewhere.gagan.WebServices;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import car.sharewhere.gagan.Async_Thread.Super_AsyncTask;

/**
 * Created by ameba on 4/11/15.
 */
public class GlobalConstants {

    public static final String URL_REGISTER_LOGIN = "http://112.196.34.42:9091/Customer/";

    public static String REGISTER_CONSTANT = "SaveCustomer";

    public static String LOGIN_CONSTANT = "ValidateUserCustomer";

    public static  String OFFER_RIDE="http://112.196.34.42:9091/Trip/SaveTrip";

    public static String MOBILE_VERIFICATION="http://112.196.34.42:9091/Customer/ValidateMobileCode";
    public static String FIND_A_RIDE="http://112.196.34.42:9091/Trip/GetAllTrips";
    public static String GET_PROFILE_BYID="http://112.196.34.42:9091/Trip/GetTripByID?";
    public static String GET_PROFILE_BYID_CONSTANT_TRIPID="TripId=";
    public static String GET_PROFILE_BYID_CONSTANT_CUSTOMERID="&CustomerId=";


    public static final String GET_SERVICE_METHOD1 = "get1";
    public static final String GET_SERVICE_METHOD2 = "get2";
    public static final String POST_SERVICE_METHOD1 = "post1";
    public static final String POST_SERVICE_METHOD2 = "post2";
    public static final String POST_SERVICE_METHOD3 = "post3";


    public static final String MESSAGE = "Message";
    public static final String Name = "name";
    public static final String Leaving_From = "leaving_from";
    public static final String Leaving_To = "leaving_to";
    public static final String Leaving_Date = "leaving_date";
    public static final String Leaving_Time = "DepartureTime";
    public static final String Return_Time = "ReturnTime";
    public static final String Return_Date = "ReturnDate";
    public static final String Round_Trip = "RoundTrip";
    public static final String Image = "image";
    public static final String Trip_Id = "tripid";
    public static final String Mobile_Number = "mobile";
    public static final String Points = "points";
    public static final String Car_Info = "car_info";
    public static final String Is_Regular = "regular";
    public static final String Customer_ID = "CustomerId";
    public static final String Trip_Id_GCM = "trip_id_gcm";
    public static final String Flag = "flag";
    public static final String Regular_Days = "RegulerDays";
    public static final String CUstomer_ID_GCM = "customer_sender_id_gcm";
    public static final String Request_Type = "request_type";
    public static final String My_Rides_Constant = "my_rides";
    public static final String Search_Trip_passed_Constant = "trip_passed";
    public static final String Available_Seats = "available_seat";
    public static final String Rate_seat = "rate";
    public static final String Vehicle_number = "vehicle_number";
    public static final String GCM_FLAG = "flag";
    public static final String TRIP_Status = "trip_status";
    public static final String Constant_Rider = "rider";
    public static final String Constant_Driver = "driver";
    public static final String MODEL_NAME = "model_name";
    public static final String MODEL_IMAGE = "model_image";
    public static final String MODEL_NUMBER = "model_number";
    public static final String Search_TIME_pass = "time_pass";
    public static final String GCM_Lat = "gcm_lat";
    public static final String GCM_Long = "gcm_long";
    public static final String GCM_DriverId = "gcm_driver_id";
    public static final String GCM_Rider_ID = "gcm_rider_id";
    public static final String GCM_message = "gcm_message";
    public static final String GCM_REquestID = "requestid";
    public static String lat="0.0";
    public static  String longitude="0.0";

    public static Toast    t;

    public static void setListViewHeightBasedOnItems(ListView target_Listview, int limit) // LIMIT 0 FOR SHOWING ALLL CONTENTS
    {
        if (limit == 0) {
            ListAdapter listAdapter = target_Listview.getAdapter();
            if (listAdapter != null) {
                int numberOfItems = listAdapter.getCount();
                int totalItemsHeight = 0;
                for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                    View item = listAdapter.getView(itemPos, null, target_Listview);
                    item.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    totalItemsHeight += item.getMeasuredHeight();
                }

                int totalDividersHeight = target_Listview.getDividerHeight() * (numberOfItems - 1);

                ViewGroup.LayoutParams params = target_Listview.getLayoutParams();
                params.height = totalItemsHeight + totalDividersHeight;
                target_Listview.setLayoutParams(params);
                target_Listview.requestLayout();
            } else {

            }
        } else {
            ListAdapter listAdapter = target_Listview.getAdapter();
            if (listAdapter != null) {
                int numberOfItems = listAdapter.getCount();
                int totalItemsHeight = 0;
                for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                    if (itemPos < limit) {
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



    public static String latitude(double LATITUDE) {
        lat = String.valueOf(LATITUDE);
        return lat;
    }
    public static String longitude(double LONGITUDE) {
        longitude = String.valueOf(LONGITUDE);
        return longitude;
    }


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


   public enum KeyNames
    {
        fromWhere,Notification
    }
}