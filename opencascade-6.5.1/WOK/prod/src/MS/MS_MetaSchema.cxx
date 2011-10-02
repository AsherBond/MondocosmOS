#include <MS_MetaSchema.ixx>
#include <MS.hxx>

#include <MS_Package.hxx>

#include <MS_Class.hxx>
#include <MS_Method.hxx>
#include <MS_ExternMet.hxx>
#include <MS_MemberMet.hxx>
#include <MS_StdClass.hxx>
#include <MS_InstClass.hxx>
#include <MS_InstMet.hxx>
#include <MS_Pointer.hxx>
#include <MS_Field.hxx>
#include <MS_PrimType.hxx>
#include <MS_GenType.hxx>
#include <MS_GenClass.hxx>
#include <MS_Param.hxx>
#include <MS_Enum.hxx>
#include <MS_Alias.hxx>
#include <MS_Imported.hxx>

#include <MS_DataMapIteratorOfMapOfGlobalEntity.hxx>
#include <MS_DataMapIteratorOfMapOfType.hxx>
#include <MS_DataMapIteratorOfMapOfMethod.hxx>

#include <MS_HSequenceOfField.hxx>
#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_HSequenceOfExternMet.hxx>
#include <MS_HSequenceOfMemberMet.hxx>
#include <MS_HSequenceOfGenType.hxx>
#include <MS_HSequenceOfParam.hxx>
#include <MS_HArray1OfParam.hxx>

#include <WOKTools_Messages.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <Standard_NullObject.hxx>

#include <WOKTools_MapOfHAsciiString.hxx>

#include <MS_HSequenceOfClass.hxx>

#define CHECKERROR "Check"

MS_MetaSchema::MS_MetaSchema()
{
}

Standard_Boolean MS_MetaSchema::AddPackage(const Handle(MS_Package)& aCommon)
{
  Standard_Boolean result = Standard_False;

  if (!aCommon.IsNull()) {
    if (!myPackages.IsBound(aCommon->FullName())) {
      result = Standard_True;
      myPackages.Bind(aCommon->FullName(),aCommon);
    }
    else {
      result = Standard_False;
    }
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema:AddPackage: - aCommon is NULL");
  }

  return result;
}

Standard_Boolean MS_MetaSchema::AddEngine(const Handle(MS_Engine)& aCommon) 
{
  Standard_Boolean result = Standard_False;

  if (!aCommon.IsNull()) {
    if (!myEngines.IsBound(aCommon->FullName())) {
      result = Standard_True;
      myEngines.Bind(aCommon->FullName(),aCommon);
    }
    else {
      result = Standard_False;
    }
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema:AddEngine: - aCommon is NULL");
  }

  return result;
}

Standard_Boolean MS_MetaSchema::AddComponent(const Handle(MS_Component)& aCommon) 
{
  Standard_Boolean result = Standard_False;

  if (!aCommon.IsNull()) {
    if (!myComponents.IsBound(aCommon->FullName())) {
      result = Standard_True;
      myComponents.Bind(aCommon->FullName(),aCommon);
    }
    else {
      result = Standard_False;
    }
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema:AddComponent: - aCommon is NULL");
  }

  return result;
}

Standard_Boolean MS_MetaSchema::AddSchema(const Handle(MS_Schema)& aCommon) 
{
  Standard_Boolean result = Standard_False;

  if (!aCommon.IsNull()) {
    if (!mySchemas.IsBound(aCommon->FullName())) {
      result = Standard_True;
      mySchemas.Bind(aCommon->FullName(),aCommon);
    }
    else {
      result = Standard_False;
    }
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::AddSchemas - aCommon is NULL");
  }

  return result;
}

Standard_Boolean MS_MetaSchema::AddExecutable(const Handle(MS_Executable)& aCommon) 
{
  Standard_Boolean result = Standard_False;

  if (!aCommon.IsNull()) {
    if (!myExecutables.IsBound(aCommon->FullName())) {
      result = Standard_True;
      myExecutables.Bind(aCommon->FullName(),aCommon);
    }
    else {
      result = Standard_False;
    }
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::AddExecutable - aCommon is NULL");
  }

  return result;
}

Standard_Boolean MS_MetaSchema::AddInterface(const Handle(MS_Interface)& aCommon) 
{
  Standard_Boolean result = Standard_False;

  if (!aCommon.IsNull()) {
    if (!myInterfaces.IsBound(aCommon->FullName())) {
      result = Standard_True;
      myInterfaces.Bind(aCommon->FullName(),aCommon);
    }
    else {
      result = Standard_False;
    }
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::AddInterface - aCommon is NULL");
  }

  return result;
}

Standard_Boolean MS_MetaSchema::AddType(const Handle(MS_Type)& aCommon) 
{
  Standard_Boolean result = Standard_False;
  
  if (!aCommon.IsNull()) {
    if (!myTypes.IsBound(aCommon->FullName())) {
      result = Standard_True;
      myTypes.Bind(aCommon->FullName(),aCommon);
    }
    else {
      result = Standard_False;
    }
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::AddType - aCommon is NULL");
  }

  return result;
}

Standard_Boolean MS_MetaSchema::AddMethod(const Handle(MS_Method)& aCommon)
{
  Standard_Boolean result = Standard_False;

  if (!aCommon.IsNull()) {
    if (!myMethods.IsBound(aCommon->FullName())) {
      result = Standard_True;
      myMethods.Bind(aCommon->FullName(),aCommon);
    }
    else {
      result = Standard_False;
    }
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::AddMethod - aCommon is NULL");
  }

  return result;
}

MS_DataMapIteratorOfMapOfGlobalEntity MS_MetaSchema::Executables() const 
{
  MS_DataMapIteratorOfMapOfGlobalEntity anIterator(myExecutables);

  return anIterator;
}

MS_DataMapIteratorOfMapOfGlobalEntity MS_MetaSchema::Components() const 
{
  MS_DataMapIteratorOfMapOfGlobalEntity anIterator(myComponents);

  return anIterator;
}

MS_DataMapIteratorOfMapOfGlobalEntity MS_MetaSchema::Engines() const 
{
  MS_DataMapIteratorOfMapOfGlobalEntity anIterator(myEngines);

  return anIterator;
}

MS_DataMapIteratorOfMapOfGlobalEntity MS_MetaSchema::Schemas() const 
{
  MS_DataMapIteratorOfMapOfGlobalEntity anIterator(mySchemas);

  return anIterator;
}

MS_DataMapIteratorOfMapOfGlobalEntity MS_MetaSchema::Interfaces() const 
{
  MS_DataMapIteratorOfMapOfGlobalEntity anIterator(myInterfaces);

  return anIterator;
}

MS_DataMapIteratorOfMapOfGlobalEntity MS_MetaSchema::Packages() const 
{
  MS_DataMapIteratorOfMapOfGlobalEntity anIterator(myPackages);
  
  return anIterator;
}

MS_DataMapIteratorOfMapOfType MS_MetaSchema::Types() const 
{
  MS_DataMapIteratorOfMapOfType anIterator(myTypes);
  
  return anIterator;
}

MS_DataMapIteratorOfMapOfMethod MS_MetaSchema::Methods() const 
{
  MS_DataMapIteratorOfMapOfMethod anIterator(myMethods);

  return anIterator;
}

const Handle(MS_Executable)& MS_MetaSchema::GetExecutable(const Handle(TCollection_HAsciiString)& anExecutable) const 
{
  static Handle(MS_Executable) NULLRESULT;

  if (!anExecutable.IsNull()) {
    return *((Handle(MS_Executable) *) &(myExecutables.Find(anExecutable)));
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::GetExecutable - anExecutable is NULL");
  }

  return NULLRESULT;
}

const Handle(MS_Engine)& MS_MetaSchema::GetEngine(const Handle(TCollection_HAsciiString)& anEngine) const 
{
  static Handle(MS_Engine) NULLRESULT;

  if (!anEngine.IsNull()) {
    return *((Handle(MS_Engine) *) &(myEngines.Find(anEngine)));
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::GetEngine - anEngine is NULL");
  }

  return NULLRESULT;
}

const Handle(MS_Component)& MS_MetaSchema::GetComponent(const Handle(TCollection_HAsciiString)& anComponent) const 
{
  static Handle(MS_Component) NULLRESULT;

  if (!anComponent.IsNull()) {
    return *((Handle(MS_Component) *) &(myComponents.Find(anComponent)));
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::GetComponent - anComponent is NULL");
  }

  return NULLRESULT;
}

const Handle(MS_Schema)& MS_MetaSchema::GetSchema(const Handle(TCollection_HAsciiString)& aSchema) const 
{
  static Handle(MS_Schema) NULLRESULT;

  if (!aSchema.IsNull()) {
    return *((Handle(MS_Schema) *) &(mySchemas.Find(aSchema)));
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::GetSchema - aSchema is NULL");
  }

  return NULLRESULT; 
}

const Handle(MS_Interface)& MS_MetaSchema::GetInterface(const Handle(TCollection_HAsciiString)& anInterface) const 
{
  static Handle(MS_Interface) NULLRESULT;

  if (!anInterface.IsNull()) {
    return *((Handle(MS_Interface) *) &(myInterfaces.Find(anInterface)));
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::GetInterface - anInterface is NULL");
  }

  return NULLRESULT;
}

const Handle(MS_Package)& MS_MetaSchema::GetPackage(const Handle(TCollection_HAsciiString)& aPackage) const 
{
  static Handle(MS_Package) NULLRESULT;

  if (!aPackage.IsNull()) {
    return  *((Handle(MS_Package) *) &(myPackages.Find(aPackage)));
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::GetPackage - aPackage is NULL");
  }
  
  return NULLRESULT;
}

void MS_MetaSchema::RemoveExecutable(const Handle(TCollection_HAsciiString)& aPackage)
{
   if (!aPackage.IsNull()) {
     if (myExecutables.IsBound(aPackage))
       myExecutables.UnBind(aPackage);
   }
}

void MS_MetaSchema::RemoveEngine(const Handle(TCollection_HAsciiString)& aPackage)
{
  if (!aPackage.IsNull()) {
    if (myEngines.IsBound(aPackage))
      myEngines.UnBind(aPackage);
  }
}
void MS_MetaSchema::RemoveComponent(const Handle(TCollection_HAsciiString)& aPackage)
{
  if (!aPackage.IsNull()) {
    if (myComponents.IsBound(aPackage))
      myComponents.UnBind(aPackage);
  }
}

void MS_MetaSchema::RemoveSchema(const Handle(TCollection_HAsciiString)& aPackage)
{
  if (!aPackage.IsNull()) {
    if (mySchemas.IsBound(aPackage))
    mySchemas.UnBind(aPackage);
  }
}

