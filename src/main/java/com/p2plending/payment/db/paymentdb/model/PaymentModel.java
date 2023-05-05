package com.p2plending.payment.db.paymentdb.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_payment")
@AllArgsConstructor
@NoArgsConstructor
public class PaymentModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "payment_type")
    private String paymentType;

    @Column(name = "total_price")
    private String totalPrice;

    @Column(name = "date")
    private Date date;

    @Column(name = "status")
    private boolean status;
}
