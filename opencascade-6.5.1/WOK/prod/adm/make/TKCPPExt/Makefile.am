# Makefile.am for Open Cascade toolkit TKCPPExt
srcdir = @srcdir@

VPATH = @srcdir@ : \
@top_srcdir@/drv/CPPExt : @top_srcdir@/src/CPPExt


AM_CXXFLAGS = 


AM_CFLAGS = 


INCLUDES = -I$(CASROOT)/inc  $(STLPort_INCLUDES)  \
-I@top_srcdir@/inc \
-I@top_srcdir@/drv/CPPExt -I@top_srcdir@/src/CPPExt


lib_LTLIBRARIES=libTKCPPExt.la

libTKCPPExt_la_LIBADD = -L$(CASROOT)/$(platform)/lib  \
../TKWOK/libTKWOK.la \
-lTKernel \
$(STLPort_LIB) 

libTKCPPExt_la_SOURCES = \
@top_srcdir@/src/CPPExt/CPPExt.cxx \
@top_srcdir@/src/CPPExt/CPPExt_Alias.cxx \
@top_srcdir@/src/CPPExt/CPPExt_Enum.cxx \
@top_srcdir@/src/CPPExt/CPPExt_Exception.cxx \
@top_srcdir@/src/CPPExt/CPPExt_MPV.cxx \
@top_srcdir@/src/CPPExt/CPPExt_Package.cxx \
@top_srcdir@/src/CPPExt/CPPExt_PersistentCSFDB.cxx \
@top_srcdir@/src/CPPExt/CPPExt_PersistentOBJS.cxx \
@top_srcdir@/src/CPPExt/CPPExt_PersistentOBJY.cxx \
@top_srcdir@/src/CPPExt/CPPExt_Pointer.cxx \
@top_srcdir@/src/CPPExt/CPPExt_Storable.cxx \
@top_srcdir@/src/CPPExt/CPPExt_Transient.cxx

