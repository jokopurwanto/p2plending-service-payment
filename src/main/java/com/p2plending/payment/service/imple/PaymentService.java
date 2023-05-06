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
import com.p2plending.payment.dto.PaymentBillCheckDto;
import com.p2plending.payment.dto.PaymentReqBorrowerDto;
import com.p2plending.payment.dto.PaymentUpdateDto;
import com.p2plending.payment.service.IPaymentService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        System.out.println(fundingLoanModel[0].getTitle());
        System.out.println(fundingLoanModel[1].getTitle());


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


        return paymentMdl;
    }

    @Override
    public Map<String, Object> createPaymentFailed(PaymentReqBorrowerDto paymentReqBorrowerDto) throws ParseException {
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
        System.out.println(fundingLoanModel[0].getTitle());
        System.out.println(fundingLoanModel[1].getTitle());

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
    public PaymentModel updatePayment(PaymentUpdateDto paymentUpdateDto, Integer id) throws ParseException {
        return null;
    }



    @Override
    public Map<String, Object> deletePayment(Integer id) {
        return null;
    }

    @Override
    public PaymentModel getPayment(Integer id) {
        return null;
    }

    @Override
    public List<PaymentModel> getAllPayment() {
        return null;
    }

    @Override
    public List<BorrowerModel> getAllUser() {
        return null;
    }

    @Override
    public List<LenderModel> getAllCatalog() {
        return null;
    }

//    @Override
//    public PaymentModel createPayment(PaymentCreateDto paymentCreateDto) {
//        PaymentModel paymentModel = PaymentModel.builder()
//                .payment_type(paymentCreateDto.getPayment_type())
//                .totalPrice(paymentCreateDto.getTotalPrice())
//                .status(paymentCreateDto.isStatus())
//                .date(paymentCreateDto.getDate())
//                .build();
//        return paymentRepository.save(paymentModel);
//    }

//    @Override
//    public Boolean checkPin(PaymentReqDto paymentReqDto) {
//        UserModel userMdl = userRepository.findById(paymentReqDto.getIdUser()).get();
//        System.out.println("data pin user model : "+userMdl.getPin());
//        System.out.println("data pin input body : "+paymentReqDto.getPin());
//        if(!userMdl.getPin().equals(paymentReqDto.getPin())){
//            return false;
//        }else {
//            return true;
//        }
//    }
//
//    @Override
//    public Map<String, Object> createPaymentSuccess(PaymentReqDto paymentReqDto) throws ParseException {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate endDate = LocalDate.parse(paymentReqDto.getEndDate().toString(), formatter);
//        LocalDate startDate = LocalDate.parse(paymentReqDto.getStartDate().toString(), formatter);
//        long days = ChronoUnit.DAYS.between(startDate, endDate);
//        LocalDate localDate = LocalDate.now();
//        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
//        PaymentModel paymentModel = PaymentModel.builder()
//                .paymentType(paymentReqDto.getPaymentType())
//                .totalPrice(paymentReqDto.getTotalPrice())
//                .status(true)
//                .date(sqlDate)
//                .build();
//        PaymentModel paymentMdl = paymentRepository.saveAndFlush(paymentModel);
//
//
//        //start hit service notification
//        //pre req body post
//        NotificationPostDto reqBody = NotificationPostDto.builder()
//                .idOrder(paymentReqDto.getIdOrder())
//                .idUser(paymentReqDto.getIdUser())
//                .destination(paymentReqDto.getDestination())
//                .startDate(paymentReqDto.getStartDate())
//                .endDate(paymentReqDto.getEndDate())
//                .totalPerson(paymentReqDto.getTotalPerson())
//                .totalPrice(paymentReqDto.getTotalPrice())
//                .build();
//
//        String username = "joko";
//        String password = "joko";
//        HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
//        httpHeaders.setBasicAuth(username, password);
//        HttpEntity<NotificationPostDto> httpEntity = new HttpEntity<>(reqBody, httpHeaders);
//
//        //hit service notification
//        NotificationPostDto notificationPostDto = restTemplate.postForObject("http://localhost:8084/api/notification",httpEntity, NotificationPostDto.class);
//        System.out.println(notificationPostDto.getData().getId());
//        System.out.println(notificationPostDto.getData().getStatus());
//        System.out.println(notificationPostDto.getMessage());
//        //end hit service notifications
//
//        Map<String,Object> response = new LinkedHashMap<>();
//        response.put("id",paymentMdl.getId());
//        response.put("paymentStatus", paymentMdl.isStatus());
//        response.put("paymentType",paymentMdl.getPaymentType());
//        response.put("totalPrice", paymentReqDto.getTotalPrice());
//        response.put("totalPerson", paymentReqDto.getTotalPerson());
//        response.put("totalDays", days);
//        response.put("startDate", startDate);
//        response.put("endDate", endDate);
//        response.put("idOrder",paymentReqDto.getIdOrder());
//        response.put("idNotification",notificationPostDto.getData().getId());
//        return response;
//    }
//
//    @Override
//    public Map<String, Object> createPaymentFailed(PaymentReqDto paymentReqDto) throws ParseException {
////        //convert date
////        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
////        LocalDate endDate = LocalDate.parse(paymentReqDto.getEndDate().toString(), formatter);
////        LocalDate startDate = LocalDate.parse(paymentReqDto.getStartDate().toString(), formatter);
////        long days = ChronoUnit.DAYS.between(startDate, endDate);
////        Integer availbility, addAvailbility;
////        LocalDate date = startDate;
////        Map<String,Object> response = new LinkedHashMap<>();
////
////        //total return availbility
////        availbility = Math.toIntExact(paymentReqDto.getTotalPerson() * days);
////
////        //loop start to end date
////        while (date.isBefore(endDate) || date.equals(endDate)) {
////
////            //convert date
////            Date dateTmp = new SimpleDateFormat("yyyy-MM-dd").parse(date.toString());
////
////            //create object catalog model
////            CatalogModel catalogMdl = (CatalogModel) catalogRepository.findByNameAndDate(paymentReqDto.getDestination(), dateTmp);
////
////            //add current avail + total return availbility
////            addAvailbility = catalogMdl.getAvailability() + availbility;
////
////            //update data
////            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
////            CatalogModel catalogMdlSave = CatalogModel.builder()
////                    .id(catalogMdl.getId())
////                    .name(catalogMdl.getName())
////                    .price(catalogMdl.getPrice())
////                    .availability(addAvailbility)
////                    .date(sqlDate)
////                    .build();
////            catalogRepository.save(catalogMdlSave);
////
////            //counter date plus 1
////            date = date.plusDays(1);
////        }
//
//        Map<String,Object> response = new LinkedHashMap<>();
//        response.put("paymentStatus", false);
//        response.put("reason","PIN tidak sama");
//        return response;
//    }
//
//    @Override
//    public Map<String, Object> createPayment(PaymentReqDto paymentReqDto) throws ParseException {
//
//        //convert date
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
//        LocalDate endDate = LocalDate.parse(paymentReqDto.getEndDate().toString(), formatter);
//        LocalDate startDate = LocalDate.parse(paymentReqDto.getStartDate().toString(), formatter);
//        long days = ChronoUnit.DAYS.between(startDate, endDate);
//        Integer availbility, addAvailbility;
//        LocalDate date = startDate;
//        Map<String,Object> response = new LinkedHashMap<>();
//
//        if(userRepository.findById(paymentReqDto.getIdUser()).isEmpty())
//            throw new PaymentNotFoundException("Data user yang dicari tidak ditemukan");
//
//        UserModel userMdl = userRepository.findById(paymentReqDto.getIdUser()).get();
//        System.out.println("data pin user model : "+userMdl.getPin());
//        System.out.println("data pin input body : "+paymentReqDto.getPin());
//        if(!userMdl.getPin().equals(paymentReqDto.getPin())){
//
//            //total return availbility
//            availbility = Math.toIntExact(paymentReqDto.getTotalPerson() * days);
//
//            //loop start to end date
//            while (date.isBefore(endDate) || date.equals(endDate)) {
//
//                //convert date
//                Date dateTmp = new SimpleDateFormat("yyyy-MM-dd").parse(date.toString());
//
//                //create object catalog model
//                CatalogModel catalogMdl = (CatalogModel) catalogRepository.findByNameAndDate(paymentReqDto.getDestination(), dateTmp);
//
//                //add current avail + total return availbility
//                addAvailbility = catalogMdl.getAvailability() + availbility;
//
//                //update data
//                java.sql.Date sqlDate = java.sql.Date.valueOf(date);
//                CatalogModel catalogMdlSave = CatalogModel.builder()
//                        .id(catalogMdl.getId())
//                        .name(catalogMdl.getName())
//                        .price(catalogMdl.getPrice())
//                        .availability(addAvailbility)
//                        .date(sqlDate)
//                        .build();
//                catalogRepository.save(catalogMdlSave);
//
//                //counter date plus 1
//                date = date.plusDays(1);
//            }
//
//            response.put("paymentStatus", false);
//            response.put("reason","PIN tidak sama");
//            return response;
//        }
//
//        LocalDate localDate = LocalDate.now();
//        java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
//        PaymentModel paymentModel = PaymentModel.builder()
//                .paymentType(paymentReqDto.getPaymentType())
//                .totalPrice(paymentReqDto.getTotalPrice())
//                .status(true)
//                .date(sqlDate)
//                .build();
//        PaymentModel paymentMdl = paymentRepository.saveAndFlush(paymentModel);
//
//        response.put("id",paymentMdl.getId());
//        response.put("paymentStatus", paymentMdl.isStatus());
//        response.put("paymentType",paymentMdl.getPaymentType());
//        response.put("totalPrice", paymentReqDto.getTotalPrice());
//        response.put("totalPerson", paymentReqDto.getTotalPerson());
//        response.put("totalDays", days);
//        response.put("startDate", startDate);
//        response.put("endDate", endDate);
//        return response;
//    }
//
//    @Override
//    public PaymentModel updatePayment(PaymentUpdateDto paymentUpdateDto, Integer id) {
//        if(paymentRepository.findById(id).isEmpty())
//            throw new PaymentNotFoundException("Data payment yang akan di-update tidak ditemukan");
//
//        PaymentModel paymentModel = PaymentModel.builder()
//                .id(id)
//                .paymentType(paymentUpdateDto.getPayment_type())
//                .totalPrice(paymentUpdateDto.getTotalPrice())
//                .date(paymentUpdateDto.getDate())
//                .status(paymentUpdateDto.isStatus())
//                .build();
//        paymentRepository.save(paymentModel);
//        return paymentRepository.findById(id).get();
//    }
//
//    @Override
//    public PaymentModel getPayment(Integer id) {
//        if(paymentRepository.findById(id).isEmpty())
//            throw new PaymentNotFoundException("Data yang dicari tidak ditemukan");
//        return paymentRepository.findById(id).get();
//    }
//
//    @Override
//    public List<PaymentModel> getAllPayment() {
//        return paymentRepository.findAll();
//    }
//
//    @Override
//    public List<UserModel> getAllUser() {
//        return userRepository.findAll();
//    }
//
//    @Override
//    public List<CatalogModel> getAllCatalog() {
//        return catalogRepository.findAll();
//    }
//
//    @Override
//    public Map<String, Object> deletePayment(Integer id) {
//        if(paymentRepository.findById(id).isEmpty())
//            throw new PaymentNotFoundException("Data yang dicari tidak ditemukan");
//
//        PaymentModel paymentModel = paymentRepository.findById(id).get();
//        Map<String,Object> response = new HashMap<>();
//        response.put("id", paymentModel.getId());
//        response.put("paymentType", paymentModel.getPaymentType());
//        response.put("totalPrice", paymentModel.getTotalPrice());
//        response.put("date",paymentModel.getDate());
//        response.put("status",paymentModel.isStatus());
//        paymentRepository.deleteById(id);
//        return response;
//    }

//    public List<PaymentModel> listAll(){
//        return paymentRepository.findAll();
//    }
//
//    public PaymentModel get(Integer id){
//        return paymentRepository.findById(id).get();
//    }
//
//    public void save(PaymentModel catalogModel){
//        paymentRepository.save(catalogModel);
//    }
//
//    public void delete(Integer id){
//        paymentRepository.deleteById(id);
//    }

}
