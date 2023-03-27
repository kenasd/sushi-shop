package org.alex.dto.response;

import lombok.Data;

@Data(staticConstructor = "of")
public class OrderTimeDTO {
    private final int orderId;
    private final int timeSpent;
}
