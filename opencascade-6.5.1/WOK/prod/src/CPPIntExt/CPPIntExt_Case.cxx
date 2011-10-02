// ADN
//    
// 11/1995
//

#include <MS_Type.hxx>
#include <MS_Class.hxx>
#include <MS_Param.hxx>
#include <MS_Method.hxx>
#include <MS_InstMet.hxx>
#include <MS_ExternMet.hxx>
#include <MS_ClassMet.hxx>
#include <MS_Construc.hxx>
#include <MS_MetaSchema.hxx>
#include <MS_Interface.hxx>
#include <MS_NatType.hxx>
#include <MS_Enum.hxx>
#include <MS_Alias.hxx>
#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_HArray1OfParam.hxx>
#include <MS_DataMapIteratorOfMapOfMethod.hxx>
#include <EDL_API.hxx>
#include <TCollection_HAsciiString.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_SequenceOfInteger.hxx>
#include <WOKTools_Array1OfHAsciiString.hxx>
#include <WOKTools_CompareOfHAsciiString.hxx>
#include <WOKTools_SortOfHAsciiString.hxx>

#include <CPPIntExt_Predefined.hxx>

Standard_Boolean CPPIntExt_IsRef(const Handle(MS_Type)& atyp,
				 const Handle(MS_MetaSchema)& aMeta)
{
  Handle(MS_Class) thearg = Handle(MS_Class)::DownCast(atyp);
  if (!thearg.IsNull()) {
    return (thearg->IsTransient() || thearg->IsPersistent());
  }
  Handle(MS_Alias) theal = Handle(MS_Alias)::DownCast(atyp);
  if (!theal.IsNull()) {
    Handle(TCollection_HAsciiString) deept = theal->DeepType();
    return CPPIntExt_IsRef(aMeta->GetType(deept),aMeta);
  }
  return Standard_False;
}

Handle(TCollection_HAsciiString)
     CPPIntExt_BuildAnArg(const Handle(MS_Param)& aparam,
			  const Standard_Integer position,
			  const Handle(EDL_API)& api,
			  const Handle(MS_MetaSchema)& aMeta)
{
  api->AddVariable("%NumArg",position);
  api->AddVariable("%TypArg",aparam->Type()->FullName()->ToCString());
  if (CPPIntExt_IsRef(aparam->Type(),aMeta)) {
    api->Apply("%TextArg",
	       "InterfHandleArg");
  }
  else {
    api->Apply("%TextArg",
	       "InterfArg");
  }
  return api->GetVariableValue("%TextArg");
}

Handle(TColStd_HSequenceOfHAsciiString) CPPIntExt_BuildArgs(const Handle(MS_Method)& aMet,
							    const Handle(EDL_API)& api,
							    const Handle(MS_MetaSchema)& aMeta)
{
  Handle(TColStd_HSequenceOfHAsciiString) seqres   = new TColStd_HSequenceOfHAsciiString();
  Handle(MS_HArray1OfParam)               theargs  = aMet->Params();
  Standard_Boolean                        oncemore = Standard_True;

  Standard_Integer                        nbargs;

  if(theargs.IsNull()) 
    nbargs = 0;
  else
    nbargs = theargs->Length();

  while (oncemore) {
    Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString();

    for (Standard_Integer i=1; i<=nbargs; i++) {
      if (i > 1) {
	result->AssignCat(", ");
      }
      result->AssignCat(CPPIntExt_BuildAnArg(theargs->Value(i),i,api,aMeta));
    }

    seqres->Append(result);

    if (nbargs > 0) {
      if (theargs->Value(nbargs)->GetValueType() == MS_NONE) {
	oncemore = Standard_False;
      }
      else {
	nbargs--;
      }
    }
    else {
      oncemore = Standard_False;
    }
  }
  return seqres;
}

void 
CPPIntExt_WriteCase(const Standard_Integer casenumber,
		    const Handle(EDL_API)& api)
{
  
  api->AddVariable("%NumCase",casenumber);
  
  api->Apply("%TextCase",
	     "InterfCase");
  api->WriteFile("Interfilecxx",
		 "%TextCase");
}

