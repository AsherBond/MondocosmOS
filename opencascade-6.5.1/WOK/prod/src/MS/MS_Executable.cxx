#include <MS_Executable.ixx>

MS_Executable::MS_Executable(const Handle(TCollection_HAsciiString)& anExecutable) : MS_Exec(anExecutable)
{
}

void MS_Executable::AddParts(const Handle(MS_HSequenceOfExecPart)& theParts)
{
  myParts = theParts;
}

Handle(MS_HSequenceOfExecPart) MS_Executable::Parts() const 
{
  return myParts;
}

