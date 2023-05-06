package com.p2plending.payment.db.fundingloandb.model;

import jakarta.persistence.*;
import lombok.*;
@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_funding_loan")
@AllArgsConstructor
@NoArgsConstructor
public class FundingLoanModel {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name="id_lender_payment")
    private Integer idLenderPayment;

    @Column(name="id_borrower_payment")
    private Integer idBorrowerPayment;

    @Column(name="id_borrower")
    private Integer idBorrower;

    @Column(name="id_lender")
    private Integer idLender;

    @Column(name="id_product")
    private Integer idProduct;

    @Column(name="title")
    private String title;

    @Column(name="amount")
    private Integer amount;

    @Column(name="status")
    private String status;
}
