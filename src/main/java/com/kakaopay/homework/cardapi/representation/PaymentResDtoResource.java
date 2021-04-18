package com.kakaopay.homework.cardapi.representation;

import com.kakaopay.homework.cardapi.controller.PaymentController;
import com.kakaopay.homework.cardapi.dto.PaymentResDto;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class PaymentResDtoResource extends EntityModel<PaymentResDto> {
    public PaymentResDtoResource(PaymentResDto content, Link... links){
        super(content, links);
        add(linkTo(PaymentController.class).slash(content.getPaymentId()).withSelfRel());
    }
}
