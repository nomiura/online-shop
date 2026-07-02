package domain.controller;


import domain.dto.request.CreateOrderRequest;
import domain.dto.request.UpdateStatusOrderRequest;
import domain.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<Order>> getOrdersByAccount(@PathVariable Long accountId) {
        List<Order> orders = new ArrayList<>();
        return null;
    }


    @GetMapping("/{id}")
    public ResponseEntity<List<Order>> getOrders(@PathVariable Long id) {
        return null;

    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        return null;
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long id, @RequestBody UpdateStatusOrderRequest updateStatusOrderRequest) {
        return null;
    }
}
