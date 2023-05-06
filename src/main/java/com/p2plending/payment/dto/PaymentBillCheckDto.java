package com.p2plending.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
public class PaymentBillCheckDto {

    @NotNull(message = "Invalid idProduct: idProduct is NULL")
    private Integer idProduct;
    @NotNull(message = "Invalid idProduct: idProduct is NULL")
    private Integer idOrder;
}
