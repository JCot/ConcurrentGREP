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
			Found file = (Found)message;
			
			if (file != null) {
				for(String line : file.getResults()){
					System.out.println(file.getName() + ": " + line);
				}
			}
			
			filesProcessed++;
			
			if(filesProcessed == numFiles){
				Actors.registry().shutdownAll();
			}
		}
		
	}

}
