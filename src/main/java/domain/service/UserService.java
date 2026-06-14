package domain.service;

import domain.dto.request.CreateUserRequest;
import domain.dto.request.UpdateUserRequest;
import domain.entity.User;
import jakarta.validation.Valid;

import java.util.Optional;

public interface UserService {

    Optional<User> getUserById(Long id);
    //List<User> getAllUsers(); зачем юзеру получать список всех пользователей?
    User updateUser(Long id, @Valid UpdateUserRequest request);
    User createUser(CreateUserRequest request);
    void deleteUser(Long id);





}
