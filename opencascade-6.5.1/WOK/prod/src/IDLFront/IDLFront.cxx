#include <string.h>
#include <stdio.h>
// Standard includes
//
#include <Standard_ErrorHandler.hxx>
#include <MS_TraductionError.hxx>
#include <WOKTools_Messages.hxx>

#include <MS_MetaSchema.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <idl_rules.h>

extern "C" {
void IDLrestart(FILE*);
}
// lex variable
//      line number
//   
#ifndef WNT
extern int IDLlineno;
#else
extern "C" int IDLlineno;
#endif  // WNT
   
#ifndef WNT
extern FILE             *IDLin;
#else
extern "C" FILE             *IDLin;
#endif  // WNT
extern "C" int IDLparse();


// BEGIN Variables
//
static int   YY_nb_error;
static int   YY_nb_warning;

static Handle(TCollection_HAsciiString) IDLFileName;

Handle(TColStd_HSequenceOfHAsciiString) ListOfGlobalUsed;
Handle(TColStd_HSequenceOfHAsciiString) ListOfTypeUsed;
Handle(TColStd_HSequenceOfHAsciiString) ListOfInst;
Handle(TColStd_HSequenceOfHAsciiString) ListOfGen;
Handle(MS_MetaSchema)           theMetaSchema;

IDLGlobal _IDLGlobal;

// END variables

// BEGIN Traductor (call from yacc)
//

extern "C" {
 void IDLerror(char* text)
  {
   extern int IDLlineno;
  
   // The unix like error declaration 
   //
   if (text == NULL) {
     ErrorMsg() << "IDL" << "\"" << IDLFileName->ToCString() << "\"" <<  ", line " << IDLlineno << ": syntax error..." << endm;
     MS_TraductionError::Raise("Syntax error");
   }
   else {
     ErrorMsg() << "IDL" << "\"" << IDLFileName->ToCString() << "\"" <<  ", line " << IDLlineno << ": " << text << endm;
     YY_nb_error++;
   }
 }

  int IDLTranslate(const Handle(MS_MetaSchema)&             aMetaSchema, 
		   const Handle(TCollection_HAsciiString)&  aFileName,
		   const Handle(TColStd_HSequenceOfHAsciiString)& aGlobalList,
		   const Handle(TColStd_HSequenceOfHAsciiString)& aTypeList,
		   const Handle(TColStd_HSequenceOfHAsciiString)& anInstList,
		   const Handle(TColStd_HSequenceOfHAsciiString)& anGenList);

}

void IDL_InterfaceDeclaration() 
{
  strcpy(_IDLGlobal.interfacename,_IDLGlobal.idname);
#ifdef DEB
  cout << "Declaration : identifier -> interface : " << _IDLGlobal.interfacename << endl;
#endif
}

void IDL_SetIdentifier(char *idname)
{
  strcpy(_IDLGlobal.idname,idname);
#ifdef DEB
  cout << "Declaration : identifier : " << _IDLGlobal.idname << endl;
#endif
}

void IDL_InterfaceDefinitionBegin()
{
  _IDLGlobal.traductorstate = IDL_INTERFACEDECL;

  ListOfGlobalUsed->Append(new TCollection_HAsciiString(_IDLGlobal.interfacename));
#ifdef DEB
  cout << "Declaration : BEGIN interface : " << _IDLGlobal.interfacename << endl;
#endif
}

void IDL_InterfaceDefinitionEnd()
{
  _IDLGlobal.traductorstate = IDL_INTERFACEDECL;
#ifdef DEB
  cout << "Declaration : END interface : " << _IDLGlobal.interfacename << endl;
#endif
} 
//
// END Traductor

void IDL_Main()
{
  YY_nb_error = 0;
  IDLparse();
} 

int TraductionMain(char *FileName)
{

  IDLin = fopen(FileName,"r");



  if (IDLin == NULL) {
    ErrorMsg() << "IDL" << " File not found : " << FileName << endm;
    MS_TraductionError::Raise("File not found.");
  }

  IDLrestart(IDLin);
  // Boot file
  //
  IDL_Main();

  fclose(IDLin);

  if (YY_nb_error > 0) {
    ErrorMsg() << "IDL" << YY_nb_error << " errors." << endm;
  } 
  
  if (YY_nb_warning > 0) {
    WarningMsg() << "IDL" << YY_nb_warning << " warnings." << endm;
  }

  return YY_nb_error;
}


int IDLTranslate(const Handle(MS_MetaSchema)&             aMetaSchema, 
		 const Handle(TCollection_HAsciiString)&  aFileName,
		 const Handle(TColStd_HSequenceOfHAsciiString)& aGlobalList,
		 const Handle(TColStd_HSequenceOfHAsciiString)& aTypeList,
		 const Handle(TColStd_HSequenceOfHAsciiString)& anInstList,
		 const Handle(TColStd_HSequenceOfHAsciiString)& anGenList) 
{
  volatile Standard_Integer  ErrorLevel = 0;

  theMetaSchema    = aMetaSchema;
  ListOfGlobalUsed = aGlobalList;
  ListOfTypeUsed   = aTypeList;
  ListOfInst       = anInstList;
  ListOfGen        = anGenList;

  if (!aFileName.IsNull()) {
    IDLlineno = 1;
    IDLFileName = aFileName;
    
    try {
      OCC_CATCH_SIGNALS
      ErrorLevel = TraductionMain((char *)aFileName->ToCString());
    }
    catch(Standard_Failure) {
      fclose(IDLin);
      ErrorLevel = 1;
    }
  }
  else {
    ErrorLevel = 1;
  }

  return ErrorLevel;
}
