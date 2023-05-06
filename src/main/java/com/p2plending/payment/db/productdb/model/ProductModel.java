package com.p2plending.payment.db.productdb.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_product")
@AllArgsConstructor
@NoArgsConstructor
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Integer id;

    @Column(name = "id_borrower")
    private Integer idBorrower;

    @Column(name = "product_title")
    private String productTitle;

    @Column(name = "interest")
    private Integer interest;

    @Column(name = "loan_amount")
    private String loanAmount;

    @Column(name = "remaining_req_amount")
    private String remainingReqAmount;

    @Column(name = "loan_date")
    private Date loanDate;

    @Column(name = "loan_due_date")
    private Date loanDueDate;

    @Column(name = "status")
    private Boolean status;

    @Column(name = "time_period")
    private Integer timePeriod;

}

