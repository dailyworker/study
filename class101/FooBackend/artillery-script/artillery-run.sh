#!/bin/sh
# shellcheck disable=SC2039
# shellcheck disable=SC2181
REPORT_OUTPUT='report.json'
RUN_YAML_FILE='config.yaml'

which node &> /dev/null
if [ $? == 0 ]; then
 echo "Node Installed"
 npm ls -g --depth=0 | grep -oP 'artillery' &> /dev/null
  if [ $? == 0 ]; then
    artillery run --output "${REPORT_OUTPUT}" "${RUN_YAML_FILE}"
    artillery report "${REPORT_OUTPUT}"
  else
    npm install -g artillery
    artillery run --output "${REPORT_OUTPUT}" "${RUN_YAML_FILE}"
    artillery report "${REPORT_OUTPUT}"
  fi
 exit 0;
else
 echo "Node not installed"
 exit 1;
fi

