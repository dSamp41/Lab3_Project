import java.util.ArrayList;

public class Hotel {
    private int id;
    private String name, description, city, phone;
    private ArrayList<String> services;
    private float rate;
    private Ratings ratings;
    
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

    public String toString(){
        String serviziDisp = String.join(", ", this.services);

        return this.name + "\n" + this.description + "\n" + "Servizi disponibili: " + serviziDisp + "\n\n";
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
    }
    
    private float EMA(float oldVal, float newVal){
        if(oldVal == 0){
            return newVal;
        }
        
        float alpha = (float) 0.2;
        float res = (1-alpha) * oldVal + (alpha * newVal);
        
        return res;
    }    
}



/*
"id": 1,
"name": "Hotel Aosta 1",
"description": "Un ridente hotel a Aosta, in Via della gioia, 25",
"city": "Aosta",
"phone": "347-4453634",
"services": [
    "TV in camera",
    "Palestra",
    "Cancellazione gratuita"
],
"rate": 0,
"ratings": {
    "cleaning": 0,
    "position": 0,
    "services": 0,
    "quality": 0
}
*/