package org.alex.service;

import lombok.RequiredArgsConstructor;
import org.alex.dto.response.OrderDTO;
import org.alex.dto.response.OrderTimeDTO;
import org.alex.exception.SushiNotFoundException;
import org.alex.model.Sushi;
import org.alex.model.SushiOrder;
import org.alex.repository.SushiRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final KitchenService kitchenService;
    private final SushiRepository sushiRepository;

    public OrderDTO create(String sushiName) {
        Sushi sushi = sushiRepository.findByName(sushiName)
                .orElseThrow(() -> new SushiNotFoundException("Sushi Not Found: " + sushiName));

        SushiOrder sushiOrder = new SushiOrder();
        sushiOrder.setSushi(sushi);
        sushiOrder.setCreatedAt(LocalDateTime.now());

        return kitchenService.addOrder(sushiOrder);
    }

    public Map<String, List<OrderTimeDTO>> getStatuses() {
       return kitchenService.getStatuses();
    }

    public boolean delete(int orderId) {
        return kitchenService.changeOrderStatus(orderId, "cancelled");
    }

    public boolean pause(int orderId) {
        return kitchenService.changeOrderStatus(orderId, "paused");
    }

    public boolean resume(int orderId) {
        return kitchenService.changeOrderStatus(orderId, "in-progress");
    }
}
