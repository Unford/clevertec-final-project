package ru.clevertec.banking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.PathVariable;
import ru.clevertec.banking.advice.model.ApiError;
import ru.clevertec.banking.dto.account.AccountRequest;
import ru.clevertec.banking.dto.account.AccountRequestForUpdate;
import ru.clevertec.banking.dto.account.AccountResponse;
import ru.clevertec.banking.dto.account.AccountWithCardResponse;

import java.util.List;
import java.util.UUID;

@Tag(name = "Account", description = "The Account Api")
@SecurityRequirement(name = "Bearer Authentication")
public interface AccountController {
    @Operation(summary = "Find all Accounts with pagination.", tags = "Account",
            parameters = {
                    @Parameter(name = "page", description = "Enter your page number here", example = "0"),
                    @Parameter(name = "size", description = "Enter your page size here", example = "2"),
                    @Parameter(name = "sort", description = "Enter your sort by here", example = "openDate")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of Accounts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountWithCardResponse.class), examples = @ExampleObject("""
                            {
                                "content": [
                                    {
                                        "name": "Название счёта 1",
                                        "iban": "FFFFCCCDDDDEEEEEEEEEEEEWESSS",
                                        "iban_readable": "FFFF CCCD DDDE EEEE EEEE EEEW ESSS",
                                        "amount": 222100.0000000000,
                                        "currency_code": "EUR",
                                        "open_date": "01.12.2001",
                                        "main_acc": true,
                                        "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                        "customer_type": "LEGAL",
                                        "rate": 0.01,
                                        "cards": [
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
                                    },
                                    {
                                        "name": "Название счёта 3",
                                        "iban": "US58469159383322778899012345",
                                        "iban_readable": "US58 4691 5938 3322 7788 9901 2345",
                                        "amount": 1500.0000000000,
                                        "currency_code": "933",
                                        "open_date": "01.12.2006",
                                        "main_acc": true,
                                        "customer_id": "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f",
                                        "customer_type": "PHYSIC",
                                        "rate": 0.01,
                                        "cards": [
                                            {
                                                "card_number": "3786543201987456",
                                                "card_number_readable": "3786 5432 0198 7456",
                                                "iban": "US58469159383322778899012345",
                                                "customer_id": "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f",
                                                "customer_type": "PHYSIC",
                                                "cardholder": "CARDHOLDER NAME",
                                                "card_status": "ACTIVE"
                                            },
                                            {
                                                "card_number": "4857263091275043",
                                                "card_number_readable": "4857 2630 9127 5043",
                                                "iban": "US58469159383322778899012345",
                                                "customer_id": "8a7b3f5e-6d12-47f9-8c9a-1fcb4d3c928f",
                                                "customer_type": "PHYSIC",
                                                "cardholder": "CARDHOLDER NAME",
                                                "card_status": "INACTIVE"
                                            }
                                        ]
                                    }
                                ],
                                "pageable": {
                                    "pageNumber": 0,
                                    "pageSize": 2,
                                    "sort": [
                                        {
                                            "direction": "ASC",
                                            "property": "openDate",
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
                                "totalPages": 2,
                                "totalElements": 4,
                                "first": true,
                                "size": 2,
                                "number": 0,
                                "sort": [
                                    {
                                        "direction": "ASC",
                                        "property": "openDate",
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
                              "status": 406,
                              "message": "Unhandled exception: No property 'openDate1' found for type 'Account'; Did you mean 'openDate' cause: null request-Uri: /api/v1/accounts"
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
    Page<AccountWithCardResponse> getAll(@PageableDefault(sort = {"iban"}) Pageable pageable);

    @Operation(summary = "Save new Account.", tags = "Account",
            requestBody = @RequestBody(description = "RequestBody for Create Account Request",
                    content = @Content(schema = @Schema(implementation = AccountRequest.class),
                            examples = @ExampleObject("""
                                    {
                                        "name": "Название счёта 1",
                                        "iban": "FFFFCCCDDDDEEEEEEEEEEEEWESSS",
                                        "iban_readable": "AABB CCC DDDD EEEE EEEE EEEE EEEE",
                                        "amount": 222100.00,
                                        "currency_code": "EUR",
                                        "open_date": "01.12.2001",
                                        "main_acc": true,
                                        "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                        "customer_type" : "LEGAL",
                                        "rate": 0.01
                                      }
                                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account saved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class), examples = @ExampleObject("""
                            {
                                "name": "Название счёта 1",
                                "iban": "FFFFCCCDDDDEEEEEEEEEEEEWESSS",
                                "iban_readable": "FFFF CCCD DDDE EEEE EEEE EEEW ESSS",
                                "amount": 222100.0000000000,
                                "currency_code": "EUR",
                                "open_date": "01.12.2001",
                                "main_acc": true,
                                "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                "customer_type": "LEGAL",
                                "rate": 0.01
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Account with iban already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                        "status": 400,
                                        "message": "An account with such an iban already exists"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                        "status": 400,
                                        "message": "customer_type:[asfgasdga]: Acceptable customer_type are only: LEGAL or PHYSIC"
                                    }
                                    """)))
    })
    AccountResponse create(@RequestBody @Valid AccountRequest request);

    @Operation(summary = "Find Accounts with Cards(relations vy iban) by customer id(UUID).", tags = "Account",
            parameters = @Parameter(name = "uuid", description = "Enter uuid here", example = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List with Accounts retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountWithCardResponse.class), examples = @ExampleObject("""
                            [
                                {
                                    "name": "Название счёта 1",
                                    "iban": "FFFFCCCDDDDEEEEEEEEEEEEWESSS",
                                    "iban_readable": "FFFF CCCD DDDE EEEE EEEE EEEW ESSS",
                                    "amount": 222100.0000000000,
                                    "currency_code": "EUR",
                                    "open_date": "01.12.2001",
                                    "main_acc": true,
                                    "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                    "customer_type": "LEGAL",
                                    "rate": 0.01,
                                    "cards": [
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
    List<AccountWithCardResponse> findByCustomer(@PathVariable UUID uuid);

    @Operation(summary = "Find Account by iban", tags = "Account",
            parameters = @Parameter(name = "iban", description = "Enter iban here", example = "FFFFCCCDDDDEEEEEEEEEEEEWESSS"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class), examples = @ExampleObject("""
                            {
                                "name": "Название счёта 1",
                                "iban": "FFFFCCCDDDDEEEEEEEEEEEEWESSS",
                                "iban_readable": "FFFF CCCD DDDE EEEE EEEE EEEW ESSS",
                                "amount": 222100.0000000000,
                                "currency_code": "EUR",
                                "open_date": "01.12.2001",
                                "main_acc": true,
                                "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                "customer_type": "LEGAL",
                                "rate": 0.01
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
    AccountResponse findByIban(@PathVariable String iban);

    @Operation(summary = "Update Account by iban.", tags = "Account",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "RequestBody for AccountRequestForUpdate",
                    content = @Content(schema = @Schema(implementation = AccountRequestForUpdate.class),
                            examples = @ExampleObject("""
                                    {
                                        "iban": "FFFFCCCDDDDEEEEEEEEEEEEWESSE",
                                        "name": "Название счёта 15",
                                        "main_acc": false,
                                        "customer_type": "LEGAL"
                                    }
                                            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccountResponse.class), examples = @ExampleObject("""
                            {
                                "name": "Название счёта 15",
                                "iban": "FFFFCCCDDDDEEEEEEEEEEEEWESSS",
                                "iban_readable": "FFFF CCCD DDDE EEEE EEEE EEEW ESSS",
                                "amount": 222100.00,
                                "currency_code": "EUR",
                                "open_date": "01.12.2001",
                                "main_acc": false,
                                "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                "customer_type": "LEGAL",
                                "rate": 0.01
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "No Account with this iban in database",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                                "status": 404,
                                "message": "Account with iban: FFFFCCCDDDDEEEEEEEEEEEEWESSES not found"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                        "status": 400,
                                        "message": "customer_type:[asfa]: Acceptable customer_type are only: LEGAL or PHYSIC"
                                    }
                                         """)))
    })
    AccountResponse update(@org.springframework.web.bind.annotation.RequestBody @Valid AccountRequestForUpdate request);

    @Operation(summary = "Delete Account by iban.", tags = "Account",
            parameters = @Parameter(name = "iban", description = "Enter iban here", example = "FFFFCCCDDDDEEEEEEEEEEEEWESSES"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Account deleted successfully",
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
    void delete(@PathVariable String iban);

}
