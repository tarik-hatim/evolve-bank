package com.hatim.evolvebank.services;

import com.hatim.evolvebank.dtos.CustomerDto;
import com.hatim.evolvebank.entities.BankAccount;
import com.hatim.evolvebank.entities.Customer;
import com.hatim.evolvebank.exceptions.BankAccountNotFoundException;
import com.hatim.evolvebank.exceptions.CustomerNotFoundException;
import com.hatim.evolvebank.exceptions.InsufficientBalanceException;
import com.hatim.evolvebank.exceptions.InsufficientDepositAmountException;

import java.util.List;

public interface BankAccountService {
    CustomerDto saveCustomer(CustomerDto customer);
    CustomerDto getCustomer(Long customerId) throws CustomerNotFoundException;

    CustomerDto updateCustomer(CustomerDto customerDto) throws CustomerNotFoundException;

    void deleteCustomer(Long id) throws CustomerNotFoundException;
    List<CustomerDto> listCustomers();

    BankAccount saveCurrentBankAccount(Long customerId, double initialBalance, double overDraft) throws CustomerNotFoundException;
    BankAccount saveSavingBankAccount(Long customerId, double initialBalance, double interestRate) throws CustomerNotFoundException;
    BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException;
    void withdraw(String accountId, double amount, String description) throws BankAccountNotFoundException, InsufficientBalanceException;
    void deposit(String accountId, double amount, String description) throws BankAccountNotFoundException, InsufficientDepositAmountException;
    void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, InsufficientBalanceException, InsufficientDepositAmountException;

    List<BankAccount> bankAccountList();
}
