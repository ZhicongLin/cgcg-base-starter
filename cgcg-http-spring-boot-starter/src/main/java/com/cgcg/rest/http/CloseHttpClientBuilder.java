package com.cgcg.rest.http;

import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

public class CloseHttpClientBuilder extends HttpClientBuilder {

    public CloseHttpClientBuilder() {
        super();
    }

    public static CloseHttpClientBuilder create(HttpClientConnectionManager connectionManager) {
        final CloseHttpClientBuilder builder = new CloseHttpClientBuilder();
        return builder.connectionManager(connectionManager).sslSocketFactory();
    }

    public CloseableHttpClient build() {
        return super.build();
    }

    public CloseHttpClientBuilder connectionManager(HttpClientConnectionManager connectionManager) {
        super.setConnectionManager(connectionManager);
        return this;
    }

    private CloseHttpClientBuilder sslSocketFactory() {
        try {
            this.setSSLSocketFactory(new SSLConnectionSocketFactory(createIgnoreVerifySSL(),
                    // 指定TLS版本
                    null,
                    // 指定算法
                    null,
                    // 取消域名验证
                    (str, s) -> true));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }


    /**
     * 跳过证书效验的sslcontext
     *
     * @return
     * @throws Exception
     */
    private SSLContext createIgnoreVerifySSL() throws Exception {
        final SSLContext sc = SSLContext.getInstance("TLS");
        // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
        final X509TrustManager managers = new X509TrustManager() {
            @Override
            public void checkClientTrusted(X509Certificate[] pac, String param) {
            }

            @Override
            public void checkServerTrusted(X509Certificate[] pac, String param) {
            }

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        sc.init(null, new TrustManager[]{managers}, null);
        return sc;
    }
}
