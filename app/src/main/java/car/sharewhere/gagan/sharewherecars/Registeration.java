package car.sharewhere.gagan.sharewherecars;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import car.sharewhere.gagan.GPS_Classes.ProgressDialogWoody;
import car.sharewhere.gagan.Location.GetCurrentLocation;
import car.sharewhere.gagan.Location.Location_Notifier;
import car.sharewhere.gagan.WebServices.Asnychronus_notifier;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.WebServices.Json_AsnycTask;
import car.sharewhere.gagan.utills.ConnectivityDetector;


public class Registeration extends FragmentActivity implements Asnychronus_notifier,Location_Notifier
{

    String symbol;
    LoginButton btnLogin;
    ImageView fb_custom;
    CallbackManager callbackManager;

    HashMap<String, String> data_login = new HashMap<>();
    HashMap<String, String> data_registration = new HashMap<>();
    HashMap<String, String> data_mobile = new HashMap<>();
    String res_MobileNo, res_CustomerId, res_PhotoPath, res_FirstName,
            res_LastName, res_EmailID, res_MobileNoVerifyCode, res_emailverified, res_mobileverified,
            res_emailVerifyCode, get_email_id, photo, app_id,
            stringLatitude, stringLongitude, first_name, last_name, encoded, send_mobile_numbr;

    SharedPreferences    preferences;
    ProgressBar          progress;
    String               mobile;
    ConnectivityDetector cd;
    private CoordinatorLayout coordinatorLayout;
    Snackbar snackbar;
    String email_id = "";
    SharedPreferences.Editor editor;
    TelephonyManager         tm;
    ProfileTracker           profileTracker;
    String macAddress        = "";
    String mobileverify_code = "";
    private ProgressDialogWoody dailog;
    private GetCurrentLocation  google_location;
    boolean changed_mobilenumber = false;

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

            }

            @Override
            public void onCancel()
            {
                Log.e("OnCancel","cancel");
               LoginManager.getInstance().logOut();
                AccessToken.setCurrentAccessToken(null);
                Profile.setCurrentProfile(null);


            }

            @Override
            public void onError(FacebookException exception) {
                snackbar_method(exception.toString());


            }
        });
    }





    private String getmac_Adres()
    {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddress = wInfo.getMacAddress();
        if (macAddress != null)
        {
            Log.e("mac adres=",macAddress);
            return macAddress;
        }
        else
        {
            Log.e("MAC ADDRESS ","MMMM "+macAddress);
            //return "";
            return  getmac_Adres();
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

                        if (object.has("email"))
                        {
                            email_id = object.getString("email");
                            Log.e("email_id","yyyyyyyyyy  "+email_id);
                            editor.putString("email_id", email_id);
                           editor.commit();

                        }
                        else if (object.has("id"))
                        {
                            email_id = object.getString("id");
                            editor.putString("email_id", email_id);
                            editor.commit();
                            Log.e("Id", "ididid  " + email_id);
                        }


                        if (!email_id.equals(""))
                        {
                            Log.e("get_first_last_name", "get_first_last_name");
                            get_first_last_name();
                        }

                    } catch (Exception e)
                    {
                        snackbar_method(e.toString());

                    }
                }
            });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "email,first_name,last_name,name,id");
            request.setParameters(parameters);
            request.executeAsync();
        }
        catch (Exception ex)
        {
            snackbar_method(ex.toString());

        }
    }

