import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Optional;

enum Badge {
    A("Recensore"), 
    B("Recensore esperto"), 
    C("Contributore"),
    D("Contributore esperto"),
    E("Contributore Super");

    private final String descr;

    Badge(String descr){
        this.descr = descr;
    }

    public String getBadge(){
        return descr;
    }
};

public class User {
    private String username;
    private String pwdHash;
    private Badge badge;
    private HashMap<String, LocalDate> lastInsertedReviews;

    public User(String u, String p){
        this.username = u;
        this.pwdHash = p;
        this.badge = Badge.A;
        this.lastInsertedReviews = new HashMap<>();
    }

    public String getUsername(){
        return this.username;
    }

    public String getHash(){
        return this.pwdHash;
    }

    public String getBadge(){
        return badge.getBadge();
    }

    public static String getMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) {
                hexString.append(String.format("%02x", b & 0xFF));
            }

            return hexString.toString();
        } 
        catch(NoSuchAlgorithmException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public void addReview(String hotelName, LocalDate date){
        lastInsertedReviews.put(hotelName, date);
    }

    public Optional<LocalDate> getLastReviewDate(String hotelName){
        return Optional.ofNullable(lastInsertedReviews.get(hotelName));
    }
    
    //TODO: updateBadge
}
