package eu.kpgtb.shop.serivce.impl;

import eu.kpgtb.shop.config.Properties;
import eu.kpgtb.shop.serivce.iface.ICaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

public class CaptchaServiceImpl implements ICaptchaService {
    @Autowired
    private Properties properties;

    @Override
    public boolean verify(String token) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl("https://www.google.com/recaptcha/api/siteverify")
                .queryParam("secret", this.properties.getCaptchaSecret())
                .queryParam("response", token);

        HttpEntity<?> entity = new HttpEntity<>(headers);

        HttpEntity<CaptchaResponse> response = restTemplate.exchange(uriBuilder.toUriString(), HttpMethod.POST, entity, CaptchaResponse.class);
        return response.hasBody() && response.getBody().success;
    }

    record CaptchaResponse(boolean success) {}
}
