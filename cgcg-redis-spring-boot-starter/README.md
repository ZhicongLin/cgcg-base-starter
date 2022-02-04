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
    1.3.2.release 一、修改redis默认过期时间为3600-7200中取随机数，即未配置过期时间时，过期时间默认为 1-2个小时
                  二、修改redis缓存配置参数expire为String[]
                      当配置单个时，与之前相同，固定缓存过期时间
                      当配置两个时，取两值之间的随机值（long）
                      当配置大于两个时，只会在多个数之间取随机数（这种方式至少要3个起）
                  三、增加缓存穿透处理，处理逻辑为同一个缓存key，穿透到数据库后，1分钟内由缓存直接返回null值
                      添加缓存hash，保存穿透的key，定时1小时清理一次已经不再穿透的key，避免hash的key堆积
                  四、添加Redis消息队列的使用
                     @EnableRedisMQ 开启MQ的应用
                     @Rmqp 为生成这的方法aop注解，由aop实现推送，推送的内容为执行方法的返回值
                     @Rmqc 为消费者接收redis消息的入口方法，参数为（Message msg, String chnnal）或者(Message msg) 其中Message为消息内容，chnnal为通道id
                     @RmqListener 注解到消费者端注解@Rmqc方法的类上
                    此外：推送消息的，有提供工具类RedisMqPublisher.send静态方法
                  五、增加spring.redis.serial序列化工具配置，默认fst（FastSerializationRedisSerializer）
                        可选json（FastJsonRedisSerializer）、kryo(KryoRedisSerializer)、jdk(原生)、fst（FastSerializationRedisSerializer）