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
                title = "Transaction Ledger Service REST API Documentation",
                description = "This is a simple Transaction-Ledger service REST API documentation for RS2 assignment",
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
