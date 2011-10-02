#include <MS_InstClass.ixx>
#include <MS_TraductionError.hxx>
#include <MS.hxx>
#include <MS_GenClass.hxx>
#include <MS_StdClass.hxx>
#include <MS_GenType.hxx>
#include <MS_HSequenceOfGenType.hxx>
#include <MS_MetaSchema.hxx>
#include <MS_Package.hxx>

MS_InstClass::MS_InstClass(const Handle(TCollection_HAsciiString)& aName, 
			   const Handle(TCollection_HAsciiString)& aPackage) 
: MS_Class(aName,aPackage), myGenClass(new TCollection_HAsciiString),myBasicInsType(new TColStd_HSequenceOfHAsciiString),myInstType(new TColStd_HSequenceOfHAsciiString),
  myGenType(new TColStd_HSequenceOfHAsciiString),myNestStd(new TColStd_HSequenceOfHAsciiString),
  myNestIns(new TColStd_HSequenceOfHAsciiString),myNestNeu(new TColStd_HSequenceOfHAsciiString),myInstFlag(Standard_False)
{
    myComment        = new TCollection_HAsciiString(""); 
}

MS_InstClass::MS_InstClass(const Handle(TCollection_HAsciiString)& aName, 
			   const Handle(TCollection_HAsciiString)& aPackage, 
			   const Handle(TCollection_HAsciiString)& Mother, 
			   const Standard_Boolean aPrivate, 
			   const Standard_Boolean aDeferred, 
			   const Standard_Boolean aInComplete) 
: MS_Class(aName,aPackage,Mother,aPrivate,aDeferred,aInComplete), myGenClass(new TCollection_HAsciiString),myBasicInsType(new TColStd_HSequenceOfHAsciiString),myInstType(new TColStd_HSequenceOfHAsciiString),
  myGenType(new TColStd_HSequenceOfHAsciiString),myNestStd(new TColStd_HSequenceOfHAsciiString),
  myNestIns(new TColStd_HSequenceOfHAsciiString),myNestNeu(new TColStd_HSequenceOfHAsciiString),myInstFlag(Standard_False)
{
    myComment        = new TCollection_HAsciiString(""); 
}

void MS_InstClass::Validity(const Handle(TCollection_HAsciiString)& , 
			    const Handle(TCollection_HAsciiString)& ) const 
{
  
}

void MS_InstClass::InstType(const Handle(TCollection_HAsciiString)& aType, 
			    const Handle(TCollection_HAsciiString)& aPackage)
{
  Handle(TCollection_HAsciiString) aFullName;

  if (aPackage->IsEmpty()) {
    aFullName = aType;
  }
  else {
    aFullName = MS::BuildFullName(aPackage,aType);
  }

  myInstType->Append(aFullName);
}

