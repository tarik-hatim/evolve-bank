package com.hatim.evolvebank.repositories;

import com.hatim.evolvebank.entities.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, String> {
}
