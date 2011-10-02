#include <MS_Pointer.ixx>
#include <Standard_NullObject.hxx>
#include <MS.hxx>

MS_Pointer::MS_Pointer(const Handle(TCollection_HAsciiString)& aName, 
		       const Handle(TCollection_HAsciiString)& aPackage, 
		       const Handle(TCollection_HAsciiString)& aContainer, 
		       const Standard_Boolean aPrivate) : MS_NatType(aName,aPackage,aContainer,aPrivate)
{
  
}

void MS_Pointer::Type(const Handle(TCollection_HAsciiString)& aType, const Handle(TCollection_HAsciiString)& aPackage)
{
  if (aType.IsNull()) {
    Standard_NullObject::Raise("MS_Pointer::Type - aType is NULL");
  }
  
  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_Pointer::Type - aPackage is NULL");
  }

  myType = MS::BuildFullName(aPackage,aType);
}

const Handle(TCollection_HAsciiString)& MS_Pointer::Type() const 
{
  return myType;
}



