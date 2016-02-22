package car.sharewhere.gagan.sharewherecars;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import car.sharewhere.gagan.GPS_Classes.GetCurrentLocation;
import car.sharewhere.gagan.GPS_Classes.Location_Notifier;
import car.sharewhere.gagan.GPS_Classes.ProgressDialogWoody;
import car.sharewhere.gagan.WebServices.Asnychronus_notifier;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.WebServices.Json_AsnycTask;
import car.sharewhere.gagan.utills.ConnectivityDetector;


public class Registeration extends FragmentActivity implements Asnychronus_notifier,Location_Notifier
{

    LoginButton btnLogin;
    ImageView fb_custom;
    CallbackManager callbackManager;

    HashMap<String, String> data_login = new HashMap<>();
    HashMap<String, String> data_registration = new HashMap<>();
    HashMap<String, String> data_mobile = new HashMap<>();
    String res_MobileNo, res_CustomerId, res_PhotoPath, res_FirstName,
            res_LastName, res_EmailID, res_MobileNoVerifyCode, res_emailverified, res_mobileverified,
            res_emailVerifyCode, get_email_id, photo, app_id, facebook_id,
            stringLatitude, stringLongitude, first_name, last_name, encoded, send_mobile_numbr;

    SharedPreferences preferences;
    ProgressBar progress;
    String mobile, countryCode;
    ConnectivityDetector cd;
    private CoordinatorLayout coordinatorLayout;
    Snackbar snackbar;
    String email_id = "";
    SharedPreferences.Editor editor;
    TelephonyManager         tm;
    ProfileTracker           profileTracker;
    String macAddress        = "";
    String DeviceSerialNo    = "";
    String mobileverify_code = "";
    private ProgressDialogWoody dailog;
    private GetCurrentLocation  google_location;
    //  Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        /**
         * Facebook initialisation
         */
        try
        {
            FacebookSdk.sdkInitialize(Registeration.this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        cd = new ConnectivityDetector(Registeration.this);
        dailog = new ProgressDialogWoody(Registeration.this);

        google_location = new GetCurrentLocation(Registeration.this);
        google_location.setOnResultsListener(this);
        if (cd.isConnectingToInternet() && google_location.mGoogleApiClient != null)
        {
            google_location.mGoogleApiClient.connect();
            dailog.show();
        }
        else
        {
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_LONG).show();
        }

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        setContentView(R.layout.activity_registeration);
        findviewbyID();

        /**
         * Methods Initialisation
         */
        FacebookSdk.setApplicationId("1049198945111554");

        getmac_Adres();

        /**
         * shared preferences
         */
        preferences = PreferenceManager.getDefaultSharedPreferences(Registeration.this);
        app_id = preferences.getString("deviceToken", null);
        isAlreadyLogin(isLoggedIn());
        editor = preferences.edit();
        //dialog = new Dialog(this);

        /**
         * Hiding soft keyboard
         */
        View view = this.getCurrentFocus();
        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        /**
         * fb button click
         */
        fb_custom.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                if (!cd.isConnectingToInternet())
                {
                    snackbar_method("No internet connection!");

                }
                else
                {
                    btnLogin.performClick();
                }
            }
        });

        /**
         * fetching lat/long GPS
         */

        /**
         * facebook permissions & callback manager
         */
        callbackManager = CallbackManager.Factory.create();
        btnLogin.setReadPermissions(Arrays.asList("email"));
        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {
                user_email_id(loginResult);
                Log.e("email", email_id);

            }

            @Override
            public void onCancel()
            {
                Log.e("OnCancel", "cancel");
                LoginManager.getInstance().logOut();

            }

            @Override
            public void onError(FacebookException exception)
            {
                snackbar_method(exception.toString());

            }
        });
    }





    private String getmac_Adres() {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddress = wInfo.getMacAddress();
        if (macAddress != null) {
            Log.e("mac adres=",macAddress);
            return macAddress;
        } else {
            return "";
        }

    }







    public boolean isLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        Log.e("Access Token",""+(accessToken));
        return accessToken != null;
    }


    /**
     * @FBEmailID
     */
    public void user_email_id(LoginResult loginResult) {
        try {
            GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
//email
                        if (object.has("email")) {

                            Log.e("Yam","yyyyyyyyyyyyyy");
                            email_id = object.getString("email");
                            Log.e("Yam","yyyyyyyyyy  "+email_id);

                        }
                        else if (object.has("id"))
                        {
                            email_id = object.getString("id");
                            Log.e("YamId","ididid  "+email_id);
                        }


                        if (!email_id.equals(""))
                        {
                            Log.e("Noprofile", "tracker");
                            get_first_last_name();
                        }
//                        else
//                        {
//                            Log.e("profile", "tracker");
//                            profile_tracker_method();
//                        }


                    } catch (Exception e) {
                        snackbar_method(e.toString());
                       /* data_login.put("EmailId", "");
                        data_registration.put("EmailId", "");*/
                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email,first_name,last_name,name,id");
            request.setParameters(parameters);
            request.executeAsync();
        } catch (Exception ex) {
            snackbar_method(ex.toString());

        }
    }


