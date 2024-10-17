package eu.kpgtb.shop.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Date;

public class JsonResponse<T> extends ResponseEntity<JsonResponse.Response<T>> {

    public JsonResponse(HttpStatus status, String message) {
        this(status,message,new Date(), null);
    }

    public JsonResponse(HttpStatus status, String message, Date date) {
        this(status,message,date,null);
    }

    public JsonResponse(HttpStatus status, String message, T data) {
        this(status,message,new Date(), data);
    }

    public JsonResponse(HttpStatus status, String message, Date date, T data) {
        super(
                new Response<>(status.value(),message,date,data),
                status
        );
    }

    record Response<T>(int status, String message, Date date, T data) {}
}
