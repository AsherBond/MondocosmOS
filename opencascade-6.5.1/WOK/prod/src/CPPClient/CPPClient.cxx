// CLE : Extracteur de stubs C++ pour CAS.CADE 
//    Matra-Datavision 1995
//
// 10/1995
//
#include <MS.hxx>
#include <MS_Client.hxx>
#include <CPPClient.hxx>
#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <MS_ParamWithValue.hxx>
#include <MS_HArray1OfParam.hxx>

static WOKTools_MapOfHAsciiString MethodMap;

Handle(MS_HSequenceOfMemberMet) SeqOfMemberMet = new MS_HSequenceOfMemberMet;
Handle(MS_HSequenceOfExternMet) SeqOfExternMet = new MS_HSequenceOfExternMet;

Handle(TCollection_HAsciiString) CPPClient_InterfaceName;
Handle(TCollection_HAsciiString) CPPClient_ErrorArgument = new TCollection_HAsciiString("%error%");

// Standard Extractor API : list the EDL files used by this program
//
Handle(TColStd_HSequenceOfHAsciiString) CPPClient_TemplatesUsed()
{
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;

  result->Append(new TCollection_HAsciiString("CPPClient_Template.edl"));
  result->Append(new TCollection_HAsciiString("CPPClient_General.edl"));

  return result;
}

void CPPClient_Init(const Handle(MS_MetaSchema)& aMeta,
		    const Handle(TCollection_HAsciiString)& aName, 
		    const Handle(MS_HSequenceOfExternMet)& SeqOfEM,
		    const Handle(MS_HSequenceOfMemberMet)& SeqOfMM)
{
  Standard_Integer  i;
  Handle(MS_Client) client;
  
  SeqOfMemberMet = SeqOfMM;
  SeqOfExternMet = SeqOfEM;

  MethodMap.Clear();

  if (aMeta->IsClient(aName)) {
    Handle(MS_Method) amet;
    Handle(TColStd_HSequenceOfHAsciiString) asyncmet;

    CPPClient_InterfaceName = aName;
    client                  = aMeta->GetClient(aName);
    asyncmet                = client->Methods();
    
    for(i = 1; i <= asyncmet->Length(); i++) {
      amet = MS::GetMethodFromFriendName(aMeta,asyncmet->Value(i));
      
      if (!amet.IsNull()) {
	if (!MethodMap.Contains(amet->FullName())) {
	  MethodMap.Add(amet->FullName()); 
	}
      }
      else {
	ErrorMsg() << "CPPClient" << "Init : Method " << asyncmet->Value(i) << " not found..." << endm;
	Standard_NoSuchObject::Raise();
      }
    }
    
  }
  else {
    ErrorMsg() << "CPPClient" << "Init : Client " << aName << " not found..." << endm;
    Standard_NoSuchObject::Raise();
  }
}

Handle(TCollection_HAsciiString)& CPPClient_TransientRootName() 
{
  static Handle(TCollection_HAsciiString) name = new TCollection_HAsciiString("FrontEnd_HExternRef");

  return name;
}

Handle(TCollection_HAsciiString)& CPPClient_MemoryRootName() 
{
  static Handle(TCollection_HAsciiString) name = new TCollection_HAsciiString("MMgt_TShared");

  return name;
}

Handle(TCollection_HAsciiString)& CPPClient_MPVRootName() 
{
  static Handle(TCollection_HAsciiString) name = new TCollection_HAsciiString("FrontEnd_ExternRef");

  return name;
}

Handle(EDL_API)&  CPPClient_LoadTemplate(const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
				   const Handle(TCollection_HAsciiString)& outdir)
{
  static Handle(EDL_API)  api = new EDL_API;
  static Standard_Boolean alreadyLoaded = Standard_False;

  api->ClearVariables();

  if (!alreadyLoaded) {
    alreadyLoaded = Standard_True;

    for(Standard_Integer i = 1; i <= edlsfullpath->Length(); i++) {
      api->AddIncludeDirectory(edlsfullpath->Value(i)->ToCString());
    }

    if (api->Execute("CPPClient_Template.edl") != EDL_NORMAL) {
      ErrorMsg() << "CPPClient" << "unable to load : CPPClient_Template.edl" << endm;
      Standard_NoSuchObject::Raise();
    } 
    if (api->Execute("CPPClient_General.edl") != EDL_NORMAL) {
      ErrorMsg() << "CPPClient" << "unable to load : CPPClient_General.edl" << endm;
      Standard_NoSuchObject::Raise();
    } 
  }

  // full path of the destination directory
  //
  api->AddVariable(VFullPath,outdir->ToCString());

  // templates for methods extraction
  //
  api->AddVariable(VMethodHeader,"MethodHeader");
  api->AddVariable(VConstructorHeader,"ConstructorHeader");
  api->AddVariable(VInterface,CPPClient_InterfaceName->ToCString());

  return api;
}

// write the content of a variable into a file
//
void CPPClient_WriteFile(const Handle(EDL_API)& api,
			 const Handle(TCollection_HAsciiString)& aFileName,
			 const Standard_CString var)
{
  // ...now we write the result
  //
  api->OpenFile("HTFile",aFileName->ToCString());
  api->WriteFile("HTFile",var);
  api->CloseFile("HTFile");
}

// we test the type and dispatch it in the different lists
//
//void CPPClient_DispatchUsedType(const Handle(MS_MetaSchema)& aMeta,
void CPPClient_DispatchUsedType(const Handle(MS_MetaSchema)& ,
				const Handle(MS_Type)& thetype,
				const Handle(TColStd_HSequenceOfHAsciiString)& List,
				const Handle(TColStd_HSequenceOfHAsciiString)& Incp,
				const Standard_Boolean notusedwithref)
{
  if (thetype->IsKind(STANDARD_TYPE(MS_Class))) {
    Handle(MS_Class)                 theclass = *((Handle(MS_Class)*)&thetype);
    Handle(TCollection_HAsciiString) handlename;

    if (theclass->IsTransient() || theclass->IsPersistent()) {
      handlename = new TCollection_HAsciiString("Handle_");

      handlename->AssignCat(CPPClient_InterfaceName);
      handlename->AssignCat("_");
      handlename->AssignCat(thetype->FullName());
      MS::AddOnce(List,handlename);
      handlename = new TCollection_HAsciiString(CPPClient_InterfaceName);
      handlename->AssignCat("_");
      handlename->AssignCat(thetype->FullName());
      MS::AddOnce(Incp,handlename);
    }
    else {
      handlename = new TCollection_HAsciiString(CPPClient_InterfaceName);
      handlename->AssignCat("_");
      handlename->AssignCat(thetype->FullName());

      if (notusedwithref) {
	MS::AddOnce(List,handlename);
      }
      else {
	MS::AddOnce(Incp,handlename);
      }
    }
  }
  else if (thetype->IsKind(STANDARD_TYPE(MS_Enum))) {
    Handle(TCollection_HAsciiString) name = new TCollection_HAsciiString(CPPClient_InterfaceName);
    name->AssignCat("_");
    name->AssignCat(thetype->FullName());
    MS::AddOnce(List,name);
  }
  else if (thetype->IsKind(STANDARD_TYPE(MS_NatType))) {
    MS::AddOnce(List,thetype->FullName());
  }      
}

