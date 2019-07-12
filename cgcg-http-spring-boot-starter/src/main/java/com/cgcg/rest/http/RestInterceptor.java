package com.cgcg.rest.http;

import com.cgcg.rest.Constant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * rest 请求拦截器，默认有Authorization处理.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-17 13:17
 */
@Slf4j
public class RestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        URI uri = httpRequest.getURI();
        HttpHeaders headers = httpRequest.getHeaders();
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        Logger logger = log;
        if (log.isDebugEnabled()) {
            Object attribute = request.getAttribute(Constant.REST_METHOD_NAME);
            if (attribute != null) {
                logger = LoggerFactory.getLogger(attribute.toString());
            }
            logger.debug("{} {}.", httpRequest.getMethod(), uri);
            logger.debug("Headers {}", headers);
            MediaType contentType = headers.getContentType();
            boolean isFile = contentType != null && contentType.getType().equals(MediaType.MULTIPART_FORM_DATA.getType());
            final int length = bytes.length;
            if (length > 0 && !isFile) {
                logger.debug("Body    {}", new String(bytes, StandardCharsets.UTF_8));
            }
        }
        long start = System.currentTimeMillis();
        ClientHttpResponse execute = clientHttpRequestExecution.execute(httpRequest, bytes);
        logger.debug("Response {}\t{}ms.", uri, System.currentTimeMillis() - start);
        return execute;
    }
}
