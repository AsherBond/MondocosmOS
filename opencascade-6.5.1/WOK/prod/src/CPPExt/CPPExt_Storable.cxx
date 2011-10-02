// CLE
//    
// 10/1995
//
#include <stdio.h>
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
#include <MS_HSequenceOfField.hxx>
#include <MS_HSequenceOfGenType.hxx>
#include <MS_HArray1OfParam.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_HSequenceOfInteger.hxx>

#include <TCollection_HAsciiString.hxx>

#include <Standard_NoSuchObject.hxx>

#include <CPPExt_Define.hxx>
#include <WOKTools_Messages.hxx>

// WARNING: DB Dependent functions
//
void CPP_BuildVArrayDeclarationOBJY(const Handle(MS_MetaSchema)&,const Handle(EDL_API)&,const Handle(MS_StdClass)&,const Handle(TCollection_HAsciiString)&);
void CPP_BuildVArrayFieldOBJY(const Handle(MS_MetaSchema)&,const Handle(EDL_API)&,const Handle(MS_StdClass)&,const Handle(TCollection_HAsciiString)&);

void CPP_BuildVArrayDeclarationCSFDB(const Handle(MS_MetaSchema)&,const Handle(EDL_API)&,const Handle(MS_StdClass)&,const Handle(TCollection_HAsciiString)&);
void CPP_BuildVArrayFieldCSFDB(const Handle(MS_MetaSchema)&,const Handle(EDL_API)&,const Handle(MS_StdClass)&,const Handle(TCollection_HAsciiString)&);

void CPP_BuildVArrayDeclarationOBJS(const Handle(MS_MetaSchema)&,const Handle(EDL_API)&,const Handle(MS_StdClass)&,const Handle(TCollection_HAsciiString)&);
void CPP_BuildVArrayFieldOBJS(const Handle(MS_MetaSchema)&,const Handle(EDL_API)&,const Handle(MS_StdClass)&,const Handle(TCollection_HAsciiString)&);

Handle(TCollection_HAsciiString) CPP_BuildFieldOBJY(const Handle(MS_MetaSchema)& aMeta,
						    const Handle(MS_Field)& aField);
// create a VArray dependent declaration for DBC instance
//
void CPP_BuildVArrayDeclaration(const Handle(MS_MetaSchema)& aMeta, 
			      const Handle(EDL_API)& api, 
			      const Handle(MS_StdClass)& aClass,
			      const Handle(TCollection_HAsciiString)& Result)
{
  if (!strcmp(api->GetVariableValue("%CPPEXTDBMS")->ToCString(),"OBJY")) {
    CPP_BuildVArrayDeclarationOBJY(aMeta,api,aClass,Result);
  }
  else if (!strcmp(api->GetVariableValue("%CPPEXTDBMS")->ToCString(),"OBJS")) {
    CPP_BuildVArrayDeclarationOBJS(aMeta,api,aClass,Result);
  }
  else if (!strcmp(api->GetVariableValue("%CPPEXTDBMS")->ToCString(),"CSFDB")) {
    CPP_BuildVArrayDeclarationCSFDB(aMeta,api,aClass,Result);
  }
}

// create a VArray dependent field for DBC instance
//
void CPP_BuildVArrayField(const Handle(MS_MetaSchema)& aMeta, 
			      const Handle(EDL_API)& api, 
			      const Handle(MS_StdClass)& aClass,
			      const Handle(TCollection_HAsciiString)& Result)
{
  if (!strcmp(api->GetVariableValue("%CPPEXTDBMS")->ToCString(),"OBJY")) {
    CPP_BuildVArrayFieldOBJY(aMeta,api,aClass,Result);    
  }
  else if (!strcmp(api->GetVariableValue("%CPPEXTDBMS")->ToCString(),"OBJS")) {
    CPP_BuildVArrayFieldOBJS(aMeta,api,aClass,Result);    
  }
  else if (!strcmp(api->GetVariableValue("%CPPEXTDBMS")->ToCString(),"CSFDB")) {
    CPP_BuildVArrayFieldCSFDB(aMeta,api,aClass,Result);    
  }
}

