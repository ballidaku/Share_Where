package car.ameba.ridelele.sharewherecars.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import car.ameba.ridelele.Tabs.Driver_Rides_Tab;
import car.ameba.ridelele.Tabs.Rider_Rides_Tab;
import car.ameba.gagan.sharewherecars.R;

public class My_Rides extends FragmentG
{
    private TabLayout tabLayout;
    private ViewPager viewPager;
    Context           con;
    SharedPreferences preferences;

    public My_Rides()
    {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.home_new, container, false);
        con = getActivity();

        preferences = PreferenceManager.getDefaultSharedPreferences(con);

        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setActionBar(view, "My Rides");
        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();

        preferences.edit().putBoolean("is_myride_opened", true).apply();
    }

    @Override
    public void onPause()
    {
        super.onPause();
        preferences.edit().putBoolean("is_myride_opened", false).apply();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        preferences.edit().putBoolean("is_myride_opened", false).apply();
    }

    private void setupViewPager(ViewPager viewPager)
    {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new Driver_Rides_Tab(), "Rides as Driver");
        adapter.addFrag(new Rider_Rides_Tab(), "Rides as Rider");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList      = new ArrayList<>();
        private final List<String>   mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager)
        {
            super(manager);
        }

        @Override
        public Fragment getItem(int position)
        {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount()
        {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title)
        {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            return mFragmentTitleList.get(position);
        }
    }
}