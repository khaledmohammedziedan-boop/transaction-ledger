package rs2.com.transaction_ledger.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import rs2.com.transaction_ledger.dtos.CreateTransactionDto;
import rs2.com.transaction_ledger.dtos.TransactionResponseDto;
import rs2.com.transaction_ledger.helper.PageResponseDto;
import rs2.com.transaction_ledger.helper.QueryParameterParser;
import rs2.com.transaction_ledger.service.TransactionService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transaction")
@Tag(name = "Transactions", description = "Create transactions and retrieve the authenticated user's transaction history.")
public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping()
    @Operation(
            summary = "Create transaction",
            description = "Creates a transaction owned by the authenticated user. The counterparty IBAN is validated before the transaction is saved.",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Transaction created.",
                            content = @Content(schema = @Schema(implementation = TransactionResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid request or business validation failure.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Authentication is required.", content = @Content)
            }
    )
    public ResponseEntity<TransactionResponseDto> createTransaction(@Valid @RequestBody CreateTransactionDto createTransactionDto, Authentication authentication) {
        return new ResponseEntity<>(transactionService.createTransaction(createTransactionDto, authentication), HttpStatus.CREATED);
    }

    @GetMapping("/getTransactionsPage")
    @Operation(
            summary = "List transactions",
            description = "Returns a paginated list of transactions owned by the authenticated user. " +
                    "Use filter=field,operator,value for filtering, for example filter=amount,GT,100. " +
                    "Use sort=field,direction for sorting, for example sort=createdAt,DESC. " +
                    "Multiple filter and sort parameters are supported.",
            security = @SecurityRequirement(name = "bearer-jwt"),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transaction page returned.",
                            content = @Content(schema = @Schema(implementation = PageResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid pagination, filter, or sort parameter.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Authentication is required.", content = @Content)
            }
    )
    @Parameters({
            @Parameter(name = "page", description = "Zero-based page index", example = "0", required = false),
            @Parameter(name = "size", description = "Number of transactions to return per page.", example = "20", required = false),
            @Parameter(name = "sort", description = "Sort expression in the format field,direction. Supported directions are ASC and DESC. Multiple sort parameters may be provided.", example = "createdAt,DESC", required = false),
            @Parameter(name = "filter", description = "Filter expression in the format field,operator,value. Supported operators are EQ, NE, GT, GTE, LT, LTE, LIKE, and IN. Multiple filter parameters may be provided.", example = "amount,GT,100", required = false)
    })
    public ResponseEntity<PageResponseDto<TransactionResponseDto>> getTransactionsPage(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "20") @Min(1) int size,
            @RequestParam(required = false) String[] sort,
            @RequestParam(required = false) String[] filter,
            Authentication authentication) {
        var pageRequest = QueryParameterParser.parseQueryParameters(page, size, sort, filter);
        return new ResponseEntity<>(transactionService.getTransactionsPage(pageRequest, authentication), HttpStatus.OK);
    }

}
