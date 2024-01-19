import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.*;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private static final int PORT = 9999;
    
    private static final int INIT_DELAY = 0;
    private static final int DELAY = 1;
    private static final TimeUnit UNIT = TimeUnit.MINUTES;

    private static final long REVIEW_DELTA_DAYS = 10;
    private static String GROUP_ADDRESS = "227.227.227.227";
    private static int MS_PORT = 7777;

    private static long SORT_DELTA = 10_000;
    
    private static Type hotelArrayType = new TypeToken<ArrayList<Hotel>>(){}.getType();
    private static Type userArrayType = new TypeToken<ArrayList<User>>(){}.getType();
   
    //TODO: gruppo multicast
    //TODO: riordinamento hotelList + notifica

    public static void main(String[] args) {

        //https://stackoverflow.com/questions/39192945/serialize-java-8-localdate-as-yyyy-mm-dd-with-gson
        class LocalDateAdapter implements JsonSerializer<LocalDate> {
            public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
            }
        }

        //https://stackoverflow.com/questions/51183967/deserialize-date-attribute-of-json-into-localdate
        Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                @Override
                public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
                }
            })
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

        //Setup hotels and users lists
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

        //Starting the server
        try(ServerSocket serverSocket = new ServerSocket(PORT);
            DatagramSocket msSocket = new DatagramSocket();
        ){
            ExecutorService pool = Executors.newCachedThreadPool();

            //periodically persists users and hotels data 
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            scheduler.scheduleWithFixedDelay(new Persister(gson, hotels, users), INIT_DELAY, DELAY, UNIT);
            
            System.out.println("Server is running...");

            //This thread sort HotelList and send a notification to multicast group 
            Thread packetSend = new Thread(new PacketSend(GROUP_ADDRESS, MS_PORT, hotels, SORT_DELTA));     //TODO: add HotelList and DELTA_SORTING_TIME
            packetSend.start();

            while(true){
                pool.execute(new Session(serverSocket.accept(), hotels, users, REVIEW_DELTA_DAYS, GROUP_ADDRESS, MS_PORT));
            }
        } 
        catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
}