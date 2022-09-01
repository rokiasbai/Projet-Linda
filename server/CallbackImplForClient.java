package linda.server;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import linda.Callback;
import linda.Tuple;

public class CallbackImplForClient extends UnicastRemoteObject implements CallbackInterfaceRMI{
	
	private Callback moncall;
	
	public CallbackImplForClient(Callback moncall) throws RemoteException {
		super();
		this.moncall=moncall;
	}
	
	/** Callback when a tuple appears. 
     * 
     * @param t the new tuple
     */
	@Override
	public void call(Tuple t) {
		this.moncall.call(t);
		
	}
   
}
