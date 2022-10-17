package com.hatim.evolvebank.services;

import com.hatim.evolvebank.dtos.CustomerDto;
import com.hatim.evolvebank.entities.*;
import com.hatim.evolvebank.enums.AccountStatus;
import com.hatim.evolvebank.enums.OperationType;
import com.hatim.evolvebank.exceptions.BankAccountNotFoundException;
import com.hatim.evolvebank.exceptions.CustomerNotFoundException;
import com.hatim.evolvebank.exceptions.InsufficientBalanceException;
import com.hatim.evolvebank.exceptions.InsufficientDepositAmountException;
import com.hatim.evolvebank.mappers.BankAccountMapperImpl;
import com.hatim.evolvebank.repositories.AccountOperationRepository;
import com.hatim.evolvebank.repositories.BankAccountRepository;
import com.hatim.evolvebank.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class BankAccountServiceImpl implements BankAccountService {

    private CustomerRepository customerRepository;
    private BankAccountRepository bankAccountRepository;
    private AccountOperationRepository operationRepository;
    private BankAccountMapperImpl dtoMapper;
    //private Logger log = LoggerFactory.getLogger(this.getClass().getName());

    @Override
    public CustomerDto saveCustomer(CustomerDto customer) {
        log.info("creating a new customer");
        Customer savedCustomer = customerRepository.save(dtoMapper.fromCustomerDto(customer));
        return dtoMapper.fromCustomer(savedCustomer);
    }

    @Override
    public CustomerDto getCustomer(Long customerId) throws CustomerNotFoundException {
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer == null)
            throw new CustomerNotFoundException("Customer was not found.");
        return dtoMapper.fromCustomer(customer);
    }

    @Override
    public CustomerDto updateCustomer(CustomerDto customerDto) throws CustomerNotFoundException {
        if(customerRepository.existsById(customerDto.getId()))
            throw new CustomerNotFoundException("Customer was not found.");
        Customer customer = dtoMapper.fromCustomerDto(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return dtoMapper.fromCustomer(savedCustomer);

    }

    @Override
    public void deleteCustomer(Long id) throws CustomerNotFoundException {
        if(customerRepository.existsById(id))
            throw new CustomerNotFoundException("Customer was not found.");
        customerRepository.deleteById(id);
    }
    @Override
    public BankAccount saveCurrentBankAccount(Long customerId, double initialBalance, double overDraft) throws CustomerNotFoundException {
        CustomerDto customerDto = getCustomer(customerId);
        Customer customer = dtoMapper.fromCustomerDto(customerDto);
        CurrentAccount currentAccount = new CurrentAccount();
        currentAccount.setId(UUID.randomUUID().toString());
        currentAccount.setStatus(AccountStatus.CREATED);
        currentAccount.setCustomer(customer);
        currentAccount.setCreatedAt(new Date());
        currentAccount.setBalance(initialBalance);
        currentAccount.setOverDraft(overDraft);
        CurrentAccount savedBankAccount = bankAccountRepository.save(currentAccount);
        return savedBankAccount;
    }

    @Override
    public BankAccount saveSavingBankAccount(Long customerId, double initialBalance, double interestRate) throws CustomerNotFoundException {
        CustomerDto customerDto = getCustomer(customerId);
        Customer customer = dtoMapper.fromCustomerDto(customerDto);
        SavingAccount savingAccount = new SavingAccount();
        savingAccount.setId(UUID.randomUUID().toString());
        savingAccount.setInterestRate(interestRate);
        savingAccount.setStatus(AccountStatus.CREATED);
        savingAccount.setCustomer(customer);
        savingAccount.setCreatedAt(new Date());
        savingAccount.setBalance(initialBalance);
        SavingAccount savedSavingAccount = bankAccountRepository.save(savingAccount);
        return savedSavingAccount;
    }

    @Override
    public List<CustomerDto> listCustomers() {
        List<CustomerDto> customerDtos = customerRepository.findAll().stream().map(customer -> dtoMapper.fromCustomer(customer)).collect(Collectors.toList());
        return customerDtos;
    }

    @Override
    public BankAccount getBankAccount(String accountId) throws BankAccountNotFoundException {
        BankAccount bankAccount = bankAccountRepository.findById(accountId)
                .orElseThrow(()-> new BankAccountNotFoundException("BankAccount was not found."));
        return bankAccount;
    }

    @Override
    public void withdraw(String accountId, double amount, String description) throws BankAccountNotFoundException, InsufficientBalanceException {
        BankAccount bankAccount = getBankAccount(accountId);

        if(bankAccount.getBalance() < amount)
            throw new InsufficientBalanceException("insufficient balance,try an inferior amount.");
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setAmount(amount);
        accountOperation.setOperationType(OperationType.DEBIT);
        accountOperation.setDescription("withdraw");
        operationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance() - amount);
    }

    @Override
    public void deposit(String accountId, double amount, String description) throws BankAccountNotFoundException, InsufficientDepositAmountException {
        BankAccount bankAccount = getBankAccount(accountId);
        if (amount < 10)
            throw new InsufficientDepositAmountException("you can't deposit less than 10" + bankAccount.getCurrency());
        AccountOperation accountOperation = new AccountOperation();
        accountOperation.setAmount(amount);
        accountOperation.setBankAccount(bankAccount);
        accountOperation.setOperationDate(new Date());
        accountOperation.setOperationType(OperationType.CREDIT);
        accountOperation.setDescription("deposit");
        operationRepository.save(accountOperation);
        bankAccount.setBalance(bankAccount.getBalance()+ amount);
    }

    @Override
    public void transfer(String accountIdSource, String accountIdDestination, double amount) throws BankAccountNotFoundException, InsufficientBalanceException, InsufficientDepositAmountException {
        withdraw(accountIdSource,amount, "transfer to " + accountIdDestination);
        deposit(accountIdDestination, amount, "transfer from "+ accountIdSource);
    }

    @Override
    public List<BankAccount> bankAccountList() {
        return bankAccountRepository.findAll();
    }
}
