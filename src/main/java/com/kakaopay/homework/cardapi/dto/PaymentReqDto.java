package com.kakaopay.homework.cardapi.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
public class PaymentReqDto{
    @NotBlank(message = "카드번호는 필수값 입니다.")
    @Pattern(message = "카드번호는 10-16자리여야 합니다.",regexp = "^[0-9]{10,16}$")
    String cardNumber;      // 카드정보

    @NotBlank(message = "카드 유효기간은 필수값 입니다.")
    @Pattern(message = "카드 유효기간은 4자리여야 합니다.",regexp = "^[0-9]{4}$")
    String expirationDate;  // 유효기간

    @NotBlank(message = "카드 cvc번호는 필수값 입니다.")
    @Pattern(message = "카드번호는 3자리여야 합니다.",regexp = "^[0-9]{3}$")
    String cvc;

    @NotBlank(message = "할부정보는 필수값 입니다.")
    @Pattern(message = "할부정보는 2자리여야 합니다.",regexp = "[0-9]|[1][0-2]$")
    String installments;    // 할부정보

    @NotBlank(message = "결재가격은 필수값 입니다.")
    @Pattern(message = "결재가격은 3-10자리여야 합니다.",regexp="^[0-9]{3,10}$")
    String price;           // 결재가격
    String vat;             // 부가가치세


    @Builder
    public PaymentReqDto(String cardNumber, String expirationDate
            , String cvc, String installments, String price, String vat){
        this.cardNumber = cardNumber;
        this.expirationDate = expirationDate;
        this.cvc = cvc;
        this.installments = installments;
        this.price = price;
        this.vat = vat;
    }
}
