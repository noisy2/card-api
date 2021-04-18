package com.kakaopay.homework.cardapi.service;

import com.kakaopay.homework.cardapi.dto.CancelPaymentDto;
import com.kakaopay.homework.cardapi.dto.PaymentReqDto;
import com.kakaopay.homework.cardapi.dto.PaymentResDto;
import com.kakaopay.homework.cardapi.model.CreditCardTran;

public interface PaymentService {
    CreditCardTran doPayment(PaymentReqDto paymentReq);
    CreditCardTran cancelPayment(CancelPaymentDto paymentReq, String paymentId);

    PaymentResDto getPayment(String id);
}
