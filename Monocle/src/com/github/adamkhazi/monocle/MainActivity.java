package com.github.adamkhazi.monocle;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends FragmentActivity implements LocationListener, LocationSource {
	
	LocationManager locationManager;
	private static final LatLng LONDON_COORDINATES = new LatLng(51.5085300, -0.1257400);
	
	//Autocomplete Search bar
	public static AutoCompleteTextView matv;
	public static ArrayAdapter<Landmark> suggestionAdapter = null;
	private List<Landmark> landmarkSuggestions = new ArrayList<Landmark>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		configureMap(((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mainMenuMap)).getMap());
		locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		
	   matv = (AutoCompleteTextView) findViewById(R.id.mainmenusearchfield);
	   
	   
	   //text changed action
	   matv.addTextChangedListener(new TextWatcher() {

		   @Override
		   public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
			   // When user changes text 
			  String searchQuery = matv.getText().toString();
			  downloadLandmarks(searchQuery);
			  
			  
		   }

		   @Override
		   public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			   // TODO Auto-generated method stub

		   }

		   @Override
		   public void afterTextChanged(Editable arg0) {
			   // TODO Auto-generated method stub    
			  
		   }
	   });
		   
	   //item selected action
	   matv.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected (AdapterView<?> parent, View view, int pos, long id) {
		    	 Landmark landmarkChosen = (Landmark) parent.getAdapter().getItem(pos);
           	  	 Intent myIntent = new Intent(view.getContext(), ViewLandmark.class);
                 myIntent.putExtra(Browse.EXTRA_MESSAGE, landmarkChosen.getId());
                 startActivityForResult(myIntent, 0);
		    }

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
	   
	   suggestionAdapter = new ArrayAdapter<Landmark>(this,
               android.R.layout.simple_dropdown_item_1line, landmarkSuggestions);
	   suggestionAdapter.setNotifyOnChange(true);
	   matv.setAdapter(suggestionAdapter);
	   
	   
	   
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu; this adds items to the action bar if it is present.
	    getMenuInflater().inflate(R.menu.main, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		switch (item.getItemId()) {
        case R.id.action_settings:
        	Log.i("settings clicked menu item clicked ", "click-- ");
            Intent intent = new Intent(this, SettingsActivity.class);
    		//carry bundle of data optionally
    		startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
		}
	}
	
	/**
	 * called when planATour button is pressed
	 * @param view
	 */
	public void planATour(View view){
		Intent intent = new Intent(this, PlanATour.class);
		//carry bundle of data optionally
		startActivity(intent);
	}
	
	/**
	 * called when browse categories button is pressed
	 * @param view
	 */
	public void browseCategories(View view)
	{
		Intent intent = new Intent(this, Browse.class);
		startActivity(intent);
	}
	
	/**
	 * called when browse memories button is pressed
	 * @param view
	 */
	public void memories(View view)
	{
		//get current location coordinates 
		LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE); 
		Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		double longitude = location.getLongitude();
		double latitude = location.getLatitude();
		
		Log.i("longitude displayed", String.valueOf(longitude));
		Log.i("latitude displayed", String.valueOf(latitude));

		//get current date 
		Calendar c = Calendar.getInstance();
		SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
		String formattedDate = df.format(c.getTime());
		Log.i("time displayed", formattedDate);
		
		//get current time 
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("K:mm a");
		String formattedTime = sdf.format(now);
		Log.i("date displayed", formattedTime);

		//save to database 
//		DatabaseOperations DB = new DatabaseOperations(this);
//		DB.putInformation(DB, formattedTime, formattedDate, String.valueOf(latitude), String.valueOf(longitude));
		Toast.makeText(getBaseContext(), "successfully put data into database", Toast.LENGTH_SHORT).show();
		//finish();
	}
	
	/**
	 * called when browse favourites button is pressed
	 * @param view
	 */
	public void favourites(View view)
	{
		//retrieving database record
//		DatabaseOperations DOP = new DatabaseOperations(this);
//	    Cursor CR = DOP.getInformation(DOP);
//	    CR.moveToLast();
//	    
//	    //Toast.makeText(getBaseContext(), String.valueOf(CR.getCount()), Toast.LENGTH_SHORT).show();
//	    Toast.makeText(getBaseContext(), CR.getString(0), Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * called when search bar is clicked 
	 * @param v
	 */
	public void searchBar(View view)
	{
		Log.d("3","search bar clicked ");
        Intent intent = new Intent(view.getContext(), SearchActivity.class);
		//carry bundle of data optionally
		startActivity(intent);
	}
	
	@Override
	public void activate(OnLocationChangedListener arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deactivate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * position camera map on zone 1 london
	 * @param map
	 */
	public void configureMap(GoogleMap map){
		map.setMyLocationEnabled(true);
		map.getUiSettings().setZoomControlsEnabled(false);
		map.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder()
			.target(LONDON_COORDINATES).zoom(8).build()));
		map.getUiSettings().setMyLocationButtonEnabled(true);
		map.setMyLocationEnabled(true);
	}
	
	/**
	 * used to populate search suggestion list
	 */
	@SuppressWarnings("unchecked")
	private void downloadLandmarks(String query){
		// Check if there is a network connection available
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		
		DownloadData downloadData  = new DownloadData().createFromSourceForSearchQuery(landmarkSuggestions, query);
		if (networkInfo != null && networkInfo.isConnected()) {
			//execute a new asynchronous task
			downloadData.execute(DownloadData.SEARCH_LANDMARK); 
		}
		landmarkSuggestions = downloadData.getSearchResult();
	}
}
