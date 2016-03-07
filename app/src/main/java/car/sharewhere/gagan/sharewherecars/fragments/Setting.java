package car.sharewhere.gagan.sharewherecars.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

import car.sharewhere.gagan.sharewherecars.LicenceDummy;
import car.sharewhere.gagan.sharewherecars.R;

/**
 Created by ameba on 25/11/15. */
public class Setting extends FragmentG
{
    LinearLayout lnr_liscence, lnr_show_notificatn;
    SharedPreferences        preferences;
    SharedPreferences.Editor editor;
    SwitchCompat             switch_notification;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        View view = inflater.inflate(R.layout.seting, container, false);

        setActionBar(view, "Settings");
        findViewbyID(view);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

        switch_notification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {

                if (isChecked)
                {
                    preferences.edit().putBoolean("notification_on_off", true).apply();
                }
                else
                {
                    preferences.edit().putBoolean("notification_on_off", false).apply();
                }
            }
        });

        if (preferences.getBoolean("notification_on_off", true) == true)
        {
            switch_notification.setChecked(true);
        }
        else
        {
            switch_notification.setChecked(false);
        }

        return view;
    }

    private void findViewbyID(View view)
    {
        lnr_liscence = (LinearLayout) view.findViewById(R.id.licence_lnr);
        lnr_show_notificatn = (LinearLayout) view.findViewById(R.id.lnr_show_notificatn);
        switch_notification = (SwitchCompat) view.findViewById(R.id.switch_notification);

        lnr_liscence.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                GetLiscence();
            }
        });
    }

    private void GetLiscence()
    {
        Intent i = new Intent(getActivity(), LicenceDummy.class);
        startActivity(i);
    }

}
