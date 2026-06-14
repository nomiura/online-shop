package domain.service;

import domain.dto.request.CreateUserRequest;
import domain.dto.request.UpdateUserRequest;
import domain.entity.User;
import domain.exception.UserNotFoundException;
import domain.mapper.UserMapper;
import domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

//    @Override
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }

    @Override
    public User updateUser(Long id, UpdateUserRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setUsername(request.getUsername());
        user.setBirthday(request.getBirthday());
        user.setSex(request.getSex());
        user.setAddresses(request.getAddresses());
        User updatedUser = userRepository.save(user);
        System.out.println("Пользователь успешно обновлён");
        return updatedUser;
    }

    @Override
    public User createUser(CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        User createdUser = userRepository.save(user);
        System.out.println("Пользователь успешно создан.");
        return createdUser;
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        System.out.println("Пользователь успешно удален.");
    }


}
