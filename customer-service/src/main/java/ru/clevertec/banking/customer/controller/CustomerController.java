package ru.clevertec.banking.customer.controller;

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
import org.springframework.data.domain.Page;
import ru.clevertec.banking.advice.model.ApiError;
import ru.clevertec.banking.customer.dto.request.CreateCustomerRequest;
import ru.clevertec.banking.customer.dto.request.GetCustomersPageableRequest;
import ru.clevertec.banking.customer.dto.response.CustomerBankingProductsResponse;
import ru.clevertec.banking.customer.dto.response.CustomerResponse;

import java.util.UUID;


@Tag(name = "Customer", description = "The Customer Api")
@SecurityRequirement(name = "Bearer Authentication")
public interface CustomerController {

    @Operation(summary = "Find all Customers with pagination.", tags = "Customer",
               security = @SecurityRequirement(name = "Bearer Authentication"),
               parameters = {
                       @Parameter(name = "page", description = "Enter your page number here", example = "0"),
                       @Parameter(name = "size", description = "Enter your page size here", example = "5"),
                       @Parameter(name = "registerDate", description = "Enter your register date here", example = "10.01.2024"),
                       @Parameter(name = "customerType", description = "Enter your customer type here", example = "PHYSIC")
               })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Page of Customers retrieved successfully",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = CustomerResponse.class), examples = @ExampleObject("""
                         {
                              "content": [
                                  {
                                      "unp": "16412",
                                      "email": "example6@email.com",
                                      "phoneCode": "37529",
                                      "phoneNumber": "1112233",
                                      "customerFullname": "Ivanov Ivan Ivanovich",
                                      "customer_id": "4f92a65f-4b8f-40f5-a889-1ebc6d9dc987",
                                      "customer_type": "LEGAL",
                                      "register_date": "2024-01-10"
                                  },
                                  {
                                      "unp": "15412",
                                      "email": "example2@email.com",
                                      "phoneCode": "37529",
                                      "phoneNumber": "1112233",
                                      "customerFullname": "Ivanov Ivan Ivanovich",
                                      "customer_id": "4f92a65d-4b8f-40f5-a889-1ebc6d9dc987",
                                      "customer_type": "LEGAL",
                                      "register_date": "2024-01-10"
                                  },
                                  {
                                      "unp": "21412",
                                      "email": "example@email.com",
                                      "phoneCode": "37529",
                                      "phoneNumber": "1112233",
                                      "customerFullname": "Ivanov Ivan Ivanovich",
                                      "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                      "customer_type": "LEGAL",
                                      "register_date": "2024-01-15"
                                  }
                              ],
                              "pageable": {
                                  "pageNumber": 0,
                                  "pageSize": 3,
                                  "sort": [],
                                  "offset": 0,
                                  "unpaged": false,
                                  "paged": true
                              },
                              "last": false,
                              "totalPages": 2,
                              "totalElements": 4,
                              "size": 3,
                              "number": 0,
                              "sort": [],
                              "first": true,
                              "numberOfElements": 3,
                              "empty": false
                          }
                            """))),
            @ApiResponse(responseCode = "400", description = "Not valid request data provided",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 400,
                              "message": "Not valid request data provided"
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Not Authenticated User",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 401,
                              "message": "Authentication is required to access this resource"
                            }
                            """)))
    })
    Page<CustomerResponse> getCustomersPageable(GetCustomersPageableRequest getCustomersPageableRequest);

    @Operation(summary = "Find Customer by provided id", tags = "Customer",
               security = @SecurityRequirement(name = "Bearer Authentication"),
               parameters = @Parameter(name = "id", description = "Enter uuid here", example = "4f92a75f-4b8f-40f5-a889-1ebc6d9dc987"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer retrieved successfully",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = CustomerResponse.class), examples = @ExampleObject("""
                         {
                             "unp": "16412",
                             "email": "example6@email.com",
                             "phoneCode": "37529",
                             "phoneNumber": "1112233",
                             "customerFullname": "Иванов Иван Иванович",
                             "customer_id": "4f92a65f-4b8f-40f5-a889-1ebc6d9dc987",
                             "customer_type": "LEGAL",
                             "register_date": "2024-01-10"
                         }
                            """))),
            @ApiResponse(responseCode = "400", description = "Not valid uuid provided",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 400,
                              "message": "Not valid uuid provided"
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Not Authenticated User",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 401,
                              "message": "Authentication is required to access this resource"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 403,
                              "message": "Access denied"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "No Customer with this id in database",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                             {
                                 "status": 404,
                                 "message": "Customer with id 4f92a65f-4b8f-40f5-a889-0001ebc6d9dc not found"
                             }
                            """)))
    })
    CustomerResponse getCustomerById(UUID id);

    @Operation(summary = "Find Customer by provided unp", tags = "Customer",
               security = @SecurityRequirement(name = "Bearer Authentication"),
               parameters = @Parameter(name = "unp", description = "Enter unp here", example = "100220190"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer retrieved successfully",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = CustomerResponse.class), examples = @ExampleObject("""
                         {
                             "unp": "100220190",
                             "email": "example6@email.com",
                             "phoneCode": "37529",
                             "phoneNumber": "1112233",
                             "customerFullname": "Иванов Иван Иванович",
                             "customer_id": "4f92a65f-4b8f-40f5-a889-1ebc6d9dc987",
                             "customer_type": "LEGAL",
                             "register_date": "2024-01-10"
                         }
                            """))),
            @ApiResponse(responseCode = "400", description = "Not valid request data provided",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 400,
                              "message": "Not valid unp provided"
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Not Authenticated User",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 401,
                              "message": "Authentication is required to access this resource"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 403,
                              "message": "Access denied"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "No Customer with this id in database",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                             {
                                 "status": 404,
                                 "message": "Customer with unp 100220190 not found"
                             }
                            """)))
    })
    CustomerResponse getCustomerByUnp(String unp);

    @Operation(summary = "Find All Customer banking products by uuid", tags = "Customer",
               security = @SecurityRequirement(name = "Bearer Authentication"),
               parameters = @Parameter(name = "uuid", description = "Enter uuid here", example = "4f92a75f-4b8f-40f5-a889-1ebc6d9dc987"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Customer banking products retrieved successfully",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = CustomerBankingProductsResponse.class), examples = @ExampleObject("""
                         {
                           "accountsWithCardsResponse": [
                             {
                               "name": "Test",
                               "iban": "AABBCCCDDDDEEEEEEEEEEEEEEEE",
                               "iban_readable": "AABB CCC DDDD EEEE EEEE EEEE EEEE",
                               "amount": 2100.00,
                               "currency_code": "933",
                               "open_date": "20.01.2024",
                               "main_acc": true,
                               "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                               "customer_type" : "LEGAL",
                               "rate": 0.01,
                               "cards": [
                                 {
                                   "card_number": "5200000000001096",
                                   "card_number_readable": "5200 0000 0000 1096",
                                   "iban": "AABBCCCDDDDEEEEEEEEEEEEEEEE",
                                   "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                   "customer_type" : "LEGAL",
                                   "cardholder": "CARDHOLDER NAME",
                                   "card_status": "ACTIVE"
                                 },
                                 {
                                   "card_number": "5211111111111096",
                                   "card_number_readable": "5211 1111 1111 1096",
                                   "iban": "AABBCCCDDDDEEEEEEEEEEEEEEEE",
                                   "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                                   "customer_type" : "PHYSIC",
                                   "cardholder": "CARDHOLDER NAME",
                                   "card_status": "NEW"
                                 }
                               ]
                             }
                           ],
                           "creditsResponse": [
                             {
                               "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                               "contractNumber": "11-0216444-2-0",
                               "contractStartDate": "30.03.2022",
                               "totalDebt": 8113.99,
                               "currentDebt": 361.99,
                               "currency": "BYN",
                               "repaymentDate": "16.01.2023",
                               "rate": 22.8,
                               "iban": "AABBCCCDDDDEEEEEEEEEEEEEEEE",
                               "possibleRepayment": true,
                               "isClosed": false,
                               "customer_type" : "LEGAL"
                             }
                           ],
                           "depositsResponse": [
                             {
                               "id": 1,
                               "customer_id": "1a72a05f-4b8f-43c5-a889-1ebc6d9dc729",
                               "customer_type": "LEGAL",
                               "acc_info": {
                                 "acc_iban": "AABBCCCDDDDEEEEEEEEEEEEEEEE",
                                 "acc_open_date": "01.01.2022",
                                 "curr_amount": 1500.00,
                                 "curr_amount_currency": "USD"
                               },
                               "dep_info": {
                                 "rate": 0.02,
                                 "term_val": 12,
                                 "term_scale": "M",
                                 "exp_date": "01.01.2023",
                                 "dep_type": "REVOCABLE",
                                 "auto_renew": true
                               }
                             }
                           ]
                         }
                            """))),
            @ApiResponse(responseCode = "400", description = "Not valid customer uuid provided",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 400,
                              "message": "Not valid customer uuid provided"
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Not Authenticated User",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 401,
                              "message": "Authentication is required to access this resource"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "Access denied",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 403,
                              "message": "Access denied"
                            }
                            """))),
            @ApiResponse(responseCode = "500", description = "One of remote services didn't respond",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                             {
                                 "status": 500,
                                 "message": "Feign exception: SERVICE-NAME executing GET http://SERVICE-NAME/api/v1/... cause: feign.RetryableException "
                             }
                            """)))
    })
    CustomerBankingProductsResponse getCustomerBankingProducts(UUID id);

    @Operation(summary = "Save new Customer.", tags = "Customer",
               security = @SecurityRequirement(name = "Bearer Authentication"),
               requestBody = @RequestBody(description = "RequestBody for CustomerRequest",
                                          content = @Content(schema = @Schema(implementation = CreateCustomerRequest.class),
                                                             examples = @ExampleObject("""
                                   {
                                       "customer_id": "9f92a65d-4b3f-40f5-a889-1efc6d9dc987",
                                       "customer_type" : "LEGAL",
                                       "unp" : "97814",
                                       "register_date": "16.01.2024",
                                       "email": "vlad9@email.com",
                                       "phoneCode": "37529",
                                       "phoneNumber": "1112233",
                                       "customer_fullname": "Ivanov Ivanovich Ivan"
                                   }
                                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Customer saved successfully",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = CustomerResponse.class), examples = @ExampleObject("""
                        {
                           "customer_id": "9f92a65d-4b3f-40f5-a889-1efc6d9dc987",
                           "customer_type" : "LEGAL",
                           "unp" : "97814",
                           "register_date": "16.01.2024",
                           "email": "vlad9@email.com",
                           "phoneCode": "37529",
                           "phoneNumber": "1112233",
                           "customer_fullname": "Ivanov Ivanovich Ivan"
                        }
                            """))),
            @ApiResponse(responseCode = "400", description = """
                                                             Not valid customer data provided for creation
                                                             Customer id must be uuid, customer type must be LEGAL or PHYSICAL 
                                                             email must be valid
                                                             phone code with phone number must be valid phone number""",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 400,
                              "message": "Not valid customer uuid provided"
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Not Authenticated User",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 401,
                              "message": "Authentication is required to access this resource"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "Access denied: only Admin or SuperUser can create customer via this endpoint. Also you can create customer via RabbitMQ",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 403,
                              "message": "Access denied"
                            }
                            """))),
            @ApiResponse(responseCode = "500", description = "Connection refused: RabbitMQ service didn't respond. Must run to register customer account",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 500,
                              "message": "Connection refused: RabbitMQ service didn't respond"
                            }
                            """)))
    })
    CustomerResponse createCustomer(CreateCustomerRequest createCustomerRequest);

    @Operation(summary = "Delete Customer by uuid.", tags = "Customer",
               parameters = @Parameter(name = "uuid", description = "Enter uuid here", example = "4f92a65f-4b8f-40f5-a889-1ebc6d9dc"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Customer deleted successfully",
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
    void deleteCustomer(UUID id);
}
