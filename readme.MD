# 项目简介

- 项目名称：xy-starter
- 项目描述：
    - 一个基于 spring-boot 3.2.5 的快速开发脚手架，单体项目，采用多模块分层架构，包含后台管理系统和前后端认证等。
    - 集成和实现了一些常用的功能，可直接进行简单项目的快速上手开发。
    - 如：登陆认证、权限控制、全局异常处理、参数校验、统一返回结果、导出 excel、分布式锁、分布式 id 生成器、redis
      工具类、日志处理、日志打印、日志切面、请求追踪、频率限制、Spring bean 的路由功能、权限控制等等

### 1、如何启动

- 1.1、本地启动
    - 1、安装 mysql 8.0 和 redis
    - 2、创建数据库，执行 `db/init.sql` 数据库脚本
    - 3、修改 `application.yml` 中的数据库配置和redis 配置
    - 4、启动项目
        - 4.1、启动 `com.oneinstep.starter.admin.AdminApplication` 类，此为后台管理系统
        - 4.2、启动 `com.oneinstep.starter.api.ApiApplication` 类，此为前端接口
    - 5、可调试位于 `doc/api/*.http` 文件中的一些接口
- 1.2、docker 启动
    - 1、安装 docker 和 docker-compose
    - 2、编译项目
        - 2.1、执行 `mvn clean package -Dmaven.test.skip=true` 编译项目
    - 3、执行 `docker-compose up -d` 启动项目
    - 4、可调试位于 `doc/api/*.http` 文件中的一些接口

### 2、项目结构

- 2.1、`xy-starter-core` 为核心模块，包含一些基础工具类和配置，基本不用改动
    - mybatis-plus 配置、分页处理、结果转换
    - 全局异常处理、参数校验、统一返回结果
    - 导出 excel 工具类
    - 分布式锁工具、注解式分布式锁、分布式 id 生成器、redis 工具类、扩展功能
    - 日志处理、日志打印、日志切面、请求追踪
    - 频率限制
    - Spring bean 的路由功能
    - 各种常用 工具类
- 2.2、`xy-starter-common` 为一些公共模块，包含一些公共的配置和工具类，后续可根据业务需求进行扩展【公用代码、自行扩展】
    - 2.2.1、缓存模块
        - 缓存工具类、缓存配置（使用Caffeine + Spring Cache）、缓存注解
            - 结合 Cacheable、CacheEvict、CachePut 注解，实现缓存的增删改查
- 2.3、`xy-starter-security` 为安全模块，包含登陆、认证、权限等安全配置和工具类，后续可根据业务需求进行扩展 【安全代码、可扩展】
    - 2.3.1、`xy-starter-security-common` 基于 spring-security 和 jwt 的安全模块，包含一些基础的安全配置和工具类
        - 认证基于 jwt、权限校验、用户信息获取、权限信息获取、用户信息存储、权限信息存储
    - 2.3.2、`xy-starter-security-admin` 为后台管理系统安全模块，包含后台管理系统的安全配置和常用认证、权限校验方法
        - 后台管理系统使用 用户名 + 密码 登陆，登陆后使用 jwt 进行认证，基于 角色-权限 进行权限控制
        - 接口方法上使用 @PreAuthorize("hasRole('YOUR_ROLE_NAME')") 控制需要的角色
        - 接口方法上使用 @PreAuthorize("hasAuthority('YOUR_AUTHORITY_NAME1') && hasAuthority('YOUR_AUTHORITY_NAME2')")
          控制需要的权限
    - 2.3.3、`xy-starter-security-api` 为前端接口安全模块，包含前端接口的安全配置和常用认证、权限校验方法
        - 前端接口使用 多种登陆方式，登陆后使用 jwt 进行认证，没有权限控制
- 2.4、`xy-starter-sys` 为后台管理系统通用 用户、角色、权限、菜单等通用代码，基本不用改动
- 2.5、`xy-starter-business-admin` 为后台管理系统业务模块，用户的管理系统的业务代码写在此模块中【管理端业务代码】
- 2.6、`xy-starter-business-api` 为前端接口业务模块，用户的前端接口的业务代码写在此模块中【用户端业务代码】
- 2.7、`xy-starter-main-admin` 为后台管理系统启动模块，启动后台管理系统的启动类、配置类、Controller 等写在此模块中
- 2.8、`xy-starter-main-api` 为前端接口启动模块，启动前端接口的启动类、配置类、Controller 等写在此模块中

### 3、技术栈

- 3.0、JDK：17 ; 数据库：MySQL 8.0 ; 缓存：Redis
- 3.1、基础框架：Spring Boot 3.2.5
- 3.2、持久层框架：Mybatis-Plus 3.5.6
- 3.3、安全框架：Spring Security 6.2.4 + JWT
- 3.4、接口文档：knife4j
- 3.5、日志框架：Logback
- 3.6、Redis 客户端：Redisson 3.29.0