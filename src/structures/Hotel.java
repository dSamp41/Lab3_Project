package src.structures;
import java.util.ArrayList;

public class Hotel {
    private int id;
    private String name, description, city, phone;
    private ArrayList<String> services;
    private float rate;
    private Ratings ratings;

    private int numReviews;
    
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

        this.numReviews = 0;
    }

    public String getCity() {return this.city;}
    public String getName() {return this.name;}
    public int getNumServices() {return this.services.size();}

    public float getRate() {return this.rate;}

    private void setRate(float r){
        this.rate = r;
    }

    public Ratings getRatings() {return this.ratings;}

    public float getRatingsAvg(){
        return this.ratings.getRatingsAvg();
    }

    public int getNumReviews() {return this.numReviews;}

    public String toString(){
        String serviziDisp = String.join(", ", this.services);

        return this.name + "\n" + this.description + "\n" 
            + "Servizi disponibili: " + serviziDisp + "\n" 
            + "Numero di telefono: " + this.phone + "\n"
            + "Voto: " + rate + "\n" 
            + "Average ratings: " + this.ratings.getRatingsAvg() + "\n" 
            + "Numero recensioni: " + this.numReviews + "\n\n";
    }

    public void insertReview(float r, Ratings rtngs){
        float newRate = EMA(this.rate, r);
        setRate(newRate);

        float newCleaning = EMA(ratings.getCleaning(), rtngs.getCleaning()); 
        float newPosition = EMA(ratings.getPosition(), rtngs.getPosition());
        float newServices = EMA(ratings.getServices(), rtngs.getPosition()); 
        float newQuality = EMA(ratings.getQuality(), rtngs.getQuality());

        Ratings newRatings = new Ratings(newCleaning, newPosition, newServices, newQuality);
        this.ratings = newRatings;

        this.numReviews++;
    }
    
    private float EMA(float oldVal, float newVal){
        if(oldVal == 0){
            return newVal;
        }
        
        float alpha = (float) 0.3;
        float res = (1-alpha) * oldVal + (alpha * newVal);
        
        return res;
    }

    //scoring is a confidence interval [rate +/- 1/numReviews] ==> sorting based on lower bound
    public float getScore(){
        Float reviewsWeight = Float.parseFloat("1.5");

        float rateScore = rate - reviewsWeight * 1/numReviews;
        float ratingsScore = ratings.getRatingsAvg() - reviewsWeight * 1/numReviews;
        
        return (rateScore + ratingsScore)/2;
    }
}