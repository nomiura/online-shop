package domain.user.service;

import com.sun.tools.javac.util.List;
import domain.user.entity.User;
import domain.user.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
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
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public User changePassword(User user) {
        return null;
    }

    @Override
    public User changeEmail(User user) {
        return null;
    }

    @Override
    public User changePhone(User user) {
        return null;
    }

    @Override
    public User changeUsername(User user) {
        return null;
    }
}
