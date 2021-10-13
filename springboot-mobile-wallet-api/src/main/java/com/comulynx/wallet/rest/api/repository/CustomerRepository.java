package com.comulynx.wallet.rest.api.repository;

import com.comulynx.wallet.rest.api.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

	@Autowired
	JdbcTemplate jdbctemplate = new JdbcTemplate();

	@Query(value="SELECT * FROM customers  WHERE customer_id = ?1",nativeQuery = true)
	Optional<Customer> findByCustomerId(String customerId);

	@Query(value="SELECT * FROM customers  WHERE email = ?1 OR customer_id = ?2",nativeQuery = true)
	Optional<Customer> findByCustomerEmailOrId(String email,String id);

	@Modifying
	@Query(value="DELETE FROM customers  WHERE customer_id = ?1",nativeQuery = true)
	void deleteCustomerByCustomerId(String customer_id);

	@Modifying
	@Query(value="UPDATE customers SET first_name = ?1  WHERE customer_id = ?2 ",nativeQuery = true)
	int updateCustomerByCustomerId(String firstName, String customer_id);
	

	 @Query(value = "SELECT * FROM customers",nativeQuery = true)
	 Optional<List<Customer>> findAllCustomersWhoseEmailContainsGmail();
}
