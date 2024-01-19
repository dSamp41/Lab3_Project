import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HotelList {
    private ArrayList<Hotel> hotels;

    //Sorting
    /*
    * city (incr)
    * rate (decr)
    * avg ratings (decr)
    * num of services (decr)
    * name (incr)
    */

    Comparator<Hotel> hotelComparator = Comparator
        .comparing(Hotel::getCity).reversed()
        .thenComparing(Hotel::getRate).reversed()
        .thenComparing(Hotel::getRatingsAvg).reversed()
        .thenComparing(Hotel::getNumServices).reversed()
        .thenComparing(Hotel::getName);

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


    public ArrayList<Hotel> getFirstRanked(){
        ArrayList<Hotel> res = new ArrayList<>();
        ArrayList<Hotel> sortedHotels = getSorted();

        while(!sortedHotels.isEmpty()){
            Hotel toAdd = sortedHotels.get(0);
            res.add(toAdd);

            Predicate<Hotel> p = h -> (h.getCity().equals(toAdd.getCity()));
            sortedHotels.removeIf(p);
        }

        return res;
    }

    //sort and returns clone of this.hotels
    public ArrayList<Hotel> getSorted(){
        ArrayList<Hotel> clonedHotels = new ArrayList<Hotel>(this.hotels);
        Collections.sort(clonedHotels, hotelComparator);
        return clonedHotels;
    }

    public void sort(){
        Collections.sort(this.hotels, hotelComparator);
    }
}