void MS_MetaSchema::RemoveInterface(const Handle(TCollection_HAsciiString)& aPackage)
{
  if (!aPackage.IsNull()) {
    if (myInterfaces.IsBound(aPackage))
      myInterfaces.UnBind(aPackage);
  }
}

void MS_MetaSchema::RemovePackage(const Handle(TCollection_HAsciiString)& aPackage)
{
  Handle(MS_Package)                      result;
  Handle(TColStd_HSequenceOfHAsciiString) aSeq;
  Handle(MS_HSequenceOfExternMet)         aSeqMet;

  if (!aPackage.IsNull()) {
    Standard_Integer i;

    result = GetPackage(aPackage);

    aSeq = result->Classes();

    for(i = 1; i <= aSeq->Length(); i++) {
      RemoveType(MS::BuildFullName(aPackage,aSeq->Value(i)),Standard_False);
    }

    aSeq = result->Enums();

    for(i = 1; i <= aSeq->Length(); i++) {
      RemoveType(MS::BuildFullName(aPackage,aSeq->Value(i)),Standard_False);
    }

    aSeq = result->Excepts();

    for(i = 1; i <= aSeq->Length(); i++) {
      RemoveType(MS::BuildFullName(aPackage,aSeq->Value(i)),Standard_False);
    }

    aSeq = result->Aliases();

    for(i = 1; i <= aSeq->Length(); i++) {
      RemoveType(MS::BuildFullName(aPackage,aSeq->Value(i)),Standard_False);
    }

    aSeq = result->Pointers();

    for(i = 1; i <= aSeq->Length(); i++) {
      RemoveType(MS::BuildFullName(aPackage,aSeq->Value(i)),Standard_False);
    }

    aSeq = result->Importeds();

    for(i = 1; i <= aSeq->Length(); i++) {
      RemoveType(MS::BuildFullName(aPackage,aSeq->Value(i)),Standard_False);
    }

    aSeq = result->Primitives();

    for(i = 1; i <= aSeq->Length(); i++) {
      RemoveType(MS::BuildFullName(aPackage,aSeq->Value(i)),Standard_False);
    }

    aSeqMet = result->Methods();

    for(i = 1; i <= aSeqMet->Length(); i++) {
      RemoveMethod(aSeqMet->Value(i)->FullName());
    }

    myPackages.UnBind(aPackage);
  }
}

const Handle(MS_Type)& MS_MetaSchema::GetType(const Handle(TCollection_HAsciiString)& aType) const 
{
  static Handle(MS_Type) NULLRESULT;

  if (!aType.IsNull()) {
    return (*((Handle(MS_Type)*)&myTypes.Find(aType)));
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::GetType - aType is NULL");
  }

  return NULLRESULT;
}

void MS_MetaSchema::RemoveType(const Handle(TCollection_HAsciiString)& aType,
			       const Standard_Boolean mustUpdatePackage)
{
  Handle(MS_Type)    aMSType;
  Handle(MS_Package) aPackage;

  if (!aType.IsNull()) {
    if (!IsDefined(aType)) {
      return;
    }
//    cout << "--> KILL " << aType->ToCString() << endl;
    aMSType = GetType(aType);

    if (aMSType->IsKind(STANDARD_TYPE(MS_Class))) {
      Handle(MS_Class)                aClass = *((Handle(MS_Class)*)&aMSType);
      Handle(MS_HSequenceOfMemberMet) aMetSeq = aClass->GetMethods();
      Standard_Integer                i;
      
      if (mustUpdatePackage) {
	Handle(TColStd_HSequenceOfHAsciiString) aSeq;
	Standard_Integer                        i;

	aPackage = aClass->Package();
	aSeq = aPackage->Classes();

	for(i = 1; i <= aSeq->Length(); i++) {
	  if (aType->IsSameString(MS::BuildFullName(aPackage->Name(),aSeq->Value(i)))) {
	    aSeq->Remove(i);
	    break;
	  }
	}
      }

      for (i = 1; i <= aMetSeq->Length(); i++) {
	RemoveMethod(aMetSeq->Value(i)->FullName());
      }

      myTypes.UnBind(aType);

      if (aClass->IsKind(STANDARD_TYPE(MS_StdClass))) {
	Handle(MS_StdClass) stdclass = *((Handle(MS_StdClass)*)&aClass);

	if (!stdclass->GetMyCreator().IsNull()){
	  Handle(MS_InstClass) insclass = stdclass->GetMyCreator();

	  for (i = 1; i <= insclass->GetNestedStdClassesName()->Length(); i++) {
	    RemoveType(MS::BuildFullName(insclass->Package()->Name(),insclass->GetNestedStdClassesName()->Value(i)));
	  }
	  insclass->GetNestedStdClassesName()->Clear();

	  for (i = 1; i <= insclass->GetNestedInsClassesName()->Length(); i++) {
	    RemoveType(MS::BuildFullName(insclass->Package()->Name(),insclass->GetNestedInsClassesName()->Value(i)));
	  }
	  insclass->GetNestedInsClassesName()->Clear();

	  for (i = 1; i <= insclass->GetNestedNeuClassesName()->Length(); i++) {
	    RemoveType(MS::BuildFullName(insclass->Package()->Name(),insclass->GetNestedNeuClassesName()->Value(i)));
	  }
	  insclass->GetNestedNeuClassesName()->Clear();
	}
      } 
      else if (aClass->IsKind(STANDARD_TYPE(MS_GenClass))) {
	Handle(MS_GenClass) genclass = *((Handle(MS_GenClass)*)&aClass);

	for (i = 1; i <= genclass->GetNestedStdClassesName()->Length(); i++) {
//	  cout << "NestedStdClassesName :" << genclass->GetNestedStdClassesName()->Value(i)->ToCString() << endl;
	  RemoveType(MS::BuildFullName(genclass->Package()->Name(),genclass->GetNestedStdClassesName()->Value(i)),Standard_False);
	}

	for (i = 1; i <= genclass->GetNestedName()->Length(); i++) {
//	  cout << "NestedName :" << genclass->GetNestedName()->Value(i)->ToCString() << endl;
	  RemoveType(MS::BuildFullName(genclass->Package()->Name(),genclass->GetNestedName()->Value(i)));
	}

	for (i = 1; i <= genclass->GetNestedInsClassesName()->Length(); i++) {
//	  cout << "NestedInsClassesName :" << genclass->GetNestedInsClassesName()->Value(i)->ToCString() << endl;
	  RemoveType(MS::BuildFullName(genclass->Package()->Name(),genclass->GetNestedInsClassesName()->Value(i)));
	}
      }
    }
    else {
      myTypes.UnBind(aType);
    }
  }
}

const Handle(MS_Method)& MS_MetaSchema::GetMethod(const Handle(TCollection_HAsciiString)& aMethod) const 
{
  static Handle(MS_Method) NULLRESULT;

  if (!aMethod.IsNull()) {
    return *((Handle(MS_Method)*)&myMethods.Find(aMethod));
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::GetMethod - aType is NULL");
  }

  return NULLRESULT;
}

void MS_MetaSchema::RemoveMethod(const Handle(TCollection_HAsciiString)& aMethod)
{
  if (!aMethod.IsNull()) {
    myMethods.UnBind(aMethod);
  }
}

Standard_Boolean MS_MetaSchema::IsExecutable(const Handle(TCollection_HAsciiString)& anExecutable) const 
{
  Standard_Boolean result = Standard_False;
  
  if (!anExecutable.IsNull()) {
    result = myExecutables.IsBound(anExecutable);
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::IsExecutable - anExecutable is NULL");
  }
  
  return result;
}

Standard_Boolean MS_MetaSchema::IsEngine(const Handle(TCollection_HAsciiString)& anEngine) const 
{
  Standard_Boolean result = Standard_False;
  
  if (!anEngine.IsNull()) {
    result = myEngines.IsBound(anEngine);
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::IsEngine - anEngine is NULL");
  }
  
  return result;
}
Standard_Boolean MS_MetaSchema::IsComponent(const Handle(TCollection_HAsciiString)& anComponent) const 
{
  Standard_Boolean result = Standard_False;
  
  if (!anComponent.IsNull()) {
    result = myComponents.IsBound(anComponent);
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::IsComponent - anComponent is NULL");
  }
  
  return result;
}

Standard_Boolean MS_MetaSchema::IsSchema(const Handle(TCollection_HAsciiString)& aSchema) const 
{
  Standard_Boolean result = Standard_False;
  
  if (!aSchema.IsNull()) {
    result = mySchemas.IsBound(aSchema);
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::IsSchema - aSchema is NULL");
  }
  
  return result;
}

Standard_Boolean MS_MetaSchema::IsInterface(const Handle(TCollection_HAsciiString)& anInterface) const 
{
  Standard_Boolean result = Standard_False;
  
  if (!anInterface.IsNull()) {
    result = myInterfaces.IsBound(anInterface);
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::IsInterface - anInterface is NULL");
  }
  
  return result;
}

Standard_Boolean MS_MetaSchema::IsPackage(const Handle(TCollection_HAsciiString)& aPackage) const 
{
  Standard_Boolean result = Standard_False;

  if (!aPackage.IsNull()) {
    result = myPackages.IsBound(aPackage);
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::IsPackage - aPackage is NULL");
  }
  
  return result;
}

Standard_Boolean MS_MetaSchema::IsDefined(const Handle(TCollection_HAsciiString)& aType, const Handle(TCollection_HAsciiString)& aPackage) const 
{
  Standard_Boolean                 result = Standard_False;
  Handle(TCollection_HAsciiString) FullName = new TCollection_HAsciiString(aPackage);

  FullName->AssignCat("_");
  FullName->AssignCat(aType);

  result = IsDefined(FullName);

  return result;
}

Standard_Boolean MS_MetaSchema::IsDefined(const Handle(TCollection_HAsciiString)& aType) const 
{
  Standard_Boolean result = Standard_False;
  
  if (!aType.IsNull()) {
    result = myTypes.IsBound(aType);
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::IsDefined - aType is NULL");
  }
  
  return result;
}

Standard_Boolean MS_MetaSchema::IsMethod(const Handle(TCollection_HAsciiString)& aMethod) const 
{
  Standard_Boolean result = Standard_False;
  
  if (!aMethod.IsNull()) {
    result = myMethods.IsBound(aMethod);
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::IsMethod - aMethod is NULL");
  }
  
  return result;
}


Handle(MS_HSequenceOfInstClass) MS_MetaSchema::GetInstantiations(const Handle(TCollection_HAsciiString)& aGenClass) const
{
  Handle(MS_HSequenceOfInstClass) aSeq = new MS_HSequenceOfInstClass;
  Handle(MS_Type)                 anEntity;
  MS_DataMapIteratorOfMapOfType   anIterator(myTypes);
  Handle(MS_StdClass)             aClass;
  Handle(MS_InstClass)            anInst;

  while(anIterator.More()) {
    anEntity = anIterator.Value();
    aClass   = Handle(MS_StdClass)::DownCast(anEntity);

    if (!aClass.IsNull()) {
      if (!aClass->GetMyCreator().IsNull()) {
	anInst = aClass->GetMyCreator();

	if (anInst->GenClass()->IsSameString(aGenClass)) {
	  aSeq->Append(anInst);
	}
      }
    }
    anIterator.Next();
  }

  return aSeq;
}

