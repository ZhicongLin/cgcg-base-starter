# cgcg-base-starter
spring boot 服务基础包
## cgcg-base-spring-boot-starter
服务基础包
    
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
      
      

