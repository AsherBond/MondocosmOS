// File:	WOKStep_ResourceSource.cxx
// Created:	Thu Sep 26 16:05:39 1996
// Author:	Jean GAUTIER
//		<jga@cobrax.paris1.matra-dtv.fr>

#include <WOKStep_ResourceSource.ixx>


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

//=======================================================================
//function : WOKStep_Source
//purpose  : 
//=======================================================================
WOKStep_ResourceSource::WOKStep_ResourceSource(const Handle(WOKMake_BuildProcess)& abp,
					       const Handle(WOKernel_DevUnit)& aunit, 
					       const Handle(TCollection_HAsciiString)& acode,
					       const Standard_Boolean checked,
					       const Standard_Boolean hidden ) 
  : WOKStep_Source(abp,aunit,acode,checked,hidden)
{

}

//=======================================================================
//function : ReadFILES
//purpose  : 
//=======================================================================
void WOKStep_ResourceSource::ReadFILES(const Handle(WOKMake_InputFile)& FILES)
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
	      astr = aasciiseq->Value(i);
	  
	      astr->LeftAdjust();
	      astr->RightAdjust();
	      
	      Standard_Integer first = astr->Search(":::");

	      if (first <= 1)
		{
		  Handle(TCollection_HAsciiString) filenameFILES = Unit()->Params().Eval("%FILENAME_FILES");
		  if (strcmp(filenameFILES->ToCString(),astr->ToCString()))
		    {
		      ErrorMsg() << "WOKStep_ResourceSource::ReadFILES" 
		               << "No type specified for file " << astr << endm;
		      SetFailed();
		    }
		}
	      else
		{
		  Handle(TCollection_HAsciiString) type = astr->SubString(1,first-1);
		  Handle(TCollection_HAsciiString) name = astr->SubString(first+3, astr->Length());

		  Handle(WOKernel_FileType) theType = Unit()->GetFileType(type);

		  if(!theType.IsNull())
		    {
		      if(theType->IsStationDependent() || theType->IsDBMSDependent())
			{
			  WarningMsg() << "WOKStep_ResourceSource::ReadFILES" 
			             << "Station or DBMS Dependent type " <<  type << " : ignoring file " << name << endm;
			}
		      else
			{
			  afile = Locator()->Locate(Unit()->Name(), sourcetype, name);
		      
			  if(afile.IsNull() == Standard_True)
			    {
			      ErrorMsg() << "WOKStep_ResourceSource::ReadFILES" 
				       << "File " << astr->ToCString() << " could not be found" << endm;
			      SetFailed();
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
		  else
		    {
		      ErrorMsg() << "WOKStep_ResourceSource::ReadFILES" 
			       << "Type unknown : " << type << " for file : " << name << endm;
		      SetFailed();
		    }
		}
	    }
	}
    }
  return;
}
