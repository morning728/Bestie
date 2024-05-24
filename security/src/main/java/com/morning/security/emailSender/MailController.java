package com.morning.security.emailSender;

import com.morning.security.user.User;
import com.morning.security.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@RequiredArgsConstructor

public class MailController {
    private EmailService emailService;
    private OrderService orderService;
    private UserService userService;


    public void create(){
        User user = User.builder()
                .email("matveygrom2017@gmail.com")
                .username("Gromykhalin")
                .build();
        userService.create(user);

        orderService.createOrder(new OrderService.Order(728F, user));
    }
}
