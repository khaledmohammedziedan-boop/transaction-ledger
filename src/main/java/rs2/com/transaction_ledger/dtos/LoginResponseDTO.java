package rs2.com.transaction_ledger.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LoginResponseDTO", description = "Response payload returned after successful authentication")
public record LoginResponseDTO(
        @Schema(description = "Status message of the authentication attempt", example = "OK")
        String status,
        @Schema(description = "JWT token that should be used for subsequent authenticated requests")
        String jwtToken) {
}
