package car.sharewhere.gagan.WebServices;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import car.sharewhere.gagan.model.Latlng_data;

/**
 * Created by gagandeep on 14/10/15.
 */


public class GetCityHelperG_ extends ArrayAdapter<String> implements Filterable
{
    private List<Latlng_data> resultList = new ArrayList<>();
   private  Context con;

    public GetCityHelperG_(Context context, int textViewResourceId)
    {

        super(context, textViewResourceId);
        con = context;
    }

    @Override
    public int getCount()
    {
        return resultList.size();
    }

    @Override
    public String getItem(int index)
    {
        try
        {
            return resultList.get(index).getAddress();
        }
        catch (Exception e)
        {

        }
        return "";

    }

    @Override
    public Filter getFilter()
    {
        Filter filter = new Filter()
        {
            @Override
            protected FilterResults performFiltering(final CharSequence constraint)
            {
                FilterResults filterResults = new FilterResults();
                if (constraint != null)
                {
                    // Retrieve the autocomplete results.
                    if (!fetchingAddress)
                    {

//                        new AsyncTask<Void, Void, Void>()
//                        {
//
//                            @Override
//                            protected Void doInBackground(Void... voids)
//                            {
                                resultList = GetAddressByString(con, constraint.toString());
//                                return null;
//                            }
//                        }.execute();


                    }

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results)
            {
                try
                {

                    if (results != null && results.count > 0)
                    {
                        notifyDataSetChanged();
                    }
                    else
                    {
                        notifyDataSetInvalidated();
                    }
                }
                catch (Exception e)
                {
                    // TODO: handle exception
                }
            }
        };
        return filter;
    }


    boolean fetchingAddress = false;

    private List<Latlng_data> GetAddressByString(Context con, final String addressssss)
    {
        fetchingAddress = true;
        String addressReturn = "";
        Geocoder geocoder = new Geocoder(con, Locale.getDefault());

        List<Latlng_data> dataList = new ArrayList<>();


        List<Address> addresses;
        try
        {

            addresses = geocoder.getFromLocationName(addressssss, 3);

            Latlng_data l = null;

            for (int i = 0; i < addresses.size(); i++)
            {

                int getMAxAddrss = addresses.get(i).getMaxAddressLineIndex();

                for (int g = 0; g < getMAxAddrss; g++)
                {
                    addressReturn = addressReturn + "," + addresses.get(i).getAddressLine(g);
                }
                addressReturn = addressReturn.substring(1, addressReturn.length());
                // addrss[i] = addressReturn;
                l = new Latlng_data();
                l.setAddress(addressReturn);
                l.setLat(addresses.get(0).getLatitude());
                l.setLng(addresses.get(0).getLongitude());
                // locations_.add(addressReturn);
                addressReturn = "";

                dataList.add(l);
            }


            if (addresses.isEmpty())
            {
                dataList = getLocationFromString(addressssss);
            }


        }


        catch (Exception e)
        {
            dataList = getLocationFromString(addressssss);

        }
        catch (Error e)
        {
            dataList = getLocationFromString(addressssss);
        }
        fetchingAddress = false;


        return dataList;

    }


    // Directly access google map for location
    public List<Latlng_data> getLocationFromString(String address)
    {

        List<Latlng_data> Ldata = new ArrayList<Latlng_data>();

        try
        {
            String URL = "http://maps.google.com/maps/api/geocode/json?address=" + URLEncoder.encode(address, "UTF-8") + "&en&sensor=false";

            JSONObject jsonObject = new JSONObject(new WebServiceHelper().performGetCall(URL));

            JSONArray results = jsonObject.getJSONArray("results");

            Latlng_data l;
            for (int j = 0; j < results.length(); j++)
            {

                l = new Latlng_data();

                double lng = results.getJSONObject(j).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                double lat = results.getJSONObject(j).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                String addrsssssName = results.getJSONObject(j).getString("formatted_address");
                l.setAddress(addrsssssName != null ? addrsssssName : "");

                l.setLat(lng);
                l.setLng(lat);

                Ldata.add(l);
            }

        }
        catch (Exception e)
        {
            return Ldata;
        }
        catch (Error e)
        {
            return Ldata;
        }

        return Ldata;
    }


}