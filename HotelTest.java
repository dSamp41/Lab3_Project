import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

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
    public void userSerializationTest(){

        class LocalDateAdapter implements JsonSerializer<LocalDate> {
            public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
                return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
            }
        }



        Gson gson = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(LocalDate.class, new JsonDeserializer<LocalDate>() {
                @Override
                public LocalDate deserialize(JsonElement json, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
                    return LocalDate.parse(json.getAsJsonPrimitive().getAsString());
                }
            })
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

        User u = new User("testName", "afuheifugnf542");
        u.addReview("Hotel Aosta 1", LocalDate.now());

        String userJSON = gson.toJson(u);
        String result = 
        "{\n" + //
        "  \"username\": \"testName\",\n" +
        "  \"pwdHash\": \"afuheifugnf542\",\n" +
        "  \"badge\": \"A\",\n" +
        "  \"lastInsertedReviews\": {\n" +
        "    \"Hotel Aosta 1\": \"2024-01-18\"\n" + 
        "  }\n" +
        "}";


        assertEquals(result, userJSON);
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
        assertEquals(h0, hotelList.getHotels().getFirst());
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
    
}
  