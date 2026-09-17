package domain.service;

import domain.dto.request.CreateOrderRequest;
import domain.entity.Order;
import domain.mapper.OrderMapper;
import domain.repository.AccountRepository;
import domain.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderServiceImpl;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private OrderMapper orderMapper;

    @Test
    void createOrder_created_whenCartNotEmpty() {
        CreateOrderRequest cor = new CreateOrderRequest();
        cor.setAccountId(1L);
        cor.setComment("Ебаный рот этого Заказа");

        Order mapped = new Order();


    }

}
