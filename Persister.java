import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;

/*
Used to serialize UserList and HotelList.
It is called from the server every *x* times
*/

public class Persister<T> implements Runnable {
    private Gson gson;
    private String path;
    private T arr;

    public Persister(Gson gson, String path, T arr){
        this.gson = gson;
        this.path = path;
        this.arr = arr;
    }

    public void run(){
        //TODO: remove s
        String s = "";
        if(path.contains("Users")) s = "<Users> ";
        if(path.contains("Hotels")) s = "<Hotels> ";
        System.out.println(s + "Serializing structures... " );
        
        String arrJson = gson.toJson(arr);

        try(BufferedWriter arrWriter = new BufferedWriter(new FileWriter(path)))
        {
            arrWriter.write(arrJson);
            arrWriter.flush();
        } 
        catch(IOException e) {
            System.err.println("Error during serialization");
            System.err.println(e.getMessage());
        }
        catch(Exception e){
            System.err.println(e.getMessage());
        }

        System.out.println("Serialization was successful");
    }
}
