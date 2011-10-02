// File:	WOKOrbix_ServerSource.cxx
// Created:	Mon Aug 25 18:43:29 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_CDLFile.hxx>
#include <WOKBuilder_MSchema.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_MSTool.hxx>

#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_HSequenceOfInputFile.hxx>

#include <WOKOrbix_ServerSource.ixx>

#ifdef WNT
# include <WOKNT_WNT_BREAK.hxx>
#endif  // WNT

//=======================================================================
//function : WOKOrbix_ServerSource
//purpose  : 
//=======================================================================
WOKOrbix_ServerSource::WOKOrbix_ServerSource(const Handle(WOKMake_BuildProcess)& abp,
						   const Handle(WOKernel_DevUnit)& aunit, 
						   const Handle(TCollection_HAsciiString)& acode, 
						   const Standard_Boolean checked, 
						   const Standard_Boolean hidden)
: WOKStep_CDLUnitSource(abp,aunit, acode, checked, hidden)
{
}

//=======================================================================
//function : ReadUnitDescr
//purpose  : 
//=======================================================================
void WOKOrbix_ServerSource::ReadUnitDescr(const Handle(WOKMake_InputFile)& ServerCDL)
{
  Standard_Integer i;
  Handle(WOKBuilder_MSchema) ameta = WOKBuilder_MSTool::GetMSchema();
  Handle(TColStd_HSequenceOfHAsciiString) aseq;
  Handle(WOKernel_File) gefile, thefile, NULLFILE;

  WOKStep_CDLUnitSource::ReadUnitDescr(ServerCDL);

  switch(Status())
    {
    case WOKMake_Failed:
      return;
    default:
      break;
    }
  
  Handle(TCollection_HAsciiString) msentity = new TCollection_HAsciiString("msentity");
  
  aseq = ameta->ComponentParts(Unit()->Name());

  for(i=1; i<=aseq->Length(); i++)
    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
      Handle(TCollection_HAsciiString) msid   = WOKernel_File::FileLocatorName(Unit()->Name(), msentity, aseq->Value(i));
      Handle(WOKBuilder_Specification) cdlen  = new WOKBuilder_CDLFile(ServerCDL->File()->Path());
      Handle(WOKBuilder_MSEntity)      msent  = new WOKBuilder_MSEntity(cdlen, aseq->Value(i));
      
      Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(msid,
								  NULLFILE,
								  msent, msent->Path());
      outfile->SetLocateFlag(Standard_True);
      outfile->SetProduction();
      outfile->SetPhysicFlag(Standard_False);
      AddExecDepItem(ServerCDL, outfile, Standard_True);
    }
}



//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKOrbix_ServerSource::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist) 
{
  Handle(WOKernel_File) FILES = GetFILES();
  Handle(WOKernel_File) PKCDL = GetUnitDescr();

  if(execlist->Length())
    {
      Standard_Integer i;

      for(i=1; i<=execlist->Length(); i++)
	{
	  if(!strcmp(execlist->Value(i)->File()->Name()->ToCString(), FILES->Name()->ToCString()))
	    {
	      ReadFILES(execlist->Value(i));
	    }
	  if(!strcmp(execlist->Value(i)->File()->Name()->ToCString(), PKCDL->Name()->ToCString()))
	    {
	      ReadUnitDescr(execlist->Value(i));
	    }
	}
    }
  else
    {
      if(!FILES.IsNull())
	{
	  Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(FILES->LocatorName(), FILES, 
								   Handle(WOKBuilder_Entity)(), FILES->Path());
	  execlist->Append(infile);
	  infile->SetDirectFlag(Standard_True);
	  infile->SetLocateFlag(Standard_True);
	  
	  ReadFILES(infile);
	}

      if(CheckStatus("FILES reading")) return;
      if(!PKCDL.IsNull())
	{
	  Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(PKCDL->LocatorName(), PKCDL, 
								   Handle(WOKBuilder_Entity)(), PKCDL->Path());
	  execlist->Append(infile);
	  infile->SetDirectFlag(Standard_True);
	  infile->SetLocateFlag(Standard_True);
	  
	  ReadUnitDescr(infile);
	}
      if(CheckStatus("CDL processing")) return;

      
    }

  Handle(TCollection_HAsciiString)    astr;
  Handle(TCollection_HAsciiString)    asourcetype = new TCollection_HAsciiString("source");
  Handle(WOKernel_File)               afile;

  astr  = new TCollection_HAsciiString(Unit()->Name());
  astr->AssignCat(".cxx");  
  afile = Locator()->Locate(Unit()->Name(), asourcetype, astr);

  if(afile.IsNull())
    {
      WarningMsg() << "WOKOrbix_ServerSource::Execute"
		 << "Missing server main file " << astr << endm;
      afile = new WOKernel_File(astr, Unit(), Unit()->GetFileType(asourcetype));
      afile->GetPath();
    }

  Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(afile->LocatorName(), afile, 
							   Handle(WOKBuilder_Entity)() , afile->Path());
  execlist->Append(infile);
  infile->SetDirectFlag(Standard_True);
  infile->SetLocateFlag(Standard_True);
  
  
  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(afile->LocatorName(), afile, 
							      Handle(WOKBuilder_Entity)()  , afile->Path());
  
  outfile->SetLocateFlag(Standard_True);
  outfile->SetProduction();
  AddExecDepItem(infile, outfile, Standard_True);
  
  SetSucceeded();

  return;
}

