// File:	WOKStep_Source.cxx
// Created:	Tue Aug 29 21:41:43 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKStep_Source.ixx>

#include <WOKernel_FileType.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKUtils_AdmFile.hxx>
#include <WOKUtils_Param.hxx>
#include <WOKTools_Messages.hxx>

#include <WOKMake_AdmFileTypes.hxx>
#include <WOKMake_OutputFile.hxx>

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <TCollection_HAsciiString.hxx>

#ifdef WNT
# include <WOKNT_WNT_BREAK.hxx>
#endif  // WNT

//=======================================================================
//function : WOKStep_Source
//purpose  : 
//=======================================================================
WOKStep_Source::WOKStep_Source(const Handle(WOKMake_BuildProcess)& abp,
			       const Handle(WOKernel_DevUnit)& aunit, 
			       const Handle(TCollection_HAsciiString)& acode,
			       const Standard_Boolean checked,
			       const Standard_Boolean hidden ) 
  : WOKMake_Step(abp,aunit,acode,checked,hidden)
{
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : OutputDirTypeName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Source::OutputDirTypeName() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)TMPDIR);
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : AdmFileType
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKStep_Source::AdmFileType() const
{
  static Handle(TCollection_HAsciiString) result = new TCollection_HAsciiString((char*)ADMFILE);
  return result;
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : HandleInputFile
//purpose  : 
//=======================================================================
Standard_Boolean WOKStep_Source::HandleInputFile(const Handle(WOKMake_InputFile)& afile)
{
  Handle(WOKernel_File) file = afile->File();

  if(file.IsNull()) return Standard_False;

  if(!strcmp(file->TypeName()->ToCString(), "source"))
    {
      return Standard_True;
    }
  return Standard_False;
}

//=======================================================================
//function : GetFILES
//purpose  : 
//=======================================================================
Handle(WOKernel_File) WOKStep_Source::GetFILES() const
{
  Handle(TCollection_HAsciiString) astr = Unit()->Params().Eval("%FILENAME_FILES");
  Handle(TCollection_HAsciiString) asourcetype = new TCollection_HAsciiString("source");
  Handle(WOKernel_File) afile = Locator()->Locate(Unit()->Name(), asourcetype, astr);
  return afile;
}


//=======================================================================
//function : ReadFILES
//purpose  : 
//=======================================================================
void WOKStep_Source::ReadFILES(const Handle(WOKMake_InputFile)& FILES)
{
  Handle(WOKernel_File) afile;
  Handle(TCollection_HAsciiString) sourcetype = new TCollection_HAsciiString("source");
  Standard_Integer i;

  // le fichier FILES
  WOKUtils_Param params = Unit()->Params();

  if(FILES.IsNull() == Standard_False)
    {
      // le fichier FILES produit FILES
      Handle(WOKMake_OutputFile) OUTFILES = new WOKMake_OutputFile(FILES->File()->LocatorName(), FILES->File(), 
								   Handle(WOKBuilder_Entity)(), FILES->File()->Path());
      
      OUTFILES->SetProduction();
      OUTFILES->SetLocateFlag(Standard_True);
      AddExecDepItem(FILES, OUTFILES, Standard_True);

      // un fichier FILES existe : le lire
      WOKUtils_AdmFile afiles(FILES->File()->Path());
      Handle(TColStd_HSequenceOfHAsciiString) aasciiseq;
      Handle(TCollection_HAsciiString) astr;

      aasciiseq = afiles.Read();

      if(!aasciiseq.IsNull())
	{
	  for(i=1; i<=aasciiseq->Length(); i++)
	    {
#ifdef WNT
_TEST_BREAK();
#endif  // WNT
	      astr = aasciiseq->Value(i);
	  
	      astr->LeftAdjust();
	      astr->RightAdjust();

              if (  astr -> Search ( ":" ) != -1  ) {

               afile = Locator () -> Locate ( astr );

              } else afile = Locator()->Locate(Unit()->Name(), sourcetype, astr);
	      
	      if(afile.IsNull() == Standard_True)
		{
		  ErrorMsg() << "WOKStep_Source::ReadFILES" 
			   << "File " << astr->ToCString() << " could not be found" << endm;
		  SetFailed();
		  return;
		}
	      else
		{
		  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(afile->LocatorName(), afile, 
									      Handle(WOKBuilder_Entity)(), afile->Path());
		  outfile->SetProduction();
		  outfile->SetLocateFlag(Standard_True);
		  AddExecDepItem(FILES, outfile, Standard_True);
		}
	    }
	}
    }
  return;
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKStep_Source::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Handle(WOKernel_File) FILES = GetFILES();
  if(execlist->Length())
    {
      Standard_Integer i;

      for(i=1; i<=execlist->Length(); i++)
	{
	  if(!strcmp(execlist->Value(i)->File()->Name()->ToCString(), FILES->Name()->ToCString()))
	    {
	      ReadFILES(execlist->Value(i));
	    }
	}
    }
  else
    {
      if(!FILES.IsNull())
	{
	  Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(FILES->LocatorName(), FILES, 
								   Handle(WOKBuilder_Entity)(), FILES->Path());
	  infile->SetLocateFlag(Standard_True);
	  infile->SetDirectFlag(Standard_True);
	  execlist->Append(infile);
	  ReadFILES(infile);
	}
    }

  if (Status() != WOKMake_Failed)
    SetSucceeded();

  return;
}
