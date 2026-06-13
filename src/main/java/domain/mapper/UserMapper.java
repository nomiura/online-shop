package domain.mapper;

import domain.dto.request.CreateUserRequest;
import domain.dto.response.UserResponse;
import domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        user.setBirthday(request.getBirthday());
        user.setSex(request.getSex());
        user.setAddress(request.getAddress());
        return user;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getPhone(),
                user.getEmail(),
                user.getBirthday(),
                user.getSex(),
                user.getAddress()
        );
    }
}
