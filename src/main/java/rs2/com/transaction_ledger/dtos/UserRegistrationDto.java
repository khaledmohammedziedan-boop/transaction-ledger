package rs2.com.transaction_ledger.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(name = "UserRegistrationDto", description = "Request payload for registering a new user")
public class UserRegistrationDto {

    @Schema(description = "Full name of the user", example = "Anas Momani")
    private String name;

    @Schema(description = "Email address used as login identifier", example = "anasmomani@gmail.com")
    private String email;

    @Schema(description = "Optional mobile phone number of the user", example = "+35612345678")
    private String mobileNumber;

    @Schema(description = "Plain text password to be encoded and stored securely", example = "P@ssw0rd!")
    private String pwd;
}
