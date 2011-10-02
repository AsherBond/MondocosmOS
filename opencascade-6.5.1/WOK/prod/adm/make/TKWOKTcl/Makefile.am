# Makefile.am for Open Cascade toolkit TKWOKTcl
srcdir = @srcdir@

VPATH = @srcdir@ : \
@top_srcdir@/drv/WOKTclUtils : @top_srcdir@/src/WOKTclUtils: \
@top_srcdir@/drv/WOKTclTools : @top_srcdir@/src/WOKTclTools: \
@top_srcdir@/drv/MSAPI : @top_srcdir@/src/MSAPI: \
@top_srcdir@/drv/WOKTCL : @top_srcdir@/src/WOKTCL


AM_CXXFLAGS = 


AM_CFLAGS = 


INCLUDES = -I$(CASROOT)/inc  $(STLPort_INCLUDES) $(CSF_TclLibs_INCLUDES) $(CSF_TclTkLibs_INCLUDES)  \
-I@top_srcdir@/inc \
-I@top_srcdir@/drv/WOKTclUtils -I@top_srcdir@/src/WOKTclUtils \
-I@top_srcdir@/drv/WOKTclTools -I@top_srcdir@/src/WOKTclTools \
-I@top_srcdir@/drv/MSAPI -I@top_srcdir@/src/MSAPI \
-I@top_srcdir@/drv/WOKTCL -I@top_srcdir@/src/WOKTCL


lib_LTLIBRARIES=libTKWOKTcl.la

libTKWOKTcl_la_LIBADD = -L$(CASROOT)/$(platform)/lib  \
../TKWOK/libTKWOK.la \
-lTKernel \
$(STLPort_LIB) $(CSF_TclLibs_LIB) $(CSF_TclTkLibs_LIB) 

libTKWOKTcl_la_SOURCES = \
@top_srcdir@/src/MSAPI/MSAPI_Class.cxx \
@top_srcdir@/src/MSAPI/MSAPI_ExternMet.cxx \
@top_srcdir@/src/MSAPI/MSAPI_GenClass.cxx \
@top_srcdir@/src/MSAPI/MSAPI_InstClass.cxx \
@top_srcdir@/src/MSAPI/MSAPI_MemberMet.cxx \
@top_srcdir@/src/MSAPI/MSAPI_MetaSchema.cxx \
@top_srcdir@/src/MSAPI/MSAPI_Method.cxx \
@top_srcdir@/src/MSAPI/MSAPI_Package.cxx \
@top_srcdir@/src/MSAPI/MSAPI_Schema.cxx \
@top_srcdir@/src/MSAPI/MSAPI_StdClass.cxx \
@top_srcdir@/src/WOKTCL/WOKTCL_DefaultCommand.cxx \
@top_srcdir@/src/WOKTCL/WOKTCL_Interpretor.cxx \
@top_srcdir@/src/WOKTCL/WOKTCL_TriggerHandler.cxx \
@top_srcdir@/src/WOKTclTools/WOKTclTools_Interpretor.cxx \
@top_srcdir@/src/WOKTclTools/WOKTclTools_Messages.cxx \
@top_srcdir@/src/WOKTclTools/WOKTclTools_MsgAPI.cxx \
@top_srcdir@/src/WOKTclTools/WOKTclTools_Package.cxx \
@top_srcdir@/src/WOKTclTools/WOKTclTools_TriggerHandler.cxx \
@top_srcdir@/src/WOKTclUtils/WOKTclUtils_Path.cxx

