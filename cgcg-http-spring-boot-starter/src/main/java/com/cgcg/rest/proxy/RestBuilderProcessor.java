package com.cgcg.rest.proxy;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.cgcg.context.SpringContextHolder;
import com.cgcg.context.util.ReflectionUtils;
import com.cgcg.rest.annotation.LoadMapping;
import com.cgcg.rest.http.RestBuilder;
import com.cgcg.rest.http.RestTemplateFactory;
import com.cgcg.rest.param.RestHandle;

import lombok.extern.slf4j.Slf4j;

/**
 * 代理处理器.
 *
 * @Author: ZhiCong.Lin
 * @Create: 2018-08-21 13:49
 */
@Slf4j
public class RestBuilderProcessor implements BuilderCallBack {

    private static final long DEFAULT_EXPIRE = 60000L;
    private static final RestBuilderProcessor REST_BUILDER_PROCESSOR = new RestBuilderProcessor();
    private static final Map<Object, Map<Object, Object>> FALL_BACK_RESULT = new HashMap<>();

    /**
     * 包装调用方法：进行预处理、调用后处理
     */
    public static Object invoke(Proceeding proceeding) {
        final Method method = proceeding.getMethod();
        final Object fallbackBean = proceeding.getInstance();
        final Object[] args = proceeding.getArguments();
        final long start = System.currentTimeMillis();
        final RestBuilder builder = RestBuilder.getInstance(proceeding);
        final boolean valid = validateFallback(method, fallbackBean, start);
        try {
            if (valid) {
                proceeding.getLogger().info("RestClient Hit Fallback.");
                return FALL_BACK_RESULT.get(fallbackBean).get(method);
            }
            return builder.addArgs(args).execute(REST_BUILDER_PROCESSOR);
        } catch (Exception e) {
            if (fallbackBean != null) {
                return fallback(method, args, fallbackBean, start, valid);
            }
            throw e;
        } finally {
            proceeding.getLogger().debug("Response {}ms", (System.currentTimeMillis() - start));
        }
    }


    private static boolean validateFallback(Method method, Object fallbackBean, long startTime) {
        if (fallbackBean == null) {
            return false;
        }
        final Map<Object, Object> fallbackMethod = FALL_BACK_RESULT.get(fallbackBean);
        final String timeKey = method.getName() + "time";
        final Long expire = SpringContextHolder.getProperty("cgcg.rest.fallback.expire", Long.class);
        if (fallbackMethod != null && fallbackMethod.get(timeKey) != null) {
            final Object time = fallbackMethod.get(timeKey);
            final long timeSub = startTime - Long.parseLong(time.toString());
            return timeSub <= (expire == null ? DEFAULT_EXPIRE : expire);
        }
        return false;
    }

    private static Object fallback(Method method, Object[] args, Object fallbackBean, long startTime, boolean validate) {
        Map<Object, Object> fallbackMethod = FALL_BACK_RESULT.get(fallbackBean);
        Object result = null;
        if (fallbackMethod == null || fallbackMethod.get(method) == null || !validate) {
            result = ReflectionUtils.invokeMethod(fallbackBean, method.getName(), method.getParameterTypes(), args);
        }
        if (fallbackMethod == null) {
            fallbackMethod = new HashMap<>();
        }
        fallbackMethod.put(method.getName() + "time", startTime);
        fallbackMethod.put(method, result);
        FALL_BACK_RESULT.put(fallbackBean, fallbackMethod);
        return result;
    }

    /**
     * 具体发起请求的方法 .
     *
     * @param method
     * @param args
     * @param url
     * @Param: [method, args, serverUri, httpMethod, headers, values, returnType]
     * @Return: java.lang.Object
     * @Author: ZhiCong Lin
     * @Date: 2018/8/21 13:48
     */
    @Override
    public Object execute(Method method, Object[] args, String url, RestHandle<String, Object> handle) {
        final RestTemplateFactory templeFactory = SpringContextHolder.getBean(RestTemplateFactory.class);
        if (method.getAnnotation(LoadMapping.class) != null) {
            return templeFactory.loadFileByte(handle);
        } else {
            return templeFactory.execute(handle, method.getReturnType());
        }
    }
}
