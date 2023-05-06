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
public class PaymentReqLenderDto {

    private Integer idOrder;

    @NotNull(message = "Invalid idBorrower: idBorrower is NULL")
    private Integer idLender;

    @NotNull(message = "Invalid idProduct: idProduct is NULL")
    private Integer idProduct;

    @NotNull(message = "Invalid totalPayment: totalPayment is NULL")
    private String totalPayment;

    @NotNull(message = "Invalid pin: pin is NULL")
    private Integer pin;

}
