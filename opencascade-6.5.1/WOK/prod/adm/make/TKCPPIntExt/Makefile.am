# Makefile.am for Open Cascade toolkit TKCPPIntExt
srcdir = @srcdir@

VPATH = @srcdir@ : \
@top_srcdir@/drv/CPPIntExt : @top_srcdir@/src/CPPIntExt


AM_CXXFLAGS = 


AM_CFLAGS = 


INCLUDES = -I$(CASROOT)/inc  $(STLPort_INCLUDES)  \
-I@top_srcdir@/inc \
-I@top_srcdir@/drv/CPPIntExt -I@top_srcdir@/src/CPPIntExt


lib_LTLIBRARIES=libTKCPPIntExt.la

libTKCPPIntExt_la_LIBADD = -L$(CASROOT)/$(platform)/lib  \
../TKWOK/libTKWOK.la \
-lTKernel \
$(STLPort_LIB) 

libTKCPPIntExt_la_SOURCES = \
@top_srcdir@/src/CPPIntExt/CPPIntExt.cxx \
@top_srcdir@/src/CPPIntExt/CPPIntExt_Body.cxx \
@top_srcdir@/src/CPPIntExt/CPPIntExt_Case.cxx \
@top_srcdir@/src/CPPIntExt/CPPIntExt_Ccl.cxx \
@top_srcdir@/src/CPPIntExt/CPPIntExt_EInit.cxx \
@top_srcdir@/src/CPPIntExt/CPPIntExt_Load.cxx

