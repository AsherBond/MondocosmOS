#include <EDL_Interpretor.ixx>
#include <EDL.hxx>
#include <Standard_ErrorHandler.hxx>
#include <Standard_NoSuchObject.hxx>
#include <Standard_NullObject.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TCollection_HAsciiString.hxx>
#include <TCollection_AsciiString.hxx>
#include <EDL_FunctionSignature.hxx>
#include <EDL_ProcedureSignature.hxx>
#include <Standard_PCharacter.hxx>
#include <stdio.h>

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#include <edl_rule.h>

#include <EDL.tab.h>


#ifdef WNT
# include <windows.h>
#endif

#ifdef HAVE_UNISTD_H
# include <unistd.h>
#endif

extern "C" {int EDLparse();}
extern "C" {int EDLlex();}
extern "C" {void EDL_SetFile();}

#ifndef WNT
extern FILE *EDLin;
extern int   EDLlineno;
extern char *EDLtext;
extern char  FileName[][256];
extern int   numFileDesc;
#else
extern "C" FILE *EDLin;
extern "C" int   EDLlineno;
extern "C" char *EDLtext;
extern "C" char  FileName[][256];
extern "C" int   numFileDesc;
#endif  // WNT

extern "C" {
//  void EDLerror(char* err,char *arg) {
  void EDLerror(char* ,char *) {
    EDL::PrintError(EDL_SYNTAXERROR," ");
    Standard_NullObject::Raise();
  }
}

EDL_Interpretor *GlobalInter     = NULL;

TCollection_AsciiString EDL_CurrentFile;

// Class EDL_Interpretor
//
EDL_Interpretor::EDL_Interpretor()
{
  myIncludeTable = new TColStd_HSequenceOfAsciiString;
  myIncludeTable->Append(".");

  myVariableList = new EDL_HSequenceOfVariable;

  myArgList      = new EDL_HSequenceOfVariable;

  myRetList      = new EDL_HSequenceOfVariable;

  myParameterType = EDL_VARIABLE;
}

void EDL_Interpretor::ClearAll()
{
  mySymbolTable.Clear();

  if (!myIncludeTable.IsNull()) {
    myIncludeTable->Clear();
  }

  if (!myIncludeTable.IsNull()) {
    myIncludeTable->Append(".");
  }

  myTemplateTable.Clear();
  myFileTable.Clear();
  myLibraryTable.Clear();
  myExecutionStatus.Clear();
  myParameterType = EDL_VARIABLE;
  myExpressionMember.Clear();
  myPrintList.Clear();
  myCurrentTemplate.Clear();

  if (!myVariableList.IsNull()) {
    myVariableList->Clear();
  }

  if (!myArgList.IsNull()) {
    myArgList->Clear();
  }

  if (!myRetList.IsNull()) {
    myRetList->Clear();
  }
}

void EDL_Interpretor::ClearSymbolTable()
{
  mySymbolTable.Clear();
}

void EDL_Interpretor::ClearTemplateTable()
{
  myTemplateTable.Clear();
}

void EDL_Interpretor::ClearVariableList()
{
  myVariableList->Clear();
}

void EDL_Interpretor::ClearArgList()
{
  myArgList->Clear();
}

void EDL_Interpretor::ClearRetList()
{
  myRetList->Clear();
}

extern "C" {
 void EDLrestart(FILE *);
}

EDL_Error EDL_Interpretor::Parse(const Standard_CString aFile)
{
  GlobalInter = this;
  Standard_Boolean IsFound  = Standard_False;
  Standard_Integer DirCount = 1;

  Handle(TColStd_HSequenceOfAsciiString) IncludeDirectory = GlobalInter->GetIncludeDirectory();
  
  if (aFile != NULL) {
    FILE *fic = 0L;
    DirCount  = 1;
#ifndef WNT
    if( !access(aFile, F_OK) ) 
#else
    if ( GetFileAttributes(aFile) != 0xFFFFFFFF ) 
#endif
    {
      if (   (  fic = fopen ( aFile, "r" )  ) != NULL   ) {
        IsFound = Standard_True;
      }
    }

    while (!IsFound && DirCount <= IncludeDirectory->Length()) {
      static char tmpName[1024];
      const TCollection_AsciiString& adir = IncludeDirectory->Value(DirCount);
      memcpy(tmpName, adir.ToCString(), adir.Length());
      tmpName[adir.Length()] = '/';
      strcpy(&(tmpName[adir.Length()+1]),aFile);  
      
#ifndef WNT
      if( !access(tmpName, F_OK) ) 
#else
      if ( GetFileAttributes(tmpName) != 0xFFFFFFFF ) 
#endif
      {
	if (   (  fic = fopen ( tmpName, "r" )  ) != NULL   ) {
	  IsFound = Standard_True;
	}
      }
      
      //delete [] tmpName;
      DirCount++;
    }
    
    if (fic) {
      edlstring edlstr;
      edlstr.str = (char *)aFile;
      edlstr.length = strlen(aFile);
      EDL_SetCurrentFile(edlstr);
      EDLin           = fic;
      EDLlineno       = 1;
      numFileDesc = -1;
      EDLrestart(EDLin);
      EDLparse();

      fclose(fic);
    }
    else {
      return EDL_FILEOPENED;
    }

    EDLlineno = -1;
  }
  else {
    return EDL_FILEOPENED;
  }

  GlobalInter = NULL;

  return EDL_NORMAL;
}

EDL_Error EDL_Interpretor::AddIncludeDirectory(const Standard_CString aDirectory)
{
  TCollection_AsciiString aDir(aDirectory);

  myIncludeTable->Append(aDir);

  return EDL_NORMAL;
}

