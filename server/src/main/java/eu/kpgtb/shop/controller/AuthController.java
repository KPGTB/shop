package eu.kpgtb.shop.controller;

import eu.kpgtb.shop.config.Properties;
import eu.kpgtb.shop.data.entity.EmailTemplateEntity;
import eu.kpgtb.shop.data.entity.UserEntity;
import eu.kpgtb.shop.data.repository.EmailTemplateRepository;
import eu.kpgtb.shop.data.repository.UserRepository;
import eu.kpgtb.shop.serivce.EmailData;
import eu.kpgtb.shop.serivce.iface.ICaptchaService;
import eu.kpgtb.shop.serivce.iface.IEmailService;
import eu.kpgtb.shop.util.JsonResponse;
import lombok.SneakyThrows;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private ICaptchaService captchaService;
    @Autowired private Properties properties;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private IEmailService emailService;
    @Autowired private EmailTemplateRepository emailTemplateRepository;

    @SneakyThrows
    @PostMapping("/signup")
    public JsonResponse<Object> signup(SignupBody body) {
        if(!captchaService.verify(body.captcha)) {
            return new JsonResponse<>(properties.getFrontendUrl() + "/signup?captcha");
        }

        if(!body.password.equalsIgnoreCase(body.password2) || !EmailValidator.getInstance().isValid(body.email)) {
            return new JsonResponse<>(properties.getFrontendUrl() + "/signup?error");
        }

        if(userRepository.existsByEmail(body.email)) {
             return new JsonResponse<>(properties.getFrontendUrl() + "/signup?exists");
        }

        String ver = System.currentTimeMillis() + body.email + new Random().nextInt();
        String verificationToken = Base64.getEncoder().encodeToString(ver.getBytes()).replace("=", "");

        UserEntity entity = new UserEntity(
                body.email,
                passwordEncoder.encode(body.password),
                verificationToken,
                false,
                UserEntity.UserRole.CUSTOMER
        );
        this.userRepository.save(entity);
        sendActivationEmail(body.email, verificationToken);

        return new JsonResponse<>(properties.getFrontendUrl() + "/signup?created");
    }

    @Async
    private void sendActivationEmail(String email, String token) {
        emailService.sendEmail(EmailData.builder()
                .template(EmailTemplateEntity.CommonTemplateType.ACCOUNT_ACTIVATION.name())
                .to(email)
                .placeholder("url", properties.getFrontendUrl() + "/activate?token="+token)
                .emailTemplateRepository(this.emailTemplateRepository)
                .build());
    }

    @PostMapping("/activate")
    @SneakyThrows
    public Object verify(VerifyBody body) {
        if(!captchaService.verify(body.captcha)) {
            return new JsonResponse<>(properties.getFrontendUrl() + "/activate?captcha");
        }

        if(body.token.isEmpty()) {
            return new JsonResponse<>(properties.getFrontendUrl() + "/activate?error");
        }

        UserEntity entity = this.userRepository.findByVerificationToken(body.token);
        if(entity == null) {
            return new JsonResponse<>(properties.getFrontendUrl() + "/activate?error");
        }

        entity.setActive(true);
        entity.setVerificationToken(null);
        this.userRepository.save(entity);

        return new JsonResponse<>(properties.getFrontendUrl() + "/signin?activated");
    }


    record SignupBody(String email, String password, String password2, String captcha) {}
    record VerifyBody(String token, String captcha) {}
}