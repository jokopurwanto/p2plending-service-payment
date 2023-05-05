package com.p2plending.payment.dto;

import lombok.*;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
public class PaymentExceptionDto {
    private final Integer status;
    private final String message;
    private final Throwable throwable;
}
