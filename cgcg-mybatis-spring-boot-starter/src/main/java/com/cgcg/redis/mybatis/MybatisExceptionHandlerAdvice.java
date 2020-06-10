package com.cgcg.redis.mybatis;

import java.sql.SQLSyntaxErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.cgcg.base.format.Result;
import com.cgcg.base.language.Translator;
import com.mysql.cj.jdbc.exceptions.MysqlDataTruncation;

import lombok.extern.slf4j.Slf4j;

/**
 * Description : 使用Advice方式处理异常
 *
 * @author : zc.lin.
 * @version : 2017/10/17.
 */
@Slf4j
@Order(-1)
@ResponseBody
@ControllerAdvice
public class MybatisExceptionHandlerAdvice {

    /**
     * Handle exception result.
     *
     * @param e the e
     * @return the result
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(DataIntegrityViolationException e) {
        final Throwable cause = getCause(e);
        String message = Translator.toLocale("DataIntegrityViolationException");
        if (cause instanceof MysqlDataTruncation) {
            final List<String> msgResult = getMessage(cause.getMessage());
            message = String.format(Objects.requireNonNull(Translator.toLocale("MysqlDataTruncation")), msgResult.get(0));
        }
        log.error(message, e);
        return Result.error(100400, message);
    }

    /**
     * Handle exception result.
     *
     * @param e the e
     * @return the result
     */
    @ExceptionHandler(SQLSyntaxErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(SQLSyntaxErrorException e) {
        final String msg = e.getMessage();
        log.error(msg, e);
        return Result.error(100400, msg.replace("Table", "表").replace("doesn't exist", "不存在"));
    }

    private Throwable getCause(Throwable t) {
        final Throwable cause = t.getCause();
        if (cause == null) {
            return t;
        }
        return getCause(cause);
    }

    private List<String> getMessage(String msg) {
        Pattern p = Pattern.compile("'(.*?)'");
        Matcher m = p.matcher(msg);
        final ArrayList<String> result = new ArrayList<>();
        while (m.find()) {
            result.add(m.group());
        }
        return result;
    }
}
