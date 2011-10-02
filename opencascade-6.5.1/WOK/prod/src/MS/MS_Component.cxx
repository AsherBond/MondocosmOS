#include <MS_Component.ixx>

MS_Component::MS_Component(const Handle(TCollection_HAsciiString)& anComponent) : MS_Exec(anComponent)
{
  myUses = new TColStd_HSequenceOfHAsciiString;
  myInterfaces = new TColStd_HSequenceOfHAsciiString;
}

void MS_Component::Use(const Handle(TCollection_HAsciiString)& aComponent)
{
  myUses->Append(aComponent);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Component::Uses() const 
{
  return myUses;
}

void MS_Component::Interface(const Handle(TCollection_HAsciiString)& aInterface)
{
  myInterfaces->Append(aInterface);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Component::Interfaces() const 
{
  return myInterfaces;
}


