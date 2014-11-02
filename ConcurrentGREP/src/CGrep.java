import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;


public class CGrep {
	
	private final static int numThreads = 3;
	
	private final static ExecutorService es = Executors.newFixedThreadPool(numThreads);
	private final static CompletionService<Found> compService = new ExecutorCompletionService<Found> (es); 

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
		
		ArrayList<Future<Found>> futures = new ArrayList<Future<Found>>(filenames.size());
		if(filenames.size() != 0) {
			for(String filename : filenames){
				File file = new File(filename);
				futures.add(compService.submit(new ReadFile(filename, file, regex)));
			}
			
			for(int i = 0; i < filenames.size(); i++) {
				Future<Found> future;
				Found file = null;
				try {
					future = compService.take();
					file = future.get();
				} catch (ExecutionException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {
					for(Future<Found> f : futures) {
						f.cancel(true);
					}
				}
				if (file != null) {
					for(String line : file.getResults()){
						System.out.println(file.getName() + ": " + line);
					}
				}
					
			}
			es.shutdown();
		} else {
			//Read stdin
			//compService.submit(new ReadFile());
		}	
		
		
		
		
	}
	
	

}
