package rs2.com.transaction_ledger.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(name = "CreateTransactionDto", description = "Request payload for creating a new transaction")
public record CreateTransactionDto(
        @Schema(description = "Monetary amount of the transaction", example = "150.75")
        BigDecimal amount,
        @Schema(description = "Currency code in ISO 4217 format", example = "EUR")
        String currency,
        @Schema(description = "Free-text description of the transaction", example = "Grocery purchase")
        String description,
        @Schema(description = "Counterparty IBAN account number", example = "DE44500105175407324931")
        String counterPartyIban) {
}
