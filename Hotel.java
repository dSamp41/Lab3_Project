import java.util.ArrayList;

public class Hotel {
    private class Ratings{
        float cleaning, position, services, quality;
    }

    private int id;
    private String name, description, city, phone;
    private ArrayList<String> services;
    private int rate;
    private Ratings ratings;
    
    public Hotel(int id, String name, String description, String city, String phone, 
    ArrayList<String> services, int rate, Ratings ratings){
        this.id = id;
        this.name = name;
        this.description = description;
        this.city = city; 
        this.phone = phone;
        this.services = services;
        this.rate = rate;
        this.ratings = ratings;
    }

    public String getCity(){
        return this.city;
    }

    public String getName(){
        return this.name;
    }

    public String toString(){
        String serviziDisp = String.join(", ", this.services);

        return this.name + "\n" + this.description + "\n" + "Servizi disponibili: " + serviziDisp + "\n\n";
    }

    public void insertReview(){}
    
    private float EMA(float oldVal, float newVal){
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