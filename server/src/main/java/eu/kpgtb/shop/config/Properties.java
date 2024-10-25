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
    private String emailFrom = "Shop <noreply@example.com>";

    private String businessAccountEmail = "Thn8smFYdaeaF6ds@1pNCK3tfSf6wGwr7XVwDSh7ZHY2.com";
    private String businessAccountDefaultPassword = "C^#b2vZxcdea312tdVaRY^sj9^@pWtrK*";
    private String rememberMeKey = "d41b01d5cefb53da99adnae32idbea8y8e5f7a85495812e61f01ae467bb9d9";

    private String stripePrivateKey = "";
    private String stripeWebhookKey = "";
    private String stripeCurrency = "USD";
    private boolean stripeCollectPhoneNumber = false;
    private boolean stripeCollectBillingAddress = false;

    private String s3Endpoint = "";
    private String s3AccessKey = "";
    private String s3SecretKey = "";
    private String s3Region = "";
    private String s3PrivateBucket = "";
    private String s3PublicBucket = "";
    private String s3PublicUrl = "";

    private String captchaSecret = "";
}
