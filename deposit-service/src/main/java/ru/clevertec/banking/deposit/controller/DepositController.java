package ru.clevertec.banking.deposit.controller;

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
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.clevertec.banking.advice.model.ApiError;
import ru.clevertec.banking.deposit.model.dto.request.CreateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.request.UpdateDepositRequest;
import ru.clevertec.banking.deposit.model.dto.response.DepositResponse;

import java.util.List;
import java.util.UUID;

@Tag(name = "Deposit", description = "The Deposit Api")
@SecurityRequirement(name = "Bearer Authentication")
public interface DepositController {

    @Operation(summary = "Find all Deposits with pagination.", tags = "Deposit",
            parameters = {
                    @Parameter(name = "page", description = "Enter your page number here", example = "0"),
                    @Parameter(name = "size", description = "Enter your page size here", example = "2"),
                    @Parameter(name = "sort", description = "Enter your sort by here",
                            schema = @Schema(type = "array", example = "[\"depInfo.expDate\"]"))
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of Deposits retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DepositResponse.class), examples = @ExampleObject("""
                            {
                              "content": [
                                {
                                  "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc721",
                                  "customer_type": "LEGAL",
                                  "acc_info": {
                                    "acc_iban": "SA0380000000608010167519",
                                    "acc_open_date": "04.01.2024",
                                    "curr_amount": 100000.44,
                                    "curr_amount_currency": "SAR"
                                  },
                                  "dep_info": {
                                    "rate": 0.04,
                                    "term_val": 1,
                                    "term_scale": "D",
                                    "exp_date": "02.01.2024",
                                    "dep_type": "IRREVOCABLE",
                                    "auto_renew": false
                                  }
                                },
                                {
                                  "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                  "customer_type": "LEGAL",
                                  "acc_info": {
                                    "acc_iban": "FR7630001007941234567890185",
                                    "acc_open_date": "01.01.2024",
                                    "curr_amount": 10000.00,
                                    "curr_amount_currency": "EUR"
                                  },
                                  "dep_info": {
                                    "rate": 0.05,
                                    "term_val": 12,
                                    "term_scale": "D",
                                    "exp_date": "13.01.2024",
                                    "dep_type": "REVOCABLE",
                                    "auto_renew": true
                                  }
                                }
                              ],
                              "pageable": {
                                "pageNumber": 0,
                                "pageSize": 2,
                                "sort": [
                                  {
                                    "direction": "ASC",
                                    "property": "depInfo.expDate",
                                    "ignoreCase": false,
                                    "nullHandling": "NATIVE",
                                    "ascending": true,
                                    "descending": false
                                  }
                                ],
                                "offset": 0,
                                "unpaged": false,
                                "paged": true
                              },
                              "last": false,
                              "totalPages": 5,
                              "totalElements": 10,
                              "size": 2,
                              "number": 0,
                              "sort": [
                                {
                                  "direction": "ASC",
                                  "property": "depInfo.expDate",
                                  "ignoreCase": false,
                                  "nullHandling": "NATIVE",
                                  "ascending": true,
                                  "descending": false
                                }
                              ],
                              "first": true,
                              "numberOfElements": 2,
                              "empty": false
                            }
                            """))),
            @ApiResponse(responseCode = "500", description = "Wrong pageable sort",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 406,
                              "message": "Unhandled exception: No property 'string' found for type 'Deposit' cause: null request-Uri: /api/v1/deposits"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 403,
                              "message": "Access denied"
                            }
                            """)))
    })
    Page<DepositResponse> findAll(@ParameterObject Pageable pageable);

    @Operation(summary = "Find Deposit by iban.", tags = "Deposit",
            parameters = @Parameter(name = "iban", description = "Enter iban here", example = "GR9608100010000001234567890"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DepositResponse.class), examples = @ExampleObject("""
                            {
                              "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                              "customer_type": "LEGAL",
                              "acc_info": {
                                "acc_iban": "GR9608100010000001234567890",
                                "acc_open_date": "01.01.2024",
                                "curr_amount": 10000.00,
                                "curr_amount_currency": "EUR"
                              },
                              "dep_info": {
                                "rate": 0.05,
                                "term_val": 12,
                                "term_scale": "D",
                                "exp_date": "13.01.2024",
                                "dep_type": "REVOCABLE",
                                "auto_renew": true
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "Deposit with this iban is not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 404,
                              "message": "Deposit with iban 'GR9608100010000001234567890' is not found"
                            }
                            """))),

            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 403,
                              "message": "Access denied"
                            }
                            """)))
    })
    DepositResponse findByAccountIban(String iban);

    @Operation(summary = "Find all Deposits by customer id", tags = "Deposit",
            parameters = {
                    @Parameter(name = "customerId", description = "Enter your customer Id here",
                            example = "1a72a05f-4b8f-43c5-a889-1ebc6d9dc721"),
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of Deposits retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DepositResponse.class), examples = @ExampleObject("""
                            
                             [
                                {
                                  "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc721",
                                  "customer_type": "LEGAL",
                                  "acc_info": {
                                    "acc_iban": "SA0380000000608010167519",
                                    "acc_open_date": "04.01.2024",
                                    "curr_amount": 100000.44,
                                    "curr_amount_currency": "SAR"
                                  },
                                  "dep_info": {
                                    "rate": 0.04,
                                    "term_val": 1,
                                    "term_scale": "D",
                                    "exp_date": "02.01.2024",
                                    "dep_type": "IRREVOCABLE",
                                    "auto_renew": false
                                  }
                                },
                                {
                                  "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                  "customer_type": "LEGAL",
                                  "acc_info": {
                                    "acc_iban": "FR7630001007941234567890185",
                                    "acc_open_date": "01.01.2024",
                                    "curr_amount": 10000.00,
                                    "curr_amount_currency": "EUR"
                                  },
                                  "dep_info": {
                                    "rate": 0.05,
                                    "term_val": 12,
                                    "term_scale": "D",
                                    "exp_date": "13.01.2024",
                                    "dep_type": "REVOCABLE",
                                    "auto_renew": true
                                  }
                                }
                              ]
                            """))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 403,
                              "message": "Access denied"
                            }
                            """)))
    })
    List<DepositResponse> findAllByCustomerId(UUID customerId);


    @Operation(summary = "Delete Deposit by iban.", tags = "Deposit",
            parameters = @Parameter(name = "iban", description = "Enter iban here", example = "FR7630001007941234567890185"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deposit deleted successfully",
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
    void deleteByAccountIban(String iban);

    @Operation(summary = "Save new Deposit.", tags = "Deposit",
            requestBody = @RequestBody(description = "RequestBody for CreateDepositRequest",
                    content = @Content(schema = @Schema(implementation = CreateDepositRequest.class),
                            examples = @ExampleObject("""
                                    {
                                      "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                      "customer_type": "LEGAL",
                                      "acc_info": {
                                        "acc_iban": "AABBCCCDDDDEEEEEEEEEEEEEEEEE",
                                        "curr_amount": 3000.00,
                                        "curr_amount_currency": "BYN"
                                      },
                                      "dep_info": {
                                        "rate": 14.50,
                                        "term_val": 24,
                                        "term_scale": "M",
                                        "dep_type": "REVOCABLE",
                                        "auto_renew": true
                                      }
                                    }
                                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Deposit saved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DepositResponse.class), examples = @ExampleObject("""
                            {
                              "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                              "customer_type": "LEGAL",
                              "acc_info": {
                                "acc_iban": "AABBCCCDDDDEEEEEEEEEEEEEEEEE",
                                "acc_open_date": "17.01.2024",
                                "curr_amount": 3000.00,
                                "curr_amount_currency": "BYN"
                              },
                              "dep_info": {
                                "rate": 14.50,
                                "term_val": 24,
                                "term_scale": "M",
                                "exp_date": "17.01.2026",
                                "dep_type": "REVOCABLE",
                                "auto_renew": true
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Deposit with iban already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                      "status": 400,
                                      "message": "Deposit with acc_iban is already exist"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                    "status": 400,
                                    "message": "UpdateDepositRequest has invalid fields"
                                          }
                                    """)))
    })
    DepositResponse createDeposit(@ParameterObject CreateDepositRequest createDepositRequest);


    @Operation(summary = "Update Deposit by iban.", tags = "Deposit",
            parameters = @Parameter(name = "iban", description = "Enter iban here", example = "FR7630001007941234567890185"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "RequestBody for UpdateDepositRequest",
                    content = @Content(schema = @Schema(implementation = UpdateDepositRequest.class),
                            examples = @ExampleObject("""
                                    {
                                        "dep_info": {
                                            "dep_type": "IRREVOCABLE",
                                            "auto_renew": true
                                        }
                                    }
                                            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Deposit updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = DepositResponse.class), examples = @ExampleObject("""
                            {
                              "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                              "customer_type": "LEGAL",
                              "acc_info": {
                                "acc_iban": "FR7630001007941234567890185",
                                "acc_open_date": "01.01.2024",
                                "curr_amount": 10000.00,
                                "curr_amount_currency": "EUR"
                              },
                              "dep_info": {
                                "rate": 0.05,
                                "term_val": 12,
                                "term_scale": "D",
                                "exp_date": "13.01.2024",
                                "dep_type": "IRREVOCABLE",
                                "auto_renew": false
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "No Deposit with this iban in database",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 404,
                              "message": "Deposit with iban FR763000100794123456789018 is not found"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                           {
                                    "status": 400,
                                    "message": "UpdateDepositRequest has invalid fields"
                                          }
                                          """)))
    })
    DepositResponse updateDeposit(String iban,
                                  @Valid @ParameterObject UpdateDepositRequest updateDepositRequest);
}
