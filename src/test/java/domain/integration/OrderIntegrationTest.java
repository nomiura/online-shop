package domain.integration;

import domain.entity.Product;
import domain.repository.AccountRepository;
import domain.repository.ProductRepository;
import domain.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Testcontainers
class OrderIntegrationTest {
    @Autowired
    OrderService orderService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    AccountRepository accountRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:16");

    @Test
    void contextLoads() {
    }

    @Test
    void createOrder_whenTwoBuyersOneItem_onlyOneSucceeds() {
        Product product = new Product();
        product.setName("Говяжий анус");
        product.setCurrentPrice(new BigDecimal("100.00"));
        product.setQuantityAvailable(1);
        product = productRepository.save(product);

        assertThat(productRepository.getQuantityAvailableById(product.getProductId())).isEqualTo(1);
    }

}
