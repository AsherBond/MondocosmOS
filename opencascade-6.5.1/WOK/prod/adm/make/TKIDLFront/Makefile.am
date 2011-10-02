# Makefile.am for Open Cascade toolkit TKIDLFront
srcdir = @srcdir@

VPATH = @srcdir@ : \
@top_srcdir@/drv/IDLFront : @top_srcdir@/src/IDLFront


AM_CXXFLAGS = 


AM_CFLAGS = 


INCLUDES = -I$(CASROOT)/inc  $(STLPort_INCLUDES)  \
-I@top_srcdir@/inc \
-I@top_srcdir@/drv/IDLFront -I@top_srcdir@/src/IDLFront


lib_LTLIBRARIES=libTKIDLFront.la

libTKIDLFront_la_LIBADD = -L$(CASROOT)/$(platform)/lib  \
../TKWOK/libTKWOK.la \
-lTKernel \
$(STLPort_LIB) 

libTKIDLFront_la_SOURCES = \
@top_srcdir@/drv/IDLFront/IDL.tab.c \
@top_srcdir@/drv/IDLFront/lex.IDL.c \
@top_srcdir@/src/IDLFront/IDLFront.cxx