Handle(TColStd_HSequenceOfHAsciiString) MS_MetaSchema::GetPersistentClassesFromSchema(const Handle(TCollection_HAsciiString)& aSchema, const Standard_Boolean withStorable) const
{
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;

  if (IsSchema(aSchema)) {
    WOKTools_MapOfHAsciiString              aMap;
    Standard_Integer                        i,j,k;
    Handle(MS_Schema)                       theSchema;
    Handle(MS_Type)                         theType;
    Handle(MS_StdClass)                     theStdClass,anOther;
    Handle(TColStd_HSequenceOfHAsciiString) classList,
                                            packageList;

    theSchema = GetSchema(aSchema);

    classList = theSchema->GetClasses();

    for (i = 1; i <= classList->Length(); i++) {
      if (!aMap.Contains(classList->Value(i))) {
//	cout << "MS_MetaSchema::GetPersistentClassesFromSchema : " << classList->Value(i)->ToCString() << endl;

	if (IsDefined(classList->Value(i))) {
	  theType = GetType(classList->Value(i));

	  if (theType->IsKind(STANDARD_TYPE(MS_StdClass))) {
	    theStdClass = Handle(MS_StdClass)::DownCast(theType);

	    if ((theStdClass->IsPersistent() || (withStorable && theStdClass->IsStorable())) && !theStdClass->IsGeneric()) {
	      aMap.Add(classList->Value(i));
	      result->Append(classList->Value(i));
	    }

	    if (!theStdClass->GetMyCreator().IsNull()) {
	      Handle(MS_InstClass)                    anInst = theStdClass->GetMyCreator();
	      Handle(TColStd_HSequenceOfHAsciiString) inslist;

	      inslist = anInst->GetNestedStdClassesName();
	      
	      for (k = 1; k <= inslist->Length(); k++) {
		theType = GetType(MS::BuildFullName(theStdClass->Package()->Name(),inslist->Value(k)));
		anOther = Handle(MS_StdClass)::DownCast(theType);

		if ((anOther->IsPersistent() || (withStorable && anOther->IsStorable())) && !anOther->IsGeneric()) {
		  aMap.Add(theType->FullName());
		  result->Append(theType->FullName());
//		  cout << "MS_MetaSchema::GetPersistentClassesFromSchema : " << inslist->Value(k)->ToCString() << endl;
		}
	      }

	      inslist = anInst->GetNestedInsClassesName();
	      
	      for (k = 1; k <= inslist->Length(); k++) {
		theType = GetType(MS::BuildFullName(theStdClass->Package()->Name(),inslist->Value(k)));
		anOther = Handle(MS_StdClass)::DownCast(theType);
		
		if (!anOther.IsNull()) {
		  if ((anOther->IsPersistent() || (withStorable && anOther->IsStorable())) && !anOther->IsGeneric()) {
		    aMap.Add(theType->FullName());
		    result->Append(theType->FullName());
//		    cout << "MS_MetaSchema::GetPersistentClassesFromSchema : " << inslist->Value(k)->ToCString() << endl;
		  }
		}
	      }
      
	    }
	  }
	}
      }
    }

    packageList = theSchema->GetPackages();

    Handle(MS_Package)               thePackage;
    Handle(TCollection_HAsciiString) className;

    for (j = 1; j <=  packageList->Length(); j++) {
      if (IsPackage(packageList->Value(j))) {
	thePackage = GetPackage(packageList->Value(j));
	
	classList = thePackage->Classes();

	 for (i = 1; i <= classList->Length(); i++) {
	   className = MS::BuildFullName(packageList->Value(j),classList->Value(i));

	   if (!aMap.Contains(className)) {
//	     cout << "MS_MetaSchema::GetPersistentClassesFromSchema : " << className->ToCString() << endl;
	     if (IsDefined(className)) {
	       theType = GetType(className);
	       
	       if (theType->IsKind(STANDARD_TYPE(MS_StdClass))) {
		 theStdClass = Handle(MS_StdClass)::DownCast(theType);
		 
		 if ((theStdClass->IsPersistent() || (withStorable && theStdClass->IsStorable())) && !theStdClass->IsGeneric()) {
		   aMap.Add(className);
		   result->Append(className);
		 }

		 if (!theStdClass->GetMyCreator().IsNull()) {
		   Handle(MS_InstClass)                    anInst = theStdClass->GetMyCreator();
		   Handle(TColStd_HSequenceOfHAsciiString) inslist;
		   
		   inslist = anInst->GetNestedStdClassesName();
		   
		   for (k = 1; k <= inslist->Length(); k++) {
		     theType = GetType(MS::BuildFullName(theStdClass->Package()->Name(),inslist->Value(k)));

		     if (!aMap.Contains(theType->FullName())) {
		       anOther = Handle(MS_StdClass)::DownCast(theType);
		       
		       if ((anOther->IsPersistent() || (withStorable && anOther->IsStorable())) && !anOther->IsGeneric()) {
			 aMap.Add(theType->FullName());
			 result->Append(theType->FullName());
//			 cout << "MS_MetaSchema::GetPersistentClassesFromSchema : " << theType->FullName()->ToCString() << endl;
		       }
		     }
		   }
		   
		   inslist = anInst->GetNestedInsClassesName();
		   
		   for (k = 1; k <= inslist->Length(); k++) {
		     theType = GetType(MS::BuildFullName(theStdClass->Package()->Name(),inslist->Value(k)));

		     if (!aMap.Contains(theType->FullName())) {
		       anOther = Handle(MS_StdClass)::DownCast(theType);
		       
		       if (!anOther.IsNull()) {
			 if ((anOther->IsPersistent() || (withStorable && anOther->IsStorable())) && !anOther->IsGeneric()) {
			   aMap.Add(theType->FullName());
			   result->Append(theType->FullName());
//			   cout << "MS_MetaSchema::GetPersistentClassesFromSchema : " << theType->FullName()->ToCString() << endl;
			 }
		       }
		     }
		   }
		   
		 }
	       }
	     }
	   }
	 }
      }
    }
  }

  return result;
}

Handle(TColStd_HSequenceOfHAsciiString) MS_MetaSchema::GetPersistentClassesFromClasses(const Handle(TColStd_HSequenceOfHAsciiString)& aClassList, const Standard_Boolean withStorable) const
{
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;
  
  if (!aClassList.IsNull()) {
    WOKTools_MapOfHAsciiString              aMap,
                                            alreadyProcMap;
    Handle(MS_StdClass)                     theStdClass,
                                            theLocalClass;
    Standard_Integer                        i,
                                            lenList,
                                            j,
                                            k;
    Handle(TColStd_HSequenceOfHAsciiString) ascSeq,
                                            tmpClassList,
                                            tmpResult = new TColStd_HSequenceOfHAsciiString;
    Standard_Boolean                        isEnd        = Standard_False;
    Handle(MS_Type)                         theType;

    lenList = aClassList->Length();

    for (i = 1; i <= lenList; i++) {
      if (!aMap.Contains(aClassList->Value(i))) {
	aMap.Add(aClassList->Value(i));
//	cout << "MS_MetaSchema::GetPersistentClassesFromClasses : " << aClassList->Value(i)->ToCString() << endl;
      }
    }

    tmpClassList = aClassList;

    while(!isEnd) {
      isEnd = Standard_True;

      for (i = 1; i <= lenList; i++) {
	
	if (!alreadyProcMap.Contains(tmpClassList->Value(i))) {
	  alreadyProcMap.Add(tmpClassList->Value(i));

	  if (IsDefined(tmpClassList->Value(i))) {
	    theStdClass = Handle(MS_StdClass)::DownCast(GetType(tmpClassList->Value(i)));
	    
	    if (!theStdClass.IsNull()) {
	      ascSeq = theStdClass->GetFullInheritsNames();
	      
	      // Inherits
	      //
	      for (j = 1; j <= ascSeq->Length(); j++) {
		if (IsDefined(ascSeq->Value(j))) {
//		  cout << "MS_MetaSchema::GetPersistentClassesFromClasses : " << ascSeq->Value(j)->ToCString() << endl;
		  theLocalClass = Handle(MS_StdClass)::DownCast(GetType(ascSeq->Value(j)));
		  
		  if (!theLocalClass.IsNull() && !aMap.Contains(ascSeq->Value(j))) {
		    if (theLocalClass->IsPersistent() || theLocalClass->IsStorable()) {
		      tmpResult->Append(ascSeq->Value(j));
		      if (theLocalClass->IsPersistent() || (withStorable && theLocalClass->IsStorable())) {
			result->Append(ascSeq->Value(j));
//			cout << "MS_MetaSchema::GetPersistentClassesFromClasses : ADD " << ascSeq->Value(j)->ToCString() << endl;
		      }
		      aMap.Add(ascSeq->Value(j));
		      isEnd = Standard_False;
		    } 
		  }
		}
	      }

	      ascSeq = theStdClass->GetUsesNames();
	      
	      // Used Classes
	      //
	      for (j = 1; j <= ascSeq->Length(); j++) {
		if (IsDefined(ascSeq->Value(j))) {
		  theLocalClass = Handle(MS_StdClass)::DownCast(GetType(ascSeq->Value(j)));
		  
		  if (!theLocalClass.IsNull() && !aMap.Contains(ascSeq->Value(j))) {
		    if (theLocalClass->IsPersistent() || theLocalClass->IsStorable()) {
		      tmpResult->Append(ascSeq->Value(j));
		      if (theLocalClass->IsPersistent() || (withStorable && theLocalClass->IsStorable())) {
			result->Append(ascSeq->Value(j));
//			cout << "MS_MetaSchema::GetPersistentClassesFromClasses : ADD " << ascSeq->Value(j)->ToCString() << endl;
		      }
		      aMap.Add(ascSeq->Value(j));
		      isEnd = Standard_False;
		    }
		  }
		}
	      }

	      if (!theStdClass->GetMyCreator().IsNull()) {
		Handle(MS_InstClass)                    anInst = theStdClass->GetMyCreator();
		Handle(TColStd_HSequenceOfHAsciiString) inslist;

		inslist = anInst->GetNestedStdClassesName();
		
		for (k = 1; k <= inslist->Length(); k++) {
		  theType = GetType(MS::BuildFullName(theStdClass->Package()->Name(),inslist->Value(k)));
		  theLocalClass = Handle(MS_StdClass)::DownCast(theType);

		  if (!theLocalClass.IsNull() && !aMap.Contains(theLocalClass->FullName())) {
		    if (theLocalClass->IsPersistent() || theLocalClass->IsStorable()) {
		      tmpResult->Append(theLocalClass->FullName());      
		      if ((theLocalClass->IsPersistent() || (withStorable && theLocalClass->IsStorable())) && !theLocalClass->IsGeneric()) {
			result->Append(theLocalClass->FullName());
//			cout << "MS_MetaSchema::GetPersistentClassesFromClasses : " << theLocalClass->FullName()->ToCString() << endl;
		      }
		      aMap.Add(theLocalClass->FullName());
		      isEnd = Standard_False;
		    }
		  }
		}

		inslist = anInst->GetNestedInsClassesName();
		
		for (k = 1; k <= inslist->Length(); k++) {
		  theType = GetType(MS::BuildFullName(theStdClass->Package()->Name(),inslist->Value(k)));
		  theLocalClass = Handle(MS_StdClass)::DownCast(theType);
		  
		  if (!theLocalClass.IsNull() && !aMap.Contains(theLocalClass->FullName())) {
		    if (theLocalClass->IsPersistent() || theLocalClass->IsStorable()) {
		      tmpResult->Append(theLocalClass->FullName());      
		      if ((theLocalClass->IsPersistent() || (withStorable && theLocalClass->IsStorable())) && !theLocalClass->IsGeneric()) {
			result->Append(theLocalClass->FullName());
//			cout << "MS_MetaSchema::GetPersistentClassesFromClasses : " << theLocalClass->FullName()->ToCString() << endl;
		      }
		      aMap.Add(theLocalClass->FullName());
		      isEnd = Standard_False;
		    }
		  }
		}
	      }

	    }
	  }
	}

      }
      
      tmpClassList = tmpResult;
      tmpResult    = new TColStd_HSequenceOfHAsciiString;
      lenList      = tmpClassList->Length();
    }   
  }     
  
  return result;
}

