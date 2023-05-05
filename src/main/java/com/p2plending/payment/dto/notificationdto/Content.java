package com.p2plending.payment.dto.notificationdto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Content {
    private Integer id;
    private String status;
    private String email;
    private Timestamp cretedAt;
}
