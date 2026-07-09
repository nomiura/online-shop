package domain.service;

import domain.dto.response.CartResponseDto;
import domain.entity.*;
import domain.exception.AccountNotFoundException;
import domain.mapper.CartMapper;
import domain.repository.AccountRepository;
import domain.repository.CartRepository;
import domain.repository.OrderRepository;
import domain.repository.ProductRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import domain.exception.*;

@Data
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    @Override
    public CartResponseDto getCart(Long accountId) {
        log.info("Getting cart with account id: {}", accountId + "...");
        Cart cart = cartRepository.findByAccountId(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account not found by account id: " + accountId)
        );
        log.info("Cart with account id: {} is found", accountId + "...");
        return cartMapper.cartToResponseDto(cart);
    }


    private Cart getOrCreateCart(Long accountId) {
        return cartRepository.findByAccountId(accountId)
                .orElseGet(() -> createCart(accountId));
    }


    private Cart createCart(Long accountId) {
        log.info("Creating new cart for account: {}", accountId);

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found by id: " + accountId));

        Cart cart = new Cart();
        cart.setAccount(account);
        return cartRepository.save(cart);
    }

    @Transactional
    @Override
    public CartResponseDto addItem(Long accountId, Long productId, Integer quantity) {
        log.info("Adding {} item(s) {} to account {}", quantity, productId, accountId);

        Cart cart = getOrCreateCart(accountId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + productId));

        int available = productRepository.getQuantityAvailable(productId);
        if(available < quantity) {
            throw new InsufficientStockException("Insufficient quantity of items. Available: " + available);
        }

        CartItem existingItem = cart.getItems().stream()
                .filter(items -> items.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if(existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPriceAddition(product.getCurrentPrice());

            cart.getItems().add(cartItem);
        }

        //пересчитывает итоговые суммы в корзине
        cart.recalculateTotals();

        Cart savedCart = cartRepository.save(cart);
        log.info("Item added to cart {}: product={}, quantity={}",
                savedCart.getId(), productId, quantity);

        return cartMapper.cartToResponseDto(savedCart);
    }

    @Transactional
    @Override
    public CartResponseDto updateItemQuantity(Long accountId, Long productId, Integer quantity) {
        log.info("Updating quantity for product {} to {}", productId, quantity);

        Cart cart = getOrCreateCart(accountId);

        //фильтруем по productId
        CartItem item = cart.getItems().stream()
                        .filter(i -> i.getProduct().getId().equals(productId))
                        .findFirst()
                        .orElseThrow(() -> new CartItemNotFoundException("CartItem not found with product id: " + productId));

        //проверка остатков
        int available = productRepository.getQuantityAvailable(productId);
        if(available < quantity) {
            throw new InsufficientStockException("Insufficient quantity. Available: " + available + ", requested: " + quantity);
        }
        item.setQuantity(quantity);
        cart.recalculateTotals();

        log.info("Changed quantity to {} for product with id: {}", quantity, productId);
        Cart savedCart = cartRepository.save(cart);

        return cartMapper.cartToResponseDto(savedCart);
    }

    @Transactional
    @Override
    public void removeItem(Long accountId, Long productId) {
        log.info("Removing product {} from account {}", productId, accountId);

        Cart cart = getOrCreateCart(accountId);
        CartItem item = cart.getItems().stream()
                .filter(i -> i.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("CartItem not found with product id: " + productId));

        cart.getItems().remove(item);

        //если корзина пустая, то ее можно удалить
        if(cart.getItems().isEmpty()) {
            cartRepository.delete(cart);
            log.info("Cart {} became empty and was deleted", cart.getId());
        } else {
            cart.recalculateTotals();
            cartRepository.save(cart);
        }

        log.info("Removed product {} from account {}", productId, accountId);
    }

    @Transactional
    @Override
    public void clearCart(Long accountId) {
        log.info("Clearing cart for account {}", accountId);

        Cart cart = cartRepository.findByAccountId(accountId)
                        .orElseThrow(() -> new CartEmptyException("Cart not found for account with id: " + accountId));

        //если корзина пуста
        if (cart.getItems().isEmpty()) {
            log.info("Cart for account {} is already empty", accountId);
            return;
        }
        cart.getItems().clear();
        cart.recalculateTotals();
        cartRepository.save(cart);

        log.info("Cart is successfully cleared for account {}", accountId);
    }

    @Transactional
    @Override
    public CartResponseDto applyPromoCode(Long accountId, String promoCode) {
        return null;
    }

    @Transactional
    @Override
    public CartResponseDto removePromoCode(Long accountId) {
        return null;
    }

    @Transactional
    @Override
    public Order convertCartToOrder(Long accountId) {
        return null;
    }
}
