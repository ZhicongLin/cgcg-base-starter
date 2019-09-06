# cgcg-base-starter
spring boot 服务基础包
## cgcg-base-spring-boot-starter
服务基础包
### 包功能简介

    一、支持请求日志打印
    二、支持Swagger文档
        访问路径：http://yourdomain/swagger-ui.html
        Swagger写法详见 官网地址：https://swagger.io/
    三、支持异常处理
        1.处理spirng-mvc常见异常，
        2.处理自定义异常 throw new CommonException(code, message);
          （CommonException可以被继承）
        3.404异常捕获需要增加下面两条配置，不然不会被捕获
            spring.mvc.throw-exception-if-no-handler-found=true
            spring.mvc.static-path-pattern=/statics/**
    四、支持返回数据格式化
        如：源数据 {"yes":"ok"} 格式化后：{ "code": 200, "data": {"yes":"ok"}, "message": "操作成功"}
    五、接口数据加密(DES)
        @EnableEncryptApi -- 加密开关
        @Encrypt -- 方法或者类上注解，注解的接口进行参数的解密和结果的加密
        @EncryptController 封装@RestController --功能同@Encrypt
        参数解密只支持@RequestBody的解密操作
            XXX加密参数XXX ==> 解密后{"yes":"ok"}
        返回结果只支持@ResponseBody的加密操作
            加密结果如{ "code": 200, "data": "xxxxxx加密后的字符串xxxx", "message": "操作成功"}
            如果返回对象不包含配置的加密字段名，则进行整个对象加密
    六、线程池
        ThreadPoolManager.executor(Runnable r); 没有返回值调用
        ThreadPoolManager.submit(Runnable r); 有返回值调用
        ThreadPoolManager.cancel(Runnable r); 取消线程执行
        ExecutorTask 执行任务类，cancel和finish属性，能在finish之前停止执行当前线程
### 包引用
    
    git clone 代码到本地
    mvn clean deploy部署到maven的仓库服务
    
    <dependency>
        <groupId>com.cgcg</groupId>
        <artifactId>cgcg-base-spring-boot-starter</artifactId>
        <version>XXXX对应版本号</version>    
    </dependency>
    
### 服务配置
    
    一、拦截器相关配置
    cgcg.interceptor.auth=true 开启服务鉴权配置
    如果开启鉴权配置，则要实现AuthService接口（实现类如下：）
    
        @Service
        public class AuthServiceImpl implements AuthService {
        
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                final String token = request.getHeader("token");
                if (token != null) {
                    return true;
                }
                return false;
            }
        }
        
    cgcg.interceptor.ignore.root 配置全局忽略的路径
    cgcg.interceptor.ignore.auth-ignore: 配置忽略鉴权路径
    cgcg.interceptor.ignore.log-ignore: 配置忽略打印请求日志路径
    忽略资源路径默认"/swagger-resources/**", "/swagger-ui.html", "/error", "/webjars/**"
    auth-ignore和log-ignore各自独立拦截，且如果有配置会优先选择(log-ignore>root,auth-ignore>root)
    
    二、swagger文档相关配置
    cgcg.swagger.apis   swagger扫描的包路径
    cgcg.swagger.name   swagger文档名称
    cgcg.swagger.desc   swagger文档描述
    cgcg.swagger.version    文档的版本号
    
    三、其他配置
    cgcg.format.response-data: true 开启数据格式化，默认false
    cgcg.format.class-name: com.xxx.xx.ResultXX 有默认类，
        如果需要自定义格式化类，格式化类需要提供静态方法public ResultXX static success(T data);
    cgcg.format.des.* 如果开启了数据加密，这个密钥需要配置
      
      