// CSFDB extension for storable classes
//   only if %CPPEXTDBMS == "CSFDB"
//
void CPP_BuildStorableAccessFieldCSFDB(const Handle(MS_MetaSchema)& aMeta,
				       const Handle(EDL_API)& api,
				       const Handle(MS_Field)& field,
				       const Handle(TCollection_HAsciiString)& publics)
{
 Handle(MS_Type) thetype = aMeta->GetType(field->TYpe());

  if (field->Dimensions()->Length() > 0) {
    Standard_Integer                 i;
    Handle(TCollection_HAsciiString) sdim = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString) ddim = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString) vdim = new TCollection_HAsciiString;
    char                             num[30];
    
    
    api->AddVariable("%CSFDBType",CPP_BuildType(aMeta,field->TYpe())->ToCString());
    api->AddVariable("%Field",field->Name()->ToCString());
    
    for (i = 1; i <= field->Dimensions()->Length(); i++) {
      sdim->AssignCat("[");
      sprintf(num,"%d",i);
      sdim->AssignCat("i");
      sdim->AssignCat(num);
      sdim->AssignCat("]");
      
      if (i != 1) {
	vdim->AssignCat(",");
	ddim->AssignCat(",");
      }
      vdim->AssignCat("const Standard_Integer i");
      vdim->AssignCat(num);
      
      ddim->AssignCat("i");
      ddim->AssignCat(num);
    }
    api->AddVariable("%FDim",sdim->ToCString());
    api->AddVariable("%VarDim",vdim->ToCString());
    api->AddVariable("%Dimension",ddim->ToCString());
    api->Apply("%res","DefFuncFieldArray");
  }
  else {
    api->AddVariable("%CSFDBType",field->TYpe()->ToCString());
    api->AddVariable("%Field",field->Name()->ToCString());
    
    if (thetype->IsKind(STANDARD_TYPE(MS_StdClass))) {
      Handle(MS_StdClass) aclass = *((Handle(MS_StdClass)*)&thetype);
      
      if (aclass->IsPersistent()) {
	api->Apply("%res","DefFuncPField");
      }
      else {
	api->Apply("%res","DefFuncSField");
      }
    }
    else {
      api->Apply("%res","DefFuncPrField");
    }
  }

  publics->AssignCat(api->GetVariableValue("%res"));
}

// Extraction of a transient .ixx .jxx and _0.cxx
//   the supplement variable is used for non inline methods generated 
//   by the extractor like destructor (added to .ixx ans _0.cxx
//
void CPP_StorableDerivated(const Handle(MS_MetaSchema)& aMeta,
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
    // include hxx of me
    //
    api->AddVariable(VIClass,aClass->FullName()->ToCString());
#ifdef WNT
    api->Apply(VoutClass,"IncludeNoSafe");
#else 
    api->Apply(VoutClass,"Include");
#endif
    result->AssignCat(api->GetVariableValue(VoutClass));
    
    api->AddVariable(VoutClass,result->ToCString());

    aFileName->AssignCat(api->GetVariableValue(VFullPath));
    aFileName->AssignCat(aClass->FullName());
    aFileName->AssignCat(".jxx");
  
    CPP_WriteFile(api,aFileName,VoutClass);
    result->Clear();
    outfile->Append(aFileName);
  }
  
  aFileName = new TCollection_HAsciiString(api->GetVariableValue(VFullPath));
  aFileName->AssignCat(aClass->FullName());

  if (theClass->GetMyCreator().IsNull()) {
    aFileName->AssignCat(".ixx");
  }
  else {
    aFileName->AssignCat("_0.cxx");
  }

  if (theClass->GetMyCreator().IsNull()) {
    result->Clear();
  }

  // Type Management and supplement
  //  
  for (i = 1; i <= supplement->Length(); i++) {
    result->AssignCat(supplement->Value(i));
  }

  CPP_ClassTypeMgt(aMeta,api,aClass,VTypeMgt);

  result->AssignCat(api->GetVariableValue(VTypeMgt));
 
  api->AddVariable(VSupplement,result->ToCString());
  
  // Methods
  //
  result->Clear();
  
  if (!theClass->GetMyCreator().IsNull()) {
    CPP_GenericDefine(aMeta,api,theClass->GetMyCreator(),VTICDefines,VTICUndefines,Standard_False);
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

  api->AddVariable(VMethods,result->ToCString());

  if (theClass->GetMyCreator().IsNull()) {
    api->AddVariable(VSuffix,"jxx");
  }
  else {
    api->AddVariable(VSuffix,"hxx");
  }

  api->AddVariable(VClass,aClass->FullName()->ToCString());

  api->Apply(VoutClass,"StorableIxx");

  CPP_WriteFile(api,aFileName,VoutClass); 
  
  outfile->Append(aFileName);
}