void
CPPIntExt_WriteBreak(const Handle(EDL_API)& api)
{
  api->Apply("%TextBreak",
	     "InterfBreak");
  api->WriteFile("Interfilecxx",
		 "%TextBreak");
  
}

void 
CPPIntExt_WriteArgsDat(const Handle(MS_MetaSchema)& aMeta,
		       const Handle(MS_Method)& amet,
		       const Handle(EDL_API)& api,
		       const Standard_Integer nbremove)
{
  Handle(MS_HArray1OfParam) params = amet->Params();
  if(!params.IsNull())
    api->WriteFileConst("Interfiledat",params->Length()-nbremove);
  else
    api->WriteFileConst("Interfiledat",0-nbremove);

  api->WriteFileConst("Interfiledat",CPPIntPreDef_NewLine);

  if(!params.IsNull()) {
    for (Standard_Integer i=1; i<= params->Length()-nbremove; i++) {
      Handle(MS_Type) thetype = params->Value(i)->Type();
      if (thetype->IsKind(STANDARD_TYPE(MS_Alias))) {
	Handle(TCollection_HAsciiString) deepname = Handle(MS_Alias)::DownCast(thetype)->DeepType();
	thetype = aMeta->GetType(deepname);
      }
      api->WriteFileConst("Interfiledat",thetype->FullName()->ToCString());
      api->WriteFileConst("Interfiledat",CPPIntPreDef_NewLine);
      if (params->Value(i)->IsIn()) {
	if (params->Value(i)->IsOut()) {
	  api->WriteFileConst("Interfiledat",CPPIntPreDef_EngineInOut);
	}
	else {
	  api->WriteFileConst("Interfiledat",CPPIntPreDef_EngineIn);
	}
      }
      else {
	api->WriteFileConst("Interfiledat",CPPIntPreDef_EngineOut);
      }
    }
  }
}
	

void CPPIntExt_WriteMethodDat(const Handle(MS_Method)& amet,
			      const Handle(MS_MetaSchema)& aMeta,
			      const Handle(EDL_API)& api,
			      const Standard_Integer nbremove)
{
  api->WriteFileConst("Interfiledat",amet->Name()->ToCString());
  api->WriteFileConst("Interfiledat",CPPIntPreDef_NewLine);

  if (amet->IsKind(STANDARD_TYPE(MS_InstMet))) {
    Handle(MS_Type) theclass = aMeta->GetType(Handle(MS_InstMet)::DownCast(amet)->Class());
    api->WriteFileConst("Interfiledat",theclass->FullName()->ToCString());
    api->WriteFileConst("Interfiledat",CPPIntPreDef_NewLine);
    api->WriteFileConst("Interfiledat",CPPIntPreDef_EngineInstance);
  }
  else {
    Handle(MS_ExternMet) pkmet = Handle(MS_ExternMet)::DownCast(amet);
    if (!pkmet.IsNull()) {
      api->WriteFileConst("Interfiledat",pkmet->Package()->ToCString());
      api->WriteFileConst("Interfiledat",CPPIntPreDef_NewLine);
      api->WriteFileConst("Interfiledat",CPPIntPreDef_EnginePackage);
    }
    else {
      api->WriteFileConst("Interfiledat",Handle(MS_ClassMet)::DownCast(amet)->Class()->ToCString());
      api->WriteFileConst("Interfiledat",CPPIntPreDef_NewLine);
      api->WriteFileConst("Interfiledat",CPPIntPreDef_EngineClass);
    }
  }
  CPPIntExt_WriteArgsDat(aMeta,
			 amet,
			 api,
			 nbremove);
  Handle(MS_Param) theret = amet->Returns();
  if (theret.IsNull()) {
    api->WriteFileConst("Interfiledat","0\n");
  }
  else {
    api->WriteFileConst("Interfiledat","1\n");
    Handle(MS_Type) rettype = theret->Type();
    if (rettype->IsKind(STANDARD_TYPE(MS_Alias))) {
      Handle(TCollection_HAsciiString) deepname = Handle(MS_Alias)::DownCast(rettype)->DeepType();
      rettype = aMeta->GetType(deepname);
    }
    api->WriteFileConst("Interfiledat",rettype->FullName()->ToCString());
    api->WriteFileConst("Interfiledat",CPPIntPreDef_NewLine);
  }
}

