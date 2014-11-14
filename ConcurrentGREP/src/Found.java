import java.util.ArrayList;


public class Found {
	private final String name; // The file the Found instance is associated with
	private final ArrayList<String> results = new ArrayList<String>(); // List of lines that match the regex given.
	
	public Found(String name){
		this.name = name;
	}
	
	// Adds an item to the result list.
	public void addItem(String match){
		results.add(match);
	}
	
	// Returns the results.
	public ArrayList<String> getResults(){
		return results;
	}
	
	// Returns the name.
	public String getName(){
		return name;
	}
}