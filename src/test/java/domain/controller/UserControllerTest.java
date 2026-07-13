package domain.controller;


import domain.controller.UserController;
import domain.dto.request.CreateUserRequest;
import domain.dto.request.UpdateUserRequest;
import domain.dto.response.UserResponse;
import domain.entity.Sex;
import domain.entity.User;
import domain.exception.GlobalExceptionHandler;
import domain.mapper.UserMapper;
import domain.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    private MockMvc mockMvc;

    private  ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getUser_returns200_whenFound() throws Exception {
        User user = new User();
        user.setId(1L);
        UserResponse response = new UserResponse(1L, "Аня",
                LocalDate.of(2000, 1, 1), Sex.FEMALE, List.of("Москва"));

        when(userService.getUserById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponse(user)).thenReturn(response);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deletedUser_returns204() throws Exception {
       mockMvc.perform(delete("/users/1"))
                       .andExpect(status().isNoContent());
       verify(userService).deleteUser(1L);
    }

    @Test
    void getUser_returns404_whenNotFound() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_returns201_whenCreated() throws Exception {
        User user = new User();
        user.setId(1L);
        CreateUserRequest request = new CreateUserRequest();
        request.setUsername("Anna");
        request.setBirthday(LocalDate.of(1993, 8, 22));
        request.setSex(Sex.FEMALE);
        request.setAddresses(List.of("Малая Токмачка"));
        UserResponse response = new UserResponse(1L,"Anna", LocalDate.of(1993,8,22),Sex.FEMALE, List.of("Малая Токмачка"));
        when(userService.createUser(any(CreateUserRequest.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

    }

    @Test
    void createUser_returns400_whenInvalid() throws Exception {
        CreateUserRequest request = new CreateUserRequest();

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void updateUser_returns200_whenUpdated  () throws Exception {
        User user = new User();
        user.setId(1L);
        UpdateUserRequest request = new UpdateUserRequest();
        request.setUsername("Kate");
        request.setBirthday(LocalDate.of(1993, 8, 22));
        request.setSex(Sex.FEMALE);
        request.setAddresses(List.of("Малая Токмачка","Средняя Токмачка"));
        UserResponse response = new UserResponse(1L,"Kate", LocalDate.of(1993,8,22),Sex.FEMALE, List.of("Малая Токмачка","Средняя Токмачка"));
        when(userService.updateUser(eq(1L),any(UpdateUserRequest.class))).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(response);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
