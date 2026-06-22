package rs2.com.transaction_ledger.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import rs2.com.transaction_ledger.model.User;

import java.util.Optional;
import java.util.UUID;

@Repository("UserRepository")
public interface UserRepository extends JpaRepository<User, UUID> {

    @Query("SELECT u FROM User u" +
            " WHERE u.email = :email")
    Optional<User> loadUserByUsername(String email);

}
