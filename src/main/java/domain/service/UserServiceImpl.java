package domain.service;

import domain.dto.request.CreateUserRequest;
import domain.dto.request.UpdateUserRequest;
import domain.entity.User;
import domain.exception.UserNotFoundException;
import domain.mapper.UserMapper;
import domain.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    @Override
    public User createUser(CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        User createdUser = userRepository.save(user);
        log.info("User successfully created.");
        return createdUser;
    }

    @Transactional
    @Override
    public User updateUser(Long id, UpdateUserRequest request) {
        User updatedUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        if (request.getUsername() != null) updatedUser.setUsername(request.getUsername());
        if (request.getSex() != null) updatedUser.setSex(request.getSex());
        if (request.getBirthday() != null) updatedUser.setBirthday(request.getBirthday());
        if (request.getAddresses() != null) updatedUser.setAddresses(request.getAddresses());

        userRepository.save(updatedUser);
        log.info("User successfully updated with id {}.", id);
        return updatedUser;
    }

    @Transactional
    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
        log.info("User successfully deleted with id {}.", id);
    }


//    @Override
//    public List<User> getAllUsers() {
//        return userRepository.findAll();
//    }

//    @Transactional
//    @Override
//    public User updateUser(Long id, UpdateUserRequest request) {
//        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
//        user.setUsername(request.getUsername());
//        user.setBirthday(request.getBirthday());
//        user.setSex(request.getSex());
//        user.setAddresses(request.getAddresses());
//        User updatedUser = userRepository.save(user);
//        log.info("Пользователь успешно обновлен.");
//        return updatedUser;
//    }

}
