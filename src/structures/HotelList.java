package src.structures;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HotelList {
    private ConcurrentHashMap<String, CopyOnWriteArrayList<Hotel>> hotels;

    //Sorting
    /*
    * score (decr)
    * num of services (decr)
    * name (incr)
    */

    Comparator<Hotel> hotelComparator = Comparator
        .comparing(Hotel::getScore)
        .thenComparing(Hotel::getNumServices).reversed()
        .thenComparing(Hotel::getName);

    public HotelList(){
        this.hotels = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, CopyOnWriteArrayList<Hotel>> getHotels(){
        return hotels;
    }

    public void add(Hotel h){
        hotels.compute(h.getCity(), (key, arr) -> {
            // If the key is not present, create a new ArrayList and add the hotel
            if(arr == null) {
                CopyOnWriteArrayList<Hotel> newList = new CopyOnWriteArrayList<>();
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

    public void addAll(ConcurrentHashMap<String, CopyOnWriteArrayList<Hotel>> newHotels){
        newHotels.forEach((k, arr) -> hotels.merge(k, arr, (a1, a2) -> {
            a1.addAll(a2);
            return a1;
        }));
    }

    public Optional<List<Hotel>> searchByCity(String city){
        return Optional.ofNullable(hotels.get(city));
    }

    public Optional<List<Hotel>> searchByName(String name, String city){
        Optional<CopyOnWriteArrayList<Hotel>> hotelsInCity = Optional.ofNullable(hotels.get(city));

        if(hotelsInCity.isEmpty()){
            return Optional.empty();
        }

        Predicate<Hotel> p = h -> (h.getName().equals(name));
        return Optional.ofNullable(hotelsInCity.get().stream()
            .filter(p)
            .collect(Collectors.toList())
        );
    }

    public ArrayList<Hotel> getFirstRanked(){
        ArrayList<Hotel> res = new ArrayList<>();
        
        for(CopyOnWriteArrayList<Hotel> localRanking: hotels.values()){
            res.add(localRanking.get(0));
        }

        return res;
    }

    //sort and returns clone of this.hotels
    public ArrayList<Hotel> getSorted(){
        ArrayList<Hotel> clonedHotels = new ArrayList<Hotel>();
        
        for(CopyOnWriteArrayList<Hotel> arr: hotels.values()){
            ArrayList<Hotel> a = new ArrayList<>(arr);
            clonedHotels.addAll(a);
        }

        return clonedHotels;
    }

    public void sort(){
        for(CopyOnWriteArrayList<Hotel> arr: hotels.values())
            Collections.sort(arr, hotelComparator);
    }
}
