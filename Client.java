import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

//TODO: multicast only to logged clients

//split main in ClientMain to improve testability
public class Client {
    private static final int PORT = 9999;
    private static final Object consoleLock = new Object();

    private static String GROUP_ADDRESS = "227.227.227.227";
    private static final int MS_PORT = 7777;
    
    public static void main(String[] args) {

        try(Socket socket = new Socket("localhost", PORT);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
            MulticastSocket msSocket = new MulticastSocket(MS_PORT)
        ){
            String serverRsp, userReq;

            System.out.println("Welcome, these are your actions: register, login, searchAllHotels, searchHotel, showBadge, logout, insertReview");

            //Start multicast sniffer
            msSocket.joinGroup(InetAddress.getByName(GROUP_ADDRESS));
            Thread msSniffer = new Thread(new MulticastSniffer(msSocket, consoleLock));
            msSniffer.start();
            

            while(true){
                synchronized(consoleLock){
                    System.out.println("\nInsert text: ");
                    userReq = userInput.readLine();
                    toServer.println(userReq);
                }

                serverRsp = fromServer.readLine();
                if(serverRsp == null){
                    System.out.println("The server is down. Disconetting...");
                    break;
                }
                
                serverRsp = serverRsp.replace("^", "\n");
                System.out.println("Server: " + serverRsp);
                if(serverRsp.equals("Logout successful")){  //TODO: fix client logout closing phase 
                    break;
                }
            }
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }        
    }    
}