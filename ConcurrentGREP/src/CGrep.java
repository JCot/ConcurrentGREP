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
 * CGrep is the main class file that will create actors that
 * will concurrently match a regex pattern in a set of
 * files or from standard input.
 * 
 * @author Andrew Popovich (ajp7560@rit.edu)
 *		   Justin Cotner
 *		   Shannon Trudeau
 */
public class CGrep {

	/**
	 * Immutable class that is used to to configure a ScanActor
	 */
	static class Configure {
		
		/** Name of the file to parse */
		private final String fileName;
		
		/** Name of the input stream to parse from */
		private final InputStream inputStream;
		
		/** Reference to the collection actor to send results to */
		private final ActorRef collActor;
		
		/** Pattern to match */
		private final String regex;
		
		/**
		 * Constructor for a Configure immutable message.
		 * 
		 * @param fileName - String of the file name that will be parsed
		 * @param inStream - InputStream for the file to be parse
		 * @param collActor - Reference to the collection actor
		 * @param regex - String of the regex pattern to match
		 */
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
	
	/**
	 * Immutable class for the FileCount message.
	 */
	static class FileCount {
		
		/** Number of files to be parsed */
		private final int numFiles;
		
		/**
		 * Constructor for the FileCount immutable message.
		 * 
		 * @param numFiles - int representing the number of files being parsed
		 */
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
		
		//Create and start the collection actor
		collection = actorOf(
				new UntypedActorFactory(){
					public UntypedActor create(){
						return new CollectionActor();
					}
				
				});
		collection.start();
		
		//If there are no files pass 1 as the file count for Standard In
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
