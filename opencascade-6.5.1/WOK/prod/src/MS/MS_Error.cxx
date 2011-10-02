#include <MS_Error.ixx>
#include <Standard_NullObject.hxx>

MS_Error::MS_Error(const Handle(TCollection_HAsciiString)& aName, 
		   const Handle(TCollection_HAsciiString)& aPackage) : MS_StdClass(aName,aPackage)
{
  Incomplete(Standard_False);
}

MS_Error::MS_Error(const Handle(TCollection_HAsciiString)& aName, 
		   const Handle(TCollection_HAsciiString)& aPackage, 
		   const Handle(TCollection_HAsciiString)& Mother, 
		   const Standard_Boolean aPrivate, 
		   const Standard_Boolean aDeferred, 
		   const Standard_Boolean aInComplete) : MS_StdClass(aName,aPackage,Mother,aPrivate,aDeferred,aInComplete)
{
  Incomplete(Standard_False);
}

//void MS_Error::Validity(const Handle(TCollection_HAsciiString)& aName, const Handle(TCollection_HAsciiString)& aPackage) const 
void MS_Error::Validity(const Handle(TCollection_HAsciiString)& , const Handle(TCollection_HAsciiString)& ) const 
{
}

