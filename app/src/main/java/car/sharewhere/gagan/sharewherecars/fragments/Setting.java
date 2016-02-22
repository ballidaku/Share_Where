package car.sharewhere.gagan.sharewherecars.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
 * Created by ameba on 25/11/15.
 */
public class Setting extends FragmentG {
    LinearLayout lnr_liscence,lnr_show_notificatn;
    CheckBox chk_notificatn;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    String chkbox_notifctn_state;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.seting, container, false);

        setActionBar(view,"Settings");
        findViewbyID(view);

        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = preferences.edit();

         chkbox_notifctn_state = preferences.getString("notification", null);

        if (chkbox_notifctn_state == null) {
            chk_notificatn.setChecked(true);
        }
        else if (chkbox_notifctn_state.equals("no"))
        {
            chk_notificatn.setChecked(false);
        }
        else if (chkbox_notifctn_state.equals("yes"))
        {
            chk_notificatn.setChecked(true);
        }




        checkbox_pic_listner();

        return view;
    }

    /**
     * finding view by id
     */
    private void findViewbyID(View view) {
        lnr_liscence = (LinearLayout) view.findViewById(R.id.licence_lnr);
        lnr_show_notificatn = (LinearLayout) view.findViewById(R.id.lnr_show_notificatn);
        chk_notificatn = (CheckBox) view.findViewById(R.id.chk_notificatn);

       /* lnr_show_notificatn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getActivity(), NotificationActivity.class);
                startActivity(i);
            }
        });*/
        lnr_liscence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetLiscence();
            }
        });
    }


    private void GetLiscence() {
        Intent i = new Intent(getActivity(), LicenceDummy.class);
        startActivity(i);
    }

    /**
     * @CheckboxPic_Listner
     */
    private void checkbox_pic_listner() {
        chk_notificatn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (chk_notificatn.isChecked()) {
                    editor.putString("notification", "yes");
                    editor.commit();
                } else {
                    editor.putString("notification", "no");
                    editor.commit();
                }
            }
        });
        {
        }


    }

    @Override
    public void onResume() {
        super.onResume();

    }
}
