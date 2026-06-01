package domain.user.service;

import com.sun.tools.javac.util.List;
import domain.user.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> getUserById(long id);
    List<User> getAllUsers();
    User updateUser(User user);
    User createUser(User user);
    void deleteUser(long id);
    User changePassword(User user);



}
