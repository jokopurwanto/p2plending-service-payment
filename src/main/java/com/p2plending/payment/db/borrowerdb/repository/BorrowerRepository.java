package com.p2plending.payment.db.borrowerdb.repository;

import com.p2plending.payment.db.borrowerdb.model.BorrowerModel;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface BorrowerRepository extends JpaRepository<BorrowerModel, Integer> {
//    @Modifying
//    @Query(value="update tbl_borrower set balance = :balance where id = :id", nativeQuery = true)
//    void updateBalance(@Param("balance") Integer balance, @Param("id") Integer id);

}
