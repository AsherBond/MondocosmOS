#include <MS_Package.ixx>
#include <Standard_NullObject.hxx>
#include <Standard_OStream.hxx>

MS_Package::MS_Package(const Handle(TCollection_HAsciiString)& aPackage) : MS_GlobalEntity(aPackage)
{
  myUses     = new TColStd_HSequenceOfHAsciiString;
  myClasses  = new TColStd_HSequenceOfHAsciiString;
  myExcepts  = new TColStd_HSequenceOfHAsciiString;
  myEnums    = new TColStd_HSequenceOfHAsciiString;
  myAliases  = new TColStd_HSequenceOfHAsciiString;
  myPointers = new TColStd_HSequenceOfHAsciiString;
  myImports  = new TColStd_HSequenceOfHAsciiString;
  myPrims    = new TColStd_HSequenceOfHAsciiString;

  myPackComment  = new TCollection_HAsciiString("");

  myMethods  = new MS_HSequenceOfExternMet;

  myUses->Append(Name());
}

void MS_Package::Use(const Handle(TCollection_HAsciiString)& aPackage)
{
  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_Package::Use - aPackage is NULL");
  }

  if (!IsUsed(aPackage)) {
    myUses->Append(aPackage);
  }
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Package::Uses() const 
{
  return myUses;
}

Standard_Boolean MS_Package::IsUsed(const Handle(TCollection_HAsciiString)& aPackage) const
{
  Standard_Integer len     = myUses->Length();
  Standard_Boolean IsFound = Standard_False;


  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_Package::IsUsed - aPackage is NULL");
  }

  // we look if the package is defined
  //
  if (!aPackage->IsSameString(Name())) {
    for (Standard_Integer i = 1; i <= len && !IsFound; i++) {
      if (myUses->Value(i)->IsSameString(aPackage)) {
	IsFound = Standard_True;
      }
    }
  }
  else {
    IsFound = Standard_True;
  }

  return IsFound;
}

void MS_Package::Class(const Handle(TCollection_HAsciiString)& aClass)
{
  myClasses->Append(aClass);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Package::Classes() const 
{
  return myClasses;
}

Standard_Boolean MS_Package::HasClass(const Handle(TCollection_HAsciiString)& aPackage) const
{
  Standard_Integer len     = myClasses->Length();
  Standard_Boolean IsFound = Standard_False;


  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_Package::HasClass - aClass is NULL");
  }

  // we look if the package is defined
  //
  for (Standard_Integer i = 1; i <= len && !IsFound; i++) {
    if (myClasses->Value(i)->IsSameString(aPackage)) {
      IsFound = Standard_True;
    }
  }

  return IsFound;
}

void MS_Package::Except(const Handle(TCollection_HAsciiString)& anExcept)
{
  myExcepts->Append(anExcept);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Package::Excepts() const 
{
  return myExcepts;
}

Standard_Boolean MS_Package::HasExcept(const Handle(TCollection_HAsciiString)& aPackage) const
{
  Standard_Integer len     = myExcepts->Length();
  Standard_Boolean IsFound = Standard_False;


  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_Package::HasExcept - aExcept is NULL");
  }

  // we look if the package is defined
  //
  for (Standard_Integer i = 1; i <= len && !IsFound; i++) {
    if (myExcepts->Value(i)->IsSameString(aPackage)) {
      IsFound = Standard_True;
    }
  }

  return IsFound;
}

void MS_Package::Enum(const Handle(TCollection_HAsciiString)& anEnum)
{
  myEnums->Append(anEnum);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Package::Enums() const 
{
  return myEnums;
}

Standard_Boolean MS_Package::HasEnum(const Handle(TCollection_HAsciiString)& aPackage) const
{
  Standard_Integer len     = myEnums->Length();
  Standard_Boolean IsFound = Standard_False;


  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_Package::HasEnum - aEnum is NULL");
  }

  // we look if the package is defined
  //
  for (Standard_Integer i = 1; i <= len && !IsFound; i++) {
    if (myEnums->Value(i)->IsSameString(aPackage)) {
      IsFound = Standard_True;
    }
  }

  return IsFound;
}

