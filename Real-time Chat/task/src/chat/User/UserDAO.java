package chat.User;

import java.util.List;

public interface UserDAO {

    public User getUser(Long id);
    public Long saveUser(User User);
    public void updateUser(Long id, User User);
    public void deleteUser(Long id);
    public List<User> getAllUsers();
}
