import static org.junit.Assert.assertEquals;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

import com.google.gson.Gson;

import src.server.GsonFactory;
import src.structures.User;

public class UserTest {
    @Test
    public void userSerializationTest(){
        Gson gson = GsonFactory.get();

        User u = new User("testName", "afuheifugnf542");
        u.addReview("Hotel Aosta 1", LocalDate.now());

        String userJSON = gson.toJson(u);
        String result = 
        "{\n" + //
        "  \"username\": \"testName\",\n" +
        "  \"pwdHash\": \"afuheifugnf542\",\n" +
        "  \"badge\": \"A\",\n" +
        "  \"reviewCount\": 1,\n" +
        "  \"lastInsertedReviews\": {\n" +
        "    \"Hotel Aosta 1\": \"2024-01-27\"\n" + 
        "  }\n" +
        "}";

        assertEquals(result, userJSON);
    }



    @Test
    public void userBadgeTest(){
        User u = new User("aaaaaa", "11111");

        for(int i=0; i<4; i++){
            u.addReview("a", LocalDate.now());
        }
        assertEquals("Recensore", u.getBadge());

        for(int i=0; i<5; i++){
            u.addReview("a", LocalDate.now());
        }
        assertEquals("Recensore esperto", u.getBadge());

        for(int i=0; i<5; i++){
            u.addReview("a", LocalDate.now());
        }
        assertEquals("Contributore", u.getBadge());

        for(int i=0; i<10; i++){
            u.addReview("a", LocalDate.now());
        }
        assertEquals("Contributore esperto", u.getBadge());

        for(int i=0; i<50; i++){
            u.addReview("a", LocalDate.now());
        }
        assertEquals("Contributore Super", u.getBadge());
    }
}
