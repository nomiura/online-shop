package domain.repository;

import domain.entity.Product;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long productId);
    Integer getQuantityAvailable(Long productId);
}
