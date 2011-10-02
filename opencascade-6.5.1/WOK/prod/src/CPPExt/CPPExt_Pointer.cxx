// CLE
//    
// 11/1995
//
#include <MS.hxx>

#include <EDL_API.hxx>

#include <MS_MetaSchema.hxx>
#include <MS_Pointer.hxx>
#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <CPPExt_Define.hxx>

// Extraction of a transient class (inst or std)
//
//void CPP_Pointer(const Handle(MS_MetaSchema)& aMeta,
void CPP_Pointer(const Handle(MS_MetaSchema)& ,
		 const Handle(EDL_API)& api,
		 const Handle(MS_Pointer)& aPointer,
		 const Handle(TColStd_HSequenceOfHAsciiString)& outfile)
{
  if (aPointer.IsNull()) return;

  Handle(TCollection_HAsciiString)        aFileName;

  // Hxx extraction
  //
  api->AddVariable(VClass,aPointer->FullName()->ToCString());
  api->AddVariable(VInherits,aPointer->Type()->ToCString());

  api->Apply(VoutClass,"PointerHXX");

  aFileName = new TCollection_HAsciiString(api->GetVariableValue(VFullPath));
  
  aFileName->AssignCat(aPointer->FullName());
  aFileName->AssignCat(".hxx");

  CPP_WriteFile(api,aFileName,VoutClass); 
  outfile->Append(aFileName);
}

