package car.sharewhere.gagan.utills;


import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
 * Created by Gagan on 7/3/2015.
 */
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


    public static String address(Context con,double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(con, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("urrentloctionaddress", "" + strReturnedAddress.toString());
            } else {
                Log.w("Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("Currentloctionaddress", "Canont get Address!");
        }
        return strAdd;
    }




    //dialog onw button
    public static void show_dialog_msg(final Context con, final String text,
                                       View.OnClickListener onClickListener)
    {
        global_dialog = new Dialog(con,R.style.AppCompatAlertDialogStyle);
        global_dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        global_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        global_dialog.setContentView(R.layout.mobile_custom_verify);

       // global_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        TextView tex = (TextView) global_dialog.findViewById(R.id.text);
        EditText edt_nmr = (EditText) global_dialog.findViewById(R.id.text_mobile);
        Button ok = (Button) global_dialog.findViewById(R.id.ok);
        Button cancel = (Button) global_dialog.findViewById(R.id.cancel);


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
                    if(text.equals("You want to exit Share Where ?")){


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





}