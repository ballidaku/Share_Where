package car.sharewhere.gagan.sharewherecars.fragments;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.gms.location.places.AutocompleteFilter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import car.sharewhere.gagan.WebServices.GetCityHelperG_;
import car.sharewhere.gagan.sharewherecars.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Find_A_Ride extends FragmentG
{


    public Find_A_Ride()
    {
        // Required empty public constructor
    }


    private AutoCompleteTextView autocompleteTo, autocompleteFrom;

    private LinearLayout listVFindRide;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_find__a__ride, container, false);


        setActionBar(view,"Find a Ride");
        findViewbyID(view);


        return view;
    }


    private void findViewbyID(View view)
    {
        autocompleteTo = (AutoCompleteTextView) view.findViewById(R.id.autocompleteTo);
        autocompleteFrom = (AutoCompleteTextView) view.findViewById(R.id.autocompleteFrom);

        listVFindRide = (LinearLayout) view.findViewById(R.id.listVFindRide);


        autocompleteFrom.setThreshold(1);
        autocompleteTo.setThreshold(1);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
//                android.R.layout.simple_list_item_1, getCityArray(getResources().getString(R.string.cityList)));
//        listVFindRide.setAdapter(adapter);

        autocompleteFrom.setAdapter(new GetCityHelperG_(getActivity(), R.layout.layout_autocomplete));
        autocompleteTo.setAdapter(new GetCityHelperG_(getActivity(), R.layout.layout_autocomplete));

    }


    private String[] getCityArray(String json)
    {

        try
        {
            return json.substring(1, json.length() - 1).split(",");

        }
        catch (Exception e)
        {

            e.printStackTrace();
        }

        return null;
    }


}
