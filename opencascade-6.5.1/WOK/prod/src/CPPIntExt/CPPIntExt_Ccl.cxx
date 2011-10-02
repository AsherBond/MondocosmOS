// ADN
//    
// 12/1995
//

#include <MS_MetaSchema.hxx>
#include <MS_Engine.hxx>
#include <MS_Class.hxx>
#include <MS_Package.hxx>
#include <MS_Enum.hxx>
#include <MS_MapOfType.hxx>
#include <MS_DataMapIteratorOfMapOfType.hxx>
#include <MS_DataMapIteratorOfMapOfGlobalEntity.hxx>
#include <MS_MapOfGlobalEntity.hxx>
#include <EDL_API.hxx>
#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>


//void CPPIntExt_ProcessCcl(const Handle(MS_MetaSchema)& aMeta,
void CPPIntExt_ProcessCcl(const Handle(MS_MetaSchema)& ,
//			  const Handle(MS_Engine)& srcEngine,
			  const Handle(MS_Engine)& ,
			  const Handle(EDL_API)& api,
			  MS_MapOfType& maptype,
			  MS_MapOfGlobalEntity& mappack)
{
  // Writing Classes declarations

  MS_DataMapIteratorOfMapOfType ittype1(maptype);

  while (ittype1.More()) {
    if (ittype1.Value()->IsKind(STANDARD_TYPE(MS_Class))) {
      api->AddVariable("%CLName",ittype1.Key()->ToCString());
      api->Apply("%TextDefClass",
		 "EngineLLDefClass");
      api->WriteFile("Enginefilell",
		     "%TextDefClass");
    }
    ittype1.Next();
  }
  // Writing Packages declarations

  MS_DataMapIteratorOfMapOfGlobalEntity itpack(mappack);

  while (itpack.More()) {
    api->AddVariable("%PKName",itpack.Key()->ToCString());
    api->Apply("%TextDefPackage",
	       "EngineLLDefPackage");
    api->WriteFile("Enginefilell",
		   "%TextDefPackage");
    itpack.Next();
  }

  // Writing Enums

  MS_DataMapIteratorOfMapOfType ittype2(maptype);
  

  while (ittype2.More()) {
    if (ittype2.Value()->IsKind(STANDARD_TYPE(MS_Enum))) {
      Handle(MS_Enum) theenum = Handle(MS_Enum)::DownCast(ittype2.Value());
      Handle(TColStd_HSequenceOfHAsciiString) seqval = theenum->Enums();
      for (Standard_Integer i=1; i<= seqval->Length(); i++) {
	api->AddVariable("%EnumMember",
			 seqval->Value(i)->ToCString());
	api->AddVariable("%EnumValue",i-1);
	api->Apply("%TextDefEnum",
		   "EngineLLSetEnum");
	api->WriteFile("Enginefilell",
		       "%TextDefEnum");
      }
    }
    ittype2.Next();
  }
}



