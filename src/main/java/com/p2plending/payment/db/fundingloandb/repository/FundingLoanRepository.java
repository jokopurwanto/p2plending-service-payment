package com.p2plending.payment.db.fundingloandb.repository;

import com.p2plending.payment.db.fundingloandb.model.FundingLoanModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FundingLoanRepository extends JpaRepository<FundingLoanModel, Integer> {

    FundingLoanModel[] findByIdBorrowerAndIdProduct(Integer idBorrower, Integer idProduct);
}
