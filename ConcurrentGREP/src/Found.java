import java.util.ArrayList;


public class Found {
	private String name;
	private ArrayList<String> results = new ArrayList<String>();
	
	public Found(String name){
		this.name = name;
	}
	
	public void addItem(String match){
		results.add(match);
	}
	
	public ArrayList<String> getResults(){
		return results;
	}
	
	public String getName(){
		return name;
	}
}