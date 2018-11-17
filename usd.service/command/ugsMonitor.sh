#!/bin/bash
source ~/.bash_profile

if [ -f ${UGS_CONFIG_FILE} ]; then
  source ${UGS_CONFIG_FILE}
fi

PROCNAME_LIST="xrk.usd.service"
for PROCNAME in $PROCNAME_LIST;
do
let PROCNUM=$(ps -eo uid,pid,cmd|grep -w ${PROCNAME}|grep -vw grep |wc -l)
if [ "$PROCNUM" -le "0" ]; then
  case  ${PROCNAME} in
    "xrk.usd.service") start_ugs.sh;;
  esac
else
  echo "> $PROCNAME already start, do nothing ... "
fi
done



