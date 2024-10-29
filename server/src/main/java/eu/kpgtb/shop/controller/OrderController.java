package eu.kpgtb.shop.controller;

import eu.kpgtb.shop.data.dto.order.OrderDto;
import eu.kpgtb.shop.data.repository.order.OrderRepository;
import eu.kpgtb.shop.util.JsonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired private OrderRepository orderRepository;

    @GetMapping("/all")
    public JsonResponse<List<OrderDto>> orders() {
        List<OrderDto> orders = new ArrayList<>();
        this.orderRepository.findAll().forEach(order -> orders.add(new OrderDto(
                order, Arrays.asList(
                "products",
                "products.product",
                "products.fields",
                "products.fields.field",
                "user"
        ), ""
        )));
        return new JsonResponse<>(HttpStatus.OK, "List of orders", orders);
    }
}
