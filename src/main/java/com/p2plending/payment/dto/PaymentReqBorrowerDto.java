package com.p2plending.payment.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
@Data
@Builder
public class PaymentReqBorrowerDto {

    @NotNull(message = "Invalid iduser: iduser is NULL")
    private Integer idOrder;

    @NotNull(message = "Invalid iduser: iduser is NULL")
    private Integer idBorrower;

    @NotNull(message = "Invalid iduser: iduser is NULL")
    private Integer idProduct;

    @NotNull(message = "Invalid destination: destination is NULL")
    private String title;

    @NotNull(message = "Invalid Start date: Start date is NULL")
    private Date totalPayment;

    @NotNull(message = "Invalid End date: End date is NULL")
    private Date pin;

}
