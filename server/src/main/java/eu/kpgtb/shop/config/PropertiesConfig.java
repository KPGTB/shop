package eu.kpgtb.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class PropertiesConfig {
    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() throws IOException {
        File file = new File("config.properties");

        if(!file.exists()) {
            file.createNewFile();

            PrintWriter writer = new PrintWriter(file);

            writer.println("# ");
            writer.println("# Spring Boot Configuration");
            writer.println("# ");
            writer.println("spring.application.name=Shop");
            writer.println("server.error.whitelabel.enabled=false");

            writer.println("# ");
            writer.println("# Database Configuration");
            writer.println("# ");
            writer.println("spring.jpa.hibernate.ddl-auto=update");
            writer.println("spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver");
            writer.println("spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect");
            writer.println("spring.datasource.url=jdbc:mysql://localhost:3306/shop");
            writer.println("spring.datasource.username=root");
            writer.println("spring.datasource.password=");

            writer.println("# ");
            writer.println("# Session Cookies Configuration");
            writer.println("# ");
            writer.println("server.servlet.session.cookie.same-site=none");

            writer.println("# ");
            writer.println("# SSL Configuration");
            writer.println("# ");
            writer.println("server.ssl.enabled=true");
            writer.println("server.ssl.key-alias=skipper");
            writer.println("server.ssl.key-store=C:/skipper.keystore");
            writer.println("server.ssl.key-store-type=jks");
            writer.println("server.ssl.key-store-password=skipper");
            writer.println("server.ssl.key-password=skipper");

            writer.println("# ");
            writer.println("# Shop Configuration");
            writer.println("# ");
            writer.println("eu.kpgtb.frontend-url=http://localhost:5173");
            writer.close();
        }

        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new FileSystemResource(file));
        configurer.setIgnoreResourceNotFound(false);
        return configurer;
    }
}
