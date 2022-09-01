package linda.test;

import java.util.ArrayList;
import java.util.Collection;

import linda.*;

public class MonTestReadAll {

    public static void main(String[] a) {
        final Linda linda = new linda.shm.CentralizedLinda();
        //final Linda linda = new linda.server.LindaClient("//localhost:4000/MonServeur");
                                
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                	
                Tuple t2 = new Tuple(1, "hello");
                System.out.println("write: " + t2);
                linda.write(t2);
                
                Tuple t5 = new Tuple(2, "bye");
                System.out.println("write: " + t5);
                linda.write(t5);
                
                
                Tuple t6 = new Tuple(3, "re");
                System.out.println("write: " + t6);
                linda.write(t6);
                
                
                Tuple motif = new Tuple(Integer.class, String.class);
                             	
                
                Collection <Tuple> res1 =linda.readAll(motif);//ca passe
                System.out.println("Tout:" + res1);
                linda.debug("(1)");
                                
                Tuple res2 = linda.take(motif);//ca passe 
                System.out.println("Resultat:" + res2);
                linda.debug("(1)");
                
                Tuple res3 = linda.take(motif);//ca passe 
                System.out.println("Resultat:" + res3);
                linda.debug("(1)");
                
                Tuple res4 = linda.take(motif);//ca passe 
                System.out.println("Resultat:" + res4);
                linda.debug("(1)");
                
            }
        }.start();
                
    }
}
