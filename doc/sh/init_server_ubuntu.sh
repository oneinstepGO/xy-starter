# Ubuntu 20.04 LTS

# 更新安装包
sudo apt-get update -y

# 安装 vim
sudo apt install vim -y
# 安装 wget
sudo apt install wget -y
# 安装 unzip
sudo apt install unzip -y
# 安装 telnet
sudo apt install telnet -y
# 安装 net-tools
sudo apt install net-tools -y
# 安装 git
sudo apt install git -y


# 修改主机名
sudo vim /etc/hostname
# 查看
hostname

#设置时区i
sudo timedatectl set-timezone Asia/Shanghai
# 查看时区
timedatectl

# 关闭防火墙
sudo vim /etc/selinux/config
SELINUX=disabled

# 重启
sudo reboot



# 安装 jdk17
sudo dnf install java-17-openjdk-devel -y
# 查看版本
java -version

# 安装 maven
wget https://downloads.apache.org/maven/maven-3/3.9.5/binaries/apache-maven-3.9.5-bin.tar.gz
tar -xzf apache-maven-3.9.5-bin.tar.gz
sudo mv apache-maven-3.9.5 /usr/local

vi ~/.bashrc
export MAVEN_HOME=/usr/local/apache-maven-3.9.5
export PATH=$MAVEN_HOME/bin:$PATH

source ~/.bashrc

# 查看maven版本
mvn -v


# 创建工作目录
sudo mkdir -p /opt/projects/logs/admin
sudo mkdir -p /opt/projects/logs/api

# 安装 nginx
sudo apt-get install nginx -y
# 启动Nginx服务：
sudo systemctl start nginx
# 配置Nginx开机自启动
sudo systemctl enable nginx


# 查看nginx 状态
#sudo systemctl status nginx
## 停止 nginx
#sudo systemctl stop nginx
## 重新启动Nginx服务
#sudo systemctl restart nginx
## 刷新nginx 配置
#sudo systemctl reload nginx

# nginx 安装 GeoIP  模块



# 安装 https 证书
# 证书申请 https://certbot.eff.org/instructions?ws=nginx&os=arch

# 安装 snapd
sudo apt update -y
sudo apt install snapd -y

sudo systemctl enable --now snapd.socket
sudo ln -s /var/lib/snapd/snap /snap

#  重启 =============
sudo reboot

# 安装 证书
sudo apt remove certbot
sudo snap install --classic certbot
sudo ln -s /snap/bin/certbot /usr/bin/certbot

# 申请证书
sudo certbot certonly --nginx

# 刷新证书权限
sudo certbot renew --dry-run

# 安装 redis
sudo dnf install redis -y
# 修改 redis 配置文件
vim /etc/redis/redis.conf
# 启动 redis
sudo systemctl start redis
# 设置开机启动
sudo systemctl enable redis
# 重启 redis
sudo systemctl restart redis

sudo ssh-keygen -t rsa -b 4096 -C "1@gmail.com"
cd ~/.ssh/
cat id_rsa.pub
