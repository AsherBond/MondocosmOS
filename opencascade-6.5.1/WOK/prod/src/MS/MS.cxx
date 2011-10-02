#include <MS.ixx>
#include <MS_MemberMet.hxx>
#include <MS_ClassMet.hxx>
#include <MS_Param.hxx>
#include <MS_ParamWithValue.hxx>
#include <MS_Package.hxx>
#include <MS_InstMet.hxx>
#include <MS_ExternMet.hxx>
#include <MS_Construc.hxx>
#include <MS_MetaSchema.hxx>
#include <MS_StdClass.hxx>
#include <MS_GenClass.hxx>
#include <MS_InstClass.hxx>
#include <MS_Enum.hxx>
#include <MS_Pointer.hxx>
#include <MS_NatType.hxx>
#include <MS_Error.hxx>
#include <MS_Imported.hxx>
#include <MS_Alias.hxx>
#include <MS_HArray1OfParam.hxx>
#include <MS_HSequenceOfParam.hxx>
#include <MS_HSequenceOfField.hxx>
#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_HSequenceOfType.hxx>
#include <MS_HSequenceOfExternMet.hxx>
#include <Standard_NullObject.hxx>
#include <WOKTools_Messages.hxx>
#include <Standard_NoSuchObject.hxx>
#include <TColStd_HSequenceOfInteger.hxx>
#include <WOKTools_DataMapOfHAsciiStringOfHAsciiString.hxx>

WOKTools_DataMapOfHAsciiStringOfHAsciiString globMapOfName;
#define NAME_STATS 1


#ifdef NAME_STATS
Standard_Integer economised = 0;
Standard_Integer allocated  = 0;
#endif

Standard_EXPORT const Handle(TCollection_HAsciiString)&  MS_GetName(const Handle(TCollection_HAsciiString)&aname)
{
  if(!globMapOfName.IsBound(aname))
    {
       globMapOfName.Bind(aname,aname);
    }
  return globMapOfName.Find(aname);
}

Standard_EXPORT void  MS_ClearMapOfName()
{
  globMapOfName.Clear();
}

Handle(TCollection_HAsciiString) MS::BuildFullName(const Handle(TCollection_HAsciiString)& aGEName,
						   const Handle(TCollection_HAsciiString)& aName)
{
  Handle(TCollection_HAsciiString) aFullName = new TCollection_HAsciiString(aGEName);

  aFullName->AssignCat("_");
  aFullName->AssignCat(aName);

  return MS_GetName(aFullName);
}

Handle(TCollection_HAsciiString) MS::BuildComplexName(const Handle(TCollection_HAsciiString)& MyName,
						      const Handle(TCollection_HAsciiString)& aGEName,
						      const Handle(TCollection_HAsciiString)& aName)
{
  Standard_Integer                 Locate, Length = 0;
  Handle(TCollection_HAsciiString) theComplexName = new TCollection_HAsciiString(aGEName);
  
  theComplexName = new TCollection_HAsciiString(aGEName);
  
  Locate = 0;
  
  if (theComplexName->Length() > aName->Length()) {
    Length = theComplexName->Length();
    Locate = theComplexName->Location(aName,Length - aName->Length(),Length);
  }
    
  if (Locate) {
    theComplexName->Remove(Locate, Length - Locate + 1);
  } 
  else {
    theComplexName->AssignCat("Of");
  }
  
  theComplexName->AssignCat(MyName);
  return MS_GetName(theComplexName);
}

Handle(MS_InstClass) MS::BuildInstClass(const Handle(MS_Class)& aClass,
					const Handle(TCollection_HAsciiString)& aName,
					const Handle(TCollection_HAsciiString)& aPackage,
					const Handle(TColStd_HSequenceOfHAsciiString)& aSeqGen,
					const Handle(TColStd_HSequenceOfHAsciiString)& aSeqType)
{
  Handle(MS_InstClass) aNewInst;

  if (!aClass.IsNull()) {
    Standard_Integer                        i;
    Handle(TColStd_HSequenceOfHAsciiString) aSeqStr;
    Handle(MS_InstClass)                    anInst;

    anInst   = *((Handle(MS_InstClass)*)&aClass);
    aNewInst = new MS_InstClass(aName,aPackage);

    aSeqStr = anInst->BasicInstTypes();
    
    for (i = 1; i <= aSeqStr->Length(); i++) {
      aNewInst->BasicInstType(aSeqStr->Value(i));
    }

    aSeqStr = anInst->InstTypes();
    
    for (i = 1; i <= aSeqStr->Length(); i++) {
      aNewInst->InstType(aSeqStr->Value(i));
    }

    aSeqStr = anInst->GenTypes();

    for (i = 1; i <= aSeqStr->Length(); i++) {
      aNewInst->GenType(aSeqStr->Value(i));
    }

    for (i = 1; i <= aSeqType->Length(); i++) {
      aNewInst->ResolveInstType(aSeqGen->Value(i),aSeqType->Value(i));
    }

    aNewInst->Private(anInst->Private());
    aNewInst->GenClass(anInst->GenClass());
    aNewInst->Mother(anInst->GetMother());
    aNewInst->MetaSchema(anInst->GetMetaSchema());
    aNewInst->Package(aPackage);
    aNewInst->Incomplete(Standard_False);
    aNewInst->NestingClass(anInst->GetNestingClass());
  }
  else {
    cerr << "Error : MS::BuildInstClass - aClass is NULL" << endl;
    Standard_NullObject::Raise();
  }

  return aNewInst;
}

