#include <MS_ExecFile.ixx>

MS_ExecFile::MS_ExecFile(const Handle(TCollection_HAsciiString)& aName)
{
  myName     = aName;
  myLanguage = MS_CPP;
}

void MS_ExecFile::SetName(const Handle(TCollection_HAsciiString)& aName)
{
  myName     = aName;
}

Handle(TCollection_HAsciiString) MS_ExecFile::Name() const 
{
  return myName;
}

void MS_ExecFile::SetLanguage(const MS_Language aLang)
{
  myLanguage = aLang;
}

MS_Language MS_ExecFile::Language() const 
{
  return myLanguage;
}

