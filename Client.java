import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

//split main in ClientMain to improve testability
public class Client {
    private static final int PORT = 9999;
    
    public static void main(String[] args) {

        try(Socket socket = new Socket("localhost", PORT);
            BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter toServer = new PrintWriter(socket.getOutputStream(), true);
        ){
            String serverRsp, msMsg, userReq;

            System.out.println("Welcome, these are your actions: register, login, searchAllHotels, searchHotel, showBadge, logout, insertReview");

            while(true){
                System.out.println("\nInsert text: ");
                userReq = userInput.readLine();
                toServer.println(userReq);

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

                msMsg = fromServer.readLine();
                if(msMsg != null){
                    System.out.println(msMsg);
                }
            }
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }
        
    }    
}
