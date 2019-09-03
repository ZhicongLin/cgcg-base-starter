package com.cgcg.rest.filter;

import com.cgcg.context.SpringContextHolder;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

/**
 * my filter.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-09 17:43
 */
public class BaseRestFilter implements RestFilter {

    private static volatile BaseRestFilter baseRestFilter;

    /**
     * .
     *
     * @Param: []
     * @Return: RestFilter
     * @Author: ZhiCong Lin
     * @Date: 2018/8/10 9:38
     */
    public static RestFilter getFilter() {
        RestFilter filter = null;
        try {
            filter = SpringContextHolder.getBean(RestFilter.class);
        } catch (NoSuchBeanDefinitionException ne) {
            //TODO
        }
        return filter != null ? filter : getInstance();
    }

    /**
     * 获取过滤器实例 .
     *
     * @Param: []
     * @Return: RestFilter
     * @Author: ZhiCong Lin
     * @Date: 2018/8/9 17:53
     */
    public static RestFilter getInstance() {
        if (baseRestFilter == null) {
            synchronized (BaseRestFilter.class) {
                if (baseRestFilter == null) {
                    baseRestFilter = new BaseRestFilter();
                }
            }
        }
        return baseRestFilter;
    }
}
