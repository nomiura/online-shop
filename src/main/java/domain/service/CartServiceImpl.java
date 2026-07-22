package domain.service;

import domain.dto.response.CartResponseDto;
import domain.entity.*;
import domain.exception.AccountNotFoundException;
import domain.mapper.CartMapper;
import domain.repository.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import domain.exception.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Slf4j
@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    private final AccountRepository accountRepository;
    private final ProductRepository productRepository;
    private final PromoCodeRepository promoCodeRepository;
    private final OrderRepository orderRepository;

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

        int available = productRepository.getQuantityAvailableById(productId);
        if (available < quantity) {
            throw new InsufficientStockException("Insufficient quantity of items. Available: " + available);
        }

        CartItem existingItem = cart.getItems().stream()
                .filter(items -> items.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
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
                .filter(i -> i.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("CartItem not found with product id: " + productId));

        //проверка остатков
        int available = productRepository.getQuantityAvailableById(productId);
        if (available < quantity) {
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
                .filter(i -> i.getProduct().getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new CartItemNotFoundException("CartItem not found with product id: " + productId));

        cart.getItems().remove(item);

        //если корзина пустая, то ее можно удалить
        if (cart.getItems().isEmpty()) {
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
    public CartResponseDto applyPromoCode(Long accountId, String code) {
        Cart cart = getOrCreateCart(accountId);

        PromoCode promoCode = promoCodeRepository.findByCode(code)
                .orElseThrow(() -> new PromoCodeException("PromoCode not found with promo code: " + code));

        //валидация промокода для корзины
        validatePromoCodeForCart(promoCode, cart);

        //расчет скидки
        BigDecimal discountedPrice = calculateDiscount(promoCode, cart.getSubtotalPrice()); //subtotalprice!

        //применяем к корзине
        cart.setPromoCode(code);
        cart.setDiscountPrice(discountedPrice);

        //пересчет всех сумм уже с учетом промокода
        cart.recalculateTotals(); //пересчет ВСЕХ сумм

        //обновляем счетчик использований промокода
        promoCode.setUsedCount(promoCode.getUsedCount() + 1);
        promoCodeRepository.save(promoCode);

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.cartToResponseDto(savedCart);
    }

    private void validatePromoCodeForCart(PromoCode promoCode, Cart cart) {
        //базовая валидация
        validatePromoCode(promoCode);

        if (promoCode.getMinOrderAmount() != null) {
            if (cart.getSubtotalPrice().compareTo(promoCode.getMinOrderAmount()) < 0) {
                throw new PromoCodeException("Minimum amount of order should be reached: "
                        + promoCode.getMinOrderAmount());

            }
        }

        if (promoCode.getCode().equals(cart.getPromoCode())) {
            throw new PromoCodeException("Promo code is already applied");
        }
    }

    private void validatePromoCode(PromoCode promoCode) {
        if (!promoCode.getIsActive()) throw new PromoCodeException("PromoCode is no longer valid.");
        LocalDateTime now = LocalDateTime.now();

        if (promoCode.getTimeValidFrom() != null && now.isBefore(promoCode.getTimeValidFrom())) {
            throw new PromoCodeException("Promo code is not active for now. The starting time is "
                    + promoCode.getTimeValidFrom());
        }

        if (promoCode.getTimeValidTo() != null && now.isAfter(promoCode.getTimeValidTo())) {
            throw new PromoCodeException("Promo code is no longer active.");
        }

        if (promoCode.getUsageLimit() != null) {
            if (promoCode.getUsedCount() >= promoCode.getUsageLimit()) {
                throw new PromoCodeException("Promo code is already used. Limit usage: "
                        + promoCode.getUsageLimit());
            }
        }
    }

    private BigDecimal calculateDiscount(PromoCode promoCode, BigDecimal subtotalPrice) {
        BigDecimal discountPrice = BigDecimal.ZERO;

        if (promoCode.getDiscountType() == DiscountType.FIXED) {
            discountPrice = promoCode.getDiscountValue();
            //скидка не может быть больше суммы заказа
            if (discountPrice.compareTo(subtotalPrice) > 0) {
                discountPrice = subtotalPrice;
            }
        } else if (promoCode.getDiscountType() == DiscountType.PERCENT) {
            discountPrice = subtotalPrice.multiply(promoCode.getDiscountValue())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        }

        if (promoCode.getMaxDiscount() != null && discountPrice.compareTo(promoCode.getMaxDiscount()) > 0) {
            discountPrice = promoCode.getMaxDiscount();
        }

        return discountPrice.setScale(2, RoundingMode.HALF_UP);
    }

    @Transactional
    @Override
    public CartResponseDto removePromoCode(Long accountId) {
        Cart cart = getOrCreateCart(accountId);

        if (cart.getPromoCode() == null) {
            throw new PromoCodeException("No promo code applied to the cart.");
        }

        PromoCode promoCode = promoCodeRepository.findByCode(cart.getPromoCode())
                .orElseThrow(() -> new PromoCodeException("PromoCode not found with promo code: " + cart.getPromoCode()));

        //уменьшаем счетчик использования промокода (пользователь отменил применение)
        if (promoCode.getUsedCount() > 0) {
            promoCode.setUsedCount(promoCode.getUsedCount() - 1);
            promoCodeRepository.save(promoCode);
        }

        //очищаем промокод в корзине
        cart.setPromoCode(null);
        cart.setPromoCodeDiscount(BigDecimal.ZERO);

        //пересчет сумм
        cart.recalculateTotals();

        Cart savedCart = cartRepository.save(cart);
        return cartMapper.cartToResponseDto(savedCart);
    }

    @Transactional
    @Override
    public Order convertCartToOrder(Long accountId) {
        Cart cart = cartRepository.findByAccountId(accountId).orElseThrow(
                () -> new AccountNotFoundException("Account not found by account id: " + accountId)
        );

        if (cart.getItems().isEmpty()) {
            throw new CartEmptyException("Cannot convert empty cart to order.");
        }

        Order order = new Order();
        order.setCreatedBy(accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found by account id: " + accountId)));
        order.setOrderStatus(OrderStatus.CREATED);

        // преобразуем эл-ы корзины в эл-ы заказа
        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                        OrderItem item = new OrderItem();
                        item.setOrder(order);
                        item.setProduct(cartItem.getProduct());
                        item.setQuantity(cartItem.getQuantity());
                        item.setPriceAtPurchase(cartItem.getProduct().getCurrentPrice()); //фикс цены на момент заказа
                        return item;
                })
                .toList();
        order.setItems(orderItems);

        BigDecimal totalPrice = cart.getTotalPrice();
        order.setPrice(totalPrice);

        Order savedOrder = orderRepository.save(order);

        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }
}