void 
CPPIntExt_WriteMetOut(const Handle(MS_Method)&,
		      const Handle(MS_MetaSchema)&,
		      const Handle(MS_Interface)&,
		      const Handle(EDL_API)&,
		      const Standard_Integer);
void 
CPPIntExt_WriteConstructor(const Handle(MS_Construc)& ametconst,
			   const Handle(MS_MetaSchema)& aMeta,
			   const Handle(MS_Interface)& srcInterface,
			   const Handle(EDL_API)& api,
			   Standard_Integer& count)
{
  Handle(TCollection_HAsciiString) theclass = ametconst->Class();
  Handle(MS_Type) thetype = aMeta->GetType(theclass);
  if (!Handle(MS_Class)::DownCast(thetype)->Deferred()) {
    Handle(TColStd_HSequenceOfHAsciiString) seqargs = CPPIntExt_BuildArgs(ametconst,api,aMeta);
    for (Standard_Integer i=1; i<= seqargs->Length(); i++) {
      
      CPPIntExt_WriteCase(count,api);
      Handle(TCollection_HAsciiString) args = seqargs->Value(i);
      api->AddVariable("%ArgsConstruc",
		       args->ToCString());
      api->AddVariable("%CLName",theclass->ToCString());
      if (CPPIntExt_IsRef(thetype,aMeta)) {
	api->Apply("%TextConstructor",
		   "InterfHandleConstructor");
      }
      else {
	api->Apply("%TextConstructor",
		   "InterfConstructor");
      }
      api->WriteFile("Interfilecxx",
		     "%TextConstructor");
      CPPIntExt_WriteMetOut(ametconst,aMeta,srcInterface,api,0);
      CPPIntExt_WriteBreak(api);
      count++;
      // ecriture .dat
      api->WriteFileConst("Interfiledat",CPPIntPredef_Create);
      api->WriteFileConst("Interfiledat",theclass->ToCString());
      api->WriteFileConst("Interfiledat",CPPIntPreDef_NewLine);
      
      api->WriteFileConst("Interfiledat",CPPIntPreDef_EngineConstructor);
      CPPIntExt_WriteArgsDat(aMeta,ametconst,api,i-1);
      api->WriteFileConst("Interfiledat","1\n");
      
      api->WriteFileConst("Interfiledat",theclass->ToCString());
      api->WriteFileConst("Interfiledat",CPPIntPreDef_NewLine);
      
    }
  }
}

Handle(TColStd_HSequenceOfHAsciiString) CPPIntExt_BuildMethodBody(const Handle(MS_Method)& amet,
								  const Handle(MS_MetaSchema)& aMeta,
//								  const Handle(MS_Interface)& srcInterface,
								  const Handle(MS_Interface)& ,
								  const Handle(EDL_API)& api)
{
  api->AddVariable("%MetName",amet->Name()->ToCString());

  Handle(TColStd_HSequenceOfHAsciiString) seqargs = CPPIntExt_BuildArgs(amet,api,aMeta);
  Handle(TColStd_HSequenceOfHAsciiString) seqres = new TColStd_HSequenceOfHAsciiString();

  for (Standard_Integer i=1; i<= seqargs->Length(); i++) {
    api->AddVariable("%ArgsMet",seqargs->Value(i)->ToCString());

    if (amet->IsKind(STANDARD_TYPE(MS_InstMet))) {
      Handle(MS_Type) theclass = aMeta->GetType(Handle(MS_InstMet)::DownCast(amet)->Class());

      api->AddVariable("%CLName",theclass->FullName()->ToCString());
      if (CPPIntExt_IsRef(theclass,aMeta)) {
	api->Apply("%TextBody",
		   "InterfInstMethodHandleBody");
      }
      else {
	api->Apply("%TextBody",
		   "InterfInstMethodBody");
      }
    }
    else {
      Handle(MS_ExternMet) pkmet = Handle(MS_ExternMet)::DownCast(amet);

      if (!pkmet.IsNull()) {
	api->AddVariable("%CLName",pkmet->Package()->ToCString());
      }
      else {
	api->AddVariable("%CLName",
			 Handle(MS_ClassMet)::DownCast(amet)->Class()->ToCString());
      }
      api->Apply("%TextBody",
		 "InterfClassMethodBody");
    }
    seqres->Append(api->GetVariableValue("%TextBody"));
  }

  return seqres;
}