void MS_InstClass::InstType(const Handle(TCollection_HAsciiString)& aType)
{
  myInstType->Append(aType);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_InstClass::InstTypes() const 
{
  return myInstType;
}

void MS_InstClass::BasicInstType(const Handle(TCollection_HAsciiString)& aType)
{
  myBasicInsType->Append(aType);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_InstClass::BasicInstTypes() const 
{
  return myBasicInsType;
}

void MS_InstClass::InstType(const Handle(MS_Type)& aType)
{
  myInstType->Append(aType->FullName());
}

void MS_InstClass::RemoveInstType(const Handle(TCollection_HAsciiString)& aType)
{
  Standard_Integer i;
  Standard_Boolean IsNotFound = Standard_False;

  for (i = 1; i <= myInstType->Length() && !IsNotFound; i++) {
    if (myInstType->Value(i)->IsSameString(aType)) {
      myInstType->Remove(i);
      IsNotFound = Standard_True;
    }
  }
}

void MS_InstClass::ResolveInstType(const Handle(TCollection_HAsciiString)& aType,
				   const Handle(TCollection_HAsciiString)& aTypeConv)
{
  Standard_Integer i;

  for (i = 1; i <= myInstType->Length(); i++) {
    if (myInstType->Value(i)->IsSameString(aType)) {
      myInstType->SetValue(i,aTypeConv);
    }
  }
}

void MS_InstClass::GenType(const Handle(TCollection_HAsciiString)& aType)
{
  myGenType->Append(aType);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_InstClass::GenTypes() const 
{
  return myGenType;
}

void MS_InstClass::GenClass(const Handle(TCollection_HAsciiString)& aName, 
			    const Handle(TCollection_HAsciiString)& aPackage)
{
  myGenClass = MS::BuildFullName(aPackage,aName);
}

void MS_InstClass::GenClass(const Handle(TCollection_HAsciiString)& aName)
{
  myGenClass = aName;
}

Handle(TCollection_HAsciiString) MS_InstClass::GenClass() const 
{
  return myGenClass;
}

void MS_InstClass::NestedStdClass(const Handle(TCollection_HAsciiString)& aClass)
{
  myNestStd->Append(aClass);
}

void MS_InstClass::RemoveNestedStdClass(const Handle(TCollection_HAsciiString)& aClass)
{
  Standard_Integer i;
  Standard_Boolean IsNotFound = Standard_False;

  for (i = 1; i <= myNestStd->Length() && !IsNotFound; i++) {
    if (myNestStd->Value(i)->IsSameString(aClass)) {
      myNestStd->Remove(i);
      IsNotFound = Standard_True;
    }
  }
}

void MS_InstClass::NestedNeuClass(const Handle(TCollection_HAsciiString)& aClass)
{
  myNestNeu->Append(aClass);
}

void MS_InstClass::RemoveNestedNeuClass(const Handle(TCollection_HAsciiString)& aClass)
{
  Standard_Integer i;
  Standard_Boolean IsNotFound = Standard_False;

  for (i = 1; i <= myNestNeu->Length() && !IsNotFound; i++) {
    if (myNestNeu->Value(i)->IsSameString(aClass)) {
      myNestNeu->Remove(i);
      IsNotFound = Standard_True;
    }
  }
}


void MS_InstClass::NestedInsClass(const Handle(TCollection_HAsciiString)& aClass)
{
  myNestIns->Append(aClass);
}

void MS_InstClass::RemoveNestedInsClass(const Handle(TCollection_HAsciiString)& aClass)
{
  Standard_Integer i;
  Standard_Boolean IsNotFound = Standard_False;

  for (i = 1; i <= myNestIns->Length() && !IsNotFound; i++) {
    if (myNestIns->Value(i)->IsSameString(aClass)) {
      myNestIns->Remove(i);
      IsNotFound = Standard_True;
    }
  }
}

Handle(TColStd_HSequenceOfHAsciiString) MS_InstClass::GetNestedStdClassesName() const 
{
  return myNestStd;
}

Handle(TColStd_HSequenceOfHAsciiString) MS_InstClass::GetNestedInsClassesName() const 
{
  return myNestIns;
}

Handle(TColStd_HSequenceOfHAsciiString) MS_InstClass::GetNestedNeuClassesName() const 
{
  return myNestIns;
}

// here we make the first phase of instantiation
// we explode the class and create the name of the nested in the metaschema
//
void MS_InstClass::Instantiates()
{
  if (myInstFlag) return;

  Handle(MS_GenClass)                     theGenClass;
  Handle(TColStd_HSequenceOfHAsciiString) theInstOfGenclass,
                                          theStdOfGenclass;
  Standard_Integer                        i;
  Standard_Boolean                        GenClassNotDefined = Standard_False;

  if (GetMetaSchema() == UndefinedHandleAddress) {
    MS_TraductionError::Raise("Error : Instantiation without MetaSchemna...");
  }

  // we look for the generic class of me
  //
  if (GetMetaSchema()->IsDefined(myGenClass)) {
    Handle(MS_Type) aType = GetMetaSchema()->GetType(myGenClass);
    
    theGenClass = Handle(MS_GenClass)::DownCast(aType);

    if (theGenClass.IsNull()) GenClassNotDefined = Standard_True;
  }
  else GenClassNotDefined = Standard_True;
  
  // by now, only a warning, but wait...
  //
  if (GenClassNotDefined) {
    cout << "Error : Generic class " << myGenClass->ToCString() << " not defined for instantiation" << endl;
    MS_TraductionError::Raise("Error : Instantiation generic class...");
  }

  // we init save buffer
  //
  if (myBasicInsType->IsEmpty()) {
    for (i = 1; i <= myInstType->Length(); i++) {
      myBasicInsType->Append(myInstType->Value(i));
    }
  }

  if ((theGenClass->GenTypes()->Length() != myBasicInsType->Length()) &&  !theGenClass->Incomplete()) {
    cout << "Warning : The instantiation types have been modified in the generic class : " << myGenClass->ToCString() 
         << " used by " << FullName()->ToCString() << endl;
  }

  Handle(TCollection_HAsciiString) theComplexName;
  Handle(MS_StdClass)              theNewStdClass;

  // for neutral nested group
  //
  if (myNestNeu->Length() == 0) {
    theStdOfGenclass = theGenClass->GetNestedName();
    
    for (i = 1; i <= theStdOfGenclass->Length(); i++) {
      theComplexName = MS::BuildComplexName(Name(),theStdOfGenclass->Value(i),theGenClass->Name());
      theNewStdClass = new MS_StdClass(theComplexName,Package()->Name());
      theNewStdClass->Mother(MS::BuildFullName(theGenClass->Package()->Name(),theStdOfGenclass->Value(i)));
      theNewStdClass->MetaSchema(GetMetaSchema());
      theNewStdClass->NestingClass(FullName());
      myNestNeu->Append(theComplexName);

      if (!GetMetaSchema()->AddType(theNewStdClass)) {
//	cout << "WARNING : neutral class " << theNewStdClass->FullName()->ToCString() << " already here..." << endl;
	GetMetaSchema()->RemoveType(theNewStdClass->FullName());
	GetMetaSchema()->AddType(theNewStdClass);
      }
    }
  }
  
  myInstFlag = Standard_True;
}
 
void MS_InstClass::InstToStd()
{
  Standard_Integer                 i,j;
  Handle(MS_Type)                  aType;
  Handle(MS_StdClass)              aNewStdClass;
  MS_StdClass*                     aPartial;
  Handle(MS_StdClass)              aGenerator;
  Handle(MS_InstClass)             aInstClass;
  Handle(TCollection_HAsciiString) aFullName;
  Handle(MS_GenClass)              aGenClass;
  Handle(MS_HSequenceOfGenType)    theGenTypes;

  aType       = GetMetaSchema()->GetType(myGenClass);
  aGenClass   = *((Handle(MS_GenClass)*)&aType);
  theGenTypes = aGenClass->GenTypes();

  myGenType->Clear();
  
  for (i = 1; i <= theGenTypes->Length(); i++) {
    GenType(theGenTypes->Value(i)->Name());
  }

  GenType(myGenClass);
  InstType(FullName());

  myNestIns->Clear();
  myNestStd->Clear();
  for (i = 1; i <= myNestNeu->Length(); i++) {
    aFullName = MS::BuildFullName(Package()->Name(),myNestNeu->Value(i));
    aType     = GetMetaSchema()->GetType(aFullName);
    aPartial  = (MS_StdClass*)aType.operator->();
    aType     = GetMetaSchema()->GetType(aPartial->GetMother());

    if (aType->IsKind(STANDARD_TYPE(MS_InstClass))) {
      myNestIns->Append(myNestNeu->Value(i));
    }
    else if (aType->IsKind(STANDARD_TYPE(MS_StdClass))) {
      myNestStd->Append(myNestNeu->Value(i));
    }
    else {
      cerr << "WARNING : MS_InstClass::InstToStd - neutral nested class with unknown type " << myNestNeu->Value(i)->ToCString() << endl;
    }
  }
  
  myNestNeu->Clear();

  for (i = 1; i <= myNestIns->Length(); i++) {
    Handle(TCollection_HAsciiString) aname,nestedName;
    
    aFullName = MS::BuildFullName(Package()->Name(),myNestIns->Value(i));
    aType     = GetMetaSchema()->GetType(aFullName);
    aPartial  = (MS_StdClass*)aType.operator->();
    aType     = GetMetaSchema()->GetType(aPartial->GetMother());

    MS_InstClass* instt = (MS_InstClass*)aType.operator->();
    MS_GenClass*  genn  = (MS_GenClass*)GetMetaSchema()->GetType(instt->GenClass()).operator->();

    GenType(aPartial->GetMother());
    InstType(aFullName);

    for (j = 1; j <= genn->GetNestedInsClassesName()->Length(); j++) {
      nestedName = genn->GetNestedInsClassesName()->Value(j);
      aname = MS::BuildComplexName(aType->Name(),nestedName,genn->Name());
      GenType(MS::BuildFullName(aType->Package()->Name(),aname));
      aname = MS::BuildComplexName(myNestIns->Value(i),nestedName,genn->Name());
      InstType(MS::BuildFullName(Package()->Name(),aname));
    }
    for (j = 1; j <= genn->GetNestedStdClassesName()->Length(); j++) {
      nestedName = genn->GetNestedStdClassesName()->Value(j);
      aname = MS::BuildComplexName(aType->Name(),nestedName,genn->Name());
      GenType(MS::BuildFullName(aType->Package()->Name(),aname));
      aname = MS::BuildComplexName(myNestIns->Value(i),nestedName,genn->Name());
      InstType(MS::BuildFullName(Package()->Name(),aname));
    }
    for (j = 1; j <= genn->GetNestedName()->Length(); j++) {
      nestedName = genn->GetNestedName()->Value(j);
      aname = MS::BuildComplexName(aType->Name(),nestedName,genn->Name());
      GenType(MS::BuildFullName(aType->Package()->Name(),aname));
      aname = MS::BuildComplexName(myNestIns->Value(i),nestedName,genn->Name());
      InstType(MS::BuildFullName(Package()->Name(),aname));
    }
  }

  for (i = 1; i <= myNestStd->Length(); i++) {
    aFullName = MS::BuildFullName(Package()->Name(),myNestStd->Value(i));
    aType     = GetMetaSchema()->GetType(aFullName);
    aPartial  = (MS_StdClass*)aType.operator->();
    GenType(aPartial->GetMother());
    InstType(aFullName);
  }

 

  for (i = 1; i <= myNestIns->Length(); i++) {
    aFullName  = MS::BuildFullName(Package()->Name(),myNestIns->Value(i));
    aType      = GetMetaSchema()->GetType(aFullName);
    aPartial   = (MS_StdClass*)aType.operator->();
    aType      = GetMetaSchema()->GetType(aPartial->GetMother());    
    aInstClass = *((Handle(MS_InstClass)*)&aType);
    aInstClass = MS::BuildInstClass(aInstClass,myNestIns->Value(i),Package()->Name(),myGenType,myInstType);
    aInstClass->NestingClass(FullName());
    if (Private()) aInstClass->Private(Private());
    aInstClass->Instantiates();
    aInstClass->InstToStd();
    if (!aInstClass->Package()->HasClass(aInstClass->Name())) {
      aInstClass->Package()->Class(aInstClass->Name());
    }
  }

  for (i = 1; i <= myNestStd->Length(); i++) {
    aFullName    = MS::BuildFullName(Package()->Name(),myNestStd->Value(i));
    aType        = GetMetaSchema()->GetType(aFullName);
    aPartial     = (MS_StdClass*)aType.operator->();
    aType        = GetMetaSchema()->GetType(aPartial->GetMother());
    aGenerator   = *((Handle(MS_StdClass)*)&aType);
    aNewStdClass = MS::BuildStdClass(aGenerator,aPartial->Name(),Package()->Name(),myGenType,myInstType);
    
    aNewStdClass->NestingClass(FullName());

    GetMetaSchema()->RemoveType(aPartial->FullName(),Standard_False);
    aNewStdClass->MetaSchema(GetMetaSchema());
    GetMetaSchema()->AddType(aNewStdClass);
    aNewStdClass->CreatedBy(this);
    if (Private()) aNewStdClass->Private(Private());
    if (!aNewStdClass->Package()->HasClass(aNewStdClass->Name())) {
      aNewStdClass->Package()->Class(aNewStdClass->Name());
    }
  }

  aNewStdClass = MS::BuildStdClass(aGenClass,Name(),Package()->Name(),myGenType,myInstType);
  aNewStdClass->Private(Private());
  aNewStdClass->CreatedBy(this);
  aNewStdClass->NestingClass(GetNestingClass());

  GetMetaSchema()->RemoveType(FullName(),Standard_False);
  aNewStdClass->MetaSchema(GetMetaSchema());
  GetMetaSchema()->AddType(aNewStdClass);
}

Standard_Boolean MS_InstClass::IsAlreadyDone() const 
{
  return myInstFlag;
}

void MS_InstClass::AlreadyDone(const Standard_Boolean flag)
{
  myInstFlag = flag;
}

void MS_InstClass::Initialize() 
{
  Standard_Integer i;

  myInstFlag = Standard_False;
  // we init save buffer
  //
  myInstType->Clear();

  for (i = 1; i <= myBasicInsType->Length(); i++) {
    myInstType->Append(myBasicInsType->Value(i));
  }
}

