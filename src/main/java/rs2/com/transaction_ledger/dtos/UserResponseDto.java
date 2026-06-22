package rs2.com.transaction_ledger.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(name = "UserResponseDto", description = "Basic information about a user returned by the API")
public record UserResponseDto(
        @Schema(description = "Unique identifier of the user", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,
        @Schema(description = "Full name of the user", example = "Anas Momani")
        String name,
        @Schema(description = "Email address of the user", example = "anasmomani@gmail.com")
        String email,
        @Schema(description = "Mobile phone number of the user", example = "+35612345678")
        String mobileNumber) {
}
