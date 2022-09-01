package linda.shm;
import linda.Callback;
import linda.Linda;
import linda.Tuple;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
//import Synchro.Assert;
import java.util.concurrent.locks.Condition;

/** Shared memory implementation of Linda. */
public class CentralizedLinda implements Linda {

    private Lock mon_lock; 
	private Condition attente;
    private ArrayList <Tuple> tuplespace; //mon espace de tuple
    private Map<Tuple, Map<eventMode, Callback>> mescallbacks = new HashMap<>();// mes callbakcs

    
    public CentralizedLinda() {
    	
        this.mon_lock = new ReentrantLock();
    	this.attente = mon_lock.newCondition();
        this.tuplespace = new ArrayList<Tuple>();
    }

    /** Adds a tuple t to the tuplespace. */
    public void write(Tuple t){
    	//sans callbacks
        /*mon_lock.lock();
        Tuple copie =t.deepclone();
        tuplespace.add(copie);
    	attente.signalAll();   	
       // ajouter copie dans l'espace partagé
        mon_lock.unlock();*/
    	//avec callbacks
    	mon_lock.lock();
    	boolean take = false;
    	ArrayList<Callback> lancer = new ArrayList<Callback>();
    	ArrayList<Tuple> tcalls = new ArrayList<Tuple>();
        for (Tuple tcall : mescallbacks.keySet()) {
        	if (t.matches(tcall)) {
        		for (HashMap.Entry<eventMode, Callback> entry :(mescallbacks.get(tcall)).entrySet()){      		
        			lancer.add(entry.getValue());
        			tcalls.add(t);
        			if (entry.getKey() == eventMode.TAKE) {
        				take=true;//pas de clonage 
        			}
        		}
        		mescallbacks.remove(tcall);
        	}
        }
        for (int i = 0; i < lancer.size(); i++) {
        	//exécuter tous les callbacks qui viennent d'etre rajoutés avec les bons tuples 
			lancer.get(i).call(tcalls.get(i));
		}
        if (!take) { //read
        	attente.signalAll();
        	Tuple copie =t.deepclone();
            tuplespace.add(copie);
        }
        mon_lock.unlock();
    }
    
