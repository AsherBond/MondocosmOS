#!/bin/bash

script_full_path=$(cd "${0%/*}" && echo $PWD/${0##*/})
script_directory=`dirname "$script_full_path"`

ant_home="./apache-ant-1.8.2"


function compile_igo_tools() {
    pushd ${script_directory} > /dev/null


    export CLASSPATH=""
    local jars=`find ${ant_home}/lib/*.jar`
    for jar in ${jars[@]}
    do
        export CLASSPATH="${CLASSPATH}${jar}:"
    done

    # needed for javac
    #export CLASSPATH="${CLASSPATH}${java_home}lib/tools.jar:"

    # needed for eclipse java compiler (jdt)
    export CLASSPATH="${CLASSPATH}eclipse_helios/plugins/org.eclipse.jdt.core_3.6.0.v_A58.jar:"
    export CLASSPATH="${CLASSPATH}eclipse_helios/jdtCompilerAdapter.jar"

    #echo $CLASSPATH


#        -emacs \
    ${java_cmd} \
        -Dant.home=${ant_home} \
        org.apache.tools.ant.launch.Launcher \
        -logger org.apache.tools.ant.NoBannerLogger \
        dist
    errorLevel="$?"


    popd > /dev/null
    return ${errorLevel}
}


function run_TBuild() {
    export CLASSPATH="${script_directory}/_build/igotools.jar"

    local deploy_property=""
    if [ "${DEPLOY}" = "yes" ] ; then
        deploy_property=" -Ddeploy=yes "
    fi

    local tools_jdk_home_property=""
    if [ ! "${TOOLS_JDK_HOME}" = "" ] ; then
        tools_jdk_home_property=" -Dtools.jdk.home=${TOOLS_JDK_HOME} "
    fi

    ${java_cmd} \
        -Xmx1G \
        ${deploy_property} \
        ${tools_jdk_home_property} \
        -Dtemplates.directory=${script_directory}/templates \
        -Dtools.eclipse.jdt.jar=${script_directory}/eclipse_helios/plugins/org.eclipse.jdt.core_3.6.0.v_A58.jar \
        -Dtools.jdt.adapter.jar=${script_directory}/eclipse_helios/jdtCompilerAdapter.jar \
        -Dant.home=${script_directory}/${ant_home} \
        -Digotools.home=${script_directory} \
        es.igosoftware.tools.TBuild \
        $*

}



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
    if [ "${TOOLS_JDK_HOME}" = "" ] ; then
        tools_jdk_home="${script_directory}/../../../IGO-GIT-Repository/tools_jdk_1.6.26/"
        tools_jdk_home=$(readlink -f $tools_jdk_home)
        if ! test -d ${tools_jdk_home} ; then
            echo - ERROR: Can not find a TOOLS_JDK_HOME
            exit 2
        fi

        echo - Auto set tools_jdk_home to "${tools_jdk_home}"
    else
        echo - Setting tools_jdk_home from given environment variable to "${TOOLS_JDK_HOME}"
        tools_jdk_home="${TOOLS_JDK_HOME}"
    fi

    java_home="${tools_jdk_home}/${PLATFORM}/${BITS_32_OR_64}/"
    if ! test -d ${java_home} ; then
        echo - ERROR: Can not find java for platform $PLATFORM/$BITS_32_OR_64 at ${tools_jdk_home}
        exit 3
    fi

    java_cmd="${java_home}bin/java"
fi

echo

# compile tools if needed
compile_igo_tools
errorLevel="$?"
echo
if [ ! "${errorLevel}" = "0" ] ; then
    echo ERROR: Compilation of tools terminated with errorLevel=${errorLevel}
    exit ${errorLevel}
fi


# run TBuild after a succefully compilation of tools
run_TBuild $*
errorLevel="$?"
echo
if [ ! "${errorLevel}" = "0" ] ; then
    echo ERROR: TBuild terminated with errorLevel=${errorLevel}
    exit ${errorLevel}
fi
