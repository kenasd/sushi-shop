package org.alex.dto.response;

import lombok.Data;

@Data(staticConstructor = "of")
public class DefaultResponse {
    private final int code;
    private final String msg;
}