// Check consistency of a GlobalEntity
//
Standard_Boolean MS_MetaSchema::Check(const Handle(TCollection_HAsciiString)& aName) const
{
  Standard_Boolean result = Standard_True,locRes;
  
  if (IsPackage(aName)) {
    Handle(MS_Package)                      aPack = GetPackage(aName);
    Handle(TColStd_HSequenceOfHAsciiString) aASeq;
    Standard_Integer                        i;

    aASeq = aPack->Classes();

    Handle(MS_Class)                 aClass;
    Handle(TCollection_HAsciiString) fullName;

    for (i = 1; i <= aASeq->Length(); i++) {
      fullName = MS::BuildFullName(aName,aASeq->Value(i));

      aClass = Handle(MS_Class)::DownCast(GetType(fullName));

      locRes = CheckClass(aClass);
      result = result && locRes;
    }

    Handle(MS_HSequenceOfExternMet) met = aPack->Methods();

    for (i = 1; i <= met->Length(); i++) {
      locRes = CheckExternMethod(met->Value(i));
      result = result && locRes;
    }
  }
  else if (IsSchema(aName)) {
    Handle(MS_Schema) sch = GetSchema(aName);
    Handle(TColStd_HSequenceOfHAsciiString) aSeq = sch->GetClasses();
    Standard_Integer                        i;

    for (i = 1; i <= aSeq->Length(); i++) {
      if (!IsDefined(aSeq->Value(i))) {
	ErrorMsg() << CHECKERROR << "the class " << aSeq->Value(i)->ToCString() << " used in schema is not defined in a package." << endm;
	result = Standard_False;
      }
    }

    aSeq = sch->GetPackages();

    for (i = 1; i <= aSeq->Length(); i++) {
      if (!IsPackage(aSeq->Value(i))) {
	ErrorMsg() << CHECKERROR << "the package " << aSeq->Value(i)->ToCString() << " used in schema does not exist." << endm;
	result = Standard_False;
      }
    }
  }
  else if (IsClient(aName)) {
    Handle(MS_Client)                       client = GetClient(aName);
    Handle(TColStd_HSequenceOfHAsciiString) aASeq;
    Standard_Integer                        i;
    Handle(MS_Method)                       met;

    aASeq = client->Methods();

    for (i = 1; i <= aASeq->Length(); i++) {
      met = MS::GetMethodFromFriendName(this,aASeq->Value(i));

      if (met.IsNull()) {
	ErrorMsg() << CHECKERROR << "the method " << aASeq->Value(i)->ToCString() << " is not defined." << endm;
	result = Standard_False;
      }
    }

    aASeq = client->Interfaces();

    for (i = 1; i <= aASeq->Length(); i++) {
      if (IsInterface(aASeq->Value(i))) {
	locRes = Check(aASeq->Value(i));
	result = result && locRes;
      }
      else {
	ErrorMsg() << CHECKERROR << "in client " << aName->ToCString() << ", the interface " << aASeq->Value(i)->ToCString() << " is not defined." << endm;
	result = Standard_False;
      }
    }
  }
  else if (IsEngine(aName)) {
    Handle(MS_Engine)                       engine = GetEngine(aName);
    Handle(TColStd_HSequenceOfHAsciiString) aASeq;
    Standard_Integer                        i;

    aASeq = engine->Interfaces();

    for (i = 1; i <= aASeq->Length(); i++) {
      if (IsInterface(aASeq->Value(i))) {
	locRes = Check(aASeq->Value(i));
	result = result && locRes;
      }
      else {
	ErrorMsg() << CHECKERROR << "in engine " << aName->ToCString() << ", the interface " << aASeq->Value(i)->ToCString() << " is not defined." << endm;
	result = Standard_False;
      }
    }
  }
  else if (IsInterface(aName)) {
    Handle(MS_Interface)                    anInter = GetInterface(aName);
    Handle(TColStd_HSequenceOfHAsciiString) aASeq;
    Standard_Integer                        i;

    aASeq = anInter->Classes();

    Handle(MS_Type)                  aType;
    Handle(TCollection_HAsciiString) fullName;

    for (i = 1; i <= aASeq->Length(); i++) {
      fullName = aASeq->Value(i);
      if (IsDefined(fullName)) {
	aType = GetType(fullName);
	
	locRes = aType->IsKind(STANDARD_TYPE(MS_Class));
	
	if (!locRes) {
	  ErrorMsg() << CHECKERROR << "the type " << fullName->ToCString() << " is not a class." << endm;
	  result = Standard_False;
	}
	
	if (aType->IsKind(STANDARD_TYPE(MS_GenClass))) {
	  ErrorMsg() << CHECKERROR << "the type " << fullName->ToCString() << " is a generic class, it cannot be exported." << endm;
	  result = Standard_False;
	}
      }
      else {
	ErrorMsg() << CHECKERROR << "the type " << fullName->ToCString() << " is not defined." << endm;
	result = Standard_False;
      }
    }   
    
    aASeq = anInter->Methods();
    Handle(MS_Method)                       amet;
    
    for (i = 1; i <= aASeq->Length(); i++) {
      amet = MS::GetMethodFromFriendName(this,aASeq->Value(i));
      
      if (amet.IsNull()) {
	ErrorMsg() << CHECKERROR << "the method " << aASeq->Value(i)->ToCString() << " is not defined." << endm;
	result = Standard_False;
      }
    }
  }
  else if (IsDefined(aName)) {
    Handle(MS_Type) type = GetType(aName);
    if (type->IsKind(STANDARD_TYPE(MS_Class))) {
      Handle(MS_Class) aClass = *((Handle(MS_Class)*)&type);

      locRes = CheckClass(aClass);
      result = result && locRes;
    }
  }
  
  return result;
}

