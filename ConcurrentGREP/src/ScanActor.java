import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import akka.actor.UntypedActor;

/**
 * ScanActor is a representation of an actor that
 * will parse a file and match the passed in
 * regex pattern in that file.
 * 
 * @author Andrew Popovich (ajp7560@rit.edu)
 *		   Justin Cotner
 *		   Shannon Trudeau
 */
public class ScanActor extends UntypedActor {
	
	/** Configure message to be passed in via messaging */
	private CGrep.Configure config;
	
	/** Pattern object representing the regex to match */
	private Pattern p;
	
	/**
	 * onReceive is the method that will be called whenever
	 * a ScanActor receives a message.  It expects the object
	 * to be passed in to be a Configure object, and will use
	 * the information from that to parse a file to match a regex.
	 * 
	 * @param configure - Configure object containing data for parsing a file
	 */
	@Override
	public void onReceive(Object configure) throws Exception {
		//Get the configure object and obtain the regex and input stream
		config = (CGrep.Configure) configure;
		p = Pattern.compile(this.config.getRegex());
		
		Found results = new Found(this.config.getFileName());
		
		List<String> matchingLines = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(this.config.getInputStream())); 
		
		//Parse the file and find matching regex patterns
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
		
		//Send the results to the collection actor
		this.config.getCollActor().tell(results, self());
		
	}
	
}