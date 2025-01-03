package eu.kpgtb.shop.config;

import eu.kpgtb.shop.serivce.iface.ICaptchaService;
import eu.kpgtb.shop.serivce.iface.IRconService;
import eu.kpgtb.shop.serivce.impl.CaptchaServiceImpl;
import eu.kpgtb.shop.serivce.impl.EmailServiceImpl;
import eu.kpgtb.shop.serivce.iface.IEmailService;
import eu.kpgtb.shop.serivce.iface.IS3Service;
import eu.kpgtb.shop.serivce.impl.RconServiceImpl;
import eu.kpgtb.shop.serivce.impl.S3ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ServiceConfig {
    @Autowired
    private Properties properties;

    @Bean public IEmailService emailService() {
        return new EmailServiceImpl();
    }
    @Bean public IS3Service ss3Service() {
        return new S3ServiceImpl(properties);
    }
    @Bean public ICaptchaService captchaService() {
        return new CaptchaServiceImpl();
    }
    @Bean public IRconService rconService() {return new RconServiceImpl();}
    @Bean public RestTemplate restTemplate() {return new RestTemplate();}
}
