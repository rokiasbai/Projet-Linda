package linda.test;

import linda.*;

public class MonTestReadTake {

    public static void main(String[] a) {
                
        final Linda linda = new linda.shm.CentralizedLinda();
        //final Linda linda = new linda.server.LindaClient("//localhost:4000/aaa");
                
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Tuple t0 = new Tuple(1000, "MonTest");
                System.out.println(" write:" + t0);
                linda.write(t0);
                Tuple t10 = new Tuple(2000, "MonTest2");
                System.out.println(" write:" + t10);
                linda.write(t10);
                Tuple motif = new Tuple(Integer.class, String.class);
                
            }
        }.start();
       
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple motif = new Tuple(Integer.class, String.class);
                
                Tuple res = linda.take(motif);//on enleve le premier tuple qui correspond il en reste 1
                System.out.println("Resultat1:" + res);
                linda.debug("(1)");
                
                Tuple res2 = linda.take(motif);//on enleve le dernier il n'en reste plus
                System.out.println("Resultat2:" + res2);
                linda.debug("(1)");
                
                Tuple res3 = linda.take(motif);//ca ne passe plus car plus de tuples dans le tuplespace
                System.out.println("Resultat3:" + res3);
                linda.debug("(1)");
               
                
            }
        }.start();
        
        
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Tuple motif = new Tuple(Integer.class, String.class);
                
                Tuple t10 = new Tuple(3000, "MonTest3");
                System.out.println(" write:" + t10);
                linda.write(t10);
                Tuple res4 = linda.take(motif);//ca ne passe pas plus de tuples
                System.out.println("Resultat4:" + res4);
                linda.debug("(1)");
                
            }
        }.start();
        
                
    }
}
