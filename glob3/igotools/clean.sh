#!/bin/bash


#--------------------------------------------------------------------------------
tools_jdk_home="/home/dgd/Desktop/IGO-GIT-Repository/tools_jdk_1.6.26/"
ant_home="./apache-ant-1.8.2"
#--------------------------------------------------------------------------------



function cleanTools() {
    local jars=`find ${ant_home}/lib/*.jar`
    export CLASSPATH=""
    for jar in ${jars[@]}
    do
        export CLASSPATH="${CLASSPATH}${jar}:"
    done
    export CLASSPATH="${CLASSPATH}${java_home}lib/tools.jar:"

    ${java_home}bin/java \
        -Dant.home=${ant_home} \
        org.apache.tools.ant.launch.Launcher \
        clean
}



# change directory to where the script is
ABSPATH=$(cd "${0%/*}" && echo $PWD/${0##*/})
PATH_ONLY=`dirname "$ABSPATH"`
cd "${PATH_ONLY}"


# detect OS
DETECTED_OS=`uname`
echo - Detected ${DETECTED_OS} Operating System

if [ "$DETECTED_OS" = "Linux" ]; then
    PLATFORM="linux"
else
    echo - ERROR: Currently only Linux is supported
    exit 1
fi


# detect 32 or 64 bits
BITS_32_OR_64=`uname -m`
if [ "$BITS_32_OR_64" = "x86_64" ] || [ "$BITS_32_OR_64" = "ia64" ]; then
    BITS_32_OR_64="64"
    echo - Detected 64-Bit Architecture
else
    BITS_32_OR_64="32"
    echo - Detected 32-Bit Architecture
fi


java_home="${tools_jdk_home}/${PLATFORM}/${BITS_32_OR_64}/"


# compile tools if needed
cleanTools
errorLevel="$?"
echo
if [ ! "${errorLevel}" = "0" ] ; then
    echo ERROR: Cleaning of tools terminated with errorLevel=${errorLevel}
    exit ${errorLevel}
fi
