#!/bin/bash

clear

# change directory to where the script is
ABSPATH=$(cd "${0%/*}" && echo $PWD/${0##*/})
PATH_ONLY=`dirname "$ABSPATH"`
cd "${PATH_ONLY}"

export DEVELOPMENT=on
export LOW_END=off
#export ARCHITECTURE="32"

./runjava.sh es.igosoftware.euclid.pointscloud.GPointsCloud $*
