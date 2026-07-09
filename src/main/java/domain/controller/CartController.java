package domain.controller;


import domain.annotation.CurrentAccount;
import domain.dto.request.CartAddRequest;
import domain.dto.request.CartPromoCodeRequest;
import domain.dto.request.CartUpdateRequest;
import domain.dto.response.CartResponseDto;
import domain.entity.Account;
import domain.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// /cart                → Корзина текущего пользователя
// /cart/items          → Позиции в корзине
// /cart/items/{id}     → Конкретная позиция

// productId - id товара в каталоге -- когда товар добавляют в каталог 1 раз
// (глобально уникальный для всего магазина - не может измениться)
// cartItemId - id конкретной позиции в корзине -- когда пользователь кладет товар в корзину
// (уникальный только в рамке корзины - может измениться, каждый раз новый при добавлении)

@RequiredArgsConstructor
@Slf4j
@RestController
@Validated
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;

    // получение корзины
    @GetMapping()
    public ResponseEntity<CartResponseDto> getCart(
            @CurrentAccount Account currentAccount) {

        log.debug("Getting cart for account with id: {}", currentAccount.getId());
        CartResponseDto response = cartService.getCart(currentAccount.getId());
        return ResponseEntity.ok(response);
    }

    // добавление товара в корзину
    @PostMapping("/items")
    public ResponseEntity<CartResponseDto> addItem(
            @CurrentAccount Account currentAccount,
            @Valid @RequestBody CartAddRequest request //productId inside
            ) {

        log.info("Adding product {} to cart for account with id: {} (quantity: {})",
                request.getProductId(), currentAccount.getId(), request.getQuantity());
        CartResponseDto response = cartService.addItem(
                currentAccount.getId(),
                request.getProductId(),
                request.getQuantity());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // обновить кол-во товаров в корзине
    @PatchMapping("/items")
    public ResponseEntity<CartResponseDto> updateItemQuantity(
            @CurrentAccount Account currentAccount,
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartUpdateRequest request
            ) {

        log.info("Updating cartItemId {} quantity to {} for account with id: {}",
                cartItemId,
                request.getQuantity(),
                currentAccount.getId());


        CartResponseDto response = cartService.updateItemQuantity(
                currentAccount.getId(), cartItemId, request.getQuantity());
        return ResponseEntity.ok(response);
    }

    // удаление товара из корзины
    @DeleteMapping("/items/{cartItemId}")
    public ResponseEntity<Void> removeItem(
            @CurrentAccount Account currentAccount,
            @PathVariable Long cartItemId
    ) {

        log.info("Removing cart item {} from cart for account with id: {}",
                cartItemId, currentAccount.getId());
        cartService.removeItem( currentAccount.getId(), cartItemId);
        return ResponseEntity.noContent().build();
    }

    // полностью очистить корзину
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@CurrentAccount Account account) {
        log.info("Clearing cart for account with id: {}", account.getId());
        cartService.clearCart(account.getId());
        return ResponseEntity.noContent().build();
    }

    // применение промокода к корзине
    @PostMapping("/promo")
    public ResponseEntity<CartResponseDto> applyPromoCode(
            @CurrentAccount Account currentAccount,
            @RequestBody @Valid CartPromoCodeRequest request) {

        log.info("Applying promo code '{}' for account with id: {}",
                request.getPromoCode(), currentAccount.getId());
        CartResponseDto response = cartService.applyPromoCode(currentAccount.getId(), request.getPromoCode());
        return ResponseEntity.ok(response);
    }

    // удаление промокода из корзины
    @DeleteMapping("/promo")
    public ResponseEntity<CartResponseDto> removePromoCode(
            @CurrentAccount Account account) {
        log.info("Removing promo Code for account with id: {}", account.getId());
        CartResponseDto response = cartService.removePromoCode(account.getId());
        return ResponseEntity.ok(response);
    }
}
