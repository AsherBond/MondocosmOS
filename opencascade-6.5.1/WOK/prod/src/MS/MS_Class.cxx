#include <MS_Class.ixx>
#include <MS_MetaSchema.hxx>
#include <MS_StdClass.hxx>
#include <Standard_NullObject.hxx>
#include <Standard_NoSuchObject.hxx>
#include <MS.hxx>

MS_Class::MS_Class(const Handle(TCollection_HAsciiString)& aName, 
	const Handle(TCollection_HAsciiString)& aPackage) : MS_Type(aName)
{
  if (!aPackage.IsNull()) {
    Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(aPackage,aName);
    
    Package(aPackage);
    
    FullName(aFullName);
    myInherits       = new TColStd_HSequenceOfHAsciiString;
    myUses           = new TColStd_HSequenceOfHAsciiString;
    myRaises         = new TColStd_HSequenceOfHAsciiString;
    myMethods        = new MS_HSequenceOfMemberMet;
    myFields         = new MS_HSequenceOfField;
    myFriendMets     = new TColStd_HSequenceOfHAsciiString;
    myFriends        = new TColStd_HSequenceOfHAsciiString;
    myComment        = new TCollection_HAsciiString("");
    myIncomplete     = Standard_True;
    myPrivate        = Standard_False;
    myDeferred       = Standard_False;
  }
  else {
    Standard_NullObject::Raise("MS_Class::MS_Class - aPakage is NULL");
  }
}

MS_Class::MS_Class(const Handle(TCollection_HAsciiString)& aName, 
		   const Handle(TCollection_HAsciiString)& aPackage, 
		   const Handle(TCollection_HAsciiString)& Mother, 
		   const Standard_Boolean aPrivate, 
		   const Standard_Boolean aDeferred, 
		   const Standard_Boolean aInComplete) : MS_Type(aName)
{
  if (!aPackage.IsNull()) {
    Handle(TCollection_HAsciiString) aFullName =  MS::BuildFullName(aPackage,aName);
    
    if (GetMetaSchema() != (MS_MetaSchemaPtr)UndefinedHandleAddress) {
      Package(aPackage);
    }
    
    myIncomplete     = aInComplete;
    myPrivate        = aPrivate;
    myDeferred       = aDeferred;
    myMother         = Mother;
    myInherits       = new TColStd_HSequenceOfHAsciiString;
    myUses           = new TColStd_HSequenceOfHAsciiString;
    myRaises         = new TColStd_HSequenceOfHAsciiString;
    myMethods        = new MS_HSequenceOfMemberMet;
    myFields         = new MS_HSequenceOfField;
    myFriendMets     = new TColStd_HSequenceOfHAsciiString;
    myFriends        = new TColStd_HSequenceOfHAsciiString;
    myComment        = new TCollection_HAsciiString("");

    FullName(aFullName);
  }
  else {
    Standard_NullObject::Raise("MS_Class::MS_Class - aPakage is NULL");
  }
}

void MS_Class::Deferred(const Standard_Boolean aDeferred)
{
  myDeferred = aDeferred;
}

Standard_Boolean MS_Class::Deferred() const 
{
  return myDeferred;
}

void MS_Class::Private(const Standard_Boolean aPrivate)
{
  myPrivate = aPrivate;
}

Standard_Boolean MS_Class::Private() const 
{
  return myPrivate;
}

void MS_Class::Check(const Handle(TCollection_HAsciiString)& , const Handle(TCollection_HAsciiString)& ) const 
{
}

void MS_Class::Inherit(const Handle(MS_Class)& aClass)
{
  if (!aClass.IsNull()) {
    myInherits->Append(aClass->FullName());
  }
  else {
    Standard_NullObject::Raise("MS_Class::MS_Inherit - aClass is NULL");
  }
}

void MS_Class::Inherit(const Handle(TCollection_HAsciiString)& aClass, const Handle(TCollection_HAsciiString)& aPackage)
{
  Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(aPackage,aClass);
  
  myInherits->Append(aFullName);
}

void MS_Class::Inherit(const Handle(TCollection_HAsciiString)& aClass)
{
  myInherits->Append(aClass);
}

