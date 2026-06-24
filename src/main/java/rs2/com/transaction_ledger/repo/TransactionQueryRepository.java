package rs2.com.transaction_ledger.repo;

import lombok.RequiredArgsConstructor;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.SortField;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import rs2.com.transaction_ledger.dtos.Direction;
import rs2.com.transaction_ledger.dtos.FilterDto;
import rs2.com.transaction_ledger.dtos.Operator;
import rs2.com.transaction_ledger.dtos.PageRequestDto;
import rs2.com.transaction_ledger.dtos.SortDto;
import rs2.com.transaction_ledger.dtos.TransactionResponseDto;
import rs2.com.transaction_ledger.exception.BusinessException;
import rs2.com.transaction_ledger.helper.PageResponseDto;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static rs2.com.transaction_ledger.jooq.generated.Tables.TRANSACTIONS;

@Repository
@RequiredArgsConstructor
public class TransactionQueryRepository {

    private static final Map<String, Field<?>> ALLOWED_FIELDS = Map.of(
            "amount", TRANSACTIONS.AMOUNT,
            "currency", TRANSACTIONS.CURRENCY,
            "description", TRANSACTIONS.DESCRIPTION,
            "counterPartyIban", TRANSACTIONS.COUNTER_PARTY_IBAN,
            "createdAt", TRANSACTIONS.CREATED_AT
    );

    private final DSLContext dsl;

    public PageResponseDto<TransactionResponseDto> findPageForUser(UUID userId, PageRequestDto request) {
        Condition condition = TRANSACTIONS.USER_ID.eq(userId);

        for (FilterDto filter : request.filters()) {
            condition = condition.and(buildCondition(filter));
        }

        int totalElements = dsl.selectCount()
                .from(TRANSACTIONS)
                .where(condition)
                .fetchOne(0, int.class);

        List<TransactionResponseDto> content = dsl.select(
                        TRANSACTIONS.AMOUNT,
                        TRANSACTIONS.CURRENCY,
                        TRANSACTIONS.DESCRIPTION,
                        TRANSACTIONS.COUNTER_PARTY_IBAN,
                        TRANSACTIONS.CREATED_AT)
                .from(TRANSACTIONS)
                .where(condition)
                .orderBy(buildSortFields(request.sorts()))
                .limit(request.size())
                .offset((long) request.page() * request.size())
                .fetch(record -> new TransactionResponseDto(
                        record.get(TRANSACTIONS.AMOUNT),
                        record.get(TRANSACTIONS.CURRENCY),
                        record.get(TRANSACTIONS.DESCRIPTION),
                        record.get(TRANSACTIONS.COUNTER_PARTY_IBAN),
                        record.get(TRANSACTIONS.CREATED_AT)
                ));

        int totalPages = totalElements == 0 ? 0 : (int) Math.ceil((double) totalElements / request.size());
        boolean last = request.page() + 1 >= totalPages;

        return new PageResponseDto<>(
                content,
                request.page(),
                request.size(),
                totalElements,
                totalPages,
                last
        );
    }

    private List<SortField<?>> buildSortFields(List<SortDto> sorts) {
        List<SortField<?>> sortFields = new ArrayList<>();

        for (SortDto sort : sorts) {
            Field<?> field = allowedField(sort.field());
            sortFields.add(sort.direction() == Direction.DESC ? field.desc() : field.asc());
        }

        return sortFields;
    }

    private Condition buildCondition(FilterDto filter) {
        Field<?> field = allowedField(filter.field());
        Object value = parseValue(field, filter.value());

        return switch (filter.operator()) {
            case EQ -> equal(field, value);
            case NE -> notEqual(field, value);
            case GT -> comparableField(field).gt((Comparable<?>) value);
            case GTE -> comparableField(field).ge((Comparable<?>) value);
            case LT -> comparableField(field).lt((Comparable<?>) value);
            case LTE -> comparableField(field).le((Comparable<?>) value);
            case LIKE -> stringField(field).likeIgnoreCase("%" + filter.value() + "%");
            case IN -> in(field, parseInValues(field, filter.value()));
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Condition equal(Field<?> field, Object value) {
        return ((Field) field).eq(value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Condition notEqual(Field<?> field, Object value) {
        return ((Field) field).ne(value);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Condition in(Field<?> field, List<Object> values) {
        return ((Field) field).in(values);
    }

    private Field<?> allowedField(String fieldName) {
        Field<?> field = ALLOWED_FIELDS.get(fieldName);
        if (field == null) {
            throw new BusinessException(
                    "invalidQueryField",
                    "Unsupported transaction query field: " + fieldName,
                    HttpStatus.BAD_REQUEST.value()
            );
        }
        return field;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private Field<Comparable> comparableField(Field<?> field) {
        if (!Comparable.class.isAssignableFrom(field.getType())) {
            throw new BusinessException(
                    "invalidQueryOperator",
                    "Operator requires a comparable field",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
        return (Field<Comparable>) field;
    }

    @SuppressWarnings("unchecked")
    private Field<String> stringField(Field<?> field) {
        if (!String.class.equals(field.getType())) {
            throw new BusinessException(
                    "invalidQueryOperator",
                    "LIKE operator requires a text field",
                    HttpStatus.BAD_REQUEST.value()
            );
        }
        return (Field<String>) field;
    }

    private List<Object> parseInValues(Field<?> field, String rawValue) {
        List<Object> values = new ArrayList<>();
        for (String value : rawValue.split(",")) {
            values.add(parseValue(field, value.trim()));
        }
        return values;
    }

    private Object parseValue(Field<?> field, String rawValue) {
        try {
            if (BigDecimal.class.equals(field.getType())) {
                return new BigDecimal(rawValue);
            }
            if (LocalDateTime.class.equals(field.getType())) {
                Instant instant = Instant.parse(rawValue);
                return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
            }
            return rawValue;
        } catch (RuntimeException ex) {
            throw new BusinessException(
                    "invalidQueryValue",
                    "Invalid value for field: " + field.getName(),
                    HttpStatus.BAD_REQUEST.value()
            );
        }
    }
}
