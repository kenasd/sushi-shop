package org.alex.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alex.dto.request.OrderRequest;
import org.alex.dto.response.DefaultResponse;
import org.alex.dto.response.OrderDTO;
import org.alex.dto.response.OrderResponse;
import org.alex.dto.response.OrderTimeDTO;
import org.alex.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @PostMapping()
    public ResponseEntity<OrderResponse> createOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Create sushi order: " + orderRequest.getSushiName());
        final OrderDTO order = orderService.create(orderRequest.getSushiName());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.of(0, "Order created", order));
    }

    @DeleteMapping(path = "/{order_id}")
    public ResponseEntity<DefaultResponse> deleteOrder(@PathVariable("order_id") int orderId) {
        log.info("Delete {} sushi order", orderId);
        int code = orderService.delete(orderId) ? 0 : 1;
        return ResponseEntity.status(HttpStatus.OK).body(DefaultResponse.of(code, "Order cancelled"));
    }

    @GetMapping(path = "/status")
    public ResponseEntity<Map<String, List<OrderTimeDTO>>> getStatuses() {
        log.info("Get statuses");

        final Map<String, List<OrderTimeDTO>> statuses = orderService.getStatuses();
        return ResponseEntity.status(HttpStatus.OK).body(statuses);
    }

    @PutMapping(path = "/{order_id}/pause")
    public ResponseEntity<DefaultResponse> pauseOrder(@PathVariable("order_id") int orderId) {
        log.info("Pause {} sushi order", orderId);
        int code = orderService.pause(orderId) ? 0 : 1;
        return ResponseEntity.status(HttpStatus.OK).body(DefaultResponse.of(code, "Order paused"));
    }

    @PutMapping(path = "/{order_id}/resume")
    public ResponseEntity<DefaultResponse> resumeOrder(@PathVariable("order_id") int orderId) {
        log.info("Resume {} sushi order", orderId);
        int code = orderService.resume(orderId) ? 0 : 1;
        return ResponseEntity.status(HttpStatus.OK).body(DefaultResponse.of(code, "Order resumed"));
    }
}
