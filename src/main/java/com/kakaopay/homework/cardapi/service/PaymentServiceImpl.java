package com.kakaopay.homework.cardapi.service;

import com.kakaopay.homework.cardapi.dto.*;
import com.kakaopay.homework.cardapi.exception.InvalidRequestException;
import com.kakaopay.homework.cardapi.exception.NotFoundException;
import com.kakaopay.homework.cardapi.model.CreditCardTran;
import com.kakaopay.homework.cardapi.model.Payment;
import com.kakaopay.homework.cardapi.repository.CreditCardTranRepository;
import com.kakaopay.homework.cardapi.repository.PaymentRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Resource
    PaymentRepository paymentRepository;

    @Resource
    CreditCardTranRepository creditCardTranRepository;

    @Override
    public CreditCardTran doPayment(PaymentReqDto paymentReq) {
        Payment payment;
        try{
            payment = Payment.builder()
                        .cardInfo(paymentReq.getCardNumber()
                                +"/"+paymentReq.getExpirationDate()
                                +"/"+paymentReq.getCvc())
                        .installments(Integer.parseInt(paymentReq.getInstallments()))
                        .price(Integer.parseInt(paymentReq.getPrice()))
                        .vat(calcVat(paymentReq.getPrice(), paymentReq.getVat()))
                        .paymentType(PaymentType.PAYMENT).build();
        } catch (Exception e){
            e.printStackTrace();
            throw new InvalidRequestException("숫자이외의 값은 허용되지 않습니다.");
        }

        // 결재정보 저장
        paymentRepository.save(payment);

        // 신용카드 회사 전송
        return creditCardTranRepository.save(CreditCardTran.builder()
                .paymentId(payment.getPaymentId()).transactionInfo(convertTransactionString(payment)).build());
    }

    @Override
    public CreditCardTran cancelPayment(CancelPaymentDto paymentReq, String paymentId) {
        Payment findPayment = paymentRepository.findById(paymentId)
                .orElseThrow(()->new NotFoundException(paymentId));

        validateReq(paymentReq, findPayment);

        Payment canceledPayment = paymentRepository.save(Payment.builder()
                .paymentType(PaymentType.CANCEL)
                .cardInfo(findPayment.getCardInfo())
                .price(findPayment.getPrice())
                .installments(findPayment.getInstallments())
                .vat(findPayment.getVat())
                .parent(findPayment)
                .build());

        return creditCardTranRepository.save(CreditCardTran.builder()
                .paymentId(canceledPayment.getPaymentId())
                .transactionInfo(convertTransactionString(canceledPayment)).build());
    }

    @Override
    public PaymentResDto getPayment(String id) {
        Payment findPayment = paymentRepository.findById(id).orElseThrow(()->new NotFoundException(id));

        String[] cardInfoArr = findPayment.getCardInfo().split("/");

        PaymentResDto returnPayment = PaymentResDto.builder()
                .paymentId(findPayment.getPaymentId())
                .cardInfo(new CreditCard(maskingCardNumber(cardInfoArr[0]),Integer.parseInt(cardInfoArr[1]),Integer.parseInt(cardInfoArr[2])))
                .paymentPrice(new PaymentPrice(findPayment.getPrice(), findPayment.getVat()))
                .paymentType(findPayment.getPaymentType())
                .build();

        if( findPayment.getPaymentType().equals(PaymentType.CANCEL) ){
            returnPayment.setOriginPaymentId(findPayment.getParent().getPaymentId());
        }

        return returnPayment;
    }

    private void validateReq(CancelPaymentDto cancelPaymentDto, Payment findPayment) {
        try{
            if( findPayment.getPaymentType().equals(PaymentType.CANCEL) ){
                throw new InvalidRequestException("취소거래를 취소할 순 없습니다.");
            }
            if( findPayment.getChildren() != null && findPayment.getChildren().size() > 0 ){
                throw new InvalidRequestException("이미 취소된 거래입니다.");
            }
            if( findPayment.getPrice() != Integer.parseInt(cancelPaymentDto.getPrice()) ){
                throw new InvalidRequestException("원거래 결재금액과 동일한 금액이 입력되어야 합니다. 현재 부분취소는 지원되지 않습니다.");
            }
            if( !StringUtils.isEmpty(cancelPaymentDto.getVat())
                    && findPayment.getVat() != Integer.parseInt(cancelPaymentDto.getVat()) ){
                throw new InvalidRequestException("원거래 부가세와 동일한 금액이 입력되어야 합니다. 현재 부분취소는 지원되지 않습니다.");
            }
        } catch (NumberFormatException e){
            throw new InvalidRequestException("잘못된 요청입니다.");
        }
    }

    public String convertTransactionString(Payment payment) {
        String rtnString = String.format("%-10s%-20s",payment.getPaymentType(),payment.getPaymentId())+payment.toString();
        rtnString = String.format("%4d",rtnString.length()) + rtnString;
        return rtnString;
    }

    public int calcVat(String price, String vat){
        int returnVat;
        double dPrice = Double.valueOf(price);

        if(StringUtils.isEmpty(vat)){
            returnVat = Math.toIntExact(Math.round(dPrice/11));
        } else {
            returnVat = Integer.parseInt(vat);
        }
        return returnVat;
    }

    private String maskingCardNumber(String cardNumber) {
        StringBuilder rtnString = new StringBuilder(cardNumber.substring(0, 6));
        for(int i=5; i<cardNumber.length()-3; i++){
            rtnString.append("*");
        }
        rtnString.append(cardNumber.substring(cardNumber.length() - 3));
        return rtnString.toString();
    }
}
