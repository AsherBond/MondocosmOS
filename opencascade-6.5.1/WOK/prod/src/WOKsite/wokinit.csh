#!/bin/csh -f
#

set OS_NAME=`uname`
set OS_PLATFORM=""
if ( $OS_NAME == "SunOS" ) then
   set OS_PLATFORM="sun"
else if ( $OS_NAME == "Linux" ) then
   set OS_PLATFORM="lin"
else if ( $OS_NAME == "HP-UX" ) then
   set OS_PLATFORM="hp"
else if ( $OS_NAME == "Darwin" ) then
   set OS_PLATFORM="mac"
else if ( $OS_NAME == "FreeBSD" ) then
   set OS_PLATFORM="bsd"
endif

setenv WOKHOME ${CASROOT}/../wok
setenv HOME ${WOKHOME}/site
setenv WOK_LIBPATH ${WOKHOME}/lib/${OS_PLATFORM}

setenv WOK_ROOTADMDIR ${WOKHOME}/wok_entities
setenv WOK_SESSIONID ${HOME}

setenv TCLHOME ${CASROOT}/../3rdparty/${OS_NAME}/tcltk
setenv TCLLIBPATH "${TCLHOME}/lib:${WOK_LIBPATH}"

set TCLLIB=${TCLHOME}/lib
set TCLBIN=${TCLHOME}/bin

setenv LD_LIBRARY_PATH "/usr/lib:/usr/X11R6/lib:/lib:${TCLLIB}:${WOKHOME}/lib/:${WOKHOME}/lib/${OS_PLATFORM}"
setenv path "/usr/bin /bin /usr/bin /sbin /usr/sbin /usr/local/bin /usr/local/sbin /usr/X11R6/bin /etc"
setenv PATH "/usr/bin:/bin:/usr/bin:/sbin:/usr/sbin:/usr/local/bin:/usr/local/sbin:/usr/X11R6/bin:/etc"

echo ${LD_LIBRARY_PATH}
env | grep -i wok
env | grep -i tcl

cd ${WOK_ROOTADMDIR}

${TCLBIN}/tclsh < ${WOKHOME}/site/CreateFactory.tcl

