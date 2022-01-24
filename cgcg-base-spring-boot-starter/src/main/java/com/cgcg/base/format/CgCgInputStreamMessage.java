package com.cgcg.base.format;

import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Getter
public class CgCgInputStreamMessage implements HttpInputMessage {

    private final InputStream body;

    private final HttpHeaders headers;

    public CgCgInputStreamMessage(String body, HttpHeaders headers) {
        this.body = IOUtils.toInputStream(body, StandardCharsets.UTF_8);
        this.headers = headers;
    }

}