void MS_Package::Alias(const Handle(TCollection_HAsciiString)& anAlias)
{
  myAliases->Append(anAlias);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Package::Aliases() const 
{
  return myAliases;
}

Standard_Boolean MS_Package::HasAlias(const Handle(TCollection_HAsciiString)& aPackage) const
{
  Standard_Integer len     = myAliases->Length();
  Standard_Boolean IsFound = Standard_False;


  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_Package::HasAlias - aAlias is NULL");
  }

  // we look if the package is defined
  //
  for (Standard_Integer i = 1; i <= len && !IsFound; i++) {
    if (myAliases->Value(i)->IsSameString(aPackage)) {
      IsFound = Standard_True;
    }
  }

  return IsFound;
}

void MS_Package::Pointer(const Handle(TCollection_HAsciiString)& aPointer)
{
  myPointers->Append(aPointer);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Package::Pointers() const 
{
  return myPointers;
}

Standard_Boolean MS_Package::HasPointer(const Handle(TCollection_HAsciiString)& aPackage) const
{
  Standard_Integer len     = myPointers->Length();
  Standard_Boolean IsFound = Standard_False;


  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_Package::HasPointer - aPointer is NULL");
  }

  // we look if the package is defined
  //
  for (Standard_Integer i = 1; i <= len && !IsFound; i++) {
    if (myPointers->Value(i)->IsSameString(aPackage)) {
      IsFound = Standard_True;
    }
  }

  return IsFound;
}

void MS_Package::Imported(const Handle(TCollection_HAsciiString)& anImported)
{
  myImports->Append(anImported);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Package::Importeds() const 
{
  return myImports;
}

Standard_Boolean MS_Package::HasImported(const Handle(TCollection_HAsciiString)& aPackage) const
{
  Standard_Integer len     = myImports->Length();
  Standard_Boolean IsFound = Standard_False;


  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_Package::HasImport - aImport is NULL");
  }

  // we look if the package is defined
  //
  for (Standard_Integer i = 1; i <= len && !IsFound; i++) {
    if (myImports->Value(i)->IsSameString(aPackage)) {
      IsFound = Standard_True;
    }
  }

  return IsFound;
}

void MS_Package::Primitive(const Handle(TCollection_HAsciiString)& aPrimitive)
{
  myPrims->Append(aPrimitive);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Package::Primitives() const 
{
  return myPrims;
}

Standard_Boolean MS_Package::HasPrimitive(const Handle(TCollection_HAsciiString)& aPackage) const
{
  Standard_Integer len     = myPrims->Length();
  Standard_Boolean IsFound = Standard_False;


  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_Package::HasPrimitive - aPrim is NULL");
  }

  // we look if the package is defined
  //
  for (Standard_Integer i = 1; i <= len && !IsFound; i++) {
    if (myPrims->Value(i)->IsSameString(aPackage)) {
      IsFound = Standard_True;
    }
  }

  return IsFound;
}

void MS_Package::Method(const Handle(MS_ExternMet)& aMethod)
{
  myMethods->Append(aMethod);
}

Handle(MS_HSequenceOfExternMet) MS_Package::Methods() const 
{
  return myMethods;
}

Handle(TCollection_HAsciiString)  MS_Package::Comment() const
{
  //Handle(TCollection_HAsciiString) aRetComment = myComment;
  //myComment->Clear();
  //return aRetComment;
  //const Handle(TCollection_HAsciiString)& startComment  = new TCollection_HAsciiString("///");
  //if (myPackComment->IsSameString(startComment)) myPackComment->Clear();
  return myPackComment;
}

void MS_Package::SetComment(const Handle(TCollection_HAsciiString)& aComment)
{
  myPackComment -> AssignCat(aComment);
}


