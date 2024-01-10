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

    public void add(User u){
        users.add(u);
    }

    public void addAll(ArrayList<User> u){
        users.addAll(u);
    }

    public List<User> searchByUsername(String name){
        Predicate<User> p = u -> (u.getUsername().equals(name));
        
        return users.stream().filter(p).collect(Collectors.toList());
    }

}
