package com.cgcg.base.format;

import com.cgcg.base.language.Translator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 通用返回结果类.
 *
 * @author zhicong.lin
 * @date 2019/6/19
 */
@Setter
@Getter
@ApiModel("返回结果")
public final class Result {

    @ApiModelProperty("编号")
    private int code;
    @ApiModelProperty("返回数据")
    private Object data;
    @ApiModelProperty("提示信息")
    private String message;

    public Result() {
        this.code = 200;
        this.message = Translator.toLocale("success", "操作成功");
    }

    public Result(Object data) {
        this();
        this.data = data;
    }

    public static  Result success(Object data) {
        return new Result(data);
    }
    public static  Result error(int code, String message) {
        final Result error = error();
        error.setCode(code);
        error.setMessage(message);
        return error;
    }

    public static  Result error() {
        final Result error = new Result();
        error.setCode(400);
        error.setMessage(Translator.toLocale("failed", "操作失败"));
        return error;
    }

}
