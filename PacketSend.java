import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

//This thread periodically sort HotelList. If the first position changes, it will notify multicast group
public class PacketSend implements Runnable {
    private String groupAddress = "227.227.227.227";
    private int MS_PORT = 7777;
    private HotelList hotelList;
    private long DELTA;

    public PacketSend(String groupAddress, int port, HotelList hotels, long delta){
        this.groupAddress = groupAddress;
        this.MS_PORT = port;
        this.hotelList = hotels;
        this.DELTA = delta;
    }

    public void run(){
        try(DatagramSocket msSocket = new DatagramSocket()){
            ArrayList<Hotel> oldFirst, newFirst;
            
            while(true){
                Thread.sleep(DELTA);

                oldFirst = hotelList.getFirstRanked();   //ottieni primi in ranking locali
                
                hotelList.sort();
                newFirst = hotelList.getFirstRanked();

                if(newFirst.equals(oldFirst) == false){
                    String msg = "New hotel are now first ranked!";
                    byte[] buffer = msg.getBytes();
                    
                    System.out.println(msg);
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(groupAddress), MS_PORT);
                    msSocket.send(packet);
                }
            }
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
        
    }
    
}
