package com.github.adamkhazi.monocle;


public class Category {

    private static final String SEP_CHARACTER="\u0007";
    
    //data contained in this object describing the Category
    private int id;
    private String categoryName;
    private String packedString; //A row received from server
    
    //getters for the data
    public int getId() {return id;}
    public String getCategoryName() {return categoryName;}
    
    //constructor - for packing up a search by name search result into an object
    Category(int id, String categoryName){  
            this.id = id;
            this.categoryName = categoryName;
    }
    
    //constructor - for creating a new category with a packed string retrieved from server
    Category(String packedString){
    	this.packedString=packedString;
    }
    
    /**
     * invokes super constructor
     */
    public Category(){
        super();
    }
    
    /**
    *pack the data into the packed format, using SEP_CHARACTER
    *as a column divider
    */
    public String pack(){
    	StringBuilder sb = new StringBuilder()
    	.append(id).append(SEP_CHARACTER)
    	.append(categoryName);
    	return sb.toString();
    }
    
    /**
     * unpack string sent from server into id and categoryName
     */
    public Category unPack(){
    	String[] parts = packedString.split(SEP_CHARACTER);
    	id = Integer.parseInt(parts[0]);
    	categoryName = parts[1]; 
    	return this;
    }
    
    /**
     * to display in listview
     */
    @Override
    public String toString() {
        return this.categoryName;
    }
}

