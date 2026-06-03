package domain.user.controller;

import domain.user.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/user")
    public ResponseEntity<User> getUser() {

        return null;
    }
}
