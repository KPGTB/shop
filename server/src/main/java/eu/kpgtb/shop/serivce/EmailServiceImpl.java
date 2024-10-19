package eu.kpgtb.shop.serivce;

import eu.kpgtb.shop.config.Properties;
import jakarta.mail.internet.MimeMessage;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import java.io.File;

public class EmailServiceImpl implements IEmailService{
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
        helper.setTo(data.getTo().toArray(new String[0]));
        helper.setSubject(data.getSubject());
        helper.setText(data.getContent(),true);

        for (File file : data.getAttachments()) {
            helper.addAttachment(file.getName(),file);
        }

        this.javaMailSender.send(mime);
    }
}
