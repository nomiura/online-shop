package domain.service;

import domain.dto.response.CartItemResponseDto;
import domain.dto.response.CartResponseDto;
import domain.entity.*;
import domain.exception.*;
import domain.mapper.CartMapper;
import domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private PromoCodeRepository promoCodeRepository;

    @Mock
    private CartMapper cartMapper;

    private static final Long ACCOUNT_ID = 5L;
    private static final Long CART_ID = 1L;
    private static final Long PRODUCT_ID = 43L;
    private static final Long PROMO_CODE_ID = 155L;
    private Account account;
    private Cart cart;
    private Product product;
    private CartResponseDto expectedDto;
    PromoCode promoCode;

    //подготовь -> выполни -> проверь
    //arrange -> act -> assert

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setId(ACCOUNT_ID);
        account.setEmail("test@example.ru");
        account.setPassword("password");
        account.setAccountType(AccountType.INDIVIDUAL);
        account.setCity("Moscow");
        account.setPhone("89839495738");

        cart = new Cart();
        cart.setId(CART_ID);
        cart.setAccount(account);

        promoCode = new PromoCode();
        promoCode.setId(PROMO_CODE_ID);
        promoCode.setCode("discount111");
        promoCode.setDiscountValue(BigDecimal.valueOf(111));
        promoCode.setDiscountType(DiscountType.FIXED);
        promoCode.setUsedCount(15);

        expectedDto = getCartResponseDto();
    }

    //GET
    //-------------------------------------------------------------------------
    @Test
    @DisplayName("getCart - Should return Cart for account by accountId")
    void getCart_shouldReturnCartForAccount() {
        // Arrange
        when(cartRepository.findByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.of(cart));
        when(cartMapper.cartToResponseDto(cart)).thenReturn(expectedDto);

        // Act
        CartResponseDto actualDto = cartService.getCart(ACCOUNT_ID);

        // Assert
        assertThat(actualDto).isNotNull();
        assertThat(actualDto.getCartId()).isEqualTo(CART_ID);
        assertThat(actualDto.getAccountId()).isEqualTo(ACCOUNT_ID);
        assertThat(actualDto.getItems().get(0).getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(actualDto.getTotalQuantity()).isEqualTo(1);
        assertThat(actualDto.getPromoCode()).isEqualTo("promo");

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(cartMapper, times(1)).cartToResponseDto(cart);
    }

    @Test
    @DisplayName("getCart - Should create new cart if not exists")
    void getCart_shouldCreateNewCartIfNotExists() {
        when(accountRepository.findById(ACCOUNT_ID))
                .thenReturn(Optional.of(account));
        when(cartRepository.findByAccountId(ACCOUNT_ID))
                .thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToResponseDto(cart)).thenReturn(expectedDto);


        CartResponseDto actualDto = cartService.getCart(ACCOUNT_ID);

        assertThat(actualDto).isNotNull();

        verify(accountRepository, times(1)).findById(ACCOUNT_ID);
        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(cartMapper, times(1)).cartToResponseDto(cart);
    }

    @Test
    @DisplayName("getCart - Should throw exception when account not found")
    void getCart_shouldThrowException_whenAccountNotFound() {
        when(cartRepository.findByAccountId(ACCOUNT_ID))
                .thenThrow(new AccountNotFoundException("Account not found by id: " + ACCOUNT_ID));

        assertThatThrownBy(() -> cartService.getCart(ACCOUNT_ID))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account not found by id: " + ACCOUNT_ID);

        verify(cartRepository, times(1)).findByAccountId(anyLong());
        verify(accountRepository, never()).findById(ACCOUNT_ID);
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartMapper, never()).cartToResponseDto(any(Cart.class));
        verifyNoMoreInteractions(accountRepository);
    }

    //POST
    //-------------------------------------------------------------------------
    @Test
    @DisplayName("addItem - Should add new item to cart")
    void addItem_shouldAddNewItemToCart() {
        int availableQuantity = 2;
        int initialQuantity = 1;
        int expectedQuantity = availableQuantity + initialQuantity;

        cart = getCart();
        Product product = cart.getItems().get(0).getProduct();

        expectedDto.getItems().get(0).setQuantity(expectedQuantity);
        expectedDto.setTotalQuantity(expectedQuantity);

        //when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.getQuantityAvailableById(PRODUCT_ID)).thenReturn(availableQuantity);
        when(cartMapper.cartToResponseDto(cart)).thenReturn(expectedDto);
        when(cartRepository.save(cart)).thenReturn(cart);

        CartResponseDto actualDto = cartService.addItem(ACCOUNT_ID, PRODUCT_ID, availableQuantity);

        assertThat(actualDto).isNotNull();
        assertThat(actualDto.getCartId()).isEqualTo(CART_ID);
        assertThat(actualDto.getAccountId()).isEqualTo(ACCOUNT_ID);
        assertThat(actualDto.getItems().get(0).getProductId()).isEqualTo(PRODUCT_ID);
        assertThat(actualDto.getTotalQuantity()).isEqualTo(expectedQuantity);

        //verify(accountRepository, times(1)).findById(ACCOUNT_ID);
        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(productRepository, times(1)).findById(PRODUCT_ID);
        verify(productRepository, times(1)).getQuantityAvailableById(PRODUCT_ID);
        verify(cartRepository, times(1)).save(any(Cart.class));
        verify(cartMapper, times(1)).cartToResponseDto(cart);
    }

    @Test
    @DisplayName("addItem - Should throw AccountNotFoundException when account not found")
    void addItem_shouldThrowException_ifAccountNotFound() {
        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.empty());
        when(accountRepository.findById(ACCOUNT_ID))
                .thenThrow(new AccountNotFoundException("Account not found by id: " + ACCOUNT_ID));

        assertThatThrownBy(() -> cartService.addItem(ACCOUNT_ID, PRODUCT_ID, 2))
                .isInstanceOf(AccountNotFoundException.class)
                .hasMessage("Account not found by id: " + ACCOUNT_ID);


        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(accountRepository, times(1)).findById(ACCOUNT_ID);
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartMapper, never()).cartToResponseDto(any(Cart.class));
    }

    @Test
    @DisplayName("addItem - Should throw ProductNotFoundException if product not found")
    void addItem_shouldThrowException_ifProductNotFound() {
        //when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));
        when(productRepository.findById(666L))
                .thenThrow(new ProductNotFoundException("Product not found by id: " + 666L));

        assertThatThrownBy(() -> cartService.addItem(ACCOUNT_ID, 666L, 1))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found by id: " + 666L);

        //verify(accountRepository,times(1)).findById(ACCOUNT_ID);
        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(productRepository, times(1)).findById(666L);
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartMapper, never()).cartToResponseDto(any(Cart.class));
    }

    @Test
    @DisplayName("addItem - Should throw InsufficientStockException when not enough stock")
    void addItem_shouldThrowException_ifStockNotEnough() {
        int availableQuantity = 1; //меньше, чем запрашиваемое кол-во(2)
        Cart cart = getCart();

        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));
        when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(productRepository.getQuantityAvailableById(PRODUCT_ID)).thenReturn(availableQuantity);

        assertThatThrownBy(() -> cartService.addItem(ACCOUNT_ID, PRODUCT_ID, 2))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Insufficient quantity of items. Available: " + availableQuantity);

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(productRepository, times(1)).findById(PRODUCT_ID);
        verify(productRepository, times(1)).getQuantityAvailableById(PRODUCT_ID);
        verify(cartRepository, never()).save(any(Cart.class));
        verify(cartMapper, never()).cartToResponseDto(any(Cart.class));
    }

    @Test
    @DisplayName("applyPromoCode - Should apply promo code")
    void applyPromoCode_shouldApplyPromoCode() {
        Cart cart = getCart();
        expectedDto.setAccountId(ACCOUNT_ID);
        expectedDto.setPromoCode(promoCode.getCode());
        expectedDto.setDiscountPrice(BigDecimal.valueOf(111));

        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));
        when(promoCodeRepository.findByCode(promoCode.getCode()))
                .thenReturn(Optional.of(promoCode));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToResponseDto(any(Cart.class))).thenReturn(expectedDto);

        CartResponseDto actualDto = cartService.applyPromoCode(ACCOUNT_ID, promoCode.getCode());

        assertThat(cart.getPromoCode()).isEqualTo(actualDto.getPromoCode());
        assertThat(actualDto.getDiscountPrice()).isEqualTo(expectedDto.getDiscountPrice());

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(promoCodeRepository, times(1)).findByCode(promoCode.getCode());
        verify(promoCodeRepository, times(1)).save(promoCode);
        verify(cartRepository, times(1)).save(cart);
        verify(cartMapper, times(1)).cartToResponseDto(cart);
    }

    @Test
    @DisplayName("applyPromoCode - Should throw PromoCodeException when promo code is invalid")
    void applyPromoCode_shouldThrowException_ifPromoCodeInvalid() {
        Cart cart = getCart();
        String fakePromoCode = "fakefakeeverythingislie";

        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));
        when(promoCodeRepository.findByCode(fakePromoCode))
                .thenThrow(new PromoCodeException("PromoCode not found with promo code: " + fakePromoCode));

        assertThatThrownBy(() -> cartService.applyPromoCode(ACCOUNT_ID, fakePromoCode))
                .isInstanceOf(PromoCodeException.class)
                .hasMessage("PromoCode not found with promo code: " + fakePromoCode);

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(promoCodeRepository, times(1)).findByCode(fakePromoCode);
        verify(promoCodeRepository, never()).save(promoCode);
        verify(cartRepository, never()).save(cart);
        verify(cartMapper, never()).cartToResponseDto(cart);
    }

    //PATCH
    //-------------------------------------------------------------------------
    @Test
    @DisplayName("updateItemQuantity - Should update item quantity")
    void updateItemQuantity_shouldUpdateItemQuantity_ifItemExists() {
        Cart cart = getCart();
        int newQuantity = 3;
        int availableQuantity = 5;
        expectedDto.getItems().get(0).setQuantity(newQuantity);

        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));
        when(productRepository.getQuantityAvailableById(PRODUCT_ID)).thenReturn(availableQuantity);
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToResponseDto(any(Cart.class))).thenReturn(expectedDto);

        CartResponseDto actualDto = cartService.updateItemQuantity(ACCOUNT_ID, PRODUCT_ID, newQuantity);

        assertThat(actualDto).isNotNull();
        assertThat(actualDto.getItems().get(0).getQuantity()).isEqualTo(newQuantity);

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(productRepository, times(1)).getQuantityAvailableById(PRODUCT_ID);
        verify(cartRepository, times(1)).save(cart);
        verify(cartMapper, times(1)).cartToResponseDto(cart);
    }

    @Test
    @DisplayName("updateItemQuantity - Should throw exception if item with invalid product id not found")
    void updateItemQuantity_shouldUpdateItemQuantity_ifItemNotFound() {
        Cart cart = getCart();
        int newQuantity = 3;
        Long nonExistsProductId = 999L;

        cart.getItems().clear();
        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartService.updateItemQuantity(ACCOUNT_ID, nonExistsProductId, newQuantity))
                .isInstanceOf(CartItemNotFoundException.class)
                .hasMessage("CartItem not found with product id: " + nonExistsProductId);

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(productRepository, never()).getQuantityAvailableById(PRODUCT_ID);
        verify(cartRepository, never()).save(cart);
        verify(cartMapper, never()).cartToResponseDto(cart);
    }

    //DELETE
    //-------------------------------------------------------------------------
    @Test
    @DisplayName("removeItem - Should remove item from cart")
    void removeItem_shouldRemoveItem_ifItemExists() {
        cart = getCart();
        List<CartItem> newCartItems = getAdditionalCartItems(cart);
        cart.getItems().addAll(newCartItems); //тут есть productId = 65L

        Long productIdToRemove = 65L;

        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.removeItem(ACCOUNT_ID, productIdToRemove);

        assertThat(cart.getItems().stream()
                .noneMatch(item -> item.getProduct().getProductId().equals(productIdToRemove)));

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    @DisplayName("removeItem - Should throw exception if item not found")
    void removeItem_shouldThrowException_ifItemNotFound() {
        cart = getCart();
        List<CartItem> newCartItems = getAdditionalCartItems(cart);
        for (CartItem cartItem : newCartItems) { //тут нет productId = 999L;
            cart.getItems().add(cartItem);
        }
        Long productIdToRemove = 999L;

        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartService.removeItem(ACCOUNT_ID, productIdToRemove))
                .isInstanceOf(CartItemNotFoundException.class)
                .hasMessage("CartItem not found with product id: " + productIdToRemove);

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(cartRepository, never()).save(cart);
    }

    @Test
    @DisplayName("clearCart - Should clear all items")
    void clearCart_shouldClearAllItems() {
        cart = getCart();

        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        cartService.clearCart(ACCOUNT_ID);

        assertThat(cart.getItems().size()).isEqualTo(0);

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    @DisplayName("clearCart - Should throw exception if cart not found")
    void clearCart_shouldThrowException_ifCartNotFound() {
//        when(cartRepository.findByAccountId(ACCOUNT_ID))
//                .thenThrow(new CartEmptyException("Cart not found for account with id: " + ACCOUNT_ID));
        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cartService.clearCart(ACCOUNT_ID))
                .isInstanceOf(CartEmptyException.class)
                .hasMessage("Cart not found for account with id: " + ACCOUNT_ID);

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(cartRepository, never()).save(cart);
        verify(cartMapper, never()).cartToResponseDto(cart);
    }

    @Test
    @DisplayName("removePromoCode - Should remove promo code from cart")
    void removePromoCode_shouldRemovePromoCode() {
        cart = getCart();
        cart.setPromoCode("discount10");
        expectedDto.setPromoCode(null);
        expectedDto.setDiscountPrice(BigDecimal.ZERO);
        promoCode.setCode("discount10");
        promoCode.setUsedCount(1);

        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);
        when(cartMapper.cartToResponseDto(cart)).thenReturn(expectedDto);
        when(promoCodeRepository.findByCode("discount10")).thenReturn(Optional.of(promoCode));

        CartResponseDto actualDto = cartService.removePromoCode(ACCOUNT_ID);

        assertThat(actualDto.getPromoCode()).isNull();
        assertThat(cart.getPromoCode()).isNull();
        assertThat(actualDto.getDiscountPrice()).isZero();
        assertThat(actualDto).isEqualTo(expectedDto);
        assertThat(promoCode.getUsedCount()).isEqualTo(0);

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(cartRepository, times(1)).save(cart);
        verify(promoCodeRepository, times(1)).save(promoCode);
        verify(cartMapper, times(1)).cartToResponseDto(cart);
    }

    @Test
    @DisplayName("removePromoCode - Should throw PromoCodeException if promo code is not applied")
    void removePromoCode_shouldThrowException_ifNoPromoCodeApplied() {
        cart = getCart();
        cart.setPromoCode(null);
        expectedDto.setPromoCode(null);
        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartService.removePromoCode(ACCOUNT_ID))
                .isInstanceOf(PromoCodeException.class)
                .hasMessage("No promo code applied to the cart.");

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(cartRepository, never()).save(cart);
        verify(promoCodeRepository, never()).findByCode("discount10");
    }


    // ORDER ---------------------------------------------------------------------------

    @Test
    @DisplayName("convertCartToOrder - Should convert cart to order")
    void convertCartToOrder_shouldConvertCartToOrder() {
        cart = getCart();
        Order order = getOrder();
        BigDecimal expectedTotal = cart.getTotalPrice();

        List<CartItem> originalCartItems = new ArrayList<>(cart.getItems());

        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));
        when(accountRepository.findById(ACCOUNT_ID)).thenReturn(Optional.of(account));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setOrderId(44L); // только id, остальное уже установлено сервисом
            return saved;
        });
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Order actualOrder = cartService.convertCartToOrder(ACCOUNT_ID);

        assertThat(actualOrder).isNotNull();
        assertThat(actualOrder.getOrderId()).isEqualTo(44L);
        assertThat(actualOrder.getOrderStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(actualOrder.getCreatedBy().getId()).isEqualTo(ACCOUNT_ID);
        assertThat(actualOrder.getPrice()).isEqualTo(expectedTotal);

        // Используем сохраненную копию для проверки
        for (int i = 0; i < actualOrder.getItems().size(); i++) {
            OrderItem orderItem = actualOrder.getItems().get(i);
            CartItem cartItem = originalCartItems.get(i); // используем копию
            assertThat(orderItem.getProduct()).isEqualTo(cartItem.getProduct());
            assertThat(orderItem.getQuantity()).isEqualTo(cartItem.getQuantity());
            assertThat(orderItem.getPriceAtPurchase()).isEqualTo(cartItem.getProduct().getCurrentPrice());
        }

        // Проверяем, что корзина очищена
         assertThat(cart.getItems().size()).isEqualTo(0);

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(accountRepository, times(1)).findById(ACCOUNT_ID);
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    @DisplayName("convertCartToOrder - Should throw exception if cart is empty")
    void convertCartToOrder_shouldThrowException_ifCartIsEmpty() {
        cart = getCart();
        cart.getItems().clear();

        when(cartRepository.findByAccountId(ACCOUNT_ID)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> cartService.convertCartToOrder(ACCOUNT_ID))
                .isInstanceOf(CartEmptyException.class)
                .hasMessage("Cannot convert empty cart to order.");

        verify(cartRepository, times(1)).findByAccountId(ACCOUNT_ID);
        verify(accountRepository, never()).save(any(Account.class));
        verify(cartRepository, never()).save(any(Cart.class));
        verify(orderRepository, never()).save(any(Order.class));
    }

    private CartResponseDto getCartResponseDto() {
        CartItemResponseDto cartItemResponseDto = new CartItemResponseDto();
        cartItemResponseDto.setCartItemId(15L);
        cartItemResponseDto.setProductId(PRODUCT_ID);
        cartItemResponseDto.setQuantity(1);
        cartItemResponseDto.setAdditionPrice(BigDecimal.valueOf(1111));

        List<CartItemResponseDto> cartItemDto = new ArrayList<>();
        cartItemDto.add(cartItemResponseDto);

        CartResponseDto response = new CartResponseDto();
        response.setAccountId(ACCOUNT_ID);
        response.setCartId(CART_ID);
        response.setItems(cartItemDto);
        response.setTotalQuantity(1);
        response.setTotalPrice(BigDecimal.valueOf(1000));
        response.setSubtotalPrice(BigDecimal.valueOf(1111));
        response.setDiscountPrice(BigDecimal.valueOf(111));
        response.setPromoCode("promo");

        return response;
    }


    private Cart getCart() {
        cart = new Cart();
        cart.setId(CART_ID);
        List<CartItem> cartItems = new ArrayList<>();

        product = new Product();
        product.setProductId(PRODUCT_ID);
        product.setQuantityAvailable(2);
        product.setCurrentPrice(BigDecimal.valueOf(1111));

        CartItem cartItem = new CartItem();
        cartItem.setId(15L);
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cartItem.setQuantity(1);
        cartItem.setPriceAddition(BigDecimal.valueOf(1111));

        cartItems.add(cartItem);
        cart.setItems(cartItems);

        return cart;
    }

    private List<CartItem> getAdditionalCartItems(Cart cart) {
        List<CartItem> cartItems = new ArrayList<>();

        Product firstProd = new Product();
        firstProd.setProductId(65L);
        firstProd.setQuantityAvailable(15);
        firstProd.setCurrentPrice(BigDecimal.valueOf(5200));
        firstProd.setDiscountPercent(10);

        Product secondProd = new Product();
        secondProd.setProductId(66L);
        secondProd.setQuantityAvailable(40);
        secondProd.setCurrentPrice(BigDecimal.valueOf(200));
        secondProd.setDiscountPercent(20);

        Product thirdProd = new Product();
        thirdProd.setProductId(67L);
        thirdProd.setQuantityAvailable(10);
        thirdProd.setCurrentPrice(BigDecimal.valueOf(300));
        thirdProd.setDiscountPercent(30);

        CartItem firstCartItem = new CartItem();
        firstCartItem.setId(144L);
        firstCartItem.setCart(cart);
        firstCartItem.setProduct(firstProd);
        firstCartItem.setQuantity(2);
        firstCartItem.setPriceAddition(BigDecimal.valueOf(5200));
        firstCartItem.setOriginalPrice(BigDecimal.valueOf(5200));

        CartItem secondCartItem = new CartItem();
        secondCartItem.setId(145L);
        secondCartItem.setCart(cart);
        secondCartItem.setProduct(secondProd);
        secondCartItem.setQuantity(1);
        secondCartItem.setPriceAddition(BigDecimal.valueOf(200));
        secondCartItem.setOriginalPrice(BigDecimal.valueOf(200));

        CartItem thirdCartItem = new CartItem();
        thirdCartItem.setId(146L);
        thirdCartItem.setCart(cart);
        thirdCartItem.setProduct(thirdProd);
        thirdCartItem.setQuantity(5);
        thirdCartItem.setPriceAddition(BigDecimal.valueOf(300));
        thirdCartItem.setOriginalPrice(BigDecimal.valueOf(300));

        cartItems.add(firstCartItem);
        cartItems.add(secondCartItem);
        cartItems.add(thirdCartItem);

        return cartItems;
    }

    public Order getOrder() {
        cart = getCart();

        Order order = new Order();
        order.setOrderId(44L);
        order.setCreatedBy(account);
        order.setPrice(cart.getTotalPrice());
        order.setOrderStatus(OrderStatus.CREATED);
        order.setItems(new ArrayList<>());
        return order;
    }
}