Standard_Boolean CPPClient_AncestorHaveEmptyConstructor(const Handle(MS_MetaSchema)& aMeta,
							const Handle(TCollection_HAsciiString)& aMother)
{
  Standard_Boolean result = Standard_False;

  if (aMeta->IsDefined(aMother)) {
    Handle(MS_Type) t = aMeta->GetType(aMother);

    if (t->IsKind(STANDARD_TYPE(MS_Class))) {
      Handle(MS_HSequenceOfMemberMet) methods;
      Handle(MS_Class)                c = *((Handle(MS_Class)*)&t);
      Standard_Integer                i;

      methods = c->GetMethods();

      for (i = 1; i <= methods->Length() && !result; i++) {
	if (methods->Value(i)->IsKind(STANDARD_TYPE(MS_Construc))) {
	  if ((methods->Value(i)->Params().IsNull()) && !methods->Value(i)->Private() && !methods->Value(i)->IsProtected()) {
	    result = Standard_True;
	  }
	}
      }
    }
  }
  
  return result;
}

// sort the method used types :
//
//    FullList : all the used types 
//    List     : the types that must have a full definition
//    Incp     : the types that only have to be declared
//
void CPPClient_MethodUsedTypes(const Handle(MS_MetaSchema)& aMeta,
			       const Handle(MS_Method)& aMethod,
			       const Handle(TColStd_HSequenceOfHAsciiString)& List,
			       const Handle(TColStd_HSequenceOfHAsciiString)& Incp)
{
  Standard_Integer                 i;
  Handle(MS_Param)                 aParam;
  Handle(MS_Type)                  thetype;
  Handle(TCollection_HAsciiString) aName,aNameType,parname;

  if (aMethod->IsKind(STANDARD_TYPE(MS_MemberMet))) {
    Handle(MS_MemberMet) aMM = *((Handle(MS_MemberMet)*)&aMethod);

    aName = aMM->Class();
  }
  else if (aMethod->IsKind(STANDARD_TYPE(MS_ExternMet))) {
    Handle(MS_ExternMet) aMM = *((Handle(MS_ExternMet)*)&aMethod);

    aName = aMM->Package();
  }

  aParam = aMethod->Returns();

  if (!aParam.IsNull()) {
    thetype = aParam->Type();
    parname = aParam->TypeName();

    if (thetype->IsKind(STANDARD_TYPE(MS_Alias))) {
      Handle(MS_Alias) analias = *((Handle(MS_Alias)*)&thetype);
      
      parname = analias->DeepType();
      
      if (aMeta->IsDefined(parname)) {
	thetype = aMeta->GetType(parname);
      }
      else {
	ErrorMsg() << "CPPClient" << "Type " << parname << " not defined..." << endm;
	Standard_NoSuchObject::Raise();
      }
    }
  
    if (!parname->IsSameString(aName)) {
      CPPClient_DispatchUsedType(aMeta,thetype,List,Incp,!aMethod->IsRefReturn());
    }
  }

  Handle(MS_HArray1OfParam)      seqparam = aMethod->Params();

  if(!seqparam.IsNull()) {
    for (i = 1; i <= seqparam->Length(); i++) {
      thetype = seqparam->Value(i)->Type();
      parname = seqparam->Value(i)->TypeName();

      if (thetype->IsKind(STANDARD_TYPE(MS_Alias))) {
	Handle(MS_Alias) analias = *((Handle(MS_Alias)*)&thetype);
      
	parname = analias->DeepType();
      
	if (aMeta->IsDefined(parname)) {
	  thetype = aMeta->GetType(parname);
	}
	else {
	  ErrorMsg() << "CPPClient" << "Type " << parname << " not defined..." << endm;
	  Standard_NoSuchObject::Raise();
	}
      }

      if (!parname->IsSameString(aName)) {
	CPPClient_DispatchUsedType(aMeta,thetype,List,Incp,Standard_False);
      }
    }
  }
}


// sort the class used types :
//
//    FullList : all the used types 
//    List     : the types that must have a full definition
//    Incp     : the types that only have to be declared
//
void CPPClient_ClassUsedTypes(const Handle(MS_MetaSchema)& aMeta,
			const Handle(MS_Class)& aClass,
			const Handle(TColStd_HSequenceOfHAsciiString)& List,
			const Handle(TColStd_HSequenceOfHAsciiString)& Incp)
{
  Standard_Integer                        i;
  Handle(TColStd_HSequenceOfHAsciiString) asciiseq;
  Handle(TCollection_HAsciiString)        str,aNameType;

  asciiseq = aClass->GetInheritsNames();

  for (i = 1; i <= asciiseq->Length(); i++) {
    aNameType = new TCollection_HAsciiString;
    aNameType->AssignCat(CPPClient_InterfaceName);
    aNameType->AssignCat("_");
    aNameType->AssignCat(asciiseq->Value(i));
    MS::AddOnce(List,aNameType);
  }

  Handle(MS_HSequenceOfMemberMet) metseq = aClass->GetMethods();

  for (i = 1; i <= metseq->Length(); i++) {
    CPPClient_MethodUsedTypes(aMeta,metseq->Value(i),List,Incp);
  }
}


// sort the used types :
//
//    FullList : all the used types 
//    List     : the types that must have a full definition
//    Incp     : the types that only have to be declared
//
void CPPClient_UsedTypes(const Handle(MS_MetaSchema)& aMeta,
			 const Handle(MS_Common)& aCommon,
			 const Handle(TColStd_HSequenceOfHAsciiString)& List,
			 const Handle(TColStd_HSequenceOfHAsciiString)& Incp)
{
  if (aCommon->IsKind(STANDARD_TYPE(MS_Type))) {
    if (aCommon->IsKind(STANDARD_TYPE(MS_Class))) {
      Handle(MS_Class) aClass = *((Handle(MS_Class)*)&aCommon);
      
      CPPClient_ClassUsedTypes(aMeta,aClass,List,Incp);
    }
  }
}

// build a return, parameter or field type in c++
//  return a <type name> or a Handle_<type name>
//
Handle(TCollection_HAsciiString) CPPClient_BuildType(const Handle(MS_MetaSchema)& aMeta,
						     const Handle(TCollection_HAsciiString)& aTypeName)
{
  Handle(TCollection_HAsciiString)   result = new TCollection_HAsciiString();
  Handle(TCollection_HAsciiString)   rTypeName;
  Handle(TCollection_HAsciiString)   parname;
  Handle(MS_Type)                    aType;

  
  if (aMeta->IsDefined(aTypeName)) {
    aType     = aMeta->GetType(aTypeName);
    parname   = aTypeName;

    if (aType->IsKind(STANDARD_TYPE(MS_Alias))) {
      Handle(MS_Alias) analias = *((Handle(MS_Alias)*)&aType);
      
      parname = analias->DeepType();
      
      if (aMeta->IsDefined(parname)) {
	aType = aMeta->GetType(parname);
      }
      else {
	ErrorMsg() << "CPPClient" << "Type " << parname << " not defined..." << endm;
	Standard_NoSuchObject::Raise();
      }
    }
  
    rTypeName = new TCollection_HAsciiString(CPPClient_InterfaceName);
    rTypeName->AssignCat("_");
    rTypeName->AssignCat(parname);

    if (aType->IsKind(STANDARD_TYPE(MS_Class))) {
      Handle(MS_Class) aClass;
      
      aClass = *((Handle(MS_Class)*)&aType);
      
      if (aClass->IsPersistent() || aClass->IsTransient()) {
	result->AssignCat("Handle_");
	result->AssignCat(rTypeName);
      }
      else {
	result->AssignCat(rTypeName);
      } 
    }
    else if (aType->IsKind(STANDARD_TYPE(MS_NatType))) {
      if (aType->IsKind(STANDARD_TYPE(MS_Imported)) || aType->IsKind(STANDARD_TYPE(MS_Pointer))) {
	result = CPPClient_ErrorArgument;
      }
      else {
	result->AssignCat(parname);
      }
    }
  }
  else {
    ErrorMsg() << "CPPClient" << "Type " << aTypeName << " not defined..." << endm;
    Standard_NoSuchObject::Raise();
  }

  return result;
}

