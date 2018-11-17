#!/bin/bash

usage() {
 echo "Usage: build.sh [pkg] [ugs]"
 exit 1
}

PROJDIR=`pwd`
INSTALL_DIR="install"
VERSION="V1.0"
BUILD_SUCC=0

function build_ugs(){
 echo "===============================================================================";
 cd $PROJDIR 
 
 echo "> Get last version"
 git pull origin develop
 echo "> Get last version finished."

 if [ ! -x third-party/apache-maven-3.3.3/bin/mvn ]; then
   chmod +x third-party/apache-maven-3.3.3/bin/*
 fi 

 echo "> Build UGS service ...";
 third-party/apache-maven-3.3.3/bin/mvn clean install package -DskipTests | tee build.out
 echo "> Build ugs package finished."
 echo "===============================================================================";

 BUILD_SUCC=`grep "BUILD SUCCESS" build.out | wc -l`
}

function buildpkg(){
  if [ ${BUILD_SUCC} -ne 1 ]; then
     echo "> compiler failed, please check error and rebuild!"
     return
  fi

  echo "> Build install package ...";
  cd $PROJDIR
  
  if [ -d ${INSTALL_DIR} ]; then
    rm -rf ${INSTALL_DIR}
  fi 
  #create install folder
  mkdir -p ${INSTALL_DIR}
  mkdir -p ${INSTALL_DIR}/bin
  mkdir -p ${INSTALL_DIR}/jre
  mkdir -p ${INSTALL_DIR}/conf
  mkdir -p ${INSTALL_DIR}/lib
  mkdir -p ${INSTALL_DIR}/pg
  
  #generate version file
  generateVersionFile  
  
  #ugs op bin scripts  
  if [ -e build_tool ]; then
    cp -rf build_tool/* ${INSTALL_DIR}/bin
  fi
  
  #copy jre runtime
  cp -rf third-party/jre1.8.0_45/* ${INSTALL_DIR}/jre
  #copy postgresql tools
  cp -rf third-party/pgsql9.3.5/* ${INSTALL_DIR}/pg
  
  #copy ugs build file
  cp -rf usd.service/target/conf/* ${INSTALL_DIR}/conf
  cp -rf usd.service/target/lib/* ${INSTALL_DIR}/lib
  cp -rf usd.service/target/conf/* ${INSTALL_DIR}/conf
  cp -rf usd.service/target/*.jar ${INSTALL_DIR}
  cp -rf usd.service/target/*.sh ${INSTALL_DIR}  
  cp document/db/*.sql ${INSTALL_DIR}
 
  #remove .git directory
  cd $PROJDIR
  find ${INSTALL_DIR}/ |grep .git | xargs rm -rf

  chmod +x ${INSTALL_DIR}/start_ugs.sh
  chmod +x ${INSTALL_DIR}/stop_ugs.sh
  chmod +x ${INSTALL_DIR}/ugsMonitor.sh
  
  echo "> Build install package completed.";
}

function generateVersionFile(){
  cd ${INSTALL_DIR}

  if [ -f UGSVersion.txt ]; then
     rm -f UGSVersion.txt
  fi

  echo 'UGS service version info:' >> UGSVersion.txt
  echo -n ' branches= ' >> UGSVersion.txt
  git branch | grep \* | cut -b 3- >> UGSVersion.txt
  echo -n ' revision =' >> UGSVersion.txt
  git show-ref | grep heads/develop | cut -d ' ' -f1 >> UGSVersion.txt
  echo -n ' date = ' >> UGSVersion.txt
  date +"%Y%m%d" >> UGSVersion.txt
  
  cd ..
}

function mkInstallPkg(){
  if [ ${BUILD_SUCC} -ne 1 ]; then
     echo "> compiler failed, please check error and rebuild!"
     return
  fi

  cd $PROJDIR
  packageName=$1
  selfPackageName=$2 
  
  cat build_tool/inst_script.sh > ${selfPackageName}
  echo "__VER_INFO_START__" >> ${selfPackageName}
  cat ${INSTALL_DIR}/UGSVersion.txt >> ${selfPackageName}
  echo "__ARCHIVE_BELOW__" >> ${selfPackageName}
  cat  $packageName  >> ${selfPackageName}
  
  chmod +x ${selfPackageName}
  if [ ! -d packages ]; then
	  mkdir packages
  fi
  
  rm -rf ${INSTALL_DIR}/UGSVersion.txt
  mv ${selfPackageName} packages/${selfPackageName}
}

function buildinstall(){

  if [ ${BUILD_SUCC} -ne 1 ]; then
     echo "> compiler failed, please check error and rebuild!"
     return
  fi
  
  cd $PROJDIR
 
  if [ -e ${INSTALL_DIR}/jre ]; then
  
  echo "> Make the self-extract install package for UGS Server ...";
  dataTime=`date +"%Y%m%d"`
  packageName="ugsInstall_${VERSION}_${dataTime}.tar.bz"
  selfextpkg="ugsInstall_${VERSION}_${dataTime}.sh"
  
  if [ -e ${packageName} ]; then
    rm -f ${packageName}
  fi
  
  if [ -e ${selfextpkg} ]; then
    rm -f ${selfextpkg}
  fi
  
  tar -jcvf ${packageName} ${INSTALL_DIR}
  	
  if [ -e ${packageName} ]; then
  
    echo "> The self-extract install package made completed !";
    mkInstallPkg ${packageName} $selfextpkg
    rm -f ${packageName}
  
  fi  
 else
  echo "> Error: The package number is not enough, please check."
 fi
 
 #rm -rf ${INSTALL_DIR}
}

if [ "${1}" = "pkg" ]; then 
  build_ugs   
  buildpkg
elif [ "${1}" = "ugs" ]; then
  build_ugs
else
  build_ugs
  buildpkg
  buildinstall
fi

if [ -e build.out ]; then
  rm -f build.out
fi

