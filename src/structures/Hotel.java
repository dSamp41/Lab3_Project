package src.structures;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Hotel {
    private int id;
    private String name, description, city, phone;
    private ArrayList<String> services;
    private float rate;
    private Ratings ratings;

    private AtomicInteger numReviews;
    
    public Hotel(int id, String name, String description, String city, String phone, 
    ArrayList<String> services, float rate, Ratings ratings){
        this.id = id;
        this.name = name;
        this.description = description;
        this.city = city; 
        this.phone = phone;
        this.services = services;
        this.rate = rate;
        this.ratings = ratings;

        this.numReviews = new AtomicInteger(0);
    }

    public String getCity() {return this.city;}
    public String getName() {return this.name;}
    public int getNumServices() {return this.services.size();}

    public float getRate() {return this.rate;}

    public Ratings getRatings() {return this.ratings;}

    public float getRatingsAvg(){
        return this.ratings.getRatingsAvg();
    }

    public String toString(){
        String serviziDisp = String.join(", ", this.services);

        return this.name + "\n" + this.description + "\n" 
            + "Servizi disponibili: " + serviziDisp + "\n" 
            + "Numero di telefono: " + this.phone + "\n"
            + "Voto: " + rate + "\n"  
            + "Pulizia: " + ratings.getCleaning() + "\n"
            + "Posizione: " + ratings.getPosition() + "\n"
            + "Servizi: " + ratings.getServices() + "\n"
            + "Qualità: " + ratings.getQuality() + "\n"
            + "Numero recensioni: " + this.numReviews.get() + "\n\n";
    }

    public synchronized void insertReview(float r, Ratings rtngs){
        float newRate = EMA(this.rate, r);
        this.rate = newRate;

        float newCleaning = EMA(ratings.getCleaning(), rtngs.getCleaning()); 
        float newPosition = EMA(ratings.getPosition(), rtngs.getPosition());
        float newServices = EMA(ratings.getServices(), rtngs.getPosition()); 
        float newQuality = EMA(ratings.getQuality(), rtngs.getQuality());

        Ratings newRatings = new Ratings(newCleaning, newPosition, newServices, newQuality);
        this.ratings = newRatings;

        this.numReviews.incrementAndGet();
    }
    
    private float EMA(float oldVal, float newVal){
        if(oldVal == 0){
            return newVal;
        }
        
        float alpha = (float) 0.2;
        float res = (1-alpha) * oldVal + (alpha * newVal);
        
        return res;
    }

    //scoring is a confidence interval [rate +/- 1/numReviews] ==> sorting based on lower bound
    public float getScore(){
        if(numReviews.get() == 0) return 0;
        Float reviewsWeight = Float.parseFloat("1.5");

        float rateScore = rate - reviewsWeight * 1/numReviews.get();
        float ratingsScore = ratings.getRatingsAvg() - reviewsWeight * 1/numReviews.get();
        
        return (rateScore + ratingsScore)/2;
    }
}