// Build a parameter list for methods
//    the output is in C++
//
Handle(TCollection_HAsciiString) CPPClient_BuildParameterList(const Handle(MS_MetaSchema)& aMeta, 
							      const Handle(MS_HArray1OfParam)& aSeq,
							      const Standard_Boolean withDefaultValue)
{
  Standard_Integer                 i;
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString;
  Handle(MS_Type)                  aType;
  Handle(MS_Class)                 aClass;
  Handle(TCollection_HAsciiString) parname;

  if(!aSeq.IsNull()) {
    for (i = 1; i <= aSeq->Length(); i++) {
      if (i > 1) {
	result->AssignCat(",");
      }

      if (!aSeq->Value(i)->IsOut()) {
	result->AssignCat("const ");
      }
    
      if (aMeta->IsDefined(aSeq->Value(i)->TypeName())) {
	parname = aSeq->Value(i)->TypeName();
	aType   = aMeta->GetType(parname);
      
	if (aType->IsKind(STANDARD_TYPE(MS_Alias))) {
	  Handle(MS_Alias) analias = *((Handle(MS_Alias)*)&aType);
	
	  parname = analias->DeepType();
	
	  if (aMeta->IsDefined(parname)) {
	    aType = aMeta->GetType(parname);
	  }
	  else {
	    ErrorMsg() << "CPPClient" << "Type " << parname << " not defined..." << endm;
	    Standard_NoSuchObject::Raise();
	  }
	}
  

      
	if (aType->IsKind(STANDARD_TYPE(MS_Class))) {
	  aClass = *((Handle(MS_Class)*)&aType);

	  if (aClass->IsPersistent() || aClass->IsTransient()) {
	    result->AssignCat("Handle(");
	    result->AssignCat(CPPClient_InterfaceName);
	    result->AssignCat("_");
	    result->AssignCat(parname);
	    result->AssignCat(")& ");
	    result->AssignCat(aSeq->Value(i)->Name());
	  }
	  else {
	    result->AssignCat(CPPClient_InterfaceName);
	    result->AssignCat("_");
	    result->AssignCat(parname);
	    result->AssignCat("& ");
	    result->AssignCat(aSeq->Value(i)->Name());
	  }
	} 
	else if ((aType->IsKind(STANDARD_TYPE(MS_Alias)) || aSeq->Value(i)->IsItem() || aSeq->Value(i)->IsOut()) && !(aType->IsKind(STANDARD_TYPE(MS_Imported)) || aType->IsKind(STANDARD_TYPE(MS_Pointer)))) {
	  result->AssignCat(parname);
	  result->AssignCat("& ");
	  result->AssignCat(aSeq->Value(i)->Name());
	}
	// WARNING : error here
	//
	else if (aType->IsKind(STANDARD_TYPE(MS_Imported)) || aType->IsKind(STANDARD_TYPE(MS_Pointer))) {
	  result = CPPClient_ErrorArgument;
	  return result;
	}
	else if (aType->IsKind(STANDARD_TYPE(MS_PrimType))) {
	  result->AssignCat(parname);
	  result->AssignCat(" ");
	  result->AssignCat(aSeq->Value(i)->Name());
	}
	else {
	  result->AssignCat(parname);
	  result->AssignCat(" ");
	  result->AssignCat(aSeq->Value(i)->Name());
	}
      }
      else {
	result->AssignCat(aSeq->Value(i)->TypeName());
	result->AssignCat("& ");
	result->AssignCat(aSeq->Value(i)->Name());
      }

      if (aSeq->Value(i)->GetValueType() != MS_NONE && withDefaultValue) {
	MS_ParamWithValue* pwv = (MS_ParamWithValue*)aSeq->Value(i).operator->();
	result->AssignCat(" = ");
	result->AssignCat(pwv->GetValue());
      }
    }
  }
  return result;
}


// build a c++ declaration method (ASYNCHRONOUS)
// the result is in the EDL variable VMethod
//
//   template used :
//
//         MethodTemplateDef
//         ConstructorTemplateDef
//         MethodTemplateDec
//         ConstructorTemplateDec
//         InlineMethodTemplateDec
//
//   the EDL variables : 
//        VMethodHeader : must contains the name of the template used for 
//                        methods construction
//        VConstructorHeader :  must contains the name of the template used for 
//                              constructors construction
//
//  WARNING : if an error was found the result in the variable "%Method" will be "%error%"
//
void CPPClient_BuildAsynchronousMethod(const Handle(MS_MetaSchema)& aMeta, 
				       const Handle(EDL_API)& api, 
				       const Handle(MS_Method)& m,
				       const Handle(TCollection_HAsciiString)& methodName,
				       const Standard_Boolean forGetArg,
				       const Standard_Boolean forDeclaration = Standard_True)
{
  Handle(MS_InstMet)  im;
  Handle(MS_ClassMet) cm;
  Handle(MS_Construc) ct;
  Handle(MS_Param)    retType;
  
  Handle(TCollection_HAsciiString) MetTemplate,
                                   theArgList,
                                   ConTemplate;

  Standard_Boolean InlineMethod;

  MetTemplate = api->GetVariableValue(VMethodHeader);
  ConTemplate = api->GetVariableValue(VConstructorHeader);

 
  // no inline method in c++ client
  //
  InlineMethod = Standard_False;
  api->AddVariable(VIsInline,"no");

  api->AddVariable(VVirtual,"");

  if (forGetArg) {
    Handle(TCollection_HAsciiString) argName = new TCollection_HAsciiString(methodName);
    
    argName->AssignCat("Arg");
    api->AddVariable(VMethodName,argName->ToCString());      
  }
  else {
    api->AddVariable(VMethodName,methodName->ToCString());
  }

  // it s returning const ?
  //
  if (m->IsConstReturn()) {
    api->AddVariable(VRetSpec,"const");
  }
  else {
    api->AddVariable(VRetSpec,"");
  }
  
  // no ref return in c++ client
  //
  api->AddVariable(VAnd,"");

  theArgList = CPPClient_BuildParameterList(aMeta,m->Params(),forDeclaration);

  if (theArgList == CPPClient_ErrorArgument) {
    WarningMsg() << "CPPClient" << "Bad argument type in method (pointer or imported type) " << m->FullName() << endm;
    WarningMsg() << "CPPClient" << "Method : " << m->FullName() << " not exported." << endm;
    api->AddVariable(VMethod,CPPClient_ErrorArgument->ToCString());
    return;
  }

  if (m->IsKind(STANDARD_TYPE(MS_ClassMet)) || m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
    if (!theArgList->IsEmpty()) {
      theArgList->AssignCat(",");
    }
    else {
      theArgList->AssignCat(" ");
    }
    api->AddVariable("%Arguments",theArgList->ToCString());
    if (forDeclaration) {
      api->Apply("%Arguments","AsyncClientDec");
    }
    else {
       api->Apply("%Arguments","AsyncClientDef");
    }
    api->AddVariable("%Arguments",api->GetVariableValue("%Arguments")->ToCString());
  }
  else {
    if (!theArgList->IsEmpty()) {
      theArgList->AssignCat(",FrontEnd_MID& uid");
    }
    else {
      theArgList->AssignCat("FrontEnd_MID& uid");
    }
    api->AddVariable("%Arguments",theArgList->ToCString());
  }

  // it s returning a type or void ?
  //
  retType = m->Returns();
  
  if (!retType.IsNull() && forGetArg) {
    Handle(TCollection_HAsciiString) returnT = CPPClient_BuildType(aMeta,retType->TypeName());

    if (returnT == CPPClient_ErrorArgument) {
      WarningMsg() << "CPPClient" << "Return type (pointer or imported type) of " << m->FullName() << " not exportable." << endm;
      WarningMsg() << "CPPClient" << "Method : " << m->FullName() << " not exported." << endm;
      api->AddVariable(VMethod,CPPClient_ErrorArgument->ToCString());
      return;
    }
    else {
      api->AddVariable(VReturn,returnT->ToCString());
    }
  }
  else {
    api->AddVariable(VReturn,"void");
  }
  
  // now the specials attributes
  //
  // instance methods
  //
  if (m->IsKind(STANDARD_TYPE(MS_InstMet))) {
    im = *((Handle(MS_InstMet)*)&m);
    
    api->AddVariable(VIsCreateMethod,"no");

    if (!im->IsDeferred() || !forDeclaration) {
      if (!im->IsStatic() && forDeclaration) {
		api->AddVariable(VVirtual,"Standard_EXPORT virtual");
      }
    	else if (im->IsStatic() && forDeclaration) {
		api->AddVariable(VVirtual,"Standard_EXPORT");
	}

      if (im->IsConst()) {
	api->AddVariable(VMetSpec,"const");
      }
      else {
	api->AddVariable(VMetSpec,"");
      }
    }
    // no pure vir classes in stubs
    //
    else if (forDeclaration) {
      api->AddVariable(VVirtual,"Standard_EXPORT virtual");

      if (im->IsConst()) {
	api->AddVariable(VMetSpec,"const");
      }
      else {
	api->AddVariable(VMetSpec,"");
      }
    }

    api->Apply(VMethod,MetTemplate->ToCString());
  }
  //
  // class methods
  //
  else if (m->IsKind(STANDARD_TYPE(MS_ClassMet))) {
    api->AddVariable(VIsCreateMethod,"no");
    api->AddVariable(VMetSpec,"");
    if (forDeclaration) {
      api->AddVariable(VVirtual,"Standard_EXPORT static");
    }
    else {
      api->AddVariable(VVirtual,"");
    }
   
    api->Apply(VMethod,MetTemplate->ToCString());
  }
  //
  // package methods
  //
  else if (m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
    api->AddVariable(VIsCreateMethod,"no");
    api->AddVariable(VMetSpec,"");
    if (forDeclaration) {
      api->AddVariable(VVirtual,"Standard_EXPORT static");
    }

    api->Apply(VMethod,MetTemplate->ToCString());
  }  
}

