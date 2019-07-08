## cgcg-redis-spring-boot-starter
服务redis缓存工具基础包
### 包功能简介

    一、注解
        @EnableCgCgRedis 启用CgCgRedis，需要启用才能使包生效
        @RedisNameSpace  注解在类上
            
             * RedisNameSpace缓存参数说明：
             * cache ->hash的name
             * expire->过期时间默认值是7200 可以填properties的key，也可以写固定值
             * unit-> 时间单位 RedisTimeUnit.SECONDS 默认是秒
             * RedisNameSpace配合RedisCache使用，RedisCache优先的原则
            
        @RedisCache      注解在方法上方
             * RedisCache缓存参数说明：
             * cache ->hash的name
             * key   ->hash里面的KV的key 支持SPEL表达式 #p0.id ，也可以写固定值 xxx
             * expire->过期时间默认值是7200 可以填properties的key，也可以写固定值
             * timeUnit-> 时间单位 RedisTimeUnit.SECONDS 默认是秒
             * type  -> 操作类型 RedisEnum.SEL,RedisEnum.UPD,RedisEnum.DEL 默认是SEL（查询）
             * lock -> 是否加锁 LockUnit.NULL LockUnit.LOCK LockUnit.UNLOCK ， 默认是NULL，自动根据操作类型判断是否加锁，其中UPD和DEL会自动上锁
         RedisCache注解方式缓存数据，有更新缓存时，会异步更新缓存数据。降低缓存对主流程的影响。
    二、工具类
        RedisHelper --Spring javabean类，通过DI注入来使用，提供缓存锁的功能
        
### 包引用
    
    git clone 代码到本地
    mvn clean deploy部署到maven的仓库服务
    
    <dependency>
        <groupId>com.cgcg</groupId>
        <artifactId>cgcg-redis-spring-boot-starter</artifactId>
        <version>XXXX对应版本号</version>    
    </dependency>
    
### 服务配置
    
    缓存连接配置同Spring boot配置相同即可
      
      

### 版本迭代
    1.2.1.release 修改redis缓存key-value的形式，每个kv使用单独过期时间（原先用的hset，过期时间配置会存在问题）
    1.2.2.release 支持base包中数据校验，添加优先顺序，@Validate和@RedisCache同时注解时，优先执行@RedisCache
