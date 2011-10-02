// File:	WOKStep_ImplementationDep.cxx
// Created:	Thu Oct 26 18:36:01 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>
#include <Standard_Stream.hxx>

#include <TCollection_HAsciiString.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <OSD_Protection.hxx>

#include <WOKTools_MapOfHAsciiString.hxx>
#include <WOKTools_MapIteratorOfMapOfHAsciiString.hxx>
#include <WOKTools_Messages.hxx>

#ifndef WNT
# include <WOKUnix_FDescr.hxx>
#endif  // WNT

#include <WOKUtils_Path.hxx>
#include <WOKUtils_AdmFile.hxx>

#include <WOKernel_HSequenceOfFile.hxx>
#include <WOKernel_Workbench.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_Session.hxx>

#include <WOKBuilder_MFile.hxx>
#include <WOKBuilder_Command.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_AdmFileTypes.hxx>

#include <WOKStep_ImplementationDep.ixx>

#ifdef WNT
# include <WOKNT_WNT_BREAK.hxx>
#endif  // WNT

#define READBUF_SIZE 1024

//=======================================================================
//function : WOKStep_ImplementationDep
//purpose  : 
//=======================================================================
 WOKStep_ImplementationDep::WOKStep_ImplementationDep(const Handle(WOKMake_BuildProcess)& abp,
						      const Handle(WOKernel_DevUnit)& aunit, 
						      const Handle(TCollection_HAsciiString)& acode, 
						      const Standard_Boolean checked, 
						      const Standard_Boolean hidden) 
: WOKMake_Step(abp,aunit, acode, checked, hidden)
{
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_ImplementationDep::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STADMFILE);
  return result; 
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_ImplementationDep::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)STTMPDIR);
  return result; 
}

