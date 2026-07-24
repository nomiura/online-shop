package domain.controller;

import domain.config.SecurityConfig;
import domain.dto.request.CartAddRequest;
import domain.dto.request.CartPromoCodeRequest;
import domain.dto.request.CartUpdateRequest;
import domain.dto.response.CartItemResponseDto;
import domain.dto.response.CartResponseDto;
import domain.entity.Account;
import domain.entity.AccountType;
import domain.exception.AccessDeniedException;
import domain.exception.GlobalExceptionHandler;
import domain.exception.PromoCodeException;
import domain.mapper.CartMapper;
import domain.resolver.CurrentAccountArgumentResolver;
import domain.service.CartService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private CartController cartController;

    @Mock
    private CartService cartService;

    @Mock
    private CurrentAccountArgumentResolver currentAccountArgumentResolver; // Добавить мок для резолвера

    private static final Long ACCOUNT_ID = 5L;

    private Account account;

    @BeforeEach
    void setUp() {
        //настраиваем MockMvc с контроллером и резолвером
        mockMvc = MockMvcBuilders
                .standaloneSetup(cartController)
                .setCustomArgumentResolvers(currentAccountArgumentResolver) //наш кастомный резолвер
                .setControllerAdvice(new GlobalExceptionHandler())  //наш кастомный глобал хэндлер
                .build();

        account = new Account();
        account.setId(ACCOUNT_ID);
        account.setEmail("test@example.ru");
        account.setPassword("password");
        account.setAccountType(AccountType.INDIVIDUAL);
        account.setCity("Moscow");
        account.setPhone("89839495738");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    //GET
    //-------------------------------------------------------------------------

    @Test
    //@WithMockUser(username = "1") //id пользователя как строка
    @DisplayName("Should return 200 OK and dto the existing cart")
    void getCart_shouldReturnCart() throws Exception {
        CartResponseDto response = getCartResponseDto();

        when(currentAccountArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentAccountArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(account);

        when(cartService.getCart(ACCOUNT_ID)).thenReturn(response);

        mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(cartService, times(1)).getCart(ACCOUNT_ID);
    }

    @Test
    @DisplayName("GET /cart - Should return 401 when user is not authenticated")
    void getCart_shouldReturnUnauthorized() throws Exception {
        //резолвер выбрасывает исключение, сервис не нужно вызывать, тк отказано в доступе
        when(currentAccountArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentAccountArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenThrow(new AccessDeniedException("User is not authenticated"));

        mockMvc.perform(get("/cart")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());

        verify(cartService, never()).getCart(anyLong());
    }

    @Test
    @DisplayName("POST /cart/items - Should return 201 isCreated add item to cart and return CREATED 201")
    void addItem() throws Exception {
        CartAddRequest request = new CartAddRequest(43L, 1);
        CartResponseDto response = getCartResponseDto();

        //мокаем резолвер - говорим, что он поддерживает наш параметр, и что вернет наш аккаунт
        when(currentAccountArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentAccountArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(account);

        //мокаем сервис
        when(cartService.addItem(ACCOUNT_ID, request.getProductId(), request.getQuantity()))
                .thenReturn(response);


        String requestJson = objectMapper.writeValueAsString(request);

        //act & assert
        mockMvc.perform(post("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cartId").value(ACCOUNT_ID))
                .andExpect(jsonPath("$.items[0].productId").value(request.getProductId()))
                .andExpect(jsonPath("$.items[0].quantity").value(request.getQuantity()))
                .andExpect(jsonPath("$.totalQuantity").value(1))
                .andExpect(jsonPath("$.promoCode").value("promo"));

        verify(cartService, times(1))
                .addItem(ACCOUNT_ID, request.getProductId(), request.getQuantity());
    }

    @Test
    @DisplayName("PATCH /cart/items - Should return 200 isOk and change the quantity of item")
    void updateItemQuantity() throws Exception {
        Long cartItemId = 15L;
        CartUpdateRequest request = new CartUpdateRequest(43L, cartItemId, 4);
        CartResponseDto response = getCartResponseDto();
        response.getItems().getFirst().setQuantity(4);

        when(currentAccountArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentAccountArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(account);

        when(cartService.updateItemQuantity(ACCOUNT_ID, cartItemId, 4))
                .thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/cart/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cartId").value(ACCOUNT_ID))
                .andExpect(jsonPath("$.items[0].productId").value(43L))
                .andExpect(jsonPath("$.items[0].quantity").value(4));

        verify(cartService, times(1)).updateItemQuantity(ACCOUNT_ID, cartItemId, 4);
    }

    @Test
    @DisplayName("DELETE/cart/items/{cartItemId} - Should return 204 No Content and delete item from cart")
    void removeItem() throws Exception {
        Long cartItemId = 15L;

        when(currentAccountArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentAccountArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(account);

        doNothing().when(cartService).removeItem(ACCOUNT_ID, cartItemId);

        mockMvc.perform(delete("/cart/items/{cartItemId}", cartItemId))
                .andExpect(status().isNoContent()); //204 No Content

        verify(cartService, times(1)).removeItem(ACCOUNT_ID, cartItemId);
    }

    @Test
    @DisplayName("DELETE /cart - Should return 204 No Contend and clear the cart")
    void clearCart() throws Exception {
        when(currentAccountArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentAccountArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(account);

        doNothing().when(cartService).clearCart(ACCOUNT_ID);

        mockMvc.perform(delete("/cart"))
                .andExpect(status().isNoContent());

        verify(cartService, times(1)).clearCart(ACCOUNT_ID);
        verifyNoMoreInteractions(cartService);
    }

    @Test
    @DisplayName("DELETE /cart - Should return 401 Unauthorized and throw AccessDeniedException")
    void clearCart_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        when(currentAccountArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentAccountArgumentResolver.resolveArgument(any(), any(), any(), any()))
                .thenThrow(new AccessDeniedException("User is not authenticated"));

        mockMvc.perform(delete("/cart"))
                .andExpect(status().isUnauthorized());

        verify(cartService, never()).clearCart(ACCOUNT_ID);
    }

    @Test
    @DisplayName("POST /cart/promo - Should return 200 OK and apply the promo code")
    void applyPromoCode() throws Exception {
        String promoCode = "discount10";
        CartPromoCodeRequest request = new CartPromoCodeRequest(promoCode);
        CartResponseDto response = getCartResponseDto();
        response.setPromoCode(promoCode);

        when(currentAccountArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentAccountArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(account);

        when(cartService.applyPromoCode(account.getId(), request.getPromoCode())).thenReturn(response);

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/cart/promo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promoCode").value(promoCode));

        verify(cartService, times(1)).applyPromoCode(account.getId(), request.getPromoCode());
    }

    @Test
    @DisplayName("POST /cart/promo - Should return 200 OK and apply the promo code")
    void applyPromoCode_withInvalidPromo_shouldReturn400BadRequest() throws Exception {
        String promoCode = "This is invalid promoCode";
        CartPromoCodeRequest request = new CartPromoCodeRequest(promoCode);

        when(currentAccountArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentAccountArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(account);

        when(cartService.applyPromoCode(account.getId(), request.getPromoCode()))
                .thenThrow(new PromoCodeException("Invalid promo code"));

        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/cart/promo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest()); //400

        verify(cartService, times(1)).applyPromoCode(account.getId(), promoCode);
    }

    @Test
    @DisplayName("DELETE /cart/promo - Should return 200 OK and return cartResponseDto")
    void removePromoCode() throws Exception {
        CartResponseDto response = getCartResponseDto();
        response.setPromoCode(null);

        when(currentAccountArgumentResolver.supportsParameter(any())).thenReturn(true);
        when(currentAccountArgumentResolver.resolveArgument(any(), any(), any(), any())).thenReturn(account);

        when(cartService.removePromoCode(account.getId())).thenReturn(response);

        mockMvc.perform(delete("/cart/promo")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.promoCode").doesNotExist())
                .andExpect(jsonPath("$.promoCode").isEmpty())
                .andExpect(jsonPath("$.cartId").value(ACCOUNT_ID));

        verify(cartService, times(1)).removePromoCode(account.getId());
    }

    private CartResponseDto getCartResponseDto() {
        CartItemResponseDto cartItemResponseDto = new CartItemResponseDto();
        cartItemResponseDto.setCartItemId(15L);
        cartItemResponseDto.setProductId(43L);
        cartItemResponseDto.setQuantity(1);
        cartItemResponseDto.setAdditionPrice(BigDecimal.valueOf(1111));

        List<CartItemResponseDto> cartItemDto = new ArrayList<>();
        cartItemDto.add(cartItemResponseDto);


        CartResponseDto response = new CartResponseDto();
        response.setCartId(5L);
        response.setItems(cartItemDto);
        response.setTotalQuantity(1);
        response.setTotalPrice(BigDecimal.valueOf(1000));
        response.setSubtotalPrice(BigDecimal.valueOf(1111));
        response.setDiscountPrice(BigDecimal.valueOf(111));
        response.setPromoCode("promo");
        return response;
    }
}