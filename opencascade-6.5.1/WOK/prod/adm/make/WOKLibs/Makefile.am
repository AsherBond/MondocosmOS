# Makefile.am for executable WOKLibs
srcdir = @srcdir@

VPATH = @srcdir@ : \
@top_srcdir@/src/WOKLibs

INCLUDES = -I$(CASROOT)/inc  $(STLPort_INCLUDES) $(CSF_TclLibs_INCLUDES) $(CSF_TclTkLibs_INCLUDES) $(CSF_SOCKETLibs_INCLUDES) $(CSF_FPELibs_INCLUDES) $(CSF_ThreadLibs_INCLUDES) $(CSF_advapi32_INCLUDES) $(CSF_gdi32_INCLUDES) $(CSF_user32_INCLUDES) $(CSF_kernel32_INCLUDES) $(CSF_TBB_INCLUDES) $(CSF_MotifLibs_INCLUDES)  \
-I@top_srcdir@/inc \
-I@top_srcdir@/src/WOKLibs

 
lib_LTLIBRARIES= libwokcmd.la libwoktoolscmd.la libwokutilscmd.la libwoksteps.la libwokobjssteps.la libwokdfltsteps.la libwokdeliverysteps.la libwokorbixsteps.la libmscmd.la
 libwokcmd_la_SOURCES = @top_srcdir@/src/WOKLibs/wokcmd.cxx 
 libwoktoolscmd_la_SOURCES = @top_srcdir@/src/WOKLibs/woktoolscmd.cxx 
 libwokutilscmd_la_SOURCES = @top_srcdir@/src/WOKLibs/wokutilscmd.cxx 
 libwoksteps_la_SOURCES = @top_srcdir@/src/WOKLibs/woksteps.cxx 
 libwokobjssteps_la_SOURCES = @top_srcdir@/src/WOKLibs/wokobjssteps.cxx 
 libwokdfltsteps_la_SOURCES = @top_srcdir@/src/WOKLibs/wokdfltsteps.cxx 
 libwokdeliverysteps_la_SOURCES = @top_srcdir@/src/WOKLibs/wokdeliverysteps.cxx 
 libwokorbixsteps_la_SOURCES = @top_srcdir@/src/WOKLibs/wokorbixsteps.cxx 
 libmscmd_la_SOURCES = @top_srcdir@/src/WOKLibs/mscmd.cxx 
 libwokcmd_la_LIBADD = -L$(CASROOT)/$(platform)/lib  ../TKWOKTcl/libTKWOKTcl.la \
../TKWOK/libTKWOK.la \
-lTKAdvTools \
-lTKernel \
 $(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) $(CSF_SOCKETLibs_LIB) $(CSF_FPELibs_LIB) $(CSF_ThreadLibs_LIB) $(CSF_advapi32_LIB) $(CSF_gdi32_LIB) $(CSF_user32_LIB) $(CSF_kernel32_LIB) $(CSF_TBB_LIB) $(CSF_MotifLibs_LIB)  
 libwoktoolscmd_la_LIBADD = -L$(CASROOT)/$(platform)/lib  ../TKWOKTcl/libTKWOKTcl.la \
../TKWOK/libTKWOK.la \
-lTKAdvTools \
-lTKernel \
 $(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) $(CSF_SOCKETLibs_LIB) $(CSF_FPELibs_LIB) $(CSF_ThreadLibs_LIB) $(CSF_advapi32_LIB) $(CSF_gdi32_LIB) $(CSF_user32_LIB) $(CSF_kernel32_LIB) $(CSF_TBB_LIB) $(CSF_MotifLibs_LIB)  
 libwokutilscmd_la_LIBADD = -L$(CASROOT)/$(platform)/lib  ../TKWOKTcl/libTKWOKTcl.la \
