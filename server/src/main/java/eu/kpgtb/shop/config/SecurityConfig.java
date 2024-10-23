package eu.kpgtb.shop.config;

import eu.kpgtb.shop.data.entity.UserEntity;
import org.apache.tomcat.util.http.Rfc6265CookieProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
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
                // Authentication
                .requestMatchers(HttpMethod.POST,"/auth/signin", "/auth/signup").anonymous()
                .requestMatchers("/auth/*").authenticated()
                // Category Controller
                .requestMatchers(HttpMethod.POST,"/category").hasAuthority(UserEntity.UserRole.BUSINESS.getAuthority())
                .requestMatchers(HttpMethod.PUT,"/category").hasAuthority(UserEntity.UserRole.BUSINESS.getAuthority())
                .requestMatchers(HttpMethod.DELETE,"/category").hasAuthority(UserEntity.UserRole.BUSINESS.getAuthority())
                // Product Controller
                .requestMatchers(HttpMethod.POST, "/product/updateAll").hasAuthority(UserEntity.UserRole.BUSINESS.getAuthority())
                .requestMatchers(HttpMethod.POST, "/product").hasAuthority(UserEntity.UserRole.BUSINESS.getAuthority())
                .requestMatchers(HttpMethod.PUT, "/product").hasAuthority(UserEntity.UserRole.BUSINESS.getAuthority())
                .requestMatchers(HttpMethod.DELETE, "/product").hasAuthority(UserEntity.UserRole.BUSINESS.getAuthority())
                // Image
                .requestMatchers(HttpMethod.PUT, "/image/upload").hasAuthority(UserEntity.UserRole.BUSINESS.getAuthority())
                .anyRequest().permitAll()
            ).formLogin((form) -> form
                .loginPage("/auth/signin")
                .usernameParameter("email")
                .defaultSuccessUrl(this.properties.getFrontendUrl())
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
            ).exceptionHandling(handler -> handler
                        .authenticationEntryPoint(new Http403ForbiddenEntryPoint()) // Throw 403 instead of redirecting to login page
            ).requestCache(RequestCacheConfigurer::disable) // Don't redirect to previous unauthenticated request after authentication
            .build();
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowCredentials(true)
                .allowedMethods("POST", "GET", "PUT", "DELETE", "OPTIONS")
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
