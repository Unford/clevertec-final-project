package ru.clevertec.banking.customer.validator;

import jakarta.validation.ConstraintValidatorContext;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.clevertec.banking.customer.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.customer.service.CustomerService;
import ru.clevertec.banking.customer.testutil.builders.CreateCustomerRequestTestBuilder;

import static org.mockito.Mockito.when;

@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
class CreateCustomerRequestValidatorTest {

    @Mock
    ConstraintValidatorContext validatorContext;
    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;
    @InjectMocks
    CreateCustomerRequestValidator createCustomerRequestValidator;
    @Mock
    CustomerService customerService;

    @Test
    @DisplayName("should return true for valid create customer request")
    void shouldReturnTrueForValidCreateCustomerRequest() {
        CreateCustomerRequest request = new CreateCustomerRequestTestBuilder().build();
        when(customerService.isCustomerExist(request.getId(), request.getEmail(), request.getUnp()))
                .thenReturn(false);

        boolean actual = createCustomerRequestValidator.isValid(request, validatorContext);

        Assertions.assertThat(actual).isTrue();
        Mockito.verify(validatorContext, Mockito.never()).disableDefaultConstraintViolation();
        Mockito.verify(validatorContext, Mockito.never()).buildConstraintViolationWithTemplate(Mockito.anyString());
    }

    @Test
    @DisplayName("should return true for valid create customer request without unp")
    void shouldReturnTrueForValidCreateCustomerRequestWithoutUnp() {
        CreateCustomerRequest request = new CreateCustomerRequestTestBuilder().build();
        request.setCustomerType("PHYSIC");
        request.setUnp(null);
        when(customerService.isCustomerExist(request.getId(), request.getEmail()))
                .thenReturn(false);

        boolean actual = createCustomerRequestValidator.isValid(request, validatorContext);

        Assertions.assertThat(actual).isTrue();
        Mockito.verify(validatorContext, Mockito.never()).disableDefaultConstraintViolation();
        Mockito.verify(validatorContext, Mockito.never()).buildConstraintViolationWithTemplate(Mockito.anyString());
    }

    @Test
    @DisplayName("should return false customer whit such email or id already exist")
    void shouldReturnFalseForInvalidCreateCustomerRequestWithoutUnp() {
        CreateCustomerRequest request = new CreateCustomerRequestTestBuilder().build();
        request.setCustomerType("PHYSIC");
        request.setUnp(null);

        when(customerService.isCustomerExist(request.getId(), request.getEmail()))
                .thenReturn(true);
        Mockito.when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
               .thenReturn(constraintViolationBuilder);

        boolean actual = createCustomerRequestValidator.isValid(request, validatorContext);

        Assertions.assertThat(actual).isFalse();
        Mockito.verify(validatorContext).disableDefaultConstraintViolation();
        Mockito.verify(validatorContext).buildConstraintViolationWithTemplate(Mockito.anyString());
        Mockito.verify(constraintViolationBuilder).addConstraintViolation();
    }

    @Test
    @DisplayName("should return false customer whit such email, id or unp already exist")
    void shouldReturnFalseForInvalidCreateCustomerRequestWithUnp() {
        CreateCustomerRequest request = new CreateCustomerRequestTestBuilder().build();

        when(customerService.isCustomerExist(request.getId(), request.getEmail(), request.getUnp()))
                .thenReturn(true);
        Mockito.when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
               .thenReturn(constraintViolationBuilder);

        boolean actual = createCustomerRequestValidator.isValid(request, validatorContext);

        Assertions.assertThat(actual).isFalse();
        Mockito.verify(validatorContext).disableDefaultConstraintViolation();
        Mockito.verify(validatorContext).buildConstraintViolationWithTemplate(Mockito.anyString());
        Mockito.verify(constraintViolationBuilder).addConstraintViolation();
    }

    @Test
    @DisplayName("should return false customer whit type Legal must have unp")
    void shouldReturnFalseForLegalCreateCustomerRequestWithoutUnp() {
        CreateCustomerRequest request = new CreateCustomerRequestTestBuilder().build();
        request.setUnp(null);
        request.setCustomerType("LEGAL");

        when(customerService.isCustomerExist(request.getId(), request.getEmail(), request.getUnp()))
                .thenReturn(false);
        Mockito.when(validatorContext.buildConstraintViolationWithTemplate(Mockito.anyString()))
               .thenReturn(constraintViolationBuilder);

        boolean actual = createCustomerRequestValidator.isValid(request, validatorContext);

        Assertions.assertThat(actual).isFalse();
        Mockito.verify(validatorContext).disableDefaultConstraintViolation();
        Mockito.verify(validatorContext).buildConstraintViolationWithTemplate(Mockito.anyString());
        Mockito.verify(constraintViolationBuilder).addConstraintViolation();
    }
}