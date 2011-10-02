#include <MS_Param.ixx>
#include <Standard_NoSuchObject.hxx>
#include <MS_MetaSchema.hxx>
#include <MS.hxx>
#include <MS_HSequenceOfMemberMet.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <MS_MemberMet.hxx>
#include <MS_Class.hxx>

#include <MS_AccessMode.hxx>

MS_Param::MS_Param(const Handle(MS_Method)& aMethod, 
		   const Handle(TCollection_HAsciiString)& aName) : MS_Common(aName), myMethod(aMethod.operator->()),myAccessMode(0),myType(new TCollection_HAsciiString)
{
}

void MS_Param::Method(const Handle(MS_Method)& aMethod)
{
  myMethod = aMethod.operator->();
}

Standard_Boolean MS_Param::IsIn() const 
{
  return ((myAccessMode & MS_IN) || (myAccessMode & MS_INOUT));
}

Standard_Boolean MS_Param::IsOut() const 
{
  return ((myAccessMode & MS_OUT) || (myAccessMode & MS_INOUT));
}

Standard_Boolean MS_Param::IsImmutable() const 
{
  Standard_Boolean                 result = Standard_False;
  Handle(TCollection_HAsciiString) tname  = TypeName();

  if (GetMetaSchema()->IsDefined(tname)) {
    Handle(MS_Type) t = GetMetaSchema()->GetType(tname);

    if (t->IsKind(STANDARD_TYPE(MS_Class))) {
      MS_Class *c = (MS_Class *)t.operator->();

      if ((c->IsPersistent() || c->IsTransient()) && !(myAccessMode & MS_MUTABLE)) {
	result = Standard_True;
      }
    }
    else result = (myAccessMode & MS_IMMUTABLE);
  }

  return result;
}

Standard_Boolean MS_Param::IsMutable() const 
{
  return (myAccessMode & MS_MUTABLE);
}

Standard_Boolean MS_Param::IsAny() const 
{
  return (myAccessMode & MS_ANY);
}

void MS_Param::AccessMode(const Standard_Integer aMode)
{
  myAccessMode |= aMode;
}

Standard_Integer MS_Param::GetAccessMode() const 
{
  return myAccessMode;
}

void MS_Param::Type(const Handle(TCollection_HAsciiString)& aTypeName,
		    const Handle(TCollection_HAsciiString)& aPackName)
{
  if (!aPackName->IsEmpty()) {
    myType = MS::BuildFullName(aPackName,aTypeName);
  }
  // it s a generic type
  //
  else {
    myType = aTypeName;
  }
}


void MS_Param::Type(const Handle(TCollection_HAsciiString)& aTypeName)
{
  myType = aTypeName;
}

Handle(MS_Type) MS_Param::Type() const 
{
  Handle(MS_Type) aType;

  if (GetMetaSchema() != UndefinedHandleAddress) {
    Handle(TCollection_HAsciiString) TheTypeName = TypeName();

    if (GetMetaSchema()->IsDefined(TheTypeName)) {
      aType = GetMetaSchema()->GetType(TheTypeName);
    }
    else {
      Handle(TCollection_HAsciiString) aMsg = new TCollection_HAsciiString("Error : MS_Param::Type - Type ");

      aMsg->AssignCat(TheTypeName);
      aMsg->AssignCat(" not found.");

      Standard_NoSuchObject::Raise(aMsg->ToCString());
    }
  }

  return aType;
}

// for type param we must do a complex computation 
// if its a like type
//
Handle(TCollection_HAsciiString) MS_Param::TypeName() const 
{
  if (IsLike()) {
    if (myMethod->IsKind(STANDARD_TYPE(MS_MemberMet))) {
      Handle(MS_MemberMet)                    ammet     = Handle(MS_MemberMet)::DownCast(myMethod);
      Handle(TCollection_HAsciiString)        metname   = myMethod->FullName(),
                                              TheTypeName  = ammet->Class();
      Handle(MS_Class)                        aClass    = Handle(MS_Class)::DownCast(GetMetaSchema()->GetType(ammet->Class()));
      Handle(TColStd_HSequenceOfHAsciiString) ancestors = aClass->GetFullInheritsNames();
      Handle(MS_HSequenceOfMemberMet)         metseq;
      Standard_Integer                        i,j;
      Standard_Boolean                        IsFound;

      for (i = 1; i <= ancestors->Length(); i++) {
	aClass  =  Handle(MS_Class)::DownCast(GetMetaSchema()->GetType(ancestors->Value(i)));
	metseq  = aClass->GetMethods();
	IsFound = Standard_False;

	for (j = 1; j <= metseq->Length() && !IsFound; j++) {
	  if (metseq->Value(j)->IsSameSignature(metname)) {
	    IsFound = Standard_True;
	    TheTypeName  = aClass->FullName();
	  }
	}
      }

      return TheTypeName;
    }
  }

  return myType;
}

void MS_Param::Like(const Standard_Boolean aLike)
{
  if (aLike) {
    myAccessMode |= MS_LIKE;
  }
  else {
    myAccessMode &= (myAccessMode ^ MS_LIKE);
  }
}

Standard_Boolean MS_Param::IsLike() const 
{
  return (myAccessMode & MS_LIKE);
}


void MS_Param::ItsItem()
{
  myAccessMode |= MS_GENITEM;
}

void MS_Param::ItsNotItem()
{
  myAccessMode &= (myAccessMode ^ MS_GENITEM);
}

Standard_Boolean MS_Param::IsItem() const
{
  return (myAccessMode & MS_GENITEM);
}

MS_TypeOfValue MS_Param::GetValueType() const 
{
  return MS_NONE;
}
