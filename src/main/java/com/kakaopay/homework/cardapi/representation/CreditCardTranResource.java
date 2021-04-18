package com.kakaopay.homework.cardapi.representation;

import com.kakaopay.homework.cardapi.controller.PaymentController;
import com.kakaopay.homework.cardapi.model.CreditCardTran;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class CreditCardTranResource extends EntityModel<CreditCardTran> {
    public CreditCardTranResource(CreditCardTran content, Link... links) {
        super(content, links);
        add(linkTo(PaymentController.class).slash(content.getPaymentId()).withSelfRel());
    }
}
