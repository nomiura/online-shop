package domain.controller;


import domain.dto.request.CreateOrderRequest;
import domain.dto.request.UpdateDescriptionRequest;
import domain.dto.response.OrderResponse;
import domain.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByAccount(@PathVariable Long accountId) {
        return ResponseEntity.ok(orderService.findByAccountId(accountId));
    }


    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.findById(orderId));

    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId));
    }

    @PatchMapping("/{orderId}/description")
    public ResponseEntity<OrderResponse> updateDescription(@PathVariable Long orderId, @Valid @RequestBody UpdateDescriptionRequest request) {
        return ResponseEntity.ok(orderService.updateDescription(orderId,request));
    }

    @PostMapping("/{orderId}/recreate")
    public ResponseEntity<OrderResponse> recreateOrder(@PathVariable Long orderId) {
        OrderResponse response = orderService.recreateOrder(orderId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
