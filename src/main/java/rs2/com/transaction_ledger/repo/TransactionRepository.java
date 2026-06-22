package rs2.com.transaction_ledger.repo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import rs2.com.transaction_ledger.model.Transaction;

import java.util.UUID;

@Repository("TransactionRepository")
public interface TransactionRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {
}
