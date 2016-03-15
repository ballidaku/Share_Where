package car.sharewhere.gagan.utills;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import car.sharewhere.gagan.sharewherecars.R;

/**
 Created by Gagan on 7/3/2015. */
public class Utills_G
{

    public static Toast toast;

    public static void showToast(String msg, Context context, boolean center)
    {
        if (toast != null)
        {
            toast.cancel();
        }
        toast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        if (center)
        {
            toast.setGravity(Gravity.CENTER, 0, 0);
        }

        toast.show();

    }

    public static Dialog global_dialog;

    public static String address(Context con, double LATITUDE, double LONGITUDE)
    {
        String   strAdd   = "";

        try
        {


            Geocoder geocoder = new Geocoder(con, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null)
            {
                Log.e("addresses in Util ", "" + addresses);

                Address returnedAddress = addresses.get(0);
//                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++)
                {
//                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");

                    strAdd = strAdd.isEmpty() ? returnedAddress.getAddressLine(i) :strAdd + ", " + returnedAddress.getAddressLine(i);




                }
//                strAdd = strReturnedAddress.toString();
                Log.e("urrentloctionaddress", "" + strAdd);
            }
            else
            {
                Log.e("Current loction address", "No Address returned!");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Log.w("Currentloctionaddress", "Canont get Address!");
        }
        return strAdd;
    }

    public void hide_keyboard(Context con)
    {
        try
        {
            InputMethodManager inputMethodManager = (InputMethodManager) con.getSystemService(con.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isAcceptingText())
            {
                inputMethodManager.hideSoftInputFromWindow(((Activity) con).getCurrentFocus().getWindowToken(), 0);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }



       /* if(con instanceof  Activity)
        {
            inputMethodManager.hideSoftInputFromWindow(((Activity) con).getCurrentFocus().getWindowToken(), 0);
        }
        else  if(con instanceof  Fragment)
        {
            inputMethodManager.hideSoftInputFromWindow(((Home) con).getActivity().getCurrentFocus().getWindowToken(), 0);
        }*/




       /* View view = con.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)con.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }*/
    }

    public static void cancelNotification(Context ctx, int notifyId)
    {
        String              ns   = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    //dialog onw button
    public static void show_dialog_msg(final Context con, final String text, View.OnClickListener onClickListener)
    {
        global_dialog = new Dialog(con, R.style.AppCompatAlertDialogStyle);
        global_dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        global_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        global_dialog.setContentView(R.layout.mobile_custom_verify);

        // global_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView tex     = (TextView) global_dialog.findViewById(R.id.text);
        EditText edt_nmr = (EditText) global_dialog.findViewById(R.id.text_mobile);
        Button   ok      = (Button) global_dialog.findViewById(R.id.ok);
        Button   cancel  = (Button) global_dialog.findViewById(R.id.cancel);

        tex.setText(text);
        edt_nmr.setVisibility(View.GONE);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(global_dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        global_dialog.show();
        global_dialog.getWindow().setAttributes(lp);

        if (onClickListener != null)
        {
            cancel.setVisibility(View.VISIBLE);
            // ok.setOnClickListener(oc);
            cancel.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    global_dialog.dismiss();

                }
            });

            ok.setOnClickListener(onClickListener);

        }
        else
        {
            ok.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    if (text.equals("You want to exit Share Where ?"))
                    {

                    }
                    global_dialog.dismiss();

                }
            });
        }

    }

    public static String dateToString(Date date, String format)
    {

        try
        {
            String strDate = "";
            SimpleDateFormat simpledateformat = new SimpleDateFormat(format);
            strDate = simpledateformat.format(date);
            return strDate;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    public void setRoundImage(Context con, ImageView imageView, String url)
    {

        Picasso.with(con).load(url).placeholder(R.mipmap.ic_default_pic_rounded) // optional
                  .error(R.mipmap.ic_default_pic_rounded).transform(new CircleTransform()).into(imageView);

    }

    public static String ridedetails = "com.ridedetails";

    public static void rideDetailsBroadcastReceiver(Context con)
    {

        Intent intent = new Intent(Utills_G.ridedetails);
        con.sendBroadcast(intent);
    }

    public static String refresh_chat = "com.refresh_chat";

    public static void refresh_chat_BroadcastReceiver(Context con)
    {

        Intent intent = new Intent(Utills_G.refresh_chat);
        con.sendBroadcast(intent);
    }

    public static String refresh_drivertab = "com.refresh_drivertab";

    public static void refresh_DriverTab_BroadcastReceiver(Context con)
    {

        Intent intent = new Intent(Utills_G.refresh_drivertab);
        con.sendBroadcast(intent);
    }

    public static String refresh_ridertab = "com.refresh_ridertab";

    public static void refresh_RiderTab_BroadcastReceiver(Context con)
    {

        Intent intent = new Intent(Utills_G.refresh_ridertab);
        con.sendBroadcast(intent);
    }



    public static String refresh_OfferRide = "com.refresh_offerride";

    public static void refresh_OfferRide_BroadcastReceiver(Context con)
    {

        Intent intent = new Intent(Utills_G.refresh_OfferRide);
        con.sendBroadcast(intent);
    }

}