// WARNING : do not modify this part unless you understand it
//
Standard_Boolean MS_MetaSchema::CheckClass(const Handle(MS_Class)& aClass) const
{
  Standard_Boolean                        result        = Standard_True,
                                          isFound,
                                          locRes        = Standard_False;
  Standard_Integer                        i,j,k;
  Handle(MS_HSequenceOfField)             aSeqField     = aClass->GetFields();
  Handle(TColStd_HSequenceOfHAsciiString) uses;
  Handle(MS_Type)                         theType;

  // Check uses
  //
  uses = aClass->GetUsesNames();
  
  for (i = 1; i <= uses->Length(); i++) {
    locRes = IsDefined(uses->Value(i));
    
    if (!locRes) {
       ErrorMsg() << CHECKERROR << "the type " << uses->Value(i)->ToCString() << " is not defined." << endm;
       ErrorMsg() << CHECKERROR << "first reference in 'uses' clause of " << aClass->FullName()->ToCString() << endm;
    }
    else {
      theType = GetType(uses->Value(i));

      if (theType->IsKind(STANDARD_TYPE(MS_GenClass))) {
	ErrorMsg() << CHECKERROR << "the generic class " << uses->Value(i)->ToCString() << " cannot be" << endm;
	ErrorMsg() << CHECKERROR << "in 'uses' clause of " << aClass->FullName()->ToCString() << endm;
	locRes = Standard_False;
      }
    }

    result = result && locRes;
  }    
  
  // Check inherits 
  //
  uses = aClass->GetFullInheritsNames();

  Handle(MS_HSequenceOfMemberMet)  aSeqM,
                                   aSeqM_i_1,
                                   aSeqM_i;
  Handle(MS_StdClass)              aInhClass;
  Standard_Boolean                 starTestDeferred = Standard_False,
                                   beginTestDeferred = Standard_False;

  for (i = uses->Length(); i >= 1; i--) {
    locRes = IsDefined(uses->Value(i));
    
    if (!locRes) {
       ErrorMsg() << CHECKERROR << "the type " << uses->Value(i)->ToCString() << " is not defined." << endm;
       ErrorMsg() << CHECKERROR << "first reference in 'inherits' clause of ";

       if (i == 1) {
	 ErrorMsg() << aClass->FullName()->ToCString() << endm;
       }
       else {
	 ErrorMsg() << uses->Value(i-1)->ToCString() << endm;
       }
    }
    else {
      theType = GetType(uses->Value(i));

      if (theType->IsKind(STANDARD_TYPE(MS_GenClass))) {
	ErrorMsg() << CHECKERROR << "the generic class " << uses->Value(i)->ToCString() << " cannot be" << endm;
	ErrorMsg() << CHECKERROR << "in 'inherits' clause of ";

	if (i == 1) {
	  ErrorMsg() << aClass->FullName()->ToCString() << endm;
	}
	else {
	  ErrorMsg() << uses->Value(i-1)->ToCString() << endm;
	}

	locRes = Standard_False;
      }
    }

    // Check deferred methods
    //
    if (locRes) {
      theType = GetType(uses->Value(i));

      if (theType->IsKind(STANDARD_TYPE(MS_StdClass))) {
	aInhClass    = *((Handle(MS_StdClass)*)&theType);

	// WARNING : This code is valid only because these Standard classes have no deferred methods
	//
 	if (!starTestDeferred &&
	    !aInhClass->FullName()->IsSameString(MS::GetPersistentRootName()) &&
	    !aInhClass->FullName()->IsSameString(MS::GetStorableRootName()) &&
	    !aInhClass->FullName()->IsSameString(MS::GetTransientRootName()) &&
	    aInhClass->Deferred()) {
	  starTestDeferred = Standard_True;
	  beginTestDeferred = Standard_True;
	}

	if (starTestDeferred) {
	  aSeqM_i_1 = aSeqM_i;
	  aSeqM     = aInhClass->GetMethods();
	  aSeqM_i   = new MS_HSequenceOfMemberMet;
	  
	  if (i == uses->Length() || beginTestDeferred) {
	    beginTestDeferred = Standard_False;
	    aSeqM             = aInhClass->GetMethods();
	    aSeqM_i           = new MS_HSequenceOfMemberMet;
	    
	    for(j = 1; j <= aSeqM->Length(); j++) {
	      if (aSeqM->Value(j)->IsKind(STANDARD_TYPE(MS_InstMet))) {
		Handle(MS_InstMet) im = *((Handle(MS_InstMet)*)&aSeqM->Value(j));
		
		if (im->IsDeferred()) {
		  aSeqM_i->Append(im);
		}
	      }
	    }
	  }
	  else {
	    for(j = 1; j <= aSeqM->Length(); j++) {
	      if (aSeqM->Value(j)->IsKind(STANDARD_TYPE(MS_InstMet))) {
		Handle(MS_InstMet) im = *((Handle(MS_InstMet)*)&aSeqM->Value(j));
		
		isFound = Standard_False;
		
		for (k = 1; k <= aSeqM_i_1->Length() && !isFound; k++) {
		  if (im->IsSameSignature(aSeqM_i_1->Value(k)->FullName())) {
		    aSeqM_i_1->Remove(k);
		    isFound = Standard_True;
		  }
		}
		
		if (im->IsDeferred()) {
		  aSeqM_i->Append(im);
		}
	      }
	    }
	    
	    if (aInhClass->Deferred()) {
	      aSeqM_i->Append(aSeqM_i_1);
	      aSeqM_i_1->Clear();
	    }
	    
	    if (aSeqM_i_1->Length() > 0) {
	      for (k = 1; k <= aSeqM_i_1->Length(); k++) {
		ErrorMsg() << CHECKERROR << "the deferred method " << aSeqM_i_1->Value(k)->FullName()->ToCString() << " must" << endm;
		ErrorMsg() << CHECKERROR << "have an implementation in class ";
		
		if (i == 1) {
		  ErrorMsg() << aClass->FullName()->ToCString() << endm;
		}
		else {
		  ErrorMsg() << uses->Value(i)->ToCString() << endm;
		}
	      }
	      
	      locRes = Standard_False;
	    }
	  }
	}
      }
    }
    
    result = result && locRes;
  }  

  if (starTestDeferred && (uses->Length() > 0) && aClass->IsKind(STANDARD_TYPE(MS_StdClass))) {
    aSeqM     = aClass->GetMethods();
    aSeqM_i_1 = aSeqM_i;

    for(j = 1; j <= aSeqM->Length(); j++) {
      if (aSeqM->Value(j)->IsKind(STANDARD_TYPE(MS_InstMet))) {
	Handle(MS_InstMet) im = *((Handle(MS_InstMet)*)&aSeqM->Value(j));
	
	isFound = Standard_False;
	
	for (k = 1; k <= aSeqM_i_1->Length() && !isFound; k++) {
	  if (im->IsSameSignature(aSeqM_i_1->Value(k)->FullName())) {
	    aSeqM_i_1->Remove(k);
	    isFound = Standard_True;
	  }
	}
      }
    }

    if (aClass->Deferred()) {
      aSeqM_i_1->Clear();
    }
    
    if (aSeqM_i_1->Length() > 0) {
      for (k = 1; k <= aSeqM_i_1->Length(); k++) {
	ErrorMsg() << CHECKERROR << "the deferred method " << aSeqM_i_1->Value(k)->FullName()->ToCString() << " must" << endm;
	ErrorMsg() << CHECKERROR << "have an implementation in class " << aClass->FullName()->ToCString() << endm;
      }
      
      locRes = Standard_False;
    }

    result = result && locRes;
  }

  // Check fields
  //
  for (i = 1; i <= aSeqField->Length(); i++) {
    locRes = CheckField(aSeqField->Value(i));
    result = result && locRes;
  }    
    
  // Check friends methods
  //
  Handle(MS_Method)                       met;
  
  uses = aClass->GetFriendMets();
  
  for (i = 1; i <= uses->Length(); i++) {
    met = MS::GetMethodFromFriendName(this,uses->Value(i));
    
    if (met.IsNull()) {
      ErrorMsg() << CHECKERROR << "the friend method '" << uses->Value(i)->ToCString() << "' in 'friend' clause of the class '" << aClass->FullName()->ToCString() << "' is not defined." << endm;
      result = Standard_False;
    }
  }

  // Check methods
  //
  aSeqM = aClass->GetMethods();
  
  for (i = 1; i <= aSeqM->Length(); i++) {
    locRes = CheckMemberMethod(aSeqM->Value(i));
    result = locRes && result;
  }
  
  if (aClass->IsKind(STANDARD_TYPE(MS_StdClass))) {
    Handle(MS_StdClass) aStdClass = *((Handle(MS_StdClass)*)&aClass);

    if (!aStdClass->GetMyCreator().IsNull() && !aStdClass->IsGeneric()) {
      Handle(MS_InstClass) anInstClass = aStdClass->GetMyCreator();

      locRes = CheckInstClass(anInstClass);
      result = locRes && result;

    }
#if 1
// ---> EUG 15-APR-2000
      else {

     int                                       i, j, k;
     MS_MapOfMethod                            vMap;
     Handle( MS_InstMet                      ) im;
     Handle( TCollection_HAsciiString        ) mName;
     Handle( TColStd_HSequenceOfHAsciiString ) parents = aStdClass -> GetFullInheritsNames ();

     for ( i = 1; i <= parents -> Length (); ++i ) {

      Handle( MS_Class ) aParent = Handle( MS_Class ) :: DownCast (
                                                          GetType (  parents -> Value ( i )  )
                                                         );

      if (   !aParent.IsNull () && aParent -> IsKind (  STANDARD_TYPE( MS_StdClass )  )   ) {

       Handle( MS_StdClass ) aStdClass = *(   (  Handle( MS_StdClass )*  )&aParent   );

       if (  !aStdClass -> GetMyCreator ().IsNull () && !aStdClass -> IsGeneric ()  ) {
       
        aSeqM = aParent -> GetMethods ();

        for ( j = 1; j <= aSeqM -> Length (); ++j )

         if (   aSeqM -> Value ( j ) -> IsKind (  STANDARD_TYPE( MS_InstMet )  )   ) {

          im    = *(   (  Handle( MS_InstMet )*  )&aSeqM -> Value ( j )   );
          mName = im -> FullName () -> Token ( ":", 2 );

          if (  !im -> IsStatic () && !vMap.IsBound ( mName )  ) vMap.Bind ( mName, im );

         }  // end if

       }  // end if

      }  // end if

     }  // end for

     if (  !vMap.IsEmpty ()  ) {

      aSeqM = aStdClass -> GetMethods ();

      for ( i = 1; i <= aSeqM -> Length (); ++i ) {

       mName = aSeqM -> Value ( i ) -> FullName () -> Token ( ":", 2 );

       if (  vMap.IsBound ( mName )  ) {

        Handle ( MS_InstMet ) imParent =
         *(   (  Handle( MS_InstMet )*  )&vMap.Find ( mName )   );

        im = *(   (  Handle( MS_InstMet )*  )&aSeqM -> Value ( i )   );

        Handle( MS_HArray1OfParam ) pParent = imParent -> Params ();
        Handle( MS_HArray1OfParam ) pClass  = im       -> Params ();
 
        if (  !pParent.IsNull ()  )

         for ( k = 1; k <= pParent -> Length (); ++k )

          if (   pParent -> Value ( k ) -> IsItem () &&
                !pClass  -> Value ( k ) -> IsItem ()
          ) pClass -> Value ( k ) -> ItsItem ();

       }  // end if

      }  // end for

     }  // end if

    }  // end else
// <--- EUG 15-APR-2000
#endif
  }

  
  return result;
}

