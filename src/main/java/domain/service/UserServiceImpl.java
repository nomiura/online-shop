package domain.service;

import java.util.List;

import domain.dto.request.UpdateUserRequest;
import domain.entity.User;
import domain.exception.UserNotFoundException;
import domain.repository.UserRepository;
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
        return userRepository.findAll();
    }

    @Override
    public User updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setBirthday(request.getBirthday());
        return userRepository.save(user);
    }

    @Override
    public User createUser(User user) {

        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        System.out.println("Пользователь успешно удален.");
    }

    @Override
    public User changePassword(User user) {

        return userRepository.getReferenceById(user.getId());
    }

}
