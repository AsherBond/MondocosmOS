
%{
#include <WOKDeliv_ParseDelivery.h>
#include <stdlib.h>

#define yyv DELIVERYv

%}


%token T_REQUIRES, T_PUTPATH, T_PUTINCLUDE, T_PUTLIB, T_GET, T_NAME, T_IFDEF, T_ENDIF, T_INVALID

%token T_DEVUNIT, T_PACKAGE, T_NOCDLPACK, T_EXECUTABLE, T_INTERFACE, T_ENGINE, T_CLIENT, T_SCHEMA, T_TOOLKIT, T_CCL, T_FRONTAL, T_OLH, T_RESOURCE

%token T_LIBRARY, T_SHARED, T_ARCHIVE, T_DATAOBJECT, T_CDL, T_INCLUDES, T_STATIC, T_DYNAMIC, T_GETRES
%token T_STUB_CLIENT, T_STUB_SERVER, T_FDDB, T_SOURCES, T_DDL

%token T_ALPHA, T_SEPARATOR

%token T_WSPACE, T_NEWLINE

%%


components: 
	component
  |     components component
  ;

component:
	parceldescription
  |     unitdeclaration
  |     parcelifdef
  |     T_NEWLINE
  ;

parceldescription:
	T_PUTPATH T_NEWLINE
        { Traite_PutPath(); }
  |     T_PUTINCLUDE T_NEWLINE
	{ Traite_PutInclude(); }
  |     T_PUTLIB T_NEWLINE
	{ Traite_PutLib(); }
  |	T_GET	T_WSPACE T_ALPHA { Traite_GetUnit(TheText); } T_SEPARATOR T_ALPHA { Traite_GetType(TheText); } T_SEPARATOR T_ALPHA { Traite_GetFile(TheText); } T_NEWLINE
  |	T_NAME	T_WSPACE T_ALPHA T_NEWLINE
	{ Traite_Name(TheText); }
  |	T_REQUIRES T_WSPACE T_ALPHA T_NEWLINE  
        { Traite_Requires(TheText); }
  ;

parcelifdef:
	T_IFDEF T_WSPACE T_ALPHA T_NEWLINE
	{ Traite_Ifdef(TheText); }
  |     T_ENDIF T_NEWLINE
        {Traite_Endif(); }
  ;

unitdeclaration : 
	typeunit T_WSPACE T_ALPHA T_NEWLINE
        { 
	  ClasseElt_DeliverFormatAll(TheType,TheText);
	}
    |   typeunit  T_WSPACE T_ALPHA {ClasseElt_DeliverFormatBase(TheType,TheText);} T_WSPACE attrs T_NEWLINE
	  
;

typeunit : T_DEVUNIT {TheType = T_DEVUNIT;}
	|  T_PACKAGE {TheType = T_PACKAGE;}
	|  T_NOCDLPACK {TheType = T_NOCDLPACK;}
	|  T_EXECUTABLE {TheType = T_EXECUTABLE;}
	|  T_INTERFACE {TheType = T_INTERFACE;}
	|  T_ENGINE {TheType = T_ENGINE;}
	|  T_CLIENT {TheType = T_CLIENT;}
	|  T_SCHEMA {TheType = T_SCHEMA;}
	|  T_TOOLKIT {TheType = T_TOOLKIT;}
	|  T_CCL {TheType = T_CCL;}
	|  T_FRONTAL {TheType = T_FRONTAL ;}
	|  T_RESOURCE {TheType = T_RESOURCE;}
	
;

attr  :      
		T_ARCHIVE       { TheAttrib = T_ARCHIVE;}
	|	T_CDL		{ TheAttrib = T_CDL;}
	|	T_DATAOBJECT	{ TheAttrib = T_DATAOBJECT;}
	|	T_DDL   	{ TheAttrib = T_DDL;}
	|	T_DYNAMIC	{ TheAttrib = T_DYNAMIC;}
	|	T_FDDB          { TheAttrib = T_FDDB;}
	|	T_GETRES	{ TheAttrib = T_GETRES;}
	|	T_INCLUDES	{ TheAttrib = T_INCLUDES;}
	|	T_LIBRARY	{ TheAttrib = T_LIBRARY;}
	|	T_SHARED	{ TheAttrib = T_SHARED;}
	|	T_SOURCES	{ TheAttrib = T_SOURCES;}
	|	T_STATIC	{ TheAttrib = T_STATIC;}
	|	T_STUB_CLIENT   { TheAttrib = T_STUB_CLIENT;}
	|	T_STUB_SERVER   { TheAttrib = T_STUB_SERVER;}
;

attrs :
		attr {ClasseElt_DeliverFormat(TheAttrib);ClasseElt_EndDeliverFormat();}
	|	attr {ClasseElt_DeliverFormat(TheAttrib);} T_WSPACE attrs
	;


