package rs2.com.transaction_ledger.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class IbanValidatorTest {

    private final IbanValidator ibanValidator = new IbanValidator();

    @Test
    void acceptsValidInternationalIban() {
        assertThat(ibanValidator.isValid("DE44500105175407324931")).isTrue();
        assertThat(ibanValidator.isValid("GB82 WEST 1234 5698 7654 32")).isTrue();
    }

    @Test
    void rejectsInvalidIbanChecksum() {
        assertThat(ibanValidator.isValid("DE44500105175407324932")).isFalse();
    }

    @Test
    void normalizesWhitespaceAndCase() {
        assertThat(ibanValidator.normalize("gb82 west 1234 5698 7654 32"))
                .isEqualTo("GB82WEST12345698765432");
    }
}
