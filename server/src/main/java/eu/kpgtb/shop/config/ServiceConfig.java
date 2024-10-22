package eu.kpgtb.shop.config;

import eu.kpgtb.shop.serivce.EmailServiceImpl;
import eu.kpgtb.shop.serivce.IEmailService;
import eu.kpgtb.shop.serivce.IS3Service;
import eu.kpgtb.shop.serivce.S3ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {
    @Autowired
    private Properties properties;

    @Bean
    public IEmailService emailService() {
        return new EmailServiceImpl();
    }

    @Bean
    public IS3Service ss3Service() {
        return new S3ServiceImpl(properties);
    }
}
