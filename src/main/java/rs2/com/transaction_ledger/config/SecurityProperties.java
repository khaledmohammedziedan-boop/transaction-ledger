package rs2.com.transaction_ledger.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.security")
public record SecurityProperties(String JWT_SECRET_KEY,
                                 String JWT_SECRET_DEFAULT_VALUE,
                                 String JWT_HEADER) {

}
