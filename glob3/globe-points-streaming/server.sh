#!/bin/bash

clear

# change directory to where the script is
ABSPATH=$(cd "${0%/*}" && echo $PWD/${0##*/})
PATH_ONLY=`dirname "$ABSPATH"`
cd "${PATH_ONLY}"

export DEVELOPMENT=on
export LOW_END=off
export ARCHITECTURE="32"
#export EXTRA_JAVAVM_ARG="-server -XX:+DoEscapeAnalysis"
#export JAVAVM="/home/dgd/Desktop/jdk1.6.0_19/bin/java"

export EXTRA_CLASSPATH="../euclid:../euclid/libs/jna.jar"

../dmvc/runjava.sh es.igosoftware.experimental.pointscloud.loading.GPointsStreamingServer $*
