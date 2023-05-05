package com.p2plending.payment.db.catalogdb.model;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Date;

@Getter
@Setter
@Builder
@Entity
@Table(name = "tbl_packages")
@AllArgsConstructor
@NoArgsConstructor
public class CatalogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name ="id")
    private Integer id;

    @Column(name = "destination")
    private String name;

    @Column(name = "price")
    private String price;

    @Column(name = "availability")
    private Integer availability;

    @Column(name = "date")
    private Date date;

}
