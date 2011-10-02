
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

// Extraction of a transient .ixx .jxx and _0.cxx
//   the supplement variable is used for non inline methods generated 
//   by the extractor like destructor (added to .ixx ans _0.cxx
//
void CPP_PackageDerivated(const Handle(MS_MetaSchema)& ,
			    const Handle(EDL_API)& api,
			    const Handle(MS_Package)& aPackage,			    
			    const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
			    const Handle(TColStd_HSequenceOfHAsciiString)& inclist,
			    const Handle(TColStd_HSequenceOfHAsciiString)& supplement)
{
  Standard_Integer                        i;
  Handle(TCollection_HAsciiString)        aFileName = new TCollection_HAsciiString;
  Handle(TCollection_HAsciiString)        result    = new TCollection_HAsciiString;

  api->AddVariable(VClass,aPackage->Name()->ToCString());
  //api->AddVariable(VClassComment,aPackage->Comment()->ToCString());
  api->AddVariable(VSuffix,"hxx");
  
  for (i = 1; i <= inclist->Length(); i++) {
    api->AddVariable(VIClass,inclist->Value(i)->ToCString());
    api->Apply(VoutClass,"Include");
    result->AssignCat(api->GetVariableValue(VoutClass));
  }


  // include hxx of me
  //
  api->AddVariable(VIClass,aPackage->Name()->ToCString());
#ifdef WNT
  api->Apply(VoutClass,"IncludeNoSafe");
#else 
  api->Apply(VoutClass,"Include");
#endif
  result->AssignCat(api->GetVariableValue(VoutClass));
  
  api->AddVariable(VoutClass,result->ToCString());
  
  aFileName->AssignCat(api->GetVariableValue(VFullPath));
  aFileName->AssignCat(aPackage->Name());
  aFileName->AssignCat(".jxx");
  
  CPP_WriteFile(api,aFileName,VoutClass);

  outfile->Append(aFileName);

  aFileName = new TCollection_HAsciiString(api->GetVariableValue(VFullPath));
  aFileName->AssignCat(aPackage->Name());
  aFileName->AssignCat(".ixx");

  // Supplement
  //
  result->Clear();

  for (i = 1; i <= supplement->Length(); i++) {
    result->AssignCat(supplement->Value(i));
  }
  
  api->AddVariable(VSupplement,result->ToCString());
  
  // Methods
  //
  result->Clear();
  
  api->AddVariable(VSuffix,"jxx");

  api->AddVariable(VClass,aPackage->Name()->ToCString());

  api->Apply(VoutClass,"MPVIxx");

  CPP_WriteFile(api,aFileName,VoutClass); 
  

  outfile->Append(aFileName);
}


