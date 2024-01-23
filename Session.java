import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Session implements Runnable {
    private final long REVIEW_DELTA_DAYS;

    private Socket clientSocket;
    private HotelList hotels;
    private UserList users;

    private boolean isLogged = false;
    private String username = "";
    private User currentUser;

    public Session(Socket s, HotelList h, UserList u, long delta){
        this.clientSocket = s;
        this.hotels = h;
        this.users = u;
        this.REVIEW_DELTA_DAYS = delta;
    }

    public void run(){
        System.out.println("New client: " + clientSocket);

        try(BufferedReader fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter toClient = new PrintWriter(clientSocket.getOutputStream(), true)) 
        {
            String inputLine;

            while((inputLine = fromClient.readLine()) != null){
                processCommand(inputLine, toClient);
            }
        } 
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        finally{
            System.out.println("A client left: " + clientSocket);
            if(isLogged) logout();
            
        }
    }

    //read input from in; write output to out
    private void processCommand(String in, PrintWriter out) throws IOException{
        String[] input = in.split(" ");
        String op = input[0];

        System.out.println(clientSocket + "sent: " + in);

        switch(op) {
            case "register":
                if(input.length != 3){
                    out.println("Too much or too little args");
                    break;
                }

                String pwHash = User.getMD5Hash(input[2]);
                String registrationStatus = register(input[1], pwHash);
                out.println(registrationStatus);
                
                break;

            case "login":
                if(input.length != 3){
                    out.println("Too much or too little args");
                    break;
                }
                
                String pw_hash = User.getMD5Hash(input[2]);
                String loginStatus = login(input[1], pw_hash);
                out.println(loginStatus);

                break;

            case "logout":
                if(input.length != 1){
                    out.println("Too much or too little args");
                    break;
                }
                if(!isLogged){
                    out.println("Not logged in");
                    break;
                }

                out.println(logout());
                break;
                
            case "searchAllHotels":
                if(input.length != 2){
                    out.println("Too much or too little args");
                    break;
                }
                
                out.println(searchAllHotels(input[1]));
                break;

            case "searchHotel":
                if(input.length != 5){
                    out.println("Too much or too little args");
                    break;
                }                
                
                String name = input[1] + " " + input[2] + " " + input[3];
                out.println(searchHotel(name, input[4]));
                
                break;
            
            case "showBadge":
                if(input.length != 1){
                    out.println("Too much or too little args");
                    break;
                }
                if(!isLogged){
                    out.println("You must be logged in to see your badge");
                    break;
                }

                out.println(showBadge(username));
                break;

            case "insertReview":
            //insertReview Hotel Roma 1 Roma 5 4 4 4 4
                if(input.length != 10){
                    out.println("Too much or too little args");
                    break;
                }

                if(!isLogged){
                    out.println("You must be logged in to insert a review");
                    break;
                }

                String hotelName = String.format("%s %s %s", input[1], input[2], input[3]);
                String hotelCity = input[4];
                int globalRate = Integer.parseInt(input[5]);
                int[] ratings = {Integer.parseInt(input[6]), Integer.parseInt(input[7]), Integer.parseInt(input[8]), Integer.parseInt(input[8])};

                out.println(insertReview(hotelName, hotelCity, globalRate, ratings));
                break;

            default:
                out.printf("Unknown command <%s>\n", op);
                break;
        }
    }

    private String register(String username, String pwdHash){
        boolean usernameAlreadyTaken = users.searchByUsername(username).size() != 0;
        
        if(usernameAlreadyTaken){
            return "Username already taken";
        }
        else{
            users.add(new User(username, pwdHash));
            return "Registration was successful";
        }
    }

    private String login(String username, String pwdHash){
        if(isLogged){
            return "Already logged in";
        }
        List<User> userWithUsername = users.searchByUsername(username);

        if(userWithUsername.size() == 0){
            return "This user doesn't exist";
        }
        
        User user = userWithUsername.get(0);
        if(!user.getHash().equals(pwdHash)){    //wrong password
            return "Wrong password";
        }
        else{
            this.isLogged = true;
            this.username = username;
            this.currentUser = users.getByName(username);

            return "Successfully logged in";
        }
    }

    private String logout(){
        this.isLogged = false;
        this.username = "";

        return "Logout successful";
    }

    private String searchAllHotels(String city){
        List<Hotel> hotelsInCity = hotels.searchByCity(city);

        if(hotelsInCity.size() == 0){
            return "No hotel found in " + city;
        }
        //TODO: orber by ranking; https://stackoverflow.com/questions/2784514/sort-arraylist-of-custom-objects-by-propertyf
        String foundHotels = hotelsInCity.stream()
            .map(Hotel::toString)
            .collect(Collectors.joining(""));
        
        return foundHotels.replace("\n", "^");
    }

    private String searchHotel(String name, String city){
        List<Hotel> results = hotels.searchByName(name, city);

        if(results.size() == 0){
            return "Searched hotel not found";
        }
        return results.get(0).toString().replace("\n", "^");
    }

    private String showBadge(String username){
        return users.searchByUsername(username).get(0).getBadge();
    }

    private String insertReview(String hotelName, String hotelCity, float globalRate, int[] ratings){
        
        //check if user already inserted a review for the same hotel less then REVIEW_DELTA_DAYS ago. 
        Optional<LocalDate> lastReviewDate = currentUser.getLastReviewDate(hotelName);
        if(lastReviewDate.isEmpty()){
            currentUser.addReview(hotelName, LocalDate.now());
        }
        else{
            LocalDate d = lastReviewDate.get();

            if(ChronoUnit.DAYS.between(d, LocalDate.now()) < REVIEW_DELTA_DAYS){
                return "You already inserted a recent review for this hotel.";
            }
            currentUser.addReview(hotelName, LocalDate.now());
        }
                
        List<Hotel> searchedHotels = hotels.searchByName(hotelName, hotelCity);
        if(searchedHotels.size() == 0){ 
            return String.format("Hotel not found. The name <%s> or city <%s> is wrong", hotelName, hotelCity);
        }

        //Hotel.insertReview()
        Hotel selectedHotel = searchedHotels.get(0);
        Ratings r = new Ratings(ratings[0], ratings[1], ratings[2], ratings[3]);
        selectedHotel.insertReview(globalRate, r);

        return "The review has been successfully inserted";
    }
}
