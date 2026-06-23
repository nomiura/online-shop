package ru.test.domain.service;

import domain.dto.request.CreateUserRequest;
import domain.dto.request.UpdateUserRequest;
import domain.entity.Sex;
import domain.entity.User;
import domain.exception.UserNotFoundException;
import domain.mapper.UserMapper;
import domain.repository.UserRepository;
import domain.service.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private  UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void  getUserById_returnsUser_whenFound(){
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Optional<User> result = userService.getUserById(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1L);
    }
    @Test
    void  updateUser_updatesFields_whenUserExist(){
        User existing = new User();
        existing.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        //when(...).thenReturn(...) —
        // «когда у мока вызовут вот этот метод с такими аргументами — пусть вернёт вот это».
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("Аня");
        request.setBirthday(LocalDate.of(2000, 1, 1));
        request.setSex(Sex.FEMALE);
        request.setAddresses(List.of("Москва"));

        User result = userService.updateUser(1L, request);

        assertThat(result.getUsername()).isEqualTo("Аня");
        assertThat(result.getSex()).isEqualTo(Sex.FEMALE);
        verify(userRepository).save(existing);
        //verify - проверка факта вызова, полезно когда метод void
    }

    @Test
    void updateUser_throws_whenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, new UpdateUserRequest()))
                .isInstanceOf(UserNotFoundException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_savesMappedEntity() {
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Иван");

        User mapped = new User();
        mapped.setUsername("Иван");
        when(userMapper.toEntity(request)).thenReturn(mapped);
        when(userRepository.save(mapped)).thenReturn(mapped);

        User result = userService.createUser(request);

        assertThat(result.getUsername()).isEqualTo("Иван");
        verify(userRepository).save(mapped);
    }

    @Test
    void deleteUser_callsRepository() {
        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }
}
