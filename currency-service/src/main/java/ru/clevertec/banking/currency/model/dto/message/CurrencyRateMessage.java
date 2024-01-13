package ru.clevertec.banking.currency.model.dto.message;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CurrencyRateMessage {
    private MessageHeader header;
    private CurrencyRateMessagePayload payload;

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class MessageHeader {
        private String messageType;
    }
}
