package domain.mapper;

import domain.dto.response.CartItemResponseDto;
import domain.entity.CartItem;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Component
public class CartItemMapper {

    public CartItemResponseDto toCartItemResponse(CartItem cartItem) {
        CartItemResponseDto cartItemResponse = new CartItemResponseDto();
        cartItemResponse.setProductId(cartItem.getId());
        cartItemResponse.setProductName(cartItem.getProduct().getName());
        cartItemResponse.setProductImage(cartItem.getProduct().getImage());
        cartItemResponse.setQuantity(cartItem.getQuantity());
        cartItemResponse.setAdditionPrice(cartItem.getPriceAddition());
        return cartItemResponse;
    }

    public List<CartItemResponseDto> toCartItemResponseList(List<CartItem> cartItems) {
        if (cartItems == null || cartItems.isEmpty()) return Collections.emptyList();
        return cartItems.stream()
                .filter(Objects::nonNull)
                .map(this::toCartItemResponse)
                .toList();
    }
}
