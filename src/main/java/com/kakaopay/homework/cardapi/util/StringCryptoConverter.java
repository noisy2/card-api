package com.kakaopay.homework.cardapi.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import java.security.Key;
import java.util.Base64;

@Convert
public class StringCryptoConverter implements AttributeConverter<String, String> {

    @Override
    public String convertToDatabaseColumn(String attribute) {
        String encryptedString = "";
        try{
            encryptedString = StringCrypto.encrypt(attribute);
        } catch (Exception e){
            e.printStackTrace();
        }
        return encryptedString;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        String decryptedString = "";
        try{
            decryptedString = StringCrypto.decrypt(dbData);
        } catch (Exception e){
            e.printStackTrace();
        }
        return decryptedString;
    }
}
