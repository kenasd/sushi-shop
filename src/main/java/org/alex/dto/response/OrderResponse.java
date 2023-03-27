package org.alex.dto.response;

import lombok.Data;

@Data(staticConstructor = "of")
public class OrderResponse {
    private final int code;
    private final String msg;
    private final OrderDTO orderDTO;
}
