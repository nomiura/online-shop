package domain.service;

import domain.dto.response.CartResponseDto;
import domain.entity.Order;

public interface CartService {
    CartResponseDto getCart(Long accountId);
    CartResponseDto addItem(Long accountId, Long productId, Integer quantity);
    CartResponseDto updateItemQuantity(Long accountId, Long productId, Integer quantity);
    void removeItem(Long accountId, Long productId);

    void clearCart(Long accountId);

    CartResponseDto applyPromoCode(Long accountId, String promoCode);
    CartResponseDto removePromoCode(Long accountId);
    Order convertCartToOrder(Long accountId);
}
