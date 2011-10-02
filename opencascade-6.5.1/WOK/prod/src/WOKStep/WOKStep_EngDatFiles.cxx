// File:	WOKStep_EngDatFiles.cxx
// Created:	Mon Jul 29 17:26:17 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Extension.hxx>
#include <WOKUtils_Path.hxx>
#include <WOKUtils_Shell.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>

#include <WOKBuilder_Miscellaneous.hxx>
#include <WOKBuilder_MSEntity.hxx>
#include <WOKBuilder_Command.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_HSequenceOfInputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_EngDatFiles.ixx>


//=======================================================================
//function : WOKStep_EngDatFiles
//purpose  : 
//=======================================================================
 WOKStep_EngDatFiles::WOKStep_EngDatFiles(const Handle(WOKMake_BuildProcess)& abp,
					  const Handle(WOKernel_DevUnit)& aunit, 
					  const Handle(TCollection_HAsciiString)& acode, 
					  const Standard_Boolean checked, 
					  const Standard_Boolean hidden)
: WOKMake_Step(abp, aunit,  acode, checked, hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_EngDatFiles::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result; 
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_EngDatFiles::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result; 
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_EngDatFiles::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;

  if(!infile->IsPhysic())
    {
      if(!strcmp("msentity", infile->ID()->Token(":",2)->ToCString()))
	{
	  infile->SetDirectFlag(Standard_True);
	  infile->SetBuilderEntity(new WOKBuilder_MSEntity(infile->ID()->Token(":",3)));
	  return Standard_True;
	}
    }
  else
    {
      apath = infile->File()->Path();
      switch(apath->Extension())
	{
	case WOKUtils_DATFile:  result = new WOKBuilder_Miscellaneous(apath);     break;
	default:  
	  return Standard_False;
	}
      
      infile->SetBuilderEntity(result);
      infile->SetDirectFlag(Standard_True);
      return Standard_True;
    }
  return Standard_False;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_EngDatFiles::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(TCollection_HAsciiString) astr;
  static Handle(TCollection_HAsciiString) derivtype  = new TCollection_HAsciiString("derivated");
  static Handle(TCollection_HAsciiString) engdattype = new TCollection_HAsciiString("engdatfile");
  Standard_Integer i;

  Handle(WOKBuilder_Command) acmd = new WOKBuilder_Command(new TCollection_HAsciiString("COMMAND"), 
							   Unit()->Params());
  acmd->SetShell(Shell());
  
  if(!Shell()->IsLaunched()) Shell()->Launch();

  for(i=1; i<=execlist->Length(); i++)
    {
      Handle(WOKBuilder_Miscellaneous) adatfile;
      Handle(WOKMake_InputFile)        infile = execlist->Value(i), indatfile;
      Handle(WOKBuilder_Entity)        anent  = infile->BuilderEntity();

      if(anent->IsKind(STANDARD_TYPE(WOKBuilder_MSEntity)))
	{
	  Handle(WOKBuilder_MSEntity) amsent = Handle(WOKBuilder_MSEntity)::DownCast(anent);
	  Handle(WOKernel_File)       afile;
	  Handle(WOKernel_DevUnit)    aunit = Locator()->LocateDevUnit(amsent->Name());
	  
	  if(!aunit.IsNull())
	    {
	      if(WOKernel_IsInterface(aunit))
		{
		  astr = new TCollection_HAsciiString(aunit->Name());
		  astr->AssignCat("_ExportedMethods.dat");

		  afile = Locator()->Locate(aunit->Name(), derivtype, astr);
		  
		  if(afile.IsNull())
		    {
		      ErrorMsg() << "WOKStep_EngDatFiles::Execute"
			       << "Unable to locate file : " << astr << " in " << aunit->Name() << endm;
		      SetFailed();
		    }
		  else
		    {
		      adatfile  = new WOKBuilder_Miscellaneous(afile->Path());
		      indatfile = new WOKMake_InputFile(afile->LocatorName(), 
							afile, adatfile, afile->Path());
		      indatfile->SetDirectFlag(Standard_False);
		      indatfile->SetLocateFlag(Standard_True);
		      indatfile->SetPhysicFlag(Standard_True);
		    }
		}
	    }
	  else
	    {
	      ErrorMsg() << "WOKStep_EngDatFiles::Execute"
		       << "Could not locate interface : " << amsent->Name() << endm;
	      SetFailed();
	    }
	}
      else
	{
	  if(anent->IsKind(STANDARD_TYPE(WOKBuilder_Miscellaneous)) && anent->Path()->Extension() == WOKUtils_DATFile)
	    {
	      adatfile = Handle(WOKBuilder_Miscellaneous)::DownCast(anent);
	    }
	}

      if(!adatfile.IsNull())
	{
	  Handle(WOKernel_File)       afile;
	  
	  astr = new TCollection_HAsciiString(Unit()->Name());
	  astr->AssignCat("-");
	  astr->AssignCat(adatfile->Path()->FileName());

	  afile = new WOKernel_File(astr, Unit(), Unit()->GetFileType(engdattype));
	  afile->GetPath();

	  InfoMsg() << "WOKStep_EngDatFiles::Execute" 
		  << "Copy of : " << adatfile->Path()->Name() << " to " << afile->Path()->Name() << endm;

	  switch(acmd->Copy(adatfile->Path(), afile->Path()))
	    {
	    case WOKBuilder_Success:
	      {
		Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(afile->LocatorName(), afile, 
									    new WOKBuilder_Miscellaneous(afile->Path()), 
									    afile->Path());
		outfile->SetLocateFlag(Standard_True);
		outfile->SetProduction();
		AddExecDepItem(infile, outfile, Standard_True);

		if(!indatfile.IsNull())
		  {
		    AddExecDepItem(indatfile, outfile, Standard_True);
		  }
	      }
	    break;
	    case WOKBuilder_Failed:
	    case WOKBuilder_Unbuilt:
	      ErrorMsg() << "WOKStep_EngDatFiles::Execute" 
		       << "Copy Failed" << endm;
	      SetFailed();
	      break;
	    }
	}
    }
  if(Status() != WOKMake_Failed)
    {
      SetSucceeded();
    }
  return;
}
