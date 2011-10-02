#include <Standard_NotImplemented.hxx>

#include <MS_ExternMet.ixx>

MS_ExternMet::MS_ExternMet(const Handle(TCollection_HAsciiString)& aName) : MS_Method(aName)
{
}

MS_ExternMet::MS_ExternMet(const Handle(TCollection_HAsciiString)& aName, 
			   const Handle(TCollection_HAsciiString)& aPackage) : MS_Method(aName)
{
  myPackage = aPackage;
}

void MS_ExternMet::CreateFullName() 
{
  Handle(TCollection_HAsciiString) myIdName;

  MS_Method::CreateFullName();

  if (!myPackage.IsNull()) {
    myIdName = new TCollection_HAsciiString(myPackage);
    myIdName->AssignCat("::");
    myIdName->AssignCat(FullName());
    FullName(myIdName);
  }
}

void MS_ExternMet::Package(const Handle(TCollection_HAsciiString)& aPack)
{
  myPackage = aPack;
}

Handle(TCollection_HAsciiString) MS_ExternMet::Package() const 
{
  return myPackage;
}

void MS_ExternMet::Raises(const Handle(TCollection_HAsciiString)& aExcept)
{
// Standard_NotImplemented::Raise("MS_ExternMet::Raises(const Handle(TCollection_HAsciiString)& aExcept) not implemented") ;
 MS_Method::Raises( aExcept ) ;
}

//void MS_ExternMet::Raises(const Handle(TCollection_HAsciiString)& aExcept, const Handle(TCollection_HAsciiString)& aPackage)
void MS_ExternMet::Raises(const Handle(TCollection_HAsciiString)& , const Handle(TCollection_HAsciiString)& )
{
}



