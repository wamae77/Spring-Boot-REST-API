DROP TABLE IF EXISTS customers CASCADE;
CREATE TABLE customers (
  id INT AUTO_INCREMENT  PRIMARY KEY,
  customer_id VARCHAR(255),
  pin VARCHAR(250),
  first_name VARCHAR(250),
  email VARCHAR(250),
  last_name VARCHAR(250)
);

DROP TABLE IF EXISTS accounts CASCADE;
CREATE TABLE  accounts (
    id INT AUTO_INCREMENT  PRIMARY KEY,
    customer_id VARCHAR(255),
    account_no VARCHAR(255),
    balance  DOUBLE
);
DROP TABLE IF EXISTS transactions CASCADE;
CREATE TABLE  transactions (
    id INT AUTO_INCREMENT  PRIMARY KEY,
    transaction_id VARCHAR(255),
    customer_id VARCHAR(255),
    account_no VARCHAR(255),
    amount DOUBLE,
    transaction_type VARCHAR(255),
    debit_or_Credit VARCHAR(250),
    balance DOUBLE
);

