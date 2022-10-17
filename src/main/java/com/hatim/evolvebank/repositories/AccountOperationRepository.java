package com.hatim.evolvebank.repositories;

import com.hatim.evolvebank.entities.AccountOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, Long> {
}
