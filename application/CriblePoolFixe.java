
import linda.*;
import java. util . concurrent .*;

public class CriblePoolFixe {
	 public static void main(String[] a) throws Exception { 
	final Linda linda = new linda.shm.CentralizedLinda();
	final int MAX = 10;
	boolean[] array = new boolean[MAX];
	//Nombre fixe d'ouvriers
	ExecutorService exec = Executors.newFixedThreadPool(2);
	Future<?>[] res = new Future<?>[MAX];
	//On parcourt tous les nombres qui sont inf�rieurs � MAX
	for (int i = 2; i < MAX; i++) {
		int l =i;
	res[i] =exec.submit(new Callable<Integer>() {
				public Integer call () {
		          { // Si le nombre n'est pas �limin� alors il est premier
		        	  if (!array[l] && l< Math.floor(MAX/2)) {
						    int j = l + l;
						    //On �limine tous les multiples du nombre premier 
						    while (j < MAX) {
						      array[j] = true;
						      j += l;
						    }
					  }
				  }
				return l;
		        }
			});
	
	}
	//On interdit la soumission de nouvelles tches
	exec.shutdown();
	//On r�cup�re les r�sultats
	for (int i=2;i<array.length;i++) {
	  // On rajoute dans l'espace partag� les nombres premiers avec un false si ils sont premiers et un true si ils ne sont pas premiers
		//elle bloque en attente du r�sultat
		res [ i ]. get ();
		Tuple t = new Tuple(i, array[i]);
		System.out.println(" write: " + t);
		linda.write(t);
	}
}
}
