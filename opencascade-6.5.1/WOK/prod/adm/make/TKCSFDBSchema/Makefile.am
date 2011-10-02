# Makefile.am for Open Cascade toolkit TKCSFDBSchema
srcdir = @srcdir@

VPATH = @srcdir@ : \
@top_srcdir@/drv/CSFDBSchema : @top_srcdir@/src/CSFDBSchema


AM_CXXFLAGS = 


AM_CFLAGS = 


INCLUDES = -I$(CASROOT)/inc  $(STLPort_INCLUDES)  \
-I@top_srcdir@/inc \
-I@top_srcdir@/drv/CSFDBSchema -I@top_srcdir@/src/CSFDBSchema


lib_LTLIBRARIES=libTKCSFDBSchema.la

libTKCSFDBSchema_la_LIBADD = -L$(CASROOT)/$(platform)/lib  \
../TKWOK/libTKWOK.la \
-lTKernel \
$(STLPort_LIB) 

libTKCSFDBSchema_la_SOURCES = \
@top_srcdir@/src/CSFDBSchema/CSFDBSchema.cxx

