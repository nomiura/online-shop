package domain.repository;

import domain.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findById(Long productId);

    @Modifying
    @Query("""
            UPDATE Product p 
            SET p.quantityAvailable = p.quantityAvailable - :qty
            WHERE p.productId = :productId AND p.quantityAvailable >= :qty
            """)
    int updateStock(@Param("productId") Long productId, @Param("qty") Integer quantityAvailable);

    @Query("SELECT p.quantityAvailable FROM Product p WHERE p.productId = :productId")
    Integer getQuantityAvailableById(@Param("productId") Long productId);
}
