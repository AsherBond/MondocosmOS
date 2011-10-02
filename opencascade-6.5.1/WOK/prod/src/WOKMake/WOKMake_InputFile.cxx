// File:	WOKMake_InputFile.cxx
// Created:	Mon Nov 20 20:11:05 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_Stream.hxx>

#include <Standard_ProgramError.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKernel_Locator.hxx>

#include <WOKMake_InputFile.ixx>

#define READBUF_SIZE 1024

//=======================================================================
//function : WOKMake_InputFile
//purpose  : 
//=======================================================================
WOKMake_InputFile::WOKMake_InputFile() 
{
}

//=======================================================================
//function : WOKMake_InputFile
//purpose  : 
//=======================================================================
WOKMake_InputFile::WOKMake_InputFile(const Handle(TCollection_HAsciiString)& anid,
				     const Handle(WOKernel_File)& afile, 
				     const Handle(WOKBuilder_Entity)& anent,
				     const Handle(WOKUtils_Path)& apath) 
: WOKMake_StepFile(anid, afile, anent, apath)
{
}

//=======================================================================
//function : WOKMake_InputFile
//purpose  : 
//=======================================================================
WOKMake_InputFile::WOKMake_InputFile(const Handle(WOKMake_OutputFile)& outfile)
  : WOKMake_StepFile(outfile->ID(), outfile->File(), outfile->BuilderEntity(), outfile->LastPath())
{
  SetLocateFlag(outfile->IsLocateAble());
  SetPhysicFlag(outfile->IsPhysic());
  SetStepID(outfile->IsStepID());
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ReadLine
//purpose  : 
//=======================================================================
void WOKMake_InputFile::ReadLine(Standard_IStream &                astream,
				 const Handle(WOKernel_Locator)&   alocator,
				 Handle(WOKMake_InputFile)&        infile)
{
  Standard_Integer i,len;
  Handle(TCollection_HAsciiString) id;
  Handle(TCollection_HAsciiString) aunitname, atype, aname;
  Handle(TCollection_HAsciiString) oldpath;
  Handle(WOKernel_File) afile;
  Handle(WOKMake_InputFile) ainfile;
  static char flags[READBUF_SIZE];
  static char anamebuf[READBUF_SIZE], apathbuf[READBUF_SIZE];
  
  *flags = *anamebuf = *apathbuf = '\0';

  if(!astream)
    {
      infile.Nullify();
      return;
    }

  //astream >> setw(READBUF_SIZE) >> flags >> setw(READBUF_SIZE) >> anamebuf >> setw(READBUF_SIZE) >> apathbuf;
  astream.get(flags, READBUF_SIZE, ' ');
  astream.ignore(READBUF_SIZE, ' ');
  astream.get(anamebuf, READBUF_SIZE, ' ');
  astream.ignore(READBUF_SIZE, ' ');
  astream.get(apathbuf, READBUF_SIZE);
  astream.ignore(READBUF_SIZE, '\n');


  if(anamebuf[0] == '\0' || flags[0] == '\0' || apathbuf[0] == '\0' )
    {
      infile.Nullify();
      return;
    }

  oldpath = new TCollection_HAsciiString(apathbuf);
  ainfile = new WOKMake_InputFile;
    
  len = strlen(flags);
  for(i=0; i<len; i++)
    {
      switch(flags[i])
	{
	case '+':
	  ainfile->SetDirectFlag(Standard_True);
	  break;
	case '-':
	  ainfile->SetDirectFlag(Standard_False);
	  break;
	case 'V':
	  ainfile->SetPhysicFlag(Standard_False);
	  break;
	case 'S':
	  ainfile->SetStepID(Standard_True);
	  break;
	}
    }

  if(anamebuf[0] != '.')
    {
      Handle(TCollection_HAsciiString) id = new TCollection_HAsciiString(anamebuf);

      if(ainfile->IsPhysic())
	{
	  afile = alocator->Locate(id);
      
	  if(!afile.IsNull())
	    {
	      id = afile->LocatorName();
	    }
	}

      ainfile->SetID(id);
      ainfile->SetFile(afile);
      ainfile->SetLastPath(new WOKUtils_Path(oldpath));
      ainfile->SetLocateFlag(Standard_True);

      //if(!strcmp(id->Token(":", 2)->ToCString(), "msentity"))
      //{
      //  ainfile->SetPhysicFlag(Standard_False);
      //}
    }
  else
    {
      ainfile->SetID(oldpath);
      ainfile->SetLastPath(new WOKUtils_Path(oldpath));
      ainfile->SetLocateFlag(Standard_False);
    }
	

  infile = ainfile;
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WriteLine
//purpose  : 
//=======================================================================
void WOKMake_InputFile::WriteLine(Standard_OStream&                       astream,
				  const Handle(WOKMake_InputFile)&        infile)
{
  if(infile.IsNull())
    {
      Standard_ProgramError::Raise("WOKMake_InputFile::WriteLine : Null Input");
    }

  if(infile->IsDirectInput()) astream << "+";
  else                        astream << "-";
  if(!infile->IsPhysic())     astream << "V";
  if(infile->IsStepID())      astream << "S";

  astream << " ";

  if(!infile->IsLocateAble())
    {
	  astream << ". "  << infile->LastPath()->Name()->ToCString() << endl;
    }
  else
    {
      if(infile->IsPhysic())
	{
	  astream << infile->File()->LocatorName()->ToCString() << " " << infile->LastPath()->Name()->ToCString() << endl;
	}
      else
	{
	  astream << infile->ID()->ToCString() << " ." << endl;
	}
    }
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ReadFile
//purpose  : 
//=======================================================================
Standard_Integer WOKMake_InputFile::ReadFile(const Handle(WOKUtils_Path)&    apath, 
					     const Handle(WOKernel_Locator)& alocator,
					     WOKMake_IndexedDataMapOfHAsciiStringOfInputFile& amap)
{
  if(apath->Exists())
    {
      Standard_Integer i = 0;
      ifstream astream(apath->Name()->ToCString());
      Handle(WOKMake_InputFile) infile;
      Handle(TCollection_HAsciiString) locatorname;

      ReadLine(astream, alocator, infile);

      while(!infile.IsNull())
	{
	  i++;
	  amap.Add(infile->ID(), infile);
	  ReadLine(astream, alocator, infile);
	}

      astream.close();
      return i;
    }
  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WriteFile
//purpose  : 
//=======================================================================
Standard_Integer WOKMake_InputFile::WriteFile(const Handle(WOKUtils_Path)& apath, 
					      const WOKMake_IndexedDataMapOfHAsciiStringOfInputFile& amap)
{
  Standard_Integer i;
  ofstream astream(apath->Name()->ToCString(), ios::out);
  
  if(!astream)
    {
      ErrorMsg() << "WOKMake_StepInput::Dump" << "Could not open " << apath->Name() << endm;
      Standard_ProgramError::Raise("");
    }
  
  
  for(i=1; i<=amap.Extent(); i++)
    {
      WriteLine(astream, amap(i));
    }
  astream.close();
  return 0;
}



//=======================================================================
//Author   : Jean Gautier (jga)
//function : ReadFile
//purpose  : 
//=======================================================================
Standard_Integer WOKMake_InputFile::ReadFile(const Handle(WOKUtils_Path)& apath, 
					     const Handle(WOKernel_Locator)& alocator,
					     const Handle(WOKMake_HSequenceOfInputFile)& aseq)
{
  if(apath.IsNull() || aseq.IsNull())
    {
      Standard_ProgramError::Raise("WOKMake_InputFile::ReadFile : NullInput");
    }

  if(apath->Exists())
    {
      Standard_Integer i = 0;
      ifstream astream(apath->Name()->ToCString(), ios::in);
      Handle(WOKMake_InputFile) infile;
      Handle(TCollection_HAsciiString) locatorname;

      ReadLine(astream, alocator, infile);

      while(!infile.IsNull())
	{
	  i++;
	  aseq->Append(infile);
	  ReadLine(astream, alocator, infile);
	}

      astream.close();
      return i;
    }
  return 0;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WriteFile
//purpose  : 
//=======================================================================
Standard_Integer WOKMake_InputFile::WriteFile(const Handle(WOKUtils_Path)& apath, 
					      const Handle(WOKMake_HSequenceOfInputFile)& aseq)
{
  Standard_Integer i;
  ofstream astream(apath->Name()->ToCString(), ios::out);
  
  if(!astream)
    {
      ErrorMsg() << "WOKMake_StepInput::Dump" << "Could not open " << apath->Name() << endm;
      Standard_ProgramError::Raise("");
    }
  
  
  for(i=1; i<=aseq->Length(); i++)
    {
      WriteLine(astream, aseq->Value(i));
    }
  astream.close();
  return 0;
}



//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetDirectFlag
//purpose  : 
//=======================================================================
void WOKMake_InputFile::SetDirectFlag(const Standard_Boolean aflag)
{
  if(aflag)
    myattr |= STEPFILE_DIRECT;
  else
    myattr &= (STEPFILE_DIRECT ^ myattr);
}
