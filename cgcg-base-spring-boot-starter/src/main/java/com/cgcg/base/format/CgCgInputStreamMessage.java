package com.cgcg.base.format;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CgCgInputStreamMessage implements HttpInputMessage {

    private String body;

    private HttpHeaders httpHeaders;

    public CgCgInputStreamMessage(String body, HttpHeaders httpHeaders) {
        this.body = body;
        this.httpHeaders = httpHeaders;
    }

    @Override
    public InputStream getBody() throws IOException {
        return IOUtils.toInputStream(body, StandardCharsets.UTF_8);
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.httpHeaders;
    }
}