    //fonction privée rajouté pour vérifier qu'un tuple existe dans le tuplespace
    private Tuple exist(Tuple tuple) {
    	  int j=-1;
    	  Tuple copie =null;
    	  for(int i =0; i<tuplespace.size(); i++){
          	if ((tuplespace.get(i)).matches(tuple)) {
          		//s'il y a un match alors le stocker dans le Tuple copie
          		j=i; 
          		copie =tuplespace.get(j);
          	}
          }
    	  return copie;
    }
    /** Returns a tuple matching the template and removes it from the tuplespace.
     * Blocks if no corresponding tuple is found. */
    public Tuple take(Tuple template){ 
    	mon_lock.lock();
    	Tuple resultat = null;
        while (exist(template)==null) {
        	try {
				attente.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        }
        resultat = (exist(template)).deepclone();
        tuplespace.remove(exist(template));
        mon_lock.unlock();
        return resultat;
    }
  

    /** Returns a tuple matching the template and leaves it in the tuplespace.
     * Blocks if no corresponding tuple is found. */
    public Tuple read(Tuple template) {
    	mon_lock.lock();
    	Tuple resultat = null;
        while (exist(template)==null) {
        	try {
				attente.await();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        //Signaler aux autres threads qui veulent lire la fin de la lecture
        resultat = (exist(template)).deepclone();
        mon_lock.unlock();
        return resultat;
    }

    /** Returns a tuple matching the template and removes it from the tuplespace.
     * Returns null if none found. */
    public Tuple tryTake(Tuple template){
    	mon_lock.lock();
        Tuple resultat = null; 
        if (!((exist(template)) == null)) {
        resultat = (exist(template)).deepclone();	
        tuplespace.remove(resultat);
        }
        mon_lock.unlock();
        return resultat;
    }

    /** Returns a tuple matching the template and leaves it in the tuplespace.
     * Returns null if none found. */
    public Tuple tryRead(Tuple template){
    	mon_lock.lock();
        Tuple resultat =null;
        if (!(exist(template)==null)) {
        	resultat =exist(template).deepclone();
        }
        mon_lock.unlock();
        return resultat;
    }
    /** Returns all the tuples matching the template and removes them from the tuplespace.
     * Returns an empty collection if none found (never blocks).
     * Note: there is no atomicity or consistency constraints between takeAll and other methods;
     * for instance two concurrent takeAll with similar templates may split the tuples between the two results.
     */
    public Collection <Tuple> takeAll(Tuple template){
    	mon_lock.lock();
    	ArrayList <Tuple> tupleresultat = new ArrayList<Tuple>(tuplespace.size());
        
        Iterator<Tuple> iter = tuplespace.iterator();
        Tuple tu=new Tuple();
        while(iter.hasNext()) {
        	tu = (Tuple) iter.next();
        	if (tu.matches(template)) {
        		iter.remove();
        		tupleresultat.add(tu);
        	}
        }
        mon_lock.unlock();
        return tupleresultat;
        
    }

    /** Returns all the tuples matching the template and leaves them in the tuplespace.
     * Returns an empty collection if none found (never blocks).
     * Note: there is no atomicity or consistency constraints between readAll and other methods;
     * for instance (write([1]);write([2])) || readAll([?Integer]) may return only [2].
     */
    public Collection <Tuple> readAll(Tuple template){
    	 mon_lock.lock();
         ArrayList <Tuple> tupleresultat = new ArrayList<Tuple>(tuplespace.size());
         for(int i =0; i<tuplespace.size(); i++){
             if (tuplespace.get(i).matches(template)) {
                 tupleresultat.add((tuplespace.get(i)).deepclone());
             }
         }
         mon_lock.unlock();
         return tupleresultat;
    }
   
  
    /** Registers a callback which will be called when a tuple matching the template appears.
     * If the mode is Take, the found tuple is removed from the tuplespace.
     * The callback is fired once. It may re-register itself if necessary.
     * If timing is immediate, the callback may immediately fire if a matching tuple is already present; if timing is future, current tuples are ignored.
     * Beware: a callback should never block as the calling context may be the one of the writer (see also {@link AsynchronousCallback} class).
     * Callbacks are not ordered: if more than one may be fired, the chosen one is arbitrary.
     * Beware of loop with a READ/IMMEDIATE re-registering callback !
     *
     * @param mode read or take mode.
     * @param timing (potentially) immediate or only future firing.
     * @param template the filtering template.
     * @param callback the callback to call if a matching tuple appears.
     */
    public void eventRegister(eventMode mode, eventTiming timing, Tuple template, Callback callback){
    	Tuple res = new Tuple();
        if (timing==eventTiming.IMMEDIATE) {
        	if(mode == eventMode.TAKE) {
        		res=tryTake(template);
        	}
    		else {
    			res=tryRead(template);
    		}
        }
        if (res!=null) { //si take ou read renvoie quelquechose alors exécuter call de Callback
        	callback.call(res);
        }
        else { //eventTiming.FUTURE
        	//ajouter à mes callbacks
        	if(mescallbacks.containsKey(template))   {
        		(mescallbacks.get(template)).put(mode, callback);
        	}
        	else {
        		HashMap<eventMode, Callback> val = new HashMap<>();
        		val.put(mode, callback);
        		mescallbacks.put(template,val);
        	}
        }
    }

    public void debug(String prefix) {
    	//affichage du contenu de mon espace de tuple
        for (Tuple t : tuplespace) {
            System.out.println("tuple space contient : "+t);
        }
        //affichage du contenu de mes callbacks
        for (Tuple t : mescallbacks.keySet().toArray(new Tuple[0])) {
			System.out.println("# " + t + " --> " + mescallbacks.get(t));
		}
    }

}