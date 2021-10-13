package com.comulynx.wallet.rest.api.controller;

import com.comulynx.wallet.rest.api.AppUtilities;
import com.comulynx.wallet.rest.api.model.Account;
import com.comulynx.wallet.rest.api.model.Customer;
import com.comulynx.wallet.rest.api.repository.AccountRepository;
import com.comulynx.wallet.rest.api.repository.CustomerRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@CrossOrigin(origins = "http://localhost:4200")
@RestController
@Transactional
@RequestMapping("/api/v1/customers")
public class CustomerController {
	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	private Gson gson = new Gson();

	@Autowired
	private CustomerRepository customerRepository;
	@Autowired
	private AccountRepository accountRepository;
	@GetMapping("/")
	public List<Customer> getAllCustomers() {
		return customerRepository.findAll();
	}

	/**
	 * Fix Customer Login functionality
	 * 
	 * Login
	 * 
	 * @param request
	 * @return
	 */
	@PostMapping("/login")
	public ResponseEntity<?> customerLogin(@RequestBody String request) {
		try {
			JsonObject response = new JsonObject();

			final JsonObject req = gson.fromJson(request, JsonObject.class);
			String customerId = req.get("customerId").getAsString();
			String customerPIN = req.get("pin").getAsString();

			Optional<Customer> customer = customerRepository.findByCustomerId(customerId);
			if (!customer.isPresent()){
				return ResponseEntity.status(404).body("Customer does not exist");
			}
			if (!customerPIN.equals(customer.get().getPin())){
				return ResponseEntity.status(404).body("Invalid credentials");
			}

			Optional<Account> account = accountRepository.findAccountByCustomerId(customerId);
			if (!account.isPresent()){
				return ResponseEntity.status(404).body("Customer account not found ");
			}

			response.addProperty("Customer Name",customer.get().getFirstName()+""+customer.get().getLastName());
			response.addProperty("Customer ID",customer.get().getCustomerId());
			response.addProperty("email",customer.get().getEmail());
			response.add("Customer Account", new Gson().toJsonTree(account.get()));
			return ResponseEntity.ok().body(gson.toJson(response));

		} catch (Exception ex) {
			logger.info("Exception {}", AppUtilities.getExceptionStacktrace(ex));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	@DeleteMapping("/")
	public ResponseEntity<?> deleteCustomer(@RequestBody String request){
		try {
			JsonObject response = new JsonObject();

			final JsonObject req = gson.fromJson(request, JsonObject.class);
			String customerId = req.get("customerId").getAsString();

			customerRepository.deleteCustomerByCustomerId(customerId);
			return ResponseEntity.ok().build();
		}catch (Exception ex){
			logger.info("Exception {}", AppUtilities.getExceptionStacktrace(ex));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping("/")
	public ResponseEntity<?> updateCustomerFirstName(@RequestBody String request){
		try {
			JsonObject response = new JsonObject();

			final JsonObject req = gson.fromJson(request, JsonObject.class);
			String customerId = req.get("customerId").getAsString();
			String firstName = req.get("firstName").getAsString();

			customerRepository.updateCustomerByCustomerId(firstName,customerId);
			return ResponseEntity.ok().build();
		}catch (Exception ex){
			logger.info("Exception {}", AppUtilities.getExceptionStacktrace(ex));
			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 *  Add required logic
	 *  
	 *  Create Customer
	 *  
	 * @param customer
	 * @return
	 */
	@PostMapping("/")
	public ResponseEntity<?> createCustomer(@Valid @RequestBody Customer customer) {
		try {
			String customerPIN = customer.getPin();
			String email = customer.getEmail();

			BCryptPasswordEncoder P = new BCryptPasswordEncoder();
			String encodedPass =  P.encode(customerPIN);

			Optional<Customer> c = customerRepository.findByCustomerEmailOrId(email,customer.getCustomerId());
			if (c.isPresent()){
				return ResponseEntity.status(HttpStatus.CONFLICT).body("Customer Exist");
			}


			String accountNo = generateAccountNo(customer.getCustomerId());
			Account account = new Account();
			account.setCustomerId("CUST"+customer.getCustomerId());
			account.setAccountNo(accountNo);
			account.setBalance(0.0);
			accountRepository.save(account);

			customer.setCustomerId("CUST"+customer.getCustomerId());
			customer.setPin(encodedPass);

			return ResponseEntity.ok().body(customerRepository.save(customer));
		} catch (Exception ex) {
			logger.info("Exception {}", AppUtilities.getExceptionStacktrace(ex));

			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}


	@GetMapping("/emailswithGmail")
	public ResponseEntity<?> getAllEmailsWithGmail(){
		try {
			Optional<List<Customer>> customerList = customerRepository.findAllCustomersWhoseEmailContainsGmail();

			List<Customer> customers = new ArrayList<>();

			String expressions = "^[a-z0-9](\\.?[a-z0-9]){5,}@g(oogle)?mail\\.com$";
			Pattern pattern = Pattern.compile(expressions);

			if (!customerList.isPresent()){
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body("NO EMAILS FOUND");
			}

			customerList.get().forEach(customer -> {
				Matcher matcher = pattern.matcher(customer.getEmail());
				if (matcher.matches()) {
					customers.add(customer);
				}
			});
			return ResponseEntity.ok().body(gson.toJson(customers));
		} catch (Exception ex) {
			logger.info("Exception {}", AppUtilities.getExceptionStacktrace(ex));

			return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}

	/**
	 *  Add required functionality
	 *
	 * generate a random but unique Account No (NB: Account No should be unique
	 * in your accounts table)
	 *
	 */
	private String generateAccountNo(String customerId) {
		Random rand = new Random();
		int maxNumber = 10;
		int p = Integer.parseInt(customerId);
		int randomNumber = rand.nextInt(maxNumber) + 1;
		String s=String.valueOf(randomNumber+p);
		return "ACT"+s;
	}
}
