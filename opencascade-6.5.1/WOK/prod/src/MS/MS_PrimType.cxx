#include <MS_PrimType.ixx>
#include <Standard_NullObject.hxx>
#include <MS.hxx>
#include <MS_MetaSchema.hxx>
#include <Standard_NoSuchObject.hxx>
#include <MS_Class.hxx>
#include <MS.hxx>

MS_PrimType::MS_PrimType(const Handle(TCollection_HAsciiString)& aName, 
			 const Handle(TCollection_HAsciiString)& aPackage, 
			 const Handle(TCollection_HAsciiString)& aContainer, 
			 const Standard_Boolean aPrivate) : MS_NatType(aName,aPackage,aContainer,aPrivate)
{
  myInherits = new TColStd_HSequenceOfHAsciiString;
}

void MS_PrimType::Inherit(const Handle(TCollection_HAsciiString)& aClass,
			  const Handle(TCollection_HAsciiString)& aPackage)
{
  if (aClass.IsNull()) {
    Standard_NullObject::Raise("MS_PrimType::Inherit - aClass is NULL");
  }

  if (aPackage.IsNull()) {
    Standard_NullObject::Raise("MS_PrimType::Inherit - aPackage is NULL");
  }

  Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(aPackage,aClass);

  myInherits->Append(aFullName);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_PrimType::GetInheritsNames() const 
{
  return myInherits;
}

Handle(TColStd_HSequenceOfHAsciiString) MS_PrimType::GetFullInheritsNames() const 
{
  Standard_Integer                        i;
  Handle(MS_Type)                         aType;
  Handle(MS_Class)                        aClass;

  if (GetMetaSchema() == UndefinedHandleAddress) {
    cerr << "Error : MS_Class::GetFullInheritsNames - Cannot compute inheritance tree without MetaSchema" << endl;
    Standard_NoSuchObject::Raise();
  }

  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;
  Handle(TColStd_HSequenceOfHAsciiString) listAncestor;
  Standard_Boolean                        End = Standard_False;

  listAncestor = myInherits;

  if (listAncestor->Length() == 0) {
    End = Standard_True;
  }
  
  while(!End) {
    for (i = 1; i <= listAncestor->Length(); i++) {
      result->Append(listAncestor->Value(i));
    }

    aType        = GetMetaSchema()->GetType(result->Value(result->Length()));
    aClass       = Handle(MS_Class)::DownCast(aType);
    listAncestor = aClass->GetInheritsNames();

    if (listAncestor->Length() == 0) {
      End = Standard_True;
    }
  }

  return result;
}

Standard_Boolean MS_PrimType::IsPersistent() const
{
  Handle(TColStd_HSequenceOfHAsciiString) Ancestors = GetFullInheritsNames();
  Handle(TCollection_HAsciiString)        Root      = MS::GetPersistentRootName();
  Standard_Integer                        i;

  if (FullName()->IsSameString(Root))
    return Standard_True;

  if (Ancestors->Length() == 0) return Standard_False;

  for (i = 1; i <= Ancestors->Length(); i++) {
    if (Ancestors->Value(i)->IsSameString(Root))
      return Standard_True;
  }

  return Standard_False;
}

Standard_Boolean MS_PrimType::IsTransient() const
{
  Handle(TColStd_HSequenceOfHAsciiString) Ancestors = GetFullInheritsNames();
  Handle(TCollection_HAsciiString)        Root      = MS::GetTransientRootName();

  if (FullName()->IsSameString(Root))
    return Standard_True;

  if (Ancestors->Length() == 0) return Standard_False;

  if (Ancestors->Value(Ancestors->Length())->IsSameString(Root))
    return Standard_True;
  else return Standard_False;
}

Standard_Boolean MS_PrimType::IsStorable() const
{
  Handle(TColStd_HSequenceOfHAsciiString) Ancestors = GetFullInheritsNames();
  Handle(TCollection_HAsciiString)        Root      = MS::GetStorableRootName();

  if (FullName()->IsSameString(Root))
    return Standard_True;

  if (Ancestors->Length() == 0) return Standard_False;

  if (Ancestors->Value(Ancestors->Length())->IsSameString(Root))
    return Standard_True;
  else return Standard_False;
}
