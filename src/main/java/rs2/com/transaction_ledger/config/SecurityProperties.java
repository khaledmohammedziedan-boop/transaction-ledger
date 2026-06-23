package rs2.com.transaction_ledger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(String jwtSecretKey,
                                 String jwtSecretDefaultValue,
                                 String jwtHeader) {

    public String jwtSecret() {
        return jwtSecretKey == null || jwtSecretKey.isBlank()
                ? jwtSecretDefaultValue
                : jwtSecretKey;
    }
}
