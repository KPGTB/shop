package eu.kpgtb.shop;

import eu.kpgtb.shop.serivce.IEmailService;
import eu.kpgtb.shop.util.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@EnableAsync
public class ShopApplication {
	@Autowired
	private IEmailService emailService;

	public static void main(String[] args) {
		SpringApplication.run(ShopApplication.class, args);
	}

	@GetMapping("/ping")
	public JsonResponse<String> ping() {
		return new JsonResponse<>(HttpStatus.OK, "Pong");
	}
}
