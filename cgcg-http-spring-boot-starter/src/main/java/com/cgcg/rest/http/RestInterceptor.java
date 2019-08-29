package com.cgcg.rest.http;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        final List<String> logNames = headers.get("client-log-name");
        Logger logger = log;
        if (log.isDebugEnabled()) {
            if (logNames != null && logNames.get(0) != null) {
                logger = LoggerFactory.getLogger(logNames.get(0));
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
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
