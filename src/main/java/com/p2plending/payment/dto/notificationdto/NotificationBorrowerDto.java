package com.p2plending.payment.dto.notificationdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationBorrowerDto {

    //req hit service notif
    private String username;
    private String totalPayment;
    private String product;
    private String email;

    //resp hit service notif
    private Integer status;
    private String message;
    private Content data;
}
