package eu.kpgtb.shop.serivce;

import com.stripe.Stripe;
import eu.kpgtb.shop.config.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class StripeInit implements CommandLineRunner {
    @Autowired
    private Properties properties;

    @Override
    public void run(String... args) {
        Stripe.apiKey = properties.getStripePrivateKey();

        new File("./assets/invoices").mkdirs();
        new File("./assets/receipts").mkdirs();
    }
}
