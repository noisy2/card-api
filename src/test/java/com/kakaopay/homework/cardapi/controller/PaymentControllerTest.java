package com.kakaopay.homework.cardapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kakaopay.homework.cardapi.common.RestDocConfiguration;
import com.kakaopay.homework.cardapi.dto.CancelPaymentDto;
import com.kakaopay.homework.cardapi.dto.PaymentReqDto;
import com.kakaopay.homework.cardapi.model.CreditCardTran;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@Import(RestDocConfiguration.class)
public class PaymentControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext wac;

    @Test
    public void test_결재_요청한_결재내역이_정상적으로_적재되고_응답되는가() throws Exception {
        PaymentReqDto paymentReqDto = PaymentReqDto.builder().cardNumber("1111222233334444")
                .cvc("258")
                .expirationDate("1023")
                .installments("0")
                .price("1000")
                .build();

        mockMvc.perform(
                    post("/api/payment").contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(paymentReqDto))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("paymentId").exists())
                .andDo(
                        document("do-payment",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                links(
                                        linkWithRel("self").description("link to self"),
                                        linkWithRel("cancel-payment").description("link to update"),
                                        linkWithRel("profile").description("link to profile")
                                        ),
                                requestFields(
                                        fieldWithPath("cardNumber").description("카드번호"),
                                        fieldWithPath("expirationDate").description("유효기간"),
                                        fieldWithPath("cvc").description("카드cvc번호"),
                                        fieldWithPath("installments").description("할부기간"),
                                        fieldWithPath("price").description("결재가격"),
                                        fieldWithPath("vat").description("부가세").optional()
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("Location"),
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type - HAL_JSON")
                                ),
                                responseFields(
                                        fieldWithPath("paymentId").description("거래번호id"),
                                        fieldWithPath("transactionInfo").description("카드사 전송 데이터"),
                                        fieldWithPath("_links.self.href").description("link to self"),
                                        fieldWithPath("_links.cancel-payment.href").description("link to cancel"),
                                        fieldWithPath("_links.profile.href").description("link to profile")
                                )

                        )
                );
    }

    @Test
    public void test_조회_요청한_결재내역이_정상적으로_조회되는가() throws Exception {
        PaymentReqDto paymentReqDto = PaymentReqDto.builder().cardNumber("1111222233334444")
                .cvc("258")
                .expirationDate("1023")
                .installments("0")
                .price("1000")
                .build();

        MvcResult mvcResult = mockMvc.perform(
                post("/api/payment").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentReqDto))
                )
                .andDo(print())
                .andReturn();

        CreditCardTran result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreditCardTran.class);
        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/payment/{id}",result.getPaymentId())
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("paymentId").value(result.getPaymentId()))
                .andDo(
                        document("get-payment",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                links(
                                        linkWithRel("self").description("link to self"),
                                        linkWithRel("cancel-payment").description("link to update"),
                                        linkWithRel("profile").description("link to profile")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("거래번호id")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type - HAL_JSON")
                                ),
                                responseFields(
                                        fieldWithPath("paymentId").description("거래번호id"),
                                        fieldWithPath("cardInfo.cardNumber").description("카드번호"),
                                        fieldWithPath("cardInfo.expirationDate").description("유효기간"),
                                        fieldWithPath("cardInfo.cvc").description("cvc번호"),
                                        fieldWithPath("paymentType").description("거래구분"),
                                        fieldWithPath("paymentPrice.price").description("결재가격"),
                                        fieldWithPath("paymentPrice.vat").description("부가세"),
                                        fieldWithPath("originPaymentId").type(JsonFieldType.STRING).optional().description("원거래 관리번호"),
                                        fieldWithPath("_links.self.href").description("link to self"),
                                        fieldWithPath("_links.cancel-payment.href").description("link to cancel"),
                                        fieldWithPath("_links.profile.href").description("link to profile")
                                ))
                );
    }
    @Test
    public void test_취소_요청한_결재내역이_정상적으로_취소되는가() throws Exception {
        PaymentReqDto paymentReqDto = PaymentReqDto.builder().cardNumber("1111222233334444")
                .cvc("258")
                .expirationDate("1023")
                .installments("0")
                .price("1000")
                .build();

        MvcResult mvcResult = mockMvc.perform(
                post("/api/payment").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentReqDto))
                )
                .andDo(print())
                .andReturn();

        CreditCardTran result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), CreditCardTran.class);

        CancelPaymentDto cancelPaymentDto = CancelPaymentDto.builder()
                .price("1000").build();

        mockMvc.perform(RestDocumentationRequestBuilders.put("/api/payment/{id}",result.getPaymentId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelPaymentDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("paymentId").exists())
                .andDo(
                        document("cancel-payment",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                links(
                                        linkWithRel("self").description("link to self"),
                                        linkWithRel("cancel-payment").description("link to update"),
                                        linkWithRel("profile").description("link to profile")
                                ),
                                pathParameters(
                                        parameterWithName("id").description("거래번호id")
                                ),
                                requestFields(
                                        fieldWithPath("price").description("취소 결재금액"),
                                        fieldWithPath("vat").description("취소 부가세")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("Location"),
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type - HAL_JSON")
                                ),
                                responseFields(
                                        fieldWithPath("paymentId").description("거래번호id"),
                                        fieldWithPath("transactionInfo").description("카드사 전송 데이터"),
                                        fieldWithPath("_links.self.href").description("link to self"),
                                        fieldWithPath("_links.cancel-payment.href").description("link to cancel"),
                                        fieldWithPath("_links.profile.href").description("link to profile")
                                )
                ));
    }
    @Test
    public void test_조회_조재하지_않는_정보조회시_에러나는가() throws Exception {
        String paymentId = "123456789012345678901";

        mockMvc.perform(RestDocumentationRequestBuilders.get("/api/payment/{id}",paymentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(
                        document("errors",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                responseFields(
                                        fieldWithPath("message").description("오류 종류"),
                                        fieldWithPath("details").description("오류 상세내용")
                                ))
                );
    }
    @Test
    public void test_취소_요청금액이_없을시_에러나는가() throws Exception {
        String paymentId = "123456789012345678901";
        CancelPaymentDto cancelPaymentDto = CancelPaymentDto.builder().build();

        mockMvc.perform(put("/api/payment/{id}",paymentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelPaymentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_취소_원거래관리번호가_정수가_아니라면_에러발생하는가() throws Exception {
        String paymentId = "a1234567891234567890";
        CancelPaymentDto cancelPaymentDto = CancelPaymentDto.builder().price("1000").build();
        mockMvc.perform(put("/api/payment/{id}",paymentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelPaymentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_취소_원거래관리번호가_20자리정수가_아니라면_에러발생하는가() throws Exception {
        String paymentId = "123456789012345678901";
        CancelPaymentDto cancelPaymentDto = CancelPaymentDto.builder().price("1000").build();
        mockMvc.perform(put("/api/payment/{id}",paymentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelPaymentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_결재_필수값_notNull테스트() throws Exception {
        PaymentReqDto paymentReqDto = PaymentReqDto.builder().cardNumber("1111222233334444")
                .cvc("258")
                .expirationDate("1023")
                .installments("0")
                .build();

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentReqDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_결재_거래금액_범위테스트_99원() throws Exception {
        PaymentReqDto paymentReqDto = PaymentReqDto.builder().cardNumber("1111222233334444")
                .cvc("258")
                .expirationDate("1023")
                .installments("0")
                .price("99")
                .build();

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentReqDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_결재_거래금액_범위테스트_11억() throws Exception {
        PaymentReqDto paymentReqDto = PaymentReqDto.builder().cardNumber("1111222233334444")
                .cvc("258")
                .expirationDate("1023")
                .installments("0")
                .price("1100000000")
                .build();

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentReqDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_결재_거래금액_범위테스트_부가세가_더_큰_경우() throws Exception {
        PaymentReqDto paymentReqDto = PaymentReqDto.builder().cardNumber("1111222233334444")
                .cvc("258")
                .expirationDate("1023")
                .installments("0")
                .price("1000")
                .vat("1100")
                .build();

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentReqDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_결재_거래금액_1000원이_아닌데_부가세가_0원인경우() throws Exception {
        PaymentReqDto paymentReqDto = PaymentReqDto.builder().cardNumber("1111222233334444")
                .cvc("258")
                .expirationDate("1023")
                .installments("0")
                .price("10000")
                .vat("0")
                .build();

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentReqDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_결재_부가세가_0원보다_작은경우() throws Exception {
        PaymentReqDto paymentReqDto = PaymentReqDto.builder().cardNumber("1111222233334444")
                .cvc("258")
                .expirationDate("1023")
                .installments("0")
                .price("10000")
                .vat("-100")
                .build();

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentReqDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}