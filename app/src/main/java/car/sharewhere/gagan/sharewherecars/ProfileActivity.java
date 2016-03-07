package car.sharewhere.gagan.sharewherecars;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;

import car.sharewhere.gagan.WebServices.Asnychronus_notifier;
import car.sharewhere.gagan.WebServices.GlobalConstants;
import car.sharewhere.gagan.WebServices.Json_AsnycTask;
import car.sharewhere.gagan.utills.CircleTransform;
import car.sharewhere.gagan.utills.ConnectivityDetector;

public class ProfileActivity extends AppCompatActivity implements Asnychronus_notifier
{
    ImageView profile_img;
    EditText edt_aboutme,txt_name;
    LinearLayout layoutContainerAll;
    SharedPreferences preferences;
    String pref_firstname, pref_lastname, pref_mobile,pref_photopath,changed_photopath,customerid,
            res_CustomerId,res_CompanyID,res_UserId,res_PhotoPath,res_FirstName,res_LastName,res_MobileNo,
            res_carInfo,res_abtMe,pref_carinfo,pref_aboutme;

    Uri selectedImageUri;
    String  selectedPath;
    static int status=0;
    LinearLayout linear_car_name;
    TextView edt_car_name;
    FloatingActionButton fab;
    HashMap<String, String> data_edit_profile = new HashMap<>();
    ConnectivityDetector cd;
    Snackbar snackbar;
    private CoordinatorLayout coordinatorLayout;
    TextView txt_toolbar_done,txt_phone;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setActionBar();
        findViewbyID();


        /**
         * initialisations
         */
        cd = new ConnectivityDetector(ProfileActivity.this);
        preferences = PreferenceManager
                .getDefaultSharedPreferences(ProfileActivity.this);
        set_sharedpref_data();

        View view = this.getCurrentFocus();
        if(view!=null){
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }


    /**
     *shared prefrence set values
     */
    private void set_sharedpref_data(){
        pref_mobile = preferences.getString("mobile_no", null);
        pref_lastname=preferences.getString("last_name", null);
        pref_firstname=preferences.getString("first_name", null);
        pref_photopath=preferences.getString("photo_path", null);
        pref_aboutme=preferences.getString("about_me", null);
        pref_carinfo=preferences.getString("car_info", null);
        customerid=preferences.getString("CustomerId",null);

        if(pref_firstname!=null){
            String strng_nul="null";
            if(!pref_firstname.equals("")){
                if(!pref_firstname.equals(strng_nul)){
                    txt_name.setText(pref_firstname);
                }

            }
        }

        if(pref_mobile!=null){
            txt_phone.setText(pref_mobile);
        }

        if(pref_aboutme!=null){
            if(!pref_aboutme.equals("")){
                if( !pref_aboutme.equals("null") ){
                    if(!pref_aboutme.equals("'null'")){
                        edt_aboutme.setText(pref_aboutme);
                    }
                }
            }
        }
        if(selectedPath!=null){
            Picasso.with(ProfileActivity.this).load(new File(selectedPath)).skipMemoryCache().
                    transform(new CircleTransform()).error(R.mipmap.ic_default_pic_rounded).
                    into(profile_img);
        }
        else if(pref_photopath!=null){
            Picasso.with(this).load(pref_photopath).transform(new CircleTransform()).into(profile_img);
        }

        if(pref_carinfo!=null){
            edt_car_name.setText(pref_carinfo);
        }
    }



