package rs2.com.transaction_ledger.dtos;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SortDto", description = "Sort specification for pagination")
public record SortDto(
        @Schema(description = "Field name to sort by", example = "createdAt")
        String field,
        @Schema(description = "Sort direction (ASC or DESC)", example = "DESC")
        Direction direction
) {}