// Extraction of a transient class (inst or std)
//
void CPP_StorableClass(const Handle(MS_MetaSchema)& aMeta,
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
    Handle(TCollection_HAsciiString)        protf      = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString)        privf      = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString)        SuppMethod = new TCollection_HAsciiString;

    Handle(TColStd_HSequenceOfHAsciiString) Supplement = new TColStd_HSequenceOfHAsciiString;

    Standard_Boolean                        HasInlineMethod = Standard_False,
                                            HasDestructor   = Standard_False,
                                            HasEmptyConst   = Standard_False,
                                            HasConstructor  = Standard_False;


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

    if (theClass->GetInheritsNames()->Length() > 0) {
      if (!theClass->GetInheritsNames()->Value(1)->IsSameString(MS::GetStorableRootName())) {
	publics->AssignCat(" : public ");
	publics->AssignCat(theClass->GetInheritsNames()->Value(1));
	api->AddVariable(VInherits,publics->ToCString());
	publics->Clear();
      }
      else {
	api->AddVariable(VInherits,"");
      }
    }
    else {
      api->AddVariable(VInherits,"");
    }

    api->AddVariable(VClass,theClass->FullName()->ToCString());
    api->AddVariable(VClassComment,theClass->Comment()->ToCString());

    api->AddVariable("%NameField",theClass->FullName()->ToCString());

    for (i = 1; i <= theClass->GetFriendsNames()->Length(); i++) {
      publics->AssignCat("friend ");
      api->AddVariable(VIClass,theClass->GetFriendsNames()->Value(i)->ToCString());
      api->Apply(VTICPublicfriends,"ShortDec");
      publics->AssignCat(api->GetVariableValue(VTICPublicfriends));
    }

    if (!CPP_SetFriendMethod(aMeta,api,theClass->GetFriendMets(),publics)) {
      ErrorMsg() << "CPPExt" << "Error : a friend method was not found..." << endm;
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

      // if the class has no empty constructor, we give it
      //
      if (methods->Value(i)->IsKind(STANDARD_TYPE(MS_Construc))) { 
	if (methods->Value(i)->Params().IsNull()) {
	  HasEmptyConst = Standard_True;
	}
	
	HasConstructor = Standard_True;
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

    if (!HasEmptyConst && (aClass->GetFields()->Length() > 0)) {
      api->AddVariable("%Class",aClass->FullName()->ToCString());
      api->AddVariable("%Arguments"," ");
      api->Apply(VMethod,"ConstructorHeader");
      api->AddVariable(VMBody,"");
      api->Apply(VMethod,"MethodTemplateDef");
      publics->AssignCat(api->GetVariableValue(VMethod));
    }

    // extraction of fields
    //   WARNING: DB dependent 
    //
    Handle(MS_HSequenceOfField) fields =  theClass->GetFields();

    for (i = 1; i <= fields->Length(); i++) {
      if (fields->Value(i)->Protected()) {
	if (!strcmp(api->GetVariableValue("%CPPEXTDBMS")->ToCString(),"OBJY")) {
	  protf->AssignCat(CPP_BuildFieldOBJY(aMeta,fields->Value(i)));
	}
	else {
	  protf->AssignCat(CPP_BuildField(aMeta,fields->Value(i)));
	}
      }
      else {
	if (!strcmp(api->GetVariableValue("%CPPEXTDBMS")->ToCString(),"OBJY")) {
	  privf->AssignCat(CPP_BuildFieldOBJY(aMeta,fields->Value(i)));
	}
	else {
	  privf->AssignCat(CPP_BuildField(aMeta,fields->Value(i)));
	}
      }      
      if (!strcmp(api->GetVariableValue("%CPPEXTDBMS")->ToCString(),"CSFDB")) {
	CPP_BuildStorableAccessFieldCSFDB(aMeta,api,fields->Value(i),publics);
      }
    }

    // for DBC instance
    //
    CPP_BuildVArrayField(aMeta,api,theClass,privates);

    api->AddVariable(VTICPrivatefields,privf->ToCString());
    api->AddVariable(VTICProtectedfields,protf->ToCString());
    api->AddVariable(VTICPublicmets,publics->ToCString());
    api->AddVariable(VTICPrivatemets,privates->ToCString());
    api->AddVariable(VTICProtectedmets,protecteds->ToCString());

    publics->Clear();
    privates->Clear();
    protecteds->Clear();
    privf->Clear();
    protf->Clear();

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
	
	CPP_GenericDefine(aMeta,api,aCreator,VTICDefines,VTICUndefines,Standard_False);
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
#ifdef WNT
	api->Apply(VTICIncludes,"IncludeNoSafe");
#else
	api->Apply(VTICIncludes,"Include");
#endif
	publics->AssignCat(api->GetVariableValue(VTICIncludes));
      }
    }

    // a storable class must include Standard_PrimitiveTypes.hxx
    //
    api->AddVariable(VIClass,"Standard_PrimitiveTypes");
    api->Apply(VTICIncludes,"Include");
    publics->AssignCat(api->GetVariableValue(VTICIncludes));
    
    for (i = 1; i <= incp->Length(); i++) {
      if (!incp->Value(i)->IsSameString(theClass->FullName())) {
	api->AddVariable(VIClass,incp->Value(i)->ToCString());
	api->Apply(VTICIncludes,"ShortDec");
	publics->AssignCat(api->GetVariableValue(VTICIncludes));
      }
    }

    // for DBC instance
    //
    CPP_BuildVArrayDeclaration(aMeta,api,theClass,publics);

    api->AddVariable(VTICIncludes,publics->ToCString());

    api->Apply(VoutClass,"StorableClass");
    
    // we write the .hxx of this class
    //
    Handle(TCollection_HAsciiString) aFile = new TCollection_HAsciiString(api->GetVariableValue(VFullPath));

    aFile->AssignCat(theClass->FullName());
    aFile->AssignCat(".hxx");

    CPP_WriteFile(api,aFile,VoutClass);

    outfile->Append(aFile);

    CPP_StorableDerivated(aMeta,api,aClass,outfile,incp,Supplement);
  }
  else {
    ErrorMsg() << "CPPExt" << "CPP_StorableClass - the class is NULL..." << endm;
    Standard_NoSuchObject::Raise();
  }
}

