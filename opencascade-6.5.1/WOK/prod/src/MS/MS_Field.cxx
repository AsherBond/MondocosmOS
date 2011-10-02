#include <MS_Field.ixx>
#include <MS.hxx>
#include <MS_MetaSchema.hxx>

MS_Field::MS_Field(const Handle(MS_Class)& aClass, 
		   const Handle(TCollection_HAsciiString)& aName) : MS_Common(aName), myClass(aClass->FullName()),myDimension(new TColStd_HSequenceOfInteger),myProtected(Standard_False)
{
}

void MS_Field::Class(const Handle(MS_Class)& aClass)
{
  myClass     = aClass->FullName();
}

Handle(MS_Class) MS_Field::Class() const 
{
  Handle(MS_Class) aClass = Handle(MS_Class)::DownCast(GetMetaSchema()->GetType(myClass));

  return aClass;
}

void MS_Field::TYpe(const Handle(TCollection_HAsciiString)& aType)
{
  myType = aType;
}

void MS_Field::TYpe(const Handle(TCollection_HAsciiString)& aType, const Handle(TCollection_HAsciiString)& aPackage)
{
  if (!aPackage->IsEmpty()) {
    myType = MS::BuildFullName(aPackage,aType);
  }
  // for generic type
  //
  else {
    myType = aType;
  }
}

const Handle(TCollection_HAsciiString)& MS_Field::TYpe() const 
{
  return myType;
}

void MS_Field::Dimension(const Standard_Integer aDimension)
{
  myDimension->Append(aDimension);
}

const Handle(TColStd_HSequenceOfInteger)& MS_Field::Dimensions() const 
{
  return myDimension;
}

void MS_Field::Protected(const Standard_Boolean aProtected)
{
  myProtected = aProtected;
}

Standard_Integer MS_Field::Protected() const 
{
  return myProtected;
}

