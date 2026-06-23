package rs2.com.transaction_ledger.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rs2.com.transaction_ledger.config.SecurityProperties;
import rs2.com.transaction_ledger.dtos.LoginRequestDTO;
import rs2.com.transaction_ledger.dtos.LoginResponseDTO;
import rs2.com.transaction_ledger.dtos.UserRegistrationDto;
import rs2.com.transaction_ledger.dtos.UserResponseDto;
import rs2.com.transaction_ledger.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Register users, authenticate credentials, and retrieve the current user's profile.")
public class UserController {

    private final UserService userService;
    private final SecurityProperties securityProperties;

    @PostMapping("/register")
    @Operation(
            summary = "Register user",
            description = "Creates a user account using the supplied profile and password details.",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User registered."),
                    @ApiResponse(responseCode = "400", description = "Invalid registration request.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "Registration could not be completed.", content = @Content)
            }
    )
    public ResponseEntity<String> registerUser(@RequestBody UserRegistrationDto userRegistrationDto) {
        try {
            userService.registerUser(userRegistrationDto);
            return ResponseEntity.status(HttpStatus.CREATED).
                    body("Given user details are successfully registered");

        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).
                    body("An exception occurred: " + ex.getMessage());
        }
    }

    @GetMapping("/user")
    @Operation(
            summary = "Get current user",
            description = "Returns profile information for the authenticated user represented by the JWT.",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Current user profile returned.",
                            content = @Content(schema = @Schema(implementation = UserResponseDto.class))),
                    @ApiResponse(responseCode = "401", description = "Authentication is required.", content = @Content)
            }
    )
    public UserResponseDto getUserDetailsAfterLogin(Authentication authentication) {
        return userService.loadUserByUsername(authentication.getName());
    }

    @PostMapping("/apiLogin")
    @Operation(
            summary = "Log in",
            description = "Authenticates user credentials and returns a JWT in both the response body and configured authorization header.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authentication succeeded.",
                            content = @Content(schema = @Schema(implementation = LoginResponseDTO.class))),
                    @ApiResponse(responseCode = "401", description = "Credentials are invalid.", content = @Content)
            }
    )
    public ResponseEntity<LoginResponseDTO> apiLogin(@RequestBody LoginRequestDTO loginRequest) {
        LoginResponseDTO response = userService.apiLogin(loginRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .header(securityProperties.jwtHeader(), response.jwtToken())
                .body(response);
    }
}
