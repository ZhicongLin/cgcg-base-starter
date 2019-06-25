# cgcg-base-starter
spring boot 服务基础包
## cgcg-base-spring-boot-starter
服务基础包
### 包功能简介

    一、支持请求日志打印
    二、支持Swagger文档
    三、支持异常处理
    四、支持返回数据格式化
### 包引用
    
    git clone 代码到本地
    mvn clean deploy部署到maven的仓库服务
    
    <dependency>
        <groupId>com.github.cgcg</groupId>
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
    auth-ignore和log-ignore各自独立拦截，且如果有配置会优先选择(log-ignore>root,auth-ignore>root)
    
    二、swagger文档相关配置
    cgcg.swagger.apis   swagger扫描的包路径
    cgcg.swagger.name   swagger文档名称
    cgcg.swagger.desc   swagger文档描述
    cgcg.swagger.version    文档的版本号
    
    三、其他配置
    cgcg.format.response-data: true 开启数据格式化，默认false
    开启返回数据格式化后，返回的结果{ "code": 200, "data": "result-data", "message": "操作成功"}
      
      

