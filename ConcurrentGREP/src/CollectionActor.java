import akka.actor.Actors;
import akka.actor.UntypedActor;

/**
 * CollectionActor is a representation of an actor
 * that will simply print out the results of
 * a regex match once a scan actor finds a match.
 * 
 * @author Andrew Popovich (ajp7560@rit.edu)
 *		   Justin Cotner
 *		   Shannon Trudeau
 */
public class CollectionActor extends UntypedActor {
	
	/** Number of files passed in */
	private int numFiles = 0;
	
	/** Number of files processed */
	private int filesProcessed = 0;

	/**
	 * onReceive is the method that is called whenever a CollectionActor
	 * receives a message.  The first message that is expected is a FileCount
	 * message containing the number of files passed into the system.  Later
	 * messages are then expected to contain the results from parsing those files
	 * against a regex.
	 * 
	 * @param message - Either a FileCount message or a Found message
	 */
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof CGrep.FileCount) {
			//Set the number of files to be processed
			numFiles = ((CGrep.FileCount)message).getNumFiles();
		} else if (message instanceof Found) {
			//Process a file by printing out the results
			Found results = (Found)message;
			
			if (results != null) {
				for(String line : results.getResults()){
					System.out.println(results.getName() + ": " + line);
				}
			}
			
			filesProcessed++;
			
			//If everything was processed then shutdown the actors
			if(filesProcessed == numFiles){
				Actors.registry().shutdownAll();
			}
		}	
	}
}
