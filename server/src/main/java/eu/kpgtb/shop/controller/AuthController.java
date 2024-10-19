package eu.kpgtb.shop.controller;

import eu.kpgtb.shop.auth.User;
import eu.kpgtb.shop.util.JsonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @GetMapping("/info")
    public JsonResponse<User.PrivateUserInfo> info(Authentication authentication) {
        return new JsonResponse<>(HttpStatus.OK, "Success", ((User)authentication.getPrincipal()).getPrivateUserInfo());
    }
}
