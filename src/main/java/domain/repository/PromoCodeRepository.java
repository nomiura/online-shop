package domain.repository;

import domain.entity.Cart;
import domain.entity.PromoCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PromoCodeRepository extends JpaRepository<PromoCode,Long> {
    Optional<PromoCode> findByCode(String promoCode);
}
