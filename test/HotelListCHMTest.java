import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class HotelListCHMTest {
    private Hotel h0, h1, h2;
    private HotelListCHM hotels;

    @Before
    public void setup(){
        ArrayList<String> services = new ArrayList<>();
        services.add("TV in camera");
        services.add("Palestra");
        services.add("Cancellazione gratuita");

        Ratings ratings = new Ratings(0, 0, 0, 0);
        h0 = new Hotel(1, "Hotel Test 0", "TestDescription", "A", "347-4453634", services, 0, ratings);


        ArrayList<String> services1 = new ArrayList<>();
        services1.add("TV in camera");
        services1.add("Palestra");
        services1.add("Cancellazione gratuita");

        Ratings ratings1 = new Ratings(0, 0, 0, 0);
        h1 = new Hotel(1, "Hotel Test 1", "TestDescription1", "A", "347-4453634", services1, 0, ratings1);


        ArrayList<String> services2 = new ArrayList<>();
        services2.add("TV in camera");
        services2.add("Palestra");

        Ratings ratings2 = new Ratings(0, 0, 0, 0);
        h2 = new Hotel(2, "Hotel Test 2", "TestDescription2", "C", "347-4453634", services2, 0, ratings2);
    }

    @Before
    public void CHMSetup(){
        ArrayList<Hotel> hs = new ArrayList<>();
        hs.add(h0);
        hs.add(h1);
        hs.add(h2);

        hotels = new HotelListCHM();
        hotels.addAll(hs);
    }

    @Test
    public void AssertionTest(){

        assertEquals(2, hotels.searchByCity("A").size());
        assertEquals(1, hotels.searchByCity("C").size());

        assertEquals(1, hotels.searchByName("Hotel Test 1", "A").size());
    }

    @Test
    public void SerializeTest(){
        h1.insertReview(5, new Ratings(4, 4, 3, 5));
        hotels.sort();

        Gson gson = GsonFactory.get();
        String CHM_json = gson.toJson(hotels.getHotels());

        //assertEquals("", CHM_json);
        ArrayList<Hotel> fst = new ArrayList<>();
        fst.add(h1);
        fst.add(h2);
        assertEquals(fst, hotels.getFirstRanked());
    }
    
}
