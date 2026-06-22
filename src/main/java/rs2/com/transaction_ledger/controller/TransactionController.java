package rs2.com.transaction_ledger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rs2.com.transaction_ledger.dtos.CreateTransactionDto;
import rs2.com.transaction_ledger.dtos.TransactionResponseDto;
import rs2.com.transaction_ledger.helper.PageResponseDto;
import rs2.com.transaction_ledger.service.TransactionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
@Tag(name = "Transactions", description = "Endpoints for creating and querying transactions")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping()
    @Operation(
            summary = "Create a new transaction",
            description = "Creates a new transaction for the currently authenticated user.",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Transaction created successfully",
                            content = @Content(schema = @Schema(implementation = TransactionResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation or business error", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            }
    )
    public ResponseEntity<TransactionResponseDto> createTransaction(@RequestBody CreateTransactionDto createTransactionDto, Authentication authentication) {
        return new ResponseEntity<>(transactionService.createTransaction(createTransactionDto, authentication), HttpStatus.CREATED);
    }

    @GetMapping("/getTransactionsPage")
    @Operation(
            summary = "Get paginated list of transactions",
            description = "Returns a page of transactions filtered and sorted according to the given criteria. " +
                    "Supports filtering with: ?filter=field,operator,value (e.g., ?filter=amount,GT,100) " +
                    "and sorting with: ?sort=field,direction (e.g., ?sort=createdAt,DESC). " +
                    "Multiple filters and sorts can be applied.",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transactions page returned successfully",
                            content = @Content(schema = @Schema(implementation = PageResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Validation error", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content)
            }
    )
    @Parameters({
            @Parameter(name = "page", description = "Zero-based page index", example = "0", required = false),
            @Parameter(name = "size", description = "Page size (number of elements per page)", example = "20", required = false),
            @Parameter(name = "sort", description = "Sort specifications in format field,direction (e.g., createdAt,DESC). " +
                    "Direction can be ASC or DESC. Multiple sort parameters can be provided.", example = "createdAt,DESC", required = false),
            @Parameter(name = "filter", description = "Filter specifications in format field,operator,value (e.g., amount,GT,100). " +
                    "Operators: EQ, NE, GT, GTE, LT, LTE, LIKE, IN. Multiple filter parameters can be provided.", example = "amount,GT,100", required = false)
    })
    public ResponseEntity<PageResponseDto<TransactionResponseDto>> getTransactionsPage(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] filter,
            Authentication authentication) {
        var pageRequest = QueryParameterParser.parseQueryParameters(page, size, sort, filter);
        return new ResponseEntity<>(transactionService.getTransactionsPage(pageRequest), HttpStatus.OK);
    }

}
