package com.p2plending.payment.db.borrowerdb.repository;

import com.p2plending.payment.db.borrowerdb.model.BorrowerModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BorrowerRepository extends JpaRepository<BorrowerModel, Integer> {
}
