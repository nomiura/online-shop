package domain.repository;

import domain.entity.Order;
import domain.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order,Long> {
    List<Order> findByCreatedBy_Id(Long accountId);

    @Query("""
       SELECT CASE WHEN COUNT(oi) > 0 THEN true ELSE false END
       FROM Order o
       JOIN o.items oi
       WHERE o.createdBy.id = :accountId
         AND oi.product.productId = :productId
         AND o.orderStatus IN :statuses
       """)
    boolean existsCompletedPurchase(@Param("accountId") Long accountId,
                                    @Param("productId") Long productId,
                                    @Param("statuses") List<OrderStatus> statuses);
}
