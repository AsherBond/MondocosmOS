#include <MS_Engine.ixx>

MS_Engine::MS_Engine(const Handle(TCollection_HAsciiString)& anEngine) : MS_Exec(anEngine)
{
  myUses = new TColStd_HSequenceOfHAsciiString;
  myInterfaces = new TColStd_HSequenceOfHAsciiString;
}

void MS_Engine::Use(const Handle(TCollection_HAsciiString)& aEngine)
{
  myUses->Append(aEngine);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Engine::Uses() const 
{
  return myUses;
}

void MS_Engine::Interface(const Handle(TCollection_HAsciiString)& aInterface)
{
  myInterfaces->Append(aInterface);
}

Handle(TColStd_HSequenceOfHAsciiString) MS_Engine::Interfaces() const 
{
  return myInterfaces;
}


