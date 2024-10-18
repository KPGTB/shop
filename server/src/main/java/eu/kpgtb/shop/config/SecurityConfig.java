package eu.kpgtb.shop.config;

import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
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

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider::authenticate;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {return PasswordEncoderFactories.createDelegatingPasswordEncoder();}

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
                .defaultSuccessUrl(properties.getFrontendUrl() + "/customer")
                .failureUrl(properties.getFrontendUrl() + "/signin?error")
            ).logout(logout -> logout
                        .logoutUrl("/auth/signout")
                        .logoutSuccessUrl(properties.getFrontendUrl())
                        .deleteCookies("JSESSIONID")
                        .deleteCookies("remember-me")
            ).rememberMe(rememberMe -> rememberMe
                        .key("d41b01d5cefb53da99a93583583e41be06ee5f7a85495812e61f01ae467bb9d9")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .useSecureCookie(true)
            ).build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("admin@kpgtb.eu")
                .password("admin")
                .roles("USER")
                .build();
        UserDetails admin = User.withDefaultPasswordEncoder()
                .username(properties.getBusinessAccountEmail())
                .password("business")
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user,admin);
    }

    @Bean
    WebServerFactoryCustomizer<TomcatServletWebServerFactory> cookieProcessorCustomizer() {
        return tomcatServletWebServerFactory -> tomcatServletWebServerFactory.addContextCustomizers(context -> {
            Rfc6265CookieProcessor processor=new Rfc6265CookieProcessor();
            processor.setSameSiteCookies("none");
            context.setCookieProcessor(processor);
        });
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedOrigins(properties.getFrontendUrl());
    }
}
