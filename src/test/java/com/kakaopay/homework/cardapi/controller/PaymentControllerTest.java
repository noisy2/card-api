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
    public void test_??????_?????????_???????????????_???????????????_????????????_???????????????() throws Exception {
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
                                        fieldWithPath("cardNumber").description("????????????"),
                                        fieldWithPath("expirationDate").description("????????????"),
                                        fieldWithPath("cvc").description("??????cvc??????"),
                                        fieldWithPath("installments").description("????????????"),
                                        fieldWithPath("price").description("????????????"),
                                        fieldWithPath("vat").description("?????????").optional()
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("Location"),
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type - HAL_JSON")
                                ),
                                responseFields(
                                        fieldWithPath("paymentId").description("????????????id"),
                                        fieldWithPath("transactionInfo").description("????????? ?????? ?????????"),
                                        fieldWithPath("_links.self.href").description("link to self"),
                                        fieldWithPath("_links.cancel-payment.href").description("link to cancel"),
                                        fieldWithPath("_links.profile.href").description("link to profile")
                                )

                        )
                );
    }

    @Test
    public void test_??????_?????????_???????????????_???????????????_???????????????() throws Exception {
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
                                        parameterWithName("id").description("????????????id")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type - HAL_JSON")
                                ),
                                responseFields(
                                        fieldWithPath("paymentId").description("????????????id"),
                                        fieldWithPath("cardInfo.cardNumber").description("????????????"),
                                        fieldWithPath("cardInfo.expirationDate").description("????????????"),
                                        fieldWithPath("cardInfo.cvc").description("cvc??????"),
                                        fieldWithPath("paymentType").description("????????????"),
                                        fieldWithPath("paymentPrice.price").description("????????????"),
                                        fieldWithPath("paymentPrice.vat").description("?????????"),
                                        fieldWithPath("originPaymentId").type(JsonFieldType.STRING).optional().description("????????? ????????????"),
                                        fieldWithPath("_links.self.href").description("link to self"),
                                        fieldWithPath("_links.cancel-payment.href").description("link to cancel"),
                                        fieldWithPath("_links.profile.href").description("link to profile")
                                ))
                );
    }
    @Test
    public void test_??????_?????????_???????????????_???????????????_???????????????() throws Exception {
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
                                        parameterWithName("id").description("????????????id")
                                ),
                                requestFields(
                                        fieldWithPath("price").description("?????? ????????????"),
                                        fieldWithPath("vat").description("?????? ?????????")
                                ),
                                responseHeaders(
                                        headerWithName(HttpHeaders.LOCATION).description("Location"),
                                        headerWithName(HttpHeaders.CONTENT_TYPE).description("Content type - HAL_JSON")
                                ),
                                responseFields(
                                        fieldWithPath("paymentId").description("????????????id"),
                                        fieldWithPath("transactionInfo").description("????????? ?????? ?????????"),
                                        fieldWithPath("_links.self.href").description("link to self"),
                                        fieldWithPath("_links.cancel-payment.href").description("link to cancel"),
                                        fieldWithPath("_links.profile.href").description("link to profile")
                                )
                ));
    }
    @Test
    public void test_??????_????????????_??????_???????????????_???????????????() throws Exception {
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
                                        fieldWithPath("message").description("?????? ??????"),
                                        fieldWithPath("details").description("?????? ????????????")
                                ))
                );
    }
    @Test
    public void test_??????_???????????????_?????????_???????????????() throws Exception {
        String paymentId = "123456789012345678901";
        CancelPaymentDto cancelPaymentDto = CancelPaymentDto.builder().build();

        mockMvc.perform(put("/api/payment/{id}",paymentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelPaymentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_??????_????????????????????????_?????????_????????????_?????????????????????() throws Exception {
        String paymentId = "a1234567891234567890";
        CancelPaymentDto cancelPaymentDto = CancelPaymentDto.builder().price("1000").build();
        mockMvc.perform(put("/api/payment/{id}",paymentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelPaymentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
    @Test
    public void test_??????_????????????????????????_20???????????????_????????????_?????????????????????() throws Exception {
        String paymentId = "123456789012345678901";
        CancelPaymentDto cancelPaymentDto = CancelPaymentDto.builder().price("1000").build();
        mockMvc.perform(put("/api/payment/{id}",paymentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(cancelPaymentDto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void test_??????_?????????_notNull?????????() throws Exception {
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
    public void test_??????_????????????_???????????????_99???() throws Exception {
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
    public void test_??????_????????????_???????????????_11???() throws Exception {
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
    public void test_??????_????????????_???????????????_????????????_???_???_??????() throws Exception {
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
    public void test_??????_????????????_1000??????_?????????_????????????_0????????????() throws Exception {
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
    public void test_??????_????????????_0?????????_????????????() throws Exception {
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