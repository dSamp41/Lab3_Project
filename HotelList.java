import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HotelList {
    private ArrayList<Hotel> hotels;

    public HotelList(){
        this.hotels = new ArrayList<>();
    }

    public ArrayList<Hotel> getHotels(){
        return this.hotels;
    }

    public void add(Hotel h){
        hotels.add(h);
    }

    public void addAll(ArrayList<Hotel> h){
        hotels.addAll(h);
    }

    public List<Hotel> searchByCity(String city){
        Predicate<Hotel> p = h -> (h.getCity().equals(city));

        return hotels.stream().filter(p).collect(Collectors.toList());
    }

    public List<Hotel> searchByName(String name, String city){
        Predicate<Hotel> p = h -> (h.getName().equals(name) && h.getCity().equals(city));
        
        return hotels.stream().filter(p).collect(Collectors.toList());
    }

    //TODO: sorting
    /*
     * city
     * rate
     * ratings (avg?)
     * num of services
     * name
     */
    
}