// build a c++ declaration method
// the result is in the EDL variable VMethod
//
//   template used :
//
//         MethodTemplateDef
//         ConstructorTemplateDef
//         MethodTemplateDec
//         ConstructorTemplateDec
//         InlineMethodTemplateDec
//
//   the EDL variables : 
//        VMethodHeader : must contains the name of the template used for 
//                        methods construction
//        VConstructorHeader :  must contains the name of the template used for 
//                              constructors construction
//
//  WARNING : if an error was found the result in the variable "%Method" will be "%error%"
//
void CPPClient_BuildMethod(const Handle(MS_MetaSchema)& aMeta, 
			   const Handle(EDL_API)& api, 
			   const Handle(MS_Method)& m,
			   const Handle(TCollection_HAsciiString)& methodName,
			   const Standard_Boolean forDeclaration = Standard_True)
{
  Standard_Boolean IsAsynchrone = MethodMap.Contains(m->FullName());

  // Here we build calls for synchronous methods
  //
  if (!IsAsynchrone) {
    Standard_Boolean                 InlineMethod;
    Handle(MS_InstMet)               im;
    Handle(MS_ClassMet)              cm;
    Handle(MS_Construc)              ct;
    Handle(MS_Param)                 retType;
    Handle(TCollection_HAsciiString) MetTemplate,
                                     theArgList,
                                     ConTemplate;

    MetTemplate = api->GetVariableValue(VMethodHeader);
    ConTemplate = api->GetVariableValue(VConstructorHeader);
    
    // here we process all the common attributes of methods
    //
    api->AddVariable(VMethodName,methodName->ToCString());
    api->AddVariable(VVirtual,"");
    
    // no inline method in c++ client
    //
    InlineMethod = Standard_False;
    api->AddVariable(VIsInline,"no");
    
    // it s returning const ?
    //
    if (m->IsConstReturn()) {
      api->AddVariable(VRetSpec,"const");
    }
    else {
      api->AddVariable(VRetSpec,"");
    }
    
    // no ref return in c++ client
    //
    api->AddVariable(VAnd,"");
    
    theArgList = CPPClient_BuildParameterList(aMeta,m->Params(),forDeclaration);
    
    if (theArgList == CPPClient_ErrorArgument) {
      WarningMsg() << "CPPClient" << "Bad argument type in method (pointer or imported type) " << m->FullName() << endm;
      WarningMsg() << "CPPClient" << "Method : " << m->FullName() << " not exported." << endm;
      api->AddVariable(VMethod,CPPClient_ErrorArgument->ToCString());
      return;
    }
    
    if (m->IsKind(STANDARD_TYPE(MS_Construc)) || m->IsKind(STANDARD_TYPE(MS_ClassMet)) || m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
      if (!theArgList->IsEmpty()) {
	theArgList->AssignCat(",");
      }
      else {
	theArgList->AssignCat(" ");
      }
      api->AddVariable("%Arguments",theArgList->ToCString());
      if (forDeclaration) {
	api->Apply("%Arguments","ConstructorClientDec");
      }
      else {
	api->Apply("%Arguments","ConstructorClientDef");
      }
      api->AddVariable("%Arguments",api->GetVariableValue("%Arguments")->ToCString());
    }
    else {
      api->AddVariable("%Arguments",theArgList->ToCString());
    }
    
    // it s returning a type or void ?
    //
    retType = m->Returns();
    
    if (!retType.IsNull()) {
      Handle(TCollection_HAsciiString) returnT = CPPClient_BuildType(aMeta,retType->TypeName());
      
      if (returnT == CPPClient_ErrorArgument) {
	WarningMsg() << "CPPClient" << "Return type (pointer or imported type) of " << m->FullName() << " not exportable." << endm;
	WarningMsg() << "CPPClient" << "Method : " << m->FullName() << " not exported." << endm;
	api->AddVariable(VMethod,CPPClient_ErrorArgument->ToCString());
	return;
      }
      else {
	api->AddVariable(VReturn,returnT->ToCString());
      }
    }
    else {
      api->AddVariable(VReturn,"void");
    }
    
    // now the specials attributes
    //
    // instance methods
    //
    if (m->IsKind(STANDARD_TYPE(MS_InstMet))) {
      im = *((Handle(MS_InstMet)*)&m);
      
      api->AddVariable(VIsCreateMethod,"no");
      
      if (!im->IsDeferred() || !forDeclaration) {
	if (!im->IsStatic() && forDeclaration) {
	  api->AddVariable(VVirtual,"Standard_EXPORT virtual");
	}
	else if (im->IsStatic() && forDeclaration) {
		api->AddVariable(VVirtual,"Standard_EXPORT");
	}

	if (im->IsConst()) {
	  api->AddVariable(VMetSpec,"const");
	}
	else {
	  api->AddVariable(VMetSpec,"");
	}
      }
      // no pure vir classes in stubs
      //
      else if (forDeclaration) {
	api->AddVariable(VVirtual,"virtual");
	
	if (im->IsConst()) {
	  api->AddVariable(VMetSpec,"const");
	}
	else {
	  api->AddVariable(VMetSpec,"");
	}
      }
      
      api->Apply(VMethod,MetTemplate->ToCString());
    }
    //
    // class methods
    //
    else if (m->IsKind(STANDARD_TYPE(MS_ClassMet))) {
      api->AddVariable(VIsCreateMethod,"no");
      api->AddVariable(VMetSpec,"");
      if (forDeclaration) {
	api->AddVariable(VVirtual,"Standard_EXPORT static");
      }
      else {
	api->AddVariable(VVirtual,"");
      }
      api->Apply(VMethod,MetTemplate->ToCString());
    }
    //
    // constructors
    //
    else if (m->IsKind(STANDARD_TYPE(MS_Construc))) {
      api->AddVariable(VIsCreateMethod,"yes");
      api->Apply(VMethod,ConTemplate->ToCString());
    }
    //
    // package methods
    //
    else if (m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
      api->AddVariable(VIsCreateMethod,"no");
      api->AddVariable(VMetSpec,"");
      if (forDeclaration) {
	api->AddVariable(VVirtual,"Standard_EXPORT static");
      }
      
      api->Apply(VMethod,MetTemplate->ToCString());
    }  
  }
  // Here we build calls for asynchronous methods
  //
  else {
    Handle(TCollection_HAsciiString) tmp;

    CPPClient_BuildAsynchronousMethod(aMeta,api,m,methodName,Standard_False,forDeclaration);

    if (!api->GetVariableValue("%Method")->IsSameString(CPPClient_ErrorArgument)) {    
      tmp = api->GetVariableValue(VMethod);
      tmp->AssignCat(";");
      CPPClient_BuildAsynchronousMethod(aMeta,api,m,methodName,Standard_True,forDeclaration);
      tmp->AssignCat(api->GetVariableValue(VMethod));
      api->AddVariable(VMethod,tmp->ToCString());
    }
  }
}