Handle(TColStd_HSequenceOfAsciiString) EDL_Interpretor::GetIncludeDirectory() const
{
  return myIncludeTable;
}

EDL_Error EDL_Interpretor::AddFile(const Standard_CString aVariable, const Standard_CString filename)
{
  TCollection_AsciiString  anAscName(aVariable);
  char                    *realFilename = (char *)filename;
  
  // we can open a file with a name defined by a string or a variable
  //
  if (myParameterType == EDL_VARIABLE) {
    TCollection_AsciiString  aFileName(filename);

    if (mySymbolTable.IsBound(aFileName)) {
      realFilename = (char *)mySymbolTable.Find(aFileName).GetValue();
    }
    else {
      EDL::PrintError(EDL_VARNOTFOUND,filename);
      return EDL_VARNOTFOUND;
    }
  }

  if (myFileTable.IsBound(anAscName)) {
    EDL::PrintError(EDL_FILEOPENED,aVariable);
    return EDL_FILEOPENED;
  }
  else {
    EDL_File aFile(realFilename);

    if (aFile.Open()) {
      myFileTable.Bind(anAscName,aFile);
    }
    else {
      EDL::PrintError(EDL_FILENOTOPENED,realFilename);
      return EDL_FILENOTOPENED;
    }
  }

  return EDL_NORMAL;
}

EDL_File& EDL_Interpretor::GetFile(const Standard_CString aVariable)
{
  TCollection_AsciiString  anAscName(aVariable);

  if (myFileTable.IsBound(anAscName)) {
    return myFileTable.ChangeFind(anAscName);
  }
  else {
    // Raise
    //
    EDL::PrintError(EDL_FILENOTOPENED,aVariable);
    Standard_NoSuchObject::Raise();
  }
    return myFileTable.ChangeFind(anAscName);
}

void EDL_Interpretor::RemoveFile(const Standard_CString aVariable)
{
  TCollection_AsciiString  anAscName(aVariable);

  if (myFileTable.IsBound(anAscName)) {
    myFileTable.UnBind(anAscName);
  }
  else {
    // Raise
    //
    EDL::PrintError(EDL_FILENOTOPENED,aVariable);
    Standard_NoSuchObject::Raise();
  }
}

EDL_Error EDL_Interpretor::AddVariable(const Standard_CString aVariable, const Standard_CString aValue)
{
  if (aVariable != NULL && aValue != NULL) {
    TCollection_AsciiString  anAscName(aVariable);

    if (aVariable[0] != '%')       {
      // Raise
      //
      anAscName.AssignCat(" : wrong indirection...");
      EDL::PrintError(EDL_VARNOTFOUND,anAscName.ToCString());
      Standard_NoSuchObject::Raise();
    }

    if (mySymbolTable.IsBound(anAscName)) {
      mySymbolTable.ChangeFind(anAscName).SetValue(aValue);
    }
    else {
      EDL_Variable aVar(aVariable,aValue);
      mySymbolTable.Bind(anAscName,aVar);
    }
  }
  else {
    return EDL_SYNTAXERROR;
  }

  return EDL_NORMAL;
}

EDL_Variable& EDL_Interpretor::GetVariable(const Standard_CString aVariable)
{
  if (aVariable != NULL) {
    
    TCollection_AsciiString  anAscName(aVariable);

    if (mySymbolTable.IsBound(anAscName)) {
      return mySymbolTable.ChangeFind(anAscName);
    }
    else {
      // Raise
      //
      EDL::PrintError(EDL_VARNOTFOUND,aVariable);
      Standard_NoSuchObject::Raise();
    }
  }
  else {
    // Raise
    EDL::PrintError(EDL_VARNOTFOUND,aVariable);
    Standard_NullObject::Raise();
  }
      return mySymbolTable.ChangeFind(TCollection_AsciiString ());
}

Standard_Boolean EDL_Interpretor::IsDefined(const Standard_CString aVariable) const
{
 if (aVariable != NULL) {
    
    TCollection_AsciiString  anAscName(aVariable);

    if (mySymbolTable.IsBound(anAscName) ||myTemplateTable.IsBound(anAscName)) {
      return Standard_True;
    }
    else {
      return Standard_False;
    }
  }
 else {
   return Standard_False;
 }
}

Standard_Boolean EDL_Interpretor::IsFile(const Standard_CString aFileName) const
{
 if (aFileName != NULL) {
    
    TCollection_AsciiString  fname(aFileName);
    
    Standard_Boolean IsFound  = Standard_False;
    Standard_Integer DirCount = 1;

    Handle(TColStd_HSequenceOfAsciiString) IncludeDirectory = GetIncludeDirectory();

    DirCount  = 1;
    while (!IsFound && DirCount <= IncludeDirectory->Length()) {
      static char tmpName[1024];
      const TCollection_AsciiString& adir = IncludeDirectory->Value(DirCount);
      memcpy(tmpName, adir.ToCString(), adir.Length());
      tmpName[adir.Length()] = '/';
      strcpy(&(tmpName[adir.Length()+1]),aFileName);  
      
#ifndef WNT
      if( !access(tmpName, F_OK) ) 
#else
      if ( GetFileAttributes(tmpName) != 0xFFFFFFFF ) 
#endif
	{
	  IsFound = Standard_True;
	}
      DirCount++;
    }
    return IsFound;
  }
 else {
   return Standard_False;
 }
}

void EDL_Interpretor::RemoveVariable(const Standard_CString aVariable)
{
  if (aVariable != NULL) {
    TCollection_AsciiString  anAscName(aVariable);

    if (mySymbolTable.IsBound(anAscName)) {
      mySymbolTable.UnBind(anAscName);
    }
    else {
      // Raise
      //
      EDL::PrintError(EDL_VARNOTFOUND,aVariable);
      Standard_NoSuchObject::Raise();
    }
  }
  else {
    // Raise
    EDL::PrintError(EDL_VARNOTFOUND,aVariable);
    Standard_NullObject::Raise();
  }
}

