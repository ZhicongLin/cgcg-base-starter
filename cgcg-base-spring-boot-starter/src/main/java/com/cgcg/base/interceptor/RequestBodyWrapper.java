package com.cgcg.base.interceptor;

import com.cgcg.base.enums.CharsetCode;
import com.cgcg.base.exception.CommonException;
import com.cgcg.base.util.DES3Util;
import com.cgcg.base.util.HttpHelper;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;

/**
 * Created with antnest-platform
 */
public class RequestBodyWrapper extends HttpServletRequestWrapper {

	private final byte[] body;

	public RequestBodyWrapper(HttpServletRequest request, String paramEncrypt) throws IOException {
		super(request);
		try {
			String in = HttpHelper.getStringBody(request);
			String str = "";
			if (in.contains("%")) {
				str = DES3Util.decryptMode(URLDecoder.decode(in), paramEncrypt);
			} else {
				str = DES3Util.decryptMode(in, paramEncrypt);
			}
			body = str.getBytes(CharsetCode.forUtf8());
		} catch (Exception e) {
			throw new CommonException(100400, "参数错误");
		}
	}

	@Override
	public BufferedReader getReader() throws IOException {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}

	@Override
	public ServletInputStream getInputStream() throws IOException {

		final ByteArrayInputStream bais = new ByteArrayInputStream(body);

		return new ServletInputStream() {

			@Override
			public boolean isFinished() {
				return false;
			}

			@Override
			public boolean isReady() {
				return false;
			}

			@Override
			public void setReadListener(ReadListener readListener) { //NOSONAR

			}

			@Override
			public int read() throws IOException {
				return bais.read();
			}
		};
	}

}
