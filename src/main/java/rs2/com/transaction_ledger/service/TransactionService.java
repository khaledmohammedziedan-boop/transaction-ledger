package rs2.com.transaction_ledger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import rs2.com.transaction_ledger.dtos.CreateTransactionDto;
import rs2.com.transaction_ledger.dtos.PageRequestDto;
import rs2.com.transaction_ledger.dtos.TransactionResponseDto;
import rs2.com.transaction_ledger.exception.BusinessException;
import rs2.com.transaction_ledger.helper.PageResponseDto;
import rs2.com.transaction_ledger.model.Transaction;
import rs2.com.transaction_ledger.model.User;
import rs2.com.transaction_ledger.repo.TransactionQueryRepository;
import rs2.com.transaction_ledger.repo.TransactionRepository;

import java.math.BigDecimal;

@Service("TransactionService")
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionQueryRepository transactionQueryRepository;
    private final UserService userService;
    private final IbanValidator ibanValidator;

    public TransactionResponseDto createTransaction(CreateTransactionDto createTransactionDto, Authentication authentication) {
        validateTransactionRequest(createTransactionDto);

        User user = getAuthenticatedUser(authentication);
        Transaction transaction = mapToTransaction(createTransactionDto);
        transaction.setUser(user);
        transaction = transactionRepository.save(transaction);
        return mapToResponseDto(transaction);
    }

    public PageResponseDto<TransactionResponseDto> getTransactionsPage(PageRequestDto request, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return transactionQueryRepository.findPageForUser(user.getId(), request.withSafeDefaults());
    }

    private void validateTransactionRequest(CreateTransactionDto createTransactionDto) {
        if (createTransactionDto.amount() == null || createTransactionDto.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("transactionAmountLessThanZero", "Amount must be greater than zero!", HttpStatus.BAD_REQUEST.value());
        }

        if (!ibanValidator.isValid(createTransactionDto.counterPartyIban())) {
            throw new BusinessException("invalidCounterPartyIban", "Counterparty IBAN is invalid", HttpStatus.BAD_REQUEST.value());
        }
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("unauthenticated", "User must be authenticated", HttpStatus.UNAUTHORIZED.value());
        }
        return userService.getUserByUsername(authentication.getName());
    }

    private Transaction mapToTransaction(CreateTransactionDto dto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.amount());
        transaction.setCurrency(dto.currency());
        transaction.setDescription(dto.description());
        transaction.setCounterPartyIban(ibanValidator.normalize(dto.counterPartyIban()));
        return transaction;
    }

    private TransactionResponseDto mapToResponseDto(Transaction transaction) {
        return new TransactionResponseDto(
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getDescription(),
                transaction.getCounterPartyIban(),
                transaction.getCreatedAt()
        );
    }
}
