#!/bin/bash


# change directory to _build parent directory
script_full_name=$(cd "${0%/*}" && echo $PWD/${0##*/})
script_directory=`dirname "$script_full_name"`
cd ${script_directory}/..


# detect OS
DETECTED_OS=`uname`
echo - Detected ${DETECTED_OS} Operating System

case $DETECTED_OS in
    "Linux" )
        PLATFORM="linux"
        ;;
#    "Darwin" )
#        PLATFORM="apple"
#        ;;
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


JAVA_CMD="@BUILD_TOOLS_JDK_HOME@/${PLATFORM}/${BITS_32_OR_64}/jre/bin/java"
echo - JAVA_CMD=${JAVA_CMD}


if ! test -e project.properties ; then
    echo - ERROR: Can not find project.properties
    exit 2
fi

. ./project.properties


setJAVA_VM_ARGS="JAVA_VM_ARGS=\$JAVA_VM_ARGS_${PLATFORM}_${BITS_32_OR_64}"
eval $setJAVA_VM_ARGS
if [ "$JAVA_VM_ARGS" = "" ] ; then
    echo - WARNING: JAVA_VM_ARGS_${PLATFORM}_${BITS_32_OR_64} property not found!
else
    echo - JAVA_VM_ARGS=${JAVA_VM_ARGS}
fi


java_library_path=""
if [ ! "$JAVA_VM_JAVA_LIBRARY_PATH" = "" ] ; then
    echo - JAVA_VM_JAVA_LIBRARY_PATH=${JAVA_VM_JAVA_LIBRARY_PATH}

    for each in ${JAVA_VM_JAVA_LIBRARY_PATH[@]}
    do
        java_library_path="${java_library_path} -Djava.library.path=${each}/${PLATFORM}/${BITS_32_OR_64}/ "
    done
fi


jna_library_path=""
if [ ! "$JAVA_VM_JNA_LIBRARY_PATH" = "" ] ; then
    echo - JAVA_VM_JNA_LIBRARY_PATH=${JAVA_VM_JNA_LIBRARY_PATH}

    for each in ${JAVA_VM_JNA_LIBRARY_PATH[@]}
    do
        jna_library_path="${jna_library_path} -Djna.library.path=${each}/${PLATFORM}/${BITS_32_OR_64}/ "
    done
fi


export CLASSPATH="@CLASSPATH@"
#echo - CLASSPATH=${CLASSPATH}

echo

#    -enablesystemassertions \
exec \
    ${JAVA_CMD} \
    -showversion \
    ${java_library_path} \
    ${jna_library_path} \
    ${JAVA_VM_ARGS} \
    ${JAVA_VM_ARGS} \
    $*
