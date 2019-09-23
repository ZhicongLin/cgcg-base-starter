package com.cgcg.rest.http;

import com.cgcg.rest.properties.RestPoolProperties;
import com.cgcg.rest.properties.RestProperties;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * rest temp conf.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-08 17:37
 */
@Configuration
public class RestTemplateConfigure {

    @Resource
    private RestTemplateBuilder builder;
    @Resource
    private RestProperties restProperties;
    @Resource
    private RestPoolProperties restPoolProperties;
    /**
     * 让spring管理RestTemplate,参数相关配置
     */
    @Bean
    public RestTemplate restTemplate() {
        final RestTemplate restTemplate = builder.build();// 生成一个RestTemplate实例
        restTemplate.setRequestFactory(clientHttpRequestFactory());
        final List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
        interceptors.add(new RestInterceptor());
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

    /**
     * 客户端请求链接策略
     *
     * @return
     */
    @Bean
    public ClientHttpRequestFactory clientHttpRequestFactory() {
        final HttpComponentsClientHttpRequestFactory chrf = new HttpComponentsClientHttpRequestFactory();
        chrf.setHttpClient(httpClientBuilder().build());
        chrf.setConnectTimeout(restProperties.getConnectTimeout()); // 连接超时时间/毫秒
        chrf.setReadTimeout(restProperties.getReadTimeout()); // 读写超时时间/毫秒
        chrf.setConnectionRequestTimeout(restProperties.getConnectionRequestTimeout());// 请求超时时间/毫秒
        return chrf;
    }

    /**
     * 设置HTTP连接管理器,连接池相关配置管理
     *
     * @return 客户端链接管理器
     */
    @Bean
    public HttpClientBuilder httpClientBuilder() {
        final HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        httpClientBuilder.setConnectionManager(poolingConnectionManager());
        return httpClientBuilder;
    }

    /**
     * 链接线程池管理,可以keep-alive不断开链接请求,这样速度会更快 MaxTotal 连接池最大连接数 DefaultMaxPerRoute
     * 每个主机的并发 ValidateAfterInactivity
     * 可用空闲连接过期时间,重用空闲连接时会先检查是否空闲时间超过这个时间，如果超过，释放socket重新建立
     */
    @Bean
    public HttpClientConnectionManager poolingConnectionManager() {
        final PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager();
        poolingConnectionManager.setMaxTotal(this.restPoolProperties.getMaxTotal());
        poolingConnectionManager.setDefaultMaxPerRoute(this.restPoolProperties.getMaxPerRoute());
        poolingConnectionManager.setValidateAfterInactivity(this.restPoolProperties.getValidateAfterInactivity());
        return poolingConnectionManager;
    }

}
