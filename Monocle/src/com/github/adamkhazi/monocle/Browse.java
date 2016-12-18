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

public class Browse extends Activity{
//a test comment
	
	List <Category> categories = new ArrayList<Category>();
	ArrayAdapter<Category> arrayAdapter = null; 
	public static final String EXTRA_MESSAGE = "com.example.projectmonocle.MESSAGE";
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse);
	    
		ListView lv = (ListView) findViewById(R.id.listView1); //interface with xml element 
		arrayAdapter = new ArrayAdapter<Category>(this,R.layout.browse_list_item,
				categories); //to notify change from inner class
		
		downloadCategories(); //download categories to rootCategoryList
        configureListView(lv);
              
	}
	
	/**
	 * called when search bar is clicked 
	 * @param v
	 */
	public void searchBar(View view)
	{
		Log.d("1","search bar clicked");
        Intent intent = new Intent(view.getContext(), SearchActivity.class);
		//carry bundle of data optionally
		startActivity(intent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.browse, menu);
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
	
	@SuppressWarnings("unchecked")
	private void downloadCategories(){
		// Check if there is a network connection available
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		DownloadData downloadData  = new DownloadData().createFromSourceForCategory(categories);
		if (networkInfo != null && networkInfo.isConnected()) {
			//execute a new asynchronous task
			downloadData.execute(DownloadData.BROWSE_URL); 
		}
		categories = downloadData.getCategoryList();
		updateAdapterWithNewData();

	}
	
	public void updateAdapterWithNewData(){
		arrayAdapter.notifyDataSetChanged();
	}
	
	public void configureListView(ListView lv)
	{
		lv.setAdapter(arrayAdapter); 
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3){
				Category categoryChosen = (Category) arg0.getAdapter().getItem(pos);

				//Log.d("listener", categoryChosen);
				Intent myIntent = new Intent(view.getContext(), ListLandmarks.class);
				myIntent.putExtra(EXTRA_MESSAGE, categoryChosen.getId());
				startActivityForResult(myIntent, 0);
			}
		}); 
	}
}