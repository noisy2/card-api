package com.kakaopay.homework.cardapi.controller;

import com.kakaopay.homework.cardapi.dto.CancelPaymentDto;
import com.kakaopay.homework.cardapi.dto.PaymentReqDto;
import com.kakaopay.homework.cardapi.dto.PaymentResDto;
import com.kakaopay.homework.cardapi.exception.InvalidRequestException;
import com.kakaopay.homework.cardapi.model.CreditCardTran;
import com.kakaopay.homework.cardapi.representation.CreditCardTranResource;
import com.kakaopay.homework.cardapi.representation.PaymentResDtoResource;
import com.kakaopay.homework.cardapi.service.PaymentService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigInteger;
import java.net.URI;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/api/payment", produces = MediaTypes.HAL_JSON_VALUE)
@AllArgsConstructor
public class PaymentController {

    @Resource
    private PaymentService paymentService;

    @PostMapping
    public ResponseEntity<?> savePayment(@Valid @RequestBody PaymentReqDto paymentReq) {
        validatePaymentReqDto(paymentReq);

        CreditCardTran creditCardTran = paymentService.doPayment(paymentReq);

        CreditCardTranResource cardTranResource = new CreditCardTranResource(creditCardTran);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(PaymentController.class).slash(creditCardTran.getPaymentId());
        URI createdUri = selfLinkBuilder.toUri();
        cardTranResource.add(linkTo(PaymentController.class).slash(creditCardTran.getPaymentId()).withRel("cancel-payment"));
        cardTranResource.add(new Link("/docs/index.html#resources-payment-create").withRel("profile"));
        return ResponseEntity.created(createdUri).body(cardTranResource);
    }

    @GetMapping(value = "/{paymentId}")
    public ResponseEntity<?> getOnePayment(@PathVariable String paymentId) {
        validatePaymentId(paymentId);
        PaymentResDto findPayment = paymentService.getPayment(paymentId);

        PaymentResDtoResource paymentResource = new PaymentResDtoResource(findPayment);
        paymentResource.add(linkTo(PaymentController.class).slash(findPayment.getPaymentId()).withRel("cancel-payment"));
        paymentResource.add(new Link("/docs/index.html#resources-payment-get").withRel("profile"));
        return ResponseEntity.ok(paymentResource);
    }

    @PutMapping(value = "/{paymentId}")
    public ResponseEntity<?> cancelPayment(@PathVariable String paymentId
            , @Valid @RequestBody CancelPaymentDto cancelPaymentDto) {
        validatePaymentId(paymentId);

        CreditCardTran creditCardTran = paymentService.cancelPayment(cancelPaymentDto, paymentId);
        CreditCardTranResource cardTranResource = new CreditCardTranResource(creditCardTran);
        WebMvcLinkBuilder selfLinkBuilder = linkTo(PaymentController.class).slash(creditCardTran.getPaymentId());
        URI createdUri = selfLinkBuilder.toUri();
        cardTranResource.add(linkTo(PaymentController.class).slash(creditCardTran.getPaymentId()).withRel("cancel-payment"));
        cardTranResource.add(new Link("/docs/index.html#resources-payment-cancel").withRel("profile"));
        return ResponseEntity.created(createdUri).body(cardTranResource);
    }

    private void validatePaymentId(String paymentId) {
        try {
            if( (new BigInteger(paymentId)).toString().length() != 20 ){
                throw new InvalidRequestException("id는 20자리 정수여야 합니다.");
            }
        } catch (NumberFormatException e){
            throw new InvalidRequestException("id는 20자리 정수여야 합니다.");
        }
    }

    private void validatePaymentReqDto(PaymentReqDto paymentReq) {
        try{
            if( Integer.parseInt(paymentReq.getPrice()) < 100 || Integer.parseInt(paymentReq.getPrice()) > 1000000000 ){
                throw new InvalidRequestException("결재금액은 100원이상 10억이하여야 합니다.");
            }
            if(!StringUtils.isEmpty(paymentReq.getVat())){
                if( Integer.parseInt(paymentReq.getPrice()) < Integer.parseInt(paymentReq.getVat()) ){
                    throw new InvalidRequestException("결재금액이 부가세보다 작을 수 없습니다.");
                }
                if( Integer.parseInt(paymentReq.getVat()) == 0 && Integer.parseInt(paymentReq.getPrice()) != 1000 ){
                    throw new InvalidRequestException("부가세는 0원일 수 없습니다.");
                }
                if( Integer.parseInt(paymentReq.getVat()) < 0 ){
                    throw new InvalidRequestException("부가세는 0원보다 작을 수 없습니다.");
                }
            }
        } catch (NumberFormatException e){
            throw new InvalidRequestException("잘못된 요청입니다.");
        }
    }
}
