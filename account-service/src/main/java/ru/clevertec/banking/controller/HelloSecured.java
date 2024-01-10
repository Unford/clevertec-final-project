package ru.clevertec.banking.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/accounts")
public class HelloSecured {

    @GetMapping("/hello-secured")
    public String secured(@RequestHeader HttpHeaders headers) {
        System.out.println(headers.toString());
        return "Hello secured!";
    }
}
