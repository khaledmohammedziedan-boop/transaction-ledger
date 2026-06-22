package rs2.com.transaction_ledger.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rs2.com.transaction_ledger.config.SecurityProperties;
import rs2.com.transaction_ledger.dtos.LoginRequestDTO;
import rs2.com.transaction_ledger.dtos.LoginResponseDTO;
import rs2.com.transaction_ledger.dtos.UserRegistrationDto;
import rs2.com.transaction_ledger.dtos.UserResponseDto;
import rs2.com.transaction_ledger.model.User;
import rs2.com.transaction_ledger.repo.UserRepository;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service("UserService")
@RequiredArgsConstructor
public class UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final SecurityProperties securityProperties;
    private final Environment env;

    public void registerUser(UserRegistrationDto userRegistrationDto) {
        User user = new User();
        String hashPwd = passwordEncoder.encode(userRegistrationDto.getPwd());
        user.setPwd(hashPwd);
        user.setName(userRegistrationDto.getName());
        user.setEmail(userRegistrationDto.getEmail());
        user.setMobileNumber(userRegistrationDto.getMobileNumber());
        user.setCreatedBy("SYSTEM_REGISTRATION");
        User savedCustomer = userRepository.save(user);
    }

    public UserResponseDto loadUserByUsername(String email) {
        User user = userRepository.loadUserByUsername(email).orElseThrow(() -> new
                RuntimeException("User details not found for the user: " + email));
        return new UserResponseDto(user.getId(), user.getEmail(), user.getName(), user.getMobileNumber());
    }

    public User getUserByUsername(String email) {
        return userRepository.loadUserByUsername(email).orElseThrow(() -> new
                RuntimeException("User details not found for the user: " + email));
    }

    public LoginResponseDTO apiLogin(LoginRequestDTO loginRequest) {
        String jwt = "";
        Authentication authentication = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(),
                loginRequest.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authentication);
        if (authenticationResponse != null && authenticationResponse.isAuthenticated()) {
            if (env != null) {
                String secret = env.getProperty(securityProperties.JWT_SECRET_KEY(),
                        securityProperties.JWT_SECRET_DEFAULT_VALUE());
                SecretKey secretKey = io.jsonwebtoken.security.Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
                jwt = io.jsonwebtoken.Jwts.builder().issuer("RS2").subject("JWT Token")
                        .claim("username", authenticationResponse.getName())
                        .claim("authorities", authenticationResponse.getAuthorities().stream()
                                .map(GrantedAuthority::getAuthority)
                                .collect(Collectors.joining(",")))
                        .issuedAt(new java.util.Date())
                        .expiration(new java.util.Date((new java.util.Date()).getTime() + 30000000))
                        .signWith(secretKey).compact();
            }
        }
        return new LoginResponseDTO(HttpStatus.OK.getReasonPhrase(), jwt);
    }
}