// WARNING : do not modify this part unless you understand it
//
Standard_Boolean MS_MetaSchema::CheckMemberMethod(const Handle(MS_MemberMet)& aMeth) const
{
  Standard_Boolean                        result = Standard_True;
  Handle(MS_Type)                         gettyperes = GetType(aMeth->Class());
  Handle(MS_Class)                        aClass = *((Handle(MS_Class)*)&gettyperes);
  Handle(TColStd_HSequenceOfHAsciiString) aSeqAncestors = aClass->GetFullInheritsNames();
  Standard_Integer                        j,
                                          len;
  Handle(TCollection_HAsciiString)        whoStopRedefinition;
  Handle(MS_Param)                        returnValue;

  //    cout << "METHOD : " <<  aMeth->FullName()->ToCString() << endl;
  
  if (aMeth->IsKind(STANDARD_TYPE(MS_InstMet))) {
    Handle(MS_InstMet) anInst = *((Handle(MS_InstMet)*)&aMeth),
                       anOther;
    Standard_Boolean   IsVirtual,
                       IsDeferred,
                       StopRedefinition;
    Standard_Integer   whoIsDeferred,
                       IsFound;
    Standard_Boolean   myclassishandled = aClass->IsPersistent() || aClass->IsTransient();
      
    if (anInst->IsDeferred() && !aClass->Deferred()) {
      ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " cannot have the keyword 'deferred' in a class without keyword 'deferred'." << endm;
      result = Standard_False;
    }

    if (myclassishandled) {
      if (anInst->IsOut()) {
	ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " cannot have the keyword 'out' but 'mutable' because it's comes from a Persistent or a Transient class." << endm;
	result = Standard_False;
      }
    }
    else {
      if (anInst->IsMutable()) {
	ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " cannot have the keyword 'mutable' but 'out' because it does not comes from a Persistent or a Transient class." << endm;
	result = Standard_False;
      }
    }

    returnValue      = anInst->Returns();
    IsVirtual        = Standard_False;
    IsDeferred       = Standard_False;
    StopRedefinition = Standard_False;
    whoIsDeferred    = 0;
    IsFound          = 0;
    
    for (j = 1; j <= aSeqAncestors->Length(); j++) {
      Handle(MS_Class)                 anAncestor = Handle(MS_Class)::DownCast(GetType(aSeqAncestors->Value(j)));
      Handle(MS_HSequenceOfMemberMet)  aSeqMA     = anAncestor->GetMethods();
      
      for (len = aSeqMA->Length(); len >= 1; len--) {
	if (aSeqMA->Value(len)->IsKind(STANDARD_TYPE(MS_InstMet))) {
	  if (anInst->IsSameSignature(aSeqMA->Value(len)->FullName())) {
	    anOther = *((Handle(MS_InstMet)*)&aSeqMA->Value(len));
	    
	    IsFound = j;
	    
	    if (!returnValue.IsNull()) {
	      if (!anOther->Returns().IsNull()) {
		if ((anOther->IsConstReturn() != anInst->IsConstReturn()) || (anOther->IsRefReturn() != anInst->IsRefReturn())) {
		  result = Standard_False;
		  ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " cannot have a different C++ return mode than" << endm;
		  ErrorMsg() << CHECKERROR << "the redefined super method : " << anOther->FullName()->ToCString() << endm;
		}
		
		if (!anOther->Returns()->TypeName()->IsSameString(returnValue->TypeName())) {
		  result = Standard_False;
		  ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " cannot have a different return value than" << endm;
		  ErrorMsg() << CHECKERROR << "the redefined super method : " << anOther->FullName()->ToCString() << endm;
		}
	      }
	      else {
		result = Standard_False;
		ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " have a return value but" << endm;
		ErrorMsg() << CHECKERROR << "not the super method " << anOther->FullName()->ToCString() << endm;
	      }
	    } 
	    else if (!anOther->Returns().IsNull()) {
	      result = Standard_False;
	      ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " cannot have a return value because" << endm;
	      ErrorMsg() << CHECKERROR << "the super method " << anOther->FullName()->ToCString() << " does not have one" << endm;
	    }
	    
	    if (!IsDeferred && !StopRedefinition) {
	      IsDeferred = anOther->IsDeferred();
	      
	      if (IsDeferred) {
		whoIsDeferred = j;
	      }
	    }
	    
	    if (!IsVirtual && !StopRedefinition) {
	      IsVirtual  = !anOther->IsStatic();
	    }
	    
	    if (anOther->IsRedefined() && !anOther->IsDeferred() && anOther->IsStatic()) {
	      IsVirtual  = Standard_False;
	      IsDeferred = Standard_False;
	      StopRedefinition       = Standard_True;
	      whoStopRedefinition = aSeqAncestors->Value(j);
	    }
	  }
	}
      }
    }
    
    // the ancestors methods are not virtual or deferred but they are redefined here
    // it's strange so we must take a look
    //
    if (!IsVirtual && !IsDeferred && IsFound != 0) {
      
      if (anInst->IsRedefined()) {
	result = Standard_False;
	ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " cannot have the 'redefined' keyword" << endm;
	
	if (StopRedefinition) {
	  ErrorMsg() << CHECKERROR << "the redefinition was stopped in class " << whoStopRedefinition->ToCString() << endm;
	}
	else {
	  ErrorMsg() << CHECKERROR << "to be able to make this redefinition, the keyword 'is virtual' must be set at the end of the method of the class " << aSeqAncestors->Value(IsFound)->ToCString() << endm;
	}
      }
      else if (!anInst->IsStatic() && StopRedefinition) {
	result = Standard_False;
	ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " cannot have the 'virtual' keyword because redefinition" << endm;
	ErrorMsg() << CHECKERROR << "was stopped in class " << whoStopRedefinition->ToCString() << endm;
      }
      else if (anInst->IsDeferred() && StopRedefinition) {
	result = Standard_False;
	ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " cannot have the 'deferred' keyword because redefinition" << endm;
	ErrorMsg() << CHECKERROR << "was stopped in class " << whoStopRedefinition->ToCString() << endm;
      }
      else if (!anInst->IsDeferred() && anInst->IsStatic()) {
	result = Standard_False;
	
	ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " cannot redefine the same method than class "
	  << aSeqAncestors->Value(IsFound)->ToCString() << endm;
	
	if (StopRedefinition) {
	  ErrorMsg() << "the redefinition was stopped in class " << whoStopRedefinition->ToCString() << endm;
	}
	else {
	  ErrorMsg() << CHECKERROR <<"to be able to make this redefinition, the keyword 'is virtual' must be set at the end of the method of the class "
	    << aSeqAncestors->Value(IsFound)->ToCString() << endm;
	}
      }
      
    }
    else if ((IsVirtual || IsDeferred) && IsFound != 0) {
      if ((IsDeferred && !anInst->IsRedefined() && (whoIsDeferred > IsFound)) || (!IsDeferred && IsVirtual && !anInst->IsRedefined())) {
	result = Standard_False;
	ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " must have the 'redefined' keyword." << endm;
      }
    }
    else if ((IsFound == 0 ) && anInst->IsRedefined()) {
      result = Standard_False;
      ErrorMsg() << CHECKERROR << "the method " << anInst->FullName()->ToCString() << " must have been declared in an ancestor class" << endm;
      ErrorMsg() << CHECKERROR << "to be redefined." << endm;
    }
  }

  Handle(MS_HArray1OfParam) parSeq = aMeth->Params();
  Standard_Boolean            locRes = Standard_True;
  
  whoStopRedefinition = aMeth->Class();
  
  if(!parSeq.IsNull()) {
    for (j = 1; j <= parSeq->Length(); j++) {
      Handle(MS_Param) p = parSeq->Value(j);
      if (IsDefined(p->TypeName())) {
	Handle(MS_Type)  t = p->Type();
	
	if (t->IsKind(STANDARD_TYPE(MS_Alias))) {
	  MS_Alias *deepType = (MS_Alias *)(t.operator->());
	  t = GetType(deepType->DeepType());
	}


	if (t->IsKind(STANDARD_TYPE(MS_Class))) {
	  MS_Class      *pclass;

	  if (t->IsKind(STANDARD_TYPE(MS_InstClass))) {
	    MS_InstClass    *iclass = (MS_InstClass*)(t.operator->());
	    Handle(MS_Type) gtype   = GetType(iclass->GenClass());
	    
	    pclass = (MS_Class*)(gtype.operator->());
	  }
	  else {
	    pclass = (MS_Class*)(t.operator->());
	  }

	  Standard_Boolean  handleclass = pclass->IsPersistent() || pclass->IsTransient();
	  
	  if (!handleclass) {
	    if (p->IsMutable() || p->IsImmutable()) {
	      ErrorMsg() << CHECKERROR << "the parameter " << p->Name()->ToCString() << " from method " << aMeth->FullName()->ToCString() << " must be set 'in' or 'out' , but not 'mutable' or 'immutable' because class " << t->FullName()->ToCString() << " is not persistent or transient." << endm;
	      result = Standard_False;
	    }
	  }
	}
	else {
	  if (p->IsMutable() || p->IsImmutable()) {
	    ErrorMsg() << CHECKERROR <<  "the parameter " << p->Name()->ToCString() << " from method " << aMeth->FullName()->ToCString() << " cannot be 'mutable' or 'immutable' because " << t->FullName()->ToCString() << " is not a class." << endm;
	    result = Standard_False;
	  }
	}

	if (t->Private()) {
	  if (!t->Package()->FullName()->IsSameString(aClass->Package()->FullName())) {
	    ErrorMsg() << CHECKERROR << "the private type " << t->FullName()->ToCString() << " cannot be used in method " << aMeth->FullName()->ToCString() << endm;
	    locRes = Standard_False;
	  }
	  else if (!aClass->Private() && !aMeth->Private() && !whoStopRedefinition->IsSameString(p->TypeName())) {
	    Standard_Boolean cankill = Standard_True;
	    
	    if (aClass->IsKind(STANDARD_TYPE(MS_StdClass))) {
	      Handle(MS_StdClass) stdClass = *((Handle(MS_StdClass)*)&aClass);
	      
	      if (!stdClass->GetMyCreator().IsNull()) {
		if (stdClass->GetMyCreator()->FullName()->IsSameString(p->TypeName())) {
		  cankill = Standard_False;
		}
	      }
	    }
	    if (cankill) {
	      ErrorMsg() << CHECKERROR << "the non private method " << aMeth->FullName()->ToCString() << " cannot use a private type as parameter." << endm;
	      locRes = Standard_False;
	    }
	  }
	}
	result = result && locRes;
      }
    }
  }

  Handle(MS_Param) p = aMeth->Returns(); 

  if (!p.IsNull()) {
    if (IsDefined(p->TypeName())) {
      Handle(MS_Type)  t = p->Type();
      
      if (t->IsKind(STANDARD_TYPE(MS_Alias))) {
	MS_Alias *deepType = (MS_Alias *)(t.operator->());
	t = GetType(deepType->DeepType());
      }

      if (t->IsKind(STANDARD_TYPE(MS_Class))) {
	MS_Class      *pclass;

	if (t->IsKind(STANDARD_TYPE(MS_InstClass))) {
	  MS_InstClass    *iclass = (MS_InstClass*)(t.operator->());
	  Handle(MS_Type) gtype   = GetType(iclass->GenClass());
	  
	  pclass = (MS_Class*)(gtype.operator->());
	}
	else {
	  pclass = (MS_Class*)(t.operator->());
	}

	Standard_Boolean  handleclass = pclass->IsPersistent() || pclass->IsTransient();
	
	if (!handleclass) {
	  if (p->IsMutable() || p->IsImmutable()) {
	    ErrorMsg() << CHECKERROR << "the return's type from method " << aMeth->FullName()->ToCString() << " cannot be 'mutable' or 'immutable' because class " << t->FullName()->ToCString() << " is not persistent or transient." << endm;
	    result = Standard_False;
	  }
	}
      }   
      else {
	if (p->IsMutable() || p->IsImmutable()) {
	  ErrorMsg() << CHECKERROR << "the return's type from method " << aMeth->FullName()->ToCString() << " cannot be 'mutable' or 'immutable' because " << t->FullName()->ToCString() << " is not a class." << endm;
	  result = Standard_False;
	}
      }

      if (t->Private()) {
	if (!t->Package()->FullName()->IsSameString(aClass->Package()->FullName())) {
	  ErrorMsg() << CHECKERROR << "the private type " << t->FullName()->ToCString() << " cannot be used in 'returns' clause of " << aMeth->FullName()->ToCString() << endm;
	  locRes = Standard_False;
	}
	else if (!aClass->Private() && !aMeth->Private() && !whoStopRedefinition->IsSameString(p->TypeName())) {
	  Standard_Boolean cankill = Standard_True;
	  
	  if (aClass->IsKind(STANDARD_TYPE(MS_StdClass))) {
	    MS_StdClass *stdClass = (MS_StdClass*)aClass.operator->();
	    
	    if (!stdClass->GetMyCreator().IsNull()) {
	      if (stdClass->GetMyCreator()->FullName()->IsSameString(p->TypeName())) {
		cankill = Standard_False;
	      }
	    }
	  }
	  if (cankill) {
	    ErrorMsg() << CHECKERROR << "the non private method " << aMeth->FullName()->ToCString() << " cannot return a private type." << endm;
	    result = Standard_False;
	  }
	}
      }
    }
  }

  return result;
}

