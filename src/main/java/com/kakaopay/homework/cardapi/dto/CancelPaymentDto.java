package com.kakaopay.homework.cardapi.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;

@Data
@NoArgsConstructor
public class CancelPaymentDto {
    @NotBlank @NotNull(message = "취소금액은 필수값 입니다.")
    @Pattern(message = "취소금액은 3-10자리여야 합니다.",regexp="^[0-9]{3,10}$")
    String price;           // 결재가격
    String vat;             // 부가가치세

    @Builder
    public CancelPaymentDto(String price, String vat){
        this.price = price;
        this.vat = vat;
    }
}
