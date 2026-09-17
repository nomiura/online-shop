package domain.repository;


import domain.entity.Review;
import domain.interfaces.ProductReviewStatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // все отзывы на товар, свежие сверху
    List<Review> findByProduct_ProductIdOrderByCreatedDateDesc(Long productId);

    // защита от дубликата: этот аккаунт уже писал отзыв на этот товар?
    boolean existsByAccount_IdAndProduct_ProductId(Long accountId, Long productId);

    // достать конкретный отзыв аккаунта на товар (для редактирования/удаления)
    Optional<Review> findByAccount_IdAndProduct_ProductId(Long accountId, Long productId);

    List<Review> findByAccount_IdOrderByCreatedDateDesc(Long accountId);

    // сколько всего отзывов на товар
    long countByProduct_ProductId(Long productId);

    // средний рейтинг товара
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);

    //находим количество отзывов и рейтинг по конкретному продукту
    @Query("""
    SELECT r.product.productId AS productId,
           COUNT(r) AS count,
           AVG(r.rating) AS averageRating
    FROM Review r
    WHERE r.product.productId IN :productIds
    GROUP BY r.product.productId
    """)
    List<ProductReviewStatsProjection> findStatsByProductIds(@Param("productIds") Collection<Long> productIds);
}