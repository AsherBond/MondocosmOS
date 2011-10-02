# Makefile.am for Open Cascade toolkit TKCPPClient
srcdir = @srcdir@

VPATH = @srcdir@ : \
@top_srcdir@/drv/CPPClient : @top_srcdir@/src/CPPClient


AM_CXXFLAGS = 


AM_CFLAGS = 


INCLUDES = -I$(CASROOT)/inc  $(STLPort_INCLUDES)  \
-I@top_srcdir@/inc \
-I@top_srcdir@/drv/CPPClient -I@top_srcdir@/src/CPPClient


lib_LTLIBRARIES=libTKCPPClient.la

libTKCPPClient_la_LIBADD = -L$(CASROOT)/$(platform)/lib  \
../TKWOK/libTKWOK.la \
-lTKernel \
$(STLPort_LIB) 

libTKCPPClient_la_SOURCES = \
@top_srcdir@/src/CPPClient/CPPClient.cxx \
@top_srcdir@/src/CPPClient/CPPClient_Enum.cxx \
@top_srcdir@/src/CPPClient/CPPClient_MPV.cxx \
@top_srcdir@/src/CPPClient/CPPClient_Package.cxx \
@top_srcdir@/src/CPPClient/CPPClient_Transient.cxx

