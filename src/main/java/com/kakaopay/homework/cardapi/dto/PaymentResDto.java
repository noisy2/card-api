package com.kakaopay.homework.cardapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

@Getter
@Setter
public class PaymentResDto {
    String paymentId;
    CreditCard cardInfo;
    PaymentType paymentType;
    PaymentPrice paymentPrice;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String originPaymentId;    // 원거래번호 (취소시 )
    @Builder
    public PaymentResDto(String paymentId, CreditCard cardInfo, PaymentType paymentType
            , PaymentPrice paymentPrice, String originPaymentId){
        this.paymentId = paymentId;
        this.cardInfo = cardInfo;
        this.paymentType = paymentType;
        this.paymentPrice = paymentPrice;
        this.originPaymentId = originPaymentId;
    }
}
