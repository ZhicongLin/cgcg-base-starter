package org.cgcg.redis.core.util;

import java.lang.reflect.Method;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.env.Environment;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * 解析SPEL 表达式
 *
 * @auth zhicong.lin
 * @date 2019/6/20
 */
public class SpelUtils {
    private static final String TIME_REGEX = "^\\d+$";
    private static final long DEFAULT_EXPIRE = 7200L;

    public static long getExpireTime(String expire, Environment environment) {
        if (StringUtils.isNotBlank(expire)) {
            final boolean matches = expire.matches(TIME_REGEX);
            if (matches) {
                return Long.parseLong(expire);
            } else if (StringUtils.isNotBlank(expire)) {
                final String property = environment.getProperty(expire);
                if (property != null && property.matches(TIME_REGEX)) {
                    return Long.parseLong(property);
                }
            }
        }
        return DEFAULT_EXPIRE;
    }

    public static String parse(String spel, Method method, Object[] args) {
        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u =
                new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);
        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        return getString(spel, args, paraNameArr, parser, context);
    }

    /**
     * 支持 #p0 参数索引的表达式解析
     *
     * @param rootObject 根对象,method 所在的对象
     * @param spel       表达式
     * @param method     ，目标方法
     * @param args       方法入参
     * @return 解析后的字符串
     */
    public static String parse(Object rootObject, String spel, Method method, Object[] args) {
        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u =
                new LocalVariableTableParameterNameDiscoverer();
        String[] paraNameArr = u.getParameterNames(method);
        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser();
        //SPEL上下文
        StandardEvaluationContext context = new MethodBasedEvaluationContext(rootObject, method, args, u);
        //把方法参数放入SPEL上下文中
        return getString(spel, args, paraNameArr, parser, context);
    }

    private static String getString(String spel, Object[] args, String[] paraNameArr, ExpressionParser parser, StandardEvaluationContext context) {
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(spel).getValue(context, String.class);
    }
}
