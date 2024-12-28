package eu.kpgtb.shop.controller;

import eu.kpgtb.shop.config.Properties;
import eu.kpgtb.shop.data.entity.EmailTemplateEntity;
import eu.kpgtb.shop.data.entity.NewsletterEntity;
import eu.kpgtb.shop.data.repository.EmailTemplateRepository;
import eu.kpgtb.shop.data.repository.NewsletterRepository;
import eu.kpgtb.shop.serivce.EmailData;
import eu.kpgtb.shop.serivce.iface.ICaptchaService;
import eu.kpgtb.shop.serivce.iface.IEmailService;
import eu.kpgtb.shop.util.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/newsletter")
public class NewsletterController {
    @Autowired private NewsletterRepository newsletterRepository;
    @Autowired private EmailTemplateRepository emailTemplateRepository;
    @Autowired private IEmailService emailService;
    @Autowired private ICaptchaService captchaService;
    @Autowired private Properties properties;

    @PutMapping
    public JsonResponse<Boolean> send(@RequestBody NewsletterSendBody body) {
        newsletterRepository.findAllByActive(true).forEach(entity -> {
            sendNewsletter(entity.getEmail(), entity.getToken(),body.subject,body.content);
        });
        return new JsonResponse<>(HttpStatus.OK, "Verification email sent", true);
    }

    @Async
    private void sendNewsletter(String email, String token, String subject, String content) {
        emailService.sendEmail(EmailData.builder()
                .template(EmailTemplateEntity.CommonTemplateType.NEWSLETTER.name())
                .to(email)
                .placeholder("unsubscribe", properties.getFrontendUrl() + "/newsletter/unsubscribe?token="+token)
                .placeholder("subject", subject)
                .placeholder("content", content)
                .emailTemplateRepository(this.emailTemplateRepository)
                .build());
    }

    @PostMapping
    public JsonResponse<Boolean> subscribe(@RequestBody NewsletterSubscribeBody body) {
        Optional<NewsletterEntity> entityCheck = newsletterRepository.findByEmail(body.email);
        if(entityCheck.isPresent() && entityCheck.get().isActive()) return new JsonResponse<>(HttpStatus.BAD_REQUEST, "E-mail already subscribed", false);
        entityCheck.ifPresent(newsletterRepository::delete);

        String ver = System.currentTimeMillis() + body.email + new Random().nextInt();
        String token = Base64.getEncoder().encodeToString(ver.getBytes()).replace("=", "");
        NewsletterEntity entity = new NewsletterEntity(body.email, token,false);
        newsletterRepository.save(entity);
        sendVerificationEmail(body.email,token);
        return new JsonResponse<>(HttpStatus.OK, "Verification email sent", true);
    }

    @Async
    private void sendVerificationEmail(String email, String token) {
        emailService.sendEmail(EmailData.builder()
                .template(EmailTemplateEntity.CommonTemplateType.NEWSLETTER_SUBSCRIBE.name())
                .to(email)
                .placeholder("subscribe", properties.getFrontendUrl() + "/newsletter/verify?token="+token)
                .emailTemplateRepository(this.emailTemplateRepository)
                .build());
    }

    @PostMapping("/verify")
    public JsonResponse<Boolean> verify(@RequestBody NewsletterActionBody body) {
        if(!captchaService.verify(body.captcha)) {
            return new JsonResponse<>(HttpStatus.BAD_REQUEST, "Captcha failed");
        }
        NewsletterEntity entity = newsletterRepository.findByToken(body.token).orElse(null);
        if(entity == null) return new JsonResponse<>(HttpStatus.NOT_FOUND, "Token invalid", false);
        entity.setActive(true);
        newsletterRepository.save(entity);
        return new JsonResponse<>(HttpStatus.OK, "Newsletter subscribed", true);
    }

    @DeleteMapping
    public JsonResponse<Boolean> unsubscribe(@RequestBody NewsletterActionBody body) {
        if(!captchaService.verify(body.captcha)) {
            return new JsonResponse<>(HttpStatus.BAD_REQUEST, "Captcha failed");
        }
        Optional<NewsletterEntity> entity = newsletterRepository.findByToken(body.token);
        if(entity.isEmpty()) return new JsonResponse<>(HttpStatus.NOT_FOUND, "Token invalid", false);
        newsletterRepository.delete(entity.get());
        return new JsonResponse<>(HttpStatus.OK, "Newsletter unsubscribed", true);
    }


    public record NewsletterSendBody(String subject, String content) {}
    public record NewsletterSubscribeBody(String email) {}
    public record NewsletterActionBody(String token, String captcha) {}
}
