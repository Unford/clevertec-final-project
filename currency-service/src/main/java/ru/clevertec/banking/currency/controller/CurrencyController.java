package ru.clevertec.banking.currency.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.clevertec.banking.advice.model.ApiError;
import ru.clevertec.banking.currency.model.dto.response.ExchangeRateResponse;

import java.time.OffsetDateTime;

@Tag(name = "Currency", description = "The Currency Api")

public interface CurrencyController {
    @Operation(summary = "Find latest currencies.", tags = "Currency",
            parameters = @Parameter(name = "dateTime", description = "Enter dateTime here", example = "2024-01-03T13:56:51.604498616Z"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Currencies retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ExchangeRateResponse.class),
                            examples = @ExampleObject("""
                                    {
                                                "startDt": "2024-01-03T13:56:51.604498616+03:00",
                                                "exchangeRates": [
                                                    {
                                                        "buyRate": 3.33,
                                                        "sellRate": 3.43,
                                                        "srcCurr": "EUR",
                                                        "reqCurr": "BYN"
                                                    },
                                                    {
                                                        "buyRate": 3.05,
                                                        "sellRate": 3.15,
                                                        "srcCurr": "USD",
                                                        "reqCurr": "BYN"
                                                    },
                                                    {
                                                        "id": 5,
                                                        "buyRate": 1.075,
                                                        "sellRate": 1.1,
                                                        "srcCurr": "EUR",
                                                        "reqCurr": "USD"
                                                    }
                                                ]
                                        }
                                                                
                            """))),
            @ApiResponse(responseCode = "404", description = "Currencies are not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 404,
                              "message": "Currencies are not found"
                            }
                            """)))
    })

    @GetMapping
    ExchangeRateResponse findLatestCurrencies(@RequestParam(name = "dateTime",
            defaultValue = "#{T(java.time.OffsetDateTime).now()}") OffsetDateTime dateTime);
}
