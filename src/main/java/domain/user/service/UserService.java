package domain.user.service;

import com.sun.tools.javac.util.List;
import domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

public interface UserService {

    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(User user);
    User createUser(User user);
    void deleteUser(Long id);
    User changePassword(User user);
    User changeEmail(User user);
    User changePhone(User user);
    User changeUsername(User user);




}
