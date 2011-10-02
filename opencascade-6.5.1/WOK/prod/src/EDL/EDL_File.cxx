#include <EDL_File.ixx>
#include <stdio.h>

EDL_File::EDL_File()
{
  myFile = NULL;
}

EDL_File::EDL_File(const Standard_CString aName)
{
  myFile = NULL;

  if (aName != NULL) {
    myName = new TCollection_HAsciiString(aName);
  }
}

void EDL_File::Assign(const EDL_File& aFile)
{
  if (aFile.GetName() != NULL) {
    myName = new TCollection_HAsciiString(aFile.GetName());
  }

  myFile = aFile.GetFile();
}

void EDL_File::Destroy()
{
}

Standard_CString EDL_File::GetName() const 
{
  return myName->ToCString();
}

Standard_Boolean EDL_File::Open()
{
  Standard_Boolean aResult = Standard_False;

  if (!myName.IsNull()) {
    myFile = (Standard_Address)fopen(myName->ToCString(),"w");

    if (myFile != NULL) {
      aResult = Standard_True;
    }
  }
  
  return aResult;
}

void EDL_File::Write(const Standard_CString aBuffer)
{
  if (myFile != NULL) {
    fprintf((FILE*)myFile,"%s",aBuffer);
  }
}

Standard_CString EDL_File::Read() const 
{
  return NULL;
}

void EDL_File::Close()
{
  if (myFile != NULL) {
    fclose((FILE*)myFile);
    myFile = NULL;
  }
}

Standard_Address EDL_File::GetFile() const 
{
  return myFile;
}

