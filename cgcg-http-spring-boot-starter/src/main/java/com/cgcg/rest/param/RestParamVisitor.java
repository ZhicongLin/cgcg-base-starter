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

    void visitor(RequestHeader annotation, Object param, RestHandle<String, Object> restParam);

    void visitor(RequestParam annotation, Object param, RestHandle<String, Object> restParam);

    void visitor(ModelAttribute annotation, Object param, RestHandle<String, Object> restParam);

    void visitor(PathVariable annotation, Object param, RestHandle<String, Object> restParam);

    void visitor(RequestBody annotation, Object param, RestHandle<String, Object> restParam);

    void visitor(RequestPart annotation, Object param, RestHandle<String, Object> restParam);

    void visitor(DynamicParam annotation, Object param, RestHandle<String, Object> restParam);

}
