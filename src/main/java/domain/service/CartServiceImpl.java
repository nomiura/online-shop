package domain.service;

import domain.entity.Cart;
import domain.entity.Order;
import domain.repository.CartRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Data
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;

    @Transactional(readOnly = true)
    @Override
    public Cart getCart(Long accountId) {
        return null;
    }

    @Transactional
    @Override
    public Cart createCart(Long accountId) {
        return null;
    }

    @Transactional
    @Override
    public Cart addItem(Long accountId, Long itemId, Integer quantity) {
        return null;
    }

    @Transactional
    @Override
    public Cart updateItemQuantity(Long accountId, Long itemId, Integer quantity) {
        return null;
    }

    @Transactional
    @Override
    public Cart removeItem(Long accountId, Long itemId) {
        return null;
    }

    @Transactional
    @Override
    public void clearCart(Long accountId) {

    }

    @Transactional
    @Override
    public Cart applyPromoCode(Long accountId, Integer promoCode) {
        return null;
    }

    @Transactional
    @Override
    public Order convertCartToOrder(Long accountId) {
        return null;
    }
}
