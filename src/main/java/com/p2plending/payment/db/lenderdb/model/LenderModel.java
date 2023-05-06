package com.p2plending.payment.db.lenderdb.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_lender")
@AllArgsConstructor
@NoArgsConstructor
public class LenderModel {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="email")
    private String email;

    @Column(name="account_number")
    private Integer accountNumber;

    @Column(name="bank_name")
    private String bankName;

    @Column(name="balance")
    private Integer balance;

    @Column(name="lender_type")
    private String lenderType;

    @Column(name="pin")
    private Integer pin;

}
