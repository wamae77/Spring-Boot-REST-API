package com.comulynx.wallet.rest.api.repository;

import com.comulynx.wallet.rest.api.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	@Query(value = "SELECT * FROM transactions WHERE customer_id = ?1 ORDER BY id DESC LIMIT 100  ",nativeQuery = true)
	Optional<List<Transaction>> findTransactionsByCustomerId(String customerId);

	Optional<List<Transaction>> findTransactionsByTransactionId(String transactionId);

	Optional<List<Transaction>> findTransactionsByCustomerIdOrTransactionId(String transactionId, String customerId);

	@Query(value = "SELECT * FROM transactions WHERE customer_id = ?1 AND  account_no =?2 ORDER BY id DESC LIMIT 5",nativeQuery = true)
	Optional<List<Transaction>> getMiniStatementUsingCustomerIdAndAccountNo(String customer_id, String account_no);

}
