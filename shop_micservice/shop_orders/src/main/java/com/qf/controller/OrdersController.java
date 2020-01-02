package com.qf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/orders")
public class OrdersController {

    @RequestMapping("/list")
    public String ordersList(){

        return "orderslist";
    }
}
