#!/bin/bash
source ~/.bash_profile
if [ -f ${UGS_CONFIG_FILE} ]; then
  source ${UGS_CONFIG_FILE}
fi

CURR_PATH=`pwd`
ugsRemoveCrontab()
{
  username=`whoami`
  crontab -l > /tmp/ugscronfile.${username}.1
  sed /ugsMonitor/d /tmp/ugscronfile.${username}.1 > /tmp/ugscronfile.${username}
  
  crontab -r
  
  crontab /tmp/ugscronfile.${username}
  rm -rf /tmp/ugscronfile.${username}*
}

ugsRemoveCrontab

stop_num=0
ugs_pid=$(ps -eo pid,cmd|grep -w xrk.usd.service|grep -vw grep | sed -n '1p' | awk '{print $1;}')
if [ -z ${ugs_pid} ]; then
  ugs_pid=0
fi

while [ ${ugs_pid} -gt 0 ]; do
  echo "> Find ugs server thread id:${ugs_pid}"
  kill -9 ${ugs_pid}
  sleep 1

  ugs_pid=$(ps -eo pid,cmd|grep -w xrk.usd.service|grep -vw grep | sed -n '1p' | awk '{print $1;}')
  if [ -z ${ugs_pid} ]; then
    ugs_pid=0
  fi
done

echo "> Stop ugs server successfully! "
