#!/bin/bash
source ~/.bash_profile
if [ -f ${UGS_CONFIG_FILE} ]; then
  source ${UGS_CONFIG_FILE}
fi

CURR_PATH=`pwd`

function ugsUpdateCrontab()
{
  #delete old config
  username=`whoami`
  crontab -l > /tmp/ugscronfile.${username}.1
  sed /ugsMonitor/d /tmp/ugscronfile.${username}.1 > /tmp/ugscronfile.${username}
  crontab -r
  
  echo "* * * * * bash ${UGS_INSTALL_PATH}/ugsMonitor.sh >/dev/null 2>&1" >> /tmp/ugscronfile.${username}
  
  crontab /tmp/ugscronfile.${username}
  rm -rf /tmp/ugscronfile.${username}*
}

cd ${UGS_INSTALL_PATH}
PARAMER=-Dfile.encoding=utf-8
JAVA_MEM='-Xmx2048m -Xms1024m -Xmn712m'
JAVA_HOME=jre
JAVA_FILE=xrk.usd.service-1.0-SNAPSHOT.jar

if [ ! -x ${JAVA_HOME}/bin/java ]; then
    chmod +x ${JAVA_HOME}/bin/*
fi

let ugs_start_num=0
let MAX_TRY_COUNT=3
ugs_pid=`ps -eo pid,cmd | grep xrk.usd.service | grep java | grep -v grep | sed -n '1p' | awk '{print $1}'`

if [ ! -z ${ugs_pid} ]; then
  echo "> UGS server process already started, please stop it first!"
  exit 0
fi

while [ -z ${ugs_pid} ]; do
  ${JAVA_HOME}/bin/java ${PARAMER} ${JAVA_MEM} -jar ${JAVA_FILE} >/dev/null 2>&1 &
  sleep 1
  
  ugs_pid=`ps -eo pid,cmd | grep xrk.usd.service | grep java | grep -v grep | sed -n '1p' | awk '{print $1}'`
  let ugs_start_num=$((${ugs_start_num} + 1))
  
  if [ ! -z ${ugs_pid} ]; then
    echo "> Start ugs server process successfully!"
  else
    echo "> Start ugs server process failure, try ${ugs_start_num} already !"
    if [ ${ugs_start_num} -gt ${MAX_TRY_COUNT} ]; then
      echo "> Exceed the ${MAX_TRY_COUNT} times, start failure!"
      break;
    fi
  fi

done

ugsUpdateCrontab

cd ${CURR_PATH}