EDL_Error EDL_Interpretor::AddTemplate(const Standard_CString aTemplate)
{
  if (aTemplate != NULL) {
    myCurrentTemplate = aTemplate;
    
    if (myTemplateTable.IsBound(myCurrentTemplate)) {
      myTemplateTable.UnBind(myCurrentTemplate);
    }
    
    EDL_Template aTmp(aTemplate);
    myTemplateTable.Bind(myCurrentTemplate,aTmp);
  }
  else {
    // Raise
    //
    Standard_NullObject::Raise();
  }

  return EDL_NORMAL;
}

EDL_Error EDL_Interpretor::AddToTemplate(const Standard_CString aTemplate)
{
  if (aTemplate != NULL) {
    
    if (myTemplateTable.IsBound(myCurrentTemplate)) {
      myCurrentTemplate = aTemplate;
    }
    else {
      EDL::PrintError(EDL_TEMPLATENOTDEFINED,aTemplate);
      Standard_NoSuchObject::Raise();
    }
  }
  else {
    // Raise
    //
    Standard_NullObject::Raise();
  }

  return EDL_NORMAL;
}
EDL_Error EDL_Interpretor::ClearTemplate(const Standard_CString aTemplate)
{
  if (aTemplate != NULL) {
    TCollection_AsciiString TmpName(aTemplate);

    if (myTemplateTable.IsBound(TmpName)) {
      myTemplateTable.ChangeFind(TmpName).ClearLines();
    }
    else {
      EDL::PrintError(EDL_TEMPLATENOTDEFINED,aTemplate);
      Standard_NoSuchObject::Raise();
    }
  }
  else {
    // Raise
    //
    Standard_NullObject::Raise();
  }

  return EDL_NORMAL;
}

EDL_Template& EDL_Interpretor::GetTemplate(const Standard_CString aTemplate)
{
  if (aTemplate != NULL) {
    TCollection_AsciiString TmpName(aTemplate);

    if (myTemplateTable.IsBound(TmpName)) {
      return myTemplateTable.ChangeFind(TmpName);
    }
    else {
      EDL::PrintError(EDL_TEMPLATENOTDEFINED,aTemplate);
      Standard_NoSuchObject::Raise();
    }
  }
  else {
    // Raise
    //
    Standard_NullObject::Raise();
  }
      return myTemplateTable.ChangeFind(TCollection_AsciiString());
}

EDL_DataMapIteratorOfMapOfTemplate EDL_Interpretor::GetTemplateIterator() const
{
  EDL_DataMapIteratorOfMapOfTemplate amapi(myTemplateTable);

  return amapi;
}

EDL_DataMapIteratorOfMapOfVariable EDL_Interpretor::GetVariableIterator() const
{
  EDL_DataMapIteratorOfMapOfVariable amapi(mySymbolTable);

  return amapi;
}

void EDL_Interpretor::EvalTemplate(const Standard_CString aTemplate, const Standard_CString aResult)
{
  TCollection_AsciiString anAscName(aTemplate);

  myTemplateTable.ChangeFind(anAscName).Eval(myVariableList);

  Handle(TColStd_HSequenceOfAsciiString) aNewResult = myTemplateTable.Find(anAscName).GetEval();
  Standard_PCharacter                    aValue;
  Standard_Integer                       nbByte = 0,
                                         i;
  for (i = 1; i <= aNewResult->Length(); i++) {
    nbByte = nbByte + aNewResult->Value(i).Length();
  }

  aValue = (Standard_PCharacter) Standard::Allocate(nbByte + 1);
  aValue[0] = '\0';
  Standard_Integer idx=0;
  for (i = 1; i <= aNewResult->Length(); i++) {
    const TCollection_AsciiString& astr = aNewResult->Value(i);
    memcpy(&aValue[idx], astr.ToCString(), astr.Length());
    idx += astr.Length();
  }
  aValue[nbByte] = '\0';

  AddVariable(aResult,aValue);

  Standard::Free((void*&)aValue);
}

//=======================================================================
//function : RemoveTemplate
//purpose  : 
//=======================================================================
void EDL_Interpretor::RemoveTemplate(const Standard_CString aTemplate)
{
  if (aTemplate != NULL) {
    TCollection_AsciiString TmpName(aTemplate);
    
    if (myTemplateTable.IsBound(TmpName)) {
      myTemplateTable.UnBind(TmpName);
    }
  }
  else {
    // Raise
    //
    Standard_NullObject::Raise();
  }
}

EDL_Error EDL_Interpretor::AddLibrary(const Standard_CString aLibrary)
{
  if (aLibrary != NULL) {
    TCollection_AsciiString  anAscName(aLibrary);

    if (myLibraryTable.IsBound(anAscName)) {
    }
    else {
      EDL_Library aLib(aLibrary);
      char        *aStatus = (char *)aLib.GetStatus();
      
      if (aStatus != NULL) {
	EDL::PrintError(EDL_LIBNOTOPEN,aStatus);
	return EDL_LIBNOTOPEN;
      } 
      else {
	myLibraryTable.Bind(anAscName,aLib);
      }
    }
  }
  else {
    // Raise
    //
    Standard_NullObject::Raise();
  }
  return EDL_LIBNOTOPEN;
}

