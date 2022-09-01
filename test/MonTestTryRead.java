package linda.test;

import linda.*;

public class MonTestTryRead {

    public static void main(String[] a) {
        final Linda linda = new linda.shm.CentralizedLinda();
        //              final Linda linda = new linda.server.LindaClient("//localhost:4000/MonServeur");
           
        for (int i = 0; i <= 1; i++) {
            final int j = i;
            new Thread() {  
                public void run() {
                    try {
                        Thread.sleep(j*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Tuple motif = new Tuple(Integer.class, String.class);
                    Tuple res = linda.tryRead(motif);
                    System.out.println("("+j+") Resultat:" + res);
                    linda.debug("("+j+")");
                }
            }.start();
        }
        
        new Thread() {
            public void run() {
            	
            	try {
                    Thread.sleep(30);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            	
                Tuple t2 = new Tuple(1, "hello");
                System.out.println("write: " + t2);
                linda.write(t2);
                linda.debug("(0)");
                
                Tuple motif = new Tuple(Integer.class, String.class);
                Tuple res = linda.read(motif);//ca passe et montre qu'on a enregistré le tuple dans l'espace partagé
                System.out.println("Resultat:" + res);
                linda.debug("(1)");
                
                
                Tuple res1 = linda.take(motif);//ca passe parce que tryRead de la boucle lit seulement le motif et ne l'enlève pas  
                System.out.println("Resultat:" + res1);
                linda.debug("(1)");
                
                Tuple res3 = linda.read(motif);//ca passe pas parce que tuplespace vide et read bloquant
                System.out.println("Resultat:" + res3);
                linda.debug("(1)");
            }
        }.start();
                
    }
}