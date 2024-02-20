package src.server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import src.structures.Hotel;
import src.structures.HotelList;
import src.structures.Ratings;
import src.structures.User;
import src.structures.UserList;

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
            String inputLine, response;

            while((inputLine = fromClient.readLine()) != null){
                response = processCommand(inputLine);
                toClient.println(response);
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
    private String processCommand(String in) throws IOException{
        String[] input = in.split(" ");
        String op = input[0];

        System.out.println(clientSocket + "sent: " + in);

        switch(op) {
            case "register":
                if(input.length != 3){
                    return "Too much or too little args";
                }

                String pwHash = User.getMD5Hash(input[2]);
                String registrationStatus = register(input[1], pwHash);
                return registrationStatus;
                

            case "login":
                if(input.length != 3){
                    return "Too much or too little args";
                }
                
                String pw_hash = User.getMD5Hash(input[2]);
                String loginStatus = login(input[1], pw_hash);
                return (loginStatus);

            case "logout":
                if(input.length != 1){
                    return "Too much or too little args";
                }
                if(!isLogged){
                    return ("Not logged in");
                }

                String logoutStatus = logout();
                return logoutStatus;
                
            case "searchAllHotels":
                if(input.length != 2){
                    return "Too much or too little args";
                }
                
                return searchAllHotels(input[1]);

            case "searchHotel":
                String[] inputParts = in.split("\"");
                if(inputParts.length != 3){
                    return "Too much or too little args";
                }

                String name = inputParts[1];
                String city = inputParts[2].replaceFirst(" ", "");
                return searchHotel(name, city);
            
            case "showBadge":
                if(input.length != 1){
                    return "Too much or too little args";
                }
                if(!isLogged){
                    return "You must be logged in to see your badge";
                }

                return showBadge(username);

            case "insertReview":
                if(!isLogged){
                    return "You must be logged in to insert a review";
                }

                //insertReview "Hotel Roma 1" Roma 5 4 4 4 4
                String[] parts = in.split("\"");

                String hotelName = parts[1];
                String[] cityAndRates = parts[2]
                    .replaceFirst(" ", "")
                    .split(" ");

                if(cityAndRates.length != 6){
                    return "Too much or too little args";
                }

                String hotelCity = cityAndRates[0];
                int globalRate = Integer.parseInt(cityAndRates[1]);

                String[] rtngs = {cityAndRates[2], cityAndRates[3], cityAndRates[4], cityAndRates[5]};
                int[] ratings = Arrays.stream(rtngs)
                    .mapToInt(Integer::parseInt)
                    .toArray();

                boolean globalRateValid = isValidRate(globalRate);
                boolean ratingsValid = isValidRate(ratings[0]) && isValidRate(ratings[1]) && isValidRate(ratings[2]) && isValidRate(ratings[3]);
                boolean allRatesValid = globalRateValid && ratingsValid;

                if(allRatesValid){
                    return insertReview(hotelName, hotelCity, globalRate, ratings);
                }
                else{
                    return "All rates must be between 0 and 5";
                }

            default:
                return "Unknown command <" + op + ">";
        }
    }

    private String register(String username, String pwdHash){
        boolean usernameAlreadyTaken = users.getByUsername(username).isPresent();
        
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
        Optional<User> userWithUsername = users.getByUsername(username);

        if(userWithUsername.isEmpty()){
            return "This user doesn't exist";
        }
        
        User user = userWithUsername.get();
        if(!user.getHash().equals(pwdHash)){    //right username, wrong password
            return "Wrong password";
        }
        else{
            this.isLogged = true;
            this.username = username;
            this.currentUser = user;

            return "Successfully logged in";
        }
    }

    private String logout(){
        if(this.isLogged == true){
            this.isLogged = false;
            this.username = "";
            
            return "Logout successful";
        }
        else{
            return "Not logged in";
        }
    }

    private String searchAllHotels(String city){
        Optional<List<Hotel>> hotelsInCity = hotels.searchByCity(city);

        if(hotelsInCity.isEmpty()){
            return "City not found";
        }

        if(hotelsInCity.get().size() == 0){
            return "No hotel found in " + city;
        }

        String foundHotels = hotelsInCity.get().stream()
            .map(Hotel::toString)
            .collect(Collectors.joining(""));
        
        return foundHotels.replace("\n", "^");
    }

    private String searchHotel(String name, String city){
        Optional<List<Hotel>> results = hotels.searchByName(name, city);

        if(results.isEmpty()){
            return "City not found";
        }

        if(results.get().size() == 0){
            return "Searched hotel not found";
        }
        String searchedHotel = results.get().get(0).toString();

        return searchedHotel.replace("\n", "^");
    }

    private String showBadge(String username){
        return users.getByUsername(username).get()
            .getBadge();
    }

    private String insertReview(String hotelName, String hotelCity, float globalRate, int[] ratings){
        
        //check if user already inserted a review for the same hotel less then REVIEW_DELTA_DAYS ago. 
        Optional<LocalDate> lastReviewDate = currentUser.getLastReviewDate(hotelName);
        if(lastReviewDate.isEmpty()){
            currentUser.addReview(hotelName, LocalDate.now());
        }
        else{
            LocalDate d = lastReviewDate.get();

            if(ChronoUnit.DAYS.between(d, LocalDate.now()) < REVIEW_DELTA_DAYS){    //less then REVIEW_DELTA_DAYS are passed between the old review and the new one
                return "You already inserted a recent review for this hotel.";
            }
        }
                
        Optional<List<Hotel>> searchedHotels = hotels.searchByName(hotelName, hotelCity);
        
        if(searchedHotels.isEmpty() || searchedHotels.get().size() == 0){ 
            return String.format("Hotel not found. The name <%s> or city <%s> is wrong", hotelName, hotelCity);
        }

        //insert review to the hotel
        Hotel selectedHotel = searchedHotels.get().get(0);
        Ratings r = new Ratings(ratings[0], ratings[1], ratings[2], ratings[3]);

        selectedHotel.insertReview(globalRate, r);
        currentUser.addReview(hotelName, LocalDate.now());

        return "The review has been successfully inserted";
    }

    private boolean isValidRate(int n){
        return (0 <= n) && (n <= 5);
    }

}
