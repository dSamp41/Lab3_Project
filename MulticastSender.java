import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//This thread periodically sort HotelList. If the first position changes, it will notify multicast group
public class MulticastSender implements Runnable {
    private String groupAddress;
    private int MS_PORT;
    private HotelList hotelList;
    private long DELTA;

    public MulticastSender(String groupAddress, int port, HotelList hotels, long delta){
        this.groupAddress = groupAddress;
        this.MS_PORT = port;
        this.hotelList = hotels;
        this.DELTA = delta;
    }

    public void run(){
        try(DatagramSocket msSocket = new DatagramSocket()){
            ArrayList<Hotel> oldFirst, newFirst;
            
            oldFirst = hotelList.getFirstRanked();   //ottieni primi in ranking locali
            
            while(true){
                Thread.sleep(DELTA);                
                    
                hotelList.sort();
                newFirst = hotelList.getFirstRanked();

                ArrayList<Hotel> delta = new ArrayList<>(newFirst);
                delta.removeAll(oldFirst);
                
                List<String> deltaName = delta.stream().map(h -> h.getName()).collect(Collectors.toList());
                System.out.println("New hotels: " + deltaName);

                System.out.println("<PacketSend thread> Sorting HotelList");

                if(!newFirst.equals(oldFirst)){
                    String msg = "New hotel are now first ranked!";
                    byte[] buffer = msg.getBytes();
                    
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(groupAddress), MS_PORT);
                    msSocket.send(packet);
                    
                    System.out.println(msg);
                    System.out.println("MULTICASTIN' TIME BABY!!");
                }
                else{
                    System.out.println("No new first hotels");
                }

                oldFirst = newFirst;
            }
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
        
    }
    
}
