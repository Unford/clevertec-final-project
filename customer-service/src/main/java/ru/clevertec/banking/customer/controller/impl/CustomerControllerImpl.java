package ru.clevertec.banking.customer.controller.impl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.clevertec.banking.customer.controller.CustomerController;
import ru.clevertec.banking.customer.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.customer.dto.request.GetCustomersPageableRequest;
import ru.clevertec.banking.customer.dto.response.CustomerBankingProductsResponse;
import ru.clevertec.banking.customer.dto.response.CustomerResponse;
import ru.clevertec.banking.customer.service.CustomerBankingProductsService;
import ru.clevertec.banking.customer.service.CustomerService;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerControllerImpl implements CustomerController {

    private final CustomerService customerService;
    private final CustomerBankingProductsService customerBankingProductsService;

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public Page<CustomerResponse> getCustomersPageable(
            @ModelAttribute @Valid GetCustomersPageableRequest getCustomersPageableRequest) {
        return customerService.getCustomersPageable(getCustomersPageableRequest);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public CustomerResponse getCustomerById(@PathVariable("id") UUID id) {
        return customerService.getCustomersById(id);
    }

    @GetMapping("/by-unp/{unp}")
    @PreAuthorize("isAuthenticated()")
    public CustomerResponse getCustomerByUnp(@PathVariable("unp") String unp) {
        return customerService.getCustomersByUnp(unp);
    }

    @GetMapping("/{id}/banking-products")
    @PreAuthorize("hasRole('ROLE_ADMIN') OR " +
                  "@customSecurityExpression.hasUserRoleAndIdEquals(#id)")
    public CustomerBankingProductsResponse getCustomerBankingProducts(@PathVariable("id") UUID id) {
        return customerBankingProductsService.getCustomerBankingProducts(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public CustomerResponse createCustomer(@RequestBody @Valid CreateCustomerRequest createCustomerRequest) {
        return customerService.saveCustomer(createCustomerRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ROLE_SUPER_USER')")
    public void deleteCustomer(@PathVariable("id") UUID id) {
        customerService.deleteCustomer(id);
    }
}