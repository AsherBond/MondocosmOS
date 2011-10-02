#ifndef WNT
#include <stdio.h>

#include <WOKUnix_ShellStatus.ixx>

#include <TCollection_AsciiString.hxx>

//=======================================================================
//function : WOKUnix_ShellStatus
//purpose  : 
//=======================================================================
 WOKUnix_ShellStatus::WOKUnix_ShellStatus()
{
//JR  myfile.BuildNamedPipe();
  myfileend = myfile.BuildNamedPipe();
}

//=======================================================================
//function : WOKUnix_ShellStatus
//purpose  : 
//=======================================================================
 WOKUnix_ShellStatus::WOKUnix_ShellStatus(const TCollection_AsciiString& apath)
{
  myfile.BuildTemporary(apath);
}

//=======================================================================
//function : StatusFile
//purpose   :
//=======================================================================
 WOKUnix_FDescr &  WOKUnix_ShellStatus::StatusFile() 
{
  return myfile;
}

//=======================================================================
//function : No
//purpose  : 
//=======================================================================
Standard_Integer WOKUnix_ShellStatus::No() const 
{
  return myfile.FileNo();
}


//=======================================================================
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKUnix_ShellStatus::Name() const 
{
  return myfile.Name();
}

//=======================================================================
//function : Status
//purpose  : 
//=======================================================================
Standard_Integer WOKUnix_ShellStatus::Status() const 
{
  return mystatus;
}

//=======================================================================
//function : Get
//purpose  : Reads on a pipe the status of a shell command 
//=======================================================================
Standard_Integer WOKUnix_ShellStatus::Get()
{
  myfile.Flush();

  Standard_Integer nbtoread = myfile.GetNbToRead();
  if(nbtoread == 0) 
    {
      Standard_ProgramError::Raise("WOKUnix_ShellStatus::Get : Nothing to read on status pipe\n");
      return 1;
    }

  TCollection_AsciiString buf;
  
  myfile.Read(buf, nbtoread);
  if(nbtoread != buf.Length())
    {
      perror(Name()->ToCString());
      Standard_ProgramError::Raise("WOKUnix_ShellStatus::Get : Could not read from status pipe\n");
      return 1;
    }
  buf.Trunc(nbtoread);
  mystatus = buf.IntegerValue();

  return mystatus;
}
//=======================================================================
//function : GetRemote
//purpose  : Reads in a file the status of a remote shell command
//=======================================================================
Standard_Integer WOKUnix_ShellStatus::GetRemote()
{
  myfile.Flush();

  Standard_Integer nbtoread = myfile.GetSize();
  if(nbtoread == 0) 
    {
      Standard_ProgramError::Raise("WOKUnix_ShellStatus::GetRemote : Nothing to read on status file\n");
      return 1;
    }

  TCollection_AsciiString buf;
  
  myfile.Read(buf, nbtoread);
  if(nbtoread != buf.Length())
    {
      perror(Name()->ToCString());
      Standard_ProgramError::Raise("WOKUnix_ShellStatus::GetRemote : Could not read from status file\n");
      return 1;
    }
  buf.Trunc(nbtoread);
  mystatus = buf.IntegerValue();

  return mystatus;
}

//=======================================================================
//function : Destroy
//purpose  : 
//=======================================================================
void WOKUnix_ShellStatus::Destroy()
{
  if ( myfile.FileNo() >= 0 ) {
    myfile.Close();
  }
  if ( myfileend.FileNo() >= 0 ) {
    myfileend.Close();
  }

  myfile.Remove();
  myfileend.Remove();
}




#endif
