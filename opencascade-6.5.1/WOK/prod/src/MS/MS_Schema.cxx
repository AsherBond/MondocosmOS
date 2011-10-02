#include <MS_Schema.ixx>
#include <MS.hxx>

MS_Schema::MS_Schema(const Handle(TCollection_HAsciiString)& aSchema) : MS_GlobalEntity(aSchema)
{
  myPackages = new TColStd_HSequenceOfHAsciiString;
  myClasses  = new TColStd_HSequenceOfHAsciiString;
  mySchemaComment  = new TCollection_HAsciiString("");
}

void MS_Schema::Package(const Handle(TCollection_HAsciiString)& aPackage)
{
  MS::AddOnce(myPackages,aPackage);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Schema::GetPackages() const 
{
  return myPackages;
}

void MS_Schema::Class(const Handle(TCollection_HAsciiString)& aClass)
{
  MS::AddOnce(myClasses,aClass);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Schema::GetClasses() const 
{
  return myClasses;
}
Handle(TCollection_HAsciiString)  MS_Schema::Comment() const
{
//  const Handle(TCollection_HAsciiString)& startComment  = new TCollection_HAsciiString("///");
//  if (mySchemaComment->IsSameString(startComment)) mySchemaComment->Clear();
  return mySchemaComment;
}

void MS_Schema::SetComment(const Handle(TCollection_HAsciiString)& aComment)
{
  mySchemaComment->AssignCat(aComment);
}


