package com.p2plending.payment.service;

import com.p2plending.payment.dto.PaymentUpdateDto;
import com.p2plending.payment.db.catalogdb.model.CatalogModel;
import com.p2plending.payment.dto.PaymentReqBorrowerDto;
import com.p2plending.payment.db.paymentdb.model.PaymentModel;
import com.p2plending.payment.db.borrowerdb.model.BorrowerModel;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface IPaymentService {
//    public PaymentModel createPayment(PaymentCreateDto paymentCreateDto);
    public Boolean checkPin(PaymentReqBorrowerDto paymentReqBorrowerDto);
    public Map<String, Object> createPaymentSuccess(PaymentReqBorrowerDto paymentReqBorrowerDto) throws ParseException;
    public Map<String, Object> createPaymentFailed(PaymentReqBorrowerDto paymentReqBorrowerDto) throws ParseException;
    public PaymentModel updatePayment(PaymentUpdateDto paymentUpdateDto, Integer id) throws ParseException;
    public Map<String, Object> deletePayment(Integer id);
    public PaymentModel getPayment(Integer id);
    public List<PaymentModel> getAllPayment();
    public List<BorrowerModel> getAllUser();
    public List<CatalogModel> getAllCatalog();
}
