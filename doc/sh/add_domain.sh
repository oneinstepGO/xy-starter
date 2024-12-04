#!/bin/bash
# 添加不存在的域名到nginx配置
# 检查是否填写了 域名参数
if [ -z "$1" ]; then
    echo "Usage: $0 <domain>"
    exit 1
fi

domain=$1

# Step 0: 先解析域名到该机器
# Step 1: 检查是否已存在该域名配置
if [ -f "/etc/nginx/conf.d/$domain.conf" ]; then
    echo "已存在该域名配置."
    exit 1
fi

# Step 2: 复制和修改nginx 配置
cd /etc/nginx/conf.d/
cp template.txt $domain.conf

# 将 your_domain 替换成 你的域名
sed -i "s/your_domain/$domain/g" $domain.conf

echo "添加nginx http 配置文件成功"

# Step 3: 检查 nginx 配置
nginx_test=$(sudo nginx -t)
if [[ $nginx_test != *"syntax is ok"* && $nginx_test != *"test is successful"* ]]; then
    echo "nginx 配置检查失败，中止脚本."
    exit 1
fi

echo "nginx配置检查成功 1..."

# Step 4: 重启 nginx
sudo systemctl restart nginx
echo "重启nginx 成功"

# Step 5: 请求域名证书
echo "请求https 证书..."
sudo certbot certonly --nginx -d $domain

# Step 6: 修改域名 https 配置
ssl_config="listen       443 ssl http2;\nlisten       [::]:443 ssl http2;\nserver_name  $domain;\nssl_certificate /etc/letsencrypt/live/$domain/fullchain.pem;\nssl_certificate_key /etc/letsencrypt/live/$domain/privkey.pem;\nssl_session_cache shared:SSL:10m;\nssl_session_timeout  10m;\nssl_protocols TLSv1.2;\nssl_prefer_server_ciphers on;\nssl_ciphers \"EECDH+AESGCM:EDH+AESGCM:AES256+EECDH:AES256+EDH\";"

# 替换 https 证书配置
sed -i "/listen       80;/,/server_name  $domain;/d" $domain.conf
sed -i "s|# Insert SSL configuration here|$ssl_config|g" $domain.conf

echo "修改域名 https 配置成功..."

# Step 7: 再次检查 nginx 配置
nginx_test=$(sudo nginx -t)
if [[ $nginx_test != *"syntax is ok"* && $nginx_test != *"test is successful"* ]]; then
    echo "加入 https 配置后，nginx 配置检查失败. 中止程序运行."
    exit 1
fi

echo "nginx配置检查成功 2..."

# Step 8: 重启nginx
sudo systemctl restart nginx
echo "再次重启nginx 成功"

echo "配置域名 $domain 完成."