//void CPPClient_ClassTypeMgt(const Handle(MS_MetaSchema)& aMeta,
void CPPClient_ClassTypeMgt(const Handle(MS_MetaSchema)& ,
			    const Handle(EDL_API)& api,
			    const Handle(MS_Class)& aClass,
			    const Standard_CString var)
{
  Handle(TColStd_HSequenceOfHAsciiString) inh = aClass->GetFullInheritsNames();
  Standard_Integer                        i;
  Handle(TCollection_HAsciiString)        ichar;
  Handle(TCollection_HAsciiString)        rTypeName;
  Handle(TCollection_HAsciiString)        str  = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString)        str1 = new TCollection_HAsciiString;

  for (i = 1; i <= inh->Length(); i++) {
    ichar = new TCollection_HAsciiString(i);

    api->AddVariable(VNb,ichar->ToCString());
    api->Apply(VNb,"TypeMgtAncestorType");

    str1->AssignCat(api->GetVariableValue(VNb));
    str1->AssignCat(",");

    rTypeName = new TCollection_HAsciiString(CPPClient_InterfaceName);
    rTypeName->AssignCat("_");
    rTypeName->AssignCat(inh->Value(i));

    api->AddVariable(VAncestors,rTypeName->ToCString());
    api->Apply("%TMgtInherits","TypeMgtAncestor");
    str->AssignCat(api->GetVariableValue("%TMgtInherits"));
  }

  rTypeName = new TCollection_HAsciiString(CPPClient_InterfaceName);
  rTypeName->AssignCat("_");
  rTypeName->AssignCat(aClass->FullName());

  api->AddVariable("%TMgtInherits",str->ToCString());
  api->AddVariable(VAncestors,str1->ToCString());
  api->AddVariable("%TMgtClass",rTypeName->ToCString());

  api->Apply(var,"TypeMgt");
}

// build the return sentence for a stub c++ method call
//
Handle(TCollection_HAsciiString) CPPClient_BuildAsynchronousReturnCode(const Handle(MS_MetaSchema)& aMeta, 
								       const Handle(EDL_API)& api, 
								       const Handle(MS_Method)& m)
{
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString;
  Handle(MS_Type)                  rtype  = aMeta->GetType(m->Returns()->TypeName());

  if (rtype->IsKind(STANDARD_TYPE(MS_Alias))) {
    Handle(TCollection_HAsciiString) parname;
    Handle(MS_Alias)                 analias = *((Handle(MS_Alias)*)&rtype);
    
    parname = analias->DeepType();
    
    if (aMeta->IsDefined(parname)) {
      rtype = aMeta->GetType(parname);
    }
    else {
      ErrorMsg() << "CPPClient" << "Type " << parname << " not defined..." << endm;
      Standard_NoSuchObject::Raise();
    }
  }
  

  if (rtype->IsKind(STANDARD_TYPE(MS_PrimType))) {
    api->AddVariable("%PrimName",rtype->Name()->ToCString());
    api->Apply("%Return","AsyncMethodReturnPrim");
  }
  else if (rtype->IsKind(STANDARD_TYPE(MS_Enum))) {
    api->AddVariable("%PrimName",rtype->FullName()->ToCString());
    api->Apply("%Return","AsyncMethodReturnEnum");
  }
  else if (rtype->IsKind(STANDARD_TYPE(MS_StdClass)) && !rtype->IsKind(STANDARD_TYPE(MS_Error))) {
    Handle(MS_StdClass) aClass = *((Handle(MS_StdClass)*)&rtype);
    Handle(TCollection_HAsciiString) cname = new TCollection_HAsciiString(CPPClient_InterfaceName);

    cname->AssignCat("_");
    cname->AssignCat(aClass->FullName());
    api->AddVariable("%PrimName",cname->ToCString());

    if (aClass->IsTransient() || aClass->IsPersistent()) {
      if (m->IsKind(STANDARD_TYPE(MS_ClassMet)) || m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
	api->Apply("%Return","AsyncStaticMethodReturnHandle");
      }
      else {
	api->Apply("%Return","AsyncMethodReturnHandle");
      }
    }
    else {
      if (m->IsKind(STANDARD_TYPE(MS_ClassMet)) || m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
	api->Apply("%Return","AsyncStaticMethodReturnValue");
      }
      else {
	api->Apply("%Return","AsyncMethodReturnValue");
      }
    }
  }

  result->AssignCat(api->GetVariableValue("%Return"));

  return result;
}

