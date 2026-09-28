#!/usr/bin/env bash
set -euo pipefail

NACOS_SERVER_ADDR="${NACOS_SERVER_ADDR:-127.0.0.1:8848}"
DATA_ID="${DATA_ID:-nacos-config-demo.yaml}"
GROUP="${GROUP:-DEFAULT_GROUP}"
CONTENT="${CONTENT:-$'demo:\n  message: hello from nacos\n  version: v1\n'}"

echo "Publishing ${DATA_ID} to http://${NACOS_SERVER_ADDR} (group=${GROUP})"

curl -fsS -X POST "http://${NACOS_SERVER_ADDR}/nacos/v1/cs/configs" \
  --data-urlencode "dataId=${DATA_ID}" \
  --data-urlencode "group=${GROUP}" \
  --data-urlencode "type=yaml" \
  --data-urlencode "content=${CONTENT}"

echo
echo "Published. Verify with:"
echo "  curl \"http://${NACOS_SERVER_ADDR}/nacos/v1/cs/configs?dataId=${DATA_ID}&group=${GROUP}\""
