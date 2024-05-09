FROM openjdk:17.0.2

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

RUN mkdir -p /opt/projects/xy-starter

WORKDIR /opt/projects/xy-starter

ADD ./xy-starter-main-admin/target/xy-starter-main-admin-1.0.0-SNAPSHOT.jar ./

EXPOSE 8085

CMD java -jar -Xms2000m -Xmx2000m -Dspring.profiles.active=docker xy-starter-main-admin-1.0.0-SNAPSHOT.jar