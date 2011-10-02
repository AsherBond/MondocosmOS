#include <MS_GenClass.ixx>
#include <MS_TraductionError.hxx>
#include <MS_GenType.hxx>
#include <MS_MetaSchema.hxx>
#include <Standard_NullObject.hxx>

MS_GenClass::MS_GenClass(const Handle(TCollection_HAsciiString)& aName, const Handle(TCollection_HAsciiString)& aPackage) 
: MS_Class(aName,aPackage)
{
  myGenTypes = new MS_HSequenceOfGenType; 
  myNestStd  = new TColStd_HSequenceOfHAsciiString;
  myNestIns  = new TColStd_HSequenceOfHAsciiString;
  myNested   = new TColStd_HSequenceOfHAsciiString;
  myComment  = new TCollection_HAsciiString("");
}

MS_GenClass::MS_GenClass(const Handle(TCollection_HAsciiString)& aName, 
			 const Handle(TCollection_HAsciiString)& aPackage, 
			 const Standard_Boolean aPrivate, 
			 const Standard_Boolean aDeferred, 
			 const Standard_Boolean aInComplete) : MS_Class(aName,aPackage)
{
  myGenTypes = new MS_HSequenceOfGenType; 
  myNestStd  = new TColStd_HSequenceOfHAsciiString;
  myNestIns  = new TColStd_HSequenceOfHAsciiString;
  myNested   = new TColStd_HSequenceOfHAsciiString;
  myComment  = new TCollection_HAsciiString("");

  Private(aPrivate);
  Deferred(aDeferred);
  Incomplete(aInComplete);
}

void MS_GenClass::NestedStdClass(const Handle(TCollection_HAsciiString)& aClass)
{
  Standard_Boolean alreadyHere = Standard_False;
  
  for (Standard_Integer i = 1; i <= myNestStd->Length() && !alreadyHere; i++) {
    if (myNestStd->Value(i)->IsSameString(aClass)) {
      alreadyHere = Standard_True;
    }
  }
  
  if (!alreadyHere) {
    myNestStd->Append(aClass);
  }
}

void MS_GenClass::NestedInsClass(const Handle(TCollection_HAsciiString)& aClass)
{
  Standard_Boolean alreadyHere = Standard_False;
  
  for (Standard_Integer i = 1; i <= myNestIns->Length() && !alreadyHere; i++) {
    if (myNestIns->Value(i)->IsSameString(aClass)) {
      alreadyHere = Standard_True;
    }
  }
  
  if (!alreadyHere) {
    myNestIns->Append(aClass);
  }
}

Handle(TColStd_HSequenceOfHAsciiString) MS_GenClass::GetNestedStdClassesName() const 
{
  return myNestStd;
}

Handle(TColStd_HSequenceOfHAsciiString) MS_GenClass::GetNestedInsClassesName() const 
{
  return myNestIns;
}

void MS_GenClass::RemoveNested(const Handle(TCollection_HAsciiString)& aClass)
{
  Standard_Boolean alreadyHere = Standard_False;
  
  for (Standard_Integer i = 1; i <= myNested->Length() && !alreadyHere; i++) {
    if (myNested->Value(i)->IsSameString(aClass)) {
      alreadyHere = Standard_True;
      myNested->Remove(i);
    }
  }
}

