package linda.search.basic;
import java. util . concurrent .*;
import linda.*;

public class Main {
    //Pour avoir plusieurs activités qui recherchent au meme temps 
    public static void main(String args[]) {
    	if ((!((args.length) % 2 == 0 )|| (args.length==0))) {
            System.err.println("linda.search.basic.Main search file.");
            return;
    	}
        Linda linda = new linda.shm.CentralizedLinda();
        //nombre des activités qui font la recherche au meme temps 
    	final int MAXsearcher = 10;
    	//Pool de threads qui gère l'execution des différentes activités qui font la recherche 
    	ExecutorService execSerch = Executors.newCachedThreadPool();
    	//Pool de threads qui gère l'execution des différentes activités qui déposent les requetes
    	ExecutorService execManag = Executors.newCachedThreadPool();
    	//Chaque manager prend 2 arguments consécutifs pour déposer plus de requetes
    	  for (int s = 0; s < args.length; s=s+2) {
    		  int l =s;
      		  execSerch.execute(new Runnable() {
              public void run() { 
              //Si on utilise la version quand le manager est un client du serveur
              //final ClientManager manager = new linda.search.basic.ClientManager("//localhost:4000/LindaServer", args[l+1], args[l]);
              //Si on utilise la version avec linda centralisée
              Manager manager = new Manager(linda, args[l+1], args[l]);
               }});
    	  }  
    	for (int j = 1; j < MAXsearcher; j++) {
    		int i =j;
    		execSerch.execute(new Runnable() {
                public void run() { 
                	//Si on utilise la version quand le searcher est un client du serveur
                	//final ClientSearcher Searcher = new linda.search.basic.ClientSearcher("//localhost:4000/LindaServer");
                	//Si on utilise la version avec linda centralisée
					Searcher searcher = new Searcher(linda);
					
		    }});
    	}
    }
}