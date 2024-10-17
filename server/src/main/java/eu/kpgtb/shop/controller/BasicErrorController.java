package eu.kpgtb.shop.controller;

import eu.kpgtb.shop.util.JsonResponse;
import eu.kpgtb.shop.util.StringUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicErrorController implements ErrorController {

    @RequestMapping("/error")
    public JsonResponse<Void> handleError(HttpServletRequest request) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        if(status == null) {
            status = 500;
        }

        int statusCode = Integer.valueOf(status.toString());
        String type = StringUtil.convertSneakToTitleCase(
                HttpStatus.valueOf(statusCode).name()
        );

        return new JsonResponse<>(HttpStatus.valueOf(statusCode), type);
    }
}
