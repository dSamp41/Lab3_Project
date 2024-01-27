package src.structures;
public class Ratings{
    private float cleaning, position, services, quality;

    public Ratings(float cleaning, float position, float services, float quality){
        this.cleaning = cleaning;
        this.position = position;
        this.services = services;
        this.quality = quality;
    }

    public float getCleaning(){return this.cleaning;}
    public float getPosition(){return this.position;}
    public float getServices(){return this.services;}
    public float getQuality(){return this.quality;}

    public float getRatingsAvg() {
        return (cleaning + position + services + quality) / 4;
    }

    public void setCleaning(float c){this.cleaning = c;}
    public void setPosition(float p){this.position = p;}
    public void setServices(float s){this.services = s;}
    public void setQuality(float q){this.quality = q;}
}