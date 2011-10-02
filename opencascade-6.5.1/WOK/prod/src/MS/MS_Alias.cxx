#include <MS_Alias.ixx>
#include <Standard_NullObject.hxx>
#include <MS.hxx>
#include <MS_MetaSchema.hxx>

MS_Alias::MS_Alias(const Handle(TCollection_HAsciiString)& aName, 
		   const Handle(TCollection_HAsciiString)& aPackage, 
		   const Handle(TCollection_HAsciiString)& aContainer, 
		   const Standard_Boolean aPrivate) : MS_NatType(aName,aPackage,aContainer,aPrivate)
{
}

void MS_Alias::Type(const Handle(TCollection_HAsciiString)& aType, 
		    const Handle(TCollection_HAsciiString)& aPackage)
{
  if (aType.IsNull()) {
    Standard_NullObject::Raise("MS_Alias::Type - aType is NULL");
  }

  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_Alias::Type - aPackage is NULL");
  }

  myType = MS::BuildFullName(aPackage,aType);
}

const Handle(TCollection_HAsciiString)& MS_Alias::Type() const 
{
  return myType;
}


Handle(TCollection_HAsciiString) MS_Alias::DeepType() const
{
  Handle(MS_Alias)                 alias; 
  Handle(TCollection_HAsciiString) realType = myType;
  Standard_Boolean                 mustGoDown = Standard_False;
 
  if (GetMetaSchema()->IsDefined(realType)) {
    mustGoDown = GetMetaSchema()->GetType(realType)->IsKind(STANDARD_TYPE(MS_Alias));
  }

  while (mustGoDown) {
    alias = Handle(MS_Alias)::DownCast(GetMetaSchema()->GetType(realType));
    realType = alias->Type();

    if (GetMetaSchema()->IsDefined(realType)) {
      mustGoDown = GetMetaSchema()->GetType(realType)->IsKind(STANDARD_TYPE(MS_Alias));
    }
  }

  return realType;
} 
