package src.server;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import src.structures.Hotel;
import src.structures.HotelList;
import src.structures.User;
import src.structures.UserList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Properties;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static String HOTEL_PATH;
    private static String USER_PATH;
    private static int PORT;
    
    private static final int INIT_DELAY = 0;
    
    private static int SERIALIZE_HOTEL_DELAY_MINS;
    private static int SERIALIZE_USER_DELAY_MINS;
    private static final TimeUnit UNIT = TimeUnit.MINUTES;

    private static long REVIEW_DELTA_DAYS;
    private static String GROUP_ADDRESS;
    private static int MS_PORT;

    private static long SORT_DELTA_MILLS;
    
    private static Type hotelArrayType = new TypeToken<ConcurrentHashMap<String, CopyOnWriteArrayList<Hotel>>>(){}.getType();//new TypeToken<ArrayList<Hotel>>(){}.getType();
    private static Type userArrayType = new TypeToken<SortedMap<String, User>>(){}.getType();

    public void start() {
        Gson gson = GsonFactory.get();

        //Setup hotels and users lists
        HotelList hotels = new HotelList();
        UserList users = new UserList();

        try(BufferedReader hotelReader = new BufferedReader(new FileReader(HOTEL_PATH));
            BufferedReader userReader = new BufferedReader(new FileReader(USER_PATH)))
        {
            hotels.addAll(gson.fromJson(hotelReader, hotelArrayType));
            users.addAll(gson.fromJson(userReader, userArrayType));
        }
        catch(IOException e){
            System.err.println(e.getMessage());
        }

        ExecutorService pool = Executors.newCachedThreadPool();
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);

        //Starting the server
        try(ServerSocket serverSocket = new ServerSocket(PORT);
            DatagramSocket msSocket = new DatagramSocket();
        ){
            //periodically persists users and hotels data + sort and multicast notification
            Runnable hotelPersister = new Persister<>(gson, HOTEL_PATH, hotels.getHotels());
            scheduler.scheduleWithFixedDelay(hotelPersister, INIT_DELAY, SERIALIZE_HOTEL_DELAY_MINS, UNIT);

            Runnable userPersister = new Persister<>(gson, USER_PATH, users.getUsers());
            scheduler.scheduleWithFixedDelay(userPersister, INIT_DELAY, SERIALIZE_USER_DELAY_MINS, UNIT);

            //This task sort HotelList and send a notification to multicast group 
            Runnable sorter = new MulticastSender(GROUP_ADDRESS, MS_PORT, hotels);
            scheduler.scheduleWithFixedDelay(sorter, INIT_DELAY, SORT_DELTA_MILLS, TimeUnit.MILLISECONDS);

            //Final operation when the server is interrupted: no new client, serialize structures
            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                @Override
                public void run()
                {
                    System.out.println("Shutting down");
                    pool.close();
                    pool.shutdown();

                    scheduler.schedule(hotelPersister, INIT_DELAY, UNIT);
                    scheduler.schedule(userPersister, INIT_DELAY, UNIT);
                    scheduler.close();
                    scheduler.shutdown();
                }
            });


            System.out.println("Server is running...");

            while(true){
                pool.execute(new Session(serverSocket.accept(), hotels, users, REVIEW_DELTA_DAYS));
            }            
        } 
        catch(IOException e) {
            System.err.println(e.getMessage());
        }
        finally{
            scheduler.shutdown();
            pool.shutdown();
        }
    }

    public void readConfig(String configPath) {
        try {
            BufferedInputStream input = new BufferedInputStream(new FileInputStream(configPath));
        
            Properties prop = new Properties();
            prop.load(input);

            PORT = Integer.parseInt(prop.getProperty("PORT"));
            GROUP_ADDRESS = prop.getProperty("GROUP_ADDRESS");
            MS_PORT = Integer.parseInt(prop.getProperty("MS_PORT"));
            HOTEL_PATH = prop.getProperty("HOTEL_PATH");
            USER_PATH = prop.getProperty("USER_PATH");

            SORT_DELTA_MILLS = Long.parseLong(prop.getProperty("SORT_DELTA_MILLS"));
            SERIALIZE_HOTEL_DELAY_MINS = Integer.parseInt(prop.getProperty("SERIALIZE_HOTEL_DELAY_MINS"));
            SERIALIZE_USER_DELAY_MINS = Integer.parseInt(prop.getProperty("SERIALIZE_USER_DELAY_MINS"));
            REVIEW_DELTA_DAYS = Long.parseLong(prop.getProperty("REVIEW_DELTA_DAYS"));
        } 
        catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        }        
    }
}