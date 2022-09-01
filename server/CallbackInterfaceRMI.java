package linda.server;

import java.rmi.Remote;
import java.rmi.RemoteException;
import linda.Tuple;


public interface CallbackInterfaceRMI extends Remote{

    /** Callback when a tuple appears. 
     * @param t the new tuple
     */
    void call(Tuple t) throws RemoteException;
}
