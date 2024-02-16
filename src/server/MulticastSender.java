package src.server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import src.structures.Hotel;
import src.structures.HotelList;

//This thread periodically sort HotelList. If the first position changes, it will notify multicast group
public class MulticastSender implements Runnable {
    private String groupAddress;
    private int MS_PORT;
    private HotelList hotelList;

    public MulticastSender(String groupAddress, int port, HotelList hotels){
        this.groupAddress = groupAddress;
        this.MS_PORT = port;
        this.hotelList = hotels;
    }

    public void run(){
        System.out.println("Sorting hotels");

        ArrayList<Hotel> oldFirst, newFirst;
        oldFirst = hotelList.getFirstRanked();   //get first hotel for each local ranking (old)
        
        hotelList.sort();
        newFirst = hotelList.getFirstRanked();   //get first hotel for each local ranking (new)
        
        ArrayList<Hotel> delta = new ArrayList<>(newFirst);     //delta contains all new hotels
        delta.removeAll(oldFirst);
        
        List<String> deltaName = delta.stream()
            .map(h -> h.getName())
            .collect(Collectors.toList());
        System.out.println("New hotels: " + deltaName);
        
        
        if(!newFirst.equals(oldFirst)){
            try(DatagramSocket msSocket = new DatagramSocket()){
                String msg = "New hotel are now first ranked: " + deltaName.toString();
                byte[] buffer = msg.getBytes();
                
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(groupAddress), MS_PORT);
                msSocket.send(packet);
                
                System.out.println(msg);
            }
            catch(Exception e){
                System.err.println(e.getMessage());
            }
        }
        else{
            System.out.println("No new first hotels");
        }
    }
}
