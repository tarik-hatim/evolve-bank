package com.hatim.evolvebank.exceptions;

public class InsufficientDepositAmountException extends Throwable {
    public InsufficientDepositAmountException(String message) {
        super(message);
    }
}
