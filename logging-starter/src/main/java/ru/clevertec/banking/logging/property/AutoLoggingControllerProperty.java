package ru.clevertec.banking.logging.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "clevertec.logging.controller")
public class AutoLoggingControllerProperty {
    private boolean enabled = true;
}
