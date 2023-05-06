package com.p2plending.payment.db.lenderdb.repository;

import com.p2plending.payment.db.lenderdb.model.LenderModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;

public interface LenderRepository extends JpaRepository<LenderModel, Integer> {

}
