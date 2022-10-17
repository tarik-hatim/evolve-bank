package com.hatim.evolvebank;

import com.github.javafaker.Faker;
import com.hatim.evolvebank.dtos.CustomerDto;
import com.hatim.evolvebank.entities.*;
import com.hatim.evolvebank.enums.AccountStatus;
import com.hatim.evolvebank.enums.OperationType;
import com.hatim.evolvebank.exceptions.BankAccountNotFoundException;
import com.hatim.evolvebank.exceptions.CustomerNotFoundException;
import com.hatim.evolvebank.exceptions.InsufficientBalanceException;
import com.hatim.evolvebank.exceptions.InsufficientDepositAmountException;
import com.hatim.evolvebank.repositories.AccountOperationRepository;
import com.hatim.evolvebank.repositories.BankAccountRepository;
import com.hatim.evolvebank.repositories.CustomerRepository;
import com.hatim.evolvebank.services.BankAccountService;
import com.hatim.evolvebank.services.BankService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.UUID;
import java.util.stream.Stream;

@SpringBootApplication
public class EvolveBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(EvolveBankApplication.class, args);
    }

    @Bean
    CommandLineRunner read(BankAccountService bankAccountService) {
        return args -> {
            Stream.of("Khalid","Halima","Arthur").forEach(name->{
                CustomerDto customerDto = new CustomerDto();
                customerDto.setName(name);
                customerDto.setEmail(name+"@gmail.com");
                bankAccountService.saveCustomer(customerDto);
            });
            bankAccountService.listCustomers().forEach(customer -> {
                try {
                    bankAccountService.saveCurrentBankAccount(customer.getId(), Math.random()*100000,8000);
                    bankAccountService.saveSavingBankAccount(customer.getId(),Math.random()*150000,3.7);
                } catch (CustomerNotFoundException e) {
                    e.printStackTrace();
                }
            });

            bankAccountService.bankAccountList().forEach(bankAccount -> {
                for (int i = 0; i < 10; i++) {
                    try {
                        bankAccountService.deposit(bankAccount.getId(),Math.random()*10000,"deposit");
                        bankAccountService.withdraw(bankAccount.getId(), Math.random()* 5000, "withdraw");
                    } catch (BankAccountNotFoundException | InsufficientDepositAmountException |
                             InsufficientBalanceException e) {
                        e.printStackTrace();
                    }
                }
            });

        };
    }
    //@Bean
    CommandLineRunner start(
            CustomerRepository customerRepository,
            BankAccountRepository bankAccountRepository,
            AccountOperationRepository accountOperationRepository
        ) {
        Faker faker = new Faker();
        return args -> {

            Stream.of("Hicham", "Imane", "Wahiba").forEach(name -> {
                Customer customer = new Customer();
                customer.setName(name);
                customer.setEmail(faker.internet().emailAddress());
                customerRepository.save(customer);

            });
            customerRepository.findAll().forEach(customer -> {
                CurrentAccount currentAccount = new CurrentAccount();
                currentAccount.setId(UUID.randomUUID().toString());
                currentAccount.setOverDraft(faker.number().randomDouble(2, 8000, 12000));
                currentAccount.setBalance(faker.number().randomDouble(2, 70000, 1480000));
                currentAccount.setCurrency("DH");
                currentAccount.setCreatedAt(new Date());
                currentAccount.setStatus(AccountStatus.CREATED);
                currentAccount.setCustomer(customer);
                bankAccountRepository.save(currentAccount);

                SavingAccount savingAccount = new SavingAccount();
                savingAccount.setId(UUID.randomUUID().toString());
                savingAccount.setInterestRate(3.5);
                savingAccount.setBalance(faker.number().randomDouble(2, 70000, 1480000));
                savingAccount.setCurrency("DH");
                savingAccount.setCreatedAt(new Date());
                savingAccount.setStatus(AccountStatus.CREATED);
                savingAccount.setCustomer(customer);
                bankAccountRepository.save(savingAccount);
            });
            bankAccountRepository.findAll().forEach(bankAccount -> {
                for (int i = 0; i < 10; i++) {
                    AccountOperation accountOperation = new AccountOperation();
                    accountOperation.setOperationDate(new Date());
                    accountOperation.setAmount(Math.random() * 15000);
                    accountOperation.setOperationType(Math.random() > 0.5 ? OperationType.CREDIT : OperationType.DEBIT);
                    accountOperation.setBankAccount(bankAccount);
                    accountOperationRepository.save(accountOperation);

                }
            });
        };
    }

}
