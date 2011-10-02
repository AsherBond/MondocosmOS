#include <MS_StdClass.ixx>
#include <MS_MetaSchema.hxx>
#include <Standard_NullObject.hxx>
#include <Standard_NoSuchObject.hxx>

MS_StdClass::MS_StdClass(const Handle(TCollection_HAsciiString)& aName, 
			 const Handle(TCollection_HAsciiString)& aPackage) :
                         MS_Class(aName,aPackage), myNestingState(Standard_False)
{
    myComment        = new TCollection_HAsciiString("");
}

MS_StdClass::MS_StdClass(const Handle(TCollection_HAsciiString)& aName, 
			 const Handle(TCollection_HAsciiString)& aPackage, 
			 const Handle(TCollection_HAsciiString)& Mother, 
			 const Standard_Boolean aPrivate, 
			 const Standard_Boolean aDeferred, 
			 const Standard_Boolean aInComplete) 
: MS_Class(aName,aPackage,Mother,aPrivate,aDeferred,aInComplete), myNestingState(Standard_False)
{
    myComment        = new TCollection_HAsciiString("");
}

void MS_StdClass::Validity(const Handle(TCollection_HAsciiString)& , 
			   const Handle(TCollection_HAsciiString)& ) const 
{
}

void MS_StdClass::CreatedBy(const Handle(MS_InstClass)& anInstClass) 
{
  myInstClass = anInstClass;
}

Handle(MS_InstClass) MS_StdClass::GetMyCreator() const
{
  return myInstClass;
}

void MS_StdClass::SetGenericState(const Standard_Boolean aNestingState) 
{
  myNestingState = aNestingState;
}

Standard_Boolean MS_StdClass::IsGeneric() const
{
  return myNestingState;
}

//Handle(TCollection_HAsciiString)  MS_StdClass::Comment() const
//{
//  return myComment;
//}

//void MS_StdClass::SetComment(const Handle(TCollection_HAsciiString)& aComment)
//{
//  myComment->AssignCat(aComment);
//}

