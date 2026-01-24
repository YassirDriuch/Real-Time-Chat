package chat.DAO;

import chat.Model.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UserDAOImpl implements UserDAO{
    private Long id = 0L;

    private final List<User> users = new ArrayList<>();

    @Override
    public User getUser(Long id) {
        return users.stream().filter(user -> user.getId().equals(id)).toList().getFirst();
    }

    @Override
    public User saveUser(User user) {
        user.setId(id++);
        users.add(user);
        return user;
    }

    @Override
    public void updateUser(Long id, User user) {
        users.set(users.indexOf(getUser(id)), user);
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(getUser(id));
    }

    public List<User> getAllUsers() {
        return users;
    }

}
