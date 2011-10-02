

#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>
#include <WOKUtils_Param.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>

#include <WOKMake_DepItem.hxx>
#include <WOKMake_OutputFile.hxx>
#include <WOKMake_InputFile.hxx>


#include <WOKOrbix_IDLFile.hxx>

#include <WOKOrbix_IDLSource.ixx>

//=======================================================================
//function : WOKOrbix_IDLSource
//purpose  : 
//=======================================================================
WOKOrbix_IDLSource::WOKOrbix_IDLSource(const Handle(WOKMake_BuildProcess)& abp,
				       const Handle(WOKernel_DevUnit)& aunit,const Handle(TCollection_HAsciiString)& acode,
				       const Standard_Boolean checked,const Standard_Boolean hidden)
  : WOKStep_Source(abp,aunit,acode,checked,hidden)
{
}

//=======================================================================
//function : GetUnitDescr
//purpose  : 
//=======================================================================
Handle(WOKernel_File) WOKOrbix_IDLSource::GetUnitDescr() const
{
  Handle(TCollection_HAsciiString)    astr;
  Handle(TCollection_HAsciiString)    asourcetype = new TCollection_HAsciiString("source");
  Handle(WOKernel_File)               afile;

  astr  = new TCollection_HAsciiString(Unit()->Name());
  astr->AssignCat(".idl");  
  afile = Locator()->Locate(Unit()->Name(), asourcetype, astr);
  return afile; 
}

//=======================================================================
//function : Execute
//purpose  : 
//=======================================================================
void WOKOrbix_IDLSource::Execute(const Handle(WOKMake_HSequenceOfInputFile)& execlist) 
{
  Handle(WOKernel_File) FILES = GetFILES();
  Handle(WOKernel_File) PKIDL = GetUnitDescr();

  if(execlist->Length())
    {
      Standard_Integer i;

      for(i=1; i<=execlist->Length(); i++)
	{
	  if(!strcmp(execlist->Value(i)->File()->Name()->ToCString(), FILES->Name()->ToCString()))
	    {
	      ReadFILES(execlist->Value(i));
	    }
	  if(!strcmp(execlist->Value(i)->File()->Name()->ToCString(), PKIDL->Name()->ToCString()))
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
	  execlist->Append(infile);
	  infile->SetDirectFlag(Standard_True);
	  infile->SetLocateFlag(Standard_True);
	  
	  ReadFILES(infile);
	}

      if(CheckStatus("FILES reading")) return;
      if(!PKIDL.IsNull())
	{
	  Handle(WOKBuilder_Specification) cdlent = new WOKOrbix_IDLFile(PKIDL->Path());
	  Handle(WOKMake_InputFile) infile = new WOKMake_InputFile(PKIDL->LocatorName(), PKIDL, 
								   cdlent , PKIDL->Path());
	  execlist->Append(infile);
	  infile->SetDirectFlag(Standard_True);
	  infile->SetLocateFlag(Standard_True);

	  
	  Handle(WOKMake_OutputFile) outfile = new WOKMake_OutputFile(PKIDL->LocatorName(), PKIDL, 
								      cdlent , PKIDL->Path());

	  outfile->SetLocateFlag(Standard_True);
	  outfile->SetProduction();
	  AddExecDepItem(infile, outfile, Standard_True);
	}
      if(CheckStatus("IDL processing")) return;
    }

  SetSucceeded();

  return;
}