EDL_Library& EDL_Interpretor::GetLibrary(const Standard_CString aLibrary)
{
  if (aLibrary != NULL) {
    TCollection_AsciiString aName(aLibrary);
    
    if (myLibraryTable.IsBound(aName)) {
      return myLibraryTable.ChangeFind(aName);
    }
    else {
      // Raise
      //
      Standard_NoSuchObject::Raise();
    }
  }
  else {
    // Raise
    //
    Standard_NullObject::Raise();
  }
  return myLibraryTable.ChangeFind(TCollection_AsciiString());
}

EDL_Error EDL_Interpretor::CallFunction(const Standard_CString libname, const Standard_CString funcname, const Standard_CString returnName) 
{
  if (libname != NULL) {
    TCollection_AsciiString  anAscName(libname);
    
    if (!myLibraryTable.IsBound(anAscName)) {
      EDL::PrintError(EDL_LIBRARYNOTFOUND,libname);
      return EDL_LIBRARYNOTFOUND;
    }
    else {
      OSD_Function aFunc = myLibraryTable.Find(anAscName).GetSymbol(funcname);

      if (aFunc == NULL) {
	EDL::PrintError(EDL_FUNCTIONNOTFOUND,funcname);
	return EDL_FUNCTIONNOTFOUND;
      }
      else {
	EDL_Variable* argv = new EDL_Variable[myArgList->Length()];
	EDL_Variable returnVar;
	Standard_Integer argc = myArgList->Length();
	Standard_Integer i;

	for (i = 0; i < argc; i++) {
	  argv[i] = myArgList->Value(i+1);
	}

	if (returnName != 0L) {
	  EDL_FunctionSignature aFFunc = (EDL_FunctionSignature)aFunc;
	  returnVar = (*aFFunc)(argc,argv);
	  AddVariable(returnName,returnVar.GetValue());
	}
	else {
	  EDL_ProcedureSignature aFFunc = (EDL_ProcedureSignature)aFunc;
	  (*aFFunc)(argc,argv);
	}

	delete [] argv;
	myArgList->Clear();
	myRetList->Clear();
      }
    }
  }

  return EDL_NORMAL;
}

void EDL_Interpretor::RemoveLibrary(const Standard_CString aLibrary)
{
  if (aLibrary != NULL) {
    TCollection_AsciiString aName(aLibrary);
    
    if (myLibraryTable.IsBound(aName)) {
      myLibraryTable.UnBind(aName);
    }
    else {
      // Raise
      //
      EDL::PrintError(EDL_LIBRARYNOTFOUND,aLibrary);
      Standard_NoSuchObject::Raise();
    }
  }
  else {
    // Raise
    //
    Standard_NullObject::Raise();
  }
}

void EDL_Interpretor::AddExecutionStatus(const Standard_Boolean aValue)
{
  myExecutionStatus.Push(aValue);
}

Standard_Boolean EDL_Interpretor::RemoveExecutionStatus()
{
  Standard_Boolean aResult;
  
  if (!myExecutionStatus.IsEmpty()) {
    aResult = myExecutionStatus.Top();
    myExecutionStatus.Pop();
  }
  else {
    aResult = Standard_True;
  }
  
  return aResult;
}

Standard_Boolean EDL_Interpretor::GetExecutionStatus()
{
  Standard_Boolean aResult;

  if (!myExecutionStatus.IsEmpty()) {
    aResult = myExecutionStatus.Top();
  }
  else {
    aResult = Standard_True;
  }

  return aResult;
}

void EDL_Interpretor::SetParameterType(const EDL_ParameterMode aMode)
{
  myParameterType = aMode;
}

EDL_ParameterMode EDL_Interpretor::GetParameterType() const
{
  return myParameterType;
}

void EDL_Interpretor::AddExpressionMember(const Standard_Boolean aValue)
{
  myExpressionMember.Push(aValue);
}

Standard_Boolean EDL_Interpretor::GetExpressionMember() 
{
  Standard_Boolean aResult = myExpressionMember.Top();
  
  myExpressionMember.Pop();

  return aResult;
}

void EDL_Interpretor::SetPrintList(const Standard_CString aValue)
{
  if (aValue != NULL) {
    myPrintList = aValue;
  }
  else {
    myPrintList.Clear();
  }
}

TCollection_AsciiString& EDL_Interpretor::GetPrintList()
{
  return myPrintList;
}

void EDL_Interpretor::SetCurrentTemplate(const Standard_CString aValue)
{
  if (aValue != NULL) {
    myCurrentTemplate = aValue;
  }
  else {
    myCurrentTemplate.Clear();
  }
}

TCollection_AsciiString& EDL_Interpretor::GetCurrentTemplate()
{
  return myCurrentTemplate;
}

void EDL_Interpretor::AddToVariableList(const Standard_CString aVariable)
{
  if (aVariable != NULL) {
    TCollection_AsciiString aVar(aVariable);
    
    if (!mySymbolTable.IsBound(aVar)) {
      EDL::PrintError(EDL_VARNOTFOUND,aVariable);
      // Raise
      //
      Standard_NoSuchObject::Raise();
    }
    else {
      myVariableList->Append(mySymbolTable.Find(aVar));
    }
  }
  else {
    // Raise
    //
    Standard_NullObject::Raise();
  }
}

Handle(EDL_HSequenceOfVariable) EDL_Interpretor::GetVariableList() const
{
  return myVariableList;
}

void EDL_Interpretor::AddToArgList(const Standard_CString aVariable)
{
  if (aVariable != NULL) {
    TCollection_AsciiString aVar(aVariable);
    
    if (!mySymbolTable.IsBound(aVar)) {
      EDL::PrintError(EDL_VARNOTFOUND,aVariable);
      // Raise
      //
      Standard_NoSuchObject::Raise();
    }
    else {
      myArgList->Append(mySymbolTable.Find(aVar));
    }
  }
  else {
    // Raise
    //
    Standard_NullObject::Raise();
  }
}