Handle(MS_StdClass) MS::BuildStdClass(const Handle(MS_Class)& aClass,
				      const Handle(TCollection_HAsciiString)& aName,
				      const Handle(TCollection_HAsciiString)& aPackage,
				      const Handle(TColStd_HSequenceOfHAsciiString)& aSeqGen,
				      const Handle(TColStd_HSequenceOfHAsciiString)& aSeqType)
{
  Handle(MS_StdClass) aNewClass;
  
  if (!aClass.IsNull()) {
    Standard_Integer                        i,j;
    Standard_Boolean                        IsFound = Standard_False;
    Handle(TColStd_HSequenceOfHAsciiString) aSeqStr = aClass->GetInheritsNames();
    
    aNewClass = new MS_StdClass(aName,aPackage);
    aNewClass->MetaSchema(aClass->GetMetaSchema());
    aNewClass->Package(aPackage);
    aNewClass->Deferred(aClass->Deferred());
    aNewClass->Private(aClass->Private());
    aNewClass->Mother(aClass->FullName());
    aNewClass->NestingClass(aClass->GetNestingClass());

    for (i = 1; i <= aSeqStr->Length(); i++) {
      aNewClass->Inherit(aSeqStr->Value(i));
    }
    
    aSeqStr = aClass->GetUsesNames();
    
    for (i = 1; i <= aSeqStr->Length(); i++) {
      aNewClass->Use(aSeqStr->Value(i));
    }

    // adding the use of inst types
    //
    for (i = 1; i <= aSeqType->Length(); i++) {
      for (j = 1; j <= aSeqStr->Length() && !IsFound; j++) {
	if (aSeqType->Value(i)->IsSameString(aSeqStr->Value(j))) {
	  IsFound = Standard_True;
	}
      }

      if (!IsFound) {
	aNewClass->Use(aSeqType->Value(i));
	IsFound = Standard_False;
      }
    }

    // for Type conversion 
    //     ex :  a generic copy constructor
    //                 Create ( T from p) returns T
    //              -> Create ( TOfA from p') returns TOfA
    //
    aSeqGen->Append(aClass->FullName());
    aSeqType->Append(aNewClass->FullName());

    Handle(MS_HSequenceOfField) aSeqField = aClass->GetFields();
    
    for (i = 1; i <= aSeqField->Length(); i++) {
      aNewClass->Field(MS::BuildStdField(aSeqField->Value(i),aNewClass,aSeqGen,aSeqType));
    }
    
    aSeqStr = aClass->GetFriendMets();
    
    for (i = 1; i <= aSeqStr->Length(); i++) {
      aNewClass->FriendMet(aSeqStr->Value(i));
    }
    
    Handle(MS_HSequenceOfMemberMet) aSeqMet = aClass->GetMethods();
    
    for (i = 1; i <= aSeqMet->Length(); i++) {
      aNewClass->Method(MS::BuildStdMethod(aSeqMet->Value(i),aNewClass,aSeqGen,aSeqType));
    }

    aSeqGen->Remove(aSeqGen->Length());
    aSeqType->Remove(aSeqType->Length());
    
    aSeqStr = aClass->GetRaises();
    
    for (i = 1; i <= aSeqStr->Length(); i++) {
      aNewClass->Raises(aSeqStr->Value(i));
    }
    
    aSeqStr = aClass->GetFriendsNames();

    for (i = 1; i <= aSeqStr->Length(); i++) {
      IsFound = Standard_False;

      for (j = 1; j <= aSeqGen->Length() && !IsFound; j++) {
	if (aSeqGen->Value(j)->IsSameString(aSeqStr->Value(i))) {
	  aNewClass->Friend(aSeqType->Value(j));
	  IsFound = Standard_True;
	}
      }
      
      if (!IsFound) {
	aNewClass->Friend(aSeqStr->Value(i));
      }
    }
    
    aNewClass->Incomplete(Standard_False);
  }
  else {
    cerr << "Error : MS::BuildStdClass - aClass is NULL" << endl;
    Standard_NullObject::Raise();
  }
  
  return aNewClass;
}

//=======================================================================
//function : BuildStdMethod
//purpose  : 
//=======================================================================
Handle(MS_MemberMet) MS::BuildStdMethod(const Handle(MS_MemberMet)& aMethod,
					const Handle(MS_Class)& aClass,
				        const Handle(TColStd_HSequenceOfHAsciiString)& aSeqGen,
				        const Handle(TColStd_HSequenceOfHAsciiString)& aSeqType)
{
  Handle(MS_MemberMet) aNewMethod;
  //
  if (!aMethod.IsNull() && !aClass.IsNull()) {
    Standard_Integer i, aNbParams, aNbRaises; 
    Handle(MS_InstMet)  aNewInst;
    Handle(MS_Construc) aNewCons;
    Handle(MS_ClassMet) aNewCmet;
    MS_InstMet          *anInst = NULL;//0l;
    //
    if (aMethod->IsKind(STANDARD_TYPE(MS_InstMet))) {
      anInst     = (MS_InstMet *)aMethod.operator->();
      aNewInst   = new MS_InstMet(aMethod->Name(),aClass->FullName());
      aNewMethod = aNewInst;

      aNewInst->Mode(anInst->GetMode());
    }
    else if (aMethod->IsKind(STANDARD_TYPE(MS_Construc))) {
      aNewCons   = new MS_Construc(aMethod->Name(),aClass->FullName());
      aNewMethod = aNewCons;
    }
    else {
      aNewCmet   = new MS_ClassMet(aMethod->Name(),aClass->FullName());
      aNewMethod = aNewCmet;
    }
    //
    // Parameters
    //
    Handle(MS_HArray1OfParam)   aSeqParam = aMethod->Params();
    Handle(MS_HSequenceOfParam) tmpParamSeq = new MS_HSequenceOfParam;
    if(!aSeqParam.IsNull()) {
      aNbParams=aSeqParam->Length(); 
      for (i = 1; i <= aNbParams; ++i) {
	tmpParamSeq->Append(MS::BuildStdParam(aSeqParam->Value(i),aNewMethod,aSeqGen,aSeqType));
      }
      aNewMethod->Params(tmpParamSeq);
    }
    //
    // Some attributes
    aNewMethod->Private(aMethod->Private());
    aNewMethod->Inline(aMethod->IsInline());
    aNewMethod->ConstReturn(aMethod->IsConstReturn());
    aNewMethod->RefReturn(aMethod->IsRefReturn());
    aNewMethod->Alias(aMethod->IsAlias());
    aNewMethod->Destructor(aMethod->IsDestructor());
    aNewMethod->Protected(aMethod->IsProtected());
    aNewMethod->FunctionCall(aMethod->IsFunctionCall());
    aNewMethod->SetAliasType(aMethod->IsOperator());
    //modified by NIZNHY-PKV Mon May  5 09:09:37 2008f
    aNewMethod->PtrReturn(aMethod->IsPtrReturn());
    //modified by NIZNHY-PKV Mon May  5 09:09:40 2008t
    //
    if (!aMethod->Returns().IsNull()) {
      aNewMethod->Returns(MS::BuildStdParam(aMethod->Returns(),aNewMethod,aSeqGen,aSeqType));
    }
    //
    aNewMethod->CreateFullName();
    aNewMethod->MetaSchema(aClass->GetMetaSchema());
    aClass->GetMetaSchema()->AddMethod(aNewMethod);
    //
    // Raises
    Handle(TColStd_HSequenceOfHAsciiString) aRaiseSeq  = aMethod->GetRaisesName();
    Handle(MS_Method)                       aSimpleMet = aNewMethod;
    //
    if(!aRaiseSeq.IsNull()) {
      aNbRaises=aRaiseSeq->Length();
      for (i = 1; i <= aNbRaises; ++i) {
	aSimpleMet->Raises(aRaiseSeq->Value(i));
      }
    }
  }
  else {
    cerr << "Error : MS::BuildStdMethod - aMethod or aClass are NULL" << endl;
    Standard_NullObject::Raise();
  }
  //
  return aNewMethod;
}
//
// WARNING (to do) : LikeParam is not copied 
//
Handle(MS_Param) MS::BuildStdParam(const Handle(MS_Param)& aParam,
				   const Handle(MS_Method)& aMethod,
				   const Handle(TColStd_HSequenceOfHAsciiString)& aSeqGen,
				   const Handle(TColStd_HSequenceOfHAsciiString)& aSeqType)
{
  Handle(MS_Param) aNewParam;

  if (!aParam.IsNull() && !aMethod.IsNull()) {
    Standard_Integer i;
    Standard_Boolean IsFound = Standard_False;

    if (aParam->GetValueType() == MS_NONE) {
      aNewParam = new MS_Param(aMethod,aParam->Name());
    }
    else {
      MS_ParamWithValue *pwv = (MS_ParamWithValue *)aParam.operator->();
      MS_ParamWithValue *npwv;

      aNewParam = new MS_ParamWithValue(aMethod,aParam->Name());

      npwv = (MS_ParamWithValue *)aNewParam.operator->();
      npwv->Value(pwv->GetValue(),pwv->GetValueType());
    }

    aNewParam->AccessMode(aParam->GetAccessMode());

    for (i = 1; i <= aSeqGen->Length() && !IsFound; i++) {
      if (aSeqGen->Value(i)->IsSameString(aParam->TypeName())) {
	aNewParam->Type(aSeqType->Value(i));
	aNewParam->ItsItem();
	IsFound = Standard_True;
      }
    }

    if (!IsFound) {
      aNewParam->Type(aParam->TypeName());
      aNewParam->ItsNotItem();
    }
    
    aNewParam->MetaSchema(aParam->GetMetaSchema());
  }
  else {
    cerr << "Error : MS::BuildStdParam - aParam or aMethod are NULL" << endl;
    Standard_NullObject::Raise();
  }

  return aNewParam;
}


