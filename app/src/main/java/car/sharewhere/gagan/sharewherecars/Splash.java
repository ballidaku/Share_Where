package car.sharewhere.gagan.sharewherecars;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import car.sharewhere.gagan.utills.CommonUtilities;
import car.sharewhere.gagan.utills.Dialogs;

public class Splash extends AppCompatActivity
{
//    String               symbol;
    GoogleCloudMessaging gcm;
    SharedPreferences    preferences;
//    String countryCode = "";
    public String lat, longitude;
    LocationManager locationManager;

    //Added
//    SharedPreferences.Editor editor;

    Dialogs dialogs = new Dialogs();
    Context con;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash);

        con = this;

        preferences = PreferenceManager.getDefaultSharedPreferences(Splash.this);
//        editor = preferences.edit();

    }

    /**
     registeration for GCM
     */
   /* private void registerInBackground()
    {

        new AsyncTask<Void, Void, String>()
        {
            @Override
            protected String doInBackground(Void... params)
            {

                String msg = "";

                try
                {
                    if (gcm == null)
                    {
                        gcm = GoogleCloudMessaging.getInstance(Splash.this);
                        //                        GCM_Reg_id = gcm.register(CommonUtilities.SENDER_ID);

                    }
                    msg = gcm.register(CommonUtilities.SENDER_ID);

                }
                catch (IOException e)
                {
                    // e.toString();
                    msg = "ERROR";
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String s)
            {
                super.onPostExecute(s);

                if (s.isEmpty() || s.equalsIgnoreCase("ERROR"))
                {
                    registerInBackground();

                    Log.e("registration id", "registration if");
                }
                else
                {
                    Log.e("registration id", "else");
                    editor.putString("deviceToken", s);
                    editor.apply();
                }

            }
        }.execute(null, null, null);
    }
*/


/*    private String GetCountryCode_CurrencySymbol()
    {
        String CountryID = "";

        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();
        if (CountryID.equals(""))
        {
            CountryID = "IN";
        }
        String[]              rl  = this.getResources().getStringArray(R.array.CountryCodes);
        Map<Currency, Locale> map = getCurrencyLocaleMap();
        for (int i = 0; i < rl.length; i++)
        {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim()))
            {
                countryCode = "+" + g[0];
                //===Currency Symbol====
                currency_Symbol(map, g[1].trim());
                break;
            }
        }
        return countryCode;
    }*/

/*    private void currency_Symbol(Map<Currency, Locale> map, String countryCode)
    {

        Locale   locale   = new Locale("EN", countryCode);
        Currency currency = Currency.getInstance(locale);
        // String symbol = currency.getSymbol(map.get(currency));

        try
        {
            if ((map.get(currency) == null) || (map.get(currency)).equals("null"))
            {
                Locale locale_ = new Locale(" ", countryCode);
                NumberFormat currency_ = NumberFormat.getCurrencyInstance(locale_);
                DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) currency_).getDecimalFormatSymbols();
                String currencySymbol = decimalFormatSymbols.getCurrencySymbol();
                symbol = currencySymbol;
            }
            else
            {
                symbol = currency.getSymbol(map.get(currency));
            }

            Log.e("For country", " " + countryCode + "  currency symbol is" + symbol);
        }
        catch (Exception e)
        {
            Log.e("Exception e", "" + e.toString());

        }

        //====Save Currency SYMBOL==============
        if (symbol.isEmpty() || symbol.equalsIgnoreCase("null") || symbol == null)
        {
            currency_Symbol(getCurrencyLocaleMap(), countryCode);

        }
        else
        {

            editor.putString("currency_symbol", symbol);
            editor.apply();
            Log.e("Symbol Else", "" + preferences.getString("currency_symbol", "null"));
        }

    }*/

/*    //==========
    //========Currency Symbol=======
    public static Map<Currency, Locale> getCurrencyLocaleMap()
    {
        Map<Currency, Locale> map = new HashMap<>();
        for (Locale locale : Locale.getAvailableLocales())
        {
            try
            {
                Currency currency = Currency.getInstance(locale);
                map.put(currency, locale);
            }
            catch (Exception e)
            {
                // skip strange locale
            }
        }
        return map;
    }*/
    @Override
    protected void onResume()
    {

        super.onResume();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            dialogs.turnGPSOn(con);
            //  return;
        }
        else
        {

     /*       countryCode = GetCountryCode_CurrencySymbol();
            editor.putString("country_code", countryCode);
            editor.apply();
            Log.e("country_code", "===========" + preferences.getString("country_code", ""));*/

            /*int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

            if (status == ConnectionResult.SUCCESS)
            {
//                registerInBackground();
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
*/
            android.os.Handler h = new android.os.Handler();
            h.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                   /* startActivity(new Intent(Splash.this, Registeration.class));
                    finish();*/

                    isAlreadyLogin();
                }
            }, 3000);
        }
    }

    private void isAlreadyLogin()
    {

        String get_mobile_verify = preferences.getString("mobile_verify", "");

        Log.e("get_mobile_verify", "..." + get_mobile_verify);
        Log.e("mobilenumber", "..." + preferences.getString("mobile_no", null));

        if (get_mobile_verify.equals("true"))
        {
            Intent i = new Intent(con, MainActivity.class);
            startActivity(i);
            finish();
        }
        else
        {
            Intent i = new Intent(con, Registeration.class);
            startActivity(i);
            finish();
        }

    }

}