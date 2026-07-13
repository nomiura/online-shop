package domain.repository;

import domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long productId);

    //@Query("SELECT p.quantityAvailable FROM Product p WHERE p.id = :productId")
    Integer getQuantityAvailableById(Long productId);
}
