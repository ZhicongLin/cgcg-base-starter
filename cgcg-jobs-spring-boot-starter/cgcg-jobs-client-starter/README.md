## cgcg-jobs-client-starter

    任务调度客户端及应用服务端

###技术描述及功能简介

    实现灵活配置任务调度的功能
    通过扫描注解，将任务执行器注册到Spring容器中，并暴露RMI接口（端口1234（可配置））给任务调度中心(WEB端)调度
    一、启用任务调度
        @EnableJobsClient 注解在spring能扫描到的类上，如自定义的启动类WebApp.class
    二、业务功能开发
        1、@Jobs 注解到任务执行的类上
        2、@Jobs中的参数value值是对应的任务唯一ID，请规划好ID的定义，不要重复，平台每个环境对应的ID值唯一
        3、通过@Jobs注解的类需实现MyJobs接口，每个任务需要定义一个实现类

###包引用

    git clone 代码到本地
    mvn clean deploy部署到maven的仓库服务
    
    <dependency>
        <groupId>com.cgcg</groupId>
        <artifactId>cgcg-jobs-client-starter</artifactId>
        <version>XXXX对应版本号</version>    
    </dependency>

###服务配置

    默认开启1234端口为执行器调度的端口
    如果有冲突或者需要自定义请在properties中通过cgcg.jobs.port=1234修改端口
    端口的配置需要与cgcg-jobs-web-starter后台数据库中task_info表的port字段值一致