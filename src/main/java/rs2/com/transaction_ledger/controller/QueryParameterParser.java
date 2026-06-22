package rs2.com.transaction_ledger.controller;

import rs2.com.transaction_ledger.dtos.*;
import rs2.com.transaction_ledger.exception.BusinessException;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

public class QueryParameterParser {

    /**
     * Parses query parameters into a PageRequestDto.
     *
     * Expects sort format: "field,direction" (e.g., "createdAt,DESC")
     * Expects filter format: "field,operator,value" (e.g., "amount,GT,100")
     *
     * @param page Zero-based page index
     * @param size Page size
     * @param sortParams Array of sort specifications
     * @param filterParams Array of filter specifications
     * @return PageRequestDto with parsed parameters
     */
    public static PageRequestDto parseQueryParameters(int page, int size, String[] sortParams, String[] filterParams) {
        List<SortDto> sorts = parseSortParameters(sortParams);
        List<FilterDto> filters = parseFilterParameters(filterParams);

        return new PageRequestDto(page, size, sorts, filters);
    }

    private static List<SortDto> parseSortParameters(String[] sortParams) {
        List<SortDto> sorts = new ArrayList<>();

        if (sortParams == null || sortParams.length == 0) {
            return sorts;
        }

        for (String sortParam : sortParams) {
            SortDto sortDto = parseSingleSortParameter(sortParam);
            sorts.add(sortDto);
        }

        return sorts;
    }

    private static SortDto parseSingleSortParameter(String sortParam) {
        if (sortParam == null || sortParam.trim().isEmpty()) {
            throw new BusinessException(
                    "invalidSortParameter",
                    "Sort parameter cannot be empty",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        String[] parts = sortParam.split(",");

        if (parts.length != 2) {
            throw new BusinessException(
                    "invalidSortFormat",
                    "Sort parameter must be in format 'field,direction' (e.g., 'createdAt,DESC')",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        String field = parts[0].trim();
        String directionStr = parts[1].trim().toUpperCase();

        if (field.isEmpty()) {
            throw new BusinessException(
                    "invalidSortField",
                    "Sort field cannot be empty",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        Direction direction;
        try {
            direction = Direction.valueOf(directionStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "invalidSortDirection",
                    "Sort direction must be 'ASC' or 'DESC', got: " + directionStr,
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        return new SortDto(field, direction);
    }

    private static List<FilterDto> parseFilterParameters(String[] filterParams) {
        List<FilterDto> filters = new ArrayList<>();

        if (filterParams == null || filterParams.length == 0) {
            return filters;
        }

        for (String filterParam : filterParams) {
            FilterDto filterDto = parseSingleFilterParameter(filterParam);
            filters.add(filterDto);
        }

        return filters;
    }

    private static FilterDto parseSingleFilterParameter(String filterParam) {
        if (filterParam == null || filterParam.trim().isEmpty()) {
            throw new BusinessException(
                    "invalidFilterParameter",
                    "Filter parameter cannot be empty",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        String[] parts = filterParam.split(",", 3);

        if (parts.length != 3) {
            throw new BusinessException(
                    "invalidFilterFormat",
                    "Filter parameter must be in format 'field,operator,value' (e.g., 'amount,GT,100')",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        String field = parts[0].trim();
        String operatorStr = parts[1].trim().toUpperCase();
        String value = parts[2].trim();

        if (field.isEmpty()) {
            throw new BusinessException(
                    "invalidFilterField",
                    "Filter field cannot be empty",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        if (value.isEmpty()) {
            throw new BusinessException(
                    "invalidFilterValue",
                    "Filter value cannot be empty",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        Operator operator;
        try {
            operator = Operator.valueOf(operatorStr);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(
                    "invalidFilterOperator",
                    "Invalid filter operator: " + operatorStr + ". Valid operators are: EQ, NE, GT, GTE, LT, LTE, LIKE, IN",
                    HttpStatus.BAD_REQUEST.value()
            );
        }

        return new FilterDto(field, operator, value);
    }
}