    /**
     *id's initialisations
     */
    private void findViewbyID()
    {
        layoutContainerAll = (LinearLayout) findViewById(R.id.layoutContainerAll);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        txt_name=(EditText)findViewById(R.id.txt_name);
        profile_img=(ImageView)findViewById(R.id.profile_img);
        txt_phone=(TextView)findViewById(R.id.txt_phone);
        linear_car_name=(LinearLayout)findViewById(R.id.lnr_car);
        edt_car_name=(TextView)findViewById(R.id.edt_car_name);
        edt_aboutme=(EditText)findViewById(R.id.edt_aboutme);
        coordinatorLayout=(CoordinatorLayout) findViewById(R.id.coordinatorLayout);


        /**
         * Edit button Click
         */
        fab.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                status=1;
                txt_toolbar_done.setVisibility(View.VISIBLE);
                edt_aboutme.isInEditMode();
                edt_aboutme.setFocusableInTouchMode(true);
                edt_car_name.setFocusableInTouchMode(true);
                txt_name.setFocusableInTouchMode(true);
                txt_name.requestFocus();
                txt_name.setSelection(txt_name.getText().toString().length());
                linear_car_name.setEnabled(true);
                profile_img.setEnabled(true);
            }
        });


        /**
         * Save profile data
         */
        txt_toolbar_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_name.getText().toString().trim().length() == 0) {
                    Toast.makeText(ProfileActivity.this, "Enter your name", Toast.LENGTH_SHORT).show();
                    txt_name.requestFocus();
                    txt_name.setHint("Enter name");
                    txt_name.setSelection(txt_name.getText().toString().length());
                } else {
                    callservice_editProfile();
                    status = 0;
                    txt_toolbar_done.setVisibility(View.INVISIBLE);
                    edt_aboutme.setFocusable(false);
                    edt_car_name.setFocusable(false);
                    txt_name.setFocusable(false);
                    profile_img.setEnabled(false);
                    edt_car_name.setEnabled(false);
                    linear_car_name.setEnabled(false);
                }

            }
        });


        /**
         * select image
         */
        profile_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery(10);
            }
        });

    }



    /**
     * onResume
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(status==0){
            txt_toolbar_done.setVisibility(View.INVISIBLE);
            edt_aboutme.setFocusable(false);
            edt_car_name.setFocusable(false);
            txt_name.setFocusable(false);
            profile_img.setEnabled(false);
            edt_car_name.setEnabled(false);
            linear_car_name.setEnabled(false);
        }
        else{
            txt_toolbar_done.setVisibility(View.VISIBLE);
            edt_aboutme.isInEditMode();
            edt_aboutme.setFocusableInTouchMode(true);
            edt_car_name.setFocusableInTouchMode(true);
            txt_name.setFocusableInTouchMode(true);
            profile_img.setEnabled(true);
            linear_car_name.setEnabled(true);
        }
        set_sharedpref_data();
    }


    /**
     * ActionBar Setup
     */
    private void setActionBar()
    {
        try
        {
            final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            TextView txt_toolbar=(TextView)toolbar.findViewById(R.id.txt_titleTV);
            txt_toolbar_done=(TextView)toolbar.findViewById(R.id.txt_done);
            txt_toolbar_done.setGravity(Gravity.CENTER );
            txt_toolbar.setText("User Profile");
            android.support.v7.app.ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN)
                {
                    supportFinishAfterTransition();
                }
                else
                {
                    ProfileActivity.this.finish();
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }




    /**
     * select image from gallery
     */
    public void openGallery(int req_code){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, req_code);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
        {
            if(data.getData() != null)
            {
                selectedImageUri = data.getData();
                Log.e("IMage1",""+selectedImageUri);
            }
            else
            {
                snackbar_method("failed to get Image!");

            }

            if (requestCode == 100 && resultCode == RESULT_OK)
            {
                Log.e("IMage  2","22222222");
                selectedPath = getPath(selectedImageUri);
                Picasso.with(ProfileActivity.this).load(new File(selectedPath)).skipMemoryCache().
                        transform(new CircleTransform()).error(R.mipmap.ic_default_pic_rounded).
                        into(profile_img);
                base64(selectedPath);
                Log.e("selectedPath1 : " ,selectedPath);

            }

            if (requestCode == 10)
            {

                Log.e("IMage  3", "333333");
                selectedPath = getPath(selectedImageUri);
                Log.e("IMage  4", "4444");

                profile_img.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
                Log.e("IMage  5", "555");

                Picasso.with(ProfileActivity.this).load(selectedPath).skipMemoryCache()
                        .error(R.mipmap.ic_default_pic_rounded)
                        .transform(new CircleTransform()).into(profile_img);
                Log.e("IMage  6", "666");
                //changed_photopath=base64(selectedPath);
                base64_async(selectedPath);
                Log.e("selectedPath1 : ", selectedPath);
            }
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);

    }

    /**
     *converting to Base64
     */
    private void base64_async(final String path){

        new AsyncTask<String, Void, String>() {
//            String path=path;
            @Override
            protected String doInBackground(String... params) {
                File imageFile = new File(path);
                Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
                byte[] image = stream.toByteArray();
                String img_str = Base64.encodeToString(image, 0);
                changed_photopath=img_str;

                Log.e("Asynctask", "666"+changed_photopath);
                return img_str;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
        }.execute();

    }
    public String base64(String path){
        File imageFile = new File(path);
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
        byte[] image = stream.toByteArray();
        String img_str = Base64.encodeToString(image, 0);
        changed_photopath=img_str;
        return img_str;
    }




    /***
     * Hit Service to Update Profile
     */
    public void callservice_editProfile() {
        data_edit_profile.put("DeviceType", "android");
//        data_edit_profile.put("MobileNo", txt_phone.getText().toString());
        data_edit_profile.put("FirstName", txt_name.getText().toString());
//        data_edit_profile.put("CarInfo", null);
        data_edit_profile.put("AboutMe", edt_aboutme.getText().toString());
        data_edit_profile.put("CustomerId", customerid);
        if(changed_photopath!=null){
            data_edit_profile.put("PhotoPath", changed_photopath);
        }
        else{
            data_edit_profile.put("PhotoPath", pref_photopath);
        }


        if (!cd.isConnectingToInternet()) {
            snackbar_method("No internet connection!");

        }else{
            final Json_AsnycTask tsk = new Json_AsnycTask(ProfileActivity.this,
                    "http://112.196.34.42:9091/Customer/UpdateProfile",
                    GlobalConstants.POST_SERVICE_METHOD1, data_edit_profile);
            tsk.setOnResultsListener(this);
            tsk.execute();
            dialog = ProgressDialog.show(ProfileActivity.this, "",
                    "Updating. Please wait...", true);
            dialog.setCancelable(true);
            dialog.show();
        }
    }
    @Override
    public void onResultsSucceeded_Get_Method1(JSONObject result) {
    }

    @Override
    public void onResultsSucceeded_Get_Method2(JSONObject result) {

    }
    /**
     * on post for login result handled here
     */
    @Override
    public void onResultsSucceeded_Post_Method1(JSONObject result) {
        dialog.dismiss();
        Log.e("response 1",  "Login==" + result);
        if(result!=null){
            if(result.optString("Message").equals("Internal Server Error.")) {
                snackbar_method("Try Again!! Internal server error");

            }
            else if(result.optString("Status").equals("error")){
                snackbar_method("Try Again!! Internal server error");
            }
            else
            if (result.optString("Status").equals("success"))
            {
                txt_toolbar_done.setVisibility(View.INVISIBLE);
                edt_aboutme.setFocusable(false);
                edt_car_name.setFocusable(false);
//                txt_phone.setFocusable(false);
                txt_name.setFocusable(false);
                profile_img.setEnabled(false);
                edt_car_name.setEnabled(false);
                linear_car_name.setEnabled(false);

                try
                {
                    JSONObject userDetails = result.getJSONObject("Message");
                    SharedPreferences.Editor editor = preferences.edit();
                    if (userDetails.has("CustomerId")) {
                        res_CustomerId = userDetails.getString("CustomerId");
                    }
                    if (userDetails.has("CompanyID")) {
                        res_CompanyID = userDetails.getString("CompanyID");
                    }
                    if (userDetails.has("UserId")) {
                        res_UserId = userDetails.getString("UserId");
                    }
                    if (userDetails.has("PhotoPath")) {
                        res_PhotoPath = userDetails.getString("PhotoPath");
                        editor.putString("photo_path", res_PhotoPath);
                        editor.apply();
                        Picasso.with(this).load(res_PhotoPath).
                                transform(new CircleTransform()).into(MainActivity.img_header_profile);

                    }
                    if (userDetails.has("FirstName")) {
                        res_FirstName = userDetails.getString("FirstName");
                        editor.putString("first_name", res_FirstName);
                        editor.apply();
                        MainActivity.txt_header_name.setText(res_FirstName);
                    }
                    if (userDetails.has("LastName")) {
                        res_LastName = userDetails.getString("LastName");
                        editor.putString("last_name", res_LastName);
                        editor.apply();
                    }

                    if (userDetails.has("MobileNo")) {
                        res_MobileNo = userDetails.getString("MobileNo");

                    }
                    if (userDetails.has("CarInfo")) {
                        res_carInfo = userDetails.getString("CarInfo");

                    }
                    if (userDetails.has("AboutMe")) {
                        res_abtMe = userDetails.getString("AboutMe");
                        editor.putString("about_me", res_abtMe);
                        editor.apply();
                    }
                    /*Picasso.with(this).load(pref_photopath).transform(new CircleTransform()).
                            into(MainActivity.img_header_profile);*/
                    set_sharedpref_data();
                    snackbar_method("Profile Successfully updated");

                }
                catch (Exception ex){
                    snackbar_method(ex.toString());
                }

            } else {
                snackbar_method("Sorry not able to update profile, try again");
            }

        } else {
            snackbar_method("Sorry not able to update profile, try again");

        }

    }

    @Override
    public void onResultsSucceeded_Post_Method2(JSONObject result) {

    }

    @Override
    public void onResultsSucceeded_Post_Method3(JSONObject result) {

    }

    @Override
    public void onResultsSucceeded_Post_Method4(JSONObject result)
    {

    }

    @Override
    public void onResultsSucceeded_Post_Method5(JSONObject result)
    {

    }

    /**
     * @snackbarMethod
     */
    private void snackbar_method(String text){
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
}