void MS_GenClass::AddNested(const Handle(TCollection_HAsciiString)& aClass)
{
  myNested->Append(aClass);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_GenClass::GetNestedName() const 
{
  return myNested;
}

//void MS_GenClass::Validity(const Handle(TCollection_HAsciiString)& aName, const Handle(TCollection_HAsciiString)& aPackage) const 
void MS_GenClass::Validity(const Handle(TCollection_HAsciiString)& , const Handle(TCollection_HAsciiString)& ) const 
{
}

void MS_GenClass::GenType(const Handle(TCollection_HAsciiString)& anItem)
{
  Handle(MS_Type)    tmpType;
  Handle(MS_GenType) tmpGenType;
  Standard_Integer   size,i;
  
  if (anItem->IsSameString(Name(), Standard_True)) {
    cout << "Error : MS_GenClass::GenType - the generic type " 
      << anItem->ToCString() << " and his generic class have the same name." << endl;
    MS_TraductionError::Raise();
  }
  
  size = myNestStd->Length();
  
  for(i = 1; i <= size; i++) {
    if (anItem->IsSameString(myNestStd->Value(i), Standard_True)) {
      cout << "Error : MS_GenClass::GenType - the generic type "  << anItem->ToCString() 
	<< " has the same name that a nested standard class." << endl;
      MS_TraductionError::Raise();
    }
  }

  size = myNestIns->Length();
  
  for(i = 1; i <= size; i++) {
    if (anItem->IsSameString(myNestIns->Value(i), Standard_True)) {
      cout << "Error : MS_GenClass::GenType - the generic type "  << anItem->ToCString() 
	<< " has the same name that a nested instantiated class." << endl;
      MS_TraductionError::Raise();
    }
  }

  size = myGenTypes->Length();
  
  for (i = 1; i <= size; i++) {
    tmpType = myGenTypes->Value(i);
    
    if(anItem->IsSameString(tmpType->Name(),Standard_True)) {
      cout << "Error : MS_GenClass::GenType - the generic type " << anItem->ToCString() 
	<< " is yet defined." << endl;
      MS_TraductionError::Raise();
    }
  }
  
  tmpGenType = new MS_GenType(this, anItem);
  myGenTypes->Append(tmpGenType); 
}

void MS_GenClass::GenType(const Handle(TCollection_HAsciiString)& anItem, const Handle(TCollection_HAsciiString)& aConstraint)
{
  Handle(MS_Type)    tmpType;
  Handle(MS_GenType) tmpGenType;
  Standard_Integer   size,i;
  
  if (anItem->IsSameString(Name(), Standard_True)) {
    cout << "Error : MS_GenClass::GenType - the generic type " 
      << anItem->ToCString() << " and his generic class have the same name." << endl;
    MS_TraductionError::Raise();
  }
  
  size = myNestStd->Length();
  
  for(i = 1; i <= size; i++) {
    if (anItem->IsSameString(myNestStd->Value(i), Standard_True)) {
      cout << "Error : MS_GenClass::GenType - the generic type "  << anItem->ToCString() 
	<< " has the same name that a nested standard class." << endl;
      MS_TraductionError::Raise();
    }
  }

  size = myNestIns->Length();
  
  for(i = 1; i <= size; i++) {
    if (anItem->IsSameString(myNestIns->Value(i), Standard_True)) {
      cout << "Error : MS_GenClass::GenType - the generic type "  << anItem->ToCString() 
	<< " has the same name that a nested instantiated class." << endl;
      MS_TraductionError::Raise();
    }
  }

  size = myGenTypes->Length();
  
  for (i = 1; i <= size; i++) {
    tmpType = myGenTypes->Value(i);
    
    if(anItem->IsSameString(tmpType->Name(),Standard_True)) {
      cout << "Error : MS_GenClass::GenType - the generic type " << anItem->ToCString() 
	<< " is yet defined." << endl;
      MS_TraductionError::Raise();
    }
  }
  
  tmpGenType = new MS_GenType(this, anItem, aConstraint);
  tmpGenType->MetaSchema(GetMetaSchema());
  
  myGenTypes->Append(tmpGenType); 
}

void MS_GenClass::GenType(const Handle(MS_GenType)& anItem)
{
  if (!anItem.IsNull()) {
    myGenTypes->Append(anItem);
  }
  else {
    Standard_NullObject::Raise("MS_GenClass::GenType - anItem is NULL.");
  }
}

Handle(MS_HSequenceOfGenType) MS_GenClass::GenTypes() const 
{
  return myGenTypes;
}

void MS_GenClass::CheckNested() const 
{
}