//    private void profile_tracker_method() {
//        profileTracker = new ProfileTracker() {
//            @Override
//            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
//                Log.e("AAAAAAAAAAAAAA", "Hello");
//
//                if (Profile.getCurrentProfile() != null) {
//// Log.e("currentProfile", "Hello" + currentProfile.getName() + "...." + currentProfile.getProfilePictureUri(100, 100) + "...." + currentProfile.getId());
//
//
//                    email_id = currentProfile.getId() + " ";
//
//
//                    first_name = currentProfile.getFirstName();
//                    last_name = currentProfile.getLastName();
//                    Log.e("onSuccess", "7" + first_name);
//                    Log.e("onSuccess", "8" + last_name);
//                    Log.e("onSuccess", "9" + currentProfile.getProfilePictureUri(500, 500).toString());
//
//
//                    get_profile_picture_tracker(currentProfile.getProfilePictureUri(100, 100));
//
//
//                } else {
//                    stop_fb();
//                }
//
//            }
//        };
//
//
//        profileTracker.startTracking();
//
//    }


//    public void stop_fb() {
//        Log.e("Logout", "Logout");
//
//        try {
//            if (profileTracker.isTracking()) {
//                profileTracker.stopTracking();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            AccessToken.setCurrentAccessToken(null);
//            Profile.setCurrentProfile(null);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }


    /**
     * @FbFirstname_Lastname
     */
    private void get_first_last_name()
    {
        Log.e("get_first","firstfirstlastlast");
        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null)
        {

            first_name = profile.getFirstName();
            last_name = profile.getLastName();

//            if (email_id.equals(""))
//            {
//
//                Log.e("email_id if","email_id");
//                email_id = profile.getId();
//
//                data_login.put("EmailId", email_id + "@gmail.com");
//                data_registration.put("EmailId", email_id + "@gmail.com");
//
//            }
         //   last_name = profile.getId();
//
         //   Log.e("email_id last_name",""+last_name);

            /******* get the profile picture in BASE64 form*****************/

            get_profile_picture(profile);

        }
        //Not required
        else
        {
            Log.e("get_first_last_name","ELSE");

            get_profile_picture(profile);
        }
    }


    /**
     * @FbimageBase64
     */



    public void get_profile_picture(final Profile profile) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                encoded = "";
                Bitmap mIcon1 = null;
                try {
                    URL img_value = null;
                    try {
                        img_value = new URL("" + profile.getProfilePictureUri(100, 100));
                        mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        mIcon1.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    } catch (MalformedURLException e) {
                        Log.e("Exception 1", e.toString());
                    } catch (IOException e) {
                        Log.e("Exception 2", e.toString());
                    }

                    return encoded;
                } catch (Exception e) {

                    return encoded;
                }
            }

            protected void onPostExecute(String result) {
                photo = result;
                calService();
            }
        }.execute();

    }


    public void get_profile_picture_tracker(final Uri profilePictureUri) {
        new AsyncTask<String, Void, String>() {
            @Override
            protected String doInBackground(String... params) {
                encoded = "";
                Bitmap mIcon1 = null;
                try {
                    URL img_value = null;
                    try {
                        img_value = new URL("" + profilePictureUri);
                        mIcon1 = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        mIcon1.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                        byte[] byteArray = byteArrayOutputStream.toByteArray();
                        encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    } catch (MalformedURLException e) {
                        Log.e("Exception 1", e.toString());
                    } catch (IOException e) {
                        Log.e("Exception 2", e.toString());
                    }

                    return encoded;
                } catch (Exception e) {

                   return encoded;
                }
            }

            protected void onPostExecute(String result) {
                photo = result;
                calService();

            }
        }.execute();

    }

    /**
     * @FbAcessToken
     */
    private void isAlreadyLogin(boolean currentAccessToken) {
        if (currentAccessToken) {


                Log.e("isAlreadyLogin",""+currentAccessToken);
                get_email_id = preferences.getString("email_id", null);
                get_email_id = preferences.getString("email_verify", null);
                String get_mobile_verify = preferences.getString("mobile_verify", null);
            Log.e("get_mobile_verify",""+get_mobile_verify);
                if (get_mobile_verify != null)
                {
                    if (get_mobile_verify.equals("true"))
                    {
                        Intent i = new Intent(Registeration.this, MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                    else
                    {
                        showdialog_codeValidatio();
                    }

                }
                else
                {
//                   showdialog_codeValidatio();
                }

        }
        else
        {
            Log.e("isAlreadyLogin",""+currentAccessToken);
            FacebookSdk.sdkInitialize(Registeration.this);
            LoginManager.getInstance().logOut();
        }

    }


    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * @HitServiceLogin
     */
    public void calService() {
        data_login.put("EmailId", email_id);


        data_login.put("PhotoPath", photo);
        mobile = preferences.getString("mobile_no", null);
        data_login.put("ApplicationId", app_id);
        data_login.put("DeviceType", "android");
        data_login.put("Flag", "facebook");
        data_login.put("DeviceSerialNo", getmac_Adres());

            Log.e("Service", "" + data_login);

        if (!cd.isConnectingToInternet())
        {
            snackbar_method("No internet connection!");

        }
        else
        {

            Log.e("Cal service Else",""+data_login);
            final Json_AsnycTask tsk = new Json_AsnycTask(Registeration.this,
                    GlobalConstants.URL_REGISTER_LOGIN + GlobalConstants.LOGIN_CONSTANT,
                    GlobalConstants.POST_SERVICE_METHOD1, data_login);
            tsk.setOnResultsListener(this);
            tsk.execute();

            progress.setVisibility(View.VISIBLE);
        }
    }


    /**
     * calling webservice for registeration
     */
    public void HitService_REgister() {

        data_registration.put("EmailId", email_id);
        data_registration.put("PhotoPath", photo);

        data_registration.put("FirstName", first_name);
        data_registration.put("LastName", last_name);
        data_registration.put("ApplicationId", app_id);
        data_registration.put("DeviceType", "android");
        data_registration.put("MobilePrefix", GetCountryZipCode());
        data_registration.put("MobileNo", send_mobile_numbr);
        data_registration.put("DeviceSerialNo", getmac_Adres());


        if (!cd.isConnectingToInternet()) {

            snackbar_method("No internet connection!");

        } else {
            Json_AsnycTask task = new Json_AsnycTask(Registeration.this,
                    GlobalConstants.URL_REGISTER_LOGIN + GlobalConstants.REGISTER_CONSTANT,
                    GlobalConstants.POST_SERVICE_METHOD2, data_registration);
            task.setOnResultsListener(this);
            task.execute();
            progress.setVisibility(View.VISIBLE);
        }
    }


    public void HitService_MobileVerify_Code() {

        String customerID = preferences.getString("CustomerId", null);
//        String mobileverify_code = preferences.getString("mobile_code", null);

        data_mobile.put("CustomerId", customerID);
        data_mobile.put("MobileVerifyCode", mobileverify_code);


        if (!cd.isConnectingToInternet()) {
            snackbar_method("No internet connection!");

        } else {
            Json_AsnycTask task = new Json_AsnycTask(Registeration.this,
                    GlobalConstants.MOBILE_VERIFICATION,
                    GlobalConstants.POST_SERVICE_METHOD3, data_mobile);
            task.setOnResultsListener(this);
            task.execute();
            progress.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResultsSucceeded_Get_Method1(JSONObject result) {
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onResultsSucceeded_Get_Method2(JSONObject result) {
        progress.setVisibility(View.GONE);

    }

    /**
     * @LoginResult
     */
    @Override
    public void onResultsSucceeded_Post_Method1(JSONObject result)
    {
        progress.setVisibility(View.GONE);
        Log.e("response 1", "Login==" + result);

        if (result != null)
        {
                  if (result.optString("Message").equals("Internal Server Error."))
                  {

                      FacebookSdk.sdkInitialize(Registeration.this);
                      LoginManager.getInstance().logOut();
                      snackbar_method("Try Again!! Internal server error");

                   }
                    else if (result.optString("Message").equals("Connection Timeout."))
                   {

                       FacebookSdk.sdkInitialize(Registeration.this);
                       LoginManager.getInstance().logOut();
                       snackbar_method("Try Again!! Connection Timeout.");
                   }
                 else if (result.optString("Message").contains("Email Id or Mobile"))
                 {

                     FacebookSdk.sdkInitialize(Registeration.this);
                     LoginManager.getInstance().logOut();
                     snackbar_method("Mobile number or email id is already in use with some other account, try different");
                 }
                else if (result.optString("Status").equals("success"))
                {

                    try {
                        JSONObject userDetails = result.getJSONObject("Message");
                        SharedPreferences.Editor editor = preferences.edit();

                        if (userDetails.has("CustomerId"))
                        {
                            res_CustomerId = userDetails.getString("CustomerId");
                            editor.putString("CustomerId", res_CustomerId);
                            editor.apply();
                        }
                        if (userDetails.has("PhotoPath"))
                        {
                            res_PhotoPath = userDetails.getString("PhotoPath");
                            editor.putString("photo_path", res_PhotoPath);
                            editor.apply();
                        }
                        if (userDetails.has("FirstName"))
                        {
                            res_FirstName = userDetails.getString("FirstName");
                            editor.putString("first_name", res_FirstName);
                            editor.apply();
                        }
                        if (userDetails.has("LastName"))
                        {
                            res_LastName = userDetails.getString("LastName");
                            editor.putString("last_name", res_LastName);
                            editor.apply();
                        }
                        if (userDetails.has("EmailID"))
                        {
                            res_EmailID = userDetails.getString("EmailID");
                            editor.putString("email_id", res_EmailID);
                            editor.apply();
                        }
                        if (userDetails.has("MobileVerifyCode"))
                        {
                            res_MobileNoVerifyCode = userDetails.getString("MobileVerifyCode");
                            editor.putString("mobile_code", res_MobileNoVerifyCode);
                            editor.apply();
                        }
                        if (userDetails.has("MobileNo"))
                        {
                            res_MobileNo = userDetails.getString("MobileNo");
                            editor.putString("mobile_no", res_MobileNo);
                            editor.apply();
                        }
                        if (userDetails.has("IsEmailVerified"))
                        {
                            res_emailverified = userDetails.getString("IsEmailVerified");
                            editor.putString("email_verify", res_emailverified);
                            editor.apply();
                        }
                        if (userDetails.has("IsMobileVerified"))
                        {
                            res_mobileverified = userDetails.getString("IsMobileVerified");
                            editor.putString("mobile_verify", res_mobileverified);
                            editor.apply();
                        }
                        if (userDetails.has("EmailVerifyCode"))
                        {
                            res_emailVerifyCode = userDetails.getString("EmailVerifyCode");
                            editor.putString("email_code", res_emailVerifyCode);
                            editor.apply();
                        }
                        if (res_mobileverified != null)
                        {
                            if (res_mobileverified.equals("true"))
                            {
                                Intent i = new Intent(Registeration.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                            else
                            {

                                Log.e("inner dialog","Inner");
                                showdialog_codeValidatio();
                            }
                        }
                        else
                        {
                            Log.e("outer dialog","Inner");
                            showdialog_codeValidatio();
                        }

//                    showdialog();

                    } catch (Exception ex)
                    {
                      //  snackbar_method("Try Again!! Internal server error");
Log.e("-----",""+ex.toString());
                        FacebookSdk.sdkInitialize(Registeration.this);
                        LoginManager.getInstance().logOut();
                    }

               }

            else
            {
                snackbar_method("User not registered, please wait...trying to register");
           Log.e("Register here","here");
                /*Register*/
                showdialog();
//                HitService_REgister();
            }

        }
        else
        {
            snackbar_method("User not registered, please wait...trying to register");
            Log.e("Register there", "there");
            /*Register*/
            showdialog();
//            HitService_REgister();
        }
    }

    /**
     * on post for registeration result handled here
     */




    @Override
    public void onResultsSucceeded_Post_Method2(JSONObject result) {
        progress.setVisibility(View.GONE);
        Log.e("response 2", "Register==" + result);
        if (result != null)
        {



            if (result.optString("Message").equals("Internal Server Error."))
            {
                FacebookSdk.sdkInitialize(Registeration.this);
                LoginManager.getInstance().logOut();
                snackbar_method("Try Again!! Internal server error");

            }
            else if (result.optString("Status").equals("success"))
            {
                try {
//                    Toast.makeText(Registeration.this,"Regidterd",Toast.LENGTH_LONG).show();
                    JSONObject userDetails = result.getJSONObject("Message");
                    SharedPreferences.Editor editor = preferences.edit();

                    if (userDetails.has("CustomerId"))
                    {
                        res_CustomerId = userDetails.getString("CustomerId");
                        editor.putString("CustomerId", res_CustomerId);
                        editor.apply();
                    }
                    if (userDetails.has("PhotoPath"))
                    {
                        res_PhotoPath = userDetails.getString("PhotoPath");
                        editor.putString("photo_path", res_PhotoPath);
                        editor.apply();
                    }
                    if (userDetails.has("FirstName")) {
                        res_FirstName = userDetails.getString("FirstName");
                        editor.putString("first_name", res_FirstName);
                        editor.apply();
                    }
                    if (userDetails.has("LastName"))
                    {
                        res_LastName = userDetails.getString("LastName");
                        editor.putString("last_name", res_LastName);
                        editor.apply();
                    }
                    if (userDetails.has("EmailID"))
                    {
                        res_EmailID = userDetails.getString("EmailID");
                        editor.putString("email_id", res_EmailID);
                        editor.apply();
                    }
                    if (userDetails.has("MobileVerifyCode")) {
                        res_MobileNoVerifyCode = userDetails.getString("MobileVerifyCode");
                        editor.putString("mobile_code", res_MobileNoVerifyCode);
                        editor.apply();
                    }
                    if (userDetails.has("EmailVerifyCode"))
                    {
                        res_emailVerifyCode = userDetails.getString("EmailVerifyCode");
                        editor.putString("email_code", res_emailVerifyCode);
                        editor.apply();
                    }
                    if (userDetails.has("MobileNo"))
                    {
                        res_MobileNo = userDetails.getString("MobileNo");
//                        editor.putString("mobile_no", res_MobileNo);
                        editor.apply();
                    }
                    if (userDetails.has("IsEmailVerified")) {
                        res_emailverified = userDetails.getString("IsEmailVerified");
                        editor.putString("email_verify", res_emailverified);
                        editor.apply();
                    }
                    if (userDetails.has("IsMobileVerified"))
                    {
                        res_mobileverified = userDetails.getString("IsMobileVerified");
                        editor.putString("mobile_verify", res_mobileverified);
                        editor.apply();
                    }
                    showdialog_codeValidatio();


                } catch (Exception ex) {
                    snackbar_method(ex.toString());

                    FacebookSdk.sdkInitialize(Registeration.this);
                    LoginManager.getInstance().logOut();
                }
            }
            else if(result.optString("Status").equalsIgnoreCase("error")){

                FacebookSdk.sdkInitialize(Registeration.this);
                LoginManager.getInstance().logOut();
                AccessToken.setCurrentAccessToken(null);
                  snackbar_method("Mobile number is already exists.");
            }
            else
            {
                FacebookSdk.sdkInitialize(Registeration.this);
                LoginManager.getInstance().logOut();

                snackbar_method("Not able to register");

            }
        }
        else //when result is null
        {
            FacebookSdk.sdkInitialize(Registeration.this);
            LoginManager.getInstance().logOut();

            snackbar_method("Not able to register");

        }
    }

    @Override
    public void onResultsSucceeded_Post_Method3(JSONObject result) {
        Log.e("response 3", "MobileVerify==" + result);
        progress.setVisibility(View.GONE);
        editor.putString("mobile_no", send_mobile_numbr);
        if (result != null) {
            if (result.optString("Status").equals("success")) {


//                        dialog.dismiss();

                try {
                    JSONObject userDetails = result.getJSONObject("Message");
                    if (userDetails.has("MobileNo")) {
                        String mob = userDetails.getString("MobileNo");
                        editor.putString("mobile_no", mob);
                        editor.apply();
                    }
                } catch (Exception ex) {
//                    snackbar_method(ex.toString());
                    Toast.makeText(Registeration.this,ex.toString(),Toast.LENGTH_LONG).show();
                }

                editor.putString("mobile_verify", "true");
                editor.commit();
                Intent i = new Intent(Registeration.this, MainActivity.class);
                startActivity(i);
                finish();
            }
            else if (result.optString("Status").equals("error"))
            {
//                snackbar_method("Please enter correct code.");
                Toast.makeText(Registeration.this,"Please enter correct code.",Toast.LENGTH_LONG).show();
            }
        }
        else
        {
            Toast.makeText(Registeration.this,"Internal server error",Toast.LENGTH_LONG).show();

//            snackbar_method("Internal server error");
        }


    }


    /**
     * @ViewByID
     */
    public void findviewbyID() {
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        btnLogin = (LoginButton) findViewById(R.id.loginbutton);
        //added
       btnLogin.setLoginBehavior(LoginBehavior.WEB_ONLY);


        fb_custom = (ImageView) findViewById(R.id.fb_custom);
        progress = (ProgressBar) findViewById(R.id.progressBar);

    }


    /**
     * @MobileVerification
     */
    public void showdialog() {
        final Dialog dialog = new Dialog(Registeration.this);

        dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.mobile_custom_verify);
        dialog.setCancelable(false);

        TextView text = (TextView) dialog.findViewById(R.id.text);
        Button ok = (Button) dialog.findViewById(R.id.ok);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);
        final EditText edt_mobile = (EditText) dialog.findViewById(R.id.text_mobile);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

       // dialog.show();
        dialog.getWindow().setAttributes(lp);

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (edt_mobile.getText().toString().length() > 0) {
                    String phone_number = edt_mobile.getText().toString();
                    if (phone_number.toString().length() == 10) {
                        send_mobile_numbr = edt_mobile.getText().toString();
                        HitService_REgister();
                        editor.putString("mobile_no", send_mobile_numbr);
                        editor.commit();
                        dialog.dismiss();

                    } else {
                        Toast.makeText(Registeration.this, "Enter 10 digit number", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Toast.makeText(Registeration.this, "Enter your 10 digit number", Toast.LENGTH_LONG).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Close facebook account
                FacebookSdk.sdkInitialize(Registeration.this);
                LoginManager.getInstance().logOut();
                AccessToken.setCurrentAccessToken(null);
                Profile.setCurrentProfile(null);
//Added
//                Intent intent = new Intent(Intent.ACTION_MAIN);
//                intent.addCategory(Intent.CATEGORY_HOME);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//
//               // Registeration.this.finish();
//                startActivity(intent);

                dialog.dismiss();
            }
        });

        dialog.show();
    }


    /**
     * @VerifyCode
     */
    public void showdialog_codeValidatio() {

       final Dialog dialog = new Dialog(Registeration.this);

        dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
           /* dialog.getWindow().addFlags(Window.FEATURE_NO_TITLE);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);*/
        dialog.setContentView(R.layout.mobile_custom_verify);
        dialog.setCancelable(false);

        TextView text = (TextView) dialog.findViewById(R.id.text);
        Button ok = (Button) dialog.findViewById(R.id.ok);
        Button cancel = (Button) dialog.findViewById(R.id.cancel);

        final EditText edt_mobile = (EditText) dialog.findViewById(R.id.text_mobile);
//        edt_mobile.setText(res_MobileNoVerifyCode);
       cancel.setVisibility(View.GONE);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        ok.setLayoutParams(params);
        ok.setGravity(Gravity.CENTER);
        String get_mobile_verify = preferences.getString("mobile_verify", null);
        text.setText(("Enter OTP to verify"));
        ok.setText("Verify your number");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_mobile.getText().toString().length() == 4) {
                    mobileverify_code=edt_mobile.getText().toString();
                    HitService_MobileVerify_Code();
                    dialog.dismiss();
                } else {
                    edt_mobile.setError("Enter valid OTP");
                }


            }
        });

        dialog.show();
    }


    /**
     * @CountryCode
     */
    private String GetCountryZipCode() {
        String CountryID = "";
        countryCode = tm.getSimCountryIso();
        if (countryCode.equals("")) {
            countryCode = "IN";
        }
        TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();
        if (CountryID.equals("")) {
            CountryID = "IN";
        }
        String[] rl = this.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                countryCode = "+" + g[0];
                break;
            }
        }
        return countryCode;
    }


    /**
     * @snackbarMethod
     */
    private void snackbar_method(String text) {
        snackbar = Snackbar
                .make(coordinatorLayout, text, Snackbar.LENGTH_LONG)
                .setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        snackbar.dismiss();
                    }
                });
        snackbar.setActionTextColor(Color.RED);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }

    @Override
    public void LOCATION_NOTIFIER(Location latlng) {

        dailog.hide();

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            stringLatitude = String.valueOf(latlng.getLatitude());
            stringLongitude = String.valueOf(latlng.getLongitude());

            data_login.put("Longitude", stringLongitude);
            data_login.put("Latitude", stringLatitude);

            data_registration.put("Longitude", stringLongitude);
            data_registration.put("Latitude", stringLatitude);
        }
        else {
            stringLatitude = "0";
            stringLongitude = "0";

            data_login.put("Longitude", stringLongitude);
            data_login.put("Latitude", stringLatitude);

            data_registration.put("Longitude", stringLongitude);
            data_registration.put("Latitude", stringLatitude);
        }

    }


}
