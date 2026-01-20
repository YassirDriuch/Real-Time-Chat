package chat.User;

import chat.User.User;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository

public class UserDAOImpl implements UserDAO{
    private Long id = 0L;

    private List<User> users = new ArrayList<>();

    @Override
    public User getUser(Long id) {
        return users.stream().filter(user -> user.getId() == id).toList().get(0);
    }

    @Override
    public Long saveUser(User user) {
        user.setId(id++);
        users.add(user);
        return user.getId();
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