void 
CPPIntExt_WriteMetOut(const Handle(MS_Method)& amet,
		      const Handle(MS_MetaSchema)& aMeta,
//		      const Handle(MS_Interface)& srcInterface,
		      const Handle(MS_Interface)& ,
		      const Handle(EDL_API)& api,
		      const Standard_Integer nbargsminus)
{
  Handle(MS_HArray1OfParam) parameters = amet->Params();
  
  if(!parameters.IsNull()) {
    for (Standard_Integer i=1; i<= parameters->Length() - nbargsminus; i++) {
      if (parameters->Value(i)->IsOut()) {
	if (parameters->Value(i)->Type()->IsKind(STANDARD_TYPE(MS_NatType))) {
	  Handle(TCollection_HAsciiString) thearg = 
	    CPPIntExt_BuildAnArg(parameters->Value(i),
				 i,
				 api,
				 aMeta);
	  api->AddVariable("%Arg",thearg->ToCString());
	  api->AddVariable("%Pos",i);
	  if (parameters->Value(i)->Type()->IsKind(STANDARD_TYPE(MS_Enum))) {
	    api->Apply("%TextOutArg",
		       "InterfEngineOutArgEnum");
	  }
	  else {
	    api->Apply("%TextOutArg",
		       "InterfEngineOutArg");
	  }
	  api->WriteFile("Interfilecxx",
			 "%TextOutArg");
	}
      }
    }
  }
}


void 
CPPIntExt_WriteCall(const Handle(MS_Method)& amet,
		    const Handle(MS_MetaSchema)& aMeta,
		    const Handle(MS_Interface)& srcInterface,
		    const Handle(EDL_API)& api,
		    Standard_Integer& count)
{
  Handle(TColStd_HSequenceOfHAsciiString) seqfunc = CPPIntExt_BuildMethodBody(amet,aMeta,srcInterface,api);
  for (Standard_Integer i=1; i<= seqfunc->Length(); i++) {
    CPPIntExt_WriteCase(count,api);
    Handle(TCollection_HAsciiString) func = seqfunc->Value(i);
    func->AssignCat(";");
    api->WriteFileConst("Interfilecxx",func->ToCString());
    CPPIntExt_WriteMetOut(amet,aMeta,srcInterface,api,i-1);
    CPPIntExt_WriteBreak(api);
    CPPIntExt_WriteMethodDat(amet,aMeta,api,i-1);
    count++;
  }
}

Standard_Boolean CPPIntExt_HasPublicMagic(const Handle(MS_Type)& atyp)
{
  Handle(MS_Class) thecl = Handle(MS_Class)::DownCast(atyp);
  if (thecl.IsNull()) return Standard_True;
  Handle(MS_HSequenceOfMemberMet) methods = thecl->GetMethods();
  for (Standard_Integer i=1; i<= methods->Length(); i++) {
    if (methods->Value(i)->IsKind(STANDARD_TYPE(MS_Construc))) {
      Handle(MS_HArray1OfParam) parameters = methods->Value(i)->Params();
      if (!parameters.IsNull()) {
	if (parameters->Length() == 1) {
	  if (parameters->Value(1)->Type() == atyp) {
	    if (methods->Value(i)->Private()) {
	      return Standard_False;
	    }
	    return Standard_True;
	  }
	}
      }
    }
  }
  return Standard_True;
}

Standard_Boolean CPPIntExt_HasPublicEmpty(const Handle(MS_Type)& atyp)
{
  Handle(MS_Class) thecl = Handle(MS_Class)::DownCast(atyp);
  if (thecl.IsNull()) return Standard_False;
  Handle(MS_HSequenceOfMemberMet) methods = thecl->GetMethods();
  for (Standard_Integer i=1; i<= methods->Length(); i++) {
    if (methods->Value(i)->IsKind(STANDARD_TYPE(MS_Construc))) {
      Handle(MS_HArray1OfParam) parameters = methods->Value(i)->Params();
      if (parameters.IsNull()) {
	if (methods->Value(i)->Private()) {
	  return Standard_False;
	}
	return Standard_True;
      }
    }
  }
  return Standard_False;
}


