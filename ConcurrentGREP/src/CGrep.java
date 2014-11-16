import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



//import akka.actor.ActorRef;
import akka.actor.*;
import static akka.actor.Actors.*;


/**
 * CGrep is the main class file that will create an Executor Service
 * and run threads of ReadFile to find lines that contain the
 * matching regex that is provided by the user.
 * 
 * @author Andrew Popovich (ajp7560@rit.edu)
 *
 */
public class CGrep {

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
		
		public int getNumFiles(){
			return numFiles;
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
		ActorRef collection;
		
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
		
		collection = actorOf(
				new UntypedActorFactory(){
					public UntypedActor create(){
						return new CollectionActor();
					}
				
				});
		
		collection.start();
		
		//If there are no files pass 1 as the filecount for Standard In
		int fileCount = filenames.size() == 0 ? 1 : filenames.size();
		
		collection.tell(new FileCount(fileCount));
	
		if(filenames.size() == 0) {
			//If no files were submitted, submit standard in and read
			//its output
			ActorRef scanActor = actorOf(
					new UntypedActorFactory(){
						public UntypedActor create(){
							return new ScanActor();
						}
					
					});			
			
			scanActor.start();
			
			scanActor.tell(new Configure("Standard in", System.in, collection, regex));
			
		} else {
			//Otherwise create a new actor for each file
			for(String fileName: filenames){
				File file = new File(fileName);
				InputStream iStream;

				try{
					iStream = new FileInputStream(file);
					Configure config = new Configure(fileName, iStream, collection, regex);

					ActorRef scanActor = actorOf(
							new UntypedActorFactory(){
								public UntypedActor create(){
									return new ScanActor();
								}

							});

					scanActor.start();

					scanActor.tell(config);
				}
				catch(FileNotFoundException e){
					e.printStackTrace();
				}
			}
		}
	}
}
