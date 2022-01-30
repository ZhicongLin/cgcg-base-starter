package com.cgcg.rest.param;

import com.cgcg.rest.annotation.DynamicParam;
import org.springframework.web.bind.annotation.*;

/**
 * rest param 处理工具.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-09 09:14
 */
public interface RestParamVisitor {
    /**
     * 执行组装请求参数
     *
     * @param annotation 请求头类型
     * @param param      参数值
     * @param restParam  注解的各个参数
     * @return void
     * @author : zhicong.lin
     * @date : 2022/1/26 16:10
     */
    void visitor(RequestHeader annotation, Object param, RestHandle<String, Object> restParam);

    /**
     * 执行组装请求参数
     *
     * @param annotation 请求参数
     * @param param      参数值
     * @param restParam  注解的各个参数
     * @return void
     * @author : zhicong.lin
     * @date : 2022/1/26 16:10
     */
    void visitor(RequestParam annotation, Object param, RestHandle<String, Object> restParam);

    /**
     * 执行组装请求参数
     *
     * @param annotation 表单传参
     * @param param      参数值
     * @param restParam  注解的各个参数
     * @return void
     * @author : zhicong.lin
     * @date : 2022/1/26 16:10
     */
    void visitor(ModelAttribute annotation, Object param, RestHandle<String, Object> restParam);

    /**
     * 执行组装请求参数
     *
     * @param annotation 路径带参数的类型
     * @param param      参数值
     * @param restParam  注解的各个参数
     * @return void
     * @author : zhicong.lin
     * @date : 2022/1/26 16:10
     */
    void visitor(PathVariable annotation, Object param, RestHandle<String, Object> restParam);

    /**
     * 执行组装请求参数
     *
     * @param annotation json传参类型
     * @param param      参数值
     * @param restParam  注解的各个参数
     * @return void
     * @author : zhicong.lin
     * @date : 2022/1/26 16:10
     */
    void visitor(RequestBody annotation, Object param, RestHandle<String, Object> restParam);

    /**
     * 执行组装请求参数
     *
     * @param annotation 文件传输类型
     * @param param      参数值
     * @param restParam  注解的各个参数
     * @return void
     * @author : zhicong.lin
     * @date : 2022/1/26 16:10
     */
    void visitor(RequestPart annotation, Object param, RestHandle<String, Object> restParam);

    /**
     * 执行组装请求参数
     *
     * @param annotation 动态参数类型
     * @param param      参数值
     * @param restParam  注解的各个参数
     * @return void
     * @author : zhicong.lin
     * @date : 2022/1/26 16:10
     */
    void visitor(DynamicParam annotation, Object param, RestHandle<String, Object> restParam);

}
