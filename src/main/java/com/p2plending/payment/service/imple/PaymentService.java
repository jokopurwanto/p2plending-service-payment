package com.p2plending.payment.service.imple;

import com.p2plending.payment.db.fundingloandb.model.FundingLoanModel;
import com.p2plending.payment.db.fundingloandb.repository.FundingLoanRepository;
import com.p2plending.payment.db.lenderdb.model.LenderModel;
import com.p2plending.payment.db.lenderdb.repository.LenderRepository;
import com.p2plending.payment.db.paymentdb.model.PaymentModel;
import com.p2plending.payment.db.paymentdb.repository.PaymentRepository;
import com.p2plending.payment.db.borrowerdb.model.BorrowerModel;
import com.p2plending.payment.db.borrowerdb.repository.BorrowerRepository;
import com.p2plending.payment.db.productdb.model.ProductModel;
import com.p2plending.payment.db.productdb.repository.ProductRepository;
import com.p2plending.payment.dto.PaymentReqBorrowerDto;
import com.p2plending.payment.dto.PaymentReqLenderDto;
import com.p2plending.payment.dto.PaymentUpdateDto;
import com.p2plending.payment.dto.notificationdto.NotificationBorrowerDto;
import com.p2plending.payment.handler.PaymentNotFoundException;
import com.p2plending.payment.service.IPaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class PaymentService implements IPaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    BorrowerRepository borrowerRepository;

    @Autowired
    LenderRepository lenderRepository;

    @Autowired
    FundingLoanRepository fundingLoanRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public Boolean checkPinBorrower(PaymentReqBorrowerDto paymentReqBorrowerDto) {
        BorrowerModel borrowerModel = borrowerRepository.findById(paymentReqBorrowerDto.getIdBorrower()).get();
        if(!borrowerModel.getPin().equals(paymentReqBorrowerDto.getPin())){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public Boolean checkPinLender(PaymentReqLenderDto paymentReqLenderDto) {
        LenderModel lenderModel = lenderRepository.findById(paymentReqLenderDto.getIdLender()).get();
        if(!lenderModel.getPin().equals(paymentReqLenderDto.getPin())){
            return false;
        }else {
            return true;
        }
    }

    @Override
    public PaymentModel createPaymentSuccessBorrower(PaymentReqBorrowerDto paymentReqBorrowerDto) throws ParseException {

        //get data time period
        ProductModel productModel = productRepository.findById(paymentReqBorrowerDto.getIdProduct()).get();
        System.out.println("Total time period : "+productModel.getTimePeriod());
        Integer interest = productModel.getInterest();
        Integer timePeriod = productModel.getTimePeriod();
        System.out.println("Interest : "+ productModel.getInterest());
        System.out.println("Interest : "+ interest);

        //get data borrower
        BorrowerModel borrowerModel = borrowerRepository.findById(paymentReqBorrowerDto.getIdBorrower()).get();
        System.out.println("Saldo Borrower : "+borrowerModel.getBalance());

        //get data funding loan
        FundingLoanModel fundingLoanModel[] = fundingLoanRepository.findByIdBorrowerAndIdProduct(paymentReqBorrowerDto.getIdBorrower(),paymentReqBorrowerDto.getIdProduct());
        System.out.println("Total Index Array : "+fundingLoanModel.length);
//        System.out.println(fundingLoanModel[0].getTitle());
//        System.out.println(fundingLoanModel[1].getTitle());


        //insert payment
        LocalDate localDate = LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        PaymentModel paymentModel = PaymentModel.builder()
                .totalPrice(paymentReqBorrowerDto.getTotalPayment())
                .date(sqlDate)
                .paymentType("Balance")
                .status(true)
                .build();
        PaymentModel paymentMdl = paymentRepository.saveAndFlush(paymentModel);


        Integer counter = 0;
        while (counter < fundingLoanModel.length) {
            System.out.println("Loop ke-"+counter+" : "+fundingLoanModel[counter].getTitle());
            Integer totalInterest,totalPayment, debit, kredit = 0;
            totalInterest = (fundingLoanModel[counter].getAmount() * interest * timePeriod / 100) / 12;
            totalPayment = fundingLoanModel[counter].getAmount() + totalInterest;
            System.out.println("Total Interest : "+totalInterest);
            System.out.println("Total Payment : "+totalPayment);

            //debit borrower
            debit = borrowerModel.getBalance() - totalPayment;
            borrowerModel.setBalance(debit);
            borrowerRepository.save(borrowerModel);

            //kredit lender
            LenderModel lenderModel = lenderRepository.findById(fundingLoanModel[counter].getIdLender()).get();
            kredit = lenderModel.getBalance() + totalPayment;
            lenderModel.setBalance(kredit);
            lenderRepository.save(lenderModel);

            //update status funding loan
            FundingLoanModel fundingLoanMdl = fundingLoanRepository.findById(fundingLoanModel[counter].getId()).get();
            fundingLoanMdl.setStatus("Success");

            //update id payment funding loan
            fundingLoanMdl.setIdBorrowerPayment(paymentMdl.getId());
            fundingLoanRepository.save(fundingLoanMdl);

            counter++;
        }

        //start hit service notification
        //pre req body post
        NotificationBorrowerDto reqBody = NotificationBorrowerDto.builder()
                .username(borrowerModel.getFirstName())
                .product(productModel.getProductTitle())
                .totalPayment(paymentReqBorrowerDto.getTotalPayment())
                .email(borrowerModel.getEmail())
                .build();

        String username = "joko";
        String password = "joko";
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setBasicAuth(username, password);
        HttpEntity<NotificationBorrowerDto> httpEntity = new HttpEntity<>(reqBody, httpHeaders);

        //hit service notification
        NotificationBorrowerDto notificationBorrowerDto = restTemplate.postForObject("http://localhost:8084/api/notification/borrower",httpEntity, NotificationBorrowerDto.class);
        System.out.println(notificationBorrowerDto.getData().getId());
        System.out.println(notificationBorrowerDto.getData().getStatus());
        System.out.println(notificationBorrowerDto.getMessage());
        //end hit service notifications


        return paymentMdl;
    }

    @Override
    public PaymentModel createPaymentSuccessLender(PaymentReqLenderDto paymentReqLenderDto) throws ParseException {
        //get data
        ProductModel productModel = productRepository.findById(paymentReqLenderDto.getIdProduct()).get();
        System.out.println("Total time period : "+productModel.getTimePeriod());
        Integer amount = Integer.valueOf(productModel.getLoanAmount());
        Integer totalBill = Integer.valueOf(paymentReqLenderDto.getTotalPayment());
        System.out.println("amount : "+ productModel.getLoanAmount());

        //update remain remainingAmount
        Integer remainingAmount = Integer.valueOf(productModel.getRemainingReqAmount());
        Integer remainingAmountTmp = amount - totalBill;
        Integer totalRemaining = remainingAmount + remainingAmountTmp;
        productModel.setRemainingReqAmount(Integer.toString(totalRemaining));

        //get data lender
        LenderModel lenderModel = lenderRepository.findById(paymentReqLenderDto.getIdLender()).get();
        System.out.println("Saldo Lender : "+lenderModel.getBalance());

        //get data funding loan
        FundingLoanModel fundingLoanModel = fundingLoanRepository.findByIdLenderAndIdProduct(paymentReqLenderDto.getIdLender(),paymentReqLenderDto.getIdProduct());
        System.out.println(fundingLoanModel.getTitle());


        //insert payment
        LocalDate localDate = LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        PaymentModel paymentModel = PaymentModel.builder()
                .totalPrice(paymentReqLenderDto.getTotalPayment())
                .date(sqlDate)
                .paymentType("Balance")
                .status(true)
                .build();
        PaymentModel paymentMdl = paymentRepository.saveAndFlush(paymentModel);


        Integer debit, kredit =0;

        //debit lender
        debit = lenderModel.getBalance() - totalBill;
        lenderModel.setBalance(debit);
        lenderRepository.save(lenderModel);

        //kredit borrower
        BorrowerModel borrowerModel = borrowerRepository.findById(fundingLoanModel.getIdBorrower()).get();
        kredit = borrowerModel.getBalance() + totalBill;
        borrowerModel.setBalance(kredit);
        borrowerRepository.save(borrowerModel);

        //update status funding loan
        fundingLoanModel.setStatus("IN PROGRESS");

        //update id payment funding loan
        fundingLoanModel.setIdLenderPayment(paymentMdl.getId());

        //update amount
        fundingLoanModel.setAmount(Integer.valueOf(paymentReqLenderDto.getTotalPayment()));
        fundingLoanRepository.save(fundingLoanModel);

        //update remainingAmount
        productRepository.save(productModel);

//        //start hit service notification
//        //pre req body post
//        NotificationBorrowerDto reqBody = NotificationBorrowerDto.builder()
//                .username(borrowerModel.getFirstName())
//                .product(productModel.getProductTitle())
//                .totalPayment(paymentReqBorrowerDto.getTotalPayment())
//                .email(borrowerModel.getEmail())
//                .build();
//
//        String username = "joko";
//        String password = "joko";
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//        httpHeaders.setBasicAuth(username, password);
//        HttpEntity<NotificationBorrowerDto> httpEntity = new HttpEntity<>(reqBody, httpHeaders);
//
//        //hit service notification
//        NotificationBorrowerDto notificationBorrowerDto = restTemplate.postForObject("http://localhost:8084/api/notification/borrower",httpEntity, NotificationBorrowerDto.class);
//        System.out.println(notificationBorrowerDto.getData().getId());
//        System.out.println(notificationBorrowerDto.getData().getStatus());
//        System.out.println(notificationBorrowerDto.getMessage());
//        //end hit service notifications

        return paymentMdl;
    }

    @Override
    public Map<String, Object> createPaymentFailedBorrower(PaymentReqBorrowerDto paymentReqBorrowerDto) throws ParseException {
        Map<String,Object> response = new LinkedHashMap<>();
        response.put("paymentStatus", false);
        response.put("reason","PIN tidak sama");
        return response;
    }

    @Override
    public Map<String, Object> createPaymentFailedLender(PaymentReqLenderDto paymentReqLenderDto) throws ParseException {
        Map<String,Object> response = new LinkedHashMap<>();
        response.put("paymentStatus", false);
        response.put("reason","PIN tidak sama");
        return response;
    }

    @Override
    public Map<String, Object> billCheck(Integer idBorrower, Integer idProduct) throws ParseException {
        //get data funding loan
        FundingLoanModel fundingLoanModel[] = fundingLoanRepository.findByIdBorrowerAndIdProduct(idBorrower,idProduct);
        System.out.println("Total Index Array : "+fundingLoanModel.length);
//        System.out.println(fundingLoanModel[0].getTitle());
//        System.out.println(fundingLoanModel[1].getTitle());

        //get data time period
        ProductModel productModel = productRepository.findById(idProduct).get();
        System.out.println("Total time period : "+productModel.getTimePeriod());
        Integer interest = productModel.getInterest();
        Integer timePeriod = productModel.getTimePeriod();
        System.out.println("Interest : "+ productModel.getInterest());
        System.out.println("Interest : "+ interest);

        Integer totalBill = 0;
        Integer counter = 0;
        while (counter < fundingLoanModel.length) {
            System.out.println("Loop ke-"+counter+" : "+fundingLoanModel[counter].getTitle());
            Integer totalInterest,totalLoan = 0;
            totalInterest = (fundingLoanModel[counter].getAmount() * interest * timePeriod / 100) / 12;
            totalLoan = fundingLoanModel[counter].getAmount() + totalInterest;
            System.out.println("Total Interest : "+totalInterest);
            System.out.println("Total Payment : "+totalLoan);
            if (!fundingLoanModel[counter].getStatus().equals("Success")){
                totalBill = totalBill + totalLoan;
            } else {
                totalBill = totalBill + 0;
            }

            counter++;
        }

        Map<String,Object> response = new LinkedHashMap<>();
        response.put("product",productModel.getProductTitle());
        response.put("dueDate",productModel.getLoanDueDate());
        response.put("totalBill", totalBill);
        return response;
    }

    @Override
    public PaymentModel updatePayment(PaymentUpdateDto paymentUpdateDto, Integer id){
        //insert payment
        LocalDate localDate = LocalDate.now();
        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
        PaymentModel paymentModel = PaymentModel.builder()
                .id(id)
                .totalPrice(paymentUpdateDto.getTotalPrice())
                .date(paymentUpdateDto.getDate())
                .paymentType(paymentUpdateDto.getPayment_type())
                .status(true)
                .build();
        return paymentRepository.saveAndFlush(paymentModel);
    }

    @Override
    public Map<String, Object> deletePayment(Integer id) {

        if(paymentRepository.findById(id).isEmpty())
            throw new PaymentNotFoundException("Data yang dicari tidak ditemukan");

        PaymentModel paymentModel = paymentRepository.findById(id).get();
        Map<String,Object> response = new HashMap<>();
        response.put("id", id);
        response.put("payment", paymentModel.getTotalPrice());
        response.put("paymentType", paymentModel.getPaymentType());
        response.put("createdAt",paymentModel.getDate());
        paymentRepository.deleteById(id);
        return response;
    }

    @Override
    public PaymentModel getPayment(Integer id) {
        if(paymentRepository.findById(id).isEmpty())
            throw new PaymentNotFoundException("Data yang dicari tidak ditemukan");
        return paymentRepository.findById(id).get();
    }

    @Override
    public List<PaymentModel> getAllPayment() {
        return paymentRepository.findAll();
    }

}
