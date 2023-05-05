package com.p2plending.payment.dto.notificationdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationPostDto {

    //req hit service notif
    private Integer idUser;
    private Integer idOrder;
    private String destination;
    private Date startDate;
    private Date endDate;
    private Integer totalPerson;
    private String totalPrice;

    //resp hit service notif
    private String email;
    private Integer status;
    private String message;
    private Content data;
}
