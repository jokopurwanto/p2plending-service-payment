package com.p2plending.payment.db.catalogdb.repository;

import com.p2plending.payment.db.catalogdb.model.CatalogModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface CatalogRepository extends JpaRepository<CatalogModel, Integer> {
    CatalogModel findByNameAndDate(String name, Date date);
}
