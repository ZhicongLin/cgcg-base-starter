package com.cgcg.rest.exception;

/**
 * Description: 创建Rest方法失败
 *
 * @author linzc
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本        修改人     修改日期        修改内容
 * 2020/6/12.1    linzc       2020/6/12     Create
 * </pre>
 * @date 2020/6/12
 */
public class RestBuilderException extends RuntimeException {

    public RestBuilderException(String message) {
        super(message);
    }
}