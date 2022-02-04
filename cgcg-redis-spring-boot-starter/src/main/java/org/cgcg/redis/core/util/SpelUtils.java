package org.cgcg.redis.core.util;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.lang.reflect.Method;

/**
 * 解析SPEL 表达式
 *
 * @author zhicong.lin
 * @date 2019/6/20
 */
public class SpelUtils {
    public static String parse(String spel, Method method, Object[] args) {
        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
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
     * @param spiel      表达式
     * @param method     ，目标方法
     * @param args       方法入参
     * @return 解析后的字符串
     */
    public static String parse(Object rootObject, String spiel, Method method, Object[] args) {
        if (ArrayUtils.isEmpty(args)) {
            return spiel;
        }
        //获取被拦截方法参数名列表(使用Spring支持类库)
        final LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();
        final String[] paraNameArr = u.getParameterNames(method);
        if (paraNameArr == null) {
            return spiel;
        }
        //使用SPELL进行key的解析
        final ExpressionParser parser = new SpelExpressionParser();
        //SPELL上下文
        final StandardEvaluationContext context = new MethodBasedEvaluationContext(rootObject, method, args, u);
        //把方法参数放入SPELL上下文中
        return getString(spiel, args, paraNameArr, parser, context);
    }

    private static String getString(String spiel, Object[] args, String[] paraNameArr, ExpressionParser parser, StandardEvaluationContext context) {
        for (int i = 0; i < paraNameArr.length; i++) {
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(spiel).getValue(context, String.class);
    }
}
