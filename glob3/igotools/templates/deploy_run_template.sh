#!/bin/bash


# change directory to where the script is
ABSPATH=$(cd "${0%/*}" && echo $PWD/${0##*/})
PATH_ONLY=`dirname "$ABSPATH"`
cd "${PATH_ONLY}"


# detect OS
DETECTED_OS=`uname`
echo - Detected ${DETECTED_OS} Operating System

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
    echo - Detected 64-Bit Architecture
else
    BITS_32_OR_64="32"
    echo - Detected 32-Bit Architecture
fi


export CLASSPATH="@CLASSPATH@"


JAVA_VM_ARGS_linux_64="@JAVA_VM_ARGS_linux_64@"
JAVA_VM_ARGS_linux_32="@JAVA_VM_ARGS_linux_32@"
JAVA_VM_ARGS_apple_64="@JAVA_VM_ARGS_apple_64@"
JAVA_VM_ARGS_apple_32="@JAVA_VM_ARGS_apple_32@"
setJAVA_VM_ARGS="JAVA_VM_ARGS=\$JAVA_VM_ARGS_${PLATFORM}_${BITS_32_OR_64}"
eval $setJAVA_VM_ARGS
if [ "$JAVA_VM_ARGS" = "" ] ; then
    echo - WARNING: JAVA_VM_ARGS_${PLATFORM}_${BITS_32_OR_64} property not found!
else
    echo - JAVA_VM_ARGS=${JAVA_VM_ARGS}
fi


JAVA_VM_JAVA_LIBRARY_PATH="@JAVA_VM_JAVA_LIBRARY_PATH@"
java_library_path=""
if [ ! "$JAVA_VM_JAVA_LIBRARY_PATH" = "" ] ; then
    #echo - JAVA_VM_JAVA_LIBRARY_PATH=${JAVA_VM_JAVA_LIBRARY_PATH}

    for each in ${JAVA_VM_JAVA_LIBRARY_PATH[@]}
    do
        java_library_path="${java_library_path} -Djava.library.path=libs/native/${each}/${PLATFORM}/${BITS_32_OR_64}/ "
    done

    echo - java_library_path=$java_library_path
fi

JAVA_VM_JNA_LIBRARY_PATH="@JAVA_VM_JNA_LIBRARY_PATH@"
jna_library_path=""
if [ ! "$JAVA_VM_JNA_LIBRARY_PATH" = "" ] ; then
    #echo - JAVA_VM_JNA_LIBRARY_PATH=${JAVA_VM_JNA_LIBRARY_PATH}

    for each in ${JAVA_VM_JNA_LIBRARY_PATH[@]}
    do
        jna_library_path="${jna_library_path} -Djna.library.path=libs/native/${each}/${PLATFORM}/${BITS_32_OR_64}/ "
    done

    echo - jna_library_path=$jna_library_path
fi


echo


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
    java_cmd="jre/${PLATFORM}/${BITS_32_OR_64}/bin/java"
fi


exec \
    ${java_cmd} \
    -splash:bitmaps/splash.png \
    -showversion \
    ${java_library_path} \
    ${jna_library_path} \
    ${JAVA_VM_ARGS} \
    @MAIN_CLASS@ \
    $*
