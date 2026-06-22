package rs2.com.transaction_ledger.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import rs2.com.transaction_ledger.dtos.*;
import rs2.com.transaction_ledger.exception.BusinessException;
import rs2.com.transaction_ledger.helper.PageResponseDto;
import rs2.com.transaction_ledger.model.Transaction;
import rs2.com.transaction_ledger.model.User;
import rs2.com.transaction_ledger.repo.TransactionRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service("TransactionService")
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    private final UserService userService;

    public TransactionResponseDto createTransaction(CreateTransactionDto createTransactionDto, Authentication authentication) {

        if (!(createTransactionDto.amount().compareTo(BigDecimal.ZERO) > 0)) {
            throw new BusinessException("transactionAmountLessThanZero", "Amount must be greater than zero!", HttpStatus.BAD_REQUEST.value());
        }
        User user = userService.getUserByUsername(authentication.getName());
        Transaction transaction = mapToTransaction(createTransactionDto);
        transaction.setUser(user);
        transaction = transactionRepository.save(transaction); // persist the instance with user set
        return mapToResponseDto(transaction);
    }

    private Transaction mapToTransaction(CreateTransactionDto dto) {
        Transaction transaction = new Transaction();
        transaction.setAmount(dto.amount());
        transaction.setCurrency(dto.currency());
        transaction.setDescription(dto.description());
        transaction.setCounterPartyIban(dto.counterPartyIban());
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

    public PageResponseDto<TransactionResponseDto> getTransactionsPage(PageRequestDto request) {
        Sort sort = buildSort(request.sorts());
        Pageable pageable = PageRequest.of(request.page(), request.size(), sort);

        // Start with a spec that restricts to the currently authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new BusinessException("unauthenticated", "User must be authenticated", HttpStatus.UNAUTHORIZED.value());
        }
        String username = auth.getName();

        Specification<Transaction> spec = (root, query, cb) -> cb.equal(root.get("user").get("email"), username);

        if (request.filters() != null) {
            for (FilterDto filter : request.filters()) {
                spec = spec.and(buildSpecification(filter));
            }
        }

        Page<Transaction> page = transactionRepository.findAll(spec, pageable);
        List<TransactionResponseDto> content = new ArrayList<>();
        for (Transaction tx : page.getContent()) {
            content.add(mapToResponseDto(tx));
        }
        return new PageResponseDto<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    private Sort buildSort(List<SortDto> sorts) {
        if (sorts == null || sorts.isEmpty()) {
            return Sort.unsorted();
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (SortDto sortDto : sorts) {
            Sort.Direction direction = sortDto.direction() == Direction.DESC
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
            orders.add(new Sort.Order(direction, sortDto.field()));
        }
        return Sort.by(orders);
    }

    private Specification<Transaction> buildSpecification(FilterDto filter) {
        return (root, query, cb) -> {
            String field = filter.field();
            Operator op = filter.operator();
            String value = filter.value();

            Object typedValue;
            switch (field) {
                case "amount" -> typedValue = new BigDecimal(value);
                case "createdAt" -> {
                    Instant instant = Instant.parse(value);
                    typedValue = LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
                }
                default -> typedValue = value;
            }

            return switch (op) {
                case EQ -> cb.equal(root.get(field), typedValue);
                case NE -> cb.notEqual(root.get(field), typedValue);
                case GT -> cb.greaterThan(root.get(field), (Comparable) typedValue);
                case GTE -> cb.greaterThanOrEqualTo(root.get(field), (Comparable) typedValue);
                case LT -> cb.lessThan(root.get(field), (Comparable) typedValue);
                case LTE -> cb.lessThanOrEqualTo(root.get(field), (Comparable) typedValue);
                case LIKE -> cb.like(cb.lower(root.get(field)), "%" + value.toLowerCase() + "%");
                case IN -> {
                    // Simple IN support with comma-separated values
                    String[] parts = value.split(",");
                    CriteriaBuilder.In<Object> inClause = cb.in(root.get(field));
                    for (String part : parts) {
                        inClause.value(part.trim());
                    }
                    yield inClause;
                }
            };
        };
    }
}
