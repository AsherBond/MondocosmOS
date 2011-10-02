#include <MS_Enum.ixx>
#include <Standard_NullObject.hxx>
#include <MS.hxx>
#include <MS_Package.hxx>

MS_Enum::MS_Enum(const Handle(TCollection_HAsciiString)& aName, 
		 const Handle(TCollection_HAsciiString)& aPackage, 
		 const Handle(TCollection_HAsciiString)& aContainer, 
		 const Standard_Boolean aPrivate) : MS_NatType(aName,aPackage,aContainer,aPrivate) 
{
  myEnums   = new TColStd_HSequenceOfHAsciiString;
  myEnumComment = new TCollection_HAsciiString("");
}

void MS_Enum::Enum(const Handle(TCollection_HAsciiString)& anEnum)
{
  if (anEnum.IsNull()) {
    Standard_NullObject::Raise("MS_Enum::Enum - anEnum is NULL");
  }

  Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(Package()->Name(),anEnum);

  myEnums->Append(aFullName);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Enum::Enums() const 
{
  return myEnums;
}

void MS_Enum::Check() const 
{
  Standard_Integer len = myEnums->Length();
  Standard_Integer i,j;
 
  for (i = 1; i <= len; i++) {
     for (j = i + 1; j <= len; j++) {
       if (myEnums->Value(i)->IsSameString(myEnums->Value(j))) {
	 cout << "Error : Value " << myEnums->Value(j)->ToCString() 
              << " is defined twice in enumeration " << FullName()->ToCString() << endl;
	 Standard_NullObject::Raise("");
       }
     }
  }
}

Handle(TCollection_HAsciiString)  MS_Enum::Comment() const
{
  return myEnumComment;
}

void MS_Enum::SetComment(const Handle(TCollection_HAsciiString)& aComment)
{
  myEnumComment->AssignCat(aComment);
}