void 
CPPIntExt_WriteRetClass(const Handle(MS_Method)& amet,
			const Handle(MS_MetaSchema)& aMeta,
			const Handle(MS_Interface)& srcInterface,
			const Handle(EDL_API)& api,
			Standard_Integer& count)
{
  Handle(TColStd_HSequenceOfHAsciiString) seqfunc = CPPIntExt_BuildMethodBody(amet,aMeta,srcInterface,api);
  for (Standard_Integer i=1; i<= seqfunc->Length(); i++) {
    Handle(TCollection_HAsciiString) func = seqfunc->Value(i);
    
    CPPIntExt_WriteCase(count,api);
    api->AddVariable("%TextEngineHandle",func->ToCString());
    Handle(MS_Param) theret = amet->Returns();
    api->AddVariable("%CLName",theret->TypeName()->ToCString());
    Handle(MS_Type) therettyp = theret->Type();
    if (CPPIntExt_IsRef(therettyp,aMeta)) {
      api->Apply("%TextCall",
		 "InterfNewEHHandle");
    }
    else if (amet->IsRefReturn()) {
      api->Apply("%TextCall",
		 "InterfNewEHRef");
    }
    else if (CPPIntExt_HasPublicMagic(therettyp)) {
      api->Apply("%TextCall",
		 "InterfNewEHMagic");
    }
    else if (CPPIntExt_HasPublicEmpty(therettyp)) {
      api->Apply("%TextCall",
		 "InterfNewEHEmpty");
    }
    else {
      api->Apply("%TextCall",
		 "InterfNewEHMalloc");
    }
    api->WriteFile("Interfilecxx",
		   "%TextCall");
    CPPIntExt_WriteMetOut(amet,aMeta,srcInterface,api,i-1);
    CPPIntExt_WriteBreak(api);
    CPPIntExt_WriteMethodDat(amet,aMeta,api,i-1);
    count++;
  }
}

void 
CPPIntExt_WriteRetNat(const Handle(MS_Method)& amet,
		      const Handle(MS_MetaSchema)& aMeta,
		      const Handle(MS_Interface)& srcInterface,
		      const Handle(EDL_API)& api,
		      Standard_Integer& count)
{
  Handle(TColStd_HSequenceOfHAsciiString) seqfunc = CPPIntExt_BuildMethodBody(amet,aMeta,srcInterface,api);
  for (Standard_Integer i=1; i<= seqfunc->Length(); i++) {
    Handle(TCollection_HAsciiString) func = seqfunc->Value(i);
    CPPIntExt_WriteCase(count,api);
    api->AddVariable("%TextEngineHandle",func->ToCString());
    if (amet->Returns()->Type()->IsKind(STANDARD_TYPE(MS_Enum))) {
      api->Apply("%TextCall",
	       "InterfEngineReturnEnum");
    }
    else {
      api->Apply("%TextCall",
		 "InterfEngineReturn");
    }
    api->WriteFile("Interfilecxx",
		   "%TextCall");
    CPPIntExt_WriteMetOut(amet,aMeta,srcInterface,api,i-1);
    CPPIntExt_WriteBreak(api);
    CPPIntExt_WriteMethodDat(amet,aMeta,api,i-1);
    count++;
  }
}

void 
CPPIntExt_WriteMethod(const Handle(MS_Method)& amet,
		      const Handle(MS_MetaSchema)& aMeta,
		      const Handle(MS_Interface)& srcInterface,
		      const Handle(EDL_API)& api,
		      Standard_Integer& count)
{
  Handle(MS_Param) parret = amet->Returns();
  if (parret.IsNull()) {
    CPPIntExt_WriteCall(amet,aMeta,srcInterface,api,count);
  }
  else {
    Handle(MS_Type) typret = parret->Type();
    if (typret->IsKind(STANDARD_TYPE(MS_Alias))) {
      Handle(TCollection_HAsciiString) deepname = Handle(MS_Alias)::DownCast(typret)->DeepType();
      typret = aMeta->GetType(deepname);
    }
    if (typret->IsKind(STANDARD_TYPE(MS_Class))) {
      CPPIntExt_WriteRetClass(amet,aMeta,srcInterface,api,count);
    }
    else {
      CPPIntExt_WriteRetNat(amet,aMeta,srcInterface,api,count);
    }
  }
}

