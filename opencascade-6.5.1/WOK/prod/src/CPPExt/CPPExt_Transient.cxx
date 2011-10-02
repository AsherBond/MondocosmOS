// CLE
//    
// 10/1995
//
#include <MS.hxx>

#include <EDL_API.hxx>

#include <MS_MetaSchema.hxx>

#include <MS_Class.hxx>
#include <MS_GenClass.hxx>
#include <MS_InstClass.hxx>
#include <MS_Package.hxx>
#include <MS_Error.hxx>
#include <MS_Imported.hxx>

#include <MS_InstMet.hxx>
#include <MS_ClassMet.hxx>
#include <MS_Construc.hxx>
#include <MS_ExternMet.hxx>
 
#include <MS_Param.hxx>
#include <MS_Field.hxx>
#include <MS_GenType.hxx>
#include <MS_Enum.hxx>
#include <MS_PrimType.hxx>

#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_HSequenceOfExternMet.hxx>
#include <MS_HSequenceOfParam.hxx>
#include <MS_HSequenceOfField.hxx>
#include <MS_HSequenceOfGenType.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfInteger.hxx>

#include <TCollection_HAsciiString.hxx>

#include <Standard_NoSuchObject.hxx>

#include <CPPExt_Define.hxx>
#include <WOKTools_Messages.hxx>

// Extraction of a transient handle
//
void CPP_TransientHandle(const Handle(EDL_API)& api,
			 const Handle(TCollection_HAsciiString)& aClass,
			 const Handle(TCollection_HAsciiString)& aMother,
			 const Handle(TCollection_HAsciiString)& aFileName)
{
  // we create the handle...
  //
  api->AddVariable("%HTName",aClass->ToCString());
  api->AddVariable("%HTInherits",aMother->ToCString());
  api->Apply("%HTHandle","HandleTransient");

  // ...now we write the result
  //
  api->OpenFile("HTFile",aFileName->ToCString());
  api->WriteFile("HTFile","%HTHandle");
  api->CloseFile("HTFile");
}

