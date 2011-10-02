
#include <Standard_NotImplemented.hxx>

#include <MS_MemberMet.ixx>
#include <MS.hxx>

MS_MemberMet::MS_MemberMet(const Handle(TCollection_HAsciiString)& aName, 
			   const Handle(TCollection_HAsciiString)& aClass) : MS_Method(aName)
{
  myClass = aClass;
}

void MS_MemberMet::Class(const Handle(TCollection_HAsciiString)& aClass)
{
  myClass = aClass;
}

//void MS_MemberMet::Class(const Handle(TCollection_HAsciiString)& aClass, const Handle(TCollection_HAsciiString)& aPackage)
void MS_MemberMet::Class(const Handle(TCollection_HAsciiString)& , const Handle(TCollection_HAsciiString)& )
{
}

Handle(TCollection_HAsciiString) MS_MemberMet::Class() const 
{
  return myClass;
}

void MS_MemberMet::CreateFullName() 
{
  Handle(TCollection_HAsciiString) myIdName;

  MS_Method::CreateFullName();

  if (!myClass.IsNull()) {
    myIdName = new TCollection_HAsciiString(myClass);
    myIdName->AssignCat("::");
    myIdName->AssignCat(FullName());
    FullName(myIdName);
  }
}

void MS_MemberMet::Protected(const Standard_Boolean aProtected)
{
  myProtected = aProtected;
}

Standard_Boolean MS_MemberMet::IsProtected() const 
{
  return myProtected;
}

void MS_MemberMet::Raises(const Handle(TCollection_HAsciiString)& aExcept)
{
// Standard_NotImplemented::Raise("MS_MemberMet::Raises(const Handle(TCollection_HAsciiString)& aExcept) not implemented") ;
 MS_Method::Raises( aExcept ) ;
}

void MS_MemberMet::Raises(const Handle(TCollection_HAsciiString)& aExcept, const Handle(TCollection_HAsciiString)& aPackage)
{
  Handle(TCollection_HAsciiString) aRaise = MS::BuildFullName(aPackage,aExcept);

  MS_Method::Raises(aRaise);
}

