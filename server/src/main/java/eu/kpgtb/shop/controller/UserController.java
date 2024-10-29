package eu.kpgtb.shop.controller;

import eu.kpgtb.shop.auth.User;
import eu.kpgtb.shop.data.dto.UserDto;
import eu.kpgtb.shop.data.dto.order.OrderDto;
import eu.kpgtb.shop.data.repository.order.OrderRepository;
import eu.kpgtb.shop.util.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired private OrderRepository orderRepository;

    @GetMapping
    public JsonResponse<UserDto> info(@AuthenticationPrincipal User principal) {
        return new JsonResponse<>(HttpStatus.OK, "Success", new UserDto(principal.getEntity()));
    }

    @GetMapping("/orders")
    public JsonResponse<List<OrderDto>> orders(@AuthenticationPrincipal User principal) {
        return new JsonResponse<>(
                HttpStatus.OK,
                "List of orders",
                orderRepository.findAllByUser(principal.getEntity()).stream().map(order -> new OrderDto(
                        order,Arrays.asList(
                        "products",
                        "products.product",
                        "products.fields",
                        "products.fields.field",
                        "user"
                ),""
                )).toList()
        );
    }
}
