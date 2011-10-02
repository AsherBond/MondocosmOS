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

#include <MS_MemberMet.hxx>
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

#include <CPPClient_Define.hxx>
#include <WOKTools_Messages.hxx>

void CPPClient_MethodBuilder(const Handle(MS_MetaSchema)& aMeta, 
			     const Handle(EDL_API)& api, 
			     const Handle(TCollection_HAsciiString)& className,
			     const Handle(MS_Method)& m,
			     const Handle(TCollection_HAsciiString)& methodName,const Standard_Boolean);

void CPPClient_MethodUsedTypes(const Handle(MS_MetaSchema)& aMeta,
			       const Handle(MS_Method)& aMethod,
			       const Handle(TColStd_HSequenceOfHAsciiString)& List,
			       const Handle(TColStd_HSequenceOfHAsciiString)& Incp);

Standard_Boolean CPPClient_AncestorHaveEmptyConstructor(const Handle(MS_MetaSchema)&,
							const Handle(TCollection_HAsciiString)&);

// Extraction of a transient handle
//
void CPPClient_TransientHandle(const Handle(EDL_API)& api,
			 const Handle(TCollection_HAsciiString)& aClass,
			 const Handle(TCollection_HAsciiString)& aMother,
			 const Handle(TCollection_HAsciiString)& aFileName)
{
  // we create the handle...
  //
  if (aClass->IsSameString(MS::GetTransientRootName()) ||
      aClass->IsSameString(MS::GetPersistentRootName())) {
    api->AddVariable("%Inherits",CPPClient_TransientRootName()->ToCString());
  }
  else {
    api->AddVariable(VClass,aMother->ToCString());
    api->Apply("%Inherits","BuildTypeName");
  }
  
  api->AddVariable(VClass,aClass->ToCString());
  api->Apply("%Class","BuildTypeName");
  api->Apply("%HTHandle","HandleClient");
 
  // ...now we write the result
  //
  api->OpenFile("HTFile",aFileName->ToCString());
  api->WriteFile("HTFile","%HTHandle");
  api->CloseFile("HTFile");
}

// Extraction of .cxx for handled object
//
void CPPClient_TransientDerivated(const Handle(MS_MetaSchema)& aMeta,
				  const Handle(EDL_API)& api,
				  const Handle(MS_Class)& aClass,			    
				  const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
				  const Handle(TColStd_HSequenceOfHAsciiString)& inclist,
				  const Handle(TColStd_HSequenceOfHAsciiString)& supplement,
				  const ExtractionType MustBeComplete)
{
  Handle(TCollection_HAsciiString)        publics    = new TCollection_HAsciiString;
  Standard_Integer                        i;
  
  // the name must be <Inter>_<Pack>_<Class>
  //
  api->AddVariable("%Class",aClass->FullName()->ToCString());
  api->Apply("%Class","BuildTypeName");

  if (MustBeComplete != CPPClient_INCOMPLETE) {
    for (i = 1; i <= inclist->Length(); i++) {
      if (!inclist->Value(i)->IsSameString(aClass->FullName())) {
	api->AddVariable("%IClass",inclist->Value(i)->ToCString());
	api->Apply("%Includes","Include");
	publics->AssignCat(api->GetVariableValue("%Includes"));
      }
    }
  }

  api->AddVariable("%Includes",publics->ToCString());
  publics->Clear();

  if (MustBeComplete != CPPClient_INCOMPLETE) {
    for (i = 1; i <= supplement->Length(); i++) {
      publics->AssignCat(supplement->Value(i));
    }
  }

  api->AddVariable("%Methods",publics->ToCString());
  publics->Clear();

  // we create the inheritance
  //
  if (aClass->FullName()->IsSameString(MS::GetTransientRootName()) ||
      aClass->FullName()->IsSameString(MS::GetPersistentRootName())) {
    api->AddVariable("%Inherits",CPPClient_TransientRootName()->ToCString());
  }
  else {
    api->AddVariable("%Class",aClass->GetInheritsNames()->Value(1)->ToCString());
    api->Apply("%Inherits","BuildTypeName");
  }

  // the name must be <Inter>_<Pack>_<Class>
  //
  api->AddVariable("%Class",aClass->FullName()->ToCString());
  api->Apply("%Class","BuildTypeName");

  api->AddVariable("%RealClass",aClass->FullName()->ToCString());

  CPPClient_ClassTypeMgt(aMeta,api,aClass,"%TypeMgt");
  
  api->Apply("%outClass","RefClassClientCXX");

  // we write the .cxx of this class
  //
  Handle(TCollection_HAsciiString) aFile = new TCollection_HAsciiString(api->GetVariableValue("%FullPath"));
  
  aFile->AssignCat(CPPClient_InterfaceName);
  aFile->AssignCat("_");
  aFile->AssignCat(aClass->FullName());
  aFile->AssignCat("_client.cxx");
  
  CPPClient_WriteFile(api,aFile,"%outClass");
  
  outfile->Append(aFile);
}