//======In case phot not fetched  using get_first_last_name()========
    private void profile_tracker_method() {
        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                Log.e("ProfileTracker", "======"+Profile.getCurrentProfile());

                if (Profile.getCurrentProfile() != null) {

                    Log.e("ProfileTracker", "======");
                    first_name=currentProfile.getFirstName();
                    last_name=currentProfile.getLastName();

                    editor.putString("first_name", first_name);
                    editor.putString("last_name", last_name);

                    editor.commit();
                    Log.e("onSuccess", "7" + first_name+"  "+preferences.getString("first_name", ""));
                    Log.e("onSuccess", "8" + last_name+"   "+preferences.getString("first_name", ""));
                    Log.e("onSuccess", "9" + currentProfile.getProfilePictureUri(500, 500).toString());


                    get_profile_picture_tracker(currentProfile.getProfilePictureUri(100, 100));


                }
                else
                {
                    Log.e("ProfileTracker", "ELSE");
                    stop_fb();
                }

            }
        };


        profileTracker.startTracking();

    }


    public void stop_fb() {

            snackbar_method("Please login again.");
        try {
            if (profileTracker.isTracking()) {
                profileTracker.stopTracking();
            }
            LoginManager.getInstance().logOut();
            AccessToken.setCurrentAccessToken(null);
            Profile.setCurrentProfile(null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }


    /**
     * @FbFirstname_Lastname
     */
    private void get_first_last_name()
    {

        boolean enableButtons = AccessToken.getCurrentAccessToken() != null;
        Profile profile = Profile.getCurrentProfile();
        if (enableButtons && profile != null)
        {

            first_name = profile.getFirstName();
            last_name = profile.getLastName();
            //because if phone number not registered then
            editor.putString("first_name", first_name);
            editor.putString("last_name", last_name);


            editor.commit();


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
        new AsyncTask<String, Void, URL>() {
            @Override
            protected URL doInBackground(String... params) {
                encoded = "";
                Bitmap mIcon1 = null;
                URL img_value = null;

                try {

                    Log.e("getProfile 1",""+profile.getProfilePictureUri(100, 100));

                        img_value = new URL("" + profile.getProfilePictureUri(100, 100));
                        Log.e("FirstMethod","FFFFFFF"+img_value);
                    

                }
                catch (Exception e) {

                  Log.e("Exception e"," "+e.toString());
                }
                return img_value;
            }


protected void onPostExecute(URL result) {

Log.e("result", " @@@!!!!!! " + result);
    if(result==null)
    {
        Log.e("resultNull", " null " + result);
        profile_tracker_method();
    }
    else
    {
        photo = result.toString().trim();
        if (photo.length() == 0)
        {

            Log.e("photo", "photo=======" + photo);
            profile_tracker_method();
        }
        else
        {

            editor.putString("photo_path", photo);
            editor.apply();
            calService();
        }
    }
            }
        }.execute();

    }


    public void get_profile_picture_tracker(final Uri profilePictureUri) {

        new AsyncTask<String, Void, URL>() {
            @Override
            protected URL doInBackground(String... params) {
                encoded = "";
                Bitmap mIcon1 = null;
                URL img_value = null;
                try {


                        img_value = new URL("" + profilePictureUri);
                        Log.e("SecondMethod","SSSSSSSS"+img_value);



                } catch (Exception e) {


                }
                return img_value;
            }

            protected void onPostExecute(URL result) {

                Log.e("URL SECond"," "+result);
                photo = result.toString().trim();
                editor.putString("photo_path", photo);
                editor.apply();
                calService();

            }
        }.execute();

    }

    /**
     * @FbAcessToken
     */
    private void isAlreadyLogin(boolean currentAccessToken) {
        if (currentAccessToken)
        {


                Log.e("isAlreadyLogin",""+currentAccessToken);
                get_email_id = preferences.getString("email_id", null);

                String get_mobile_verify = preferences.getString("mobile_verify", null);
            Log.e("get_mobile_verify","gggggggggggggggg       "+get_mobile_verify);
            Log.e("mobilenumber","mobilenumber       "+preferences.getString("mobile_no", null));
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
                    showdialog();

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

        //Login Service directly

        data_login.put("EmailId", email_id);

        //data_login.put("PhotoPath",photo);
        data_login.put("PhotoPath", preferences.getString("photo_path", ""));
        data_login.put("FirstName", preferences.getString("first_name", ""));
        data_login.put("LastName", preferences.getString("last_name", ""));
        mobile = preferences.getString("mobile_no", null);
        data_login.put("ApplicationId", app_id);
        data_login.put("DeviceType", "android");
        data_login.put("Flag", "facebook");
        data_login.put("DeviceSerialNo", getmac_Adres());

            Log.e("calService", "ssssssssss        " + data_login);

        if (!cd.isConnectingToInternet())
        {
            snackbar_method("No internet connection!");

        }
        else
        {


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

//preferences.getString("photo_path", "")
        Log.e("PhotoPAth",""+photo);

        Log.e("PhotoPAth",""+photo);
        Log.e("countryCode", "" + preferences.getString("country_code", ""));
        data_registration.put("EmailId", preferences.getString("email_id", ""));
//        data_registration.put("PhotoPath", photo);
        data_registration.put("PhotoPath", preferences.getString("photo_path", ""));
        data_registration.put("FirstName", preferences.getString("first_name", ""));
        data_registration.put("LastName", preferences.getString("last_name", ""));
        data_registration.put("ApplicationId", app_id);
        data_registration.put("DeviceType", "android");
//      data_registration.put("MobilePrefix", GetCountryZipCode());
        data_registration.put("MobilePrefix",preferences.getString("country_code", ""));
        data_registration.put("MobileNo", send_mobile_numbr);
        data_registration.put("DeviceSerialNo", getmac_Adres());


        if (!cd.isConnectingToInternet())
        {

            snackbar_method("No internet connection!");

        }
        else
        {
            Json_AsnycTask task = new Json_AsnycTask(Registeration.this,
                    GlobalConstants.URL_REGISTER_LOGIN + GlobalConstants.REGISTER_CONSTANT,
                    GlobalConstants.POST_SERVICE_METHOD2, data_registration);
            task.setOnResultsListener(this);
            task.execute();
            progress.setVisibility(View.VISIBLE);
        }
    }

    //======Resend OTP======

    HashMap<String,String> hashmapresend_otp=new HashMap<>();
     public void Resend_OTPService()
    {
    String customerID = preferences.getString("CustomerId", null);
    hashmapresend_otp.put("CustomerId", customerID);
   // hashmapresend_otp.put("MobileNo", preferences.getString("mobile_no",""));

    if (!cd.isConnectingToInternet())
    {
        snackbar_method("No internet connection!");

    }
    else
    {
        Json_AsnycTask task = new Json_AsnycTask(Registeration.this,
                GlobalConstants.RESEND_OTP,
                GlobalConstants.POST_SERVICE_METHOD4, hashmapresend_otp);
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


        if (!cd.isConnectingToInternet())
        {
            snackbar_method("No internet connection!");

        }
        else
        {

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
        Log.e("Postresponse 1", "Login==" + result);

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

                    }
                    catch (Exception ex)
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
              //RESPONSE WHEN NOT REGISTERED  {"Message":"Email Id not found.","Status":"error"}
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
        Log.e("Post Response 2", "Register==" + result);
//        if(dialog_.isShowing()&& dialog_!=null)
//        {
//
//            dialog_.dismiss();
//
//        }
        if (result != null)
        {



//            if (result.optString("Message").equals("Internal Server Error."))
//            {
//                FacebookSdk.sdkInitialize(Registeration.this);
//                LoginManager.getInstance().logOut();
//                snackbar_method("Try Again!! Internal server error");
//
//            }
//            else if (result.optString("Status").equals("success"))
            if (result.optString("Status").equals("success"))
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
                        editor.putString("mobile_no", res_MobileNo);
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
                    if(dialog_.isShowing()&& dialog_!=null)
                    {

                       dialog_.dismiss();
                    }
                    showdialog_codeValidatio();




                } catch (Exception ex) {
                    snackbar_method(ex.toString());

                    FacebookSdk.sdkInitialize(Registeration.this);
                    LoginManager.getInstance().logOut();
                }
            }
            else if(result.optString("Status").equalsIgnoreCase("error"))
            {
                Log.e("Error","ERROR ERROR");
                snackbar_method("Mobile number is already exists.");
                Toast.makeText(Registeration.this,"Mobile number is already exists.",Toast.LENGTH_LONG).show();
                edt_mobile.setText("");
                FacebookSdk.sdkInitialize(Registeration.this);
                LoginManager.getInstance().logOut();
                AccessToken.setCurrentAccessToken(null);

            }
//            if (result.optString("Message").equals("Internal Server Error."))
        //    else if (result.has("Message")&&!result.isNull("Message"))
            else if (result.has("Message"))
            {

                FacebookSdk.sdkInitialize(Registeration.this);
                LoginManager.getInstance().logOut();
                if(result.isNull("Message")){
                    snackbar_method("Please Try Again!! ");
                }
                else
                {
                    snackbar_method("Try Again!! " + result.optString("Message"));
                }

                if(dialog_.isShowing()&& dialog_!=null)
                {

                    dialog_.dismiss();
                }
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
        if (result != null)
        {
            if (result.optString("Status").equals("success"))
            {

                changed_mobilenumber=false;
dialog1.dismiss();

                try
                {
                    JSONObject userDetails = result.getJSONObject("Message");
                    if (userDetails.has("MobileNo"))
                    {
                        String mob = userDetails.getString("MobileNo");
                        editor.putString("mobile_no", mob);
                        editor.apply();
                    }
                }
                catch (Exception ex)
                {
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

    @Override
    public void onResultsSucceeded_Post_Method4(JSONObject result) {
        Log.e("response 4", "RESENDOTP==" + result);
        progress.setVisibility(View.GONE);
        if (result != null)
        {
            if (result.optString("Status").equals("success"))
            {
                snackbar_method("OTP sent successfully ");


            }
        }
        else
        {
            snackbar_method("Please try again");
        }


    }

    @Override
    public void onResultsSucceeded_Post_Method5(JSONObject result) {


        Log.e("RESPONSE %5","55555555555555"+result);
        progress.setVisibility(View.GONE);

        if(result!=null)
        {
            if (result.optString("Status").equals("success"))
            {

//                changed_mobilenumber=false;
                try {
                    JSONObject userDetails = result.getJSONObject("Message");
                    SharedPreferences.Editor editor = preferences.edit();

                    if (userDetails.has("MobileNo"))
                    {
                        res_MobileNo = userDetails.getString("MobileNo");
                        editor.putString("mobile_no", res_MobileNo);
                        editor.apply();
                    }

                    dialog_.dismiss();
                    showdialog_codeValidatio();
                }

                catch(Exception e)
                {
                    Log.e("EXCEPTION RESPONSE 5",""+e.toString());
                }
            }
            else if(result.optString("Status").equals("error"))
            {
               edt_mobile.setText("");
                snackbar_method("Mobile Number is already exists");
                Toast.makeText(Registeration.this,"Mobile Number is already exists",Toast.LENGTH_LONG).show();

            }
        }
        else
        {}
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
//======Changed Mobile Number============
     private void changeMobileNumber_Service()
     {

         String customerID = preferences.getString("CustomerId", null);

         data_login.put("CustomerId", customerID);
         data_login.put("MobileNo", send_mobile_numbr);

         Log.e("DATAAA", "" + data_login);

         if (!cd.isConnectingToInternet())
         {

             snackbar_method("No internet connection!");

         }
         else
         {
             Json_AsnycTask task = new Json_AsnycTask(Registeration.this,
                     GlobalConstants.CHANGE_MOBILE_NUMBER,
                     GlobalConstants.POST_SERVICE_METHOD5, data_login);
             task.setOnResultsListener(this);
             task.execute();
             progress.setVisibility(View.VISIBLE);
         }
     }


    //===================================

    /**
     * @MobileVerification
     */
   Dialog dialog_;
    EditText edt_mobile;
    public void showdialog() {
       // final Dialog dialog = new Dialog(Registeration.this);
        dialog_ = new Dialog(Registeration.this);

        dialog_.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_.setContentView(R.layout.mobile_custom_verify);
        dialog_.setCancelable(false);

        TextView text = (TextView) dialog_.findViewById(R.id.text);
        Button ok = (Button) dialog_.findViewById(R.id.ok);
        Button cancel = (Button) dialog_.findViewById(R.id.cancel);
//        final EditText edt_mobile = (EditText) dialog_.findViewById(R.id.text_mobile);
        edt_mobile = (EditText) dialog_.findViewById(R.id.text_mobile);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog_.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

       // dialog.show();
        dialog_.getWindow().setAttributes(lp);

        ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (edt_mobile.getText().toString().length() > 0)
                {
                    String phone_number = edt_mobile.getText().toString();

                    if (phone_number.toString().length() == 10)
                    {


                        send_mobile_numbr = edt_mobile.getText().toString();

//                        editor.putString("mobile_no", send_mobile_numbr);
//                        editor.commit();
                         if(changed_mobilenumber==true)
                         {


                             changeMobileNumber_Service();

                         }
                        else
                         {
                             HitService_REgister();
                           //  dialog_.dismiss();
                         }





                    }
                    else
                    {
                        Toast.makeText(Registeration.this, "Enter 10 digit number", Toast.LENGTH_LONG).show();
                    }

                }
                else
                {
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

                dialog_.dismiss();
            }
        });

        dialog_.show();
    }


    /**
     * @VerifyCode
     */
   Dialog dialog1 ;
    public void showdialog_codeValidatio() {

    //  final Dialog dialog1 = new Dialog(Registeration.this);
        dialog1 = new Dialog(Registeration.this);


        dialog1.getWindow().addFlags(Window.FEATURE_NO_TITLE);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);

        dialog1.setContentView(R.layout.resendeotp_layout);
        dialog1.setCancelable(false);

        TextView text = (TextView) dialog1.findViewById(R.id.tv_);
        Button btn_resendotp = (Button) dialog1.findViewById(R.id.btn_resend);
        Button btn_verifycode = (Button) dialog1.findViewById(R.id.btn_verify);
        Button btn_changenumber= (Button) dialog1.findViewById(R.id.btn_changeno);
        btn_changenumber.setVisibility(View.VISIBLE);
        final EditText edt_mobile = (EditText) dialog1.findViewById(R.id.ed_otp);


//        btn_resendotp.setText("Resnd OTP");
//        btn_changenumber.setText("Change Mobile Number");
//        btn_verifycode.setText("Verify  OTP");

        String get_mobile_verify = preferences.getString("mobile_verify", null);



        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        Log.e("DIALOG 1","&&&&&&&&&&&&");
        dialog1.show();
        dialog1.getWindow().setAttributes(lp);


        btn_resendotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Resend_OTPService();
                Log.e("DIALOG 2","@@@@@@@@@@@@@@@");

            }
        });

        btn_verifycode.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (edt_mobile.getText().toString().length() == 4)
                {
                    mobileverify_code = edt_mobile.getText().toString();
                    HitService_MobileVerify_Code();
                    //dialog1.dismiss();
                }
                else
                {
                    edt_mobile.setError("Enter valid OTP");
                }


            }
        });


        btn_changenumber.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                dialog1.dismiss();
                changed_mobilenumber=true;
                showdialog();
            }
        });


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
