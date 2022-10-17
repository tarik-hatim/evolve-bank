package com.hatim.evolvebank.repositories;

import com.hatim.evolvebank.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