../TKWOK/libTKWOK.la \
-lTKAdvTools \
-lTKernel \
 $(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) $(CSF_SOCKETLibs_LIB) $(CSF_FPELibs_LIB) $(CSF_ThreadLibs_LIB) $(CSF_advapi32_LIB) $(CSF_gdi32_LIB) $(CSF_user32_LIB) $(CSF_kernel32_LIB) $(CSF_TBB_LIB) $(CSF_MotifLibs_LIB)  
 libwoksteps_la_LIBADD = -L$(CASROOT)/$(platform)/lib  ../TKWOKTcl/libTKWOKTcl.la \
../TKWOK/libTKWOK.la \
-lTKAdvTools \
-lTKernel \
 $(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) $(CSF_SOCKETLibs_LIB) $(CSF_FPELibs_LIB) $(CSF_ThreadLibs_LIB) $(CSF_advapi32_LIB) $(CSF_gdi32_LIB) $(CSF_user32_LIB) $(CSF_kernel32_LIB) $(CSF_TBB_LIB) $(CSF_MotifLibs_LIB)  
 libwokobjssteps_la_LIBADD = -L$(CASROOT)/$(platform)/lib  ../TKWOKTcl/libTKWOKTcl.la \
../TKWOK/libTKWOK.la \
-lTKAdvTools \
-lTKernel \
 $(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) $(CSF_SOCKETLibs_LIB) $(CSF_FPELibs_LIB) $(CSF_ThreadLibs_LIB) $(CSF_advapi32_LIB) $(CSF_gdi32_LIB) $(CSF_user32_LIB) $(CSF_kernel32_LIB) $(CSF_TBB_LIB) $(CSF_MotifLibs_LIB)  
 libwokdfltsteps_la_LIBADD = -L$(CASROOT)/$(platform)/lib  ../TKWOKTcl/libTKWOKTcl.la \
../TKWOK/libTKWOK.la \
-lTKAdvTools \
-lTKernel \
 $(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) $(CSF_SOCKETLibs_LIB) $(CSF_FPELibs_LIB) $(CSF_ThreadLibs_LIB) $(CSF_advapi32_LIB) $(CSF_gdi32_LIB) $(CSF_user32_LIB) $(CSF_kernel32_LIB) $(CSF_TBB_LIB) $(CSF_MotifLibs_LIB)  
 libwokdeliverysteps_la_LIBADD = -L$(CASROOT)/$(platform)/lib  ../TKWOKTcl/libTKWOKTcl.la \
../TKWOK/libTKWOK.la \
-lTKAdvTools \
-lTKernel \
 $(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) $(CSF_SOCKETLibs_LIB) $(CSF_FPELibs_LIB) $(CSF_ThreadLibs_LIB) $(CSF_advapi32_LIB) $(CSF_gdi32_LIB) $(CSF_user32_LIB) $(CSF_kernel32_LIB) $(CSF_TBB_LIB) $(CSF_MotifLibs_LIB)  
 libwokorbixsteps_la_LIBADD = -L$(CASROOT)/$(platform)/lib  ../TKWOKTcl/libTKWOKTcl.la \
../TKWOK/libTKWOK.la \
-lTKAdvTools \
-lTKernel \
 $(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) $(CSF_SOCKETLibs_LIB) $(CSF_FPELibs_LIB) $(CSF_ThreadLibs_LIB) $(CSF_advapi32_LIB) $(CSF_gdi32_LIB) $(CSF_user32_LIB) $(CSF_kernel32_LIB) $(CSF_TBB_LIB) $(CSF_MotifLibs_LIB)  
 libmscmd_la_LIBADD = -L$(CASROOT)/$(platform)/lib  ../TKWOKTcl/libTKWOKTcl.la \
../TKWOK/libTKWOK.la \
-lTKAdvTools \
-lTKernel \
 $(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) $(CSF_SOCKETLibs_LIB) $(CSF_FPELibs_LIB) $(CSF_ThreadLibs_LIB) $(CSF_advapi32_LIB) $(CSF_gdi32_LIB) $(CSF_user32_LIB) $(CSF_kernel32_LIB) $(CSF_TBB_LIB) $(CSF_MotifLibs_LIB)  
