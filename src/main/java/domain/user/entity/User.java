package domain.user.entity;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Scope("prototype")
@Getter
@Setter

public class User {
    private String name;
    private long id;
    private int phone;
    private LocalDate birthday;
    private Account account;




}
