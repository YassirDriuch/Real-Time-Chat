package chat.DAO;

import chat.Model.User;

import java.util.List;

public interface UserDAO {

    public User getUser(Long id);
    public User saveUser(User user);
    public void updateUser(Long id, User user);
    public void deleteUser(Long id);
    public List<User> getAllUsers();
}
