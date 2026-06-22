package rs2.com.transaction_ledger.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "FilterDto", description = "Filter specification for pagination")
public record FilterDto(
        @Schema(description = "Field name to filter on", example = "amount")
        String field,
        @Schema(description = "Filter operator (EQ, NE, GT, GTE, LT, LTE, LIKE, IN)", example = "GT")
        Operator operator,
        @Schema(description = "Filter value", example = "100")
        String value
) {}
