package ru.clevertec.banking.deposit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.clevertec.banking.deposit.service.DepositService;

@RestController
@RequestMapping("api/v1/deposits")
@RequiredArgsConstructor
public class DepositController {
    private final DepositService depositService;


}
