package org.alex.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.alex.dto.response.OrderDTO;
import org.alex.dto.response.OrderTimeDTO;
import org.alex.exception.OrderNotFoundException;
import org.alex.exception.StatusNotFoundException;
import org.alex.model.Status;
import org.alex.model.SushiOrder;
import org.alex.repository.OrderRepository;
import org.alex.repository.StatusRepository;
import org.alex.service.cooking.CookTime;
import org.alex.util.OrderMapper;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;

@Slf4j
@EnableScheduling
@Service
@RequiredArgsConstructor
public class KitchenService {
    private static final int CHEFS_COUNT = 3;
    private static final String WORK_STATUS = "in-progress";

    private final Queue<CookTime> ordersQueue = new PriorityBlockingQueue<>(100, Comparator.comparing(CookTime::getOrderId));
    private final Queue<CookTime> chefsQueue = new LinkedBlockingQueue<>(CHEFS_COUNT);
    private final Map<Integer, CookTime> orders = new HashMap<>();
    private final Map<Integer, CookTime> pausedOrders = new HashMap<>();
    private final OrderRepository orderRepository;
    private final StatusRepository statusRepository;
    private final OrderMapper orderMapper;

    public OrderDTO addOrder(SushiOrder sushiOrder) {
        final String statusName = "created";
        sushiOrder.setStatus(getStatus(statusName));

        final SushiOrder saved = orderRepository.save(sushiOrder);
        final CookTime cookTime = new CookTime(saved);
        orders.put(cookTime.getOrderId(), cookTime);
        updateCookingState(cookTime.getOrderId(), statusName);
        return orderMapper.sushiOrderToOrderDTO(saved);
    }

    public boolean changeOrderStatus(int orderId, String statusName) {
        CookTime cookTime = orders.get(orderId);
        if (cookTime == null) {
            throw new OrderNotFoundException("Order Not Found: " + orderId);
        }

        if (updateCookingState(orderId, statusName)) {
            final SushiOrder sushiOrder = cookTime.getSushiOrder();
            sushiOrder.setStatus(getStatus(statusName));
            orderRepository.save(sushiOrder);
            return true;
        }
        return false;
    }

    public Map<String, List<OrderTimeDTO>> getStatuses() {
        final Map<String, List<SushiOrder>> sushiOrders = orderRepository.findAll().stream()
                .collect(groupingBy((order) -> order.getStatus().getName()));
        return sushiOrders.entrySet().stream()
                .map(entry -> new AbstractMap.SimpleEntry<>(entry.getKey(), convertToOrderTime(entry.getValue())))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Scheduled(initialDelay = 1000, fixedRate = 500)
    public void cooking() {
        if (chefsQueue.size() < CHEFS_COUNT) {

            // chefs are taking new orders if they have space
            while (chefsQueue.size() < CHEFS_COUNT && !ordersQueue.isEmpty()) {
                CookTime cookTime = ordersQueue.poll();
                if (cookTime.getStatusName().equals("created") || cookTime.getStatusName().equals(WORK_STATUS)) {
                    log.info("Chefs started {} order", cookTime.getOrderId());
                    changeOrderStatus(cookTime.getOrderId(), WORK_STATUS);
                    cookTime.setLastTimeUpdate(Instant.now().getEpochSecond());
                    chefsQueue.add(cookTime);
                }
            }
        }

        for (int task = chefsQueue.size() - 1; task >= 0; task--) {
            CookTime cookTime = chefsQueue.poll();
            // chefs are checking the order status and update progress time
            if (cookTime.getStatusName().equals(WORK_STATUS)) {
                long timeUpdate = Instant.now().getEpochSecond();
                long delta = timeUpdate - cookTime.getLastTimeUpdate();
                cookTime.setLastTimeUpdate(timeUpdate);
                cookTime.addTimeSpent((int) delta);
                // if order ready it will be removed from chefs and stored with new status
                if (cookTime.isReady()) {
                    log.info("Chefs finished {} order", cookTime.getOrderId());
                    changeOrderStatus(cookTime.getOrderId(), "finished");
                } else {
                    chefsQueue.add(cookTime);
                }
            }
        }
    }

    /**
     * State machine to change behavior
     */
    private boolean updateCookingState(int orderId, String statusName) {
        CookTime cookTime = orders.get(orderId);
        if (cookTime != null) {
            switch (statusName) {
                case "created":
                    ordersQueue.add(cookTime);
                    break;
                case "paused":
                    pausedOrders.put(orderId, cookTime);
                    return cookTime.getStatusName().equals("in-progress");
                case "in-progress":
                    CookTime paused = pausedOrders.remove(orderId);
                    if (paused != null) {
                        ordersQueue.add(paused);
                    }
                    return cookTime.getStatusName().equals("created") || cookTime.getStatusName().equals("paused");
                case "cancelled":
                    pausedOrders.remove(orderId);
                    return !cookTime.getStatusName().equals("finished");
                case "finished":
                    pausedOrders.remove(orderId);
                    return cookTime.getStatusName().equals("in-progress");
                default:
                    pausedOrders.remove(orderId);
            }
        }
        return false;
    }

    private Status getStatus(String statusName) {
        return statusRepository.findByName(statusName)
                .orElseThrow(() -> new StatusNotFoundException("Status Not Found: " + statusName));
    }

    private List<OrderTimeDTO> convertToOrderTime(List<SushiOrder> orders) {
        return orders.stream()
                .map(order -> OrderTimeDTO.of(order.getId(), getTimeSpent(order)))
                .collect(Collectors.toList());
    }

    /**
     * Task states stored in cache, consider flushing into DB
     */
    private int getTimeSpent(SushiOrder order) {
        final CookTime cookTime = orders.get(order.getId());
        return cookTime.getTimeSpent();
    }
}
