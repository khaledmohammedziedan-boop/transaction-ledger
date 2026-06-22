package rs2.com.transaction_ledger.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "TransactionResponseDto", description = "Represents a transaction returned by the API")
public record TransactionResponseDto(
        @Schema(description = "Monetary amount of the transaction", example = "150.75")
        BigDecimal amount,
        @Schema(description = "Currency code in ISO 4217 format", example = "EUR")
        String currency,
        @Schema(description = "Free-text description of the transaction", example = "Grocery purchase")
        String description,
        @Schema(description = "Counterparty IBAN account number", example = "DE44500105175407324931")
        String counterPartyIban,
        @Schema(description = "Timestamp when the transaction was created", example = "2026-03-30T09:15:23.456")
        LocalDateTime createdAt) {
}
