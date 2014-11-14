import akka.actor.UntypedActor;


public class CollectionActor extends UntypedActor {

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof CGrep.FileCount) {
			
		} else if (message instanceof Found) {
			
		}
	}

}
