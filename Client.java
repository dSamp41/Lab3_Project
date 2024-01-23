import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;

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
            msSocket.joinGroup(InetAddress.getByName(GROUP_ADDRESS));
            Thread msSniffer = new Thread(new MulticastReceiver(msSocket, consoleLock));

            String serverRsp, userReq;
            boolean loggedIn = false;

            //TODO: return some actions only if logged in
            System.out.println("Welcome, these are your actions: register, login, searchAllHotels, searchHotel, showBadge, logout, insertReview");

            while(true){
                synchronized(consoleLock){
                    System.out.println("\nInsert text: ");
                    userReq = userInput.readLine();
                    
                    if(socket.isClosed()){      //check if the server is down before sending something
                        System.out.println("The server is down. Disconetting...");
                        break;
                    }

                    toServer.println(userReq);
                }

                serverRsp = fromServer.readLine();
                if(serverRsp == null){
                    System.out.println("The server is down. Disconetting...");
                    break;
                }
                
                serverRsp = serverRsp.replace("^", "\n");
                System.out.println("Server: " + serverRsp);

                if(serverRsp.equals("Successfully logged in")){     //User is logged in
                    loggedIn = true;

                    //Start multicast sniffer
                    msSniffer.start();
                    System.out.println("Started receive");
                }

                //TODO: on logout infinite "Socket closed"
                if(serverRsp.equals("Logout successful")){     //User is logged out
                    loggedIn = false;
                    msSniffer.interrupt();
                    break;
                }
            }
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }   
    }    
}