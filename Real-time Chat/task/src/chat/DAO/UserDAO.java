package chat.DAO;

import chat.Model.User;

import java.util.List;

public interface UserDAO {

    User getUser(Long id);
    User saveUser(User user);
    void updateUser(Long id, User user);
    void deleteUser(Long id);
    List<User> getAllUsers();
}