// build the return sentence for a stub c++ method call
//
Handle(TCollection_HAsciiString) CPPClient_BuildReturnCode(const Handle(MS_MetaSchema)& aMeta, 
							   const Handle(EDL_API)& api, 
							   const Handle(MS_Method)& m)
{
  Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString;
  Handle(MS_Type)                  rtype  = aMeta->GetType(m->Returns()->TypeName());

  if (rtype->IsKind(STANDARD_TYPE(MS_Alias))) {
    Handle(TCollection_HAsciiString) parname;
    Handle(MS_Alias)                 analias = *((Handle(MS_Alias)*)&rtype);
    
    parname = analias->DeepType();
    
    if (aMeta->IsDefined(parname)) {
      rtype = aMeta->GetType(parname);
    }
    else {
      ErrorMsg() << "CPPClient" << "Type " << parname << " not defined..." << endm;
      Standard_NoSuchObject::Raise();
    }
  }
  

  if (rtype->IsKind(STANDARD_TYPE(MS_PrimType))) {
    api->AddVariable("%PrimName",rtype->Name()->ToCString());
    api->Apply("%Return","MethodReturnPrim");
  }
  else if (rtype->IsKind(STANDARD_TYPE(MS_Enum))) {
    api->AddVariable("%PrimName",rtype->FullName()->ToCString());
    api->Apply("%Return","MethodReturnEnum");
  }
  else if (rtype->IsKind(STANDARD_TYPE(MS_StdClass)) && !rtype->IsKind(STANDARD_TYPE(MS_Error))) {
    Handle(MS_StdClass) aClass = *((Handle(MS_StdClass)*)&rtype);
    Handle(TCollection_HAsciiString) cname = new TCollection_HAsciiString(CPPClient_InterfaceName);

    cname->AssignCat("_");
    cname->AssignCat(aClass->FullName());
    api->AddVariable("%PrimName",cname->ToCString());

    if (aClass->IsTransient() || aClass->IsPersistent()) {
      if (m->IsKind(STANDARD_TYPE(MS_ClassMet)) || m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
	api->Apply("%Return","StaticMethodReturnHandle");
      }
      else {
	api->Apply("%Return","MethodReturnHandle");
      }
    }
    else {
      if (m->IsKind(STANDARD_TYPE(MS_ClassMet)) || m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
	api->Apply("%Return","StaticMethodReturnValue");
      }
      else {
	api->Apply("%Return","MethodReturnValue");
      }
    }
  }

  result->AssignCat(api->GetVariableValue("%Return"));

  return result;
}

// build an asynchronous method call for stub c++
//
void CPPClient_AsynchronousMethodBuilder(const Handle(MS_MetaSchema)& aMeta, 
					 const Handle(EDL_API)& api, 
					 const Handle(TCollection_HAsciiString)& className,
					 const Handle(MS_Method)& m,
					 const Handle(TCollection_HAsciiString)& methodName,
//					 const Standard_Boolean mustAddAncestorConstrucCall)
					 const Standard_Boolean )
{
  Handle(TCollection_HAsciiString) metname = new TCollection_HAsciiString(CPPClient_InterfaceName);
  Handle(TCollection_HAsciiString) metbody = new TCollection_HAsciiString;
  Standard_CString                 headerTemplate = NULL;
  
  metname->AssignCat("_");
  metname->AssignCat(className);
  metname->AssignCat("::");
  metname->AssignCat(methodName);
  
  if (m->IsKind(STANDARD_TYPE(MS_InstMet))) {
    headerTemplate = "AsyncInstMethodDec";
    api->AddVariable("%WhatEngine","Engine()");
    api->AddVariable("%MethodName",methodName->ToCString());
  }
  else if (m->IsKind(STANDARD_TYPE(MS_ClassMet))) {
    headerTemplate = "AsyncClassMethodDec";
    api->AddVariable("%ClassName",className->ToCString());
    api->AddVariable("%WhatEngine","_aCurrent");
    api->AddVariable("%MethodName",methodName->ToCString());
  }
  else if (m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
    headerTemplate = "AsyncPackMethodDec";
    api->AddVariable("%ClassName",className->ToCString());
    api->AddVariable("%WhatEngine","_aCurrent");
    api->AddVariable("%MethodName",methodName->ToCString());
  }
  
  api->Apply("%Method",headerTemplate);
  
  metbody->AssignCat(api->GetVariableValue("%Method"));
  
  Handle(MS_HArray1OfParam) aSeqP = m->Params();
  Standard_Integer            i;
  
  if(!aSeqP.IsNull()) {
    for (i = 1; i <= aSeqP->Length(); i++) {
      api->AddVariable("%ArgName",aSeqP->Value(i)->Name()->ToCString());
      Handle(MS_Type) t = aMeta->GetType(aSeqP->Value(i)->TypeName());
      
      if (aSeqP->Value(i)->IsOut()) {
	if (t->IsKind(STANDARD_TYPE(MS_Enum))) {
	  Handle(TCollection_HAsciiString) n = new TCollection_HAsciiString("(Standard_Integer&)");
	  n->AssignCat(aSeqP->Value(i)->Name());
	  api->AddVariable("%ArgName",n->ToCString());
	}
	api->Apply("%Method","AsyncMethodArgOut");
      }
      else {
	if (t->IsKind(STANDARD_TYPE(MS_Enum))) {
	  Handle(TCollection_HAsciiString) n = new TCollection_HAsciiString("(Standard_Integer)");
	  n->AssignCat(aSeqP->Value(i)->Name());
	  api->AddVariable("%ArgName",n->ToCString());
	}
	api->Apply("%Method","AsyncMethodArg");
      }
    
      metbody->AssignCat(api->GetVariableValue("%Method"));
    }
  }
  metbody->AssignCat(" _aMethod->ExecuteAsynchrone();\n");

  CPPClient_BuildAsynchronousMethod(aMeta,api,m,metname,Standard_False,Standard_False);
  api->AddVariable(VConstructorHeader,"ConstructorHeader");
  
  if (!api->GetVariableValue("%Method")->IsSameString(CPPClient_ErrorArgument)) {
    api->AddVariable("%MBody",metbody->ToCString());
    api->Apply("%Method","MethodTemplateDef");
  }

  Handle(TCollection_HAsciiString) metGetArg;

  metGetArg = api->GetVariableValue("%Method");
  
  CPPClient_BuildAsynchronousMethod(aMeta,api,m,metname,Standard_True,Standard_False);

  api->AddVariable("%ReturnBody","");

  if (!api->GetVariableValue("%Method")->IsSameString(CPPClient_ErrorArgument)) {
    if (!m->Returns().IsNull()) {
      metbody = CPPClient_BuildAsynchronousReturnCode(aMeta,api,m);
      api->AddVariable("%ReturnBody",metbody->ToCString());
    }
    else {
      api->Apply("%ReturnBody","AsyncMethodReturnVoid");
    }
  }
  
  api->Apply("%Method","AsyncGetArg");

  metGetArg->AssignCat(api->GetVariableValue("%Method"));

  api->AddVariable("%Method",metGetArg->ToCString());

  api->AddVariable(VConstructorHeader,"ConstructorHeader");
}

