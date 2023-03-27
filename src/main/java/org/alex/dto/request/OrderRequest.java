package org.alex.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class OrderRequest {
    @JsonProperty("sushi_name")
    private String sushiName;
}
