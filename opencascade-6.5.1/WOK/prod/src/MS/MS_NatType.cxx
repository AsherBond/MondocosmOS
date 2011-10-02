#include <MS_NatType.ixx>
#include <MS.hxx>

MS_NatType::MS_NatType(const Handle(TCollection_HAsciiString)& aName, 
		       const Handle(TCollection_HAsciiString)& aPackage, 
		       const Handle(TCollection_HAsciiString)& aContainer, 
		       const Standard_Boolean aPrivate) : MS_Type(aName,aPackage, aContainer, Standard_True)
{
  Handle(TCollection_HAsciiString) aFullName;

  myPrivate = aPrivate;

  aFullName = MS::BuildFullName(aPackage,aName);

  FullName(aFullName);
}

void MS_NatType::Private(const Standard_Boolean aPrivate)
{
  myPrivate = aPrivate;
}

Standard_Boolean MS_NatType::Private() const 
{
  return myPrivate;
}

