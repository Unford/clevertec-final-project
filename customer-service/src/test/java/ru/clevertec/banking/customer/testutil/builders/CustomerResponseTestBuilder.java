package ru.clevertec.banking.customer.testutil.builders;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import ru.clevertec.banking.customer.dto.response.CustomerResponse;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseTestBuilder implements TestBuilder<CustomerResponse> {
    private UUID id = UUID.fromString("1a72a05f-4b8f-43c5-a889-1ebc6d9dc729");
    private String customerType = "LEGAL";
    private String unp = "123456789";
    private LocalDate registerDate = LocalDate.now();
    private String email = "p4yZu@example.com";
    private String phoneCode = "+37529";
    private String phoneNumber = "123456789";
    private String customerFullname = "John Doe Smith";

    public CustomerResponse build() {
        return new CustomerResponse(id, customerType, unp, registerDate, email, phoneCode, phoneNumber, customerFullname);
    }
}
