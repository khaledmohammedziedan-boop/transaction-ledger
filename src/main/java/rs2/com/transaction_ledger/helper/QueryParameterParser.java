package rs2.com.transaction_ledger.helper;

import org.springframework.http.HttpStatus;
import rs2.com.transaction_ledger.dtos.Direction;
import rs2.com.transaction_ledger.dtos.FilterDto;
import rs2.com.transaction_ledger.dtos.Operator;
import rs2.com.transaction_ledger.dtos.PageRequestDto;
import rs2.com.transaction_ledger.dtos.SortDto;
import rs2.com.transaction_ledger.exception.BusinessException;

import java.util.ArrayList;
import java.util.List;

public class QueryParameterParser {

	public static PageRequestDto parseQueryParameters(int page, int size, String[] sortParams, String[] filterParams) {
		List<SortDto> sorts = parseSortParameters(sortParams);
		List<FilterDto> filters = parseFilterParameters(filterParams);

		return new PageRequestDto(page, size, sorts, filters);
	}

	private static List<SortDto> parseSortParameters(String[] sortParams) {
		List<SortDto> sorts = new ArrayList<>();
		if (sortParams == null || sortParams.length == 0) return sorts;

		String[] all = String.join(",", sortParams).split(",");

		for (int i = 0; i < all.length - 1; i += 2) {
			String field = all[i].trim();
			String directionStr = all[i + 1].trim().toUpperCase();

			if (field.isEmpty()) {
				throw new BusinessException("invalidSortField", "Sort field cannot be empty", HttpStatus.BAD_REQUEST.value());
			}

			Direction direction;
			try {
				direction = Direction.valueOf(directionStr);
			} catch (IllegalArgumentException e) {
				throw new BusinessException("invalidSortDirection", "Sort direction must be 'ASC' or 'DESC', got: " + directionStr, HttpStatus.BAD_REQUEST.value());
			}

			sorts.add(new SortDto(field, direction));
		}
		return sorts;
	}

	private static List<FilterDto> parseFilterParameters(String[] filterParams) {
		List<FilterDto> filters = new ArrayList<>();
		if (filterParams == null || filterParams.length == 0) return filters;

		String[] all = String.join(",", filterParams).split(",");

		for (int i = 0; i < all.length - 2; i += 3) {
			String field = all[i].trim();
			String operatorStr = all[i + 1].trim().toUpperCase();
			String value = all[i + 2].trim();

			if (field.isEmpty()) {
				throw new BusinessException("invalidFilterField", "Filter field cannot be empty", HttpStatus.BAD_REQUEST.value());
			}

			if (value.isEmpty()) {
				throw new BusinessException("invalidFilterValue", "Filter value cannot be empty", HttpStatus.BAD_REQUEST.value());
			}

			Operator operator;
			try {
				operator = Operator.valueOf(operatorStr);
			} catch (IllegalArgumentException e) {
				throw new BusinessException("invalidFilterOperator", "Invalid filter operator: " + operatorStr + ". Valid operators are: EQ, NE, GT, GTE, LT, LTE, LIKE, IN", HttpStatus.BAD_REQUEST.value());
			}

			filters.add(new FilterDto(field, operator, value));
		}
		return filters;
	}
}
