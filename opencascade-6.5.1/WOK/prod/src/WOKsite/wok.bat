set TCLHOME=%CASROOT%/../3rdparty/win32/tcltk
set TCLBIN=%TCLHOME%/bin
set WOKHOME=%CASROOT%/../wok
set PATH=%TCLBIN%;%WOKHOME%/lib/wnt;%PATH%
set TCL_RCFILE=%HOME%/tclshrc.tcl
set WOK_ROOTADMDIR=%WOKHOME%/wok_entities
set INIT=%WOKHOME%/lib/wnt
set HOME=%WOKHOME%/site
cd /d %WOK_ROOTADMDIR%
%TCLBIN%/tclsh.exe