Handle(MS_Field) MS::BuildStdField(const Handle(MS_Field)& aField,
				   const Handle(MS_Class)& aClass,
				   const Handle(TColStd_HSequenceOfHAsciiString)& aSeqGen,
				   const Handle(TColStd_HSequenceOfHAsciiString)& aSeqType)
{
  Handle(MS_Field) aNewField;

  if (!aField.IsNull() && !aClass.IsNull()) {
    Standard_Integer i;
    Standard_Boolean IsFound = Standard_False;

    aNewField = new MS_Field(aClass,aField->Name());

    aNewField->Protected(aField->Protected());
    aNewField->MetaSchema(aField->GetMetaSchema());

    for (i = 1; i <= aField->Dimensions()->Length(); i++) {
      aNewField->Dimension(aField->Dimensions()->Value(i));
    }

    for (i = 1; i <= aSeqGen->Length() && !IsFound; i++) {
      if (aSeqGen->Value(i)->IsSameString(aField->TYpe())) {
	aNewField->TYpe(aSeqType->Value(i));
	IsFound = Standard_True;
      }
    }

    if (!IsFound) {
      aNewField->TYpe(aField->TYpe());
    }
  }
  else {
    cerr << "Error : MS::BuildStdField - aField or aClass are NULL" << endl;
    Standard_NullObject::Raise();
  }

  return aNewField;
}

Handle(TCollection_HAsciiString) MS::GetPersistentRootName()
{
  static Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(MS::GetPackageRootName(),new TCollection_HAsciiString("Persistent"));

  return aFullName;
}

Handle(TCollection_HAsciiString) MS::GetStorableRootName() 
{
  static Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(MS::GetPackageRootName(),new TCollection_HAsciiString("Storable"));

  return aFullName;
}


Handle(TCollection_HAsciiString) MS::GetTransientRootName()
{
  static Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(MS::GetPackageRootName(), new TCollection_HAsciiString("Transient"));

  return aFullName;
}

Handle(TCollection_HAsciiString) MS::GetPackageRootName()
{
  static Handle(TCollection_HAsciiString) aFullName = new TCollection_HAsciiString("Standard");

  return aFullName;
}

Handle(TCollection_HAsciiString) MS::GetVArrayRootName()
{
  static Handle(TCollection_HAsciiString) aFullName = new TCollection_HAsciiString("DBC_VArray");

  return aFullName;
}
Handle(TCollection_HAsciiString) MS::GetEntityNameFromMethodName(const Handle(TCollection_HAsciiString)& methodName)
{
  Handle(TCollection_HAsciiString) aname = methodName->Token(":");

  return aname;
}

Handle(MS_Method) MS::GetMethodFromFriendName(const Handle(MS_MetaSchema)& aMeta,
					      const Handle(TCollection_HAsciiString)& methodName)
{
  Handle(TCollection_HAsciiString) aname = methodName->Token(":");
  Standard_Boolean                 IsFound = Standard_False;
  Standard_Integer                 i;
  Handle(MS_Method)                result;

  if (!aname.IsNull()) {
    if (aMeta->IsPackage(aname)) {
      Handle(MS_Package)              apack  = aMeta->GetPackage(aname);
      Handle(MS_HSequenceOfExternMet) seqmet = apack->Methods();

      for (i = 1; i <= seqmet->Length() && !IsFound; i++) {
	if (seqmet->Value(i)->FullName()->Search(methodName->ToCString()) >= 0) {
	  IsFound = Standard_True;
	  result = seqmet->Value(i);
	}
      }
    }
    else if (aMeta->IsDefined(aname)) {
      Handle(MS_Type)  ftype = aMeta->GetType(aname);
      Handle(MS_Class) aclass = *((Handle(MS_Class)*)&ftype);
      Handle(MS_HSequenceOfMemberMet) seqmet = aclass->GetMethods();

      for (i = 1; i <= seqmet->Length() && !IsFound; i++) {
	if (seqmet->Value(i)->FullName()->Search(methodName->ToCString()) >= 0) {
	  IsFound = Standard_True;
	  result = seqmet->Value(i);
	}
      }
    }
  }

  return result;
}


