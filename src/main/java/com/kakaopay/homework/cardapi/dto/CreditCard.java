package com.kakaopay.homework.cardapi.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreditCard {
    String cardNumber;
    int expirationDate;
    int cvc;
}
