#ifndef _EDL_FunctionSignature_HeaderFile
#define _EDL_FunctionSignature_HeaderFile
#include <EDL_Variable.hxx>
#define EDL_FUNCTION(name) extern "C" EDL_Variable name( const int argc,const EDL_Variable*argv)
typedef EDL_Variable (*EDL_FunctionSignature)(const int,const EDL_Variable*);
#endif
