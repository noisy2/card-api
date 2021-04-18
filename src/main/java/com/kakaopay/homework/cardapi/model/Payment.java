package com.kakaopay.homework.cardapi.model;

import com.kakaopay.homework.cardapi.dto.PaymentType;
import com.kakaopay.homework.cardapi.util.StringCrypto;
import com.kakaopay.homework.cardapi.util.StringCryptoConverter;
import lombok.*;
import org.apache.commons.lang3.RandomStringUtils;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

@NoArgsConstructor
@Getter
@Entity
public class Payment {
    @Id @Column(name = "payment_id")
    private String paymentId;     // 관리번호
    @Convert(converter = StringCryptoConverter.class)
    private String cardInfo;    // 카드정보
    private int installments;   // 할부정보
    private int price;          // 결재가격
    private int vat;            // 부가가치세
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;    // 거래종류

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "par_payment_id")
    private Payment parent;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "parent")
    private List<Payment> children;

    @Builder
    public Payment(String cardInfo, int installments, int price, int vat
            , PaymentType paymentType, Payment parent, List<Payment> children) {
        this.paymentId = generatePaymentId();
        this.cardInfo = cardInfo;
        this.installments = installments;
        this.price = price;
        this.vat = vat;
        this.paymentType = paymentType;
        this.parent = parent;
        this.children = children;
    }

    private String generatePaymentId(){
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        Calendar calendar = Calendar.getInstance();
        return dateFormat.format(calendar.getTime())+ RandomStringUtils.randomNumeric(3);
    }

    @Override
    public String toString() {
        String[] cardInfoArr = this.cardInfo.split("/");
        String encryptedString = "";
        try{
            encryptedString = StringCrypto.encrypt(cardInfo);
        } catch (Exception e){
            e.printStackTrace();
        }
        return String.format("%-20s%02d%-4s%-3s%10d%010d%20s%-300s%-47s"
                , cardInfoArr[0], installments, cardInfoArr[1], cardInfoArr[2], price, vat
                , paymentType.equals(PaymentType.CANCEL) ? parent.paymentId : "", encryptedString, "");
    }
}
