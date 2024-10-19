package eu.kpgtb.shop.config;

import eu.kpgtb.shop.serivce.EmailServiceImpl;
import eu.kpgtb.shop.serivce.IEmailService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {
    @Bean
    public IEmailService emailService() {
        return new EmailServiceImpl();
    }
}
