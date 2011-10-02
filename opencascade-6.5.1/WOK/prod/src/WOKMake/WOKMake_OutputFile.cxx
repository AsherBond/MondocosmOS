// File:	WOKMake_OutputFile.cxx
// Created:	Thu Apr 25 21:57:20 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <Standard_Stream.hxx>

#include <Standard_ProgramError.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKernel_Locator.hxx>

#include <WOKMake_OutputFile.ixx>


#define READBUF_SIZE 1024

//=======================================================================
//function : WOKMake_OutputFile
//purpose  : 
//=======================================================================
WOKMake_OutputFile::WOKMake_OutputFile() 
{
  SetMember();
}

//=======================================================================
//function : WOKMake_OutputFile
//purpose  : 
//=======================================================================
WOKMake_OutputFile::WOKMake_OutputFile(const Handle(TCollection_HAsciiString)& anid,
				       const Handle(WOKernel_File)& afile, 
				       const Handle(WOKBuilder_Entity)& anent,
				       const Handle(WOKUtils_Path)& apath) 
  : WOKMake_StepFile(anid, afile, anent, apath)
{
  SetMember();
}

//=======================================================================
//function : WOKMake_OutputFile
//purpose  : 
//=======================================================================
WOKMake_OutputFile::WOKMake_OutputFile(const Handle(WOKMake_InputFile)& infile)
  : WOKMake_StepFile(infile->ID(), infile->File(), infile->BuilderEntity(), infile->LastPath())
{
  SetLocateFlag(infile->IsLocateAble());
  SetPhysicFlag(infile->IsPhysic());
  SetStepID(infile->IsStepID());
  //if(infile->IsMember()) SetMember();
  //else SetExtern();
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ReadLine
//purpose  : 
//=======================================================================
void WOKMake_OutputFile::ReadLine(Standard_IStream &                astream,
				 const Handle(WOKernel_Locator)&   alocator,
				 Handle(WOKMake_OutputFile)&        outfile)
{
  Handle(TCollection_HAsciiString) id;
  Handle(TCollection_HAsciiString) oldpath;
  Handle(WOKernel_File) afile;
  Handle(WOKMake_OutputFile) aoutfile;
  Standard_Integer i,len;
  static char flags[READBUF_SIZE];
  static char anamebuf[READBUF_SIZE], apathbuf[READBUF_SIZE];
  
  *flags = *anamebuf = *apathbuf = '\0';

  if(!astream)
    {
      outfile.Nullify();
      return;
    }

  //astream >> setw(READBUF_SIZE) >> flags >> setw(READBUF_SIZE) >> anamebuf >> setw(READBUF_SIZE) >> apathbuf;
  astream.get(flags, READBUF_SIZE, ' ');
  astream.ignore(READBUF_SIZE, ' ');
  astream.get(anamebuf, READBUF_SIZE, ' ');
  astream.ignore(READBUF_SIZE, ' ');
  astream.get(apathbuf, READBUF_SIZE);
  astream.ignore(READBUF_SIZE, '\n');

  if(flags[0] == '\0' || anamebuf[0] == '\0' || apathbuf[0] == '\0')
    {
      outfile.Nullify();
      return;
    }
  
  aoutfile = new WOKMake_OutputFile;

  len = strlen(flags);
  for(i=0; i<len; i++)
    {
      switch(flags[i])
	{
	case '+':
	  aoutfile->SetProduction();
	  break;
	case '-':
	  aoutfile->SetReference();
	  break;
	case 'V':
	  aoutfile->SetPhysicFlag(Standard_False);
	  break;
	case 'S':
	  aoutfile->SetStepID(Standard_True);
	  break;
	case 'M':
	  aoutfile->SetMember();
	  break;
	case 'E':
	  aoutfile->SetExtern();
	  break;
	}
    }

  oldpath  = new TCollection_HAsciiString(apathbuf);
  
  if(anamebuf[0] != '.')
    {
      Handle(TCollection_HAsciiString) id = new TCollection_HAsciiString(anamebuf);

      if( aoutfile->IsPhysic())
	{
	  afile = alocator->Locate(id);
	  
	  if(!afile.IsNull())
	    {
	      id = afile->LocatorName();
	    }
	}

      aoutfile->SetID(id);
      aoutfile->SetFile(afile);
      aoutfile->SetLastPath(new WOKUtils_Path(oldpath));
      aoutfile->SetLocateFlag(Standard_True);

      //if(!strcmp(id->Token(":", 2)->ToCString(), "msentity"))
      //{
      //	  aoutfile->SetPhysicFlag(Standard_False);
      //}
    }
  else
    {
      aoutfile->SetID(oldpath);
      aoutfile->SetLastPath(new WOKUtils_Path(oldpath));
      aoutfile->SetLocateFlag(Standard_False);
    }
	  
  outfile = aoutfile;
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : WriteLine
//purpose  : 
//=======================================================================
void WOKMake_OutputFile::WriteLine(Standard_OStream&                       astream,
				  const Handle(WOKMake_OutputFile)&        outfile)
{
  if(outfile.IsNull())
    {
      Standard_ProgramError::Raise("WOKMake_OutputFile::WriteLine : Null Output");
    }

  if(outfile->IsProduction())    astream << "+";
  else                           astream << "-";
  if(!outfile->IsPhysic())       astream << "V";
  if(outfile->IsStepID())        astream << "S";
  if(outfile->IsMember())        astream << "M";
  else                           astream << "E";

  astream << " ";

  if(outfile->IsLocateAble())
    {
      if(outfile->IsPhysic())
	{
	  astream << outfile->File()->LocatorName()->ToCString() << " " << outfile->LastPath()->Name()->ToCString() << endl;
	}
      else
	{
	  astream << outfile->ID()->ToCString() << " ." << endl;
	}
    }
  else
    {
      astream << ". " << outfile->LastPath()->Name()->ToCString() << endl;
    }
  return;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : ReadFile
//purpose  : 
//=======================================================================
Standard_Integer WOKMake_OutputFile::ReadFile(const Handle(WOKUtils_Path)&    apath, 
					      const Handle(WOKernel_Locator)& alocator,
					      WOKMake_IndexedDataMapOfHAsciiStringOfOutputFile& amap)
{
  if(apath->Exists())
    {
      Standard_Integer i = 0;
      ifstream astream(apath->Name()->ToCString());
      Handle(WOKMake_OutputFile) outfile;

      ReadLine(astream, alocator, outfile);

      while(!outfile.IsNull())
	{
	  i++;
	  amap.Add(outfile->ID(), outfile);
	  ReadLine(astream, alocator, outfile);
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
Standard_Integer WOKMake_OutputFile::WriteFile(const Handle(WOKUtils_Path)& apath, 
					      const WOKMake_IndexedDataMapOfHAsciiStringOfOutputFile& amap)
{
  Standard_Integer i;
  ofstream astream(apath->Name()->ToCString(), ios::out);
  
  if(!astream)
    {
      ErrorMsg() << "WOKMake_StepOutput::Dump" << "Could not open " << apath->Name() << endm;
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
Standard_Integer WOKMake_OutputFile::ReadFile(const Handle(WOKUtils_Path)& apath, 
					     const Handle(WOKernel_Locator)& alocator,
					     const Handle(WOKMake_HSequenceOfOutputFile)& aseq)
{
  if(apath.IsNull() || aseq.IsNull())
    {
      Standard_ProgramError::Raise("WOKMake_OutputFile::ReadFile : NullOutput");
    }

  if(apath->Exists())
    {
      Standard_Integer i = 0;
      ifstream astream(apath->Name()->ToCString(), ios::in);
      Handle(WOKMake_OutputFile) outfile;

      ReadLine(astream, alocator, outfile);
      while(!outfile.IsNull())
	{
	  i++;
	  aseq->Append(outfile);
	  ReadLine(astream, alocator, outfile);
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
Standard_Integer WOKMake_OutputFile::WriteFile(const Handle(WOKUtils_Path)& apath, 
					      const Handle(WOKMake_HSequenceOfOutputFile)& aseq)
{
  Standard_Integer i;
  ofstream astream(apath->Name()->ToCString(), ios::out);
  
  if(!astream)
    {
      ErrorMsg() << "WOKMake_StepOutput::Dump" << "Could not open " << apath->Name() << endm;
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
//function : SetReference
//purpose  : 
//=======================================================================
void WOKMake_OutputFile::SetReference()
{
   myattr &= (STEPFILE_REFPROD ^ myattr);
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetReference
//purpose  : 
//=======================================================================
void WOKMake_OutputFile::SetProduction()
{
  myattr |= STEPFILE_REFPROD;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetReference
//purpose  : 
//=======================================================================
void WOKMake_OutputFile::SetMember()
{
  myattr |= STEPFILE_MEMBEXT;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : SetReference
//purpose  : 
//=======================================================================
void WOKMake_OutputFile::SetExtern()
{
  myattr &= (STEPFILE_MEMBEXT ^ myattr);
}