//Standard_Boolean MS::IsExportedType(const Handle(MS_MetaSchema)& aMeta,const Handle(MS_Type)& aType)
Standard_Boolean MS::IsExportedType(const Handle(MS_MetaSchema)& ,const Handle(MS_Type)& aType)
{ 
  if (aType->IsKind(STANDARD_TYPE(MS_Imported)) || 
      aType->IsKind(STANDARD_TYPE(MS_GenType))  ||
      aType->IsKind(STANDARD_TYPE(MS_Pointer)))
    return Standard_False;
  else 
    return Standard_True;
}

Standard_Boolean MS::IsExportableMethod(const Handle(MS_MetaSchema)& aMeta,const Handle(MS_Method)& aMethod)
{ 
  Standard_Integer            i;
  Handle(MS_HArray1OfParam)   par  = aMethod->Params();
  Handle(MS_Param)            apar = aMethod->Returns();
  Standard_Boolean            result = Standard_True;

  if(!par.IsNull()) {
    for (i = 1; i <= par->Length(); i++) {
      if (!IsExportedType(aMeta,aMeta->GetType(par->Value(i)->TypeName())))
	return Standard_False;
    }
  }

  if (!apar.IsNull()) {
    if (!IsExportedType(aMeta,aMeta->GetType(apar->TypeName())))
      return Standard_False;
  }

  return result;
}

Standard_Boolean MS::IsExportableClass(const Handle(MS_MetaSchema)& aMeta,const Handle(MS_Class)& aClass, const Standard_Boolean mustCheckField, const Standard_Boolean mustCheckMethods)
{ 
  Standard_Boolean IsExtractable = Standard_True;
  Standard_Integer i;

  if (aClass->IsKind(STANDARD_TYPE(MS_GenClass)) || !aClass->IsKind(STANDARD_TYPE(MS_StdClass))) {
    return Standard_False;
  }
  else if (aClass->IsKind(STANDARD_TYPE(MS_StdClass)) && !aClass->IsKind(STANDARD_TYPE(MS_Error))) {
    MS_StdClass* astdClass = (MS_StdClass *)aClass.operator->();
    
    if (astdClass->IsGeneric()) {
      return Standard_False;
    }
  }
  else {
    return Standard_False;
  }
  
  if (mustCheckMethods) {
    Handle(MS_HSequenceOfMemberMet) aseqmet = aClass->GetMethods();
    
    for (i = 1; i <= aseqmet->Length(); i++) {
      if (!IsExportableMethod(aMeta,aseqmet->Value(i)))
	return Standard_False;
    }  
  }

  if (mustCheckField) {
    Handle(MS_HSequenceOfField) aseqfield = aClass->GetFields();

    for (i = 1; i <= aseqfield->Length(); i++) {
      if (IsExportedType(aMeta,aMeta->GetType(aseqfield->Value(i)->TYpe())))
	return Standard_False;
    }
  }

  return IsExtractable;
}

// add an element in a sequence only if it s not already 
// here
//
void MS::AddOnce(const Handle(TColStd_HSequenceOfHAsciiString)& aSeq, 
		 const Handle(TCollection_HAsciiString)& item)
{
  Standard_Integer i;
  Standard_Boolean IsFound = Standard_False;

  for (i = 1; i <= aSeq->Length() && !IsFound; i++) {
    if (aSeq->Value(i)->IsSameString(item)) {
      IsFound = Standard_True;
    }
  }

  if (!IsFound) {
    aSeq->Append(item);
  }
}

// we test the type and dispatch it in the different lists
//
//void MS::DispatchUsedType(const Handle(MS_MetaSchema)& aMeta,
void MS::DispatchUsedType(const Handle(MS_MetaSchema)& ,
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

      handlename->AssignCat(thetype->FullName());
      MS::AddOnce(List,handlename);
      MS::AddOnce(Incp,thetype->FullName());
    }
    else {
      if (notusedwithref) {
	MS::AddOnce(List,thetype->FullName());
      }
      else {
	MS::AddOnce(Incp,thetype->FullName());
      }
    }
  }
  else if (thetype->IsKind(STANDARD_TYPE(MS_NatType))) {
    MS::AddOnce(List,thetype->FullName());
  }      
}

// sort the method used types :
//
//    List     : the types that must have a full definition
//    Incp     : the types that only have to be declared
//
void MS::MethodUsedTypes(const Handle(MS_MetaSchema)& aMeta,
			 const Handle(MS_Method)& aMethod,
			 const Handle(TColStd_HSequenceOfHAsciiString)& List,
			 const Handle(TColStd_HSequenceOfHAsciiString)& Incp)
{
  Standard_Integer                 i;
  Handle(MS_Param)                 aParam;
  Handle(MS_Type)                  thetype;
  Handle(TCollection_HAsciiString) aName;

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
    if (aMeta->IsDefined(aParam->TypeName())) {
      thetype = aParam->Type();
      if (!aParam->TypeName()->IsSameString(aName)) {
	if (aMethod->IsInline() || aMethod->IsFunctionCall() || aMethod->IsOperator()) {
	  MS::DispatchUsedType(aMeta,thetype,List,Incp,!aMethod->IsRefReturn());
	}
	else {
	  MS::DispatchUsedType(aMeta,thetype,List,Incp,Standard_False);
	}
      }
    }
  }

  Handle(MS_HArray1OfParam)      seqparam = aMethod->Params();
  
  if(!seqparam.IsNull()) {
    for (i = 1; i <= seqparam->Length(); i++) {
      if (!seqparam->Value(i)->TypeName()->IsSameString(aName)) {
	if (aMeta->IsDefined(seqparam->Value(i)->TypeName())) {
	  thetype = seqparam->Value(i)->Type();

	  // parametre avec valeur par defaut manipule par valeur
	  //
	  if (seqparam->Value(i)->IsKind(STANDARD_TYPE(MS_ParamWithValue))) {
	    MS::DispatchUsedType(aMeta,thetype,List,Incp,Standard_True);
	  }
	  else {
	    MS::DispatchUsedType(aMeta,thetype,List,Incp,Standard_False);
	  }
	}
      }
    }
  }
}

