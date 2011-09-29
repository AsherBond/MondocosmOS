#!/bin/bash

script_full_name=$(cd "${0%/*}" && echo $PWD/${0##*/})
script_directory=`dirname "$script_full_name"`

TARGET_PROJECT=$(readlink -f $1)
shift; # consume the TARGET_PROJECT argument


if [ "$TARGET_PROJECT" = "" ] ; then
    echo - ERROR: Must specify a project!
    exit 1
fi

if ! test -d ${TARGET_PROJECT} ; then
    echo - ERROR: Can not find a directory for project ${TARGET_PROJECT}
    exit 2
fi

if ! test -e ${TARGET_PROJECT}/project.properties ; then
    echo - ERROR: Can not find ${TARGET_PROJECT}/project.properties
    exit 3
fi


. ${TARGET_PROJECT}/project.properties

if [ "$MAIN_CLASS" = "" ] ; then
    echo - ERROR: Must specify the MAIN_CLASS property in  ${TARGET_PROJECT}/project.properties
    exit 4
fi

if test -e ${TARGET_PROJECT}/bitmaps/splash.png ; then
    SPLASH="-splash:bitmaps/splash.png"
else
    SPLASH=""
    echo - WARNING: ${TARGET_PROJECT}/bitmaps/splash.png file not found!
fi


echo - ${TARGET_PROJECT}: Running ${MAIN_CLASS} $*

${script_directory}/runjava.sh \
    ${TARGET_PROJECT} \
    ${SPLASH} \
    ${MAIN_CLASS} \
    $*
