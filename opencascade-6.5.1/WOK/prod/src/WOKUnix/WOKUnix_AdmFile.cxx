#ifndef WNT
#include <Standard_Stream.hxx>

#include <WOKUnix_AdmFile.ixx>

#include <WOKTools_Messages.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <TCollection_AsciiString.hxx>
#include <TCollection_HAsciiString.hxx>

#include <OSD_Protection.hxx>

#define MAX_READBUFFER 1024

//=======================================================================
//function : WOKUnix_AdmFile
//purpose  : 
//=======================================================================
WOKUnix_AdmFile::WOKUnix_AdmFile()
{
}

//=======================================================================
//function : WOKUnix_AdmFile
//purpose  : 
//=======================================================================
WOKUnix_AdmFile::WOKUnix_AdmFile(const Handle(WOKUnix_Path)& apath) 
{
  SetPath(OSD_Path(apath->Name()->String()));
}

//=======================================================================
//function : WOKUnix_AdmFile
//purpose  : 
//=======================================================================
WOKUnix_AdmFile::WOKUnix_AdmFile(const Handle(TCollection_HAsciiString)& apath) 
{
  SetPath(OSD_Path(apath->String()));
}

//=======================================================================
//function : Read
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKUnix_AdmFile::Read()  
{
  Handle(TColStd_HSequenceOfHAsciiString) aresult = new TColStd_HSequenceOfHAsciiString;
  Standard_Boolean tobecontinued;
  Standard_Boolean iscontinued;
  char buffer[MAX_READBUFFER];

  if(Exists() == Standard_False) 
    {
      ErrorMsg() << "WOKUnix_AdmFile::Read" <<  "File : " << Name() << "does not exists" << endm;
      Standard_ProgramError::Raise("WOKUnix_AdmFile::Read : File dos not exists");
    }
  
  if(KindOfFile() != OSD_FILE)  
    {
      ErrorMsg() << "WOKUnix_AdmFile::Read" <<  "File : " << Name() << " is not a plain file" << endm;
      Standard_ProgramError::Raise("WOKUnix_AdmFile::Read");
    }

  ifstream astream(Name()->ToCString(), ios::in);

  tobecontinued = Standard_False;

  *buffer = '\0';
  while(astream.getline(buffer, MAX_READBUFFER))
    {
      TCollection_AsciiString linebuf(buffer);
      linebuf.LeftAdjust();

      if(!linebuf.IsEmpty() && linebuf.Value(1) != '#' )
	{
	  if(linebuf.Value(linebuf.Length()) == '\\') 
	    {
	      linebuf.Trunc(linebuf.Length() - 1);
	      iscontinued = Standard_True;
	    }
	  else 
	    {
	      iscontinued = Standard_False;
	    }

	  if(tobecontinued) {
	    aresult->Value(aresult->Length())->AssignCat(linebuf.ToCString());
	  }
	  else {
	    aresult->Append(new TCollection_HAsciiString(linebuf.ToCString()));
	  }

	  tobecontinued = iscontinued;
	}
      else tobecontinued = Standard_False;
      *buffer = '\0';
    }
  astream.close();
  return aresult;
}

#endif