// sort the class used types :
//
//    List     : the types that must have a full definition
//    Incp     : the types that only have to be declared
//
void MS::ClassUsedTypes(const Handle(MS_MetaSchema)& aMeta,
			const Handle(MS_Class)& aClass,
			const Handle(TColStd_HSequenceOfHAsciiString)& List,
			const Handle(TColStd_HSequenceOfHAsciiString)& Incp)
{
  Standard_Integer                        i;
  Handle(MS_HSequenceOfField)             fields = aClass->GetFields();
  Handle(TColStd_HSequenceOfHAsciiString) asciiseq;
  Handle(TCollection_HAsciiString)        str;

  for (i = 1; i <= fields->Length(); i++) {
    if (aMeta->IsDefined(fields->Value(i)->TYpe())) {
      MS::DispatchUsedType(aMeta,aMeta->GetType(fields->Value(i)->TYpe()),List,Incp,Standard_True);
    }
  }
  
  asciiseq = aClass->GetRaises();

  for (i = 1; i <= asciiseq->Length(); i++) {
    MS::AddOnce(Incp,asciiseq->Value(i));
  }

  asciiseq = aClass->GetInheritsNames();

  for (i = 1; i <= asciiseq->Length(); i++) {
    MS::AddOnce(List,asciiseq->Value(i));
  }

  asciiseq = aClass->GetFriendsNames();

  for (i = 1; i <= asciiseq->Length(); i++) {
    MS::AddOnce(Incp,asciiseq->Value(i));
  }

  Handle(TCollection_HAsciiString) aname;

  asciiseq = aClass->GetFriendMets();
  Handle(MS_Method) friendmethod;

  for (i = 1; i <= asciiseq->Length(); i++) {
    friendmethod = MS::GetMethodFromFriendName(aMeta,asciiseq->Value(i));
    MS::MethodUsedTypes(aMeta,friendmethod,List,Incp);
    aname = asciiseq->Value(i)->Token(":");
    MS::AddOnce(List,aname);
  }

  Handle(MS_StdClass) aStdClass = Handle(MS_StdClass)::DownCast(aClass);

  if (!aStdClass.IsNull()) {
    if (!aStdClass->GetMyCreator().IsNull()) {
      asciiseq = aStdClass->GetMyCreator()->InstTypes();
      
      for (i = 1; i <= asciiseq->Length(); i++) {
	if (!aClass->FullName()->IsSameString(asciiseq->Value(i))) {
	  MS::DispatchUsedType(aMeta,aMeta->GetType(asciiseq->Value(i)),List,Incp,Standard_False);
	}
      }
    }
  }

  Handle(MS_HSequenceOfMemberMet) metseq = aClass->GetMethods();

  for (i = 1; i <= metseq->Length(); i++) {
    MS::MethodUsedTypes(aMeta,metseq->Value(i),List,Incp);
  }
}

// we look for classes that must be extracted
//
void MS::StubClassesToExtract(const Handle(MS_MetaSchema)& aMeta,
			      const Handle(TColStd_HSequenceOfHAsciiString)& aSeq,
			      WOKTools_MapOfHAsciiString& ExtractionMap,
			      WOKTools_MapOfHAsciiString& ExtractionIncpMap,
			      WOKTools_MapOfHAsciiString& ExtractionSemiMap)
{
  Standard_Integer                        i,
                                          j;
  Handle(TColStd_HSequenceOfHAsciiString) aSeqInh;    // inherits sequence
  Standard_Boolean                        IsExtractable;
  
  for (i = 1; i <= aSeq->Length(); i++) {
    IsExtractable = Standard_False;

    if (aMeta->IsDefined(aSeq->Value(i))) {
      Handle(MS_Type) tt = aMeta->GetType(aSeq->Value(i));

      if (tt->IsKind(STANDARD_TYPE(MS_GenClass)) || !tt->IsKind(STANDARD_TYPE(MS_StdClass))) {
	IsExtractable = Standard_False;
      }
      else if (tt->IsKind(STANDARD_TYPE(MS_StdClass)) && !tt->IsKind(STANDARD_TYPE(MS_Error))) {
	Handle(MS_StdClass) aClass = *((Handle(MS_StdClass)*)&tt);

	if (aClass->IsGeneric()) {
	  IsExtractable = Standard_False;
	}
	else {
	  IsExtractable = Standard_True;
	}
      }
      else {
	IsExtractable = Standard_False;
      }
    }
    
    if (!ExtractionMap.Contains(aSeq->Value(i)) && IsExtractable) {
      ExtractionMap.Add(aSeq->Value(i));
      //cout << "ExtractionMap ADD : " << aSeq->Value(i)->ToCString() << endl;
      if (ExtractionSemiMap.Contains(aSeq->Value(i))) {
	ExtractionSemiMap.Remove(aSeq->Value(i));
	//cout << "ExtractionSemiMap DELETE : " << aSeq->Value(i)->ToCString() << endl;
      }

      if (ExtractionIncpMap.Contains(aSeq->Value(i))) {
	ExtractionIncpMap.Remove(aSeq->Value(i));
	//cout << "ExtractionIncpMap DELETE : " << aSeq->Value(i)->ToCString() << endl;
      }

      
      if (!aMeta->IsDefined(aSeq->Value(i))) {
	ErrorMsg() << "MS" << "Class " << aSeq->Value(i) << " not defined..." << endm;
	Standard_NoSuchObject::Raise();
      }

      if (aMeta->GetType(aSeq->Value(i))->IsKind(STANDARD_TYPE(MS_StdClass)) && !aMeta->GetType(aSeq->Value(i))->IsKind(STANDARD_TYPE(MS_Error))) {
	Handle(MS_Type)     tmpType = aMeta->GetType(aSeq->Value(i));
	Handle(MS_StdClass) aClass = *((Handle(MS_StdClass)*)&tmpType);
	  
	MS::StubMethodsTypesToExtract(aMeta,aClass,ExtractionMap,ExtractionIncpMap,ExtractionSemiMap);

	aSeqInh = aClass->GetFullInheritsNames();
	
	for (j = 1; j <= aSeqInh->Length(); j++) {
	  if (!ExtractionMap.Contains(aSeqInh->Value(j))) {
	    Handle(MS_Type) tmpTypej = aMeta->GetType(aSeqInh->Value(j));

	    ExtractionMap.Add(aSeqInh->Value(j));
	    
	    aClass = *((Handle(MS_StdClass)*)&tmpTypej);

	    MS::StubMethodsTypesToExtract(aMeta,aClass,ExtractionMap,ExtractionIncpMap,ExtractionSemiMap);

	    if (ExtractionIncpMap.Contains(aSeqInh->Value(j))) {
	      ExtractionIncpMap.Remove(aSeqInh->Value(j));
	      //cout << "ExtractionIncpMap DELETE : " << aSeqInh->Value(j)->ToCString() << endl;
	    }

	    if (ExtractionSemiMap.Contains(aSeqInh->Value(j))) {
	      ExtractionSemiMap.Remove(aSeqInh->Value(j));
	      //cout << "ExtractionSemiMap DELETE : " << aSeqInh->Value(j)->ToCString() << endl;
	    }
	    
	  }
	}
	
      }
    }
  }
}


