package com.example.webbanhang.controller;

import com.example.webbanhang.model.Order;
import com.example.webbanhang.model.OrderDetail;
import com.example.webbanhang.service.CartService;
import com.example.webbanhang.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkout() {
        return "/cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam String customerName,
                              @RequestParam String address,
                              @RequestParam String phoneNumber,
                              @RequestParam String notes) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setAddress(address);
        order.setPhoneNumber(phoneNumber);
        order.setNotes(notes);
        order.setOrderDetails(cartService.getCartItems().stream()
                .map(cartItem -> new OrderDetail(cartItem.getProduct(), cartItem.getQuantity()))
                .collect(Collectors.toList()));
        orderService.saveOrder(order);
        cartService.clearCart();
        return "redirect:/order/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }
}
