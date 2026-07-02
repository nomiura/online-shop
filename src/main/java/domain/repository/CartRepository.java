package domain.repository;

import domain.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByAccountId(Long accountId);
    //Optional<Cart> findBySessionId(String sessionId);
}