void MS::StubPackagesToExtract(const Handle(MS_MetaSchema)& aMeta,
			       const Handle(MS_Interface)& anInterface,
			       WOKTools_MapOfHAsciiString& ExtractionMap,
			       WOKTools_MapOfHAsciiString& ExtractionIncpMap,
			       WOKTools_MapOfHAsciiString& ExtractionSemiMap)
{
  Standard_Integer                        i,j;
  Handle(TColStd_HSequenceOfHAsciiString) aSeq,    // packages sequence
                                          aSeqClasses;
  Handle(MS_Package)                      aPackage;

  aSeq        = anInterface->Packages();
  aSeqClasses = new TColStd_HSequenceOfHAsciiString();

  for (i = 1; i <= aSeq->Length(); i++) {
    if (aMeta->IsPackage(aSeq->Value(i))) {
      aPackage = aMeta->GetPackage(aSeq->Value(i));

      if (!ExtractionMap.Contains(aSeq->Value(i))) {
	 ExtractionMap.Add(aSeq->Value(i));
	 for (j = 1; j <= aPackage->Enums()->Length(); j++) {
	   ExtractionMap.Add(MS::BuildFullName(aPackage->FullName(),aPackage->Enums()->Value(j)));
	 }
	 //cout << "ExtractionMap ADD : " << aSeq->Value(i)->ToCString() << endl;
      }

      aSeqClasses->Clear();

      for (j = 1; j <= aPackage->Classes()->Length(); j++) {
	aSeqClasses->Append(MS::BuildFullName(aPackage->FullName(),aPackage->Classes()->Value(j)));
      }

      MS::StubClassesToExtract(aMeta,aSeqClasses,ExtractionMap,ExtractionIncpMap,ExtractionSemiMap);
      MS::StubMethodsTypesToExtract(aMeta,aPackage,ExtractionMap,ExtractionIncpMap,ExtractionSemiMap);
    }
    else {
      ErrorMsg() << "MS" << "Package " << aSeq->Value(i) << " not defined..." << endm;
      Standard_NoSuchObject::Raise();
    }

  }
} 

void MS::StubMethodsToExtract(const Handle(MS_MetaSchema)& aMeta,
			      const Handle(MS_Interface)& anInterface,
			      const Handle(MS_HSequenceOfExternMet)& SeqOfExternMet,
			      const Handle(MS_HSequenceOfMemberMet)& SeqOfMemberMet,
			      WOKTools_MapOfHAsciiString& ExtractionMap,
			      WOKTools_MapOfHAsciiString& ExtractionIncpMap,
			      WOKTools_MapOfHAsciiString& ExtractionSemiMap)
{
  Standard_Integer                        i,j;
  Handle(TColStd_HSequenceOfHAsciiString) aseq = anInterface->Methods();
  Handle(MS_Method)                       amet;
  
  for (i = 1; i <= aseq->Length(); i++) {
    amet = MS::GetMethodFromFriendName(aMeta,aseq->Value(i));

    if (!amet.IsNull()) {
      if (amet->IsKind(STANDARD_TYPE(MS_ExternMet))) {
	Handle(MS_ExternMet) aemet = *((Handle(MS_ExternMet)*)&amet);

	if (!ExtractionMap.Contains(aemet->Package())) {
	  if (ExtractionIncpMap.Contains(aemet->Package())) {
	    ExtractionIncpMap.Remove(aemet->Package());
	    //cout << "ExtractionIncpMap DELETE : " << aemet->Package()->ToCString() << endl;
	  }

	  if (!ExtractionSemiMap.Contains(aemet->Package())) {
	    ExtractionSemiMap.Add(aemet->Package());
	    //cout << "ExtractionSemiMap ADD : " << aemet->Package()->ToCString() << endl;
	  }
	  
	  SeqOfExternMet->Append(aemet);
	  MS::StubMethodTypesToExtract(aMeta,aemet,ExtractionMap,ExtractionIncpMap,ExtractionSemiMap);
	}
      }
      else {
	Handle(MS_MemberMet) ammet = *((Handle(MS_MemberMet)*)&amet);

	if (!ExtractionMap.Contains(ammet->Class())) {
	  if (ExtractionIncpMap.Contains(ammet->Class())) {
	    ExtractionIncpMap.Remove(ammet->Class());
	    //cout << "ExtractionIncpMap DELETE : " << ammet->Class()->ToCString() << endl;
	  }

	  if (!ExtractionSemiMap.Contains(ammet->Class())) {
	    ExtractionSemiMap.Add(ammet->Class());
	    //cout << "ExtractionSemiMap ADD : " << ammet->Class()->ToCString() << endl;
	  }

	  SeqOfMemberMet->Append(ammet);
	  MS::StubMethodTypesToExtract(aMeta,ammet,ExtractionMap,ExtractionIncpMap,ExtractionSemiMap);
	  Handle(MS_Type)                         tmpTypejj = aMeta->GetType(ammet->Class());
	  Handle(MS_StdClass)                     aClass = *((Handle(MS_StdClass)*)&tmpTypejj);
	  Handle(TColStd_HSequenceOfHAsciiString) aSeqInh; 

	  aSeqInh = aClass->GetFullInheritsNames();

	  for (j = 1; j <= aSeqInh->Length(); j++) {
	    if (!ExtractionSemiMap.Contains(aSeqInh->Value(j)) && 
		!ExtractionMap.Contains(aSeqInh->Value(j)) && 
		!ExtractionIncpMap.Contains(aSeqInh->Value(j)))
	      {
		ExtractionIncpMap.Add(aSeqInh->Value(j));
		//cout << "ExtractionIncpMap ADD : " << aSeqInh->Value(j)->ToCString() << endl;
	      }
	  }
	}
      }
    }
    else {
      ErrorMsg() << "MS" << "Method " << aseq->Value(i) << " not defined..." << endm;
      Standard_NoSuchObject::Raise();
    }
  }
}