void EDL_Interpretor::AddToArgList(const Standard_CString aVariable, const Standard_CString aValue)
{
  EDL_Variable aVar(aVariable,aValue);
  myArgList->Append(aVar);
}

// Implementation
// --------------

// add an includes directory to the table
//
void edl_add_include_directory(const edlstring adirectory)
{
  GlobalInter->AddIncludeDirectory(adirectory.str);
  if (adirectory.str) Standard::Free((void*&)adirectory.str);
}

// set type of parameter to varname
//
void edl_set_varname()
{
  GlobalInter->SetParameterType(EDL_VARIABLE);
}

// set type of parameter to str
//
void edl_set_str()
{
  GlobalInter->SetParameterType(EDL_STRING);
}

// answer if a instruction can be executed
// in respect with the execution status stack
//
Standard_Boolean edl_must_execute()
{
  return GlobalInter->GetExecutionStatus();
}

// destruction of a variable :
//
void edl_unset_var(const edlstring varname) 
{ 
  if (!edl_must_execute()) return;

  GlobalInter->RemoveVariable(varname.str);
}

// destruction of a pointer variable :
//
void edl_unset_pvar(const edlstring varname) 
{ 
  if (!edl_must_execute()) return;
  char *aString = (char *)GlobalInter->GetVariable(varname.str).GetValue();
  
  GlobalInter->RemoveVariable(aString);
}

// assign or creation of a variable :
//     if the variable doesnt exist we create it , otherwise we change its value...
//
void edl_set_var(const edlstring varname, const edlstring value) 
{ 
  if (!edl_must_execute()) return;

  GlobalInter->AddVariable(varname.str,value.str);
}

// assign or creation of a variable with an other variable:
//     if the variable doesnt exist we create it , otherwise we change its value...
//
void edl_set_varvar(const edlstring varname, const edlstring value) 
{ 
  if (!edl_must_execute()) return;

  // we need to erase the quote
  //
  char *aString = (char *)GlobalInter->GetVariable(value.str).GetValue();

  GlobalInter->AddVariable(varname.str,aString);
}

// assign or creation of a variable with a pointer:
//     if the variable doesnt exist we create it , otherwise we change its value...
//
void edl_set_varevalvar(const edlstring varname, const edlstring value) 
{ 
  if (!edl_must_execute()) return;

  // we need to erase the quote
  //
  char *aVarPointer = (char *)GlobalInter->GetVariable(value.str).GetValue();
  char *aString = (char *)GlobalInter->GetVariable(aVarPointer).GetValue();
  GlobalInter->AddVariable(varname.str,aString);
}

// assign or creation of a variable :
//     if the variable doesnt exist we create it , otherwise we change its value...
//
void edl_set_pvar(const edlstring varname, const edlstring value) 
{ 
  if (!edl_must_execute()) return;
  char *aString = (char *)GlobalInter->GetVariable(varname.str).GetValue();
  
  GlobalInter->AddVariable(aString,value.str);
}

// assign or creation of a variable with an other variable:
//     if the variable doesnt exist we create it , otherwise we change its value...
//
void edl_set_pvarvar(const edlstring varname, const edlstring value) 
{ 
  if (!edl_must_execute()) return;

  // we need to erase the quote
  //
  char *aString = (char *)GlobalInter->GetVariable(value.str).GetValue();
  char *aString2 =(char *) GlobalInter->GetVariable(varname.str).GetValue();

  GlobalInter->AddVariable(aString2,aString);
}

// assign or creation of a variable with a pointer:
//     if the variable doesnt exist we create it , otherwise we change its value...
//
void edl_set_pvarevalvar(const edlstring varname, const edlstring value) 
{ 
  if (!edl_must_execute()) return;

  // we need to erase the quote
  //
  char *aVarPointer = (char *)GlobalInter->GetVariable(value.str).GetValue();
  char *aString = (char *)GlobalInter->GetVariable(aVarPointer).GetValue();
  char *aString2 = (char *)GlobalInter->GetVariable(varname.str).GetValue();

  GlobalInter->AddVariable(aString2,aString);
}


// evaluation of an expression like <var ope string>
//   rule : (VAR logoperator STR)
//
void edl_test_condition(const edlstring varname, int ope, const edlstring value)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&) varname.str);
    if (value.str) Standard::Free((void*&)value.str);
    return;
  }

  char             *aString1 = (char *)GlobalInter->GetVariable(varname.str).GetValue(),
                   *aString2 = value.str;
  
  Standard_Integer  aResult = strcmp(aString1,aString2);
  Standard_Boolean  aMember;
  
  switch (ope) {
    case EQ: if (aResult == 0) {
                aMember = Standard_True;
             }
             else {
                aMember = Standard_False; 
             }
             break;
    case NEQ: if (aResult != 0) {
	        aMember = Standard_True;
	      }
	      else {
	        aMember = Standard_False; 
	      }
              break;
    default:
      EDLerror((char*)"wrong logical operator...",(char*)"");
      exit(EDL_SYNTAXERROR);
   }
  
  // we add the evaluation of the expression to the stack
  //
  GlobalInter->AddExpressionMember(aMember);
  if (varname.str) Standard::Free((void*&)varname.str);
  if (value.str)   Standard::Free((void*&)value.str);
}

// evaluation of a complex expression
//   rule : (condition [|| &&] expr_operator condition)
//
void edl_eval_local_condition(int ope)
{
  if (!edl_must_execute()) return;

  Standard_Boolean Left,Right,Result;

  Left  = GlobalInter->GetExpressionMember();
  Right = GlobalInter->GetExpressionMember();

  switch (ope) {
  case LOGAND: Result = Left && Right;
	       break;
  case LOGOR:  Result = Left || Right;
	       break;
  default:     EDLerror((char*)"wrong logical operator...",(char*)" ");
	       exit(EDL_SYNTAXERROR);
  }

  // we add the result of the evaluation to the stack
  //
  GlobalInter->AddExpressionMember(Result);
}

