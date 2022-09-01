package linda.server;

import java.rmi.RemoteException;

import linda.Callback;
import linda.Tuple;

public class CallbackImplForServer implements Callback{
	
	private CallbackInterfaceRMI moncall;
	
	public CallbackImplForServer(CallbackInterfaceRMI moncall) {
		this.moncall=moncall;
	}
	
	@Override
	public void call(Tuple t) {
		try {
			this.moncall.call(t);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
