package com.clevertec.starter.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "clevertec.logging")
public class AutoLoggingProperty {
    private boolean enabled = true;

}
