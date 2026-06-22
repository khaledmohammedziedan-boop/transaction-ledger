package rs2.com.transaction_ledger.helper;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(name = "PageResponseDto", description = "Generic page wrapper for paginated responses")
public record PageResponseDto<T>(
        @Schema(description = "Page content elements")
        List<T> content,
        @Schema(description = "Current page index (zero-based)", example = "0")
        int page,
        @Schema(description = "Size of the page (number of elements)", example = "20")
        int size,
        @Schema(description = "Total number of elements across all pages", example = "125")
        long totalElements,
        @Schema(description = "Total number of pages", example = "7")
        int totalPages,
        @Schema(description = "Indicator whether this page is the last one", example = "true")
        boolean last) {
}
