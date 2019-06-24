package com.cgcg.base.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 通用返回结果类.
 *
 * @author zhicong.lin
 * @date 2019/6/19
 */
@Setter
@Getter
@ApiModel("返回结果")
public final class ResultMap<T> {

    public static final Logger logger = LoggerFactory.getLogger(ResultMap.class);

    @ApiModelProperty("编号")
    private int code;
    @ApiModelProperty("返回数据")
    private T data;
    @ApiModelProperty("提示信息")
    private String message;

    private ResultMap() {
        this.code = 200;
        this.message = "操作成功";
    }

    private ResultMap(T data) {
        this();
        this.data = data;
    }

    public static <T> ResultMap<T> success(T data) {
        return new ResultMap<>(data);
    }

    public static <T> ResultMap<T> success(T data, String message) {
        ResultMap<T> success = new ResultMap<>(data);
        success.setCode(200);
        success.setMessage(message);
        return success;
    }

    public static <T> ResultMap<T> error(T data) {
        final ResultMap<T> error = error();
        error.setData(data);
        return error;
    }

    public static <T> ResultMap<T> error(String data) {
        final ResultMap<T> error = error();
        error.setCode(400);
        error.setMessage(data);
        return error;
    }

    public static <T> ResultMap<T> error(int code, String message) {
        final ResultMap<T> error = error();
        error.setCode(code);
        error.setMessage(message);
        return error;
    }

    public static <T> ResultMap<T> error(T data, String message) {
        final ResultMap<T> error = error();
        error.setData(data);
        error.setMessage(message);
        return error;
    }

    public static <T> ResultMap<T> success() {
        return new ResultMap<>();
    }

    public static <T> ResultMap<T> error() {
        final ResultMap<T> error = new ResultMap<>();
        error.setCode(400);
        error.setMessage("操作失败");
        return error;
    }

}
