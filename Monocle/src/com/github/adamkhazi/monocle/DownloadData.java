package com.github.adamkhazi.monocle;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

public class DownloadData extends AsyncTask{
	
	private static final String VERBOSE_TAG = "1";
	private List<Category> categories;
	private List<Landmark> landmarks;
	private int id;
	private String searchQuery;
	public final static String BROWSE_URL = "http://134.83.83.28:47216/mBrowse";
	public final static String LANDMARK_BY_CATEGORY = "http://134.83.83.28:47216/mRetrieve?type=category&id=";
	public final static String SEARCH_LANDMARK = "http://134.83.83.28:47216/mSearch?type=landmark&q=";
	public final static String SEARCH_MEMORY = "http://134.83.83.28:47216/mSearch?type=memory&q=";
	
	public DownloadData(){
		//hide constructor
	};
	
	//factory methods to deal with erasure
	public DownloadData createFromSourceForCategory(List<Category> categories){
		this.categories = new ArrayList<Category>();
		this.categories = categories;
		return this;
	}
	
	public DownloadData createFromSourceForLandmark(List<Landmark> landmarks, int id){
		this.landmarks = new ArrayList<Landmark>();
		this.landmarks = landmarks;
		this.id = id;
		return this;
	}
	
	public DownloadData createFromSourceForSearchQuery(List<Landmark> landmarks, String query){
		this.landmarks = new ArrayList<Landmark>(landmarks);
		this.searchQuery = query;
		this.id = id;
		return this;
	}
	
	public List<Category> getCategoryList(){
		return categories;
	}
	
	public List<Landmark> getLandmarkList(){
		return landmarks;
	}
	
	public List<Landmark> getSearchResult(){
		return landmarks;
	}
	@Override
	protected void onPreExecute()
	{
	    
	} 
	
	// onPostExecute displays the results of the AsyncTask. @Override
	protected void onPostExecute(Landmark landmarks) {
		Log.v(VERBOSE_TAG,"Asynctask done");
		Log.v(VERBOSE_TAG,this.landmarks.get(0).getName());
		MainActivity.suggestionAdapter.notifyDataSetChanged();
		MainActivity.matv.showDropDown();
	}
	
	@Override
	protected List<Landmark> doInBackground(Object... urls) { 
		
			try {
				return downloadUrl((String) urls[0]);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return landmarks; 
		
	}

	private List<Landmark> downloadUrl(String myurl) throws IOException { 
		InputStream is = null;
		try {
			//setup connection
			HttpURLConnection conn = null;
			
			if (myurl.equals(BROWSE_URL)){
				conn = setupConnection(myurl);
			} else if(myurl.equals(LANDMARK_BY_CATEGORY)) {
				StringBuilder sb = new StringBuilder();
				conn = setupConnection((sb.append(myurl).append(String.valueOf(id)))
						.toString()); //add requested category id to url
			} else if(myurl.equals(SEARCH_LANDMARK)) {
				StringBuilder sb = new StringBuilder();
				conn = setupConnection((sb.append(myurl).append(searchQuery))
						.toString()); //add requested category id to url
			}
			//get http input stream
			is = conn.getInputStream();
			
			//sort data and add to appropriate list
			return addDataToList(is, myurl);
			
			// Makes sure that the InputStream is closed after the app is
			// finished using it.	
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
	
	private HttpURLConnection setupConnection(String myurl) throws IOException{
		URL url = new URL(myurl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
		conn.setReadTimeout(10000 /* milliseconds */); 
		conn.setConnectTimeout(15000 /* milliseconds */); 
		conn.setRequestMethod("GET");
		conn.setDoInput(true);
		
		// Starts the query
		conn.connect();
		
		//response code
		int response = conn.getResponseCode(); 
		Log.v(VERBOSE_TAG, "The response is: " + response); 
		
		return conn;
	}
	
	//add data downloaded to appropriate list
	private List<Landmark> addDataToList(InputStream is, String myurl) throws IOException{
		// Convert the InputStream into a string
		InputStreamReader reader = new InputStreamReader(is, "UTF-8"); 
		BufferedReader bReader = new BufferedReader(reader);

		//choose between different ways to sort
		if (myurl.equals(BROWSE_URL)){
			String line;
			while ((line = bReader.readLine()) != null){
				Category newCategory = new Category(line); //new category with packed string 
				categories.add(newCategory.unPack());      //unpack into object attributes and add to categories list
			}
			Log.v(VERBOSE_TAG,"fetching all categories"); //debug message
			
		} else if (myurl.equals(LANDMARK_BY_CATEGORY)){
			String line;
			while ((line = bReader.readLine()) != null){
				Landmark newLandmark = new Landmark(line); //new category with packed string 
				landmarks.add(newLandmark.simplifiedUnpack());  //unpack into object attributes and add to landmarks list
			}
			Log.v(VERBOSE_TAG,"fetching landmarks by category"); //debug message
		} else if (myurl.equals(SEARCH_LANDMARK)){
			String line;
			while ((line = bReader.readLine()) != null){
				Landmark newLandmark = new Landmark(line); //new category with packed string 
				landmarks.add(newLandmark.simplifiedUnpack());  //unpack into object attributes and add to landmarks list
			}
			Log.v(VERBOSE_TAG,"fetching search results"); //debug message
			Log.v(VERBOSE_TAG,landmarks.get(0).getName());
		}
		return landmarks;
	}
}


