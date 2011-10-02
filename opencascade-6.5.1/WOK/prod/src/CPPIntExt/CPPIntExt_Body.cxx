// ADN
//    
// 11/1995
//

#include <stdio.h>


#include <MS_Interface.hxx>
#include <MS_Engine.hxx>
#include <MS_MapOfType.hxx>
#include <MS_MapOfGlobalEntity.hxx>
#include <MS_MapOfMethod.hxx>
#include <MS_DataMapIteratorOfMapOfGlobalEntity.hxx>
#include <MS_DataMapIteratorOfMapOfType.hxx>
#include <MS_Class.hxx>
#include <MS_Alias.hxx>
#include <MS_Enum.hxx>
#include <MS_MetaSchema.hxx>
#include <WOKTools_SortOfHAsciiString.hxx>
#include <WOKTools_Array1OfHAsciiString.hxx>
#include <WOKTools_CompareOfHAsciiString.hxx>
#include <EDL_API.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_SequenceOfInteger.hxx>
#include <TCollection_HAsciiString.hxx>

#define NBDIGITINH 10

Standard_Boolean CPPIntExt_IsRef(const Handle(MS_Type)&,
				 const Handle(MS_MetaSchema)&);


//void CPPIntExt_ProcessIncludes(const Handle(MS_Interface)& srcInterface,
void CPPIntExt_ProcessIncludes(const Handle(MS_Interface)& ,
			       const Handle(EDL_API)& api,
			       const MS_MapOfType& maptype,
			       const MS_MapOfGlobalEntity& mappack)
{
  Standard_Integer i,count;
  WOKTools_CompareOfHAsciiString cmptool;
  if (mappack.Extent() > 0) {
    MS_DataMapIteratorOfMapOfGlobalEntity itpack(mappack);
    WOKTools_Array1OfHAsciiString arpack(1,mappack.Extent());
    count =1;
    while (itpack.More()) {
      arpack(count) = itpack.Value()->Name();
      count++;
      itpack.Next();
    }
    WOKTools_SortOfHAsciiString::Sort(arpack,cmptool);
    Standard_Integer i;
    for (i=1; i<=arpack.Length(); i++) {
      api->AddVariable("%PKName",arpack(i)->ToCString());
      api->Apply("%TextPkInc",
		 "InterfPkInclude");
      api->WriteFile("Interfilecxx",
		     "%TextPkInc");
    }
  }
  if (maptype.Extent() > 0) {
    MS_DataMapIteratorOfMapOfType ittype(maptype);
    WOKTools_Array1OfHAsciiString artype(1,maptype.Extent());
    count =1;
    while (ittype.More()) {
      artype(count) = new TCollection_HAsciiString(ittype.Key());
      count++;
      ittype.Next();
    }
    WOKTools_SortOfHAsciiString::Sort(artype,cmptool);
    for (i=1; i<=artype.Length(); i++) {
      api->AddVariable("%CLName",artype(i)->ToCString());
      if (maptype(artype(i))->IsKind(STANDARD_TYPE(MS_Class))) {
	api->Apply("%TextCLInc",
		   "InterfClassInclude");
      }
      else {
	api->Apply("%TextCLInc",
		   "InterfNatClassInclude");
      }
      api->WriteFile("Interfilecxx",
		     "%TextCLInc");
    }
  }
  
}

//void CPPIntExt_ProcessHeader(const Handle(MS_Interface)& srcInterface,
void CPPIntExt_ProcessHeader(const Handle(MS_Interface)& ,
			     const Handle(EDL_API)& api)
{
  api->Apply("%TextHeader",
	     "InterfHeader");
  api->WriteFile("Interfilecxx",
		 "%TextHeader");
  
}


void CPPIntExt_ProcessTypes(const Handle(MS_MetaSchema)& aMeta,
//			    const Handle(MS_Interface)& srcInterface,
			    const Handle(MS_Interface)& ,
			    const Handle(EDL_API)& api,
			    const MS_MapOfType& maptype)
{
  Standard_Integer i;
  MS_MapOfType mapvaltype;
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
      api->AddVariable("%CLName",curtype->FullName()->ToCString());
      api->Apply("%TextDecl",
		 "InterfInitEnumType");
      api->WriteFile("Interfilecxx",
		     "%TextDecl");
      treated = Standard_True;
    }
    if (!treated) {
	api->AddVariable("%CLName",curtype->FullName()->ToCString());
	api->Apply("%TextDecl",
		   "InterfInitGlobType");
	api->WriteFile("Interfilecxx",
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
		 "InterfInitDeclInhType");
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
	       "InterfInitDefType");
    api->WriteFile("Interfilecxx",
		   "%TextDecType");

    ittyp2.Next();
  }

}  

