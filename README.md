# 基于SpringCloud的趋势投资分析系统

## 一、项目研究的主要内容

1. 根据金融投资领域的量化投资、趋势追踪、移动均线等理论，设计趋势投资算法。
2. 分布式和集群开发与ECS部署

- - - springCloud :Eureka server+Eureka client
    - 微服务之间通过http进行轻量通信：直接在properties或者yml配置访问url、微服务@enableDiscovery、Feign声明式web service
    - 对其中四个核心微服务进行集群，通过zuul进行网关转发与基本的轮询式负载均衡。
    - redis作为缓存，rabbitmq消息队列+bus总线实现动态配置，zipkin查看服务调用链，hystrix dashboard+turbine实现对集群的聚合监控



1. 根据从第三方获取到的原生资料，因为能力有限无法获取实时数据，故利用已有历史数据，用趋势投资算法对常见指数进行模拟回测，分析其收益与风险。

## 二、系统功能

1. 通过切换不同的指数，可以在**曲线分布图**上看到指数收益以及趋势投资收益的可视化区别
2. 在**收益一览**可以看到数字话的趋势投资收益比较
3. 在**交易统计**可以看到总共多少次交易，盈亏比，平均盈利比率，亏损比率。
4. 在**收益分布对比表**可以看到看到不同年份的收益比较表
5. 在**收益分布对比图**可以看到不同年份的收益比较图
6. 在**交易明细**可以看到每一笔交易的开始结束时间，对应的收盘点以及盈亏情况 除此之外还可以调整参数观察不同参数情况下的趋势投资收益变化

- 调整**均线**
- 调整**买入阈值**
- 调整**卖出阈值**
- 引入**手续费**计算
- 调制**开始** **结束**日期

## 三、软件架构

- `Java`语言开发
- 前端 `html`, `CSS`, `Javascript`,` Axios`, `JQuery` ,`Bootstrap`, `Vue.js`,`Chart.js`
- 后端框架部分 `springmvc` ,`springCloud/springBoot`一站式分布式解决方案
- 其他 `redis`缓存,`rabbitmq`消息对列，`zipkin`链路追踪,`turbine`断路器聚合,`hystrix dashboard`断路器监控,`quartz`定时器，`docker`容器

**开发工具**

`Intellij IDEA`,`Maven`，`git`项目迭代

## 分布式集群部署

环境准备:

> ECS多台

1. OS:Linux centOs7.x x64 (如果使用docker安装rabbitmq和redis,要求内核高于3.10版本以及64位机器)
2. 安装jdk1.8
3. 设置服务器安全组规则开放对应端口;防火墙也要开放对应端口,或者关闭防火墙(不推荐)

> \#systemctl status firewalld        //查看防火墙状态`#systemctl start firewalld //开启防火墙`
>
> \#firewall-cmd --list-all      //查看当前放行策略```
>
> firewall-cmd --permanent --zone=public --add-port=8080/tcp
>
> 参数介绍：
>
> 1、firewall-cmd：是Linux提供的操作firewall的一个工具；
>
> 2、--permanent：表示设置为持久；
>
> 3、--add-port：标识添加的端口；
>
> \#firewall-cmd --reload         //重新加载配置使生效



1. 安装一台redis-server，一台rabbitmq-server

- - - version: rabbitmq 3.7.24-management redis 5.0.7
    - rabbitmq需要erlang环境支持，参考rabbitmq的github官方教程安装erlang
    - docker启动redis和rabbitmq需要使用-p指定端口映射，最好-v进行配置文件映射，具体教程请前往百度

部署微服务

- 微服务

> eureka-server:注册中心
>
> 
>
> index-gather-store-service：第三方数据采集服务
>
> 
>
> third-part-index-data-project：第三方数据中心
>
> 
>
> index-config-server：配置总线中心
>
> 
>
> index-codes-server：指数代码服务
>
> 
>
> index-data-server：指数数据服务
>
> 
>
> trend-trading-backtest-service：模拟回测业务服务
>
> 
>
> trend-trading-backtest-view：模拟回测视图服务
>
> 
>
> index-hystrix-dashboard：断路器监控服务
>
> 
>
> zipkin-server：链路追踪服务
>
> 
>
> index-turbine：断路器聚合服务
>
> 
>
> index-zuul-service ：网关转发中心服务

- ​     部署顺序 -->(链路追踪服务)



-->(注册中心)



-->(第三方数据中心)



-->(第三方数据采集服务)



-->(配置总线中心)



-->(指数代码服务集群)



-->(指数数据服务集群)



-->(模拟回测业务服务集群)



-->(模拟回测视图服务集群)



-->(断路器监控服务)



-->(断路器聚合服务)



-->(网关转发中心服务)



- 注意事项及项目中遇到的问题:



- - 云服务器部署运行jar包姿势:



> nohup java -jar -Xms300m -Xmx300m xxxx.jar --server.port=8080 >xxxx.log 2>&1 &



解释:

