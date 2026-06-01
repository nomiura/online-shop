package domain.user.repository;


import ru.onlineshop.domain.user.entity.User;

public interface UserRepository {

    User persist(User user);
    User findById(long id);
    User findByPhone(int phone);
    User findByEmail(String email);
    User findAll();
}