void CPPIntExt_ProcessExec(const Handle(MS_Interface)& srcInterface,
			   const Handle(EDL_API)& api,
			   const MS_MapOfType& maptype)
{
  api->AddVariable("%IntName",srcInterface->Name()->ToCString());
  api->Apply("%TextExec",
	     "InterfExec");
  api->WriteFile("Interfilecxx",
		 "%TextExec");
  MS_DataMapIteratorOfMapOfType ittyp(maptype);
  while (ittyp.More()) {
    if (!ittyp.Value()->IsKind(STANDARD_TYPE(MS_Alias))) {
      api->AddVariable("%CLName",ittyp.Key()->ToCString());
      api->Apply("%TextTypeCall",
		 "InterfInitTypeCall");
      api->WriteFile("Interfilecxx",
		     "%TextTypeCall");
    }
    ittyp.Next();
  }

  api->Apply("%TextExec",
	     "InterfExecContinue");
  api->WriteFile("Interfilecxx",
		 "%TextExec");
  
  
  
}

//void CPPIntExt_ProcessBottom(const Handle(MS_Interface)& srcInterface,
void CPPIntExt_ProcessBottom(const Handle(MS_Interface)& ,
			     const Handle(EDL_API)& api)
{
  api->Apply("%TextBottom",
	     "InterfBottom");
  api->WriteFile("Interfilecxx",
		 "%TextBottom");
  
}

void CPPIntExt_ProcessMultiExec(const Handle(MS_Interface)& srcInterface,
				const Handle(EDL_API)& api,
				const TColStd_SequenceOfInteger& seqcase,
				const MS_MapOfType& maptype)
{
  api->AddVariable("%IntName",srcInterface->Name()->ToCString());
  api->Apply("%TextExec",
	     "InterfMultiExec");
  api->WriteFile("Interfilecxx",
		 "%TextExec");
  MS_DataMapIteratorOfMapOfType ittyp(maptype);
  while (ittyp.More()) {
    if (!ittyp.Value()->IsKind(STANDARD_TYPE(MS_Alias))) {
      api->AddVariable("%CLName",ittyp.Key()->ToCString());
      api->Apply("%TextTypeCall",
		 "InterfInitTypeCall");
      api->WriteFile("Interfilecxx",
		     "%TextTypeCall");
    }
    ittyp.Next();
  }

  api->Apply("%TextExec",
	     "InterfMultiExecContinue");
  api->WriteFile("Interfilecxx",
		 "%TextExec");
  
  for (Standard_Integer i=1; i<seqcase.Length(); i++) {
    api->AddVariable("%NumCaseFunc",i);
    api->AddVariable("%NumCaseFirst",seqcase(i));
    api->AddVariable("%NumCaseLast",seqcase(i+1));
    api->Apply("%TextCaseCall",
	       "InterfMultiCaseCall");
    api->WriteFile("Interfilecxx",
		   "%TextCaseCall");
    
  }


  api->AddVariable("%IntMaxCase",seqcase(seqcase.Length()));
  api->Apply("%TextBottom",
	     "InterfMultiBottom");
  api->WriteFile("Interfilecxx",
		 "%TextBottom");

}


void CPPIntExt_ProcessCxx(const Handle(MS_Engine)& srcEngine,
			  const Handle(EDL_API)& api,
			  const Handle(TColStd_HSequenceOfHAsciiString)& seqint)
{
  api->AddVariable("%NbInterf",seqint->Length());
  api->AddVariable("%EngineName",srcEngine->Name()->ToCString());
  api->Apply("%TextHeader",
	     "EngineCxxHeader");
  api->WriteFile("Enginefilecxx",
		 "%TextHeader");

  Standard_Integer i;
  for (i=1; i<= seqint->Length(); i++) {
    api->AddVariable("%NumInt",i);
    api->AddVariable("%IntName",seqint->Value(i)->ToCString());
    api->Apply("%TextLoad",
	       "EngineCxxLoadMet");
    api->WriteFile("Enginefilecxx",
		   "%TextLoad");
  }
  api->Apply("%TextBottom",
	     "EngineCxxBottom");
  api->WriteFile("Enginefilecxx",
		 "%TextBottom");
  

}
  