// Extraction of a transient .ixx .jxx and _0.cxx
//   the supplement variable is used for non inline methods generated 
//   by the extractor like destructor (added to .ixx ans _0.cxx
//
void CPP_TransientDerivated(const Handle(MS_MetaSchema)& aMeta,
			    const Handle(EDL_API)& api,
			    const Handle(MS_Class)& aClass,			    
			    const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
			    const Handle(TColStd_HSequenceOfHAsciiString)& inclist,
			    const Handle(TColStd_HSequenceOfHAsciiString)& supplement)
{
  Standard_Integer                        i;
  Handle(TCollection_HAsciiString)        aFileName = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString)        result    = new TCollection_HAsciiString;
  Handle(MS_StdClass)                     theClass  = Handle(MS_StdClass)::DownCast(aClass);

  // we do this only on standard classes (not on inst classes)
  //
  if (theClass.IsNull()) return;

  api->AddVariable(VClass,aClass->FullName()->ToCString());
  //api->AddVariable(VClassComment,aClass->Comment()->ToCString());

  api->AddVariable(VSuffix,"hxx");
  
  for (i = 1; i <= inclist->Length(); i++) {
    api->AddVariable(VIClass,inclist->Value(i)->ToCString());
    api->Apply(VoutClass,"Include");
    result->AssignCat(api->GetVariableValue(VoutClass));
  }

  if (theClass->GetMyCreator().IsNull()) {
    // include the hxx of me
    //
    api->AddVariable(VIClass,aClass->FullName()->ToCString());
    api->Apply(VoutClass,"Include");
    result->AssignCat(api->GetVariableValue(VoutClass));
    
    api->AddVariable(VoutClass,result->ToCString());

    aFileName->AssignCat(api->GetVariableValue(VFullPath));
    aFileName->AssignCat(aClass->FullName());
    aFileName->AssignCat(".jxx");
  
    CPP_WriteFile(api,aFileName,VoutClass);
    result->Clear();
    outfile->Append(aFileName);
  }

  CPP_ClassTypeMgt(aMeta,api,aClass,VTypeMgt);
  
  aFileName = new TCollection_HAsciiString(api->GetVariableValue(VFullPath));
  aFileName->AssignCat(aClass->FullName());

  if (theClass->GetMyCreator().IsNull()) {
    aFileName->AssignCat(".ixx");
  }
  else {
    aFileName->AssignCat("_0.cxx");
  }

  // Supplement
  //
  if (theClass->GetMyCreator().IsNull()) {
    result->Clear();
  }

  for (i = 1; i <= supplement->Length(); i++) {
    result->AssignCat(supplement->Value(i));
  }
  
  api->AddVariable(VSupplement,result->ToCString());
  
  // Methods
  //
  result->Clear();
  
  api->AddVariable(VIClass,MS::GetTransientRootName()->ToCString());
  api->Apply(VMethods,"DownCast");    
  result->AssignCat(api->GetVariableValue(VMethods));
  
  api->Apply(VMethods,"DynamicType");
  result->AssignCat(api->GetVariableValue(VMethods));
  
  api->AddVariable(VIClass,aClass->GetInheritsNames()->Value(1)->ToCString());
  api->Apply(VMethods,"IsKind");
  result->AssignCat(api->GetVariableValue(VMethods));
  
  api->Apply(VMethods,"FullEmptyHandleDestructorTemplate");
  result->AssignCat(api->GetVariableValue(VMethods));

  if (!theClass->GetMyCreator().IsNull()) {
    CPP_GenericDefine(aMeta,api,theClass->GetMyCreator(),VTICDefines,VTICUndefines,Standard_True);
    result->AssignCat(api->GetVariableValue(VTICDefines));
    api->AddVariable(VSuffix,"gxx");
    if (theClass->GetMother().IsNull()) {
      api->AddVariable(VIClass,theClass->GetMyCreator()->GenClass()->ToCString());
    }
    else {
      api->AddVariable(VIClass,theClass->GetMother()->ToCString());
    }
    api->Apply(VMethods,"IncludeNoSafe");
    result->AssignCat(api->GetVariableValue(VMethods));
  }

  if (theClass->GetMyCreator().IsNull()) {
    api->AddVariable(VSuffix,"jxx");
  }
  else {
    api->AddVariable(VSuffix,"hxx");
  }

  api->AddVariable(VMethods,result->ToCString());
  api->Apply(VoutClass,"TransientIxx");
  
  CPP_WriteFile(api,aFileName,VoutClass); 
  
  outfile->Append(aFileName);
}


