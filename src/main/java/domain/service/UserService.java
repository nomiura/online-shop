package domain.service;

import java.util.List;

import domain.dto.request.UpdateUserRequest;
import domain.entity.User;

import java.util.Optional;

public interface UserService {

    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    User updateUser(Long id, UpdateUserRequest request);
    User createUser(User user);
    void deleteUser(Long id);
    User changePassword(User user);





}
