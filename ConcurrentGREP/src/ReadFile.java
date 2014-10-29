import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.*;


public class ReadFile implements Callable{
	private File file;
	private String fileName;
	private String regex;
	private Pattern p;
	private List<String> matchingLines = new ArrayList<String>();
	
	
	public ReadFile(String fileName, File f, String regex) {
		this.fileName = fileName;
		file = f;
		this.regex = regex;
		p = Pattern.compile(regex);
	}

	@Override
	public Object call() throws Exception {
		
		BufferedReader br = new BufferedReader(new FileReader(file));  
		String line = null;  
		int lineNum = 0;
		while ((line = br.readLine()) != null)  
		{  
			Matcher m = p.matcher(line);
			lineNum++;
		    if (m.matches()) {
		    	matchingLines.add("" + lineNum + " " + line);
		    }
		} 
		
		return null;
	}
	
	public List<String> getMatchingLines() {
		return matchingLines;
	}

}
