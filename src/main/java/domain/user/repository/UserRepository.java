package domain.user.repository;

import domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository {

    Optional<User> persist(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByPhone(int phone);
    Optional<User> findByEmail(String email);
}
