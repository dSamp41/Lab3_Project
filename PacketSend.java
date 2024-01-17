import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class PacketSend implements Runnable {
    private String groupAddress = "227.227.227.227";
    private int MS_PORT = 7777;

    //TODO: this thread periodically sort HotelList. If the first position changes, it will notify multicast group
    public PacketSend(String groupAddress, int port){
        this.groupAddress = groupAddress;
        this.MS_PORT = port;

    }

    public void run(){
        Random random = new Random();
        int randN;
        

        try(DatagramSocket msSocket = new DatagramSocket();){
            
            while(true){
                /*
                 * oldFirst = hotelList.getFirstRanked();   //ottieni primi in ranking locali
                 * hotelList.sort();
                 * 
                 * if(hotelList.getFirstRanked().equals(oldFirst) == false){
                 *      send packet();
                 * }
                 * 
                 * Thread.sleep(DELTA)
                 * 
                 * 
                */
                
                /*randN = random.nextInt(10);
                System.out.println("\tRandom num: " + randN);        
                    
                if((randN <= 5)){
                _
                
                    String msg = "MULTICASTIN' TIME BABY!!";
                    byte[] buffer = msg.getBytes();
                    
                    System.out.println(msg);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(groupAddress), MS_PORT);
                    msSocket.send(packet);
                _
                }

                if(randN == 1000){
                    break;
                }*/

                Thread.sleep(10000);
            }
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
        
    }
    
}