// Extraction of a transient class (inst or std)
//
void CPP_Package(const Handle(MS_MetaSchema)& aMeta,
		 const Handle(EDL_API)& api,
		 const Handle(MS_Package)& aPackage,
		 const Handle(TColStd_HSequenceOfHAsciiString)& outfile)
{
  if (!aPackage.IsNull()) {
    Standard_Integer                        i;

    Handle(MS_HSequenceOfExternMet)         methods    = aPackage->Methods();
    
    Handle(TCollection_HAsciiString)        publics    = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString)        privates   = new TCollection_HAsciiString;

    Handle(TColStd_HSequenceOfHAsciiString) Supplement = new TColStd_HSequenceOfHAsciiString;
    Handle(TColStd_HSequenceOfHAsciiString) packClass  = aPackage->Classes();
    Standard_Boolean                        HasInlineMethod = Standard_False;

    Handle(TColStd_HSequenceOfHAsciiString) List = new TColStd_HSequenceOfHAsciiString;
    Handle(TColStd_HSequenceOfHAsciiString) incp = new TColStd_HSequenceOfHAsciiString;
    

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
    api->AddVariable(VInherits,"");
    api->AddVariable(VTICProtectedmets,"");
    api->AddVariable(VTICPrivatemets,"");
    api->AddVariable(VMethods,"");
    api->AddVariable(VClass,aPackage->FullName()->ToCString());
    api->AddVariable(VClassComment,aPackage->Comment()->ToCString());
    api->AddVariable(VTICSuppMethod,"");

    // extraction of the methods
    //
    Handle(TCollection_HAsciiString) aliasMethod;

    for (i = 1; i <= methods->Length(); i++) {
      aliasMethod.Nullify();

      if (methods->Value(i)->IsInline()) {
	HasInlineMethod = Standard_True;
      }

      if (!methods->Value(i)->IsAlias().IsNull()) {	
	if (methods->Value(i)->IsQuotedAlias()) {
	  aliasMethod = new TCollection_HAsciiString(methods->Value(i)->IsAlias());
	  aliasMethod->Remove(1);
	  if (aliasMethod->Value(aliasMethod->Length()) == '"') {
	    aliasMethod->Remove(aliasMethod->Length());
	  }
	  aliasMethod->AssignCat("\n");
	}
      }

      CPP_BuildMethod(aMeta,api,methods->Value(i),methods->Value(i)->Name());

      if (  !(methods -> Value ( i ) -> IsInline ())  ) {
        api->Apply(VMethod,"MethodTemplateDec");
      } else {
        api->Apply(VMethod,"MethodTemplateDecInlineWNT" );
      }
      MS::MethodUsedTypes(aMeta,methods->Value(i),List,incp);

      if (methods->Value(i)->Private()) {
	privates->AssignCat(api->GetVariableValue(VMethod));
	if (!aliasMethod.IsNull()) {
	  privates->AssignCat(aliasMethod);
	}
      }
      else {
	publics->AssignCat(api->GetVariableValue(VMethod));
	if (!aliasMethod.IsNull()) {
	  publics->AssignCat(aliasMethod);
	}
      }
    }

    api->AddVariable(VTICPublicmets,publics->ToCString());
    api->AddVariable(VTICPrivatemets,privates->ToCString());

    publics->Clear();
    privates->Clear();

    // include the lxx if the class have inline methods
    //
    if (HasInlineMethod) {
	api->AddVariable(VIClass,aPackage->Name()->ToCString());
	api->AddVariable(VSuffix,"lxx");
	api->Apply(VTICInlineIncludes,"IncludeNoSafe");
    }

    api->AddVariable(VSuffix,"hxx");

    for (i = 1; i <= List->Length(); i++) {
      if (!List->Value(i)->IsSameString(aPackage->Name())) {
	api->AddVariable(VIClass,List->Value(i)->ToCString());
#ifdef WNT
	api->Apply(VTICIncludes,"IncludeNoSafe");
#else
	api->Apply(VTICIncludes,"Include");
#endif
	publics->AssignCat(api->GetVariableValue(VTICIncludes));
      }
    }

    for (i = 1; i <= incp->Length(); i++) {
      if (!incp->Value(i)->IsSameString(aPackage->Name())) {
	api->AddVariable(VIClass,incp->Value(i)->ToCString());
	api->Apply(VTICIncludes,"ShortDec");
	publics->AssignCat(api->GetVariableValue(VTICIncludes));
      }
    }

    for (i = 1; i <= packClass->Length(); i++) {
      //Documentation


      // Declaration incomplete et
      //
      Handle(TCollection_HAsciiString) name = MS::BuildFullName(aPackage->Name(),packClass->Value(i));
      api->AddVariable(VIClass,name->ToCString());
      api->Apply(VTICIncludes,"ShortDec");
      publics->AssignCat(api->GetVariableValue(VTICIncludes));
      // ... declarations friends des classes declarees dans le package pour 
      // les methodes de classes privees
      //
      Handle(TCollection_HAsciiString) friendname = new TCollection_HAsciiString("friend class ");
      friendname->AssignCat(name->ToCString());
      friendname->AssignCat(";\n");
      privates->AssignCat(friendname);
    }
    api->AddVariable(VTICPrivatefriends,privates->ToCString());
    api->AddVariable(VTICIncludes,publics->ToCString());

    api->Apply(VoutClass,"MPVClass");
    
    // we write the .hxx of this class
    //
    Handle(TCollection_HAsciiString) aFile = new TCollection_HAsciiString(api->GetVariableValue(VFullPath));

    aFile->AssignCat(aPackage->Name());
    aFile->AssignCat(".hxx");

    CPP_WriteFile(api,aFile,VoutClass);

    outfile->Append(aFile);

    CPP_PackageDerivated(aMeta,api,aPackage,outfile,incp,Supplement);
  }
  else {
    ErrorMsg() << "CPPExt" << "CPP_Package - the package is NULL..." << endm;
    Standard_NoSuchObject::Raise();
  }
}

