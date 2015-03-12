package com.github.adamkhazi.monocle;


import android.provider.BaseColumns;
/**
 * Class holding column/field names and types 
 * saved to sqlite database
 * @author adam
 *
 */
public class TableData {
 
 public TableData()
 {
  
 }
 
 public static abstract class TableInfo implements BaseColumns
 {
	 public static final String TIME = "time" ;
	 public static final String DATE = "date" ;
	 public static final String LATITUDE = "latitude" ;
	 public static final String LONGITUDE = "longitude" ;
	 public static final String DATABASE_NAME = "usagedb1";
	 public static final String TABLE_NAME = "usage1";
  
 }

}

