package ru.clevertec.banking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/account")
public class HelloSecured {

    @GetMapping("/hello-secured")
    public String secured() {
        return "Hello secured!";
    }
}
