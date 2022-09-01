package linda.search.basic;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.UUID;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Paths;
import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.server.CallbackImplForClient;
import linda.server.LindaInterfaceRMI;
import linda.search.basic.*;
/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class ClientManager  implements Linda {
	  private UUID reqUUID;
	  private String pathname;
	  private String search;
	  private int bestvalue = Integer.MAX_VALUE; // lower is better
	  private String bestresult;
    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
     */
	private LindaInterfaceRMI serveur;

    public ClientManager(String serverURI, String pathname, String search) {
        try {
        	this.pathname = pathname;
            this.search = search;
			serveur =(LindaInterfaceRMI)Naming.lookup(serverURI);
		} catch (MalformedURLException | RemoteException | NotBoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

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
  //Méthodes du manager 
	 private void addSearch(String search) {
	        this.search = search;
	        this.reqUUID = UUID.randomUUID();
	        System.out.println("Search " + this.reqUUID + " for " + this.search);
	        eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE, new Tuple(Code.Result, this.reqUUID, String.class, Integer.class), new CbGetResult());
	        write(new Tuple(Code.Request, this.reqUUID, this.search));
	    }
	 
	 private void loadData(String pathname) {
	        try (Stream<String> stream = Files.lines(Paths.get(pathname))) {
	            stream.limit(10000).forEach(s -> write(new Tuple(Code.Value, s.trim())));
	        } catch (java.io.IOException e) {
	            e.printStackTrace();
	        }
	    }
	
	 private void waitForEndSearch() {
	        take(new Tuple(Code.Searcher, "done", this.reqUUID));
	        take(new Tuple(Code.Request, this.reqUUID, String.class)); // remove query
	        System.out.println("query done");
	    }

	    private class CbGetResult implements linda.Callback {
	        public void call(Tuple t) {  // [ Result, ?UUID, ?String, ?Integer ]
	            String s = (String) t.get(2);
	            Integer v = (Integer) t.get(3);
	            if (v < bestvalue) {
	                bestvalue = v;
	                bestresult = s;
	                System.out.println("New best (" + bestvalue + "): \"" + bestresult + "\"");
	            }
	            eventRegister(Linda.eventMode.TAKE, Linda.eventTiming.IMMEDIATE, new Tuple(Code.Result, reqUUID, String.class, Integer.class), this);
	        }
	    }

	    public void run() {
	        this.loadData(pathname);
	        this.addSearch(search);
	        this.waitForEndSearch();
	    }

}
