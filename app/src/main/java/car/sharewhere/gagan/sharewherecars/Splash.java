package car.sharewhere.gagan.sharewherecars;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import car.sharewhere.gagan.utills.CommonUtilities;

public class Splash extends AppCompatActivity {

    String deviceToken;
    GoogleCloudMessaging gcm;
    SharedPreferences preferences;
    String countryCode = "";
    public  String address_current="";
    public  String lat, longitude;
    LocationManager locationManager;

    //Added
    SharedPreferences.Editor editor;
    String GCM_Reg_id="";


    boolean torecreate=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        preferences = PreferenceManager.getDefaultSharedPreferences(Splash.this);
        deviceToken = preferences.getString("deviceToken", null);//GCM Id

       torecreate=false;
      //Added
        editor = preferences.edit();


        Log.e("Splash device",""+deviceToken);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        countryCode = tm.getSimCountryIso();
        Log.e("Splash countryCode", "" + countryCode);
        if (countryCode.equals("")) {
            Log.e("Splash countryCode","enteres"+countryCode);


            countryCode = "IN";
        }
        GetCountryZipCode(countryCode);




        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        if (status == ConnectionResult.SUCCESS)
        {
            registerInBackground();
        }
        else
        {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, 0);
                 if (dialog != null)
                {
                //This dialog will help the user update to the latest GooglePlayServices
                dialog.show();
                            }
            return;
        }

Log.e("    MoVE 1111","===========");


    }

    /**
     * @CountryCode
     */
    private void GetCountryZipCode(String code)
    {

        String CountryID = "";
        Log.e("before code",""+code);

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();

        Log.e("GetCountryZipCode",""+CountryID);

        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++)
        {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim()))
            {
                code = g[0];
                Log.e("for loop code",""+code);
                break;
            }
        }
    }

    /**
     * registeration for GCM
     */
    private void registerInBackground() {




        new AsyncTask<Void,Void,String>(){
            @Override
            protected String doInBackground(Void... params) {

                String msg="";


                try{
                    if (gcm == null) {
                       gcm = GoogleCloudMessaging.getInstance(Splash.this);
//                        GCM_Reg_id = gcm.register(CommonUtilities.SENDER_ID);


                 }
                    msg = gcm.register(CommonUtilities.SENDER_ID);

                }catch(IOException e){
                   // e.toString();
                    msg="ERROR";
                }
                return msg;
            }


            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                if(s.isEmpty()||s.equalsIgnoreCase("ERROR")){
                    registerInBackground();

                    Log.e("registration id","registration if");
                }
                else {
                    Log.e("registration id","else");
                    editor.putString("deviceToken", s);
                    editor.commit();
                }

            }
        }.execute(null,null,null);
    }



    public String address(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(Splash.this, Locale.getDefault());
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

    public void turnGPSOn() {

        final Dialog dialog = new Dialog(Splash.this);

        dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.mobile_custom_verify);
        dialog.setCancelable(false);

        TextView text = (TextView) dialog.findViewById(R.id.text);
        Button ok = (Button) dialog.findViewById(R.id.ok);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        final EditText edt_mobile = (EditText) dialog.findViewById(R.id.text_mobile);
        edt_mobile.setVisibility(View.GONE);

        text.setText(("Enable GPS to get your location"));


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
             //   torecreate=true;
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                finish();
            }
        });

        dialog.show();
    }



    @Override
    protected void onResume() {

        super.onResume();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            turnGPSOn();
          //  return;
        }
        else {
            Log.e("    MoVE 2222","===========");
            android.os.Handler h = new android.os.Handler();
            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(Splash.this, Registeration.class));
                    finish();
                }
            }, 3000);
        }
    }
}
