package com.github.adamkhazi.monocle;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListLandmarks extends Activity {

	//Listview
	List <Landmark> landmarks = new ArrayList<Landmark>();
	ArrayAdapter<Landmark> arrayAdapter = null; 
	private int categoryIdChosen;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_landmarks);
		
		//get id of category  chosen
		categoryIdChosen = getIntent().getIntExtra(Browse.EXTRA_MESSAGE, -1);
		Log.v("dd",String.valueOf(categoryIdChosen));
		
		ListView lv = (ListView) findViewById(R.id.subcatbrowser);
		downloadLandmarks();
		configureList(lv);
		
	}

	private void configureList(ListView lv) {
		// This is the array adapter, it takes the context of the activity as a 
        // first parameter, the type of list view as a second parameter and your 
        // array as a third parameter.
		
        ArrayAdapter<Landmark> arrayAdapter = new ArrayAdapter<Landmark>(
                this, 
                android.R.layout.simple_list_item_1,landmarks);

        lv.setAdapter(arrayAdapter); 
        lv.setOnItemClickListener(new OnItemClickListener()
        {
              @Override
              public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
            	  Landmark landmarkChosen = (Landmark) arg0.getAdapter().getItem(pos);
            	  Intent myIntent = new Intent(view.getContext(), ViewLandmark.class);
                  myIntent.putExtra(Browse.EXTRA_MESSAGE, landmarkChosen.getId());
                  startActivityForResult(myIntent, 0);
              }
        });
	}
	
	@SuppressWarnings("unchecked")
	private void downloadLandmarks(){
		// Check if there is a network connection available
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		DownloadData downloadData  = new DownloadData().createFromSourceForLandmark(landmarks, categoryIdChosen);
		if (networkInfo != null && networkInfo.isConnected()) {
			//execute a new asynchronous task
			downloadData.execute(DownloadData.LANDMARK_BY_CATEGORY); 
		}
		landmarks = downloadData.getLandmarkList();
	}
	
	public void updateAdapter(){
		arrayAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.browse_sub_cat, menu);
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
}
