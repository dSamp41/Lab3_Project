package src.client;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.MulticastSocket;
import java.net.SocketTimeoutException;

public class MulticastReceiver implements Runnable {
    private MulticastSocket ms;
    private Object consoleLock;
    
    public MulticastReceiver(MulticastSocket ms, Object cl){
        this.ms = ms;
        this.consoleLock = cl;
    }

    public void run(){
        // Buffer for incoming packets
        byte[] buffer = new byte[1024];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        
        // Continuously receive new packets
        while(true){
            try{
                ms.receive(packet);  // Receive packet from multicast group
                String message = new String(packet.getData(), 0, packet.getLength());
                
                synchronized(consoleLock){
                    System.out.println("Multicast: " + message);
                }                
            }
            catch(SocketTimeoutException e){
                //System.err.println("No msg sent");
            }
            catch(IOException e){
                System.err.println(e.getMessage());
            }
        }
    } 
}
