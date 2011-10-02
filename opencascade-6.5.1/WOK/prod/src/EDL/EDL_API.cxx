#include <EDL_API.ixx>

#include <EDL_Template.hxx>
#include <EDL_Variable.hxx>
#include <EDL_File.hxx>
#include <EDL_ParameterMode.hxx>
#include <TColStd_HSequenceOfAsciiString.hxx>
#include <TCollection_AsciiString.hxx>
#include <Standard_ErrorHandler.hxx>
#include <Standard_NullObject.hxx>

#include <stdio.h>

EDL_API::EDL_API()
{
  myInter = new EDL_Interpretor;
}

EDL_Error EDL_API::Openlib(const Standard_CString aName) const 
{
  EDL_Error result;

  result = myInter->AddLibrary(aName);

  return result;
}

EDL_Error EDL_API::Call(const Standard_CString aLibName, 
			const Standard_CString aFunction, 
			const Handle(TColStd_HSequenceOfHAsciiString)& anArgList) const 
{
  EDL_Error        result;
  Standard_Integer i;
  
  for (i = 1; i <= anArgList->Length(); i++) {
    if (anArgList->Value(i)->Value(1) == '%') {
      myInter->AddToArgList(anArgList->Value(i)->ToCString());
    }
    else {
      myInter->AddToArgList(".",anArgList->Value(i)->ToCString());
    }
  }

  result = myInter->CallFunction(aLibName,aFunction,0L);

  return result;
}

void EDL_API::Closelib(const Standard_CString aName) const 
{
  myInter->RemoveLibrary(aName);
}

void EDL_API::AddTemplate(const Standard_CString aName, 
			  const Handle(TColStd_HSequenceOfHAsciiString)& aDefinition,
			  const Handle(TColStd_HSequenceOfHAsciiString)& aVarList) const 
{
  Standard_Integer i;
  
  myInter->AddTemplate(aName);

  for (i = 1; i <= aDefinition->Length(); i++) {
    myInter->GetTemplate(aName).AddLine(aDefinition->Value(i)->ToCString());
  }

  myInter->GetTemplate(aName).VariableList(aVarList);
}

void EDL_API::Apply(const Standard_CString aResult, 
		    const Standard_CString aName) const 
{
  Standard_Integer                        i;
  EDL_Template&                           atemp = myInter->GetTemplate(aName);
  Handle(TColStd_HSequenceOfHAsciiString) theVariable = atemp.GetVariableList();

  for (i = 1; i <= theVariable->Length(); i++) {
    myInter->AddToVariableList(theVariable->Value(i)->ToCString());
  }

  myInter->EvalTemplate(aName,aResult);
  myInter->ClearVariableList();
}

void EDL_API::RemoveTemplate(const Standard_CString aName) const 
{
  myInter->RemoveTemplate(aName);
}

EDL_Template& EDL_API::GetTemplate(const Standard_CString aName) const 
{
  if (aName != NULL) {
    return myInter->GetTemplate(aName);
  }
  else {
    Standard_NullObject::Raise("EDL_API::GetTemplate - aName is NULL");
  }
    return myInter->GetTemplate(aName);

}

void EDL_API::AddVariable(const Standard_CString aName, 
			  const Standard_CString aValue) const 
{
  myInter->AddVariable(aName,(char*)(aValue?aValue:""));
}

void EDL_API::AddVariable(const Standard_CString aName, 
			  const Standard_Integer aValue) const 
{
  Handle(TCollection_HAsciiString) str = new TCollection_HAsciiString(aValue);

  myInter->AddVariable(aName,str->ToCString());
}

void EDL_API::AddVariable(const Standard_CString aName, 
			  const Standard_Real aValue) const 
{
  Handle(TCollection_HAsciiString) str = new TCollection_HAsciiString(aValue);

  myInter->AddVariable(aName,str->ToCString());
}

void EDL_API::AddVariable(const Standard_CString aName, 
			  const Standard_Character aValue) const 
{
  char str[2];

  str[0] = (char)aValue;
  str[1] = '\0';

  myInter->AddVariable(aName,str);
}

