package ru.clevertec.banking.customer.dto.message;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthMessage {

    private MessageHeader header;
    private AuthMessagePayload payload;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @Accessors(chain = true)
    public static class MessageHeader {
        private String messageType;
    }
}