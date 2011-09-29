#!/bin/bash

script_full_name=$(cd "${0%/*}" && echo $PWD/${0##*/})
script_directory=`dirname "$script_full_name"`

TARGET_PROJECT=$1
shift # consume TARGET_PROJECT argument

if [ "$TARGET_PROJECT" = "" ] ; then
    echo - ERROR: Must specify a project!
    exit /B 1
fi


cd ${script_directory}/${TARGET_PROJECT}
./_build/runjava.sh $*
