#!/bin/csh -f
#
# Use this file ONLY if you need to launch different version of Wok
# or if you want to adress different Wok entities.
# If you use this file , the 2 first arguments are mandatory.
# 
#
set noglob ; set narg = $#argv 
if ( $narg > 3) then
  echo "Usage : wok.csh wok_home wok_entities [tclhome] "
  echo "        wok_home(optional)     is the path of directory for wok shareable (Ex: <root>/lib/sun) "
  echo "        wok_entities(optional) is the path of an ATLIST file."
  echo "        tclhome(optional)      is the home directory of a Tcl distribution."
  echo "   "
  exit
endif

if ( $narg == 0 && ! ($?CASROOT)) then
echo -n "Please define CASROOT to the folder containing OpenCascade '"'src'"', '"'drv'"' and '"'inc'"' folders. :"
set res = $<
setenv CASROOT ${res}
endif

if ( $narg == 0) then
  setenv WOKHOME $CASROOT/../wok
  setenv WOK_ROOTADMDIR $WOKHOME/wok_entities
endif

if ( $narg == 1) then
  setenv WOKHOME $argv[1]
  setenv WOK_ROOTADMDIR $WOKHOME/wok_entities
endif

if ( $narg == 2 ) then
  setenv WOKHOME $argv[1]
  setenv WOK_ROOTADMDIR $argv[2]
endif


if ( $narg == 3 ) then
   setenv WOKHOME $argv[1]
   setenv WOK_ROOTADMDIR $argv[2]
   setenv TCLHOME $argv[3]
endif


set TCLLIB=${TCLHOME}/lib
set TCLBIN=${TCLHOME}/bin
if ( $?TCLLIBPATH ) then
    unsetenv TCLLIBPATH
endif
if ( ! ($?LD_LIBRARY_PATH) ) then
    setenv LD_LIBRARY_PATH ""
endif

switch ( `uname` )
    case SunOS:
	setenv WOKSTATION "sun"
	breaksw
    case Linux:
	setenv WOKSTATION "lin"
	breaksw
    case IRIX:
	setenv WOKSTATION "sil"
	breaksw
    case IRIX64:
	setenv WOKSTATION "sil"
	setenv TCLLIBPATH "${TCLLIB}/itcl ${TCLLIB} ${WOKHOME}/lib ${WOKHOME}/lib/${WOKSTATION}"
	setenv TRAP_FPE "UNDERFL=FLUSH_ZERO;OVERFL=DEFAULT;DIVZERO=DEFAULT;INT_OVERFL=DEFAULT"
	breaksw
    case HP-UX:
        setenv WOKSTATION "hp"
	setenv SHLIB_PATH "${TCLLIB}:${WOKHOME}/lib/${WOKSTATION}:${SHLIB_PATH}:"
        breaksw
    case Darwin:
        setenv WOKSTATION "mac"
	setenv DYLD_LIBRARY_PATH "${TCLLIB}:${WOKHOME}/lib/${WOKSTATION}:${DYLD_LIBRARY_PATH}:"
        breaksw
    case FreeBSD:
        setenv WOKSTATION "bsd"
        breaksw
    default:
	echo "Error : unknown platform"
	breaksw
endsw

setenv LD_LIBRARY_PATH "${TCLLIB}:${WOKHOME}/lib/${WOKSTATION}:${LD_LIBRARY_PATH}:"
${TCLBIN}/tclsh
