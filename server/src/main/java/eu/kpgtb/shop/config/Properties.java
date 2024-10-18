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
    private String businessAccountEmail = "Thn8smFY2F24kvbxYPF6ds@1pNCK3tfSf6wGwr7XVwDSh7ZHY2";
}
