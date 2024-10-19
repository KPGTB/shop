package eu.kpgtb.shop.config;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@EnableWebMvc
public class SecurityConfig implements WebMvcConfigurer {
    @Autowired
    private Properties properties;
    @Value("${server.servlet.session.cookie.same-site}")
    private String sameSitePolicy = "strict";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests((req) -> req
                .requestMatchers("/auth/signin").anonymous()
                .requestMatchers("/auth/signout").authenticated()
                .anyRequest().permitAll()
            ).formLogin((form) -> form
                .loginPage("/auth/signin")
                .usernameParameter("email")
                .defaultSuccessUrl(this.properties.getFrontendUrl() + "/customer")
                .failureUrl(this.properties.getFrontendUrl() + "/signin?error")
            ).logout(logout -> logout
                        .logoutUrl("/auth/signout")
                        .logoutSuccessUrl(this.properties.getFrontendUrl())
                        .deleteCookies("JSESSIONID")
                        .deleteCookies("remember-me")
            ).rememberMe(rememberMe -> rememberMe
                        .key(this.properties.getRememberMeKey())
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .useSecureCookie(true)
            ).build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins(this.properties.getFrontendUrl());
    }

    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
        return tomcatServletWebServerFactory -> tomcatServletWebServerFactory.addContextCustomizers(context -> {
            Rfc6265CookieProcessor processor=new Rfc6265CookieProcessor();
            processor.setSameSiteCookies(this.sameSitePolicy);
            context.setCookieProcessor(processor);
        });
    }

}
