package car.sharewhere.gagan.sharewherecars.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import car.sharewhere.gagan.sharewherecars.R;

public class AboutUs extends FragmentG
{
TextView txt_abt;

    public AboutUs()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_about_us, container, false);

        InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        setActionBar(view, "About Us");

        findViewbyId(view);

        return view;
    }

              private void findViewbyId(View view){
                  txt_abt=(TextView)view.findViewById(R.id.txt_abt);
              }


    @Override
    public void onResume() {
        super.onResume();

    }
}
