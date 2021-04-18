package com.kakaopay.homework.cardapi.service;

import com.kakaopay.homework.cardapi.dto.CancelPaymentDto;
import com.kakaopay.homework.cardapi.dto.PaymentReqDto;
import com.kakaopay.homework.cardapi.exception.InvalidRequestException;
import com.kakaopay.homework.cardapi.exception.NotFoundException;
import com.kakaopay.homework.cardapi.model.Payment;
import com.kakaopay.homework.cardapi.dto.PaymentType;
import com.kakaopay.homework.cardapi.repository.PaymentRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PaymentServiceImplTest {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    PaymentServiceImpl paymentService;

    @MockBean
    PaymentRepository paymentRepository;

    @Test(expected = InvalidRequestException.class)
    public void doPayment_숫자이외의_값이_있을경우_Exception() throws Exception {
        PaymentReqDto payment = PaymentReqDto.builder()
                .price("100a")
                .installments("0")
                .vat("0")
                .build();
        paymentService.doPayment(payment);
    }

    @Test(expected = InvalidRequestException.class)
    public void cancelPayment_요청금액과_원거래금액이_상이할때_에러나는가() throws Exception {
        Mockito.when(paymentRepository.findById("test")).thenReturn(java.util.Optional.ofNullable(Payment.builder()
                .paymentType(PaymentType.PAYMENT)
                .price(1000)
                .build()));
        CancelPaymentDto cancelPaymentDto = CancelPaymentDto.builder().price("2000").build();
        paymentService.cancelPayment(cancelPaymentDto, "test");
    }
    @Test(expected = InvalidRequestException.class)
    public void cancelPayment_취소거래를_취소할경우_에러발생여부() throws Exception {
        Mockito.when(paymentRepository.findById("test")).thenReturn(java.util.Optional.ofNullable(Payment.builder()
                .price(1000)
                .paymentType(PaymentType.CANCEL)
                .build()));
        CancelPaymentDto cancelPaymentDto = CancelPaymentDto.builder().price("1000").build();
        paymentService.cancelPayment(cancelPaymentDto, "test");
    }
    @Test(expected = InvalidRequestException.class)
    public void cancelPayment_중복해서_거래를_취소할경우_에러발생여부() throws Exception {
        List<Payment> payments = new ArrayList<Payment>();
        payments.add(Payment.builder().build());

        Mockito.when(paymentRepository.findById("test")).thenReturn(java.util.Optional.ofNullable(Payment.builder()
                .price(1000)
                .paymentType(PaymentType.PAYMENT)
                .children(payments)
                .build()));
        CancelPaymentDto cancelPaymentDto = CancelPaymentDto.builder().price("1000").build();
        paymentService.cancelPayment(cancelPaymentDto, "test");
    }

    @Test
    public void convertTransactionString_데이터의_길이가_450인지_확인() {
        Payment payment = Payment.builder().cardInfo("1111222233334444/1023/258")
                .price(1000).installments(0).paymentType(PaymentType.PAYMENT).vat(0).build();
        assertEquals(450, paymentService.convertTransactionString(payment).length());
    }

    @Test
    public void calcVat_부가세가_정상적으로_계산되는지_확인1() {
        assertEquals(91, paymentService.calcVat("1000",""));
    }
    @Test
    public void calcVat_부가세가_정상적으로_계산되는지_확인2() {
        assertEquals(0, paymentService.calcVat("1000","0"));
    }

}