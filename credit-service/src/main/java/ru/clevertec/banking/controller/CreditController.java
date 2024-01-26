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
import ru.clevertec.banking.dto.CreditRequest;
import ru.clevertec.banking.dto.CreditRequestForUpdate;
import ru.clevertec.banking.dto.CreditResponse;

import java.util.List;
import java.util.UUID;

@Tag(name = "Credit", description = "The Credit Api")
@SecurityRequirement(name = "Bearer Authentication")
public interface CreditController {

    @Operation(summary = "Find all Cards with pagination.", tags = "Credit",
            parameters = {
                    @Parameter(name = "page", description = "Enter your page number here", example = "0"),
                    @Parameter(name = "size", description = "Enter your page size here", example = "2"),
                    @Parameter(name = "sort", description = "Enter your sort by here", example = "contractNumber")
            })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of Credits retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditResponse.class), examples = @ExampleObject("""
                            {
                                "content": [
                                    {
                                        "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                        "contractNumber": "11-0216111-2-0",
                                        "contractStartDate": "30.03.2020",
                                        "totalDebt": 7000.00,
                                        "currentDebt": 300.00,
                                        "currency": "BYN",
                                        "repaymentDate": "16.01.2020",
                                        "rate": 22.8,
                                        "iban": "DE89370400440532013000234567",
                                        "possibleRepayment": true,
                                        "isClosed": false,
                                        "customer_type": "PHYSIC"
                                    },
                                    {
                                        "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                        "contractNumber": "11-0216133-2-0",
                                        "contractStartDate": "10.01.2000",
                                        "totalDebt": 999999.00,
                                        "currentDebt": 3020.00,
                                        "currency": "BYN",
                                        "repaymentDate": "16.05.2025",
                                        "rate": 22.8,
                                        "iban": "DE89370400440532013000234567",
                                        "possibleRepayment": true,
                                        "isClosed": false,
                                        "customer_type": "PHYSIC"
                                    }
                                ],
                                "pageable": {
                                    "pageNumber": 0,
                                    "pageSize": 2,
                                    "sort": {
                                        "sorted": true,
                                        "unsorted": false,
                                        "empty": false
                                    },
                                    "offset": 0,
                                    "paged": true,
                                    "unpaged": false
                                },
                                "last": false,
                                "totalPages": 3,
                                "totalElements": 5,
                                "first": true,
                                "sort": {
                                    "sorted": true,
                                    "unsorted": false,
                                    "empty": false
                                },
                                "number": 0,
                                "numberOfElements": 2,
                                "size": 2,
                                "empty": false
                            }
                            """))),
            @ApiResponse(responseCode = "500", description = "Wrong pageable sort",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                                "status": 500,
                                "message": "Unhandled exception: No property 'what' found for type 'Credit' cause: null request-Uri: /api/v1/credits"
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
    Page<CreditResponse> getAll(@PageableDefault(sort = {"contractNumber"}) Pageable pageable);


    @Operation(summary = "Save new Credit.", tags = "Credit",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "RequestBody for Create Credit Request",
                    content = @Content(schema = @Schema(implementation = CreditRequest.class),
                            examples = @ExampleObject("""
                                    {
                                        "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                        "contractNumber": "11-0216111-2-0",
                                        "contractStartDate": "30.03.2020",
                                        "totalDebt": 7000.00,
                                        "currentDebt": 300.00,
                                        "currency": "BYN",
                                        "repaymentDate": "16.01.2020",
                                        "rate": 22.8,
                                        "iban": "DE89370400440532013000234567",
                                        "possibleRepayment": true,
                                        "isClosed": false,
                                        "customer_type": "PHYSIC"
                                    }
                                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Credit saved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditResponse.class), examples = @ExampleObject("""
                            {
                                "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                "contractNumber": "11-0216111-2-0",
                                "contractStartDate": "30.03.2020",
                                "totalDebt": 7000.00,
                                "currentDebt": 300.00,
                                "currency": "BYN",
                                "repaymentDate": "16.01.2020",
                                "rate": 22.8,
                                "iban": "DE89370400440532013000234567",
                                "possibleRepayment": true,
                                "isClosed": false,
                                "customer_type": "PHYSIC"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Credit with contract number already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                        "status": 400,
                                        "message": "An credit with such an contract number already exists"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Credit with iban already exists",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                        "status": 400,
                                        "message": "An credit with such an iban already exists"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                        "status": 400,
                                        "message": "customer_type:[asdasd]: Acceptable customer_type are only: LEGAL or PHYSIC"
                                    }
                                    """)))
    })
    CreditResponse create(@RequestBody @Valid CreditRequest request);


    @Operation(summary = "Update Credit by contract number.", tags = "Credit",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "RequestBody for CreditRequestForUpdate",
                    content = @Content(schema = @Schema(implementation = CreditRequestForUpdate.class),
                            examples = @ExampleObject("""
                                    {
                                         "contractNumber": "11-0216111-2-0",
                                         "rate": 55.5,
                                         "possibleRepayment": false,
                                         "isClosed": false,
                                         "customer_type": "LEGAL"
                                    }
                                            """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credit updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditResponse.class), examples = @ExampleObject("""
                            {
                                "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                "contractNumber": "11-0216111-2-0",
                                "contractStartDate": "30.03.2020",
                                "totalDebt": 7000.00,
                                "currentDebt": 300.00,
                                "currency": "BYN",
                                "repaymentDate": "16.01.2020",
                                "rate": 55.5,
                                "iban": "DE89370400440532013000234567",
                                "possibleRepayment": false,
                                "isClosed": false,
                                "customer_type": "LEGAL"
                            }                           
                            """))),
            @ApiResponse(responseCode = "404", description = "No Credit with this contractNumber in database",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                                "status": 404,
                                "message": "Credit with contractNumber: 11-02161111-2-0 not found"
                            }
                            """))),
            @ApiResponse(responseCode = "400", description = "Validation error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class),
                            examples = @ExampleObject("""
                                    {
                                        "status": 400,
                                        "message": "rate:[-55.5]: must be greater than 0"
                                    }
                                         """)))
    })
    CreditResponse update(@RequestBody @Valid CreditRequestForUpdate request);


    @Operation(summary = "Delete Credit by contract number.", tags = "Credit",
            parameters = @Parameter(name = "contractNumber", description = "Enter contractNumber here", example = "11-0216111-2-0"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Credit deleted successfully",
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
    void deleteByContractNumber(@PathVariable String contractNumber);


    @Operation(summary = "Find Credit by contractNumber ", tags = "Credit",
            parameters = @Parameter(name = "contractNumber", description = "Enter contractNumber here", example = "11-0216111-2-0"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Credit retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditResponse.class), examples = @ExampleObject("""
                            {
                                "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                "contractNumber": "11-0216111-2-0",
                                "contractStartDate": "30.03.2020",
                                "totalDebt": 7000.00,
                                "currentDebt": 300.00,
                                "currency": "BYN",
                                "repaymentDate": "16.01.2020",
                                "rate": 55.5,
                                "iban": "DE89370400440532013000234567",
                                "possibleRepayment": false,
                                "isClosed": false,
                                "customer_type": "LEGAL"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "No Credit with this contractNumber in database",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                                "status": 404,
                                "message": "Credit with contractNumber: 11-0216111-2-00 not found"
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
    CreditResponse getByContractNumber(@PathVariable String contractNumber);

    @Operation(summary = "Find Credits by customer id(UUID).", tags = "Credit",
            parameters = @Parameter(name = "uuid", description = "Enter uuid here", example = "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List with Credits retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = CreditResponse.class), examples = @ExampleObject("""
                            [
                                {
                                    "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                    "contractNumber": "11-0216133-2-0",
                                    "contractStartDate": "10.01.2000",
                                    "totalDebt": 999999.00,
                                    "currentDebt": 3020.00,
                                    "currency": "BYN",
                                    "repaymentDate": "16.05.2025",
                                    "rate": 22.8,
                                    "iban": "DE89370400440532013000234567",
                                    "possibleRepayment": true,
                                    "isClosed": false,
                                    "customer_type": "PHYSIC"
                                },
                                {
                                    "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                    "contractNumber": "11-0216111-2-01",
                                    "contractStartDate": "30.03.2020",
                                    "totalDebt": 7000.00,
                                    "currentDebt": 300.00,
                                    "currency": "BYN",
                                    "repaymentDate": "16.01.2020",
                                    "rate": 22.8,
                                    "iban": "DE89370400440532013000234567",
                                    "possibleRepayment": true,
                                    "isClosed": false,
                                    "customer_type": "PHYSIC"
                                },
                                {
                                    "customer_id": "fbd6a92b-9e43-4c88-aa2b-6e1fe788d9c4",
                                    "contractNumber": "11-0216111-2-0",
                                    "contractStartDate": "30.03.2020",
                                    "totalDebt": 7000.00,
                                    "currentDebt": 300.00,
                                    "currency": "BYN",
                                    "repaymentDate": "16.01.2020",
                                    "rate": 55.5,
                                    "iban": "DE89370400440532013000234567",
                                    "possibleRepayment": false,
                                    "isClosed": false,
                                    "customer_type": "LEGAL"
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
    List<CreditResponse> getByCustomerId(@PathVariable UUID customerId);

}