// Extraction of a transient class (inst or std)
//
void CPPClient_TransientClass(const Handle(MS_MetaSchema)& aMeta,
			      const Handle(EDL_API)& api,
			      const Handle(MS_Class)& aClass,
			      const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
			      const ExtractionType MustBeComplete,
			      const Handle(MS_HSequenceOfMemberMet)& theMetSeq)
{
  Handle(MS_StdClass) theClass = Handle(MS_StdClass)::DownCast(aClass);

  if (!theClass.IsNull()) {
    Standard_Integer                        i;
    Handle(MS_HSequenceOfMemberMet)         methods;
    Handle(TCollection_HAsciiString)        publics             = new TCollection_HAsciiString;
    Handle(TCollection_HAsciiString)        SuppMethod          = new TCollection_HAsciiString;
    Handle(TColStd_HSequenceOfHAsciiString) Supplement          = new TColStd_HSequenceOfHAsciiString;
    Handle(TColStd_HSequenceOfHAsciiString) List = new TColStd_HSequenceOfHAsciiString;
    Handle(TColStd_HSequenceOfHAsciiString) incp = new TColStd_HSequenceOfHAsciiString;
    Standard_Boolean                        mustCallAncestor = Standard_False;

    // we create the inheritance
    //
    if (theClass->FullName()->IsSameString(MS::GetTransientRootName()) ||
	theClass->FullName()->IsSameString(MS::GetPersistentRootName())) {
      api->AddVariable("%Inherits",CPPClient_TransientRootName()->ToCString());
    }
    else {
      api->AddVariable("%Class",theClass->GetInheritsNames()->Value(1)->ToCString());
      mustCallAncestor = !CPPClient_AncestorHaveEmptyConstructor(aMeta,theClass->GetInheritsNames()->Value(1));
      api->Apply("%Inherits","BuildTypeName");
    }

    List->Append(api->GetVariableValue("%Inherits"));

    // the name must be <Inter>_<Pack>_<Class>
    //
    api->AddVariable("%Class",theClass->FullName()->ToCString());
    api->Apply("%Class","BuildTypeName");

    if (MustBeComplete == CPPClient_SEMICOMPLETE) {
      methods = theMetSeq;
    }
    else {
      methods = theClass->GetMethods();
    }

    for (i = 1; i <= methods->Length() && (MustBeComplete != CPPClient_INCOMPLETE); i++) {
      CPPClient_BuildMethod(aMeta,api,methods->Value(i),methods->Value(i)->Name());

      if (!api->GetVariableValue("%Method")->IsSameString(CPPClient_ErrorArgument)) {
	api->Apply(VMethod,"MethodTemplateDec");
	
	if ((theClass->Deferred() && methods->Value(i)->IsKind(STANDARD_TYPE(MS_Construc))) 
	    || methods->Value(i)->IsProtected() 
	    || methods->Value(i)->Private())  {
	  // nothing
	} 
	else {
	  CPPClient_MethodUsedTypes(aMeta,methods->Value(i),List,incp);
	  publics->AssignCat(api->GetVariableValue(VMethod));
	  CPPClient_MethodBuilder(aMeta,api,aClass->FullName(),methods->Value(i),methods->Value(i)->Name(),mustCallAncestor);
	  Supplement->Append(api->GetVariableValue(VMethod));
	}
      }
    }

    api->AddVariable("%Methods",publics->ToCString());

    publics->Clear();

    if (MustBeComplete != CPPClient_INCOMPLETE) {
      api->AddVariable(VSuffix,"hxx");
      
      for (i = 1; i <= List->Length(); i++) {
	if (!List->Value(i)->IsSameString(theClass->FullName())) {
	  api->AddVariable("%IClass",List->Value(i)->ToCString());
	  api->Apply("%Includes","Include");
	  publics->AssignCat(api->GetVariableValue("%Includes"));
	}
      }
      
      
      for (i = 1; i <= incp->Length(); i++) {
	if (!incp->Value(i)->IsSameString(theClass->FullName())) {
	  api->AddVariable("%IClass",incp->Value(i)->ToCString());
	  api->Apply("%Includes","ShortDec");
	  publics->AssignCat(api->GetVariableValue("%Includes"));
	}
      }
    }

    api->AddVariable("%Includes",publics->ToCString());

    // we create the inheritance
    //
    if (theClass->FullName()->IsSameString(MS::GetTransientRootName()) ||
	theClass->FullName()->IsSameString(MS::GetPersistentRootName())) {
      api->AddVariable("%Inherits",CPPClient_TransientRootName()->ToCString());
    }
    else {
      api->AddVariable("%Class",theClass->GetInheritsNames()->Value(1)->ToCString());
      api->Apply("%Inherits","BuildTypeName");
    }

    // the name must be <Inter>_<Pack>_<Class>
    //
    api->AddVariable("%Class",theClass->FullName()->ToCString());
    api->Apply("%Class","BuildTypeName");

    api->Apply("%outClass","RefClassClientHXX");

    // we write the .hxx of this class
    //
    Handle(TCollection_HAsciiString) aFile = new TCollection_HAsciiString(api->GetVariableValue("%FullPath"));

    aFile->AssignCat(CPPClient_InterfaceName);
    aFile->AssignCat("_");
    aFile->AssignCat(theClass->FullName());
    aFile->AssignCat(".hxx");

    CPPClient_WriteFile(api,aFile,"%outClass");

    outfile->Append(aFile);


    CPPClient_TransientDerivated(aMeta,api,aClass,outfile,incp,Supplement,MustBeComplete);
  }
  else {
    ErrorMsg() << "CPPClient" << "CPPClient_TransientClass - the class is NULL..." << endm;
    Standard_NoSuchObject::Raise();
  }
}