EDL_Variable& EDL_API::GetVariable(const Standard_CString aName) const 
{
  if (aName != NULL) {
    return myInter->GetVariable(aName);
  }
  else {
    Standard_NullObject::Raise("EDL_API::GetVariable - aName is NULL");
  }
  return myInter->GetVariable(aName);
}

Handle(TCollection_HAsciiString) EDL_API::GetVariableValue(const Standard_CString aName) const 
{
  Handle(TCollection_HAsciiString) result;
  
  result = new TCollection_HAsciiString(myInter->GetVariable(aName).GetValue());

  return result;
}

void EDL_API::RemoveVariable(const Standard_CString aName) const 
{
  myInter->RemoveVariable(aName);
}

Standard_Boolean EDL_API::IsDefined(const Standard_CString aName) const 
{
  return myInter->IsDefined(aName);
}

EDL_Error EDL_API::OpenFile(const Standard_CString aName, 
			    const Standard_CString aPath) const 
{
  EDL_Error         result;
  Standard_CString  thePath;
  EDL_ParameterMode OldMode = myInter->GetParameterType();

  if (aPath[0] == '%') {
    thePath = myInter->GetVariable(aPath).GetValue();
  }
  else {
    thePath = aPath;
  }

  myInter->SetParameterType(EDL_STRING);
  result = myInter->AddFile(aName,thePath);
  myInter->SetParameterType(OldMode);

  return result;
}

void EDL_API::WriteFile(const Standard_CString aName, 
			const Standard_CString aVar) const 
{
  myInter->GetFile(aName).Write(myInter->GetVariable(aVar).GetValue());
}

void EDL_API::WriteFileConst(const Standard_CString aName, 
			     const Standard_CString aValue) const 
{
  if (aValue != 0L) {
    myInter->GetFile(aName).Write(aValue);
  }
}

void EDL_API::WriteFileConst(const Standard_CString aName, 
			     const Standard_Character aValue) const 
{
  char str[2];

  str[0] = (char)aValue;
  str[1] = '\0';

  myInter->GetFile(aName).Write(str);
}

void EDL_API::WriteFileConst(const Standard_CString aName, 
			     const Standard_Integer aValue) const 
{
  Handle(TCollection_HAsciiString) str = new TCollection_HAsciiString(aValue);

  myInter->GetFile(aName).Write(str->ToCString());
}

void EDL_API::WriteFileConst(const Standard_CString aName, 
			     const Standard_Real aValue) const 
{
  Handle(TCollection_HAsciiString) str = new TCollection_HAsciiString(aValue);

  myInter->GetFile(aName).Write(str->ToCString());
}

void EDL_API::CloseFile(const Standard_CString aName) const 
{
  myInter->GetFile(aName).Close();
  myInter->RemoveFile(aName);
}

void EDL_API::AddIncludeDirectory(const Standard_CString aDirectory) const 
{
  myInter->AddIncludeDirectory(aDirectory);
}

void EDL_API::RemoveIncludeDirectory(const Standard_CString aDirectory) const 
{
  Handle(TColStd_HSequenceOfAsciiString)  aSeq = myInter->GetIncludeDirectory();
  Standard_Integer                        i;
  Standard_Boolean                        IsFound = Standard_False;

  for (i = 1; i <= aSeq->Length() && !IsFound; i++) {
    if (aSeq->Value(i).IsEqual(aDirectory)) {
      IsFound = Standard_True;
      aSeq->Remove(i);
    }
  }
}

Handle(TColStd_HSequenceOfAsciiString) EDL_API::GetIncludeDirectory() const
{
  return myInter->GetIncludeDirectory();
}

EDL_DataMapIteratorOfMapOfVariable EDL_API::GetVariableIterator() const
{
  return myInter->GetVariableIterator();
}

EDL_DataMapIteratorOfMapOfTemplate EDL_API::GetTemplateIterator() const
{
  return myInter->GetTemplateIterator();
}

void EDL_API::ClearVariables() const 
{
  myInter->ClearSymbolTable();
}

void EDL_API::ClearTemplates() const 
{
  myInter->ClearTemplateTable();
}

void EDL_API::ClearIncludes() const 
{
  myInter->GetIncludeDirectory()->Clear();
}

EDL_Error EDL_API::Execute(const Standard_CString aFileName) const 
{
  EDL_Error result;

  result = myInter->Parse(aFileName);

  return result;
}

