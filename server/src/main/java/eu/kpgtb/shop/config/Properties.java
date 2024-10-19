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
    private String businessAccountDefaultPassword = "C^#b2vZxc7DFtdVaRY^sj9^@pWtrK*";
    private String emailFrom = "Shop <noreply@example.com>";
    private String rememberMeKey = "d41b01d5cefb53da99a93583583e41be06ee5f7a85495812e61f01ae467bb9d9";
}