//=======================================================================
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_ImplementationDep::HandleInputFile(const Handle(WOKMake_InputFile)& infile)
{
  Handle(WOKBuilder_Entity) result;
  Handle(WOKUtils_Path)     apath;
  Handle(TCollection_HAsciiString) INTERNLIB = Unit()->Params().Eval("%FILENAME_INTERNLIB"); 
  
  if(!infile->File().IsNull())
    {
      if(!strcmp(infile->File()->Path()->ExtensionName()->ToCString(), ".In"))
	{
	  infile->SetDirectFlag(Standard_True);
	  return Standard_True;
	}
      else if(!strcmp(infile->File()->TypeName()->ToCString(), "source") && 
	      !strcmp(infile->File()->Name()->ToCString(), INTERNLIB->ToCString())) 
	{
	  infile->SetDirectFlag(Standard_True);
	  return Standard_True;
	}
    } 
  return Standard_False;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_ImplementationDep::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(TCollection_HAsciiString) INTERNLIB = Unit()->Params().Eval("%FILENAME_INTERNLIB"); 
  WOKTools_MapOfHAsciiString inresult;
  Handle(TColStd_HSequenceOfHAsciiString) internresult;
  Handle(WOKMake_InputFile) InFile, InternFile;
  Handle(WOKMake_HSequenceOfInputFile) InFiles = new WOKMake_HSequenceOfInputFile;

  Standard_Integer i;
  for(i=1; i<=execlist->Length(); i++)
    {
      const Handle(WOKMake_InputFile)& infile = execlist->Value(i);
      if(!strcmp(infile->File()->Path()->ExtensionName()->ToCString(), ".In"))
	{
	  InFiles->Append(infile);
	}
      else if(!strcmp(infile->File()->TypeName()->ToCString(), "source") && 
	      !strcmp(infile->File()->Name()->ToCString(), INTERNLIB->ToCString())) 
	{
	  InternFile = infile;
	}
    }
  

  if (InternFile.IsNull()) {
    // Recherche d'un eventuel INTERNLIB
    Handle(WOKernel_File) internlib = Locator()->Locate(Unit()->Name(), new TCollection_HAsciiString("source"), INTERNLIB);
      
      if(!internlib.IsNull())
	{
	  if(!myinflow.Contains(internlib->LocatorName()))
	    {
	      WarningMsg() << "WOKStep_ImplementationDep::Execute" 
			 << "Ignoring unlisted (in FILES) located " << INTERNLIB << " file " << endm;
	    }
	  else
	    {
	      InfoMsg() << "WOKStep_ImplementationDep::Execute" 
		      << "Using " << INTERNLIB << " file for implementation dependance" << endm;
	      
	      InternFile = myinflow.FindFromKey(internlib->LocatorName());
	    }
	}
      else
	{
	  Handle(TCollection_HAsciiString) internlocname = 
	    WOKernel_File::FileLocatorName(Unit()->Name(),
					   new TCollection_HAsciiString("source"),
					   INTERNLIB);
	  
	  if(myinflow.Contains(internlocname))
	    {
	      ErrorMsg() << "WOKStep_ImplementationDep::Execute" 
		<< "Could not locate listed (in FILES) " << INTERNLIB
		  << " in unit : " << Unit()->Name() << endm;
	      SetFailed();
	      return;
	    }
	}
	
    }

    
  // le ImplDep

  Handle(TCollection_HAsciiString) aname  =  new TCollection_HAsciiString(Unit()->Name());
  if(!SubCode().IsNull())
    {
      aname->AssignCat("_");
      aname->AssignCat(SubCode());
    }
  aname->AssignCat(".");
  aname->AssignCat(Unit()->Params().Eval("%FILENAME_IMPLDEP"));


  Handle(WOKernel_File)      idep    = new WOKernel_File(aname, Unit(), Unit()->FileTypeBase()->Type("stadmfile"));

  idep->GetPath();

  Handle(WOKMake_OutputFile) outidep = new WOKMake_OutputFile(idep->LocatorName(), idep, 
							      Handle(WOKBuilder_Entity)(), idep->Path());
  outidep->SetProduction();
  outidep->SetLocateFlag(Standard_True);



  if(InternFile.IsNull() && (InFiles->Length()>0))
    { 
      Standard_Integer i;
      for( i=1; i<=InFiles->Length(); i++)
	{
	  const Handle(WOKMake_InputFile)& InFile = InFiles->Value(i);

	  WOKMake_IndexedDataMapOfHAsciiStringOfInputFile inmap;
	  
	  WOKMake_InputFile::ReadFile(InFile->File()->Path(), InLocator(), inmap);
	  
          Standard_Integer j;
	  for(j=1; j<=inmap.Extent(); j++)
	    {
	      const Handle(WOKMake_InputFile)& depfile = inmap(j);
	  
	      if(depfile->IsLocateAble() && depfile->IsPhysic() && !depfile->IsStepID())
		{
		  const Handle(WOKernel_File)& file = depfile->File();		  
		  const Handle(TCollection_HAsciiString)& uname = Unit()->Session()->GetEntity(file->Nesting())->Name();
		  Standard_Boolean contains = inresult.Contains(uname);
		  if(!contains) { 
		    inresult.Add(uname);
		  }

		}
	    }
	  AddExecDepItem(InFile, outidep, Standard_True);
	}

      WOKTools_MapIteratorOfMapOfHAsciiString anit;
      anit.Initialize(inresult);
      
      ofstream stream(idep->Path()->Name()->ToCString());

      while(anit.More())
	{

	  stream << anit.Key()->ToCString() << endl;
	  anit.Next();
	}
      stream.close();
      
    }
  else if(!InternFile.IsNull())
    {
      WOKUtils_AdmFile afile(InternFile->File()->Path());

      internresult = afile.Read();

      if(!internresult.IsNull())
	{
	  ofstream stream(idep->Path()->Name()->ToCString());
	  Standard_Integer i;
	  for(i=1; i<=internresult->Length(); i++)
	    {
	      stream << internresult->Value(i)->ToCString() << endl;
	    }
	  stream.close();
	}

      AddExecDepItem(InternFile, outidep, Standard_True);
    }
  else
    {
      ErrorMsg() << "WOKStep_ImplementationDep::Execute"
	       << "Could not find any input to get Implementation dependencies" << endm;
      SetFailed();
      return;
    }
  SetSucceeded();
  return;
}



