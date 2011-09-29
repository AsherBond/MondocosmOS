#!/bin/bash

script_full_name=$(cd "${0%/*}" && echo $PWD/${0##*/})
script_directory=`dirname "$script_full_name"`

${script_directory}/../build.sh \
    $script_directory \
    $*
