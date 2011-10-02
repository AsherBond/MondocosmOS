// File:	WOKOrbix_IDLFill.cxx
// Created:	Mon Aug 25 11:35:24 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Extension.hxx>
#include <WOKUtils_Path.hxx>

#include <WOKernel_File.hxx>

#include <WOKBuilder_HSequenceOfEntity.hxx>
#include <WOKBuilder_MSEntity.hxx>

#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>

#include <WOKOrbix_IDLFile.hxx>
#include <WOKOrbix_IDLTranslator.hxx>

#include <WOKOrbix_IDLFill.ixx>

//=======================================================================
//function : WOKOrbix_IDLFill
//purpose  : 
//=======================================================================
WOKOrbix_IDLFill::WOKOrbix_IDLFill(const Handle(WOKMake_BuildProcess)& abp,
				   const Handle(WOKernel_DevUnit)& aunit,
				   const Handle(TCollection_HAsciiString)& acode,
				   const Standard_Boolean checked,const Standard_Boolean hidden)
  : WOKMake_Step(abp, aunit, acode, checked, hidden)
{
}

//=======================================================================
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOrbix_IDLFill::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;
}

//=======================================================================
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKOrbix_IDLFill::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)TMPDIR);
  return result;
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKOrbix_IDLFill::HandleInputFile(const Handle(WOKMake_InputFile)& infile) 
{
  if(infile->File()->Path()->Extension() == WOKUtils_IDLFile)
    {
      infile->SetBuilderEntity(new WOKOrbix_IDLFile(infile->File()->Path()));
      infile->SetDirectFlag(Standard_True);
      infile->SetLocateFlag(Standard_True);
      return Standard_True;
    }
  return Standard_False;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKOrbix_IDLFill::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist) 
{

  Handle(WOKOrbix_IDLTranslator) atrans = new WOKOrbix_IDLTranslator(new TCollection_HAsciiString("IDLFRONT"), Unit()->Params());

  atrans->Load();
  atrans->SetMSchema(WOKBuilder_MSTool::GetMSchema());
  
  Handle(TCollection_HAsciiString) declname = new TCollection_HAsciiString(Unit()->Name());

  declname->AssignCat(".IdlDecl");

  Handle(WOKernel_File) declfile = new WOKernel_File(declname, Unit(), Unit()->GetFileType(AdmFileType()));
  
  declfile->GetPath();

  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(declfile->LocatorName(), declfile, 
							      Handle(WOKBuilder_Entity)(), declfile->Path());

  outfile->SetLocateFlag(Standard_True);
  outfile->SetProduction();

  ofstream stream(declfile->Path()->Name()->ToCString());

  for(Standard_Integer i=1; i<=execlist->Length(); i++)
    {
      Handle(WOKMake_InputFile) infile = execlist->Value(i);
      Handle(WOKOrbix_IDLFile) idlfile = Handle(WOKOrbix_IDLFile)::DownCast(infile->BuilderEntity());


      if(idlfile.IsNull())
	{

	  ErrorMsg() << "WOKOrbix_IDLFill::Execute" 
		   << "Invalid input : " << infile->BuilderEntity()->Path()->Name() << endm;
	  SetFailed();
	  return;
	}

      if(infile->File()->Nesting()->IsSameString(Unit()->FullName()))
	{
	  InfoMsg() << "WOKOrbix_IDLFill::Execute" << "-------> " << infile->File()->Name() << endm;
	}
      else
	{
	  InfoMsg() << "WOKOrbix_IDLFill::Execute" << "-------> " << infile->File()->UserPathName() << endm;
	}
      
      switch(atrans->Execute(idlfile))
	{
	case WOKBuilder_Success:
	  {

	    Handle(WOKBuilder_HSequenceOfEntity) production = atrans->Produces();

	    for(Standard_Integer i=1; i<=production->Length(); i++)
	      {
		Handle(WOKBuilder_MSEntity) anent = *((Handle(WOKBuilder_MSEntity) *) & production->Value(i));

		stream << anent->Name()->ToCString() << " " << infile->File()->Path()->BaseName()->ToCString() << endl;
	      }
	    AddExecDepItem(infile, outfile, Standard_True);

	  }

	  break;
	default:
	  SetFailed();
	  stream.close();
	  return;
	}
    }
  stream.close();
  SetSucceeded();
  return;
}

