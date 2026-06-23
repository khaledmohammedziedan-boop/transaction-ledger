package rs2.com.transaction_ledger.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

@Schema(name = "CreateTransactionDto", description = "Request payload for creating a new transaction")
public record CreateTransactionDto(
        @Schema(description = "Monetary amount of the transaction", example = "150.75")
        @NotNull
        @Positive
        BigDecimal amount,
        @Schema(description = "Currency code in ISO 4217 format", example = "EUR")
        @NotBlank
        String currency,
        @Schema(description = "Free-text description of the transaction", example = "Grocery purchase")
        String description,
        @Schema(description = "Counterparty IBAN account number", example = "DE44500105175407324931")
        @NotBlank
        @Size(min = 15, max = 34)
        String counterPartyIban) {
}
