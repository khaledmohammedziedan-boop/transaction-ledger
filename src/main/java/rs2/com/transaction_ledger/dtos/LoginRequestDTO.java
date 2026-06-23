package rs2.com.transaction_ledger.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginRequestDTO", description = "Request payload for authenticating a user")
public record LoginRequestDTO(
        @Schema(description = "Username or email used for login", example = "kmsaleh290@gmail.com")
        String username,
        @Schema(description = "Plain text password for authentication", example = "P@ssw*rd!")
        String password) {
}
