package src.client;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Properties;

public class Client {
    private static String SERVER_IP;
    private static int PORT;
    
    private static String GROUP_ADDRESS;
    private static int MS_PORT;
    
    private static final Object consoleLock = new Object();
    
    public void start() {
        try(Socket socket = new Socket(SERVER_IP, PORT);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
            MulticastSocket msSocket = new MulticastSocket(MS_PORT)
        ){
            msSocket.joinGroup(InetAddress.getByName(GROUP_ADDRESS));
            Thread msReceiver = new Thread(new MulticastReceiver(msSocket, consoleLock));

            String serverRsp, userReq;
            boolean loggedIn = false;

            System.out.println(getHelpMessage(false));

            while(true){
                synchronized(consoleLock){      //use synchronized to avoid conflict in printing something received in multicast
                    System.out.println("\nInsert command: ");
                    userReq = userInput.readLine();
                }

                if(userReq.equals("help")){
                    System.out.println(getHelpMessage(loggedIn));
                    continue;
                }

                if(userReq.equals("exit")){
                    System.out.println("Bye bye...");
                    System.exit(0);     //closes also msReceiver
                }
                
                if(socket.isClosed()){      //check if the server is down before sending something
                    System.out.println("The server is down. Disconetting...");
                    break;
                }

                toServer.println(userReq);

                serverRsp = fromServer.readLine();
                if(serverRsp == null){
                    System.out.println("The server is down. Disconetting...");
                    break;
                }
                
                serverRsp = serverRsp.replace("^", "\n");
                System.out.println("Server: " + serverRsp);

                if(serverRsp.equals("Successfully logged in")){     //User is logged in; can receive multicast notifications
                    loggedIn = true;

                    //Start multicast sniffer
                    msReceiver.start();
                    System.out.println("You can now receive udpate about new ranking");
                }

                if(serverRsp.equals("Logout successful")){     //User is logged out; can't receive multicast notifications
                    loggedIn = false;
                    msReceiver.interrupt();
                }
            }
            
            //if client exits without logout, force msReceiver interruption
            if(msReceiver.isAlive()){
                msReceiver.interrupt();
            }
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }
    }

    private static String getHelpMessage(boolean isLoggedIn){
        String baseString = "Welcome, these are your actions: help, exit, searchAllHotels, searchHotel, ";
        String notLoggedInActions = "register, login";
        String loggedInActions = "showBadge, insertReview, logout";

        String helpString = baseString;

        if(isLoggedIn == false){
            helpString += notLoggedInActions;
        }
        else{
            helpString += loggedInActions;
        }

        return helpString;
    }

    public void readConfig(String configPath) {
        try(BufferedInputStream input = new BufferedInputStream(new FileInputStream(configPath))) 
        {        
            Properties prop = new Properties();
            prop.load(input);

            SERVER_IP = prop.getProperty("SERVER_IP");
            PORT = Integer.parseInt(prop.getProperty("PORT"));
            GROUP_ADDRESS = prop.getProperty("GROUP_ADDRESS");
            MS_PORT = Integer.parseInt(prop.getProperty("MS_PORT"));
        }
        catch(FileNotFoundException e){
            System.err.println("Config file not found");
            System.exit(-1);
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }        
    }
}