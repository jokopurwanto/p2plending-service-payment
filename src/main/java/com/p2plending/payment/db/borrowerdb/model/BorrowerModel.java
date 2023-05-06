package com.p2plending.payment.db.borrowerdb.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_borrower")
@AllArgsConstructor
@NoArgsConstructor
public class BorrowerModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Integer id;

    @Column(name = "id_loan_req")
    private Integer idLoanReq;

    @Column(name = "id_user")
    private Integer idUser;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "balance")
    private Integer balance;

    @Column(name = "pin")
    private Integer pin;

}