// build a method call for stub c++
//
void CPPClient_MethodBuilder(const Handle(MS_MetaSchema)& aMeta, 
			     const Handle(EDL_API)& api, 
			     const Handle(TCollection_HAsciiString)& className,
			     const Handle(MS_Method)& m,
			     const Handle(TCollection_HAsciiString)& methodName,
			     const Standard_Boolean mustAddAncestorConstrucCall)
{
  Standard_Boolean IsAsynchrone = MethodMap.Contains(m->FullName());

  if (!IsAsynchrone) {
    Handle(TCollection_HAsciiString) metname = new TCollection_HAsciiString(CPPClient_InterfaceName);
    Handle(TCollection_HAsciiString) metbody = new TCollection_HAsciiString;
    Standard_CString                 headerTemplate = NULL;

    metname->AssignCat("_");
    metname->AssignCat(className);
    metname->AssignCat("::");
    
    if (m->IsKind(STANDARD_TYPE(MS_Construc))) {
      metname->AssignCat(CPPClient_InterfaceName);
      metname->AssignCat("_");
      metname->AssignCat(className);
    }
    else {
      metname->AssignCat(methodName);
    }
    
    if (m->IsKind(STANDARD_TYPE(MS_InstMet))) {
      headerTemplate = "InstMethodDec";
      api->AddVariable("%MethodName",methodName->ToCString());
    }
    else if (m->IsKind(STANDARD_TYPE(MS_ClassMet))) {
      headerTemplate = "ClassMethodDec";
      api->AddVariable("%ClassName",className->ToCString());
      api->AddVariable("%MethodName",methodName->ToCString());
    }
    else if (m->IsKind(STANDARD_TYPE(MS_Construc))) {
      headerTemplate = "CreateMethodDec";
      api->AddVariable("%ClassName",className->ToCString());
      api->AddVariable("%MethodName",className->ToCString());
    }
    else if (m->IsKind(STANDARD_TYPE(MS_ExternMet))) {
      headerTemplate = "PackMethodDec";
      api->AddVariable("%ClassName",className->ToCString());
      api->AddVariable("%MethodName",methodName->ToCString());
    }
    
    api->Apply("%Method",headerTemplate);
    
    metbody->AssignCat(api->GetVariableValue("%Method"));
    
    Handle(MS_HArray1OfParam) aSeqP = m->Params();
    Standard_Integer            i;
    
    if(!aSeqP.IsNull()) {
      for (i = 1; i <= aSeqP->Length(); i++) {
	api->AddVariable("%ArgName",aSeqP->Value(i)->Name()->ToCString());
	Handle(MS_Type) t = aMeta->GetType(aSeqP->Value(i)->TypeName());

	if (aSeqP->Value(i)->IsOut()) {
	  if (t->IsKind(STANDARD_TYPE(MS_Enum))) {
	    Handle(TCollection_HAsciiString) n = new TCollection_HAsciiString("(Standard_Integer&)");
	    n->AssignCat(aSeqP->Value(i)->Name());
	    api->AddVariable("%ArgName",n->ToCString());
	  }
	  api->Apply("%Method","MethodArgOut");
	}
	else {
	  if (t->IsKind(STANDARD_TYPE(MS_Enum))) {
	    Handle(TCollection_HAsciiString) n = new TCollection_HAsciiString("(Standard_Integer)");
	    n->AssignCat(aSeqP->Value(i)->Name());
	    api->AddVariable("%ArgName",n->ToCString());
	  }
	  api->Apply("%Method","MethodArg");
	}
      
	metbody->AssignCat(api->GetVariableValue("%Method"));
      }
    }
    metbody->AssignCat(" _aMethod.Execute();\n");
    
    if (m->IsKind(STANDARD_TYPE(MS_Construc))) {
      Handle(MS_Type)  tmpType = aMeta->GetType(className);
      Handle(MS_Class) myClass     = *((Handle(MS_Class)*)&tmpType);
      Standard_Boolean iamHandled = myClass->IsTransient() || myClass->IsPersistent();
      
      // for handled classes we must call the superclass constructor
      //
      if (iamHandled) {
	api->Apply("%Return","CreateMethodReturnHandle");
      }
      else {
	api->Apply("%Return","CreateMethodReturnValue");
      }
      
      if (mustAddAncestorConstrucCall) {
	if (iamHandled || myClass->GetInheritsNames()->Length() == 0) {
	  api->AddVariable(VConstructorHeader,"ExternalConstructorCallAncestorHeader");
	}
	else {
	  api->AddVariable(VConstructorHeader,"ExternalValueConstructorCallAncestorHeader");
	}
      } else {
	if (iamHandled || myClass->GetInheritsNames()->Length() == 0) {
	  api->AddVariable(VConstructorHeader,"ExternalConstructorHeader");
	}
	else {
	  api->AddVariable(VConstructorHeader,"ExternalValueConstructorCallAncestorHeader");
	}
      }
      
      metbody->AssignCat(api->GetVariableValue("%Return"));
    }
    else if (!m->Returns().IsNull()) {
      metbody->AssignCat(CPPClient_BuildReturnCode(aMeta,api,m));
    }
    
    CPPClient_BuildMethod(aMeta,api,m,metname,Standard_False);
    api->AddVariable(VConstructorHeader,"ConstructorHeader");
    
    if (!api->GetVariableValue("%Method")->IsSameString(CPPClient_ErrorArgument)) {
      api->AddVariable("%MBody",metbody->ToCString());
      api->Apply("%Method","MethodTemplateDef");
    }
  }
  // ASYNCHRONOUS
  //
  else {
    CPPClient_AsynchronousMethodBuilder(aMeta,api,className,m,methodName,mustAddAncestorConstrucCall);
  }
}

