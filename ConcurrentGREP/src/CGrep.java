import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;


public class CGrep {
	
	private final static int numThreads = 3;
	
	private final static Executor es = Executors.newFixedThreadPool(numThreads);
	private final static CompletionService compService = new ExecutorCompletionService<Found> (es); 

	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<String> filenames = new ArrayList<String>();
		
		//Terminate the program if an improper amount of arguments are passed in
		if(args.length == 0) {
			System.exit(0);
		}
		
		String regex = args[0];
		
		if(args.length > 1) {
			for(int i = 1; i < args.length; i++) {
				filenames.add(args[i]);
			}
		}
		
		if(filenames.size() != 0) {
			for(String filename : filenames){
				compService.submit(new ReadFile());
			}
		} else {
			compService.submit(new ReadFile());
		}
		
		
		
		
		
	}
	
	

}
