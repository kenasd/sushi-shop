package org.alex.util;

import org.alex.dto.response.OrderDTO;
import org.alex.model.SushiOrder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface OrderMapper {
    @Mapping(target = "statusId", source = "status.id")
    @Mapping(target = "sushiId", source = "sushi.id")
    OrderDTO sushiOrderToOrderDTO(SushiOrder sushiOrder);

    default Long map(LocalDateTime localDateTime) {
        return localDateTime == null ? -1 : Timestamp.valueOf(localDateTime).getTime();
    }
}