void CPPIntExt_ProcessCases(const Handle(MS_MetaSchema)& aMeta,
			    const Handle(MS_Interface)& srcInterface,
			    const Handle(EDL_API)& api,
			    const MS_MapOfMethod& expmap)
{
  MS_DataMapIteratorOfMapOfMethod itmet(expmap);
  WOKTools_Array1OfHAsciiString armet(1,expmap.Extent());
  Standard_Integer count = 1;
  while (itmet.More()) {
    armet(count) = itmet.Value()->FullName();
    count++;
    itmet.Next();
  }
  WOKTools_CompareOfHAsciiString cmpmet;
  WOKTools_SortOfHAsciiString::Sort(armet,cmpmet);
 
  count = 1;
  
  for(Standard_Integer i=1;i<=armet.Length(); i++) {
    Handle(MS_Method) themet = expmap(armet(i));
    Handle(MS_Construc) metconstr = Handle(MS_Construc)::DownCast(themet);
    if (!metconstr.IsNull()) {
      CPPIntExt_WriteConstructor(metconstr,aMeta,srcInterface,api,count);
    }
    else {
      CPPIntExt_WriteMethod(themet,aMeta,srcInterface,api,count);
    }
  }
}

  
void CPPIntExt_ProcessMultiCases(const Handle(MS_MetaSchema)& aMeta,
				 const Handle(MS_Interface)& srcInterface,
				 const Handle(EDL_API)& api,
				 const MS_MapOfMethod& expmap,
				 TColStd_SequenceOfInteger& switchcount)
{
  switchcount.Clear();
  switchcount.Append(1);
  MS_DataMapIteratorOfMapOfMethod itmet(expmap);
  WOKTools_Array1OfHAsciiString armet(1,expmap.Extent());
  Standard_Integer count = 1;
  while (itmet.More()) {
    armet(count) = itmet.Value()->FullName();
    count++;
    itmet.Next();
  }
  WOKTools_CompareOfHAsciiString cmpmet;
  WOKTools_SortOfHAsciiString::Sort(armet,cmpmet);
 
  count = 1;
  Standard_Integer countintcase =1;
  api->AddVariable("%IntName",srcInterface->Name()->ToCString());
  api->AddVariable("%NumCaseFunc",1);
  api->Apply("%TextExec",
	     "InterfMultiFuncCall");
  api->WriteFile("Interfilecxx",
		 "%TextExec");
  
  for(Standard_Integer i=1;i<=armet.Length(); i++) {
    if ((count - switchcount(countintcase)) >= CPPINTEXT_MAXINTFSWITCH) {
      api->Apply("%TextExec",
		 "InterfMultiFuncEndCall");
      api->WriteFile("Interfilecxx",
		     "%TextExec");
      countintcase++;
      switchcount.Append(count);
      api->AddVariable("%IntName",srcInterface->Name()->ToCString());
      api->AddVariable("%NumCaseFunc",countintcase);
      api->Apply("%TextExec",
		 "InterfMultiFuncCall");
      api->WriteFile("Interfilecxx",
		     "%TextExec");
    }
      
    Handle(MS_Method) themet = expmap(armet(i));
    Handle(MS_Construc) metconstr = Handle(MS_Construc)::DownCast(themet);
    if (!metconstr.IsNull()) {
      CPPIntExt_WriteConstructor(metconstr,aMeta,srcInterface,api,count);
    }
    else {
      CPPIntExt_WriteMethod(themet,aMeta,srcInterface,api,count);
    }
  }
  api->Apply("%TextExec",
	     "InterfMultiFuncEndCall");
  api->WriteFile("Interfilecxx",
		 "%TextExec");
  switchcount.Append(count);
}
