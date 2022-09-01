package linda.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;

import linda.Callback;
import linda.Linda;
import linda.Tuple;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaClient implements Linda {
	
    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
     */
	private LindaInterfaceRMI serveur;
	
    public LindaClient(String serverURI) {
        try {
        	//récuperer du registre le bon serveur
			serveur =(LindaInterfaceRMI)Naming.lookup(serverURI);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }
    
    //appeler toutes les méthodes de Linda
    
	@Override
	public void write(Tuple t) {
		try {
			serveur.write(t);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Tuple take(Tuple template) {
		try {
			return serveur.take(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Tuple read(Tuple template) {
		try {
			return serveur.read(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Tuple tryTake(Tuple template) {
		try {
			return serveur.tryTake(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Tuple tryRead(Tuple template) {
		try {
			return serveur.tryRead(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) {
		try {
			return serveur.takeAll(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) {
		try {
			serveur.readAll(template);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback) {
		try {
			CallbackImplForClient ok = new CallbackImplForClient(callback);
			serveur.eventRegister(mode,timing,template,ok);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void debug(String prefix) {
		try {
			serveur.debug(prefix);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
