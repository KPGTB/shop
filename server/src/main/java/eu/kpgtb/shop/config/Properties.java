package eu.kpgtb.shop.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("eu.kpgtb")
public class Properties {
    private String frontendUrl = "http://localhost:8080";
}
