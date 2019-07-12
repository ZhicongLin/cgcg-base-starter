package com.cgcg.rest.param;

import com.cgcg.rest.annotation.DinamicaMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.lang.annotation.Annotation;

/**
 * rest param 处理工具.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-09 09:14
 */
public interface RestParamVisitor {

    void visitor(Annotation annotation, Object param, RestHandle<String, Object> restParam);

    String visitor(PathVariable annotation, Object param, String path);

    String visitor(DinamicaMapping annotation, Object arg, String url);
}
