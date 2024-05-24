package com.morning.security.emailSender;

import com.morning.security.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
public class OrderService {

    private OrderRepository repository;
    private EmailService emailService;

    public void createOrder(Order order){  //GROMYKHALIN
        repository.save(order);
        emailService.sendNotification("Notification", String.format("Total: %s", order.getTotal()));
    }















    private interface OrderRepository{
        public void  save(Order order);
    }
    @Data
    @AllArgsConstructor
    static
    class Order{
        private Float total;
        private User user;
    }
}
