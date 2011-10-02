# Makefile.am for Open Cascade toolkit TKTCPPExt
srcdir = @srcdir@

VPATH = @srcdir@ : \
@top_srcdir@/drv/TCPPExt : @top_srcdir@/src/TCPPExt


AM_CXXFLAGS = 


AM_CFLAGS = 


INCLUDES = -I$(CASROOT)/inc  $(STLPort_INCLUDES)  \
-I@top_srcdir@/inc \
-I@top_srcdir@/drv/TCPPExt -I@top_srcdir@/src/TCPPExt


lib_LTLIBRARIES=libTKTCPPExt.la

libTKTCPPExt_la_LIBADD = -L$(CASROOT)/$(platform)/lib  \
../TKWOK/libTKWOK.la \
-lTKernel \
$(STLPort_LIB) 

libTKTCPPExt_la_SOURCES = \
@top_srcdir@/src/TCPPExt/TCPPExt.cxx

