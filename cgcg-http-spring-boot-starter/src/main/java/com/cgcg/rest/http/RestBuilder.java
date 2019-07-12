package com.cgcg.rest.http;

import com.cgcg.rest.AnnotationUtil;
import com.cgcg.rest.Constant;
import com.cgcg.rest.SpringContextHolder;
import com.cgcg.rest.annotation.DinamicaMapping;
import com.cgcg.rest.annotation.MappingFilter;
import com.cgcg.rest.annotation.RestClient;
import com.cgcg.rest.filter.RestFilter;
import com.cgcg.rest.param.RestHandle;
import com.cgcg.rest.param.RestParamUtils;
import com.cgcg.rest.proxy.BuilderCallBack;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
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
public class RestBuilder {
    private static final String HTTPS = "https";
    private static final String HTTP = "http";
    private static final String HTTP_SEPARATOR = "://";
    private static final String SEPARATOR = ".";
    private static final String HOST = "host";
    private static final String PORT = "port";

    private HttpHeaders httpHeaders = new HttpHeaders();

    private Method method;

    private Object[] args;

    private String url = "";

    private HttpMethod httpMethod;

    private RestHandle<String, Object> params;

    private RestFilter filter;

    private boolean isHttps;

    /**
     * 创建Builder.
     *
     * @Param: [method, args]
     * @Return:
     * @Author: ZhiCong.Lin
     * @Date:
     */
    public RestBuilder(Method method, Object[] args) {
        this.method = method;
        this.args = args;
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes != null) {
            HttpServletRequest request = requestAttributes.getRequest();
            if (request.getAttribute(Constant.REST_METHOD_NAME) == null) {
                request.setAttribute(Constant.REST_METHOD_NAME, this.method.getDeclaringClass().getName() + "#" + method.getName());
            }
        }
        this.buildFilter(method);
        if (filter != null) {
            if (filter.pre(method, args)) {
                this.generation(method, args);
            }
        } else {
            this.generation(method, args);
        }
    }

    /**
     * 生成过滤器
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
     * 拼装url .
     *
     * @Param: [serverUri, requestMappingValue]
     * @Return: java.lang.String
     * @Author: ZhiCong Lin
     * @Date: 2018/8/9 11:36
     */
    private String createUrl(String serverUri, String methodUri) {
        if (StringUtils.isNotBlank(methodUri)) {
            final String uriSep = "/";
            return serverUri + (serverUri.endsWith(uriSep) || methodUri.startsWith(uriSep) ? "" : uriSep) + methodUri;
        }
        return serverUri;
    }

    /**
     * 生成资源参数
     * @param method
     * @param args
     */
    private void generation(Method method, Object[] args) {
        RestClient restClient = method.getDeclaringClass().getAnnotation(RestClient.class);
        url = StringUtils.isNotBlank(restClient.url()) ? this.getUrlProperties(restClient) : this.getProperties(restClient.value());
        final AnnotationUtil.MappingHandle handle = AnnotationUtil.buildMappingHandle(method);
        String methodUri = "";
        if (handle != null) {
            this.httpMethod = handle.getHttpMethod();
            methodUri = handle.getValue();
        }
        if (method.getDeclaredAnnotation(DinamicaMapping.class) != null) {
            for (Object parameter : args) {
                if (parameter instanceof HttpMethod) {
                    this.httpMethod = (HttpMethod) parameter;
                }
            }
        }
        this.params = RestParamUtils.getRestParam(method, args, createUrl(url, methodUri));
        this.url = this.params.getUrl();
        this.httpHeaders = new HttpHeaders();
        if (this.params.getContentType() != null) {
            this.httpHeaders.add("content-type", this.params.getContentType());
        }
        if (this.params.getAccept() != null) {
            this.httpHeaders.add("Accept", this.params.getAccept());
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
     * 发起http回调 .
     *
     * @Param: [callBack]
     * @Return: java.lang.Object
     * @Author: ZhiCong Lin
     * @Date: 2018/8/21 17:10
     */
    public Object bulid(BuilderCallBack callBack) {
        this.builderSchema();
        if (this.filter != null) {
            this.filter.postServer(url, httpMethod, params, httpHeaders, method.getReturnType());
        }
        final Object execute = callBack.execute(method, args, url, this.httpMethod, this.params, this.httpHeaders, method.getReturnType());
        if (this.filter != null) {
            return this.filter.end(execute, method.getReturnType());
        }
        return execute;
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
