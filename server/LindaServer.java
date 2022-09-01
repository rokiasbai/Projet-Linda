package linda.server;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;
import linda.Tuple;
import linda.shm.CentralizedLinda;

/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class LindaServer extends UnicastRemoteObject implements LindaInterfaceRMI {
	
	private CentralizedLinda mlinda;
	
	protected LindaServer() throws RemoteException {
		super();
		this.mlinda = new CentralizedLinda();
	}
	
	public static void main(String[] args) {
		try {
			//instancier le Serveur
			LindaServer serveur = new LindaServer();
			//Créer le registre
			Registry monregistre = LocateRegistry.createRegistry(4000);
			//Enregistrer le serveur dans le registre
			Naming.rebind("//localhost:4000/LindaServer",serveur);
		} catch (RemoteException | MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	//appeler toutes les méthodes du CentralizedLinda
	@Override
	public void write(Tuple t) throws RemoteException{
		this.mlinda.write(t);
	}

	@Override
	public Tuple take(Tuple template) throws RemoteException{
		return this.mlinda.take(template);
	}

	@Override
	public Tuple read(Tuple template)throws RemoteException {
		return this.mlinda.read(template);
	}

	@Override
	public Tuple tryTake(Tuple template) throws RemoteException{
		return this.mlinda.tryTake(template);
	}

	@Override
	public Tuple tryRead(Tuple template) throws RemoteException{
		return this.mlinda.tryRead(template);
	}

	@Override
	public Collection<Tuple> takeAll(Tuple template) throws RemoteException{
		return this.mlinda.takeAll(template);
	}

	@Override
	public Collection<Tuple> readAll(Tuple template) throws RemoteException{
		return this.mlinda.readAll(template);
	}
	
	@Override
	public void eventRegister(eventMode mode, eventTiming timing, Tuple template, CallbackInterfaceRMI callback) throws RemoteException{
		CallbackImplForServer ok = new CallbackImplForServer(callback);
		this.mlinda.eventRegister(mode,timing,template,ok);
	}

	@Override
	public void debug(String prefix) throws RemoteException{
		this.mlinda.debug(prefix);
		
	}
}