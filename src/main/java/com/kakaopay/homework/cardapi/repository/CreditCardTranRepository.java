package com.kakaopay.homework.cardapi.repository;

import com.kakaopay.homework.cardapi.model.CreditCardTran;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CreditCardTranRepository  extends JpaRepository<CreditCardTran, String > {
}
