// CLE
//    
// 11/1995
//
#include <MS.hxx>

#include <EDL_API.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_Enum.hxx>
#include <MS_Package.hxx>
#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <CPPExt_Define.hxx>

// Extraction of a transient class (inst or std)
//
void CPP_Enum(const Handle(MS_MetaSchema)& ,
			const Handle(EDL_API)& api,
			const Handle(MS_Enum)& anEnum,
			const Handle(TColStd_HSequenceOfHAsciiString)& outfile)
{
  if (anEnum.IsNull()) return;

  Handle(TColStd_HSequenceOfHAsciiString) EnumVal    = anEnum->Enums();
  Handle(TCollection_HAsciiString)        result,
                                          aFileName;
  Standard_Integer                        i;

  result = new TCollection_HAsciiString(EnumVal->Length());
  
  api->AddVariable(VNb,result->ToCString());
  api->AddVariable(VClass,anEnum->FullName()->ToCString());
  api->AddVariable("%EnumComment",anEnum->Comment()->ToCString());
  
  result->Clear();

  i = 1;

  for (; i < EnumVal->Length(); i++) {
    result->AssignCat(EnumVal->Value(i));
    result->AssignCat(",\n");
  }

  if (EnumVal->Length() > 0) {
    result->AssignCat(EnumVal->Value(i));
  }

  api->AddVariable(VValues,result->ToCString());
  
  api->Apply(VoutClass,"EnumHXX");
  
  aFileName = new TCollection_HAsciiString(api->GetVariableValue(VFullPath));
  
  aFileName->AssignCat(anEnum->FullName());
  aFileName->AssignCat(".hxx");

  CPP_WriteFile(api,aFileName,VoutClass); 
  outfile->Append(aFileName);
/*
  result->Clear();

  i = 1;

  for (; i < EnumVal->Length(); i++) {
    result->AssignCat("\"");
    result->AssignCat(EnumVal->Value(i));
    result->AssignCat("\",\n");
  }

  if (EnumVal->Length() > 0) {
    result->AssignCat("\"");
    result->AssignCat(EnumVal->Value(i));
    result->AssignCat("\"");
  }

  api->AddVariable(VValues,result->ToCString());
  
  api->Apply(VoutClass,"EnumCXX");

  aFileName = new TCollection_HAsciiString(api->GetVariableValue(VFullPath));
  
  aFileName->AssignCat(anEnum->FullName());
  aFileName->AssignCat("_0.cxx");

  CPP_WriteFile(api,aFileName,VoutClass); 
  outfile->Append(aFileName);
*/
}



