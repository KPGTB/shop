package eu.kpgtb.shop.config;

import org.apache.catalina.Context;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class AuthConfig {
    private static final String FRONTEND = "http://localhost:5173";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests((req) -> req
                .requestMatchers("/login").anonymous()
                .requestMatchers("/logout", "/ping2").authenticated()
                .anyRequest().permitAll()
            ).formLogin((form) -> form
                .loginPage("/login")
                .defaultSuccessUrl(FRONTEND + "/customer")
                .failureUrl(FRONTEND + "/signin?error")
            ).logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl(FRONTEND + "/")
                        .deleteCookies("JSESSIONID")
            ).rememberMe(rem -> rem
                        .key("dneund827be9782nd982ndu2nd2")
                        .tokenValiditySeconds(86400)
                        .useSecureCookie(true)
            ).build();
    }

    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
        return tomcatServletWebServerFactory -> tomcatServletWebServerFactory.addContextCustomizers(context -> {
            Rfc6265CookieProcessor processor=new Rfc6265CookieProcessor();
            processor.setSameSiteCookies("none");
            context.setCookieProcessor(processor);
        });
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("admin@kpgtb.eu")
                .password("admin")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user);
    }
}