Standard_Boolean MS_MetaSchema::CheckExternMethod(const Handle(MS_ExternMet)& aMeth) const
{
  Standard_Boolean result = Standard_True;

  Handle(MS_HArray1OfParam)      parSeq = aMeth->Params();
  Standard_Boolean                 locRes = Standard_True;
  Handle(TCollection_HAsciiString) packname = aMeth->Package();
  Standard_Integer                 j;
  Handle(TColStd_HSequenceOfHAsciiString) raisesseq = aMeth->GetRaisesName();

  for (j = 1; j <= raisesseq->Length(); j++) {
    if (!IsDefined(raisesseq->Value(j))) {
      ErrorMsg() << CHECKERROR << "the exception " << raisesseq->Value(j)->ToCString() << ", used in package method " << aMeth->Name()->ToCString() << ", is not defined." << endm;
      result = Standard_False;
    }
  }

  if(!parSeq.IsNull()) {
    for (j = 1; j <= parSeq->Length(); j++) {
      Handle(MS_Param) p = parSeq->Value(j);

      if (IsDefined(p->TypeName())) {
	Handle(MS_Type)  t = p->Type();

	if (t->IsKind(STANDARD_TYPE(MS_Alias))) {
	  MS_Alias *deepType = (MS_Alias *)(t.operator->());
	  t = GetType(deepType->DeepType());
	}

	if (t->IsKind(STANDARD_TYPE(MS_Class))) {
	  MS_Class          *pclass;

	  if (t->IsKind(STANDARD_TYPE(MS_InstClass))) {
	    MS_InstClass    *iclass = (MS_InstClass*)(t.operator->());
	    Handle(MS_Type) gtype   = GetType(iclass->GenClass());

	    pclass = (MS_Class*)(gtype.operator->());
	  }
	  else {
	    pclass = (MS_Class*)(t.operator->());
	  }

	  Standard_Boolean  handleclass = pclass->IsPersistent() || pclass->IsTransient();
	  
	  if (!handleclass) {
	    if (p->IsMutable() || p->IsImmutable()) {
	      ErrorMsg() << CHECKERROR << "the parameter " << p->Name()->ToCString() << " from method " << aMeth->FullName()->ToCString() << " must be set 'in' or 'out' , but not 'mutable' or 'immutable' because class " << t->FullName()->ToCString() << " is not persistent or transient." << endm;
	      result = Standard_False;
	    }
	  }
	}
	else {
	  if (p->IsMutable() || p->IsImmutable()) {
	    ErrorMsg() << CHECKERROR <<  "the parameter " << p->Name()->ToCString() << " from method " << aMeth->FullName()->ToCString() << " cannot be 'mutable' or 'immutable' because " << t->FullName()->ToCString() << " is not a class." << endm;
	    result = Standard_False;
	  }
	}

	if (t->Private()) {
	  if (!t->Package()->FullName()->IsSameString(packname)) {
	    ErrorMsg() << CHECKERROR << "the private type " << t->FullName()->ToCString() << " cannot be used in package method " << aMeth->FullName()->ToCString() << endm;
	    locRes = Standard_False;
	  }
	  else if (!aMeth->Private()) {
	    ErrorMsg() << CHECKERROR << "the non private package method " << aMeth->FullName()->ToCString() << " cannot use a private type as parameter." << endm;
	    locRes = Standard_False;
	  }
	}
	
	result = result && locRes;
      }
    }
  }

  Handle(MS_Param) p = aMeth->Returns(); 

  if (!p.IsNull()) {
    if (IsDefined(p->TypeName())) {
      Handle(MS_Type)  t = p->Type();

      if (t->IsKind(STANDARD_TYPE(MS_Alias))) {
	MS_Alias *deepType = (MS_Alias *)(t.operator->());
	t = GetType(deepType->DeepType());
      }
      
      if (t->IsKind(STANDARD_TYPE(MS_Class))) {
	MS_Class      *pclass;

	if (t->IsKind(STANDARD_TYPE(MS_InstClass))) {
	  MS_InstClass    *iclass = (MS_InstClass*)(t.operator->());
	  Handle(MS_Type) gtype   = GetType(iclass->GenClass());
	  
	  pclass = (MS_Class*)(gtype.operator->());
	}
	else {
	  pclass = (MS_Class*)(t.operator->());
	}

	Standard_Boolean  handleclass = pclass->IsPersistent() || pclass->IsTransient();
	
	if (!handleclass) {
	  if (p->IsMutable() || p->IsImmutable()) {
	    ErrorMsg() << CHECKERROR << "the return's type from method " << aMeth->FullName()->ToCString() << " cannot be 'mutable' or 'immutable' because class " << t->FullName()->ToCString() << " is not persistent or transient." << endm;
	    result = Standard_False;
	  }
	}
      }   
      else {
	if (p->IsMutable() || p->IsImmutable()) {
	  ErrorMsg() << CHECKERROR << "the return's type from method " << aMeth->FullName()->ToCString() << " cannot be 'mutable' or 'immutable' because " << t->FullName()->ToCString() << " is not a class." << endm;
	  result = Standard_False;
	}
      }

      if (t->Private()) {
	if (!t->Package()->FullName()->IsSameString(packname)) {
	  ErrorMsg() << CHECKERROR << "the private type " << t->FullName()->ToCString() << " cannot be used in 'returns' clause of " << aMeth->FullName()->ToCString() << endm;
	  locRes = Standard_False;
	}
	else if (!aMeth->Private()) {
	  ErrorMsg() << CHECKERROR << "the non private method " << aMeth->FullName()->ToCString() << " cannot return a private type." << endm;
	  result = Standard_False;
	}
      }
    }
  }

  return result;
}

// WARNING : do not modify this part unless you understand it
//
Standard_Boolean MS_MetaSchema::CheckInstClass(const Handle(MS_InstClass)& anInst) const
{
  Standard_Boolean                        result = Standard_True,
                                          locRes;
  Handle(MS_Type)                         gettyperes = GetType(anInst->GenClass());
  Handle(MS_GenClass)                     aGenClass = *((Handle(MS_GenClass)*)&gettyperes);
  Handle(MS_HSequenceOfGenType)           aSeqGType = aGenClass->GenTypes();
  Standard_Integer                        i;

  Handle(TColStd_HSequenceOfHAsciiString) aSeqIType = anInst->InstTypes();
  
  for (i = 1; i <= aSeqGType->Length(); i++) {

    if (!aSeqGType->Value(i)->Any()) {
      Handle(TColStd_HSequenceOfHAsciiString) aSeqCont = aSeqGType->Value(i)->GetInstTypes();


      if (aSeqCont->Length() == 0) {
	if (!aSeqGType->Value(i)->TYpeName()->IsSameString(aSeqIType->Value(i))) {
	  Handle(MS_Type) aType = GetType(aSeqIType->Value(i));
	  
	  if (aType->IsKind(STANDARD_TYPE(MS_NatType))) {
	    if (aType->IsKind(STANDARD_TYPE(MS_Alias))) {
	      Handle(MS_Alias)                 alias    = *((Handle(MS_Alias)*)&aType);
	      Handle(TCollection_HAsciiString) realType = alias->Type();
	      Handle(MS_Type)                  gettyper;

	      locRes   = realType->IsSameString(aSeqGType->Value(i)->TYpeName());
	      gettyper = GetType(realType);

	      while (gettyper->IsKind(STANDARD_TYPE(MS_Alias)) && !locRes) {
		alias      = *((Handle(MS_Alias)*)&gettyper);
		realType   = alias->Type();
		locRes     = realType->IsSameString(aSeqGType->Value(i)->TYpeName());
		gettyper = GetType(realType);
	      }

	      result = result && locRes;
	    }
	    else if (aType->IsKind(STANDARD_TYPE(MS_Enum))) {
	      if (!aSeqGType->Value(i)->TYpeName()->IsSameString(MS::GetStorableRootName())) {
		result = Standard_False;
		ErrorMsg() << CHECKERROR << "the instantiation type " << aSeqIType->Value(i)->ToCString() << " is not compatible with the constraint type " <<  aSeqGType->Value(i)->TYpeName()->ToCString() << " in the class " << anInst->FullName()->ToCString() << endm;
	      }
	    }
	    else if (aType->IsKind(STANDARD_TYPE(MS_Imported))) {
	      if (aSeqGType->Value(i)->TYpeName()->IsSameString(MS::GetStorableRootName())) {
		result = Standard_False;
		ErrorMsg() << CHECKERROR << "the instantiation type " << aSeqIType->Value(i)->ToCString() << " is not compatible with the constraint type " <<  aSeqGType->Value(i)->TYpeName()->ToCString() << " in the class " << anInst->FullName()->ToCString() << endm;
	      }
	    }
	    else if (aType->IsKind(STANDARD_TYPE(MS_PrimType))) {
	      Handle(MS_PrimType) aPrim = *((Handle(MS_PrimType)*)&aType);

	      if (aSeqGType->Value(i)->TYpeName()->IsSameString(MS::GetStorableRootName())) {
		result = aPrim->IsStorable();
		if (!result) {
		  ErrorMsg() << CHECKERROR << "the instantiation type " << aSeqIType->Value(i)->ToCString() << " is not compatible with the constraint type " <<  aSeqGType->Value(i)->TYpeName()->ToCString() << " in the class " << anInst->FullName()->ToCString() << endm;
		}
	      }
	    }
	  }
	  else if (aType->IsKind(STANDARD_TYPE(MS_StdClass))) {
	    Handle(MS_StdClass)                     theClass      = *((Handle(MS_StdClass)*)&aType);
	    Handle(TColStd_HSequenceOfHAsciiString) aSeqAncestors = theClass->GetFullInheritsNames();
	    Standard_Integer                        j;
	    Handle(MS_InstClass)                    anNestInst;

	    locRes = Standard_False;

	    if (!theClass->GetMyCreator().IsNull()) {
	      anNestInst  = theClass->GetMyCreator();
	      
	      if (anNestInst->GenClass()->IsSameString(aSeqGType->Value(i)->TYpeName())) {
		locRes = Standard_True;
	      }
	    }
	      
	    for (j = 1; j <= aSeqAncestors->Length() && !locRes; j++) {
	      //	      cout << aSeqAncestors->Value(j)->ToCString() << " == " << aSeqGType->Value(i)->TYpeName()->ToCString() << endl;
	      if (aSeqAncestors->Value(j)->IsSameString(aSeqGType->Value(i)->TYpeName())) {
		locRes = Standard_True;
	      }
	      else {
		Handle(MS_StdClass) tt = Handle(MS_StdClass)::DownCast(GetType(aSeqAncestors->Value(j)));

		if (!tt.IsNull()) {
		  if (!tt->GetMyCreator().IsNull()) {
		    anNestInst  = tt->GetMyCreator();
		    
		    if (anNestInst->GenClass()->IsSameString(aSeqGType->Value(i)->TYpeName())) {
		      locRes = Standard_True;
		    }
		  }
		}
	      }
	    }
	    
	    if (!locRes) {
	       ErrorMsg() << CHECKERROR << "the instantiation type " << aSeqIType->Value(i)->ToCString() << " is not compatible with the constraint type " <<  aSeqGType->Value(i)->TYpeName()->ToCString() << " in the class " << anInst->FullName()->ToCString() << endm;
	    }
	    result = result && locRes;
	  }
	}
      }
      else {
	Standard_Boolean ItsAClass = Standard_True;
	
	if (GetType(aSeqIType->Value(i))->IsKind(STANDARD_TYPE(MS_StdClass))) { 
	  Handle(MS_Type)                         gettypeper1 = GetType(aSeqIType->Value(i));
	  Handle(MS_StdClass)                     aStdClass = *((Handle(MS_StdClass)*)&gettypeper1);
	  Handle(TColStd_HSequenceOfHAsciiString) aSeqAncestors = aStdClass->GetFullInheritsNames();
	  Standard_Integer                        j;
	  Handle(MS_InstClass)                    anNestInst;
	  Handle(MS_GenClass)                     theGenClass;
	  
	  locRes = Standard_False;

	  // look for a generic class : our constraint is an instantiation
	  // 
	  if (!aStdClass->GetMyCreator().IsNull()) {
	    anNestInst  = aStdClass->GetMyCreator();
//	    cout << "G " << anNestInst->GenClass()->ToCString() << " = " << "GI " << aSeqGType->Value(i)->TYpeName()->ToCString() << endl;
	    if (anNestInst->GenClass()->IsSameString(aSeqGType->Value(i)->TYpeName())) {
	      locRes = Standard_True;
	    }
	  }

	  for (j = 1; j <= aSeqAncestors->Length() && !locRes; j++) {
	    gettypeper1 = GetType(aSeqAncestors->Value(j));
	    aStdClass = *((Handle(MS_StdClass)*)&gettypeper1);
	    
	    if (!aStdClass->GetMyCreator().IsNull()) {
	      anNestInst = aStdClass->GetMyCreator();

	      if (anNestInst->GenClass()->IsSameString(aSeqGType->Value(i)->TYpeName())) {
		locRes = Standard_True;
	      }
	    }
	  }

	  if (locRes) {  
	    Handle(TCollection_HAsciiString) typeName;
	    Standard_Integer                 pos = 0;

	    gettypeper1 = GetType(anNestInst->GenClass());
	    theGenClass = *((Handle(MS_GenClass)*)&gettypeper1);

	    for (j = 1; j <= aSeqCont->Length(); j++) {
//	      cout << "Contrainte Defined : " << aSeqCont->Value(j)->ToCString() << endl;

	      if (IsDefined(aSeqCont->Value(j))) {
		typeName = aSeqIType->Value(j);
		pos = j;
	      }
	      else {
		Standard_Integer k;

		typeName.Nullify();
		
		for (k = 1; k <= aSeqGType->Length() && typeName.IsNull(); k++) {
//		  cout << "C " << aSeqCont->Value(j)->ToCString() << " = " << "GI " << aSeqGType->Value(k)->Name()->ToCString() << endl;
		  if (aSeqGType->Value(k)->Name()->IsSameString(aSeqCont->Value(j))) {
		    typeName = aSeqIType->Value(k);
		    pos = k;
		  }
		}
	      }   
	    }

	    if (typeName.IsNull()) {
	      locRes = Standard_False;
	      ItsAClass = Standard_True;
	      ErrorMsg() << CHECKERROR << "the constraint of generic type " << aSeqGType->Value(i)->Name()->ToCString() << " does not have an instantiation parameter of the type " << aSeqCont->Value(pos) << " as required." << endm;
	    }
	  }
	  else {
	    ErrorMsg() << CHECKERROR << "in class " << anInst->FullName()->ToCString() << ", the instantiation parameter " << aSeqGType->Value(i)->Name()->ToCString() << " must be an instantiation not the type " <<  aSeqIType->Value(i)->ToCString() << endm;
	    locRes    = Standard_False;
	    ItsAClass = Standard_False;
	  }

	}
	else {
	  ErrorMsg() << CHECKERROR << "in class " << anInst->FullName()->ToCString() << ", the instantiation parameter " << aSeqGType->Value(i)->Name()->ToCString() << " must be a class not the type " <<  aSeqIType->Value(i)->ToCString() << endm;
	  locRes    = Standard_False;
	  ItsAClass = Standard_False;
	}

	result = result && locRes;
      }
    }
  }

  return result;
}

