## cgcg-jobs-web-starter

    任务调度客户端及应用服务端

###技术描述及功能简介

    继承quartz的功能
    启动时加载数据库中配置的任务数据，注册成对应的任务调度器，通过RMI访问客户端接口来实现任务调度
    一、启用任务调度
        @EnableJobsClient 注解在spring能扫描到的类上，如自定义的启动类WebApp.class
    二、业务功能开发
        1、@Jobs 注解到任务执行的类上
        2、@Jobs中的参数value值是对应的任务唯一ID，请规划好ID的定义，不要重复，平台每个环境对应的ID值唯一
        3、通过@Jobs注解的类需实现MyJobs接口，每个任务需要定义一个实现类
    三、分布式支持
        1、本功能需要将应用端手动注册到当前服务的数据库表，所以还不能很好的支持微服务的自动DEVOPS的场景(自动部署后仍需手动配置到服务)
        2、未实现负载均衡    
###包引用

    git clone 代码到本地
    mvn clean deploy部署到maven的仓库服务
    
    <dependency>
        <groupId>com.cgcg</groupId>
        <artifactId>cgcg-jobs-web-starter</artifactId>
        <version>XXXX对应版本号</version>    
    </dependency>

###服务配置

    执行SQL脚本
    配置对应的数据库连接