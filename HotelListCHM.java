import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HotelListCHM {
    private ConcurrentHashMap<String, ArrayList<Hotel>> hotels;

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

    public HotelListCHM(){
        this.hotels = new ConcurrentHashMap<>();
    }

    /*public List<Hotel> getHotels(){
        Collection<ArrayList<Hotel>> hs = this.hotels.values();

        List<Hotel> flattenedList = hs.stream()
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

        return flattenedList;
    }*/

    public ConcurrentHashMap<String, ArrayList<Hotel>> getHotels(){
        return hotels;
    }

    public void add(Hotel h){
        hotels.compute(h.getCity(), (key, arr) -> {
            // If the key is not present, create a new ArrayList and add the hotel
            if(arr == null) {
                ArrayList<Hotel> newList = new ArrayList<>();
                newList.add(h);
                return newList;
            } 
            else {
                // If the key is present, add the hotel to the existing ArrayList
                arr.add(h);
                return arr;
            }
        });
        
    }

    public void addAll(List<Hotel> hotelList){
        for(Hotel h: hotelList){

            hotels.compute(h.getCity(), (key, arr) -> {
                // If the key is not present, create a new ArrayList and add the hotel
                if(arr == null) {
                    ArrayList<Hotel> newList = new ArrayList<>();
                    newList.add(h);
                    return newList;
                } 
                else {
                    // If the key is present, add the hotel to the existing ArrayList
                    arr.add(h);
                    return arr;
                }
            });
        }
    }

    public List<Hotel> searchByCity(String city){
        return hotels.get(city);
    }

    public List<Hotel> searchByName(String name, String city){
        ArrayList<Hotel> hotelsInCity = hotels.get(city);

        Predicate<Hotel> p = h -> (h.getName().equals(name));
        return hotelsInCity.stream().filter(p).collect(Collectors.toList());
    }


    public ArrayList<Hotel> getFirstRanked(){
        ArrayList<Hotel> res = new ArrayList<>();
        
        for(ArrayList<Hotel> arr: hotels.values()){
            res.add(arr.get(0));
        }

        return res;
    }

    //sort and returns clone of this.hotels
    public ArrayList<Hotel> getSorted(){
        ArrayList<Hotel> clonedHotels = new ArrayList<Hotel>();
        
        for(ArrayList<Hotel> arr: hotels.values()){
            ArrayList<Hotel> a = new ArrayList<>(arr);
            clonedHotels.addAll(a);
        }

        return clonedHotels;
    }

    public void sort(){
        for(ArrayList<Hotel> arr: hotels.values())
            Collections.sort(arr, hotelComparator);
    }
}
