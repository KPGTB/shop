package eu.kpgtb.shop.data;

import eu.kpgtb.shop.data.entity.EmailTemplate;
import eu.kpgtb.shop.data.entity.UserEntity;
import eu.kpgtb.shop.data.repository.EmailTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class TemplateInit implements CommandLineRunner {
    @Autowired private EmailTemplateRepository emailTemplateRepository;

    @Override
    public void run(String... args) throws Exception {
       if(!emailTemplateRepository.existsByType(EmailTemplate.CommonTemplateType.ACCOUNT_ACTIVATION.name())) {
           emailTemplateRepository.save(new EmailTemplate(
                   EmailTemplate.CommonTemplateType.ACCOUNT_ACTIVATION.name(),
                   "Account Activation",
                   "<h1>Thanks for creating account in our store!</h1><p>Please click link below to activate it<br><a href='{{url}}'>{{url}}</a></p>"
           ));
       }

        if(!emailTemplateRepository.existsByType(EmailTemplate.CommonTemplateType.FORGOT_PASSWORD.name())) {
            emailTemplateRepository.save(new EmailTemplate(
                    EmailTemplate.CommonTemplateType.FORGOT_PASSWORD.name(),
                    "Changing password",
                    "<h1>Forgot password?</h1><p>Please click link below to change it<br><a href='{{url}}'>{{url}}</a></p>"
            ));
        }
    }
}
