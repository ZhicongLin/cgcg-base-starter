package com.cgcg.base.core.exception;

import com.cgcg.base.format.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Description : 使用Advice方式处理异常
 *
 * @author : zc.lin.
 * @version : 2017/10/17.
 */
@Slf4j
@ResponseBody
@ControllerAdvice
public class ExceptionHandlerAdvice {
    /**
     * Instantiates a new Exception handler advice.
     */
    public ExceptionHandlerAdvice() {
        log.debug("Enabled Exception Handler Advice [启动服务异常处理]");
    }

    /**
     * Handle exception result.
     *
     * @param e the e
     * @return the result
     */
    @ExceptionHandler(CommonException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(CommonException e) {
        if (e.getErrorClass() != null) {
            final Logger logger = LoggerFactory.getLogger(e.getErrorClass());
            logger.error(e.getMessage(), e);
        } else {
            log.error(e.getMessage(), e);
        }
        return Result.error(e.getErrorCode(), e.getErrorMsg() );
    }

    /**
     * Handle exception result.
     *
     * @param e the e
     * @return the result
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(MissingServletRequestParameterException e) {
        String parameterName = e.getParameterName();
        String parameterType = e.getParameterType();
        log.error("缺少必填参数{} {}", parameterType, parameterName, e);
        return Result.error(100400, "缺少必填参数:" + parameterName );
    }

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
        String message = "数据完整性冲突";
        if (cause instanceof SQLIntegrityConstraintViolationException) {
            final String msg = cause.getMessage();
            final List<String> msgResult = getMessage(msg);
            if (msgResult.size() == 1) {
                message = "数据库字段" + msgResult.get(0) + "不能为空";
            } else if (msgResult.size() == 2) {
                message = "数据库字段" + msgResult.get(1) + "插入错误的值" + msgResult.get(0);
            }
        }
        log.error(message, e);
        return Result.error(100400, message);
    }

    private Throwable getCause(Throwable t) {
        final Throwable cause = t.getCause();
        if (cause == null) {
            return t;
        }
        return getCause(cause);
    }

    private List<String> getMessage(String msg) {
        Pattern p = Pattern.compile("\'(.*?)\'");
        Matcher m = p.matcher(msg);
        final ArrayList<String> result = new ArrayList<>();
        while (m.find()) {
            result.add(m.group());
        }
        return result;
    }

    /**
     * Handle exception error result.
     *
     * @param e the e
     * @return the error result
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result handleException(HttpRequestMethodNotSupportedException e) {
        final String message = e.getMessage();
        log.error(message, e);
        final String[] split = message.split("'");
        if (split.length >= 2) {
            return Result.error(100400, String.format("请求方式错误[%s]", split[1]));
        }
        return Result.error(100400, "请求方式错误");
    }

    /**
     * Handle exception error result.
     *
     * @param e the e
     * @return the error result
     */
    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(Throwable e) {
        final String message = e.getMessage() != null ? e.getMessage() : e.toString();
        log.error(message, e);
        final String regEx = "[\u4e00-\u9fa5]";
        final Pattern p = Pattern.compile(regEx);
        if (p.matcher(message).find()) {
            return Result.error(100500, message);
        }
        if (message.contains("timeout") || message.contains("timedout")) {
            return message.contains("refused") ? Result.error(100502, "服务器拒绝连接")
                    : Result.error(100504, "服务器连接超时");
        }
        return Result.error(100500, "服务器内部异常");
    }

    /**
     * Handle exception error result.
     *
     * @param e the e
     * @return the error result
     */
    @ExceptionHandler({BindException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result handleException(Exception e) {
        log.error(e.getMessage(), e);
        return Result.error(100400, this.getBindMessage(e.getMessage()));
    }

    /**
     * No mapping error result.
     *
     * @param e the exception
     * @return the error result
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Result noMapping(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return Result.error(100404, "请求路径不存在");
    }

    /**
     * Error param error result.
     *
     * @param me the me
     * @return the error result
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result errorParam(MethodArgumentTypeMismatchException me) {
        log.error(me.getMessage(), me);
        return Result.error(100400, "请求参数不合法");
    }

    /**
     * Handle HttpMediaTypeNotSupportedException result
     *
     * @param e exception
     * @return the error result
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public Result handleException(HttpMediaTypeNotSupportedException e) {
        final String message = e.getMessage();
        log.error(message, e);
        final String[] split = message.split("'");
        if (split.length >= 2) {
            return Result.error(100415, String.format("参数文本格式错误[%s]", split[1]));
        }
        return Result.error(100415, "参数文本类型错误");
    }


    private String getBindMessage(String str) {
        if (StringUtils.hasText(str)) {
            String[] sa = str.split("message");
            if (sa.length > 0) {
                for (int i = sa.length - 1; i >= 0; --i) {
                    if (sa[i].getBytes().length != sa[i].length()) {
                        str = sa[i].trim().replace("[", "");
                        String[] st = str.split("]");
                        if (st.length > 0) {
                            str = st[0];
                        }
                        break;
                    }
                }
            }
        }
        return str;
    }
}
