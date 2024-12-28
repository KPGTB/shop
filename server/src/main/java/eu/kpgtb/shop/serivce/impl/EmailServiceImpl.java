package eu.kpgtb.shop.serivce.impl;

import eu.kpgtb.shop.config.Properties;
import eu.kpgtb.shop.serivce.EmailData;
import eu.kpgtb.shop.serivce.iface.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import java.io.File;
import java.io.InputStream;

public class EmailServiceImpl implements IEmailService {
    @Autowired
    private Properties properties;
    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    @Async
    @SneakyThrows
    public void sendEmail(EmailData data) {
        MimeMessage mime = this.javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mime,true);

        helper.setFrom(this.properties.getEmailFrom());
        if(data.isUseBcc()) {
            helper.setBcc(data.getTo().toArray(new String[0]));
        } else {
            helper.setTo(data.getTo().toArray(new String[0]));
        }
        helper.setSubject(data.getSubject());
        helper.setText(data.getContent(),true);

        for (File file : data.getAttachments()) {
            helper.addAttachment(file.getName(),file);
        }
        data.getIsAttachments().forEach((key,is) -> {
            try {
                helper.addAttachment(key,new InputStreamResource(is));
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        });

        this.javaMailSender.send(mime);
    }
}
