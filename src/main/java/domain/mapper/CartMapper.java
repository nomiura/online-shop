package domain.mapper;

import domain.dto.request.CartToAddRequest;
import domain.dto.response.CartItemResponse;
import domain.dto.response.CartResponse;
import domain.entity.Cart;
import domain.entity.CartItem;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class CartMapper {

    private final CartItemMapper cartItemMapper;

    public CartResponse cartToResponseDto(Cart cart) {
        if (cart == null) return null;

        CartResponse cartResponse = new CartResponse();
        cartResponse.setCartId(cart.getId());

        List<CartItemResponse> cartItemMapperList = cart.getItems()
                        .stream()
                        .map(cartItemMapper::toCartItemResponse)
                        .toList();
        cartResponse.setItems(cartItemMapperList);

        cartResponse.setTotalQuantity(cart.getTotalQuantity());
        cartResponse.setTotalPrice(cart.getTotalPrice());
        cartResponse.setSubtotalPrice(cart.getSubtotalPrice());
        cartResponse.setDiscountPrice(cart.getDiscountPrice());
        cartResponse.setPromoCode(cart.getPromoCode());
        return cartResponse;
    }

    public Cart requestToCart(CartToAddRequest request) {
        if (request == null) return null;

        Cart cart = new Cart();
        CartItem cartItem = new CartItem();


        return cart;
    }

}
