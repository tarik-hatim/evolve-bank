package com.hatim.evolvebank.web;

import com.hatim.evolvebank.dtos.CustomerDto;
import com.hatim.evolvebank.exceptions.CustomerNotFoundException;
import com.hatim.evolvebank.services.BankAccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class CustomerRestController {
    private BankAccountService bankAccountService;

    @GetMapping("/customers")
    public List<CustomerDto> getCustomers() {
        return bankAccountService.listCustomers();
    }

    @GetMapping("/customers/{id}")
    public CustomerDto getCustomer(@PathVariable(name = "id") Long customerId) throws CustomerNotFoundException {
        return bankAccountService.getCustomer(customerId);
    }

    @PostMapping("/customers")
    public CustomerDto createCustomer(@RequestBody CustomerDto customerDto) {
        return bankAccountService.saveCustomer(customerDto);
    }

    @PutMapping("/customers/{id}")
    public CustomerDto updateCustomer(@PathVariable Long id, @RequestBody CustomerDto customerDto) throws CustomerNotFoundException {

        customerDto.setId(id);
        return bankAccountService.updateCustomer(customerDto);
    }

    @DeleteMapping("/customers/{customerId}")
    public void deleteCustomer(@PathVariable Long customerId) throws CustomerNotFoundException {
        bankAccountService.deleteCustomer(customerId);
    }
}
