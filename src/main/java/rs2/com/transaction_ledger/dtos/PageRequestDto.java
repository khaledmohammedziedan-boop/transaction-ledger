package rs2.com.transaction_ledger.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Schema(name = "PageRequestDto", description = "Pagination request with filtering and sorting")
public record PageRequestDto(
        @NotNull
        @Min(0)
        @Schema(description = "Zero-based page index", example = "0")
        int page,
        @NotNull
        @Min(1)
        @Schema(description = "Page size (number of elements per page)", example = "20")
        int size,
        @Schema(description = "List of sort specifications")
        List<SortDto> sorts,
        @Schema(description = "List of filter specifications")
        List<FilterDto> filters
) {}
