import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final String HOTEL_PATH = "Hotels.json";
    private static final String USER_PATH = "Users.json";
    private static final int PORT = 9999;
    
    private static final int INIT_DELAY = 0;
    private static final int SERIALIZE_DELAY = 1;
    private static final TimeUnit UNIT = TimeUnit.MINUTES;

    private static final long REVIEW_DELTA_DAYS = 1;
    private static String GROUP_ADDRESS = "227.227.227.227";
    private static int MS_PORT = 7777;

    private static long SORT_DELTA = 10_000;
    
    private static Type hotelArrayType = new TypeToken<ArrayList<Hotel>>(){}.getType();
    private static Type userArrayType = new TypeToken<ArrayList<User>>(){}.getType();

    //TODO: constructor to inject parameters

    public static void main(String[] args) {
        Gson gson = GsonFactory.get();

        //Setup hotels and users lists
        HotelList hotels = new HotelList();
        UserList users = new UserList();

        //TODO: test if file do not exist
        try(FileReader hotelReader = new FileReader("Hotels.json");
            FileReader userReader = new FileReader("Users.json"))
        {
            hotels.addAll(gson.fromJson(hotelReader, hotelArrayType));
            users.addAll(gson.fromJson(userReader, userArrayType));
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }

        ExecutorService pool = Executors.newCachedThreadPool();
        Thread msSender = new Thread(new MulticastSender(GROUP_ADDRESS, MS_PORT, hotels, SORT_DELTA));

        //Starting the server
        try(ServerSocket serverSocket = new ServerSocket(PORT);
            DatagramSocket msSocket = new DatagramSocket();
        ){
            
            //periodically persists users and hotels data 
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
            scheduler.scheduleWithFixedDelay(new Persister<Hotel>(gson, HOTEL_PATH, hotels.getHotels()), INIT_DELAY, SERIALIZE_DELAY, UNIT);
            scheduler.scheduleWithFixedDelay(new Persister<User>(gson, USER_PATH, users.getUsers()), INIT_DELAY, SERIALIZE_DELAY, UNIT);


            System.out.println("Server is running...");

            //This thread sort HotelList and send a notification to multicast group 
            msSender.start();

            while(true){
                pool.execute(new Session(serverSocket.accept(), hotels, users, REVIEW_DELTA_DAYS));
            }
        } 
        catch(IOException e) {
            System.err.println(e.getMessage());
        }
        finally{
            msSender.interrupt();
            pool.shutdown();
        }
    }
}