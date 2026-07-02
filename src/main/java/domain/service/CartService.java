package domain.service;

import domain.entity.Account;
import domain.entity.Cart;
import domain.entity.Order;

public interface CartService {
    Cart getCart(Long accountId);
    Cart createCart(Long accountId);
    Cart addItem(Long accountId, Long itemId, Integer quantity);
    Cart updateItemQuantity(Long accountId, Long itemId, Integer quantity);
    Cart removeItem(Long accountId, Long itemId);

    void clearCart(Long accountId);

    Cart applyPromoCode(Long accountId, Integer promoCode);
    Order convertCartToOrder(Long accountId);
}
