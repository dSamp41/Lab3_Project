import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

/*
Used to serialize UserList and HotelList.
It is called from the server every *x* times
*/

public class Persister implements Runnable {
    private Gson gson;
    private HotelList hotels;
    private UserList users;

    public Persister(Gson gson, HotelList h, UserList u){
        this.gson = gson;
        this.hotels = h;
        this.users = u;
    }

    public void run(){
        System.out.println("Serializing structures...");
        String hJ = gson.toJson(hotels.getHotels());
        String uJ = gson.toJson(users.getUsers());

        try(BufferedWriter userWriter = new BufferedWriter(new FileWriter("Users.json"));
            BufferedWriter hotelsWriter = new BufferedWriter(new FileWriter("Hotels.json")))
        {
            userWriter.write(uJ);
            userWriter.flush();

            hotelsWriter.write(hJ);
            hotelsWriter.flush();
        } 
        catch(IOException e) {
            System.err.println(e.getMessage());
        }

        System.out.println("Serialization was successfull");
    }
}
