package com.p2plending.payment.service;

import com.p2plending.payment.dto.PaymentBillCheckDto;
import com.p2plending.payment.dto.PaymentReqLenderDto;
import com.p2plending.payment.dto.PaymentUpdateDto;
import com.p2plending.payment.db.lenderdb.model.LenderModel;
import com.p2plending.payment.dto.PaymentReqBorrowerDto;
import com.p2plending.payment.db.paymentdb.model.PaymentModel;
import com.p2plending.payment.db.borrowerdb.model.BorrowerModel;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface IPaymentService {
//    public PaymentModel createPayment(PaymentCreateDto paymentCreateDto);
    public Boolean checkPinBorrower(PaymentReqBorrowerDto paymentReqBorrowerDto);
    public Boolean checkPinLender(PaymentReqLenderDto paymentReqLenderDto);
    public PaymentModel createPaymentSuccessBorrower(PaymentReqBorrowerDto paymentReqBorrowerDto) throws ParseException;
    public PaymentModel createPaymentSuccessLender(PaymentReqLenderDto paymentReqLenderDto) throws ParseException;
    public Map<String, Object> createPaymentFailedBorrower(PaymentReqBorrowerDto paymentReqBorrowerDto) throws ParseException;
    public Map<String, Object> createPaymentFailedLender(PaymentReqLenderDto paymentReqLenderDto) throws ParseException;
    public PaymentModel updatePayment(PaymentUpdateDto paymentUpdateDto, Integer id) throws ParseException;
    public Map<String, Object> billCheck(Integer idBorrower, Integer idProduct) throws ParseException;
    public Map<String, Object> deletePayment(Integer id);
    public PaymentModel getPayment(Integer id);
    public List<PaymentModel> getAllPayment();
    public List<BorrowerModel> getAllUser();
    public List<LenderModel> getAllCatalog();
}
