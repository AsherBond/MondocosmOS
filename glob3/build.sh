#!/bin/bash

script_full_name=$(cd "${0%/*}" && echo $PWD/${0##*/})
script_directory=`dirname "$script_full_name"`


#target_project=$(readlink -f $1)
target_project=$1
shift; # consume the target_project argument

${script_directory}/igotools/run.sh ${target_project}
errorLevel="$?"
if [ ! "${errorLevel}" = "0" ] ; then
    echo ERROR: Running of tools terminated with errorLevel=${errorLevel}
    exit ${errorLevel}
fi


cd ${target_project}
./generated_ant.sh $*
