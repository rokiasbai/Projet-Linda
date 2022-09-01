package linda.test;

import linda.*;

public class MonTestTryTake {

    public static void main(String[] a) {
        final Linda linda = new linda.shm.CentralizedLinda();
        //              final Linda linda = new linda.server.LindaClient("//localhost:4000/MonServeur");
                                
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
                linda.debug("(0)");
                
                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res = linda.read(motif);//ca passe
                System.out.println("Resultat:" + res);
                linda.debug("(1)");
                
                Tuple res1 = linda.take(motif);//ca passe
                System.out.println("Resultat:" + res1);
                linda.debug("(1)");
                
                Tuple res2 = linda.tryTake(motif);//ca passe parce que tryTrake non bloquant donc 
                //le print suivant est bien affiché
                System.out.println("Resultat:" + res2);//null
                linda.debug("(1)");
                
                Tuple res3 = linda.take(motif);//ca passe pas parce que tuplespace vide et take bloquant
                System.out.println("Resultat:" + res3);
                linda.debug("(1)");
            }
        }.start();
                
    }
}
