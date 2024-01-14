package ru.clevertec.banking.customer.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.banking.customer.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.customer.dto.request.GetCustomersPageableRequest;
import ru.clevertec.banking.customer.dto.response.CustomerResponse;
import ru.clevertec.banking.customer.service.CustomerService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public Page<CustomerResponse> getCustomersPageable(
            @ModelAttribute @Valid GetCustomersPageableRequest getCustomersPageableRequest) {
        return customerService.getCustomersPageable(getCustomersPageableRequest);
    }

    @GetMapping("/{id}")
    public CustomerResponse getCustomerById(@PathVariable("id") UUID id) {
        return customerService.getCustomersById(id);
    }

    @GetMapping("/by-email/{email}") // TODO: Good ли сувать конфиденциальную инфу для реквеста в path?
    public CustomerResponse getCustomerByEmail(@PathVariable("email") @Valid @Email String email) {
        return customerService.getCustomersByEmail(email);
    }

    @GetMapping("/by-unp/{unp}")
    public CustomerResponse getCustomerByUnp(@PathVariable("unp") String unp) {
        return customerService.getCustomersByUnp(unp);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse createCustomer(@RequestBody @Valid CreateCustomerRequest createCustomerRequest) {
        return customerService.createCustomer(createCustomerRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable("id") UUID id) {
        customerService.deleteCustomer(id);
    }
}
