package com.cgcg.rest.http;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;

import com.cgcg.context.SpringContextHolder;
import com.cgcg.rest.MappingProcessor;
import com.cgcg.rest.URLUtils;
import com.cgcg.rest.annotation.DynamicMapping;
import com.cgcg.rest.annotation.MappingFilter;
import com.cgcg.rest.annotation.RestClient;
import com.cgcg.rest.exception.RestBuilderException;
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
    private static final Map<String, RestBuilder> REST_BUILDER_CONTEXT = new HashMap<>();

    private Method method;

    private Object[] args;

    private String url = "";

    private HttpMethod httpMethod;

    private RestHandle<String, Object> params;

    private RestFilter filter;

    private boolean isHttps;

    private String methodLogger;

    /**
     * 执行http请求
     *
     * @param call
     * @return
     */
    public static Object execute(Proceeding proceeding, Object[] args, BuilderCallBack call) {
        final RestBuilder restBuilder = RestBuilder.getInstance(proceeding).addArgs(args);
        restBuilder.params.setHttpMethod(restBuilder.httpMethod);
        if (restBuilder.filter != null) {
            restBuilder.filter.postServer(restBuilder.url, restBuilder.httpMethod,
                    restBuilder.params, restBuilder.params.getHeaders(), restBuilder.method.getReturnType());
        }
        final Object result = call.execute(restBuilder.method, restBuilder.args, restBuilder.url, restBuilder.params);
        if (restBuilder.filter != null) {
            return restBuilder.filter.end(result, restBuilder.method.getReturnType());
        }
        return result;
    }

    /**
     * 初始化方法操作
     * @param proceeding
     * @return
     */
    public static RestBuilder getInstance(Proceeding proceeding) {
        return getInstance(proceeding.getMethod(), proceeding.getLogName());
    }

    /**
     * 初始化方法操作
     * @param method
     * @return
     */
    public static RestBuilder getInstance(Method method, String logName) {
        RestBuilder restBuilder = REST_BUILDER_CONTEXT.get(logName);
        if (restBuilder != null) {
            return restBuilder;
        }
        synchronized (REST_BUILDER_CONTEXT) {
            restBuilder = REST_BUILDER_CONTEXT.get(logName);
            if (restBuilder == null) {
                try {
                    restBuilder = new RestBuilder(method, logName);
                    REST_BUILDER_CONTEXT.put(logName, restBuilder);
                    log.debug("Builder Rest Http Method '{}'", logName);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            return restBuilder;
        }
    }

    /**
     * 创建RestBuilder
     *
     * @param method
     */
    private RestBuilder(Method method, String methodLogger) {
        this.method = method;
        this.methodLogger = methodLogger;
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
    private RestBuilder addArgs(Object[] args) {
        if (args == null) {
            return this;
        }
        this.args = args;
        if (this.method.getDeclaredAnnotation(DynamicMapping.class) != null) {
            for (Object parameter : this.args) {
                if (parameter instanceof HttpMethod) {
                    this.httpMethod = (HttpMethod) parameter;
                }
            }
        }
        this.params = RestParamUtils.getRestParam(this.method, args, this.url);
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
        final RestClient restClient = method.getDeclaringClass().getAnnotation(RestClient.class);
        this.url = StringUtils.isNotBlank(restClient.url()) ? this.getUrlProperties(restClient) : this.getProperties(restClient.value());
        final MappingProcessor.MappingHandle handle = MappingProcessor.execute(method);
        if (handle != null) {
            this.httpMethod = handle.getHttpMethod();
            this.url = URLUtils.add(url, handle.getValue());
        } else {
            final String message = String.format("Builder Rest Http Method '%s' Cannot Found Mapping.", this.methodLogger);
            throw new RestBuilderException(message);
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

    /**
     * 处理http前缀
     */
    private void builderSchema() {
        final String http = HTTP + HTTP_SEPARATOR;
        final String https = HTTPS + HTTP_SEPARATOR;
        final String schema = this.isHttps ? https : http;
        if (this.url.toLowerCase().startsWith(schema)) {
            return;
        }
        if (this.url.toLowerCase().startsWith(http)) {
            this.url = this.url.replace(http, schema);
        } else if (this.url.toLowerCase().startsWith(https)) {
            this.url = this.url.replace(https, schema);
        } else {
            this.url = schema + this.url;
        }
    }
}
