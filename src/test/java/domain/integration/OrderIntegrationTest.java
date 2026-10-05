package domain.integration;

import domain.dto.request.CreateOrderRequest;
import domain.dto.response.OrderResponse;
import domain.entity.*;
import domain.exception.InsufficientStockException;
import domain.repository.AccountRepository;
import domain.repository.ProductRepository;
import domain.service.CartService;
import domain.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Testcontainers
class OrderIntegrationTest {
    @Autowired
    CartService cartService;
    @Autowired
    OrderService orderService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    AccountRepository accountRepository;
    @Autowired


    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Test
    void contextLoads() {
    }

    private Long createAccountWithItems(String email, String phone, Long productId) {
        Account account = new Account();
        account.setEmail(email);
        account.setPhone(phone);
        account.setPassword("password");
        account.setCity("Moscow");
        account.setAccountType(AccountType.INDIVIDUAL);
        account = accountRepository.save(account);

        cartService.addItem(account.getId(), productId, 1);
        return account.getId();
    }

    private CreateOrderRequest orderRequestFor(Long accountId) {
        return new CreateOrderRequest(accountId, "Я русский");
    }

    private boolean succeeded (Future < ? > f) throws Exception {
        try {
            f.get();
            return true;
        } catch (ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(InsufficientStockException.class);
            return false;
        }

    }

    @Test
    void createOrder_whenTwoBuyersOneItem_onlyOneSucceeds() throws Exception {
        Product product = new Product();
        product.setName("Говяжий анус");
        product.setCurrentPrice(new BigDecimal("100.00"));
        product.setQuantityAvailable(1);
        product = productRepository.save(product);

        assertThat(productRepository.getQuantityAvailableById(product.getProductId())).isEqualTo(1);

        Long firstAccountId = createAccountWithItems("a1@test.ru", "+79504937865", product.getProductId());
        Long secondAccountId = createAccountWithItems("a2@test.ru", "+79348971256", product.getProductId());

        ExecutorService ex = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);

        Future f1 = ex.submit(() -> {
            try {
                startLatch.await();
                orderService.createOrder(orderRequestFor(firstAccountId));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        });
        Future f2 = ex.submit(() -> {
            try {
                startLatch.await();
                orderService.createOrder(orderRequestFor(secondAccountId));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        startLatch.countDown();

        boolean firstWon = succeeded(f1);
        boolean secondWon = succeeded(f2);

        ex.shutdown();
        try {
            ex.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
