package domain.user.service;

import com.sun.tools.javac.util.List;
import domain.user.entity.User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    @Override
    public Optional<User> getUserById(Long id) {
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return null;
    }

    @Override
    public User updateUser(User user) {
        return null;
    }

    @Override
    public User createUser(User user) {
        return null;
    }

    @Override
    public void deleteUser(Long id) {

    }

    @Override
    public User changePassword(User user) {
        return null;
    }
}
