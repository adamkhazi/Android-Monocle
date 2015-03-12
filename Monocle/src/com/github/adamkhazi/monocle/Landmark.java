package com.github.adamkhazi.monocle;


/**
 * Object to represent landmark data 
 * @author adam
 *
 */
public class Landmark implements java.io.Serializable{
	
	//Column seperator 
    private static final String SEP_CHARACTER="\u0007";
	
    //Landmark properties
    private int id;
	private String name;
	private String description;
	private double latitude;
	private double longitude;
	private String sec_id;
	private int cat_id;
	
	//A row received from server
	private String packedString; 
	
	public Landmark()
	{
		super();
	}
	//constructor
	Landmark(String packedString)
	{
		this.packedString = packedString;
	}
	
	//constructor
	Landmark(int id, String name, String description, double latitude, double longitude, String sec_id, int cat_id) 
	{	
		this.id = id;
		this.name = name;
		this.description = description;
		this.latitude = latitude;
		this.longitude = longitude;
		this.sec_id = sec_id;
		this.cat_id = cat_id;
	}
	
	public int getId(){return(id);}
	public String getName(){return(name);}
	public String getDescription(){return(description);}
	public double getLatitude(){return(latitude);}
	public double getLongitude(){return(longitude);}

	public void setId(int id){this.id = id;}
	public void setName(String name){this.name = name;}
	public void setDescription(String description){this.description = description;}
	public void setLatitude(int latitude){this.latitude = latitude;}
	public void setLongitude(int longitude){this.longitude = longitude;}
	
	/**
     * unpack string sent from server into id and landmarkname
     * used for search and displaying landmarks in listview
     */
    public Landmark simplifiedUnpack(){
    	String[] parts = packedString.split(SEP_CHARACTER);
    	id = Integer.parseInt(parts[0]);
    	name = parts[1]; 
    	return this;
    }
    
    /**
     * unpack string sent from server into all landmark attributes
     */
    public Landmark fullUnpack(){
    	String[] parts = packedString.split(SEP_CHARACTER);
    	id = Integer.parseInt(parts[0]);
    	name = parts[1]; 
    	description = parts[2]; 
    	latitude = Double.parseDouble(parts[3]); 
    	longitude = Double.parseDouble(parts[4]);
    	sec_id = parts[5];
    	cat_id = Integer.parseInt(parts[6]);
    	return this;
    }
    
    /**
     * to display in listview
     */
    @Override
    public String toString() {
        return this.name;
    }
}
