package com.kakaopay.homework.cardapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException
{
    public NotFoundException(String id) {
        super("'" + id + "'에 대한 정보를 찾을 수 없습니다.");
    }
}