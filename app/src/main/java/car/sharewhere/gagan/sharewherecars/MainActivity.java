package car.sharewhere.gagan.sharewherecars;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import car.sharewhere.gagan.sharewherecars.fragments.AboutUs;
import car.sharewhere.gagan.sharewherecars.fragments.Find_A_Ride;
import car.sharewhere.gagan.sharewherecars.fragments.Home_Fragment;
import car.sharewhere.gagan.sharewherecars.fragments.OfferA_a_Ride;
import car.sharewhere.gagan.utills.GetCurrentLocation;
import car.sharewhere.gagan.utills.TransitionHelper;
import car.sharewhere.gagan.utills.Utills_G;

public class MainActivity extends AppCompatActivity
{
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);


        int width = getResources().getDisplayMetrics().widthPixels / 2;


        DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) navigationView.getLayoutParams();
        params.width = width + 50;
        navigationView.setLayoutParams(params);

        setupDrawerContent(navigationView);
        displayView(R.id.nav_home);


        final View header = getLayoutInflater().inflate(R.layout.nav_header, navigationView, false);
        navigationView.addHeaderView(header);




        header.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                transitionToActivity(MainActivity.this,ProfileActivity.class,header.findViewById(R.id.imgProfilePic),header.findViewById(R.id.txtvUserName));
            }
        });

    }

//    GetCurrentLocation get_current_location;

    @Override
    protected void onStart()
    {
        super.onStart();
//        get_current_location.onStart();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupDrawerContent(NavigationView navigationView)
    {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener()
                {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem)
                    {

                        menuItem.setChecked(true);

                        mDrawerLayout.closeDrawers();
                        displayView(menuItem.getItemId());


                        return true;
                    }
                });
    }


    private void displayView(int id)
    {
        // update the main content by replacing fragments
        android.app.Fragment fragment = null;
        String title = "";

        switch (id)
        {

            case R.id.nav_home:
                fragment = new Home_Fragment();

                break;

            case R.id.nav_find_ride:
                fragment = new Find_A_Ride();

                title="Find a Ride";

                break;
            case R.id.nav_offer_ride:
                fragment = new OfferA_a_Ride();
                title="Offer a Ride";

                break;

            case R.id.nav_settings:
                fragment = new Home_Fragment();

                break;
            case R.id.nav_about:
                fragment = new AboutUs();
                break;
            case R.id.nav_logout:


                Utills_G.show_dialog_msg(MainActivity.this, "Do you want to Logout ?", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {


                        Utills_G.global_dialog.dismiss();


                        try
                        {
                            FacebookSdk.sdkInitialize(MainActivity.this);
                            LoginManager.getInstance().logOut();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        MainActivity.this.finish();


                    }
                });

                break;


            default:
                break;
        }

        if (fragment != null)
        {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();


        }

    }





    private void transitionToActivity(Activity activity, Class target, View profilePic, View profileName)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
        {
            final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                    new Pair<>(profilePic, activity.getString(R.string.profile_pic_transition)),
                    new Pair<>(profileName, activity.getString(R.string.profile_name_transition)));
            startActivityG(activity, target, pairs);
        }
        else
        {
            startActivity(new Intent(activity, target));
        }
    }


    private void startActivityG(Activity activity, Class target, Pair<View, String>[] pairs)
    {

        Intent i = new Intent(activity, target);
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);
        activity.startActivity(i, transitionActivityOptions.toBundle());

    }

}
