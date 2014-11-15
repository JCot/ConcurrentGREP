import akka.actor.Actors;
import akka.actor.UntypedActor;


public class CollectionActor extends UntypedActor {
	
	int numFiles = 0;
	int filesProcessed = 0;

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof CGrep.FileCount) {
			numFiles = ((CGrep.FileCount)message).getNumFiles();
		} else if (message instanceof Found) {
			Found results = (Found)message;
			
			if (results != null) {
				for(String line : results.getResults()){
					System.out.println(results.getName() + ": " + line);
				}
			}
			
			filesProcessed++;
			
			if(filesProcessed == numFiles){
				Actors.registry().shutdownAll();
			}
		}	
	}
}
