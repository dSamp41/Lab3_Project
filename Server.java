import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.*;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final int PORT = 9999;
    private static final int DELAY = 1;
    private static final TimeUnit UNIT = TimeUnit.MINUTES;
    
    private static Type hotelArrayType = new TypeToken<ArrayList<Hotel>>(){}.getType();
    private static Type userArrayType = new TypeToken<ArrayList<User>>(){}.getType();
    
    public static void main(String[] args) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        HotelList hotels = new HotelList();
        UserList users = new UserList();

        try(FileReader hotelReader = new FileReader("Hotels.json");
            FileReader userReader = new FileReader("Users.json"))
        {
            hotels.addAll(gson.fromJson(hotelReader, hotelArrayType));
            users.addAll(gson.fromJson(userReader, userArrayType));
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }

        //server
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            ExecutorService pool = Executors.newCachedThreadPool();

            //periodically persits users and hotels data 
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.schedule(new Persister(gson, hotels, users), DELAY, UNIT);
            
            System.out.println("Server is running...");

            while(true){
                pool.execute(new Session(serverSocket.accept(), hotels, users));
            }
        } 
        catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
    
}