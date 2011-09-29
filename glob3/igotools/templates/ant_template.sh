#!/bin/bash


# detect OS
DETECTED_OS=`uname`
#echo - Detected ${DETECTED_OS} Operating System

case $DETECTED_OS in
    "Linux" )
        PLATFORM="linux"
        ;;
    "Darwin" )
        PLATFORM="apple"
        ;;
     * )
        echo - ERROR: Currently only Linux is supported
        exit 1
        ;;
esac


# detect 32 or 64 bits
BITS_32_OR_64=`uname -m`
if [ "$BITS_32_OR_64" = "x86_64" ] || [ "$BITS_32_OR_64" = "ia64" ]; then
    BITS_32_OR_64="64"
    #echo - Detected 64-Bit Architecture
else
    BITS_32_OR_64="32"
    #echo - Detected 32-Bit Architecture
fi


if [ "$PLATFORM" = "apple" ]; then
    java_in_path_version=`java -version 2>&1 | grep "java version" | awk '{print $3}' | tr -d \"`
    if [ ! "${java_in_path_version}" = "1.6.0_26" ] ; then
        echo
        echo --------------------------------------------------------------------------------------
        echo - APPLE WARNING: Version of java \(in path\) is not 1.6.0_26, it\'s ${java_in_path_version}
            echo --------------------------------------------------------------------------------------
            echo
    else
        echo - APPLE: Version of java  \(in path\) is 1.6.0_26
    fi

    java_cmd="java"
else
    tools_jdk_home="%tools_jdk_home%"

    java_home="${tools_jdk_home}/${PLATFORM}/${BITS_32_OR_64}/"
    if ! test -d ${java_home} ; then
        echo - ERROR: Can not find java for platform $PLATFORM/$BITS_32_OR_64 at ${tools_jdk_home}
        exit 3
    fi
    java_cmd="${java_home}bin/java"
fi


export CLASSPATH="%CLASSPATH%"
ant_home="%ant_home%"

#    -logger org.apache.tools.ant.listener.BigProjectLogger \
#    -emacs \

${java_cmd} \
    -Dant.home=${ant_home} \
    org.apache.tools.ant.launch.Launcher \
    -logger org.apache.tools.ant.NoBannerLogger \
    -buildfile generated_build.xml \
    $*
errorLevel="$?"
if [ ! "${errorLevel}" = "0" ] ; then
    echo ERROR: Ant execution terminated with errorLevel=${errorLevel}
    exit ${errorLevel}
fi
