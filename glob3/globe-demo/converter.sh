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


JAVA_CMD="/home/dgd/Escritorio/IGO-GIT-Repository/tools_jdk_1.6.26/${PLATFORM}/${BITS_32_OR_64}/jre/bin/java"
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


export CLASSPATH="/home/dgd/Escritorio/GLOB3-Repository/glob3/globe-demo/_build/globe-demo.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/igolibs/_build/igolibs.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/igolibs/libs/bsh/bsh-2.0b4.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/igolibs/libs/jCharts-0.7.5/jCharts-0.7.5.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/igolibs/libs/junit/junit-4.9b3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/igolibs/libs/miglayout/miglayout-3.7.3.1-swing.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/dmvc/_build/dmvc.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/dmvc/libs/netty-3.1.5.GA/netty-3.1.5.GA.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/euclid/_build/euclid.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/euclid/libs/jna/jna.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/_build/globe.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/jts-1.9.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/TableLayout-bin-jdk1.5-2007-04-21.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/geonames-1.0.2.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/jdom-1.0.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/swingx-1.6.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/commons-beanutils-1.7.0.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/commons-logging-1.1.1.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/geoapi-2.3-M1.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/geoapi-pending-2.3-M1.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/jsr-275-1.0-beta-2.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/vecmath-1.3.2.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/xercesImpl-2.7.1.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/substance6.0/substance.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/substance6.0/trident.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/jai_codec.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/jai_core.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/jogl/gluegen-rt.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/jogl/jogl.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/worldwind-1.2.0/worldwind.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/worldwind-1.2.0/worldwindx.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/netcdf/netcdf-4.2.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/netcdf/slf4j-api-1.5.6.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/netcdf/slf4j-jdk14-1.5.6.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-api-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-epsg-hsql-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-main-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-metadata-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-referencing-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-shapefile-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/postgresql-9.0-801.jdbc4.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/commons-dbcp-1.4.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/commons-pool-1.5.5.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/hsqldb-1.8.0.1.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-jdbc-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-jdbc-postgis-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/jts-1.11.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gson-1.6/gson-1.6.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-swing-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-oracle-spatial-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-jdbc-oracle-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-xsd-core-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-xsd-kml-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/picocontainer-1.2.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/xsd-2.2.2.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/ecore-2.2.2.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/common-2.2.1.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/commons-collections-3.1.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-xsd-gml2-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe/libs/gt/gt-xsd-gml3-2.7-M3.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/globe-points-streaming/_build/globe-points-streaming.jar:/home/dgd/Escritorio/GLOB3-Repository/glob3/dmvc/libs/netty-3.1.5.GA/netty-3.1.5.GA.jar:"
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