// Extraction of a transient class (inst or std)
//
void CPP_TransientClass(const Handle(MS_MetaSchema)& aMeta,
			const Handle(EDL_API)& api,
			const Handle(MS_Class)& aClass,
			const Handle(TColStd_HSequenceOfHAsciiString)& outfile)
{
  Handle(MS_StdClass) theClass = Handle(MS_StdClass)::DownCast(aClass);

  if (!theClass.IsNull()) {
    Standard_Integer                        i;

    Handle(MS_HSequenceOfMemberMet)         methods    = theClass->GetMethods();
    Handle(MS_Method)                       friendmethod;
    Handle(TCollection_HAsciiString)        publics    = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString)        protecteds = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString)        privates   = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString)        SuppMethod = new TCollection_HAsciiString;

    Handle(TColStd_HSequenceOfHAsciiString) Supplement = new TColStd_HSequenceOfHAsciiString;

    Standard_Boolean                        HasInlineMethod = Standard_False,
                                            HasDestructor   = Standard_False;


    api->AddVariable(VTICIncludes,"");
    api->AddVariable(VTICPublicfriends,"");
    api->AddVariable(VTICProtectedfields,"");
    api->AddVariable(VTICPrivatefriends,"");
    api->AddVariable(VTICDefines,"");
    api->AddVariable(VTICInlineIncludes,"");
    api->AddVariable(VTICUndefines,"");
    api->AddVariable(VTICPrivatefriends,"");
    api->AddVariable(VTICPrivatefields,"");
    api->AddVariable(VSuffix,"");
    api->AddVariable(VTICSuppMethod,"");

    api->AddVariable(VClass,theClass->FullName()->ToCString());
    api->AddVariable(VClassComment,theClass->Comment()->ToCString());

    api->AddVariable(VInherits,theClass->GetInheritsNames()->Value(1)->ToCString());

    for (i = 1; i <= theClass->GetFriendsNames()->Length(); i++) {
      publics->AssignCat("friend ");
      api->AddVariable(VIClass,theClass->GetFriendsNames()->Value(i)->ToCString());
      api->Apply(VTICPublicfriends,"ShortDec");
      publics->AssignCat(api->GetVariableValue(VTICPublicfriends));
    }
    
    if (!CPP_SetFriendMethod(aMeta,api,theClass->GetFriendMets(),publics)) {
      ErrorMsg() << "CPPExt" << "a friend method was not found..." << endm;
      Standard_NoSuchObject::Raise();
    }

    api->AddVariable(VTICPublicfriends,publics->ToCString());
    
    publics->Clear();

    // extraction of the methods
    //
    Handle(TCollection_HAsciiString) aliasMethod;

    for (i = 1; i <= methods->Length(); i++) {
      aliasMethod.Nullify();

      if (methods->Value(i)->IsInline()) {
	HasInlineMethod = Standard_True;
      }

      // if the class has no destructor we give it
      //
      if (methods->Value(i)->IsDestructor()) {
	HasDestructor = Standard_True;
      }

      if (!methods->Value(i)->IsAlias().IsNull() || methods->Value(i)->IsDestructor()) {
	aliasMethod = CPP_BuildAliasMethod(aMeta,api,methods->Value(i));
      }
      
      // Function Call c++ comment :
      //     it s must be in the _0.cxx or ixx file
      //     so we add it in the supplement sequence
      //
      if (methods->Value(i)->IsFunctionCall()) {
	SuppMethod->AssignCat(CPP_BuildAliasMethod(aMeta,api,methods->Value(i)));
      }

      CPP_BuildMethod(aMeta,api,methods->Value(i),methods->Value(i)->Name());
      if (  !methods -> Value ( i ) -> IsInline ()  )
        api->Apply(VMethod,"MethodTemplateDec");
      else
        api->Apply(VMethod,"MethodTemplateDecInlineWNT" );

      if (methods->Value(i)->Private()) {
	privates->AssignCat(api->GetVariableValue(VMethod));
	if (!aliasMethod.IsNull()) {
	  privates->AssignCat(aliasMethod);
	}
      }
      else if ((theClass->Deferred() && methods->Value(i)->IsKind(STANDARD_TYPE(MS_Construc))) || 
	       methods->Value(i)->IsProtected())  {
	protecteds->AssignCat(api->GetVariableValue(VMethod));
	if (!aliasMethod.IsNull()) {
	  protecteds->AssignCat(aliasMethod);
	}
      } 
      else {
	publics->AssignCat(api->GetVariableValue(VMethod));
	if (!aliasMethod.IsNull()) {
	  publics->AssignCat(aliasMethod);
	}
      }
    }
    
    if (!HasDestructor) {
      api->Apply(VMethod,"EmptyDestructorTemplate");
      publics->AssignCat(api->GetVariableValue(VMethod));
    }

    api->AddVariable(VTICPublicmets,publics->ToCString());
    api->AddVariable(VTICPrivatemets,privates->ToCString());
    api->AddVariable(VTICProtectedmets,protecteds->ToCString());

    publics->Clear();
    privates->Clear();
    protecteds->Clear();

    // extraction of fields
    //
    Handle(MS_HSequenceOfField) fields =  theClass->GetFields();

    for (i = 1; i <= fields->Length(); i++) {
      if (fields->Value(i)->Protected()) {
	protecteds->AssignCat(CPP_BuildField(aMeta,fields->Value(i)));
      }
      else {
	privates->AssignCat(CPP_BuildField(aMeta,fields->Value(i)));
      }      
    }

    api->AddVariable(VTICPrivatefields,privates->ToCString());
    api->AddVariable(VTICProtectedfields,protecteds->ToCString());

    publics->Clear();
    privates->Clear();
    protecteds->Clear();

    // others inline methods and functions (ex. function call)
    //
    api->AddVariable(VTICSuppMethod,SuppMethod->ToCString());

    // include the lxx if the class have inline methods
    //
    if (HasInlineMethod) {
      Handle(MS_InstClass) aCreator = theClass->GetMyCreator();

      if (aCreator.IsNull()) {
	api->AddVariable(VIClass,theClass->FullName()->ToCString());
	api->AddVariable(VSuffix,"lxx");
	api->Apply(VTICInlineIncludes,"IncludeNoSafe");
      }
      // this part is for class created by instantiations
      //
      else {
	if (theClass->GetMother().IsNull()) {
	  api->AddVariable(VIClass,aCreator->GenClass()->ToCString());
	}
	else {
	  api->AddVariable(VIClass,theClass->GetMother()->ToCString());
	}
	api->AddVariable(VSuffix,"lxx");
	api->Apply(VTICInlineIncludes,"IncludeNoSafe");
	
	CPP_GenericDefine(aMeta,api,aCreator,VTICDefines,VTICUndefines,Standard_True);
      }
    }
    
    Handle(TColStd_HSequenceOfHAsciiString) List = new TColStd_HSequenceOfHAsciiString;
    Handle(TColStd_HSequenceOfHAsciiString) incp = new TColStd_HSequenceOfHAsciiString;
    
    CPP_UsedTypes(aMeta,theClass,List,incp);

    publics->Clear();

    api->AddVariable(VSuffix,"hxx");

    for (i = 1; i <= List->Length(); i++) {
      if (!List->Value(i)->IsSameString(theClass->FullName())) {
	api->AddVariable(VIClass,List->Value(i)->ToCString());
	api->Apply(VTICIncludes,"Include");
	publics->AssignCat(api->GetVariableValue(VTICIncludes));
      }
    }


    for (i = 1; i <= incp->Length(); i++) {
      if (!incp->Value(i)->IsSameString(theClass->FullName())) {
	api->AddVariable(VIClass,incp->Value(i)->ToCString());
	api->Apply(VTICIncludes,"ShortDec");
	publics->AssignCat(api->GetVariableValue(VTICIncludes));
      }
    }

    api->AddVariable(VTICIncludes,publics->ToCString());

    api->Apply(VoutClass,"TransientInstClass");
    
    // we write the .hxx of this class
    //
    Handle(TCollection_HAsciiString) aFile = new TCollection_HAsciiString(api->GetVariableValue(VFullPath));

    aFile->AssignCat(theClass->FullName());
    aFile->AssignCat(".hxx");

    CPP_WriteFile(api,aFile,VoutClass);

    outfile->Append(aFile);

    // .ixx or _0.cxx
    //
    if (!HasDestructor) {
      api->Apply(VMethod,"FullEmptyDestructorTemplate");
      Supplement->Append(new TCollection_HAsciiString(api->GetVariableValue(VMethod)));
    }

    CPP_TransientDerivated(aMeta,api,aClass,outfile,incp,Supplement);
  }
  else {
    ErrorMsg() << "CPPExt" << "CPP_TransientClass - the class is NULL..." << endm;
    Standard_NoSuchObject::Raise();
  }
}

