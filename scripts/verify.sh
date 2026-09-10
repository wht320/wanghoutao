#!/usr/bin/env bash
set -euo pipefail

echo "== config demo =="
curl -fsS "http://127.0.0.1:8081/config"
echo

echo "== provider =="
curl -fsS "http://127.0.0.1:8082/hello?name=nacos"
echo

echo "== consumer via discovery =="
curl -fsS "http://127.0.0.1:8083/hello?name=nacos"
echo
