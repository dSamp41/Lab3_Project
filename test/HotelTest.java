import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import src.structures.Hotel;
import src.structures.HotelList;
import src.structures.Ratings;

public class HotelTest {
    private Hotel h0, h1, h2;

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
        h1 = new Hotel(1, "Hotel Test 1", "TestDescription1", "B", "347-4453634", services1, 0, ratings1);


        ArrayList<String> services2 = new ArrayList<>();
        services2.add("TV in camera");
        services2.add("Palestra");

        Ratings ratings2 = new Ratings(0, 0, 0, 0);
        h2 = new Hotel(2, "Hotel Test 2", "TestDescription2", "C", "347-4453634", services2, 0, ratings2);
    }

    @Test
    public void insertReviewTest(){

        h0.insertReview(5, new Ratings(5, 5, 5, 5));
        assertEquals(5, h0.getRate(), 0.01);
        assertEquals(5, h0.getRatings().getCleaning(), 0.01);

        h0.insertReview(1, new Ratings(1, 1, 1, 1));
        assertEquals(4.2, h0.getRate(), 0.01);
        assertEquals(4.2, h0.getRatings().getCleaning(), 0.01);

    }

    @Test
    public void dateDifferenceTest(){
        LocalDate date1 = LocalDate.of(2022, 1, 1);
        LocalDate date2 = LocalDate.of(2024, 1, 1);
        
        long daysDifference = ChronoUnit.DAYS.between(date1, date2);

        assertEquals(730, daysDifference);

        daysDifference = ChronoUnit.DAYS.between(date2, date1);
        assertEquals(-730, daysDifference);
    }

    @Test
    public void hotelListSortingTest(){
        
        HotelList hotelList = new HotelList();
        hotelList.add(h0);
        hotelList.add(h1);
        //hotelList.add(h2);
        
        h0.insertReview(5, new Ratings(5, 5, 5, 5));
        h1.insertReview(5, new Ratings(5, 5, 5, 5));
        
        hotelList.sort();
        assertEquals(h0, hotelList.searchByCity(h0.getCity()).get(0));
    }

    @Test
    public void getSortedTest(){

        HotelList hotelList = new HotelList();
        hotelList.add(h0);
        hotelList.add(h2);
        hotelList.add(h1);

        //h0.insertReview(5, new Ratings(5, 5, 5, 5));
        //h1.insertReview(4, new Ratings(5, 5, 5, 5));

        ArrayList<Hotel> cmp = new ArrayList<>();
        cmp.add(h0); cmp.add(h1); cmp.add(h2);
        
        ArrayList<Hotel> h = hotelList.getSorted();
        assertEquals(cmp, h);

        h.remove(h0);
        assertEquals(false, h.contains(h0));
        assertEquals(true, hotelList.getHotels().contains(h0));
    }

    @Test
    public void getFirstRankedTest(){

        HotelList hotelList = new HotelList();
        hotelList.add(h0);
        hotelList.add(h2);
        hotelList.add(h1);

        ArrayList<Hotel> cmp = new ArrayList<>();
        cmp.add(h0); cmp.add(h1); cmp.add(h2);

        ArrayList<Hotel> h = hotelList.getFirstRanked();
        assertEquals(cmp, h);

        assertEquals(true, cmp.equals(h));
    }

    @Test
    public void sortAndFirstTest(){
        ArrayList<String> services = new ArrayList<>();
        Ratings ratings = new Ratings(0, 0, 0, 0);
        h0 = new Hotel(1, "Hotel Test 0", "TestDescription", "A", "347-4453634", services, 0, ratings);

        ArrayList<String> services1 = new ArrayList<>();
        Ratings ratings1 = new Ratings(0, 0, 0, 0);
        h1 = new Hotel(1, "Hotel Test 1", "TestDescription1", "A", "347-4453634", services1, 0, ratings1);

        ArrayList<String> services2 = new ArrayList<>();
        Ratings ratings2 = new Ratings(0, 0, 0, 0);
        h2 = new Hotel(2, "Hotel Test 2", "TestDescription2", "A", "347-4453634", services2, 0, ratings2);


        HotelList hotelList = new HotelList();
        hotelList.add(h0);
        hotelList.add(h1);
        hotelList.add(h2);

        ArrayList<Hotel> oldFirst = hotelList.getFirstRanked();
        ArrayList<Hotel> t = new ArrayList<>();
        t.add(h0);
        assertEquals(t, oldFirst);

        h1.insertReview(5, new Ratings(5,5,5,5));
        h0.insertReview(1, new Ratings(1,1,2,2));
        
        hotelList.sort();
        ArrayList<Hotel> newFirst = hotelList.getFirstRanked();
        ArrayList<Hotel> t1 = new ArrayList<>();
        t1.add(h1);
        assertEquals(t1, newFirst);

        assertFalse(newFirst.equals(oldFirst));
        assertTrue(!newFirst.equals(oldFirst));
    }
    
}
  