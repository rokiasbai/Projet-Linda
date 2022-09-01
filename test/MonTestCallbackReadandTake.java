package linda.test;

import linda.*;
import linda.Linda.eventMode;
import linda.Linda.eventTiming;

public class MonTestCallbackReadandTake {

    private static class MyCallback implements Callback {
        public void call(Tuple t) {
            /*try {
            	Thread.sleep(1000);
            } catch (InterruptedException e) {
            }*/
            System.out.println("début "+t);
        }
    }

    public static void main(String[] a) {
        Linda linda = new linda.shm.CentralizedLinda();
        // Linda linda = new linda.server.LindaClient("//localhost:4000/MonServeur");

        Tuple motif = new Tuple(Integer.class, String.class);
        
        linda.eventRegister(eventMode.TAKE, eventTiming.IMMEDIATE, motif, new MyCallback());
        linda.eventRegister(eventMode.READ, eventTiming.IMMEDIATE, motif, new MyCallback());
        
        Tuple t1 = new Tuple("Bonjour", 6);
        System.out.println("(1) write: " + t1);
        linda.write(t1); 
        //linda.debug("(2)");
        //mes callbacks : [integer, string] Take MyCallback Read MyCallback  
        //mon tuple space : [bonjour, 6] 
        Tuple t2 = new Tuple("Au revoir", 15);
        System.out.println("(2) write: " + t2);
        linda.write(t2); 
        //linda.debug("(2)");
        //mes callbacks : [integer, string] Take MyCallback Read MyCallback  
        //mon tuple space : [bonjour, 6] [aurevoir 15] 
        Tuple t3 = new Tuple(1, "foo");
        System.out.println("(3) write: " + t3);
        linda.write(t3); 
        //linda.debug("(2)");
        //exécution de la méthode call (affichage string) x2
        //mon tuple space : [bonjour, 6] [aurevoir 15] 
        

    }

}
