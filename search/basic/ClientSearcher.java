package linda.search.basic;

import java.net.MalformedURLException;
import linda.server.*;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;
import linda.Callback;
import linda.Linda;
import linda.Tuple;
import linda.server.CallbackImplForClient;
import linda.server.LindaInterfaceRMI;
import linda.search.basic.*;
/** Client part of a client/server implementation of Linda.
 * It implements the Linda interface and propagates everything to the server it is connected to.
 * */
public class ClientSearcher  implements Linda {
    /** Initializes the Linda implementation.
     *  @param serverURI the URI of the server, e.g. "rmi://localhost:4000/LindaServer" or "//localhost:4000/LindaServer".
     */
	private LindaInterfaceRMI serveur;

    public ClientSearcher(String serverURI) {
        try {
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
	public void run() {
        System.out.println("Ready to do a search");
        Tuple treq = read(new Tuple(Code.Request, UUID.class, String.class));
        UUID reqUUID = (UUID)treq.get(1);
        String req = (String) treq.get(2);
        Tuple tv;
        System.out.println("Looking for: " + req);
        while ((tv = tryTake(new Tuple(Code.Value, String.class))) != null) {
            String val = (String) tv.get(1);
            int dist = getLevenshteinDistance(req, val);
            if (dist < 10) { // arbitrary
                write(new Tuple(Code.Result, reqUUID, val, dist));
            }
        }
        write(new Tuple(Code.Searcher, "done", reqUUID));
    }
	
	/*****************************************************************/

    /* Levenshtein distance is rather slow */
    /* Copied from https://www.baeldung.com/java-levenshtein-distance */
    static int getLevenshteinDistance(String x, String y) {
        int[][] dp = new int[x.length() + 1][y.length() + 1];
        for (int i = 0; i <= x.length(); i++) {
            for (int j = 0; j <= y.length(); j++) {
                if (i == 0) {
                    dp[i][j] = j;
                }
                else if (j == 0) {
                    dp[i][j] = i;
                }
                else {
                    dp[i][j] = min(dp[i - 1][j - 1] 
                                   + costOfSubstitution(x.charAt(i - 1), y.charAt(j - 1)), 
                                   dp[i - 1][j] + 1, 
                                   dp[i][j - 1] + 1);
                }
            }
        }
        return dp[x.length()][y.length()];
    }

    private static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    private static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
    }
	
}