package ru.clevertec.banking.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import ru.clevertec.banking.advice.model.ApiError;
import ru.clevertec.banking.auth.dto.request.AuthenticationRequest;
import ru.clevertec.banking.auth.dto.request.RefreshTokenRequest;
import ru.clevertec.banking.auth.dto.response.AuthenticationResponse;

public interface AuthenticationController {

    @Operation(summary = "Authenticate user and provide tokens.", tags = "Authentication",
               requestBody = @RequestBody(description = "RequestBody for AuthenticationRequest",
                                          content = @Content(schema = @Schema(implementation = AuthenticationRequest.class),
                                                             examples = @ExampleObject("""
                                   {
                                       "email" : "test1@example.com",
                                       "password" : "password1"
                                   }
                                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = AuthenticationResponse.class), examples = @ExampleObject("""
                           {
                               "token": "eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6IjFhNzJhMDVmLTRiOGYtNDNjNS1hODg5LTFlYmM2ZDlkYzcyOSIsImlhdCI6MTcwNTk2NzYyMiwiZXhwIjoxNzA2NTcyNDIyfQ.pw08Oluc4ZxYqlWaaTu9DPWOc51rIC9aUILwWrhD71E",
                               "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6IjFhNzJhMDVmLTRiOGYtNDNjNS1hODg5LTFlYmM2ZDlkYzcyOSIsImlhdCI6MTcwNTk2NzYyMiwiZXhwIjoxNzA2NTcyNDIyfQ.pw08Oluc4ZxYqlWaaTu9DPWOc51rIC9aUILwWrhD71E"
                           }
                            """))),
            @ApiResponse(responseCode = "400", description = "No request body were provided",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                            {
                              "status": 400,
                              "message": "Required request body is missing"
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "Not Correct credentials provided",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                         {
                             "status": 401,
                             "message": "Bad credentials"
                         }
                            """))),
            @ApiResponse(responseCode = "404", description = "Not found user with provided credentials",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                         {
                             "status": 404,
                             "message": "User with email: test11@example.com not found"
                         }
                            """)))
    })
    AuthenticationResponse authenticate(AuthenticationRequest request);

    @Operation(summary = "Refresh user tokens and provide new.", tags = "Authentication",
               requestBody = @RequestBody(description = "RequestBody for AuthenticationRequest",
                                          content = @Content(schema = @Schema(implementation = RefreshTokenRequest.class),
                                                             examples = @ExampleObject("""
                                   {
                                       "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJTVVBFUl9VU0VSIl0sInN1YiI6ImE0NDlkMDkyLTkzYzUtNGI5OC1iNWRkLTM3Y2I5MTM3NmRkMyIsImlhdCI6MTcwNTQ0MzA2MCwiZXhwIjoxNzA2MDQ3ODYwfQ.pU3Ng-2l51-2VHC1FbkbPthaMHxNHykYlSacl3-WzWA"
                                   }
                                    """))))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User refreshed tokens successfully",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = AuthenticationResponse.class), examples = @ExampleObject("""
                           {
                               "token": "eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6IjNhNzJhMDVmLTRiOGYtNDNjNS1hODg5LTFlYmM2ZDlkYzcyOSIsImlhdCI6MTcwNTk2ODE1NiwiZXhwIjoxNzA2NTcyOTU2fQ.idOqnsWULC9r6JBe1NhMO3WLcTzp1G3rFNT5c20s48U",
                               "refreshToken": "eyJhbGciOiJIUzI1NiJ9.eyJhdXRob3JpdGllcyI6WyJVU0VSIl0sInN1YiI6IjNhNzJhMDVmLTRiOGYtNDNjNS1hODg5LTFlYmM2ZDlkYzcyOSIsImlhdCI6MTcwNTk2ODE1NiwiZXhwIjoxNzA2NTcyOTU2fQ.idOqnsWULC9r6JBe1NhMO3WLcTzp1G3rFNT5c20s48U"
                           }
                            """))),
            @ApiResponse(responseCode = "400", description = "Incorrect refresh was provided",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                         {
                             "status": 400,
                             "message": "Refresh token incorrect"
                         }
                            """))),
            @ApiResponse(responseCode = "403", description = "Expired refresh token",
                         content = @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ApiError.class), examples = @ExampleObject("""
                         {
                             "status": 403,
                             "message": "JWT expired at 2024-01-22T19:18:07Z. Current time: 2024-01-23T00:00:58Z"
                         }
                            """)))
    })
    AuthenticationResponse refresh(RefreshTokenRequest refreshTokenRequest);
}
