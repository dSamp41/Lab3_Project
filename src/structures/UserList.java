package src.structures;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

//TreeMap per avere O(log n) get, put

public class UserList {
    private SortedMap<String, User> users;

    public UserList(){
        this.users = Collections.synchronizedSortedMap(new TreeMap<String, User>());
    }

    public SortedMap<String, User> getUsers(){
        return this.users;
    }

    public void add(User u){
        users.put(u.getUsername(), u);
    }

    public void addAll(Map<String, User> u){
        users.putAll(u);
    }

    public Optional<User> getByUsername(String name){
        return Optional.ofNullable(users.get(name));
    }
}