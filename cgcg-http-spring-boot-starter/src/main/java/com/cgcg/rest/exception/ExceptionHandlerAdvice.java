package com.cgcg.rest.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Description : 使用Advice方式处理异常
 *
 * @author : zc.lin.
 * @version : 2017/10/17.
 */
@Slf4j
@Order(1)
@ResponseBody
@ControllerAdvice
public class ExceptionHandlerAdvice {

    /**
     * Handle exception result.
     *
     * @param e the e
     * @return the result
     */
    @ExceptionHandler(RestException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(RestException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("errorCode", e.getErrorCode());
        result.put("errorMsg", e.getErrorMsg());
        return result;
    }

}
