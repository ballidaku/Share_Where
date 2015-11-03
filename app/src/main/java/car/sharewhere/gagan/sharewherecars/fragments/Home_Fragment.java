package car.sharewhere.gagan.sharewherecars.fragments;


import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationChangeListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import car.sharewhere.gagan.sharewherecars.R;
import car.sharewhere.gagan.utills.GetCurrentLocation;

public class Home_Fragment extends FragmentG implements OnMyLocationChangeListener
{


    private GoogleMap googleMap;

    private static MapFragment mapFragment;


    private Bundle mBundle;
    private Location currentLocation;

    public Home_Fragment()
    {

    }

    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
//		mapFragment.onLowMemory();
    }


    private void onResumeFunction()
    {
        updateLocation();

    }


    @Override
    public void onResume()
    {
        super.onResume();
        if (mapFragment == null)
        {
            initializeMapFragment();
        }

        setUpMapIfNeeded();

    }

    @Override
    public void onPause()
    {
        super.onPause();
//		mapFragment.onPause();

    }


    @Override
    public void onDestroy()
    {
//		mapFragment.onDestroy();

        // responseHandler = null;
        super.onDestroy();
    }

    private static View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

//		View rootView = inflater.inflate(R.layout.activity_map_activity, container, false);
        if (rootView != null)
        {
            ViewGroup parent = (ViewGroup) rootView.getParent();
            if (parent != null)
                parent.removeView(rootView);
        }
        try
        {
            rootView = inflater.inflate(R.layout.activity_map_activity, container, false);
        }
        catch (Exception e)
        {
        /* map is already there, just return view as it is */
        }
        setActionBar(rootView,"ShareWhere");


        initializeMapFragment();

        return rootView;
    }





    private void initializeMapFragment()
    {
        try
        {
            mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        }
        catch (Exception | Error e)
        {
            e.printStackTrace();
            if (mapFragment == null)
            {
                mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
            }
        }

        if (mapFragment == null)
        {
            mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map);
        }
    }


    private void setUpMapIfNeeded()
    {
        if (googleMap == null)
        {
            if (mapFragment != null)
            {
                mapFragment.getMapAsync(new OnMapReadyCallback()
                {
                    @Override
                    public void onMapReady(GoogleMap gMap)
                    {
                        googleMap = gMap;
                        setUpMap();
                        onResumeFunction();
                    }
                });
            }
        }
        else
        {
            setUpMap();
            onResumeFunction();
        }


    }


    private void updateLocation()
    {
        try
        {
            if (GetCurrentLocation.mLastLocation != null)
            {
                currentLocation = GetCurrentLocation.mLastLocation;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    private void setUpMap()
    {

//		Marker markr = googleMap.addMarker(new MarkerOptions().position(markerLoc).draggable(false).title("").snippet("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
//		markr.showInfoWindow();

        if (googleMap != null)
        {
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationChangeListener(this);


            if (currentLocation == null)
            {
                updateLocation();
            }
            if (currentLocation != null)
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 12.0f));
            else
                updateLocation();


        }

    }


    @Override
    public void onMyLocationChange(Location location)
    {

        // CameraUpdate myLoc = CameraUpdateFactory.newCameraPosition(new
        // CameraPosition.Builder().target(new LatLng(location.getLatitude(),
        // location.getLongitude())).zoom(6).build());
        // googleMap.moveCamera(myLoc);
        // googleMap.setOnMyLocationChangeListener(null);

        // GetAddress(location.getLatitude(), location.getLongitude());
        // GetAddressByString("7 phase");
        // //CameraUpdateFactory.newLatLngZoom(markerLoc, 12.0f)//

        if (currentLocation == null)
        {
            currentLocation = location;
        }

    }





/*private String GEtLocationNameByLatLong(double latitude, double longitude)
    {
		String fnialAddress = "";
		geocoder = new Geocoder(getActivity(), Locale.getDefault());
		StringBuilder builder = new StringBuilder();
		try
		{
			List<Address> address = geocoder.getFromLocation(latitude, longitude, 1);
			int maxLines = address.get(0).getMaxAddressLineIndex();
			for (int i = 0; i < maxLines; i++)
			{
				String addressStr = address.get(0).getAddressLine(i);
				builder.append(addressStr);
				builder.append(" ");
			}

			fnialAddress = builder.toString(); // This is the complete address.
		} catch (IOException e)
		{
			return "Current address";
		} catch (NullPointerException e)
		{
			return "Current address";
		}
		return fnialAddress;
	}*/











	/*private List<Latlng_data> GetAddressByString(final String addressssss)
    {
		fetchingAddress = true;
		addressReturn = "";
		geocoder = new Geocoder(getActivity(), Locale.getDefault());

		dataList.clear();

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
				// addressReturn = addresses.get(0).getAddressLine(0);
				// addressReturn = addressReturn + "," +
				// addresses.get(0).getAddressLine(1);
				// addressReturn =
				// addressReturn+addresses.get(0).getAdminArea();
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

		} catch (Exception e)
		{
			dataList = getLocationFromString(addressssss);

		} catch (Error e)
		{
			dataList = getLocationFromString(addressssss);
		}

		fetchingAddress = false;

		return dataList;

	}*/

    // private static final String PLACES_API_BASE =
    // "https://maps.googleapis.com/maps/api/place";
    // private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    // private static final String OUT_JSON = "/json";
    //
    // //------------ make your specific key ------------
    // private static final String API_KEY =
    // "AIzaSyCt4Qmx6jNKMkyRoPVHeWlA_hNiRz0oF_c";
    // public static ArrayList<String> autocomplete(String input) {
    // ArrayList<String> resultList = null;
    //
    // HttpURLConnection conn = null;
    // StringBuilder jsonResults = new StringBuilder();
    // try {
    // StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE
    // + OUT_JSON);
    // sb.append("?key=" + API_KEY);
    // // sb.append("&components=country:gr");
    // sb.append("&input=" + URLEncoder.encode(input, "utf8"));
    //
    // URL url = new URL(sb.toString());
    //
    // System.out.println("URL: "+url);
    // conn = (HttpURLConnection) url.openConnection();
    // InputStreamReader in = new InputStreamReader(conn.getInputStream());
    //
    // // Load the results into a StringBuilder
    // int read;
    // char[] buff = new char[1024];
    // while ((read = in.read(buff)) != -1) {
    // jsonResults.append(buff, 0, read);
    // }
    // } catch (MalformedURLException e) {
    // Log.e("maps Search", "Error processing Places API URL", e);
    // return resultList;
    // } catch (IOException e) {
    // Log.e("maps Search", "Error connecting to Places API", e);
    // return resultList;
    // } finally {
    // if (conn != null) {
    // conn.disconnect();
    // }
    // }
    //
    // try {
    //
    // // Create a JSON object hierarchy from the results
    // JSONObject jsonObj = new JSONObject(jsonResults.toString());
    // JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");
    //
    // // Extract the Place descriptions from the results
    // resultList = new ArrayList<String>(predsJsonArray.length());
    // for (int i = 0; i < predsJsonArray.length(); i++) {
    // System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
    // System.out.println("============================================================");
    // resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
    // }
    // } catch (Exception e) {
    // Log.e("maps Search", "Cannot process JSON results", e);
    // }
    //
    // return resultList;
    // }




	/*class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable
	{
		private List<Latlng_data> resultList;

		public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId)
		{
			super(context, textViewResourceId);
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
			} catch (Exception e)
			{

			}
			return addressReturn;

		}

		@Override
		public Filter getFilter()
		{
			Filter filter = new Filter()
			{
				@Override
				protected FilterResults performFiltering(CharSequence constraint)
				{
					FilterResults filterResults = new FilterResults();
					if (constraint != null)
					{
						// Retrieve the autocomplete results.
						if (!fetchingAddress)
						{
							resultList = GetAddressByString(constraint.toString());
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
					} catch (Exception e)
					{
						// TODO: handle exception
					}
				}
			};
			return filter;
		}
	}*/

}
