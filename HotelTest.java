

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

public class HotelTest {
    private Hotel h;
    @Before
    public void setup(){
        ArrayList<String> services = new ArrayList<>();
        services.add("TV in camera");
        services.add("Palestra");
        services.add("Cancellazione gratuita");

        Ratings ratings = new Ratings(0, 0, 0, 0);
        
        h = new Hotel(1, "Hotel Test 1", "TestDescription", "TestCity", "347-4453634", services, 0, ratings);
    }

    @Test
    public void insertReviewTest(){

        h.insertReview(5, new Ratings(5, 5, 5, 5));
        assertEquals(5, h.getRate(), 0.01);
        assertEquals(5, h.getRatings().getCleaning(), 0.01);

        h.insertReview(1, new Ratings(1, 1, 1, 1));
        assertEquals(4.2, h.getRate(), 0.01);
        assertEquals(4.2, h.getRatings().getCleaning(), 0.01);

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
    
}
