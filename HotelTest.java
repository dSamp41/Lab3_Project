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
        "    \"Hotel Aosta 1\": \"2024-01-14\"\n" + 
        "  }\n" +
        "}";


        assertEquals(result, userJSON);
    }
    
}
  