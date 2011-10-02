// ADN
//    
// 12/1995
//

#include <MS_MetaSchema.hxx>
#include <MS_Engine.hxx>
#include <MS_Class.hxx>
#include <MS_Alias.hxx>
#include <MS_Enum.hxx>
#include <MS_MapOfType.hxx>
#include <MS_DataMapIteratorOfMapOfType.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TCollection_HAsciiString.hxx>
#include <EDL_API.hxx>

#define NBDIGITINH 10

#include <stdio.h>

Standard_Boolean CPPIntExt_IsRef(const Handle(MS_Type)&,
				 const Handle(MS_MetaSchema)&);

void 
CPPIntExt_ProcessEngineInit(const Handle(MS_MetaSchema)& aMeta,
//			    const Handle(MS_Engine)& srcEngine,
			    const Handle(MS_Engine)& ,
			    const Handle(EDL_API)& api,
			    MS_MapOfType& maptype)
{
  api->Apply("%TextHeader",
	     "EngineInitHeader");
  api->WriteFile("Enginefileinit",
		 "%TextHeader");

  // Remplissage map des types par valeurs (et hierarchie)

  Standard_Integer i;

  MS_MapOfType mapvaltype;
  MS_MapOfType mapenum;
  MS_DataMapIteratorOfMapOfType ittyp1(maptype);
  Standard_Boolean treated;
  Handle(MS_Type) curtype;
  while (ittyp1.More()) {
    treated = Standard_False;
    curtype = ittyp1.Value();
    if (curtype->IsKind(STANDARD_TYPE(MS_Alias))) {
      Handle(TCollection_HAsciiString) deepname = Handle(MS_Alias)::DownCast(curtype)->DeepType();
      curtype = aMeta->GetType(deepname);
    }
    if (curtype->IsKind(STANDARD_TYPE(MS_Class))) {
      if (!Handle(MS_Class)::DownCast(curtype)->IsStorable()) {
	if (!CPPIntExt_IsRef(curtype,aMeta)) {
	  treated = Standard_True;
	  Handle(MS_Class) thecl = Handle(MS_Class)::DownCast(curtype);
	  mapvaltype.Bind(thecl->FullName(),thecl);
	  Handle(TColStd_HSequenceOfHAsciiString) theinh = thecl->GetFullInheritsNames();
	  for (i=1; i<= theinh->Length(); i++) {
	    mapvaltype.Bind(theinh->Value(i),
			    aMeta->GetType(theinh->Value(i)));
	  }
	}
      }
    }
    else if (curtype->IsKind(STANDARD_TYPE(MS_Enum))) {
      if (!mapenum.IsBound(curtype->FullName())) {
	api->AddVariable("%CLName",curtype->FullName()->ToCString());
	api->Apply("%TextDecl",
		   "EngineInitEnumType");
	api->WriteFile("Enginefileinit",
		       "%TextDecl");
	treated = Standard_True;
	mapenum.Bind(curtype->FullName(),curtype);
      }
    }
    if (!treated) {
	api->AddVariable("%CLName",curtype->FullName()->ToCString());
	api->Apply("%TextDecl",
		   "EngineInitGlobType");
	api->WriteFile("Enginefileinit",
		       "%TextDecl");
    }
    ittyp1.Next();
  }

  char numinh[NBDIGITINH];

  MS_DataMapIteratorOfMapOfType ittyp2(mapvaltype);
  
  while (ittyp2.More()) {
    Handle(MS_Class) thecl = Handle(MS_Class)::DownCast(ittyp2.Value());
    Handle(TColStd_HSequenceOfHAsciiString) seqpar = thecl->GetFullInheritsNames();
    Handle(TCollection_HAsciiString) inhdeclar = new TCollection_HAsciiString();
    Handle(TCollection_HAsciiString) inhlist = new TCollection_HAsciiString();
    for (i = 1; i<= seqpar->Length(); i++) {
      api->AddVariable("%NumInh",i);
      api->AddVariable("%InhName",seqpar->Value(i)->ToCString());
      api->Apply("%TextInh",
		 "EngineInitDeclInhType");
      inhdeclar->AssignCat(api->GetVariableValue("%TextInh")->ToCString());
      inhlist->AssignCat("aType");
      sprintf(numinh,"%d",i);
      inhlist->AssignCat(numinh);
      inhlist->AssignCat((Standard_CString)",\n");
    }
    api->AddVariable("%CLName",thecl->FullName()->ToCString());
    api->AddVariable("%InhDeclar",inhdeclar->ToCString());
    api->AddVariable("%InhList",inhlist->ToCString());
    api->AddVariable("%NbInh",thecl->GetInheritsNames()->Length());
    api->Apply("%TextDecType",
	       "EngineInitDefType");
    api->WriteFile("Enginefileinit",
		   "%TextDecType");

    ittyp2.Next();
  }

  api->Apply("%TextInitFunc",
	     "EngineInitFunction");
  api->WriteFile("Enginefileinit",
		 "%TextInitFunc");
  MS_DataMapIteratorOfMapOfType ittyp3(maptype);
  while (ittyp3.More()) {
    if (!ittyp3.Value()->IsKind(STANDARD_TYPE(MS_Alias))) {
      api->AddVariable("%CLName",ittyp3.Key()->ToCString());
      api->Apply("%TextTypeCall",
		 "EngineInitTypeCall");
      api->WriteFile("Enginefileinit",
		     "%TextTypeCall");
    }
    ittyp3.Next();
  }

  api->Apply("%TextInitFunc",
	     "EngineInitBody");
  api->WriteFile("Enginefileinit",
		 "%TextInitFunc");

}

