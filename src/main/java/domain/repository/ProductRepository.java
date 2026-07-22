package domain.repository;

import domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long productId);

    @Query("SELECT p.quantityAvailable FROM Product p WHERE p.productId = :productId")
    Integer getQuantityAvailableById(@Param("productId") Long productId);
}
