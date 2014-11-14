import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import akka.actor.UntypedActor;

public class ScanActor extends UntypedActor {
	
	private CGrep.Configure config;
	
	private Pattern p;
	
	@Override
	public void onReceive(Object configure) throws Exception {
		config = (CGrep.Configure) configure;
		p = Pattern.compile(this.config.getRegex());
		
		Found results = new Found(this.config.getFileName());
		
		List<String> matchingLines = new ArrayList<String>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(this.config.getInputStream()));  
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
		
		this.config.getCollActor().tell(results, self());
		
	}
	
}