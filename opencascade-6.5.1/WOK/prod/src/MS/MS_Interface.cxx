#include <MS_Interface.ixx>
#include <MS.hxx>
#include <Standard_NullObject.hxx>

MS_Interface::MS_Interface(const Handle(TCollection_HAsciiString)& aInterface) : MS_GlobalEntity(aInterface)
{
  myUses     = new TColStd_HSequenceOfHAsciiString;
  myPackages = new TColStd_HSequenceOfHAsciiString;
  myClasses  = new TColStd_HSequenceOfHAsciiString;
  myMethods  = new TColStd_HSequenceOfHAsciiString;
}

void MS_Interface::Use(const Handle(TCollection_HAsciiString)& anInterface)
{
  Standard_Integer len     = myUses->Length();
  Standard_Boolean IsFound = Standard_False;

   if (anInterface.IsNull()) {
    Standard_NullObject::Raise("MS_Interface::Use - anInterface is NULL");
  }

  // we look if the package is defined
  //
  if (!anInterface->IsSameString(Name())) {
    for (Standard_Integer i = 1; i <= len && !IsFound; i++) {
      if (myUses->Value(i)->IsSameString(anInterface)) {
	IsFound = Standard_True;
      }
    }
  }
  else {
    IsFound = Standard_True;
  }

  if (!IsFound) {
    myUses->Append(anInterface);
  }
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Interface::Uses() const 
{
  return myUses;
}

void MS_Interface::Class(const Handle(TCollection_HAsciiString)& aClass, const Handle(TCollection_HAsciiString)& aPackage)
{
  Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(aPackage,aClass);

  myClasses->Append(aFullName);
}

void MS_Interface::Class(const Handle(TCollection_HAsciiString)& aClass)
{
  Handle(TCollection_HAsciiString) aFullName = new TCollection_HAsciiString(aClass);

  myClasses->Append(aFullName);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Interface::Classes() const 
{
  return myClasses;
}

void MS_Interface::Package(const Handle(TCollection_HAsciiString)& aPackage)
{
  myPackages->Append(aPackage);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Interface::Packages() const 
{
  return myPackages;
}

void MS_Interface::Method(const Handle(TCollection_HAsciiString)& aMethod)
{
  myMethods->Append(aMethod);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Interface::Methods() const 
{
  return myMethods;
}




