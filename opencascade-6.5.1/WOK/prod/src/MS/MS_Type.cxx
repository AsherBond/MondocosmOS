#include <MS_Type.ixx>
#include <Standard_NullObject.hxx>
#include <Standard_NoSuchObject.hxx>
#include <MS_MetaSchema.hxx>

MS_Type::MS_Type(const Handle(TCollection_HAsciiString)& aName) : MS_Common(aName)
{
}

MS_Type::MS_Type(const Handle(TCollection_HAsciiString)& aName, 
		 const Handle(TCollection_HAsciiString)& aPackage, 
		 const Handle(TCollection_HAsciiString)& , 
		 const Standard_Boolean ) : MS_Common(aName)
//		 const Handle(TCollection_HAsciiString)& Container, 
//		 const Standard_Boolean InPackage) : MS_Common(aName)
{
  if (aName.IsNull()) {
    Standard_NullObject::Raise("MS_Type::MS_Type - aName is NULL");
  }

  myPackage = aPackage;
}

void MS_Type::Package(const Handle(TCollection_HAsciiString)& aPackage)
{
  if (!aPackage.IsNull()) {
    myPackage = aPackage;
  }
  else {
    Standard_NullObject::Raise("MS_Type::Package - aPackage is NULL");
  }
}

Handle(MS_Package) MS_Type::Package() const 
{
  if (GetMetaSchema() != UndefinedHandleAddress) {
    return GetMetaSchema()->GetPackage(myPackage);
  }
  else {
    Handle(TCollection_HAsciiString) aString = new TCollection_HAsciiString("MS_Type::Package - No MetaSchema for this object: ");
    
    aString->AssignCat(Name());
    Standard_NoSuchObject::Raise(aString->ToCString());
  }
    return GetMetaSchema()->GetPackage(myPackage);
}