// take the results from previous expressions evaluation
// from stack and build a final result
//    rule : conditions: condition        
//
void edl_eval_condition()
{
  if (!edl_must_execute()) {
    GlobalInter->AddExecutionStatus(Standard_False);
  }
  else {
    Standard_Boolean Result = GlobalInter->GetExpressionMember();

    // add a new level of execution
    //
    GlobalInter->AddExecutionStatus(Result);
  }
}

void edl_isvardefined(const edlstring varname) 
{
  if (!edl_must_execute()) {
    GlobalInter->AddExecutionStatus(Standard_False);
  }
  else {
    Standard_Boolean Result = GlobalInter->IsDefined(varname.str);

    // add a new level of execution
    //
    GlobalInter->AddExecutionStatus(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_isvardefinedm(const edlstring varname) 
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }
  else {
    Standard_Boolean Result = GlobalInter->IsDefined(varname.str);

    // add a new member of expression
    //
    GlobalInter->AddExpressionMember(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_fileexist(const edlstring varname) 
{
  if (!edl_must_execute()) {
    GlobalInter->AddExecutionStatus(Standard_False);
  }
  else {
    Standard_Boolean Result = GlobalInter->IsFile(varname.str);

    // add a new level of execution
    //
    GlobalInter->AddExecutionStatus(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_fileexistm(const edlstring varname) 
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }
  else {
    Standard_Boolean Result = GlobalInter->IsFile(varname.str);

    // add a new level of execution
    //
    GlobalInter->AddExpressionMember(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_fileexist_var(const edlstring varname) 
{
  if (!edl_must_execute()) {
    GlobalInter->AddExecutionStatus(Standard_False);
  }
  else {
    EDL_Variable&    aVar  = GlobalInter->GetVariable(varname.str);
    Standard_Boolean Result = GlobalInter->IsFile(aVar.GetValue());

    // add a new level of execution
    //
    GlobalInter->AddExecutionStatus(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_fileexist_varm(const edlstring varname) 
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }
  else {
    EDL_Variable&    aVar  = GlobalInter->GetVariable(varname.str);
    Standard_Boolean Result = GlobalInter->IsFile(aVar.GetValue());

    // add a new level of execution
    //
    GlobalInter->AddExpressionMember(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_filenotexist(const edlstring varname) 
{
  if (!edl_must_execute()) {
    GlobalInter->AddExecutionStatus(Standard_False);
  }
  else {
    Standard_Boolean Result = !GlobalInter->IsFile(varname.str);

    // add a new level of execution
    //
    GlobalInter->AddExecutionStatus(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_filenotexistm(const edlstring varname) 
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }
  else {
    Standard_Boolean Result = !GlobalInter->IsFile(varname.str);

    // add a new level of execution
    //
    GlobalInter->AddExpressionMember(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_filenotexist_var(const edlstring varname) 
{
  if (!edl_must_execute()) {
    GlobalInter->AddExecutionStatus(Standard_False);
  }
  else {
    EDL_Variable&    aVar  = GlobalInter->GetVariable(varname.str);
    Standard_Boolean Result = !GlobalInter->IsFile(aVar.GetValue());

    // add a new level of execution
    //
    GlobalInter->AddExecutionStatus(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_filenotexist_varm(const edlstring varname) 
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }
  else {
    EDL_Variable&    aVar  = GlobalInter->GetVariable(varname.str);
    Standard_Boolean Result = !GlobalInter->IsFile(aVar.GetValue());

    // add a new level of execution
    //
    GlobalInter->AddExpressionMember(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_isvarnotdefined(const edlstring varname) 
{
  if (!edl_must_execute()) {
    GlobalInter->AddExecutionStatus(Standard_False);
  }
  else {
    Standard_Boolean Result = !GlobalInter->IsDefined(varname.str);

    // add a new level of execution
    //
    GlobalInter->AddExecutionStatus(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_isvarnotdefinedm(const edlstring varname) 
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }
  else {
    Standard_Boolean Result = !GlobalInter->IsDefined(varname.str);

    // add a new level of execution
    //
    GlobalInter->AddExpressionMember(Result);
  }
  if (varname.str) Standard::Free((void*&)varname.str);
}

// remove one level of execution status
//   rule : ENDIF;
//
void edl_clear_execution_status()
{
  GlobalInter->RemoveExecutionStatus();
}

// clear the print list
//
void edl_clear_printlist()
{
  GlobalInter->SetPrintList(NULL);
}

// c++ like cout
//
void edl_cout()
{
  if (!edl_must_execute()) return;

  cout << GlobalInter->GetPrintList() << endl;
}

// @string function
//
void edl_create_string_var(const edlstring varname)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }
  const TCollection_AsciiString& asciistr = GlobalInter->GetPrintList();
  edlstring astr;
  astr.str    = (char *)asciistr.ToCString();
  astr.length = asciistr.Length();
  edl_set_var(varname,astr);
  if (varname.str) Standard::Free((void*&)varname.str);
}

// add the value of a variable to the print list
//
void edl_printlist_add_var(const edlstring varname)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }

  // we need to erase the quote
  //
  char* aString = (char *)GlobalInter->GetVariable(varname.str).GetValue();
    
  GlobalInter->GetPrintList().AssignCat(aString);
  if (varname.str) Standard::Free((void*&)varname.str);
}

// add the value of a variable to the print list
//   we dont free variable memory because it s used after
void edl_printlist_addps_var(const edlstring varname)
{
  if (!edl_must_execute()) {
    return;
  }

  // we need to erase the quote
  //
  char *aString = (char *)GlobalInter->GetVariable(varname.str).GetValue();
    
  GlobalInter->GetPrintList().AssignCat(aString);
}

// add a string to the print list
//
void edl_printlist_add_str(const edlstring str)
{
  if (!edl_must_execute()) {
    if (str.str) Standard::Free((void *&)str.str);
    return;
  }

  GlobalInter->GetPrintList().AssignCat(str.str);
  if (str.str) Standard::Free((void *&)str.str);
}

// create a new template var
//
void edl_create_template(const edlstring varname)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }

  GlobalInter->AddTemplate(varname.str);
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_set_template(const edlstring varname)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }

  GlobalInter->AddToTemplate(varname.str);
  if (varname.str) Standard::Free((void*&)varname.str);
}

void edl_clear_template(const edlstring varname)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }

  GlobalInter->ClearTemplate(varname.str);
  if (varname.str) Standard::Free((void*&)varname.str);
}

// add a line to a template in construction
//
void edl_add_to_template(const edlstring line)
{
  if (!edl_must_execute()) {
    if (line.str) Standard::Free((void*&)line.str);
    return;
  }

  // we remove the $
  //
  GlobalInter->GetTemplate(GlobalInter->GetCurrentTemplate().ToCString()).AddLine(&(line.str[1]));
  if (line.str) Standard::Free((void*&)line.str);
}

// close a template
//
void edl_end_template()
{
  if (!edl_must_execute()) return;
  
  GlobalInter->SetCurrentTemplate(NULL);
}

// begin to eval a template
//
void edl_apply_template(const edlstring tempname)
{
  if (!edl_must_execute()) {
    if (tempname.str) Standard::Free((void*&)tempname.str);
    return;
  }

  GlobalInter->SetCurrentTemplate(tempname.str);
  
  GlobalInter->ClearVariableList();
 
 
  // to check if the template is defined
  //
  EDL_Template& atemp = GlobalInter->GetTemplate(tempname.str);
  Handle(TColStd_HSequenceOfHAsciiString) listvar = atemp.GetVariableList();

  for (Standard_Integer i = 1; i <= listvar->Length(); i++) {
    GlobalInter->AddToVariableList(listvar->Value(i)->ToCString());
  }
       
  if (tempname.str) Standard::Free((void*&)tempname.str);
}

// add the variables used in the template
//
void edl_add_to_varlist(const edlstring varname)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }

  Handle(TCollection_HAsciiString) aVarName = new TCollection_HAsciiString(varname.str);

  GlobalInter->GetTemplate(GlobalInter->GetCurrentTemplate().ToCString()).AddToVariableList(aVarName);

  if (varname.str) Standard::Free((void*&)varname.str);
}

// evaluation of the template
//
void edl_end_apply(const edlstring varname)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }

  GlobalInter->EvalTemplate(GlobalInter->GetCurrentTemplate().ToCString(),varname.str);
  GlobalInter->ClearVariableList();

  if (varname.str) Standard::Free((void*&)varname.str);
}

// load and open a shared library
//
void edl_open_library(const edlstring library)
{
  if (!edl_must_execute()) {
    if (library.str) Standard::Free((void*&)library.str);
    return;
  }

  GlobalInter->AddLibrary(library.str);
  if (library.str) Standard::Free((void*&)library.str);
}

// close a shared library
//
void edl_close_library(const edlstring library)
{
  if (!edl_must_execute()) {
    if (library.str) Standard::Free((void*&)library.str);
    return;
  }

  GlobalInter->RemoveLibrary(library.str);
  if (library.str) Standard::Free((void*&)library.str);
}

// add a variable to arguments list
//
void edl_arglist_add_var(const edlstring varname)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }

  GlobalInter->AddToArgList(varname.str);
  if (varname.str) Standard::Free((void*&)varname.str);
}

// add a string to arguments list
//  note : the temp variable '.' is used
//         but not inserted in the symbols table
//
void edl_arglist_add_str(const edlstring string)
{
  if (!edl_must_execute()) {
    if (string.str) Standard::Free((void*&)string.str);
    return;
  }

  GlobalInter->AddToArgList(".",string.str);
  if (string.str) Standard::Free((void*&)string.str);
}

// call a function in a library opened before
//
void edl_call_procedure_library(const edlstring libname, const edlstring funcname)
{
  if (!edl_must_execute()) {
    if (libname.str) Standard::Free((void*&)libname.str);

    if (funcname.str) Standard::Free((void*&)funcname.str);
    return;
  }

  EDL_Error aResult = GlobalInter->CallFunction(libname.str,funcname.str,0L);

  if (libname.str) Standard::Free((void*&)libname.str);
  if (funcname.str) Standard::Free((void*&)funcname.str);

  if (aResult != EDL_NORMAL) {
    Standard_NoSuchObject::Raise();
  }
}

// call a function in a library opened before
//
void edl_call_function_library(const edlstring libname, const edlstring funcname, const edlstring resname)
{
  if (!edl_must_execute()) {
    if (libname.str) Standard::Free((void*&)libname.str);
    if (funcname.str) Standard::Free((void*&)funcname.str);
    if (resname.str) Standard::Free((void*&)resname.str);
    return;
  }

  EDL_Error aResult = GlobalInter->CallFunction(libname.str,funcname.str,resname.str);

  if (libname.str) Standard::Free((void*&)libname.str);
  if (funcname.str) Standard::Free((void*&)funcname.str);
  if (resname.str) Standard::Free((void*&)resname.str);

  if (aResult != EDL_NORMAL) {
    Standard_NoSuchObject::Raise();
  }
}

// open a file
//
void edl_open_file(const edlstring varname, const edlstring filename)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    if (filename.str) Standard::Free((void*&)filename.str);
    return;
  }

  EDL_Error aResult = GlobalInter->AddFile(varname.str,filename.str);

  if (varname.str) Standard::Free((void*&)varname.str);
  if (filename.str) Standard::Free((void*&)filename.str);

  if (aResult != EDL_NORMAL) {
    Standard_NoSuchObject::Raise();
  }
}

// write in a file
//
void edl_write_file(const edlstring varname, const edlstring buffername)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    if (buffername.str) Standard::Free((void*&)buffername.str);
    return;
  }

  EDL_File&     aFile = GlobalInter->GetFile(varname.str);
  EDL_Variable& aVar  = GlobalInter->GetVariable(buffername.str);

  aFile.Write(aVar.GetValue());

  if (varname.str) Standard::Free((void*&)varname.str);
  if (buffername.str) Standard::Free((void*&)buffername.str);
}

// close a file
//
void edl_close_file(const edlstring varname)
{
  if (!edl_must_execute()) {
    if (varname.str) Standard::Free((void*&)varname.str);
    return;
  }

  EDL_File& aFile = GlobalInter->GetFile(varname.str);
  
  aFile.Close();
  GlobalInter->RemoveFile(varname.str);
  if (varname.str) Standard::Free((void*&)varname.str);
}

// include and process a file
//   rule : uses <string> ;

#ifndef WNT
  extern FILE *FileDesc[];
  extern int   LineStack[];
  extern int   numFileDesc;
  extern FILE *EDLin;
#else
  extern "C" FILE *FileDesc[];
  extern "C" int   LineStack[];
  extern "C" int   numFileDesc;
  extern "C" FILE *EDLin;
#endif  // WNT

void edl_uses_var(const edlstring var) 
{
  if (edl_must_execute()) {
    EDL_Variable&           aVar  = GlobalInter->GetVariable(var.str);
    TCollection_AsciiString anAscName = aVar.GetValue();

    edlstring fname;
    fname.str = (char *)  Standard::Allocate(anAscName.Length() + 1);
    memcpy(fname.str, anAscName.ToCString(), anAscName.Length()+1);
    fname.length = anAscName.Length();
    edl_uses(fname);
  }

  if (var.str) Standard::Free((void*&)var.str);
}

void edl_uses(const edlstring filename) 
{
  Standard_Boolean IsFound  = Standard_False;
  Standard_Integer DirCount = 1;

  Handle(TColStd_HSequenceOfAsciiString) IncludeDirectory = GlobalInter->GetIncludeDirectory();

  // from lex
  //
 
  if (edl_must_execute()) {
    numFileDesc++;
    
    if (numFileDesc > 9) {
      EDL::PrintError(EDL_TOOMANYINCLUDELEVEL," ");
      Standard_NoSuchObject::Raise();
    }

    FileDesc[numFileDesc]  = EDLin;
    LineStack[numFileDesc] = EDLlineno;

    memcpy(&(FileName[numFileDesc][0]),EDL_CurrentFile.ToCString(), EDL_CurrentFile.Length()+1);
    memcpy(&(FileName[10][0]), filename.str, filename.length+1);

    edlstring _currentFile;
    _currentFile.str = &(FileName[10][0]);
    _currentFile.length = filename.length;
    EDL_SetCurrentFile(_currentFile);

    EDLlineno = 1;
    DirCount  = 1;
    EDLin = 0;
    while (!IsFound && DirCount <= IncludeDirectory->Length()) {
      static char tmpName[1024];

      const TCollection_AsciiString& adir = IncludeDirectory->Value(DirCount);
      memcpy(tmpName, adir.ToCString(), adir.Length());
      tmpName[adir.Length()] = '/';
      strcpy(&(tmpName[adir.Length()+1]),filename.str); 
      
#ifndef WNT
      if( !access(tmpName, F_OK) ) 
#else
      if ( GetFileAttributes(tmpName) != 0xFFFFFFFF ) 
#endif
	{
	  EDLin = fopen(tmpName,"r");

	  if (EDLin != 0L) {
	    IsFound = Standard_True;
	    EDL_SetFile();
	  }
	}
      DirCount++;
    }
    
    if (EDLin == NULL) {
      EDL::PrintError(EDL_FILENOTOPENED,filename.str);
      if (filename.str) Standard::Free((void*&)filename.str);
      EDLin = FileDesc[numFileDesc];
      EDLlineno = LineStack[numFileDesc];
      numFileDesc--;
      Standard_NoSuchObject::Raise();
    }
  }
  if (filename.str) Standard::Free((void*&)filename.str);
}

void edl_else_execution_status()
{
  Standard_Boolean currentstatus = GlobalInter->RemoveExecutionStatus();

  if (edl_must_execute()) {
    GlobalInter->AddExecutionStatus(!currentstatus);
  }
  else {
    GlobalInter->AddExecutionStatus(currentstatus);
  }
}

void EDL_SetCurrentFile(const edlstring fname)
{
  EDL_CurrentFile = (Standard_CString)fname.str;
}

edlstring edl_strdup(const char* buf, const int length)
{
  edlstring ret;
  ret.str = (char *) Standard::Allocate(length+1);
  memcpy(ret.str, buf,length+1);
  ret.length = length;
  return ret;
}

edlstring edl_string(const char* buf, const int length)
{
  edlstring ret;
  ret.str = (char *)  Standard::Allocate(length-1);
  memcpy(ret.str, &buf[1], length-1);
  ret.str[length - 2] = '\0';
  ret.length = length-2;
  return ret;
}

void edlstring_free(const edlstring buf)
{
  if(buf.str) Standard::Free((void*&)buf.str);
}

