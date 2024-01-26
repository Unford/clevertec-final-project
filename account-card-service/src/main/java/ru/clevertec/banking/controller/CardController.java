package ru.clevertec.banking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.clevertec.banking.advice.model.ApiError;
import ru.clevertec.banking.dto.card.CardCurrencyResponse;
import ru.clevertec.banking.dto.card.CardRequest;
import ru.clevertec.banking.dto.card.CardRequestForUpdate;
import ru.clevertec.banking.dto.card.CardResponse;

import java.util.List;
import java.util.UUID;

@Tag(name = "Card", description = "The Card Api")
@SecurityRequirement(name = "Bearer Authentication")
public interface CardController {

    @Operation(summary = "Save new Card.", tags = "Card",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "RequestBody for Create Card Request",
                    content = @Content(schema = @Schema(implementation = CardRequest.class),
                            examples = @ExampleObject("""
                                    {
                                        "card_number": "5218347602398745",
                                        "card_number_readable": "5218 3476 0239 8745",
                                        "iban": "DE89370400440532013000234567",
                                        "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                        "customer_type": "PHYSIC",
                                        "cardholder": "CARDHOLDER NAME",
                                        "card_status": "ACTIVE"
                                    }
                                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card saved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardResponse.class), examples = @ExampleObject("""
                            {
                                "card_number": "5218347602398745",
                                "card_number_readable": "5218 3476 0239 8745",
                                "iban": "DE89370400440532013000234567",
                                "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                "customer_type": "PHYSIC",
                                "cardholder": "CARDHOLDER NAME",
                                "card_status": "ACTIVE"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Card with iban already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                        "status": 400,
                                        "message": "An card with such an card_number already exists"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                        "status": 400,
                                        "message": "card_status:[WHAAAAAT]: Acceptable card_status are only: ACTIVE, INACTIVE, BLOCKED or NEW"
                                    }
                                    """)))
    })
    CardResponse create(@RequestBody @Valid CardRequest request);

    @Operation(summary = "Update Card by card number.", tags = "Card",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "RequestBody for CardRequestForUpdate",
                    content = @Content(schema = @Schema(implementation = CardRequestForUpdate.class),
                            examples = @ExampleObject("""
                                    {
                                        "card_number": "5218347602398745",
                                        "iban": "DE89370400440532013000234567",
                                        "customer_type": "LEGAL",
                                        "card_status": "NEW"
                                    }
                                            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardResponse.class), examples = @ExampleObject("""
                            {
                                "card_number": "5218347602398745",
                                "card_number_readable": "5218 3476 0239 8745",
                                "iban": "DE89370400440532013000234567",
                                "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                "customer_type": "LEGAL",
                                "cardholder": "CARDHOLDER NAME",
                                "card_status": "NEW"
                                                        
                            """))),
            @ApiResponse(responseCode = "404", description = "No Card with this card number in database",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                                "status": 404,
                                "message": "Card with card_number: 52183476023987245 not found"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                        "status": 400,
                                        "message": "card_status:[NasdEW]: Acceptable card_status are only: ACTIVE, INACTIVE, BLOCKED or NEW"
                                    }
                                         """)))
    })
    CardResponse update(@RequestBody @Valid CardRequestForUpdate request);

    @Operation(summary = "Find all Cards with pagination.", tags = "Card",
            parameters = {
                    @Parameter(name = "page", description = "Enter your page number here", example = "0"),
                    @Parameter(name = "size", description = "Enter your page size here", example = "2"),
                    @Parameter(name = "sort", description = "Enter your sort by here", example = "iban")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of Cards retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardResponse.class), examples = @ExampleObject("""
                            {
                                "content": [
                                    {
                                        "card_number": "5218347602398745",
                                        "card_number_readable": "5218 3476 0239 8745",
                                        "iban": "DE89370400440532013000234567",
                                        "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                        "customer_type": "PHYSIC",
                                        "cardholder": "CARDHOLDER NAME",
                                        "card_status": "ACTIVE"
                                    },
                                    {
                                        "card_number": "5200000000045547",
                                        "card_number_readable": "5200 0000 0004 5547",
                                        "iban": "FFFFCCCDDDDEEEEEEEEEEEEEEEEG",
                                        "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                        "customer_type": "LEGAL",
                                        "cardholder": "CARDHOLDER NAME",
                                        "card_status": "ACTIVE"
                                    }
                                ],
                                "pageable": {
                                    "pageNumber": 0,
                                    "pageSize": 2,
                                    "sort": [
                                        {
                                            "direction": "ASC",
                                            "property": "iban",
                                            "ignoreCase": false,
                                            "nullHandling": "NATIVE",
                                            "ascending": true,
                                            "descending": false
                                        }
                                    ],
                                    "offset": 0,
                                    "paged": true,
                                    "unpaged": false
                                },
                                "last": false,
                                "totalPages": 3,
                                "totalElements": 6,
                                "first": true,
                                "size": 2,
                                "number": 0,
                                "sort": [
                                    {
                                        "direction": "ASC",
                                        "property": "iban",
                                        "ignoreCase": false,
                                        "nullHandling": "NATIVE",
                                        "ascending": true,
                                        "descending": false
                                    }
                                ],
                                "numberOfElements": 2,
                                "empty": false
                            }
                            """))),
            @ApiResponse(responseCode = "500", description = "Wrong pageable sort",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                                "status": 500,
                                "message": "Unhandled exception: No property 'notProperty' found for type 'Card' cause: null request-Uri: /api/v1/cards"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 403,
                              "message": "Access denied"
                            }
                            """)))})
    Page<CardResponse> getAll(@PageableDefault(sort = {"iban"}) Pageable pageable);

    @Operation(summary = "Find Cards by customer id(UUID).", tags = "Card",
            parameters = @Parameter(name = "uuid", description = "Enter uuid here", example = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List with Cards retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardResponse.class), examples = @ExampleObject("""
                            [
                                {
                                    "card_number": "5200000000045547",
                                    "card_number_readable": "5200 0000 0004 5547",
                                    "iban": "FFFFCCCDDDDEEEEEEEEEEEEEEEEG",
                                    "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                    "customer_type": "LEGAL",
                                    "cardholder": "CARDHOLDER NAME",
                                    "card_status": "ACTIVE"
                                },
                                {
                                    "card_number": "5200000300001088",
                                    "card_number_readable": "5200 0003 0000 1088",
                                    "iban": "FFFFCCCDDDDEEEEEEEEEEEEWESSS",
                                    "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                    "customer_type": "LEGAL",
                                    "cardholder": "CARDHOLDER NAME",
                                    "card_status": "ACTIVE"
                                }
                            ]
                            """))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                                "status": 403,
                                "message": "Access Denied"
                            }
                            """)))
    })
    List<CardResponse> findByCustomer(@PathVariable UUID uuid);

    @Operation(summary = "Find Card by cartNumber with balance in other curr.", tags = "Card",
            parameters = @Parameter(name = "cardNumber", description = "Enter cardNumber here", example = "5218347602398745"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CardCurrencyResponse.class), examples = @ExampleObject("""
                            {
                                "card_number": "5218347602398745",
                                "card_number_readable": "5218 3476 0239 8745",
                                "iban": "DE89370400440532013000234567",
                                "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                "customer_type": "PHYSIC",
                                "cardholder": "CARDHOLDER NAME",
                                "card_status": "ACTIVE",
                                "card_balance": {
                                    "main_currency_card": "BYN",
                                    "balance": "21500.00",
                                    "in_other_currencies": {
                                        "EUR": 6268.22,
                                        "USD": 6825.39
                                    }
                                }
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                                "status": 403,
                                "message": "Access Denied"
                            }
                            """)))
    })
    CardCurrencyResponse findByCardNumber(@PathVariable String cardNumber);

    @Operation(summary = "Delete Card by cardNumber.", tags = "Card",
            parameters = @Parameter(name = "cardNumber", description = "Enter cardNumber here", example = "5218347602398745"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 403,
                              "message": "Access denied"
                            }
                            """)))
    })
    void delete(@PathVariable String cardNumber);


}
