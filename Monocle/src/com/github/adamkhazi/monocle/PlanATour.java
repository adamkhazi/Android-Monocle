package com.github.adamkhazi.monocle;


import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class PlanATour extends FragmentActivity implements LocationListener, LocationSource {

	// Global constants
	private ListView lv;
	private Location mCurrentLocation;
	private GoogleMap map;
	OnLocationChangedListener mListener;
	LocationManager locationManager;
	
	//some coordinates for map markers 
	private static final LatLng LONDON_COORDINATES = new LatLng(51.5085300, -0.1257400);
	private static final LatLng BIG_BEN_COORDINATES = new LatLng(51.500922, -0.124545);
	private static final LatLng LONDON_EYE_COORDINATES = new LatLng(51.5033, -0.1197);
	private static final LatLng NATIONAL_HISTORY_MUSEUM_COORDINATES = new LatLng(51.4960, -0.1764);
	private static final LatLng OXFORD_STREET_COORDINATES = new LatLng(51.5136, -0.1556);
	private static final LatLng LONDON_BRIDGE_COORDINATES = new LatLng(51.5081, -0.0878);
	
	private static final LatLng BUCKINGHAM_PALACE_COORDINATES = new LatLng(51.5010, -0.1416);
	private static final LatLng CITY_HALL_COORDINATES = new LatLng(51.5047, -0.0783);
	private static final LatLng HUNGERFORD_BRIDGE_COORDINATES = new LatLng(51.5061, -0.1200);
	private static final LatLng LEICESTER_SQUARE_COORDINATES = new LatLng(51.5103, -0.1303);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan_a_tour);
		
		map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		CameraPosition cameraPosition = new CameraPosition.Builder().target(LONDON_COORDINATES).zoom(11).build();
		
		//positioning camera view in a reasonable position to see all markers, and zooming in
		map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setMyLocationEnabled(true);
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
		//Location services

		//LoadMarkers();

		if(locationManager != null) {
			boolean gpsIsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
			boolean networkIsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

			if(gpsIsEnabled) {
				locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10F, this);
			}
			else if(networkIsEnabled) {
				locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000L, 10F, this);
			}
			else
			{
				//add error message here for GPS not enabled
			}
		}
		else
		{
			//Show a generic error message
		}

		setUpMap();
    
		// -- List view starts here --
		lv = (ListView) findViewById(R.id.listView1);
		populateList(lv, map);
		
	}

	/**
	 * called when search bar is clicked 
	 * @param v
	 */
	public void searchBar(View view)
	{
		Log.i("search bar clicked ","search bar clicked ");
        Intent intent = new Intent(view.getContext(), SearchActivity.class);
		//carry bundle of data optionally
		startActivity(intent);
	}
	
	private void setUpMap() {
		// Do a null check to confirm that we have not already instantiated the map.
		if (map == null) {
			// Try to obtain the map from the SupportMapFragment.
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			map.setLocationSource(this);

			// Check if we were successful in obtaining the map.

			Log.v("inside setup map method", "inside setup map method"); 

			if (map != null) {
				Log.v("inside if map is null statement", "inside if map is null statement"); 

				map.setMyLocationEnabled(true);
				locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
				map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()

				{
					@Override
					public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
						marker.showInfoWindow();
						return true;
					}
				});
			}
			else
				Toast.makeText(getApplicationContext(), "Unable to create Maps", Toast.LENGTH_SHORT).show();
		}		
	}

	public void populateList(ListView lv, final GoogleMap map){
		// Instanciating an array list (you don't need to do this, 
		// you already have yours).
		List<String> rootCategoryList = new ArrayList<String>();
		rootCategoryList.add("Day Trip 1");
		rootCategoryList.add("Day Trip 2");
		rootCategoryList.add("Day Trip 3");
		rootCategoryList.add("Day Trip 4");
		rootCategoryList.add("Day Trip 5");
		rootCategoryList.add("Day Trip 6");
		rootCategoryList.add("Day Trip 7");

		// This is the array adapter, it takes the context of the activity as a 
		// first parameter, the type of list view as a second parameter and your 
		// array as a third parameter.
		//ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.mylist, rootCategoryList);

		FancyAdapter fancyAdapter = new FancyAdapter(rootCategoryList);
		lv.setAdapter(fancyAdapter); 
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
				Log.v("position in list clicked --", ""+pos); 
				//Here pos is the position of row clicked   
				if (pos ==0){
					map.clear();
					map.addMarker(new MarkerOptions().position(BIG_BEN_COORDINATES).title("Big Ben").icon(BitmapDescriptorFactory.fromResource(R.drawable.landmark_icon_1)));
					map.addMarker(new MarkerOptions().position(LONDON_EYE_COORDINATES).title("London Eye").icon(BitmapDescriptorFactory.fromResource(R.drawable.landmark_icon_1)));
					map.addMarker(new MarkerOptions().position(NATIONAL_HISTORY_MUSEUM_COORDINATES).title("National History Museum").icon(BitmapDescriptorFactory.fromResource(R.drawable.landmark_icon_1)));
					map.addMarker(new MarkerOptions().position(OXFORD_STREET_COORDINATES).title("Oxford Street").icon(BitmapDescriptorFactory.fromResource(R.drawable.landmark_icon_1)));
					map.addMarker(new MarkerOptions().position(LONDON_BRIDGE_COORDINATES).title("London Bridge").icon(BitmapDescriptorFactory.fromResource(R.drawable.landmark_icon_1)));
					
					PolylineOptions line = new PolylineOptions().add(BIG_BEN_COORDINATES).add(LONDON_EYE_COORDINATES).add(NATIONAL_HISTORY_MUSEUM_COORDINATES)
							.add(OXFORD_STREET_COORDINATES).add(LONDON_BRIDGE_COORDINATES).width(2).color(Color.LTGRAY);
					map.addPolyline(line);
				}
				else if (pos ==1){
					map.clear();
					map.addMarker(new MarkerOptions().position(BUCKINGHAM_PALACE_COORDINATES)
							.title("Buckingham Palace").icon(BitmapDescriptorFactory.fromResource(R.drawable.landmark_icon_1)));
					map.addMarker(new MarkerOptions().position(CITY_HALL_COORDINATES)
							.title("City Hall").icon(BitmapDescriptorFactory.fromResource(R.drawable.landmark_icon_1)));
					map.addMarker(new MarkerOptions().position(HUNGERFORD_BRIDGE_COORDINATES)
							.title("Hungerford Bridge").icon(BitmapDescriptorFactory.fromResource(R.drawable.landmark_icon_1)));
					map.addMarker(new MarkerOptions().position(LEICESTER_SQUARE_COORDINATES)
							.title("Leicester Square").icon(BitmapDescriptorFactory.fromResource(R.drawable.landmark_icon_1)));
					
					PolylineOptions line = new PolylineOptions().add(BUCKINGHAM_PALACE_COORDINATES)
							.add(CITY_HALL_COORDINATES).add(HUNGERFORD_BRIDGE_COORDINATES)
							.add(LEICESTER_SQUARE_COORDINATES).width(2).color(Color.LTGRAY);
					map.addPolyline(line);
				}else {
					Toast.makeText(view.getContext(),"Still working on it..", Toast.LENGTH_SHORT).show();
				}
			}
		});
	}
	
	//adapter used for list view
    private class FancyAdapter extends BaseAdapter {

        private String[] mData;

        public FancyAdapter(String[] data) {
            mData = data;
        }

        public FancyAdapter(List<String> rootCategoryList) {
			// TODO Auto-generated constructor stub
        	mData=new String[rootCategoryList.size()];
        	mData = rootCategoryList.toArray(mData);
		}

		@Override
        public int getCount() {
            return mData.length;
        }

        @Override
        public String getItem(int position) {
            return mData[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView result;

            if (convertView == null) { //list item in list view pointed to, from here
                result = (TextView) getLayoutInflater().inflate(R.layout.mylist, parent, false);
            } else {
                result = (TextView) convertView;
            }

            final String cheese = getItem(position);
            result.setText(cheese);

            return result;
        }

    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.plan_atour, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		Log.v("onStatusChanged", "onStatusChanged is run"); 

	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(this, "Provided", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(this, "Disabled!", Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onPause() {
		if(locationManager != null) {	    	
			locationManager.removeUpdates(this);
		}
		super.onPause();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		setUpMap();
		if(locationManager != null) {
			map.setMyLocationEnabled(true);
		}
	}
	
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
	}

	@Override
	public void deactivate() {
		mListener = null;
	}

	@Override
	public void onLocationChanged(Location location) {

	}
	
}