Handle(MS_HSequenceOfClass) MS_Class::GetInherits() const 
{
  Handle(MS_HSequenceOfClass) aClassSeq = new MS_HSequenceOfClass();
  
  if (GetMetaSchema() != UndefinedHandleAddress) {    
    Standard_Integer i;
    
    for (i = 1; i <= myInherits->Length(); i++) {
      if (GetMetaSchema()->IsDefined(myInherits->Value(i))) {
	Standard_Boolean WrongAncestor = Standard_False;
	Handle(MS_Type)  aType         = GetMetaSchema()->GetType(myInherits->Value(i));
	Handle(MS_Class) aClass        = Handle(MS_Class)::DownCast(aType);
	
	if (!aClass.IsNull()) {
	  if (aClass->IsKind(STANDARD_TYPE(MS_StdClass))) {
	    aClassSeq->Append(aClass);
	  }
	  else {
	    WrongAncestor = Standard_True;
	  }
	}
	else {
	  WrongAncestor = Standard_True;
	}
	
	if (WrongAncestor) {
	  Handle(TCollection_HAsciiString) ErrorMsg = new TCollection_HAsciiString;
	  
	  ErrorMsg->AssignCat("Error : Class : ");
	  ErrorMsg->AssignCat(FullName());
	  ErrorMsg->AssignCat(" must inherits from a normal class (not generic, nor from a primitive type).");
	  Standard_NoSuchObject::Raise(ErrorMsg->ToCString());
	}
      }
      else {
	Handle(TCollection_HAsciiString) ErrorMsg = new TCollection_HAsciiString;
	
	ErrorMsg->AssignCat("Error : Type ");
	ErrorMsg->AssignCat(myInherits->Value(i));
	ErrorMsg->AssignCat(" is not defined.");
	Standard_NoSuchObject::Raise(ErrorMsg->ToCString());
      }
    }
  }
  
  return aClassSeq;
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Class::GetFullInheritsNames()
{
  Standard_Integer i;
  Handle(MS_Type)  aType;
  MS_Class*        aClass = 0l;

  if (GetMetaSchema() == UndefinedHandleAddress) {
    cerr << "Error : MS_Class::GetFullInheritsNames - Cannot compute inheritance tree without MetaSchema : " << 
         FullName()->ToCString() << endl;
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
    aClass       = (MS_Class*)aType.operator->();
    listAncestor = aClass->GetInheritsNames();

    if (listAncestor->Length() == 0) {
      End = Standard_True;
    }
  }

  return result;
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Class::GetInheritsNames() const 
{
  return myInherits;
}

void MS_Class::Use(const Handle(TCollection_HAsciiString)& aClass, const Handle(TCollection_HAsciiString)& aPackage)
{
  Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(aPackage,aClass);
  
  myUses->Append(aFullName);
}

void MS_Class::Use(const Handle(TCollection_HAsciiString)& aClass)
{
  myUses->Append(aClass);
}

Handle(MS_HSequenceOfType) MS_Class::GetUses() const 
{
  Handle(MS_HSequenceOfType) aTypeSeq;
  
  if (GetMetaSchema() != UndefinedHandleAddress) {
    Standard_Integer           i;
    
    aTypeSeq = new MS_HSequenceOfType;
    
    for (i = 1; i <= myUses->Length(); i++) {
      if (GetMetaSchema()->IsDefined(myUses->Value(i))) {
	aTypeSeq->Append(GetMetaSchema()->GetType(myUses->Value(i)));
      }
      else {
	Handle(TCollection_HAsciiString) aStr = new TCollection_HAsciiString("Error : The type ");
	
	aStr->AssignCat(myUses->Value(i));
	aStr->AssignCat(" is not defined.");
	Standard_NoSuchObject::Raise(aStr->ToCString());
      }
    }
  }
  else {
    Standard_NullObject::Raise("MS_Class::GetUses - The MetaSchema is NULL.");
  }
  
  return aTypeSeq;
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Class::GetUsesNames() const 
{
  return myUses;
}

void MS_Class::Field(const Handle(MS_Field)& aField)
{
  if (!aField.IsNull()) {
    myFields->Append(aField);
  }
  else {
    Standard_NullObject::Raise("MS_Class::MS_Field - aField is NULL");
  }
}

Handle(MS_HSequenceOfField) MS_Class::GetFields() const 
{
  return myFields;
}

void MS_Class::Method(const Handle(MS_MemberMet)& aMethod)
{
  myMethods->Append(aMethod);
}

Handle(MS_HSequenceOfMemberMet) MS_Class::GetMethods() const 
{
  return myMethods;
}

void MS_Class::Raises(const Handle(TCollection_HAsciiString)& aExcept, const Handle(TCollection_HAsciiString)& aPackage)
{
  Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(aPackage,aExcept);
  
  myRaises->Append(aFullName);
}

void MS_Class::Raises(const Handle(TCollection_HAsciiString)& aExcept)
{
  myRaises->Append(aExcept);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Class::GetRaises() const 
{
  return myRaises;
}

void MS_Class::FriendMet(const Handle(TCollection_HAsciiString)& aMethod)
{
  myFriendMets->Append(aMethod);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Class::GetFriendMets() const 
{
  return myFriendMets;
}

void MS_Class::Friend(const Handle(TCollection_HAsciiString)& aClass, const Handle(TCollection_HAsciiString)& aPackage)
{
  Handle(TCollection_HAsciiString) aFullName = MS::BuildFullName(aPackage,aClass);
  
  myFriends->Append(aFullName);
}

void MS_Class::Friend(const Handle(TCollection_HAsciiString)& aClass)
{
  Handle(TCollection_HAsciiString) aFullName = new TCollection_HAsciiString(aClass);
  
  myFriends->Append(aFullName);
}

void MS_Class::Friend(const Handle(MS_Class)& aClass)
{
  myFriends->Append(aClass->FullName());
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Class::GetFriendsNames() const 
{
  return myFriends;
}

void MS_Class::Incomplete(const Standard_Boolean aIncomplete)
{
  myIncomplete = aIncomplete;
}

Standard_Boolean MS_Class::Incomplete() const 
{
  return myIncomplete;
}

void MS_Class::Mother(const Handle(TCollection_HAsciiString)& aMother)
{
  myMother = aMother;
}

Handle(TCollection_HAsciiString) MS_Class::GetMother() const 
{
  return myMother;
}

Standard_Boolean MS_Class::IsNested() const
{
  return !myNestingClass.IsNull();
}

void MS_Class::NestingClass(const Handle(TCollection_HAsciiString)& aNesting)
{
  myNestingClass = aNesting;
}

Handle(TCollection_HAsciiString) MS_Class::GetNestingClass() const 
{
  return myNestingClass;
}

Standard_Boolean MS_Class::IsPersistent()
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

Standard_Boolean MS_Class::IsTransient()
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

Standard_Boolean MS_Class::IsStorable()
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

Handle(TCollection_HAsciiString)  MS_Class::Comment() const
{
//  const Handle(TCollection_HAsciiString)& startComment  = new TCollection_HAsciiString("///");

//  if (myComment->IsSameString(startComment)) myComment->Clear();
  return myComment;
}

void MS_Class::SetComment(const Handle(TCollection_HAsciiString)& aComment)
{
  myComment->AssignCat(aComment);
}

