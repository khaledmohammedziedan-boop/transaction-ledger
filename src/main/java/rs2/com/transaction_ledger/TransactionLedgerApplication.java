package rs2.com.transaction_ledger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
@OpenAPIDefinition(
        info = @Info(
                title = "Transaction Ledger API",
                description = "REST API for registering users, issuing JWTs, and managing authenticated transaction records.",
                version = "v1",
                contact = @Contact(
                        name = "Khaled Saleh",
                        email = "kmsaleh290@gmail.com"
                )
        )
)
public class TransactionLedgerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransactionLedgerApplication.class, args);
	}

}
