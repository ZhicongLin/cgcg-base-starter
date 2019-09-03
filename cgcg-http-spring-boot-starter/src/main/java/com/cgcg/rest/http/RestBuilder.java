package com.cgcg.rest.http;

import com.cgcg.rest.MappingProcessor;
import com.cgcg.context.SpringContextHolder;
import com.cgcg.rest.URLUtils;
import com.cgcg.rest.annotation.DynamicMapping;
import com.cgcg.rest.annotation.MappingFilter;
import com.cgcg.rest.annotation.RestClient;
import com.cgcg.rest.filter.RestFilter;
import com.cgcg.rest.param.RestHandle;
import com.cgcg.rest.param.RestParamUtils;
import com.cgcg.rest.proxy.BuilderCallBack;
import com.cgcg.rest.proxy.Proceeding;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Rest请求建立者.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-21 13:43
 */
@Slf4j
@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RestBuilder {
    private static final String HTTPS = "https";
    private static final String HTTP = "http";
    private static final String HTTP_SEPARATOR = "://";
    private static final String SEPARATOR = ".";
    private static final String HOST = "host";
    private static final String PORT = "port";
    private static volatile Map<Method, RestBuilder> builderMap = new HashMap<>();

    private Method method;

    private Object[] args;

    private String url = "";

    private HttpMethod httpMethod;

    private RestHandle<String, Object> params;

    private RestFilter filter;

    private boolean isHttps;

    private String methodLogger;

    public static RestBuilder getInstance(Proceeding proceeding) {
        final Method method = proceeding.getMethod();
        RestBuilder restBuilder = builderMap.get(method);
        if (restBuilder == null) {
            synchronized (RestBuilder.class) {
                restBuilder = builderMap.get(method);
                if (restBuilder == null) {
                    restBuilder = new RestBuilder(method);
                    restBuilder.setMethodLogger(proceeding.getLogName());
                }
            }
        }
        return restBuilder;
    }

    /**
     * 创建RestBuilder
     *
     * @param method
     */
    private RestBuilder(Method method) {
        this.method = method;
        this.buildFilter(method);
        this.initURI(method);
        this.builderSchema();
    }

    /**
     * 添加请求参数
     *
     * @param args
     * @return
     */
    public RestBuilder addArgs(Object[] args) {
        if (args == null) {
            return this;
        }
        this.args = args;
        if (method.getDeclaredAnnotation(DynamicMapping.class) != null) {
            for (Object parameter : this.args) {
                if (parameter instanceof HttpMethod) {
                    this.httpMethod = (HttpMethod) parameter;
                }
            }
        }
        this.params = RestParamUtils.getRestParam(method, args, url);
        this.buildHeader();
        return this;
    }

    /**
     * 添加头部信息
     */
    private void buildHeader() {
        if (this.params.getContentType() != null) {
            this.params.addHeader("content-type", this.params.getContentType());
        }
        if (this.params.getAccept() != null) {
            this.params.addHeader("Accept", this.params.getAccept());
        }
        if (this.methodLogger != null) {
            this.params.addHeader("client-log-name", this.methodLogger);
        }
    }

    /**
     * 生成过滤器
     *
     * @param method
     */
    private void buildFilter(Method method) {
        MappingFilter filter = method.getDeclaredAnnotation(MappingFilter.class);
        if (filter == null) {
            final Class<?> declaringClass = Objects.requireNonNull(method).getDeclaringClass();
            filter = declaringClass.getDeclaredAnnotation(MappingFilter.class);
        }
        if (filter != null) {
            Class<? extends RestFilter> restFilter = filter.name();
            this.filter = SpringContextHolder.getBean(restFilter);
        }
    }

    /**
     * 生成资源参数
     *
     * @param method
     */
    private void initURI(Method method) {
        RestClient restClient = method.getDeclaringClass().getAnnotation(RestClient.class);
        this.url = StringUtils.isNotBlank(restClient.url()) ? this.getUrlProperties(restClient) : this.getProperties(restClient.value());
        final MappingProcessor.MappingHandle handle = MappingProcessor.execute(method);
        if (handle != null) {
            this.httpMethod = handle.getHttpMethod();
            this.url = URLUtils.add(url, handle.getValue());
        }
    }

    private String getUrlProperties(RestClient restClient) {
        this.isHttps = restClient.https();
        return restClient.url();
    }

    private String getProperties(String uri) {
        final Environment ctx = SpringContextHolder.getBean(Environment.class);
        String isHttpsStr = ctx.getProperty(uri + SEPARATOR + HTTPS);
        this.isHttps = StringUtils.isNotBlank(isHttpsStr) && isHttpsStr.equals("true");
        final String host = ctx.getProperty(uri + SEPARATOR + HOST);
        final String httpUri = ctx.getProperty(uri);
        if (StringUtils.isNotBlank(httpUri)) {
            return httpUri;
        }
        final String httpString = (this.isHttps ? HTTPS : HTTP) + HTTP_SEPARATOR;
        if (StringUtils.isBlank(host)) { //配置文件找不到相关配置，直接返回value值
            return httpString;
        }
        final String port = ctx.getProperty(uri + SEPARATOR + PORT);
        return httpString + host + (StringUtils.isNotBlank(port) ? ":" + port : "");
    }

    public Object execute(BuilderCallBack call) {
        params.setHttpMethod(this.httpMethod);
        if (this.filter != null) {
            this.filter.postServer(url, httpMethod, params, params.getHeaders(), method.getReturnType());
        }
        final Object result = call.execute(method, args, url, this.params);
        if (this.filter != null) {
            return this.filter.end(result, method.getReturnType());
        }
        return result;
    }


    private void builderSchema() {
        String schema = "http://";
        if (isHttps) {
            schema = "https://";
        }
        if (!this.url.toLowerCase().startsWith(schema)) {
            if (this.url.toLowerCase().startsWith("http://")) {
                this.url = this.url.replace("http://", schema);
            } else if (this.url.toLowerCase().startsWith("https://")) {
                this.url = this.url.replace("https://", schema);
            } else {
                this.url = schema + this.url;
            }
        }
    }
}
