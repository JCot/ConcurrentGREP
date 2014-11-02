import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.*;


public class ReadFile implements Callable<Found>{
	private File file;
	private InputStream inputStream; // The input stream to read from.
	private String fileName; // The name of the file being read (if the input stream is a file).
	private String regex; // The regex to check against each line.
	private Pattern p;
	private List<String> matchingLines = new ArrayList<String>(); // List of matching lines.
	
	
	public ReadFile(String fileName, InputStream in, String regex) {
		this.fileName = fileName;
		inputStream = in;
		this.regex = regex;
		p = Pattern.compile(regex);
	}

	/*
	 * Scans lines in from the specified inputStream. If the line
	 * contains any matches to the specified regex add that line to
	 * the results in the Found object. Returns the Found object when
	 * finished.
	 * 
	 */
	@Override
	public Found call() throws Exception {
		Found results = new Found(fileName);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));  
		String line = null;  
		int lineNum = 0;
		while ((line = br.readLine()) != null)  
		{  
			Matcher m = p.matcher(line);
			lineNum++;
		    if (m.find()) {
		    	matchingLines.add("" + lineNum + " " + line);
		    	results.addItem("" + lineNum + " " + line);
		    }
		} 
		
		return results;
	}
	
	public List<String> getMatchingLines() {
		return matchingLines;
	}

}
