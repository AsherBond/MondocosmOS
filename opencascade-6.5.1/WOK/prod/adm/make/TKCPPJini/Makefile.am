# Makefile.am for Open Cascade toolkit TKCPPJini
srcdir = @srcdir@

VPATH = @srcdir@ : \
@top_srcdir@/drv/CPPJini : @top_srcdir@/src/CPPJini


AM_CXXFLAGS = 


AM_CFLAGS = 


INCLUDES = -I$(CASROOT)/inc  $(STLPort_INCLUDES)  \
-I@top_srcdir@/inc \
-I@top_srcdir@/drv/CPPJini -I@top_srcdir@/src/CPPJini


lib_LTLIBRARIES=libTKCPPJini.la

libTKCPPJini_la_LIBADD = -L$(CASROOT)/$(platform)/lib  \
../TKWOK/libTKWOK.la \
-lTKernel \
$(STLPort_LIB) 

libTKCPPJini_la_SOURCES = \
@top_srcdir@/drv/CPPJini/CPPJini_SequenceNodeOfSequenceOfClientInfo_0.cxx \
@top_srcdir@/drv/CPPJini/CPPJini_SequenceOfClientInfo_0.cxx \
@top_srcdir@/src/CPPJini/CPPJini.cxx \
@top_srcdir@/src/CPPJini/CPPJini_ClientInfo.cxx \
@top_srcdir@/src/CPPJini/CPPJini_Enum.cxx \
@top_srcdir@/src/CPPJini/CPPJini_MPV.cxx \
@top_srcdir@/src/CPPJini/CPPJini_Package.cxx \
@top_srcdir@/src/CPPJini/CPPJini_Transient.cxx

