import static org.junit.Assert.assertEquals;

import java.time.LocalDate;

import org.junit.Test;

public class UserTest {
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
