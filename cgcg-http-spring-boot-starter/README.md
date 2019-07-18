# Rest Template Framework

    这是一个解决前端发起http请求服务的框架。
    提供类似于Feign框架的处理方案，解决前端HTTP请求混乱的问题。
    同时简化HTTP请求的代码量。
    
## maven引用信息
    
    <dependency>
        <groupId>com.cgcg</groupId>
        <artifactId>cgcg-http-spring-boot-starter</artifactId>
        <version>${version}</version>
    </dependency>
    
## 版本迭代信息--${version}   

### 1.0.0.release

     1.提供Http请求功能，GET.POST.PUT.DELETE等方式
     2.提供并使用spring ioc
     3.提供RestFilter过滤器
     4.提供EnableRestCients开关等注解
     5.提供注解扫描和配置扫描两种方案可选
     6.提供jdk代理方式和cglib代理方式
     7.新增LoadMapping文件读取注解，DinamicaMapping动态url和mehtod请求方式的注解
     8.参数@RequestHeader支持，@RequestHeader("value")value不为空时支持string，为空时，支持全部对象属性Object转成header
### 1.0.1.release
    * 新增
    新增fallback熔断机制
    添加实现类，实现RestClient注解的interface，把实现类配置到RestClient的fallback里面
    新增熔断配置rest.fallback.enable默认true
    新增熔断配置rest.fallback.expire默认60000L、
    * 修改
    日志打印存在的BUG