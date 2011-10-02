#ifndef _EDL_ProcedureSignature_HeaderFile
#define _EDL_ProcedureSignature_HeaderFile
class EDL_Variable;
#define EDL_PROCEDURE(name) extern "C" void name( const int argc,const EDL_Variable*argv)
typedef void (*EDL_ProcedureSignature)(const int,const EDL_Variable*);
#endif