// WARNING : do not modify this part unless you understand it
//
Standard_Boolean MS_MetaSchema::CheckField(const Handle(MS_Field)& aField) const
{
  Handle(MS_Class) aClass = aField->Class();
  Standard_Boolean result = Standard_True;



  // for generic class, we test if all fields are defined types or generic items
  //
  if (!IsDefined(aField->TYpe()) && aClass->IsKind(STANDARD_TYPE(MS_GenClass))) {
    Handle(MS_GenClass)           aGenClass = *((Handle(MS_GenClass)*)&aClass);
    Handle(MS_HSequenceOfGenType) gTypes    = aGenClass->GenTypes();
    Standard_Integer              nbType;
    Standard_Boolean              IHaveTheType = Standard_False;
    
    for (nbType = 1; nbType <= gTypes->Length() && !IHaveTheType; nbType++) {
      if (aField->TYpe()->IsSameString(gTypes->Value(nbType)->Name())) {
	IHaveTheType = Standard_True;
      }
    }
    
    if (!IHaveTheType) {
      result = Standard_False;
      ErrorMsg() << CHECKERROR << "the field " << aField->Name()->ToCString() 
	   << " from generic class " << aClass->FullName()->ToCString()
	   << " has an unknown type or an undeclared generic item : " << aField->TYpe()->ToCString() << endm;
    }
  }
  else if (IsDefined(aField->TYpe())) {
    Handle(MS_Type)    t     = GetType(aField->TYpe());
    Handle(MS_NatType) aType = Handle(MS_NatType)::DownCast(t);
    Handle(MS_Class)   aCl   = Handle(MS_Class)::DownCast(t);

    if (t->Private()) {
      if (!t->Package()->FullName()->IsSameString(aClass->Package()->FullName())) {
	Standard_Boolean cankill = Standard_True;

	if (aClass->IsKind(STANDARD_TYPE(MS_StdClass))) {
	  Handle(MS_StdClass) stdClass = Handle(MS_StdClass)::DownCast(aClass);

	  if (!stdClass->GetMyCreator().IsNull()) {
	    if (stdClass->GetMyCreator()->FullName()->IsSameString(aField->TYpe())) {
	      cankill = Standard_False;
	    }
	  }
	}
	if (cankill) {
	  ErrorMsg() << CHECKERROR << "the private type " << t->FullName()->ToCString() << " cannot be used in field " << aField->Name()->ToCString() << " of class " << aClass->FullName()->ToCString() << endm;
	  result = Standard_False;
	}
      }
    }

    if (aClass->IsStorable() || aClass->IsPersistent()) {
      
      if (!aCl.IsNull()) {
	if (aCl->IsKind(STANDARD_TYPE(MS_InstClass))) {
	  Handle(MS_InstClass) inst = *((Handle(MS_InstClass)*)&aCl);
	  Handle(MS_Type)      gettypeloc = GetType(inst->GenClass());

	  aCl = *((Handle(MS_Class)*)&gettypeloc);
	}
	
	if (!(aCl->IsStorable() || aCl->IsPersistent())) {
	  result = Standard_False;
	  ErrorMsg() << CHECKERROR << "the field " << aField->Name()->ToCString() << " from class " << aClass->FullName()->ToCString()
	       << " must be Storable or Persistent." << endm;
	}
      }
      else if (!aType.IsNull()) {
	if (aType->IsKind(STANDARD_TYPE(MS_Pointer))) {
	  result = Standard_False;
	  ErrorMsg() << CHECKERROR << "the field " << aField->Name()->ToCString() << " from class " << aClass->FullName()->ToCString()
	       << " has a pointer type : " << aField->TYpe()->ToCString() << endm;
	}
	else if (aType->IsKind(STANDARD_TYPE(MS_PrimType))) {
	  Handle(MS_PrimType) aPType = *((Handle(MS_PrimType)*)&aType);
	  
	  if (!aPType->IsStorable()) {
	    result = Standard_False;
	    ErrorMsg() << CHECKERROR << "the field " << aField->Name()->ToCString() << " from class " << aClass->FullName()->ToCString()
		 << " has a non storable type : " << aField->TYpe()->ToCString() << endm;
	  }
	}
      } 
    }
    
    if (IsDefined(aField->Name())) {
      result = Standard_False;
      ErrorMsg() << CHECKERROR << "the field " << aField->Name()->ToCString() << " from class " << aClass->FullName()->ToCString()
	   << " must not have the same name than a type." << endm;
    }
  }
  
  return result;
}

Standard_Boolean MS_MetaSchema::AddClient(const Handle(MS_Client)& aCommon) 
{
  Standard_Boolean result = Standard_False;

  if (!aCommon.IsNull()) {
    if (!myClients.IsBound(aCommon->FullName())) {
      result = Standard_True;
      myClients.Bind(aCommon->FullName(),aCommon);
    }
    else {
      result = Standard_False;
    }
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::AddClient - aCommon is NULL");
  }

  return result;
}

MS_DataMapIteratorOfMapOfGlobalEntity MS_MetaSchema::Clients() const 
{
  MS_DataMapIteratorOfMapOfGlobalEntity anIterator(myClients);

  return anIterator;
}

const Handle(MS_Client)& MS_MetaSchema::GetClient(const Handle(TCollection_HAsciiString)& anClient) const 
{
  static Handle(MS_Client) NULLRESULT;

  if (!anClient.IsNull()) {
    return  *((Handle(MS_Client) *) &(myClients.Find(anClient)));
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::GetClient - anClient is NULL");
  }

  return NULLRESULT;
}

void MS_MetaSchema::RemoveClient(const Handle(TCollection_HAsciiString)& aPackage)
{
  if (!aPackage.IsNull()) {
    if (myClients.IsBound(aPackage))
      myClients.UnBind(aPackage);
  }
}

Standard_Boolean MS_MetaSchema::IsClient(const Handle(TCollection_HAsciiString)& anClient) const 
{
  Standard_Boolean result = Standard_False;
  
  if (!anClient.IsNull()) {
    result = myClients.IsBound(anClient);
  }
  else {
    Standard_NullObject::Raise("MS_MetaSchema::IsClient - anClient is NULL");
  }
  
  return result;
}
