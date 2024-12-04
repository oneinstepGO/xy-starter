#!/bin/bash
# 发布 Java 服务

cd /opt/projects
# 删除旧目录
sudo rm -rf /opt/projects/xy-starter

# 拉取git代码
git clone git@github.com:${GIT_USERNAME}/xy-starter.git
# 编译Java代码
(cd /opt/projects/xy-starter && mvn clean package)

# 复制jar文件
sudo cp /opt/projects/xy-starter/one-xy-starter-main-admin/target/one-xy-starter-main-admin-1.0.0-SNAPSHOT.jar /opt/projects/xy-starter-admin.jar
sudo cp /opt/projects/xy-starter/one-xy-starter-main-api/target/one-xy-starter-main-api-1.0.0-SNAPSHOT.jar /opt/projects/xy-starter-api.jar

# 切换目录
cd /opt/projects || exit 1

# 查找旧Java进程并杀死
PID=$(jps -lm | grep xy-starter-admin | awk '{print $1}')
if [ -n "$PID" ]; then
    echo "Killing process xy-starter-admin $PID"
    kill -9 "$PID"
fi

# 后台启动Java服务
nohup java -jar -Xms7000m -Xmx7000m -Duser.timezone=Asia/Shanghai -Dspring.profiles.active=prod xy-starter-admin.jar > /dev/null 2>&1 &
echo "Java service xy-starter-admin restarted"

# 查找旧Java进程并杀死
PID=$(jps -lm | grep xy-starter-api | awk '{print $1}')
if [ -n "$PID" ]; then
    echo "Killing process xy-starter-api $PID"
    kill -9 "$PID"
fi

nohup java -jar -Xms7000m -Xmx7000m -Duser.timezone=Asia/Shanghai -Dspring.profiles.active=prod xy-starter-api.jar > /dev/null 2>&1 &
echo "Java service xy-starter-api restarted"

# 删除 git clone 的文件
sudo rm -rf /opt/projects/xy-starter
