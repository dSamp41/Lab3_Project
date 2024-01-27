package src.structures;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class UserList {
    private ArrayList<User> users;

    public UserList(){
        this.users = new ArrayList<>();
    }

    public ArrayList<User> getUsers(){
        return this.users;
    }

    //TODO: implement sorted insertion
    public void add(User u){
        users.add(u);
    }

    public void addAll(ArrayList<User> u){
        users.addAll(u);
    }

    //TODO: binary search

    public List<User> searchByUsername(String name){
        Predicate<User> p = u -> (u.getUsername().equals(name));
        
        return users.stream().filter(p).collect(Collectors.toList());
    }

    public User getByName(String username) {
        Predicate<User> p = u -> (u.getUsername().equals(username));
        
        return users.stream().filter(p).findFirst().get();
    }

}
