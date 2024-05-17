CREATE DATABASE IF NOT EXISTS `xy-starter` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `xy-starter`;


-- 后台用户
DROP TABLE IF EXISTS `t_sys_user`;
CREATE TABLE `t_sys_user`
(
    `user_id`          bigint        NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`         varchar(50)   NOT NULL COMMENT '登录用户名',
    `password`         varchar(255)  NOT NULL COMMENT '登录密码',
    `google_secret`    varchar(128)           DEFAULT NULL COMMENT '登陆时 google 密钥',
    `nick_name`        varchar(50)   NOT NULL COMMENT '昵称',
    `last_login_time`  datetime               DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip`    varchar(50)            DEFAULT NULL COMMENT '最后登录IP',
    `login_white_list` varchar(2048) NOT NULL DEFAULT '127.0.0.1' COMMENT '登录白名单，用英文逗号分隔，不设置表示允许所有',
    `creator_id`       bigint                 DEFAULT NULL COMMENT '创建者ID',
    `remark`           varchar(255)  NOT NULL DEFAULT '' COMMENT '备注',
    `status`           tinyint       NOT NULL DEFAULT '1' COMMENT '状态 0 禁用 1 正常',
    `deleted`          tinyint       NOT NULL DEFAULT '0' COMMENT '删除标识 0 未删除 1 已删除',
    `version`          bigint        NOT NULL DEFAULT '0' COMMENT '版本号',
    `create_time`      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `udx_username` (`username`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 10000000
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='后台用户';
INSERT INTO `t_sys_user`(`user_id`, `username`, `password`, `google_secret`, `nick_name`,
                         `last_login_time`, `last_login_ip`,
                         `login_white_list`, `creator_id`, `remark`, `status`, `version`, `create_time`,
                         `update_time`)
VALUES (1, 'admin', '$2a$10$gqHrslMttQWSsDSVRTK1OehkkBiXsJ/a4z2OURU./dizwOQu5Lovu', NULL, '超管',
        NULL, NULL, '', NULL, '', 1, 0, NOW(),
        NOW());


-- 角色
DROP TABLE IF EXISTS `t_sys_role`;
CREATE TABLE `t_sys_role`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_name`   varchar(100)      DEFAULT NULL COMMENT '角色名称',
    `remark`      varchar(100)      DEFAULT NULL COMMENT '备注',
    `status`      tinyint  NOT NULL DEFAULT '1' COMMENT '状态 0 禁用 1 正常',
    `creator_id`  bigint            DEFAULT NULL COMMENT '创建者ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_role_name` (`role_name`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='角色';
INSERT INTO t_sys_role (id, role_name, remark, status, creator_id, create_time, update_time)
VALUES (1, '普通管理员', '普通管理员', 1, 1, NOW(), NOW());


-- 账户与角色对应关系
DROP TABLE IF EXISTS `t_sys_user_role`;
CREATE TABLE `t_sys_user_role`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `sys_user_id` bigint            DEFAULT NULL COMMENT '后台用户ID',
    `role_id`     bigint            DEFAULT NULL COMMENT '角色ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='账户与角色对应关系';


-- 权限
DROP TABLE IF EXISTS `t_sys_menu`;
CREATE TABLE `t_sys_menu`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `parent_id`   bigint            DEFAULT NULL COMMENT '父菜单ID，一级菜单为0',
    `name`        varchar(50)       DEFAULT NULL COMMENT '菜单名称',
    `url`         varchar(200)      DEFAULT NULL COMMENT '菜单URL',
    `perms`       varchar(500)      DEFAULT NULL COMMENT '授权(多个用逗号分隔，如：user:list,user:create)',
    `type`        int               DEFAULT NULL COMMENT '类型 0-目录 1-菜单 2-按钮',
    `icon`        varchar(50)       DEFAULT NULL COMMENT '菜单图标',
    `order_num`   int               DEFAULT NULL COMMENT '排序',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='权限';

INSERT INTO `t_sys_menu`(`id`, `parent_id`, `name`, `url`, `perms`, `type`, `icon`, `order_num`)
VALUES (1, 0, '首页', '/home', '', 0, 'Home', 1),
       (2, 0, '用户管理', '/user-manage', '', 0, 'Customer', 2),
       (90, 0, '系统管理', '/system', '', 0, 'Settings', 100),

       (200, 2, '用户列表', '/user-manage/user', '', 1, 'People', 1),

       (900, 90, '管理员管理', '/system-manage/sysUser', '', 1, 'People', 1),
       (901, 90, '角色管理', '/system/role', '', 1, 'LogoSnapchat', 2),
       (902, 90, '系统参数管理', '/system/config',
        'admin:sys:config:list,admin:sys:config:update', 1, 'Earth', 3),

       (1000, 200, '查看', NULL, 'api:user:list', 2, NULL, 0),

       (1100, 900, '查看', NULL, 'admin:sys:sysUser:list', 2, NULL, 0),
       (1101, 900, '新增', NULL, 'admin:sys:sysUser:save,admin:sys:role:list', 2, NULL, 0),
       (1102, 900, '编辑', NULL, 'admin:sys:sysUser:update,admin:sys:role:list', 2, NULL, 0),
       (1103, 900, '删除', NULL, 'admin:sys:sysUser:delete', 2, NULL, 0),
       (1104, 900, '修改密码', NULL, 'admin:sys:sysUser:updatePassword', 2, NULL, 0),
       (1105, 900, '重置谷歌验证码', NULL, 'admin:sys:sysUser:resetGoogleSecret', 2, NULL, 0),

       (1150, 901, '查看', NULL, 'admin:sys:role:list', 2, NULL, 0),
       (1151, 901, '新增', NULL, 'admin:sys:role:save,admin:sys:menu:list', 2, NULL, 0),
       (1152, 901, '编辑', NULL, 'admin:sys:role:update,admin:sys:menu:list', 2, NULL, 0);


-- 角色与权限对应关系
DROP TABLE IF EXISTS `t_sys_role_menu`;
CREATE TABLE `t_sys_role_menu`
(
    `id`          bigint   NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `role_id`     bigint            DEFAULT NULL COMMENT '角色ID',
    `menu_id`     bigint            DEFAULT NULL COMMENT '菜单ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='角色与权限对应关系';

-- 系统配置
DROP TABLE IF EXISTS `t_sys_config`;
CREATE TABLE `t_sys_config`
(
    `id`          bigint        NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `param_key`   varchar(50)   NOT NULL COMMENT '配置key',
    `param_value` varchar(2000) NOT NULL DEFAULT '' COMMENT '配置值',
    `remark`      varchar(500)           DEFAULT NULL COMMENT '备注',
    `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `udx_key` (`param_key`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='系统配置';
INSERT INTO `t_sys_config`(`id`, `param_key`, `param_value`, `remark`, `create_time`,
                           `update_time`)
VALUES (1, 'globalLoginWhiteList', '', '登录白名单', NOW(), NOW()),
       (2, 'runJobIp', '', '定时任务运行服务器ip', NOW(), NOW());


-- 用户
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user`
(
    `user_id`         bigint       NOT NULL COMMENT '用户ID',
    `nick_name`       varchar(50)  NOT NULL COMMENT '昵称',
    `real_name`       varchar(50)  NULL COMMENT '真实姓名',
    `gender`          char(1)               DEFAULT 'M' COMMENT '性别，M(男) or F(女)',
    `birthday`        date                  DEFAULT NULL COMMENT '生日，例如：2009-11-27',
    `avatar`          varchar(100) NULL     DEFAULT 'https://api.multiavatar.com/Starcrasher.png' COMMENT '头像',
    `email`           varchar(100)          DEFAULT NULL UNIQUE COMMENT '邮箱',
    `mobile`          varchar(100) NOT NULL UNIQUE COMMENT '手机号',
    `wx_open_id`      varchar(100)          DEFAULT NULL UNIQUE COMMENT '微信开放平台ID',
    `login_password`  varchar(255)          DEFAULT NULL COMMENT '登录密码',
    `pay_password`    varchar(255)          DEFAULT NULL COMMENT '支付密码',
    `google_secret`   varchar(128)          DEFAULT NULL COMMENT '登陆时 google 密钥',
    `reg_time`        datetime     NOT NULL COMMENT '注册时间',
    `reg_ip`          varchar(50)           DEFAULT NULL COMMENT '注册IP',
    `last_login_time` datetime              DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip`   varchar(50)           DEFAULT NULL COMMENT '最后登录IP',
    `remark`          varchar(255) NOT NULL DEFAULT '' COMMENT '备注',
    `status`          tinyint      NOT NULL DEFAULT '1' COMMENT '状态 0 禁用 1 正常',
    `deleted`         tinyint      NOT NULL DEFAULT '0' COMMENT '删除标识 0 未删除 1 已删除',
    `version`         bigint       NOT NULL DEFAULT '0' COMMENT '版本号',
    `create_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`user_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_general_ci COMMENT ='用户';

INSERT INTO `t_user`(`user_id`, `nick_name`, `real_name`, `gender`, `birth_date`, `avatar`,
                     `email`, `mobile`, `wx_open_id`, `login_password`, `pay_password`, `google_secret`,
                     `reg_time`, `reg_ip`, `last_login_time`, `last_login_ip`, `remark`, `status`,
                     `deleted`, `version`, `create_time`, `update_time`)
VALUES (1000000, '小妹', '肖美美', 'F', '2000-01-01', 'https://api.multiavatar.com/Starcrasher.png',
        '123fsd413@163.com', '18888888888', NULL, '$2a$10$gqHrslMttQWSsDSVRTK1OehkkBiXsJ/a4z2OURU./dizwOQu5Lovu',
        NULL, NULL, '2021-01-01 00:00:00', '123.12.3.4', NULL, NULL, '', 1, 0, 0, NOW(), NOW());


