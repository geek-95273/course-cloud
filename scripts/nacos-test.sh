#!/bin/bash
set -euo pipefail

DC_CMD=${DC_CMD:-}
if command -v docker-compose >/dev/null 2>&1; then
  DC_CMD=${DC_CMD:-"docker-compose"}
else
  DC_CMD=${DC_CMD:-"docker compose"}
fi

echo "启动所有服务..."
$DC_CMD up -d --build

echo "等待服务启动..."
sleep 40

echo "检查 Nacos 控制台..."
curl -sf http://localhost:8848/nacos/ | head -n 5 || true

echo "检查服务注册情况..."
curl -sf "http://localhost:8848/nacos/v1/ns/instance/list?serviceName=catalog-service&groupName=COURSEHUB_GROUP&namespaceId=dev" || true
echo

echo "测试服务调用(负载均衡/故障转移)..."
for i in {1..10}; do
  echo "第 $i 次请求:"
  curl -sf http://localhost:8082/api/enrollments/test || true
  echo
done

echo "查看容器状态..."
$DC_CMD ps
