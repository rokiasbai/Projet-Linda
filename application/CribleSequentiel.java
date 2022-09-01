package linda.application;
import linda.*;

public class CribleSequentiel {
	 public static void main(String[] a) {
	final Linda linda = new linda.shm.CentralizedLinda();
	final int MAX = 10;
	boolean[] array = new boolean[MAX];
	//On parcourt tous les nombres qui sont inférieurs à MAX
	for (int i = 2; i < MAX; i++) {
	// Si le nombre n'est pas éliminé alors il est premier
	  if (!array[i]) {
	    int j = i + i;
	    //On élimine tous les multiples du nombre premier 
		if ((!array[i]) && (i < Math.floor(MAX/2))) {
	      array[j] = true;
	      j += i;
	    }
	  }
	}
	for (int i=2;i<array.length;i++) {
       // On rajoute dans l'espace partagé les nombres premiers avec un false si ils sont premiers et un true si ils ne sont pas premiers
		Tuple t = new Tuple(i, array[i]);
		System.out.println(" write: " + t);
		linda.write(t);
	}
}
}
