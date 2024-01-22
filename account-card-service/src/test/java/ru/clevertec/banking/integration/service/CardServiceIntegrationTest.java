package ru.clevertec.banking.integration.service;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestConstructor;
import ru.clevertec.banking.advice.exception.ResourceNotFoundException;
import ru.clevertec.banking.dto.card.*;
import ru.clevertec.banking.entity.Card;
import ru.clevertec.banking.configuration.PostgreSQLContainerConfig;
import ru.clevertec.banking.mapper.CardMapper;
import ru.clevertec.banking.service.CardService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static ru.clevertec.banking.util.CardFactory.*;
import static ru.clevertec.banking.util.FileReaderUtils.*;

@SpringBootTest
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@ActiveProfiles("test")
@RequiredArgsConstructor
@ContextConfiguration(classes = {PostgreSQLContainerConfig.class})
@Tag("integration")
public class CardServiceIntegrationTest {

    private final CardService cardService;
    private final CardMapper mapper;
    private CardResponse response;
    private CardRequest request;

    @BeforeEach
    void init() {
        response = new CardResponse("3786543201987456",
                "3786 5432 0198 7456",
                "FR1420041010050500013M026060",
                UUID.fromString("8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f"),
                "PHYSIC",
                "CARDHOLDER NAME",
                "BLOCKED");

        request = new CardRequest("1111111111111111",
                "1111 1111 1111 1111",
                "EN1420041010050500013M026060",
                "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f",
                "LEGAL",
                "CARDHOLDER JESUS",
                "NEW");
    }


    @Nested
    @WireMockTest(httpPort = 6666)
    class CurrencyRateClientTest {

        @Test
        @DisplayName("test should return expected response and balance")
        void findByCardNumberTest() throws IOException {
            WireMock.stubFor(WireMock.get("/api/v1/currencies")
                    .willReturn(WireMock.okJson(readFile("/get-actual-currency.json"))
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            Balance expectedBalance = new Balance("BYN",
                    "2100.00",
                    Map.of("USD", BigDecimal.valueOf(666.66), "EUR", BigDecimal.valueOf(612.24)));

            Card card = getCard(null, false);
            card.setCardStatus("INACTIVE");

            CardCurrencyResponse expected = mapper.toCardWithBalance(card, expectedBalance);
            CardCurrencyResponse actual = cardService.findByCardNumber("5200000000001096");

            assertThat(actual.card_balance()).isEqualTo(expectedBalance);
            assertThat(actual).isEqualTo(expected);
        }

        @Test
        @DisplayName("test should NotFoundException and throw FeignException")
        void findByCardNumberExceptionTest() throws IOException {
            WireMock.stubFor(WireMock.get("/api/v1/currencies")
                    .willReturn(WireMock.jsonResponse(readFile("/get-actual-currency-not-found.json"),
                                    HttpStatus.NOT_FOUND.value())
                            .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)));

            assertThatThrownBy(() -> cardService.findByCardNumber("5200000000001096"))
                    .isInstanceOf(FeignException.NotFound.class);
        }
    }


    @Test
    @DisplayName("test should return List with expected response")
    void findByCustomerTest() {
        CardResponse expected = response;

        List<CardResponse> actual = cardService.findByCustomer(expected.customer_id());

        assertThat(actual).contains(expected);
    }

    @Test
    @DisplayName("test should return Page with expected response")
    void findByIbanTest() {
        CardResponse expected = response;
        Pageable pageable = PageRequest.of(0, 100);

        Page<CardResponse> actual = cardService.findByIban(expected.iban(), pageable);

        assertThat(actual).contains(expected);
    }


    @Test
    @DisplayName("test should throw ResourceNotFoundException")
    void findByCardNumberNotFoundTest() {
        assertThatThrownBy(() -> cardService.findByCardNumber("NOT FOUND"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("test should return Page with responses")
    void findAllTest() {
        Pageable pageable = PageRequest.of(0, 100);

        Page<CardResponse> actual = cardService.findAll(pageable);

        assertThat(actual).isNotEmpty();
    }

    @Test
    @DisplayName("test should return expected responses")
    void saveTest() {
        CardResponse expected = Optional.of(request)
                .map(mapper::fromRequest)
                .map(mapper::toResponse)
                .orElseThrow();

        CardResponse actual = cardService.save(request);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("test should return expected responses after update")
    void updateTest() {
        CardRequestForUpdate requestForUpdate = new CardRequestForUpdate(
                "5218347602398745",
                "FR1420041010050500013M026060",
                "LEGAL",
                "BLOCKED");

        CardResponse actual = cardService.update(requestForUpdate);

        assertThat(actual.iban()).isEqualTo(requestForUpdate.iban());
        assertThat(actual.customer_type()).isEqualTo(requestForUpdate.customer_type());
        assertThat(actual.card_status()).isEqualTo(requestForUpdate.card_status());
    }

    @Test
    @DisplayName("the test should check whether the method throws an error if the entity being updated is not found")
    void updateNotFoundTest() {
        CardRequestForUpdate requestForUpdate = new CardRequestForUpdate(
                "127368715918",
                "FR1420041010050500013M026060",
                "LEGAL",
                "BLOCKED");

        assertThatThrownBy(() -> cardService.update(requestForUpdate))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("test should return an exception after trying to retrieve an Card that has been deleted")
    void deleteByCardNumberTest() {
        cardService.save(request);

        cardService.deleteByCardNumber(request.card_number());

        assertThatThrownBy(() -> cardService.findByCardNumber(request.card_number()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