// Standard extractor API : launch the extraction of C++ files
//                          from the type <aName>
// 
void CPPClient_TypeExtract(const Handle(MS_MetaSchema)& aMeta,
			   const Handle(TCollection_HAsciiString)& aName,
			   const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
			   const Handle(TCollection_HAsciiString)& outdir,
			   const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
			   const ExtractionType MustBeComplete)
{
  Handle(MS_Type)              srcType;
  Handle(MS_Package)           srcPackage;

  // before begining, we look if the entity has something to extract...
  //
  if (aMeta->IsDefined(aName)) {
    srcType   = aMeta->GetType(aName); 
  }
  else if (aMeta->IsPackage(aName)) {
    srcPackage = aMeta->GetPackage(aName);
  }
  else {
    ErrorMsg() << "CPPClient" << aName->ToCString() << " not defined..." << endm;
    Standard_NoSuchObject::Raise();
  }
  
  // ... and we load the templates
  //
  Handle(EDL_API)     api;

  // Package Extraction
  //
  if (!srcPackage.IsNull()) {
    if (srcPackage->Methods()->Length() > 0) {
      Handle(MS_HSequenceOfExternMet) aSeqMet = new MS_HSequenceOfExternMet;

      api = CPPClient_LoadTemplate(edlsfullpath,outdir);

      if (MustBeComplete == CPPClient_SEMICOMPLETE) {
	Standard_Integer i;

	for (i = 1; i <= SeqOfExternMet->Length(); i++) {
	  if (aName->IsSameString(SeqOfExternMet->Value(i)->Package())) {
	    aSeqMet->Append(SeqOfExternMet->Value(i));
	  }
	}
      }

      CPPClient_Package(aMeta,api,srcPackage,outfile,MustBeComplete,aSeqMet);
    }
    else {
      return;
    }
  }
  else if (aName->IsSameString(MS::GetTransientRootName()) || aName->IsSameString(MS::GetPersistentRootName())) {
    Handle(TCollection_HAsciiString) aHandleFile = new TCollection_HAsciiString(outdir);
    Standard_CString                 CXX,HXX;

    if (aName->IsSameString(MS::GetTransientRootName())) {
      CXX = "TransientRootClientCXX";
      HXX = "TransientRootClientHXX";
    }
    else {
      CXX = "PersistentRootClientCXX";
      HXX = "PersistentRootClientHXX";
    }

    aHandleFile->AssignCat("Handle_");
    aHandleFile->AssignCat(CPPClient_InterfaceName);
    aHandleFile->AssignCat("_");
    aHandleFile->AssignCat(aName);
    aHandleFile->AssignCat(".hxx");
    
    outfile->Append(aHandleFile);
    
    api = CPPClient_LoadTemplate(edlsfullpath,outdir);

    CPPClient_TransientHandle(api,aName,CPPClient_TransientRootName(),aHandleFile);

    aHandleFile = new TCollection_HAsciiString(outdir);
    
    aHandleFile->AssignCat(CPPClient_InterfaceName);
    aHandleFile->AssignCat("_");
    aHandleFile->AssignCat(aName);
    aHandleFile->AssignCat("_client.cxx");
    api->Apply("%outClass",CXX);
    outfile->Append(aHandleFile);

    // ...now we write the result
    //
    api->OpenFile("HTFile",aHandleFile->ToCString());
    api->WriteFile("HTFile","%outClass");
    api->CloseFile("HTFile");

    aHandleFile = new TCollection_HAsciiString(outdir);
    
    aHandleFile->AssignCat(CPPClient_InterfaceName);
    aHandleFile->AssignCat("_");
    aHandleFile->AssignCat(aName);
    aHandleFile->AssignCat(".hxx");
    api->Apply("%outClass",HXX);
    outfile->Append(aHandleFile);

    // ...now we write the result
    //
    api->OpenFile("HTFile",aHandleFile->ToCString());
    api->WriteFile("HTFile","%outClass");
    api->CloseFile("HTFile");
  }
  else if (aName->IsSameString(CPPClient_MemoryRootName())) {
     Handle(TCollection_HAsciiString) aHandleFile = new TCollection_HAsciiString(outdir),
                                      ancestorName;
     Standard_CString                 CXX,HXX;
     
     CXX = "MemoryRootClientCXX";
     HXX = "MemoryRootClientHXX";
     
     aHandleFile->AssignCat("Handle_");
     aHandleFile->AssignCat(CPPClient_InterfaceName);
     aHandleFile->AssignCat("_");
     aHandleFile->AssignCat(aName);
     aHandleFile->AssignCat(".hxx");
     
     outfile->Append(aHandleFile);
     
     api = CPPClient_LoadTemplate(edlsfullpath,outdir);
     
     CPPClient_TransientHandle(api,aName,MS::GetTransientRootName(),aHandleFile);
     
     aHandleFile = new TCollection_HAsciiString(outdir);
     
     aHandleFile->AssignCat(CPPClient_InterfaceName);
     aHandleFile->AssignCat("_");
     aHandleFile->AssignCat(aName);
     aHandleFile->AssignCat("_client.cxx");
     api->Apply("%outClass",CXX);
     outfile->Append(aHandleFile);
     
     // ...now we write the result
     //
     api->OpenFile("HTFile",aHandleFile->ToCString());
     api->WriteFile("HTFile","%outClass");
     api->CloseFile("HTFile");
     
     aHandleFile = new TCollection_HAsciiString(outdir);
     
     aHandleFile->AssignCat(CPPClient_InterfaceName);
     aHandleFile->AssignCat("_");
     aHandleFile->AssignCat(aName);
     aHandleFile->AssignCat(".hxx");
     api->Apply("%outClass",HXX);
     outfile->Append(aHandleFile);
     
     // ...now we write the result
     //
     api->OpenFile("HTFile",aHandleFile->ToCString());
     api->WriteFile("HTFile","%outClass");
     api->CloseFile("HTFile");
   }
  else if (aName->IsSameString(MS::GetStorableRootName())) {
    Handle(TCollection_HAsciiString) aHandleFile = new TCollection_HAsciiString(outdir);

    api = CPPClient_LoadTemplate(edlsfullpath,outdir);
    
    aHandleFile = new TCollection_HAsciiString(outdir);
    
    aHandleFile->AssignCat(CPPClient_InterfaceName);
    aHandleFile->AssignCat("_");
    aHandleFile->AssignCat(aName);
    aHandleFile->AssignCat("_client.cxx");
    api->Apply("%outClass","StorableRootClientCXX");
    outfile->Append(aHandleFile);

    // ...now we write the result
    //
    api->OpenFile("HTFile",aHandleFile->ToCString());
    api->WriteFile("HTFile","%outClass");
    api->CloseFile("HTFile");

    aHandleFile = new TCollection_HAsciiString(outdir);
    
    aHandleFile->AssignCat(CPPClient_InterfaceName);
    aHandleFile->AssignCat("_");
    aHandleFile->AssignCat(aName);
    aHandleFile->AssignCat(".hxx");
    api->Apply("%outClass","StorableRootClientHXX");
    outfile->Append(aHandleFile);

    // ...now we write the result
    //
    api->OpenFile("HTFile",aHandleFile->ToCString());
    api->WriteFile("HTFile","%outClass");
    api->CloseFile("HTFile");
  }
  // Extraction of Classes
  //
  else if (srcType->IsKind(STANDARD_TYPE(MS_StdClass)) && !srcType->IsKind(STANDARD_TYPE(MS_GenClass)) && !srcType->IsKind(STANDARD_TYPE(MS_InstClass))) {
    Handle(MS_StdClass) aClass = *((Handle(MS_StdClass)*)&srcType);
    
    if (aClass->IsGeneric()) {
      return;
    }

    Handle(MS_HSequenceOfMemberMet)  aSeqMet = new MS_HSequenceOfMemberMet;

    api = CPPClient_LoadTemplate(edlsfullpath,outdir);

    if (MustBeComplete == CPPClient_SEMICOMPLETE) {
      Standard_Integer i;
      
      for (i = 1; i <= SeqOfMemberMet->Length(); i++) {
	if (aName->IsSameString(SeqOfMemberMet->Value(i)->Class())) {
	  aSeqMet->Append(SeqOfMemberMet->Value(i));
	}
      }
    }

    // Transient classes
    //
    if (aClass->IsTransient() || aClass->IsPersistent()) {
      Handle(TCollection_HAsciiString) aHandleFile = new TCollection_HAsciiString(outdir);
      
      aHandleFile->AssignCat("Handle_");
      aHandleFile->AssignCat(CPPClient_InterfaceName);
      aHandleFile->AssignCat("_");
      aHandleFile->AssignCat(aName);
      aHandleFile->AssignCat(".hxx");
      
      outfile->Append(aHandleFile);

      CPPClient_TransientHandle(api,aName,aClass->GetInheritsNames()->Value(1),aHandleFile);
      CPPClient_TransientClass(aMeta,api,aClass,outfile,MustBeComplete,aSeqMet);
    }
    // MPV classes
    //
    else {
      CPPClient_MPVClass(aMeta,api,aClass,outfile,MustBeComplete,aSeqMet);
    }
  }
  else if (srcType->IsKind(STANDARD_TYPE(MS_Enum))) {
    api = CPPClient_LoadTemplate(edlsfullpath,outdir);

    Handle(MS_Enum) theEnum = *((Handle(MS_Enum)*)&srcType);

    CPPClient_Enum(aMeta,api,theEnum,outfile);
  }
}

void CPPClient_Extract(const Handle(MS_MetaSchema)& aMeta,
		       const Handle(TCollection_HAsciiString)& aTypeName,
		       const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
		       const Handle(TCollection_HAsciiString)& outdir,
		       const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
		       const Standard_CString Mode)
{  
  if (aMeta->IsDefined(aTypeName) || aMeta->IsPackage(aTypeName)) {
    ExtractionType theMode = CPPClient_COMPLETE;
    
    if (strcmp(Mode,"CPPClient_COMPLETE") == 0)          {theMode = CPPClient_COMPLETE;}
    else if (strcmp(Mode,"CPPClient_INCOMPLETE") == 0)   {theMode = CPPClient_INCOMPLETE;}
    else if (strcmp(Mode,"CPPClient_SEMICOMPLETE") == 0) {theMode = CPPClient_SEMICOMPLETE;}
    else {
      ErrorMsg() << "CPPClient" << "Unknown extraction mode:" << Mode << endm;
      Standard_NoSuchObject::Raise();
    }
    
    CPPClient_TypeExtract(aMeta,aTypeName,edlsfullpath,outdir,outfile,theMode);
  }
  else {
    ErrorMsg() << "CPPClient" << "Type " << aTypeName << " not defined..." << endm;
    Standard_NoSuchObject::Raise();
  }
}

