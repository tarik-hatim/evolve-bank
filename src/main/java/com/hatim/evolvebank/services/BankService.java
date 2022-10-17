package com.hatim.evolvebank.services;

import com.hatim.evolvebank.entities.BankAccount;
import com.hatim.evolvebank.entities.CurrentAccount;
import com.hatim.evolvebank.entities.SavingAccount;
import com.hatim.evolvebank.repositories.BankAccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class BankService {
    BankAccountRepository accountRepository;

    public void read() {
        BankAccount bankAccount = accountRepository.findById("10db7159-054b-4d09-84e3-a84fdd6b32fb").orElse(null);
        if (bankAccount != null){
            System.out.println("=================" +bankAccount.getCustomer().getName()+" informations===================");
            System.out.println(bankAccount.getId() + "\t"+ bankAccount.getBalance() +
                    bankAccount.getCurrency()+ "" + "\t"+ bankAccount.getCreatedAt() + "\t" + bankAccount.getStatus());
            if (bankAccount instanceof CurrentAccount) {
                System.out.println("overdraft: " + ((CurrentAccount) bankAccount).getOverDraft());
            } else if (bankAccount instanceof SavingAccount) {
                System.out.println("interest rate: " + ((SavingAccount) bankAccount).getInterestRate());
            }

            System.out.println("========= "+ bankAccount.getClass().getSimpleName().replace("A", " a") + " operations ======");
            bankAccount.getAccountOperations().forEach(accountOperation -> {
                System.out.println(accountOperation.getOperationType()+"\t"+accountOperation.getAmount() + "\t"+accountOperation.getOperationDate());
            });
        }
    }
}
