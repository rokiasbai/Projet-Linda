package linda.test;

import linda.*;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;

public class MonTestCallbackIMMEDIAT {

    private static class MyCallback implements Callback {
        public void call(Tuple t) {
            try {
            	Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("début "+t);
        }
    }

    public static void main(String[] a) {
        Linda linda = new linda.shm.CentralizedLinda();
        // Linda linda = new linda.server.LindaClient("//localhost:4000/MonServeur");

        Tuple motif = new Tuple(Integer.class, String.class);
        
        Tuple t1 = new Tuple(6, "Bonjour");
        
        System.out.println("(1) write: " + t1);
        linda.write(t1); 
        //mon tuple space : [bonjour, 6] 
        
        linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, motif, new MyCallback());
        //exécution de la méthode call de MyCallback sur [Bonjour, 6]
        //mon tuple space : vide
            

    }

}
