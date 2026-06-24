package rs2.com.transaction_ledger.service;

import org.apache.commons.validator.routines.IBANValidator;
import org.springframework.stereotype.Component;

@Component
public class IbanValidator {

    private final IBANValidator validator = IBANValidator.DEFAULT_IBAN_VALIDATOR;

    public boolean isValid(String iban) {
        if (iban == null) {
            return false;
        }

        String normalizedIban = normalize(iban);
        return validator.isValid(normalizedIban);
    }

    public String normalize(String iban) {
        return iban == null ? null : iban.replaceAll("\\s+", "").toUpperCase();
    }
}
