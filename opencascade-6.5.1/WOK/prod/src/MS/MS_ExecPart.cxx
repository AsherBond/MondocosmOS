#include <MS_ExecPart.ixx>

MS_ExecPart::MS_ExecPart(const Handle(TCollection_HAsciiString)& anExecPart) : MS_Exec(anExecPart)
{
  myFiles = new MS_HSequenceOfExecFile;
  myLib   = new TColStd_HSequenceOfHAsciiString;
  myExt   = new TColStd_HSequenceOfHAsciiString;
}

void MS_ExecPart::AddFile(const Handle(MS_ExecFile)& aFile)
{
  myFiles->Append(aFile);
}

void MS_ExecPart::AddLibrary(const Handle(TCollection_HAsciiString)& aName)
{
  myLib->Append(aName);
}

void MS_ExecPart::AddExternal(const Handle(TCollection_HAsciiString)& aName)
{
  myExt->Append(aName);
}

Handle(MS_HSequenceOfExecFile) MS_ExecPart::Files() const 
{
  return myFiles;
}

Handle(TColStd_HSequenceOfHAsciiString) MS_ExecPart::Libraries() const 
{
  return myLib;
}

Handle(TColStd_HSequenceOfHAsciiString) MS_ExecPart::Externals() const 
{
  return myExt;
}

