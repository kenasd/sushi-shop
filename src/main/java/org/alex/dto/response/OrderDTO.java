package org.alex.dto.response;

import lombok.Data;

@Data
public class OrderDTO {
    private int id;
    private int statusId;
    private int sushiId;
    private long createdAt;
}