void MS::StubMethodTypesToExtract(const Handle(MS_MetaSchema)& aMeta,
				  const Handle(MS_Method)&     aMethod,
				  WOKTools_MapOfHAsciiString&  ExtractionMap,
				  WOKTools_MapOfHAsciiString&  ExtractionIncpMap,
				  WOKTools_MapOfHAsciiString&  ExtractionSemiMap)
{
  Standard_Integer                 j,k;
  Handle(MS_HArray1OfParam)        params;
  Handle(TCollection_HAsciiString) parname;
  Handle(MS_Type)                  type;
  Handle(MS_Class)                 aClass;
  Handle(MS_Package)               aPackage;
  Handle(MS_Param)                 returnType;

  params     = aMethod->Params();
  returnType = aMethod->Returns();

  if(!params.IsNull()) {
    for (j = 1; j <= params->Length(); j++) {
      parname = params->Value(j)->TypeName();
      
      if (aMeta->IsDefined(parname)) {
	type = aMeta->GetType(parname);

	if (type->IsKind(STANDARD_TYPE(MS_Alias))) {
	  Handle(MS_Alias) analias = *((Handle(MS_Alias)*)&type);
	  
	  parname = analias->DeepType();
	  
	  if (aMeta->IsDefined(parname)) {
	    type = aMeta->GetType(parname);
	  }
	  else {
	    ErrorMsg() << "MS" << "Type " << parname << " not defined..." << endm;
	    Standard_NoSuchObject::Raise();
	  }
	}
	
	
	if (type->IsKind(STANDARD_TYPE(MS_StdClass)) && !type->IsKind(STANDARD_TYPE(MS_Error))) {
	  Handle(MS_StdClass) stdclass = *((Handle(MS_StdClass)*)&type);
	  
	  if (!ExtractionIncpMap.Contains(parname) && !ExtractionMap.Contains(parname) && !ExtractionSemiMap.Contains(parname)) {
	    ExtractionIncpMap.Add(parname);
	    //cout << "ExtractionIncpMap ADD : " << parname->ToCString() << endl;
	    Handle(TColStd_HSequenceOfHAsciiString) inh = stdclass->GetFullInheritsNames();

	    for (k = 1; k <= inh->Length(); k++) {
	      if (!ExtractionIncpMap.Contains(inh->Value(k)) && 
		  !ExtractionMap.Contains(inh->Value(k)) && 
		  !ExtractionSemiMap.Contains(inh->Value(k))) {
		ExtractionIncpMap.Add(inh->Value(k));
		//cout << "ExtractionIncpMap ADD : " << inh->Value(k)->ToCString() << endl;
	      }
	    }
	  }
	}
	else if (type->IsKind(STANDARD_TYPE(MS_Enum))) {
	  if (!ExtractionMap.Contains(type->FullName())) {
	    ExtractionMap.Add(type->FullName());
	  }
	}
      }
      else {
	ErrorMsg() << "MS" << "Type " << parname << " not defined..." << endm;
	Standard_NoSuchObject::Raise();
      }
    }
  }
  
  if (!returnType.IsNull()) {
    parname = returnType->TypeName();

    if (aMeta->IsDefined(parname)) {
      type = aMeta->GetType(parname);
      
      if (type->IsKind(STANDARD_TYPE(MS_Alias))) {
	Handle(MS_Alias) analias = *((Handle(MS_Alias)*)&type);
	
	parname = analias->DeepType();
	
	if (aMeta->IsDefined(parname)) {
	  type = aMeta->GetType(parname);
	}
	else {
	  ErrorMsg() << "MS" << "Type " << parname << " not defined..." << endm;
	  Standard_NoSuchObject::Raise();
	}
      }
      
      if (type->IsKind(STANDARD_TYPE(MS_StdClass)) && !type->IsKind(STANDARD_TYPE(MS_Error))) {
	Handle(MS_StdClass) stdclass = *((Handle(MS_StdClass)*)&type);
	
	if (!ExtractionIncpMap.Contains(type->FullName()) && 
	    !ExtractionMap.Contains(type->FullName()) && 
	    !ExtractionSemiMap.Contains(type->FullName())) {
	  ExtractionIncpMap.Add(type->FullName());
	  //cout << "ExtractionIncpMap ADD : " << type->FullName()->ToCString() << endl;
	  Handle(TColStd_HSequenceOfHAsciiString) inh = stdclass->GetFullInheritsNames();

	  for (k = 1; k <= inh->Length(); k++) {
	    if (!ExtractionIncpMap.Contains(inh->Value(k)) && !ExtractionMap.Contains(inh->Value(k)) && !ExtractionSemiMap.Contains(inh->Value(k))) {
	      ExtractionIncpMap.Add(inh->Value(k));
	      //cout << "ExtractionIncpMap ADD : " << inh->Value(k)->ToCString() << endl;
	    }
	  }
	}
      }
      else if (type->IsKind(STANDARD_TYPE(MS_Enum))) {
	if (!ExtractionMap.Contains(type->FullName())) {
	  ExtractionMap.Add(type->FullName());
	}
      }
    }
    else {
      ErrorMsg() << "MS" << "Type " << parname << " not defined..." << endm;
      Standard_NoSuchObject::Raise();
    }
  }
}

