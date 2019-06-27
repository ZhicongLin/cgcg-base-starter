package com.cgcg.base.interceptor;

import com.cgcg.base.enums.FormatProperty;
import com.cgcg.base.enums.CharsetCode;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@WebFilter(urlPatterns = "/**")
public class EncryptSignFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final String encryptKey = FormatProperty.des(FormatProperty.DES_PARAM);
        if (StringUtils.isBlank(encryptKey)) {
            chain.doFilter(request, response);
        } else {
            final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            final String sign = httpServletRequest.getHeader("sign");
            request.setCharacterEncoding(CharsetCode.forUtf8().name());
            if ("encrypt".equals(sign)) {
                // 防止流读取一次后就没有了, 所以需要将流继续写出去
                ServletRequest requestWrapper = new RequestBodyWrapper(httpServletRequest, encryptKey);
                chain.doFilter(requestWrapper, response);
                return;
            }
        }
        chain.doFilter(request, response);
    }

}
