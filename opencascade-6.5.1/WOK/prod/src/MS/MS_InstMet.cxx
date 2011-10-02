#include <MS_InstMet.ixx>
#include <MS_AccessMode.hxx>

MS_InstMet::MS_InstMet(const Handle(TCollection_HAsciiString)& aName, 
		       const Handle(TCollection_HAsciiString)& aClass) : MS_MemberMet(aName,aClass), myMode(0)
{
}

void MS_InstMet::Static(const Standard_Boolean aStatic)
{
  if (aStatic) {
    myMode |= MSINSTMET_STATIC;
  }
  else {
    myMode &= (myMode ^ MSINSTMET_STATIC);
  }
}

Standard_Boolean MS_InstMet::IsStatic() const 
{
  return (myMode & MSINSTMET_STATIC);
}

void MS_InstMet::Deferred(const Standard_Boolean aDeferred)
{
  if (aDeferred) {
    myMode |= MSINSTMET_DEFERRED;
  }
  else {
    myMode &= (myMode ^ MSINSTMET_DEFERRED);
  }
}

Standard_Boolean MS_InstMet::IsDeferred() const 
{
  return (myMode & MSINSTMET_DEFERRED);
}

void MS_InstMet::Redefined(const Standard_Boolean aRedefined)
{
  if (aRedefined) {
    myMode |= MSINSTMET_REDEFINED;
  }
  else {
    myMode &= (myMode ^ MSINSTMET_REDEFINED);
  }
}

Standard_Boolean MS_InstMet::IsRedefined() const 
{
  return (myMode & MSINSTMET_REDEFINED);
}

void MS_InstMet::Const(const Standard_Boolean aConst)
{
  if (!aConst) {
    myMode |= MSINSTMET_OUT;
  }
  else {
    myMode &= (myMode ^ MSINSTMET_OUT);
    myMode &= (myMode ^ MSINSTMET_MUTABLE);
  }
}

Standard_Boolean MS_InstMet::IsConst() const
{
  return (!(myMode & MSINSTMET_OUT) && !(myMode & MSINSTMET_MUTABLE));
}


Standard_Boolean MS_InstMet::IsMutable() const
{
  return (myMode & MSINSTMET_MUTABLE);
}

Standard_Boolean MS_InstMet::IsOut() const
{
  return (myMode & MSINSTMET_OUT);
}

void MS_InstMet::ConstMode(const Standard_Integer aMode)
{
  myMode |= aMode;
}

void MS_InstMet::Mode(const Standard_Integer aMode)
{
  myMode = aMode;
}

Standard_Integer MS_InstMet::GetMode() const
{
  return myMode;
}