// we look for parameters types that must be extracted
//
void MS::StubMethodsTypesToExtract(const Handle(MS_MetaSchema)& aMeta,
				   const Handle(MS_Common)&     aCommon,
				   WOKTools_MapOfHAsciiString&  ExtractionMap,
				   WOKTools_MapOfHAsciiString&  ExtractionIncpMap,
				   WOKTools_MapOfHAsciiString& ExtractionSemiMap)
{
  Standard_Integer                 i,j,k,len;
  Handle(MS_HSequenceOfMemberMet)  aSeqMM;
  Handle(MS_HSequenceOfExternMet)  aSeqEM;
  Handle(MS_HArray1OfParam)      params;
  Handle(TCollection_HAsciiString) parname;
  Handle(MS_Type)                  type;
  Handle(MS_Class)                 aClass;
  Handle(MS_Package)               aPackage;
  Standard_Boolean                 IsClass;
  Handle(MS_Param)                 returnType;

  if (aCommon->IsKind(STANDARD_TYPE(MS_Class))) {
    IsClass = Standard_True;
    aClass  = *((Handle(MS_Class)*)&aCommon);
    aSeqMM  = aClass->GetMethods();
    len     = aSeqMM->Length();
  }
  else {
    IsClass  = Standard_False;
    aPackage = *((Handle(MS_Package)*)&aCommon);
    aSeqEM   = aPackage->Methods();
    len      = aSeqEM->Length();
  }


  for (i = 1; i <= len; i++) {
    if (IsClass) {
      params = aSeqMM->Value(i)->Params();
      returnType = aSeqMM->Value(i)->Returns();
    }
    else {
      //cout << "StubMethodsTypesToExtract :: " << aSeqEM->Value(i)->FullName()->ToCString();
      params = aSeqEM->Value(i)->Params();
      returnType = aSeqEM->Value(i)->Returns();
    }
    
    if(!params.IsNull()) {
      for (j = 1; j <= params->Length(); j++) {
	parname = params->Value(j)->TypeName();

	if (aMeta->IsDefined(parname)) {
	  type = aMeta->GetType(parname);

	  if (type->IsKind(STANDARD_TYPE(MS_Alias))) {
	    Handle(MS_Alias) analias = *((Handle(MS_Alias)*)&type);

	    parname = analias->DeepType();

	    if (aMeta->IsDefined(parname)) {
	      type = aMeta->GetType(parname);
	    }
	    else {
	      ErrorMsg() << "MS" << "Type " << parname << " not defined..." << endm;
	      Standard_NoSuchObject::Raise();
	    }
	  }

	  if (type->IsKind(STANDARD_TYPE(MS_StdClass)) && !type->IsKind(STANDARD_TYPE(MS_Error))) {
	    Handle(MS_StdClass) stdclass = *((Handle(MS_StdClass)*)&type);

	    if (!ExtractionIncpMap.Contains(parname) && !ExtractionMap.Contains(parname) && !ExtractionSemiMap.Contains(parname)) {
	      ExtractionIncpMap.Add(parname);
	      //cout << "ExtractionIncpMap ADD : " << parname->ToCString() << endl;
	      Handle(TColStd_HSequenceOfHAsciiString) inh = stdclass->GetFullInheritsNames();

	      for (k = 1; k <= inh->Length(); k++) {
		if (!ExtractionIncpMap.Contains(inh->Value(k)) && !ExtractionMap.Contains(inh->Value(k)) && !ExtractionSemiMap.Contains(inh->Value(k))) {
		  ExtractionIncpMap.Add(inh->Value(k));
		  //cout << "ExtractionIncpMap ADD : " << inh->Value(k)->ToCString() << endl;
		}
	      }
	    }
	  }
	  else if (type->IsKind(STANDARD_TYPE(MS_Enum))) {
	    if (!ExtractionMap.Contains(type->FullName())) {
	      ExtractionMap.Add(type->FullName());
	    }
	  }
	}
	else {
	  ErrorMsg() << "MS" << "Type " << parname << " not defined..." << endm;
	  Standard_NoSuchObject::Raise();
	}
      }
    }
    
    if (!returnType.IsNull()) {
      parname = returnType->TypeName();

      if (aMeta->IsDefined(parname)) {
	type = aMeta->GetType(parname);

	if (type->IsKind(STANDARD_TYPE(MS_Alias))) {
	  Handle(MS_Alias) analias = *((Handle(MS_Alias)*)&type);
	  
	  parname = analias->DeepType();
	  
	  if (aMeta->IsDefined(parname)) {
	    type = aMeta->GetType(parname);
	  }
	  else {
	    ErrorMsg() << "MS" << "Type " << parname << " not defined..." << endm;
	    Standard_NoSuchObject::Raise();
	  }
	}

	if (type->IsKind(STANDARD_TYPE(MS_StdClass)) && !type->IsKind(STANDARD_TYPE(MS_Error))) {
	  Handle(MS_StdClass) stdclass = *((Handle(MS_StdClass)*)&type);

	  if (!ExtractionIncpMap.Contains(type->FullName()) && 
	      !ExtractionMap.Contains(type->FullName()) && 
	      !ExtractionSemiMap.Contains(type->FullName())) {
	    ExtractionIncpMap.Add(type->FullName());
	    //cout << "ExtractionIncpMap ADD : " << type->FullName()->ToCString() << endl;
	    Handle(TColStd_HSequenceOfHAsciiString) inh = stdclass->GetFullInheritsNames();

	    for (k = 1; k <= inh->Length(); k++) {
	      if (!ExtractionIncpMap.Contains(inh->Value(k)) && 
		  !ExtractionMap.Contains(inh->Value(k)) && 
		  !ExtractionSemiMap.Contains(inh->Value(k))) {
		ExtractionIncpMap.Add(inh->Value(k));
		//cout << "ExtractionIncpMap ADD : " << inh->Value(k)->ToCString() << endl;
	      }
	    }
	  }
	}
	else if (type->IsKind(STANDARD_TYPE(MS_Enum))) {
	  if (!ExtractionMap.Contains(type->FullName())) {
	    ExtractionMap.Add(type->FullName());
	  }
	}
      }
      else {
	ErrorMsg() << "MS" << "Type " << parname << " not defined..." << endm;
	Standard_NoSuchObject::Raise();
      }
    }
  }

  //cout << "StubMethodsTypesToExtract :: END" << endl;
}