- - nohup 不间断运行，关闭终端等程序不会退出运行
  - -Xms300m 指定分配jvm初始堆内存300M -Xmx 指定分配jvm最大堆内存300M （可选项，建议两者设成一样）
  - --双横杠为程序运行配置，对应properties或者yml里的配置，为点进式设置
  - \>xxxx.log 重定向标准输出到xxxx.log
  - 2>&1 2代表错误输出，表示将错误也输出到前面设置的标准输出日志文件
  - & 后台运行

- eureka服务调用问题:

> eureka:
>
>   instance:
>
> ​    prefer-ip-address: **true**              //部署到公网必须指定此选项
>
> ​    ip-address: xx.xx.xx.xxx              //指定公网ip  
>
>   client:
>
> ​    service-url:
>
> ​      defaultZone: http://xx.xx.xxx.xxx:8761/eureka/
>
> //这里是个巨坑,如果不配置上面的instance选项，服务会以hostname注册到eureka server，如果通过eureka服务之间直接进行通信，会失败。prefer-ip-address:true可以让微服务以ip注册，但是仍然有问题，因为多网卡机器ip-address一般获取到的是内网地址，如果不手动指定ip-address为公网ip,服务调用依然会失败。而ip-address对于要部署到不同机器上的集群组件都不一样，这里可以不写,但必须指定prefer-ip-address =true，然后在部署的时候在-jar xxxx.jar后面用--eureka.instance.ip-address=xx.xx.xx.xxx灵活指定。



> 因为用到了bus总线和rabbitmq，zipkin需要额外配置rabbitmq连接信息才能获取到服务依赖，格式为
>
> nohup java -jar zipkin-server-2.10.1-exec.jar --zipkin.collector.rabbitmq.addresses=xx.xx.x.xxx --zipkin.collector.rabbitmq.username=xxx zipkin.collector.rabbitmq.password=xxx >zipkin.log 2>&1 &
>
> 链路信息需要进行业务访问之后才能看到，访问地址：<http://47.93.188.100:9411/zipkin/dependency/>
>
> 还有一个大坑就是eureka的超时问题，感觉目前eureka的断路器、ribbon负载均衡、zuul之间的超时很复杂，由于微服务刚启动时一般比较慢，要手动配置一下超时，不然会稳定熔断。。。本项目zuul路由是用service-id来指定的
>
> zuul:
>
> routes:
>
> api-a:
>
> path: /api-codes/**
>
> service-id: INDEX-CODES-SERVICE
>
> api-b:
>
> path: /api-backtest/**
>
> service-id: TREND-TRADING-BACKTEST-SERVICE
>
> api-c:
>
> path: /api-view/**
>
> serviceId: TREND-TRADING-BACKTEST-VIEW
>
> 
>
> 依赖的是hystrix和ribbon的超时设置，而且模拟回测业务服务和指数数据服务之间用了feign声明式web service，如果开启断路器，也依赖hystrix与ribbon超时设置，代码为
>
> 
>
> hystrix:
>
> command:
>
> default:
>
> execution:
>
> isolation:
>
> thread:
>
> timeoutInMilliseconds: 60000
>
> ribbon:
>
> ReadTimeout: 10000
>
> ConnectTimeout: 10000
>
> MaxAutoRetries: 0                   #默认值，可以不管
>
> MaxAutoRetriesNextServer: 1         #默认值，可以不管
>
> 这里还有一个问题，是hystrix的timeoutInMilliseconds必须大于ribbon的超时时间，确实应该要这样，因为hystrix断路器是用来断路时进行后续的熔断处理的，如果比负载均衡的时间还短，回导致正常的业务调用无法进行下去，ribbon的超时时间为(ReadTimeout+ConnectTimeout)*(MaxAutoRetries+1)*(MaxAutoRetriesNextServer+1)
>
> 还有一个问题是这里用config server作为配置消息分发中心，需要配置远程配置存放仓库
>
> spring:
>
> application:
>
> name: index-config-server
>
> cloud:
>
> config:
>
> label: master                  ##指定分支
>
> profile: dev                 ##配置环境
>
> server:
>
> git:
>
> uri: <https://gitee.com/m_ls/sys_trend.git>
>
> searchPaths: config-repo
>
> bus:
>
> enabled: true                    #开启消息总线
>
> trace:                           #开启消息追踪
>
> enabled: true
>
> 实现动态配置管理的方法是其他服务订阅总线消息，当远程配置文件更改后，向config-server发送一个post请求，路径为 config-server的ip:端口号/actuator/bus-refresh，成功之后回收到状态码为204 not contend的response，每次更新之后手动发送可以借助postman，但这个太傻逼了，可以借助github和gitee的webhook，把钩子地址设置成上面这个就行，每次push之后就会自动发送请求，congif-server收到后，就会借助rabbitmq发送广播通知订阅者配置刷新啦去重新获取，实现动态配置。webhook默认的话好像不行，因为他的请求体里太臃肿了，springcloud收到后会解析不了，所以会刷新失败。解决方法有两种
>
> 
>
> 1. ​          org.springframework.cloud          spring-cloud-config-monitor 
>
> 
>
> 给配置中心加上这个这个依赖，钩子那里设置成config-server的ip:端口号/monitor就可以了。
>
> 2.这个方法没有实践，看到有人说起，仅供参考：大概思路是继承httpServletWrapper