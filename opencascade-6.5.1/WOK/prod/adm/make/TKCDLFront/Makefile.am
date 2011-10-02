# Makefile.am for Open Cascade toolkit TKCDLFront
srcdir = @srcdir@

VPATH = @srcdir@ : \
@top_srcdir@/drv/CDLFront : @top_srcdir@/src/CDLFront


AM_CXXFLAGS = 


AM_CFLAGS = 


INCLUDES = -I$(CASROOT)/inc  $(STLPort_INCLUDES)  \
-I@top_srcdir@/inc \
-I@top_srcdir@/drv/CDLFront -I@top_srcdir@/src/CDLFront


lib_LTLIBRARIES=libTKCDLFront.la

libTKCDLFront_la_LIBADD = -L$(CASROOT)/$(platform)/lib  \
../TKWOK/libTKWOK.la \
-lTKernel \
$(STLPort_LIB) 

libTKCDLFront_la_SOURCES = \
@top_srcdir@/drv/CDLFront/CDL.tab.c \
@top_srcdir@/drv/CDLFront/lex.CDL.c \
@top_srcdir@/src/CDLFront/CDLFront.cxx

