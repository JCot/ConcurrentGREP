import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import akka.actor.ActorRef;


/**
 * CGrep is the main class file that will create an Executor Service
 * and run threads of ReadFile to find lines that contain the
 * matching regex that is provided by the user.
 * 
 * @author Andrew Popovich (ajp7560@rit.edu)
 *
 */
public class CGrep {
	
	/** Number of threads in the thread pool */
	private final static int numThreads = 3;
	
	/** Executor Service with a fixed thread pool size */
	private final static ExecutorService es = Executors.newFixedThreadPool(numThreads);
	
	/** ExecutorCompletionService that wraps the ExecutorService so that
	 *  any attempt to get a future is blocked until one is actually computed.
	 */
	private final static CompletionService<Found> compService = new ExecutorCompletionService<Found> (es); 

	static class Configure {
		
		private final String fileName;
		
		private final InputStream inputStream;
		
		private final ActorRef collActor;
		
		private final String regex;
		
		public Configure(String fileName, InputStream inStream, ActorRef collActor, String regex) {
			this.fileName = fileName;
			this.inputStream = inStream;
			this.collActor = collActor;
			this.regex = regex;
		}
		
		public String getFileName() {
			return fileName;
		}

		public InputStream getInputStream() {
			return inputStream;
		}

		public ActorRef getCollActor() {
			return collActor;
		}

		public String getRegex() {
			return regex;
		}

	}
	
	static class FileCount {
		
		private final int numFiles;
		
		public FileCount(int numFiles) {
			this.numFiles = numFiles;
		}
	}
	
	/**
	 * Main method that runs the program.  It takes in a regex and optional
	 * file names and compares that regex against those files or standard in.
	 * 
	 * @param args - Index 0 - Regex to match, required
	 *             - Index 1 through n - Filenames of files to check, optional
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

		//Submit tasks that correspond to each file
		for(String filename : filenames){
			File file = new File(filename);
			InputStream iStream;
			try {
				iStream = new FileInputStream(file);
				futures.add(compService.submit(new ReadFile(filename, iStream, regex)));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	
		if(filenames.size() == 0) {
			//If no files were submitted, submit standard in and read
			//its output
			futures.add(compService.submit(new ReadFile("Standard In", System.in, regex)));
			Future<Found> future;
			Found stdIn = null;
			try {
				future = compService.take();
				stdIn = future.get();
			} catch (ExecutionException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				for(Future<Found> f : futures) {
					f.cancel(true);
				}
			}
			if (stdIn != null) {
				for(String line : stdIn.getResults()){
					System.out.println(stdIn.getName() + ": " + line);
				}
			}
		} else {
			//Otherwise get the futures for each file
			//and read wach output
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
		}
		es.shutdown();
	}
}
