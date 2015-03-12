package com.github.adamkhazi.monocle;


import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;


public class SearchActivity extends Activity {
	
	//file related
	String filename = "myfile";
	String string = "Hello world!";
	
	//layout elements 
	private ListView lv;
	private EditText searchField;
	
	// Listview Adapter
    ArrayAdapter<String> adapter;
    
    //arraylists
    List<String> landmarkinfo;
    List<String> landmarkinfodisplay;

	
   public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_search);
      
      //find and populate list
      lv = (ListView) findViewById(R.id.listview10);
      populateList(lv);      
      
      configureSearchField((EditText) findViewById(R.id.searchtext));
      
   }
   
   private void populateList(ListView lv)
   {
	   Landmark a = new Landmark(1, "Big Ben", "Cool tower!", 51.5085300, -0.1257400, "00AA", 12);
	   Landmark b = new Landmark(2, "Oxford Street", "Shopping", 51.5085300, -0.1257400, "00AA", 14);

	   //the original 
	   landmarkinfo = new ArrayList<String>();
	   landmarkinfo.add(a.getName());
	   landmarkinfo.add(a.getDescription());
	   
	   landmarkinfo.add(b.getName());
	   landmarkinfo.add(b.getDescription());
	   
	   //the copy
	   landmarkinfodisplay = new ArrayList<String>();
	   
	   
	   // This is the array adapter, it takes the context of the activity as a 
	   // first parameter, the type of list view as a second parameter and your 
	   // array as a third parameter.
	   adapter = new ArrayAdapter<String>(this, R.layout.mylist, landmarkinfodisplay); //add all elements to filterable adapter list

	   lv.setAdapter(adapter); 
	   lv.setOnItemClickListener(new OnItemClickListener()
	   {
		   @Override
		   public void onItemClick(AdapterView<?> arg0, View view, int pos, long arg3) {
			   Log.i("position in list clicked --", ""+pos); 
			   //Here pos is the position of row clicked   
			   String landmarkName =(String)arg0.getItemAtPosition(pos); 
         	  
         	   Intent myIntent = new Intent(view.getContext(), ViewLandmark.class);
               myIntent.putExtra(Browse.EXTRA_MESSAGE, landmarkName);
               startActivityForResult(myIntent, 0);
		   }
	   });
   }
   
   public void configureSearchField(final EditText searchField)
   {
	   searchField.addTextChangedListener(new TextWatcher() {

		   @Override
		   public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {

			   // When user changes text 
			   landmarkinfodisplay.clear();
			   String searchstring = searchField.getText().toString();

			   for(String s : landmarkinfo)
			   {
				   if(s.toLowerCase().contains(searchstring.toLowerCase()))
				   {
					   landmarkinfodisplay.add(s);
				   }
				   adapter.notifyDataSetChanged();
			   }
		   }

		   @Override
		   public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			   // TODO Auto-generated method stub

		   }

		   @Override
		   public void afterTextChanged(Editable arg0) {
			   // TODO Auto-generated method stub    
			   if (((searchField.getText().toString()).matches("")))
			   {
				   landmarkinfodisplay.clear();
				   adapter.notifyDataSetChanged();

			   }
		   }
	   });
   }
   
   public void displayFromFile(View view){
	   //writing objects to file
	   FileInputStream fis;
	   ArrayList<Landmark> landmarks = new ArrayList<Landmark>();
       try {
           fis = openFileInput(filename);
           ObjectInputStream is = new ObjectInputStream(fis);
           landmarks = (ArrayList<Landmark>)is.readObject();
               
           Toast.makeText(getBaseContext(),"read arraylist from file",Toast.LENGTH_SHORT).show();
           Log.i("displaying works","displaying from file is the best!");
           Log.i(String.valueOf(landmarks.get(0).getName()),"displaying from file is the best!");
           Log.i(String.valueOf(landmarks.size()),"displaying from file is the best!");

           is.close();
       } 
       catch (FileNotFoundException e) {
           e.printStackTrace();
       } 
       catch (StreamCorruptedException e) {
           e.printStackTrace();
       } 
       catch (IOException e) {
           e.printStackTrace();
       } 
       catch (ClassNotFoundException e) {
           e.printStackTrace();
       }
   }
   
   public void writeToFile(View view){
	   Landmark a = new Landmark(1, "Big Ben", "Clock Tower...", 51.5085300, -0.1257400, "00AA", 12);
	   
	   ArrayList<Landmark> landmarks = new ArrayList<Landmark>();
	   
	   landmarks.add(a);

	   FileOutputStream fos;
       try {
           fos = openFileOutput(filename, Context.MODE_PRIVATE);
           ObjectOutputStream os = new ObjectOutputStream(fos);
           os.writeObject(landmarks);
           Toast.makeText(getBaseContext(),"wrote arraylist to file",Toast.LENGTH_SHORT).show();
           os.close();
       } 
       catch (FileNotFoundException e) {
           e.printStackTrace();
       } 
       catch (IOException e) {
           e.printStackTrace();
       }
   }

   public void onNewIntent(Intent intent) {
      setIntent(intent);
      handleIntent(intent);
   }
   
   public void onListItemClick(ListView l,
      View v, int position, long id) {
      // call detail activity for clicked entry
   }
   
   private void handleIntent(Intent intent) {
      if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
         String query = intent.getStringExtra(SearchManager.QUERY);
         doSearch(query);
      }
   }   
   private void doSearch(String queryStr) {
   // get a Cursor, prepare the ListAdapter
   // and set it
   }
}


