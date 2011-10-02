#include <MS_GenType.ixx>
#include <MS_MetaSchema.hxx>

MS_GenType::MS_GenType(const Handle(MS_Class)& aClass, 
		       const Handle(TCollection_HAsciiString)& aName, 
		       const Handle(TCollection_HAsciiString)& aType) : MS_Type(aName)
{
  if (aType.IsNull()) {
    myAny  = Standard_True;
  }
  else {
    myAny  = Standard_False;
    myType = aType;
  }

  myClass   = aClass->FullName();
  myPrivate = aClass->Private();
  myInsType = new TColStd_HSequenceOfHAsciiString;
}

MS_GenType::MS_GenType(const Handle(MS_Class)& aClass, 
		       const Handle(TCollection_HAsciiString)& aName) : MS_Type(aName)
{
  myClass   = aClass->FullName();
  myAny     = Standard_True;
  myPrivate = aClass->Private();
  myInsType = new TColStd_HSequenceOfHAsciiString;
}

Handle(MS_Type) MS_GenType::TYpe() const 
{
  Handle(MS_Type) tmpType;

  if (GetMetaSchema() != UndefinedHandleAddress) {    
    if (GetMetaSchema()->IsDefined(myType)) {
      tmpType = GetMetaSchema()->GetType(myType);
    }
  }

  return tmpType;
}

Handle(TCollection_HAsciiString) MS_GenType::TYpeName() const 
{
  return myType;
}

void MS_GenType::InstType(const Handle(TCollection_HAsciiString)& aTypeName)
{
  myInsType->Append(aTypeName);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_GenType::GetInstTypes() const
{
  return myInsType;
}

void MS_GenType::SetAny()
{
  myAny = Standard_True;
}

Standard_Boolean MS_GenType::Any() const 
{
  return myAny;
}

void MS_GenType::Package(const Handle(TCollection_HAsciiString)& aPackage)
{
  MS_Type::Package(aPackage);
}

Handle(MS_Package) MS_GenType::Package() const 
{
  return MS_Type::Package();
}

Standard_Boolean MS_GenType::Private() const 
{
  return myPrivate;
}


