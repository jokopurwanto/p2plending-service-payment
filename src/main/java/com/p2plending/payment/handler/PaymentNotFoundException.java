package com.p2plending.payment.handler;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(String message){
        super(message);
    }

    public PaymentNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
