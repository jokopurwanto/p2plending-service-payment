package com.p2plending.payment.db.productdb.repository;

import com.p2plending.payment.db.productdb.model.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductModel, Integer> {

    //    BorrowerModel findByNameAndDate(String name, Date date);
//    @Modifying
//    @Query("UPDATE tbl_packages u set u.availability =:availability where u.destination =:name AND u.date =:date")
//    CatalogModel updateByNameAndDate(@Param("availability") Integer availability, @Param("name") String name, @Param("date") Date date);

}
