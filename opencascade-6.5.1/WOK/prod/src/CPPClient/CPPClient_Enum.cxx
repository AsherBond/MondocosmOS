// CLE
//    
// 10/1996
//
#include <MS.hxx>

#include <EDL_API.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_Enum.hxx>
#include <MS_Package.hxx>
#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <CPPClient_Define.hxx>

// Extraction of a transient class (inst or std)
//
//void CPPClient_Enum(const Handle(MS_MetaSchema)& aMeta,
void CPPClient_Enum(const Handle(MS_MetaSchema)& ,
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
  
  //  api->AddVariable("%Nb",result->ToCString());
  api->AddVariable("%Class",anEnum->FullName()->ToCString());
  
  result->Clear();

  i = 1;

  for (; i < EnumVal->Length(); i++) {
    result->AssignCat(EnumVal->Value(i));
    result->AssignCat(",\n");
  }

  if (EnumVal->Length() > 0) {
    result->AssignCat(EnumVal->Value(i));
  }

  api->AddVariable("%Values",result->ToCString());
  
  api->Apply("%outClass","EnumHXX");
  
  aFileName = new TCollection_HAsciiString(api->GetVariableValue("%FullPath"));
  aFileName->AssignCat(CPPClient_InterfaceName);
  aFileName->AssignCat("_");
  aFileName->AssignCat(anEnum->FullName());
  aFileName->AssignCat(".hxx");

  CPPClient_WriteFile(api,aFileName,"%outClass"); 
  outfile->Append(aFileName);
}



