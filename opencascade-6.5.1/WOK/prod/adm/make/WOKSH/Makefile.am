# Makefile.am for executable WOKSH
srcdir = @srcdir@

VPATH = @srcdir@ : \
@top_srcdir@/src/WOKSH

INCLUDES = -I$(CASROOT)/inc  $(STLPort_INCLUDES) $(CSF_TclLibs_INCLUDES) $(CSF_TclTkLibs_INCLUDES) $(CSF_SOCKETLibs_INCLUDES) $(CSF_FPELibs_INCLUDES) $(CSF_ThreadLibs_INCLUDES) $(CSF_advapi32_INCLUDES) $(CSF_gdi32_INCLUDES) $(CSF_user32_INCLUDES) $(CSF_kernel32_INCLUDES) $(CSF_TBB_INCLUDES) $(CSF_MotifLibs_INCLUDES)  \
-I@top_srcdir@/inc \
-I@top_srcdir@/src/WOKSH

 
bin_PROGRAMS= woksh wokprocess
 woksh_SOURCES = @top_srcdir@/src/WOKSH/woksh.cxx 
 wokprocess_SOURCES = @top_srcdir@/src/WOKSH/wokprocess.cxx 
 woksh_LDADD = -L$(CASROOT)/$(platform)/lib  ../TKWOKTcl/libTKWOKTcl.la \
../TKWOK/libTKWOK.la \
-lTKAdvTools \
-lTKernel \
 $(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) $(CSF_SOCKETLibs_LIB) $(CSF_FPELibs_LIB) $(CSF_ThreadLibs_LIB) $(CSF_advapi32_LIB) $(CSF_gdi32_LIB) $(CSF_user32_LIB) $(CSF_kernel32_LIB) $(CSF_TBB_LIB) $(CSF_MotifLibs_LIB)  
 wokprocess_LDADD = -L$(CASROOT)/$(platform)/lib  ../TKWOKTcl/libTKWOKTcl.la \
../TKWOK/libTKWOK.la \
-lTKAdvTools \
-lTKernel \
 $(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) $(CSF_SOCKETLibs_LIB) $(CSF_FPELibs_LIB) $(CSF_ThreadLibs_LIB) $(CSF_advapi32_LIB) $(CSF_gdi32_LIB) $(CSF_user32_LIB) $(CSF_kernel32_LIB) $(CSF_TBB_LIB) $(CSF_MotifLibs_LIB)  
