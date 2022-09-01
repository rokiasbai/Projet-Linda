
import java. util . concurrent .*;

import linda.Linda;
import linda.Tuple;

public class CribleExecutorExec{
	
		public static void main(String [] a) throws Exception { 
			final Linda linda = new linda.shm.CentralizedLinda();
			final int MAX = 10;
			boolean[] array = new boolean[MAX];
		ExecutorService exec = Executors.newCachedThreadPool();
		for (int i = 2; i < MAX; i++) { // lancement des travaux
		 // Si le nombre n'est pas éliminé alors il est premier
			  if (!array[i]) {
				 // On écrit le i parce qu'on est sur qu'il est premier
			    int j = i + i;
			    Tuple t = new Tuple(i, array[i]);
				System.out.println(" write: " + t);
				linda.write(t);
			    //On élimine tous les multiples du nombre premier 
				//On paralèllise la recherche des multiples des nombres premiers donc le nombre  de thread lancés dépend des nombres premiers présents
				 int l =i;
	         	exec.execute(new Runnable() {
		                      public void run() { 
		 		             	int k =j;
						        while (k < MAX) {
								    //On l'écrit parce qu'on est sur qu'il n'est pas premier et qu'il n'était pas écrit avant 
									  if ((!array[k] && l< Math.floor(MAX/2))) {
									   array[k] = true;
								      Tuple t1 = new Tuple(k, array[k]);
									  System.out.println(" write: " + t1);
									  linda.write(t1);					      
									  }
								  array[k] = true;
								  k += l;
						        }
				        	  }
		                     });
		

               }
		}
		}
}
