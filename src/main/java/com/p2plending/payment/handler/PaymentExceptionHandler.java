package com.p2plending.payment.handler;

import com.p2plending.payment.dto.PaymentExceptionDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class PaymentExceptionHandler {
    @ExceptionHandler(value = {PaymentNotFoundException.class})
    public ResponseEntity<Object> handleCatalogNotFoundException (PaymentNotFoundException paymentNotFoundException){
        PaymentExceptionDto paymentExceptionDto = new PaymentExceptionDto(
                HttpStatus.NOT_FOUND.value(),
                paymentNotFoundException.getMessage(),
                paymentNotFoundException.getCause()
        );
        return new ResponseEntity<>(paymentExceptionDto, HttpStatus.NOT_FOUND);
    }
}
