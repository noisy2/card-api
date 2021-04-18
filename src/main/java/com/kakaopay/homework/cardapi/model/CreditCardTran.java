package com.kakaopay.homework.cardapi.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@NoArgsConstructor
@Entity
@Getter
public class CreditCardTran {
    @Id
    private String paymentId;
    @Column(length = 450)
    private String transactionInfo;

    @Builder
    public CreditCardTran(String paymentId, String transactionInfo){
        this.paymentId = paymentId;
        this.transactionInfo = transactionInfo;
    }
}
