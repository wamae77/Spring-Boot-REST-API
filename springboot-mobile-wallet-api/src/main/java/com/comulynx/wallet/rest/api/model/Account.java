package com.comulynx.wallet.rest.api.model;

import javax.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
    @Column(unique = true,nullable = false)
    private String accountNo;
	@Column(unique = true,nullable = false)
	private String customerId;
	@Column(nullable = false)
	private double balance;

	
	public Account() {
		
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

}
