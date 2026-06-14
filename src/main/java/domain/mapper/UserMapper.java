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
        user.setBirthday(request.getBirthday());
        user.setSex(request.getSex());
        user.setAddresses(request.getAddresses());
        return user;
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getBirthday(),
                user.getSex(),
                user.getAddresses()
        );
    }
}
