// ADN
//    
// 11/1995
//

#include <CPPIntExt.hxx>
#include <CPPIntExt_Predefined.hxx>
#include <Standard_NoSuchObject.hxx>
#include <TColStd_HSequenceOfHAsciiString.hxx>
#include <TColStd_SequenceOfInteger.hxx>
#include <TCollection_HAsciiString.hxx>
#include <WOKTools_Messages.hxx>


// Declarations Incompletes


extern "C" {

        Handle(TColStd_HSequenceOfHAsciiString) Standard_EXPORT CPPInt_TemplatesUsed();

        void Standard_EXPORT CPPInt_Extract(const Handle(MS_MetaSchema)& ams,
					    const Handle(TCollection_HAsciiString)& aname,
					    const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
					    const Handle(TCollection_HAsciiString)& outdir,
					    const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
					    const Standard_CString);
	
        Handle(TColStd_HSequenceOfHAsciiString) Standard_EXPORT CPPEng_TemplatesUsed();

        void Standard_EXPORT CPPEng_Extract(const Handle(MS_MetaSchema)& ams,
					    const Handle(TCollection_HAsciiString)& aname,
					    const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
					    const Handle(TCollection_HAsciiString)& outdir,
					    const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
					    const Standard_CString);
	
}

// CPPInt_TemplatesUsed

Handle(TColStd_HSequenceOfHAsciiString) CPPInt_TemplatesUsed()
{
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;

  result->Append(new TCollection_HAsciiString("Interface_Template.edl"));

  return result;
}


Handle(TColStd_HSequenceOfHAsciiString) CPPEng_TemplatesUsed()
{
  Handle(TColStd_HSequenceOfHAsciiString) result = new TColStd_HSequenceOfHAsciiString;

  result->Append(new TCollection_HAsciiString("Engine_Template.edl"));

  return result;
}


void CPPInt_InterfExtract(const Handle(MS_MetaSchema)& aMeta,
			  const Handle(TCollection_HAsciiString)& aName,
			  const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
			  const Handle(TCollection_HAsciiString)& outdir,
			  const Handle(TColStd_HSequenceOfHAsciiString)& outfile)
{
  Handle(EDL_API)     api = new EDL_API;
  
  for(Standard_Integer i = 1; i <= edlsfullpath->Length(); i++) {
    api->AddIncludeDirectory(edlsfullpath->Value(i)->ToCString());
  }

  if (api->Execute("Interface_Template.edl") != EDL_NORMAL) {
    ErrorMsg() << "CPPInt_InterfExtract" << "unable to load Interface_Template.edl" << endm;
    Standard_NoSuchObject::Raise();
  } 
  Handle(TCollection_HAsciiString) FileNamexx = new TCollection_HAsciiString(outdir);
  FileNamexx->AssignCat(aName);
  FileNamexx->AssignCat(".cxx");
  outfile->Append(FileNamexx);
  
  Handle(TCollection_HAsciiString) FileNamedat = new TCollection_HAsciiString(outdir);
  FileNamedat->AssignCat(aName);
  FileNamedat->AssignCat("_ExportedMethods.dat");
  outfile->Append(FileNamedat);
  
  if (api->OpenFile("Interfilecxx",FileNamexx->ToCString()) == EDL_NORMAL) {
    
    if (api->OpenFile("Interfiledat",FileNamedat->ToCString()) == EDL_NORMAL) {
      
      Handle(MS_Interface) srcInterface = aMeta->GetInterface(aName); 
      
      CPPIntExt_ProcessHeader(srcInterface,api);

      

      // Maps for includes
      MS_MapOfType incmaptype;
      MS_MapOfType mapusedtype;
      MS_MapOfGlobalEntity incmappack;
      // Map for Methods
      MS_MapOfMethod expmap;
      
      
      CPPIntExt_LoadMethods(aMeta,srcInterface,api,expmap,incmaptype,mapusedtype,incmappack);
      CPPIntExt_ProcessIncludes(srcInterface,api,incmaptype,incmappack);
      
      CPPIntExt_ProcessTypes(aMeta,srcInterface,api,mapusedtype);

      if (expmap.Extent() < CPPINTEXT_MAXINTFSWITCH) {
	CPPIntExt_ProcessExec(srcInterface,api,mapusedtype);
	
	CPPIntExt_ProcessCases(aMeta,srcInterface,api,expmap);
	
	CPPIntExt_ProcessBottom(srcInterface,api);
      }
      else {
	
	TColStd_SequenceOfInteger switchcount;

	CPPIntExt_ProcessMultiCases(aMeta,srcInterface,api,expmap,switchcount);
	
	CPPIntExt_ProcessMultiExec(srcInterface,api,switchcount,mapusedtype);
      }
      
      api->CloseFile("Interfiledat");
    }
    else {
      ErrorMsg()  << "CPPInt_InterfExtract" << "Cannot open file " << FileNamedat->ToCString() << endm;
      Standard_NoSuchObject::Raise();
    }
    api->CloseFile("Interfilecxx");
  }
  else {
    ErrorMsg() << "CPPInt_InterfExtract" << "Cannot open file " << FileNamexx->ToCString() << endm;
    Standard_NoSuchObject::Raise();
  }
}

void CPPInt_EnginExtract(const Handle(MS_MetaSchema)& aMeta,
			 const Handle(TCollection_HAsciiString)& aName,
			 const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
			 const Handle(TCollection_HAsciiString)& outdir,
			 const Handle(TColStd_HSequenceOfHAsciiString)& outfile)
{
  Handle(MS_Engine) srcEngine = aMeta->GetEngine(aName); 
  Handle(EDL_API)     api = new EDL_API;

  for(Standard_Integer i = 1; i <= edlsfullpath->Length(); i++) {
    api->AddIncludeDirectory(edlsfullpath->Value(i)->ToCString());
  }

  if (api->Execute("Engine_Template.edl") != EDL_NORMAL) {
    ErrorMsg()  << "CPPInt_EnginExtract" << "unable to load Engine_Template.edl" << endm;
    Standard_NoSuchObject::Raise();
  } 
  Handle(TCollection_HAsciiString) FileNamexx = new TCollection_HAsciiString(outdir);
  FileNamexx->AssignCat(aName);
  FileNamexx->AssignCat(".cxx");
  outfile->Append(FileNamexx);
  
  Handle(TCollection_HAsciiString) FileNamell = new TCollection_HAsciiString(outdir);
  FileNamell->AssignCat(aName);
  FileNamell->AssignCat(".ll");
  outfile->Append(FileNamell);
  
  Handle(TCollection_HAsciiString) FileNameInit = new TCollection_HAsciiString(outdir);
  FileNameInit->AssignCat("Engine_Init_.cxx");
//  outfile->Append(FileNameInit);
  
  if (api->OpenFile("Enginefilecxx",FileNamexx->ToCString()) == EDL_NORMAL) {
    
    if (api->OpenFile("Enginefilell",FileNamell->ToCString()) == EDL_NORMAL) {
      
      if (api->OpenFile("Enginefileinit",FileNameInit->ToCString()) == EDL_NORMAL) {
      
	Handle(TColStd_HSequenceOfHAsciiString) seqint = new TColStd_HSequenceOfHAsciiString();
	Handle(TColStd_HSequenceOfHAsciiString) refseqint = srcEngine->Interfaces();
	
	Handle(TCollection_HAsciiString) inteng = new TCollection_HAsciiString("EngineInterface");
	seqint->Append(inteng);
	for (Standard_Integer i=1; i<=refseqint->Length(); i++) {
	  seqint->Append(refseqint->Value(i));
	}

	CPPIntExt_ProcessCxx(srcEngine,api,seqint);
	
	// Maps for includes
	MS_MapOfType incmaptype;
	MS_MapOfGlobalEntity incmappack;
      // Map for Methods
	MS_MapOfMethod expmap;
	
	
	CPPIntExt_LoadMethods(aMeta,srcEngine,api,expmap,incmaptype,incmappack,seqint);
	
	CPPIntExt_ProcessCcl(aMeta,srcEngine,api,incmaptype,incmappack);

	CPPIntExt_ProcessEngineInit(aMeta,srcEngine,api,incmaptype);

	api->CloseFile("Enginefileinit");
      }
      else {
	ErrorMsg() << "CPPInt_EnginExtract" << "Cannot open file " << FileNameInit->ToCString() << endm;
	Standard_NoSuchObject::Raise();
      }
      
      api->CloseFile("Enginefilell");
      
    }
    else {
      ErrorMsg()  << "CPPInt_EnginExtract" << "Cannot open file " << FileNamell->ToCString() << endm;
      Standard_NoSuchObject::Raise();
    }
    api->CloseFile("Enginefilecxx");
      
  }
  else {
    ErrorMsg()  << "CPPInt_EnginExtract" << "Cannot open file " << FileNamexx->ToCString() << endm;
    Standard_NoSuchObject::Raise();
  }
}
  

// Standard extractor API : launch the extraction of Interface or Engine
//                          files from <aName>.
// 

void CPPInt_Extract(const Handle(MS_MetaSchema)& aMeta,
		    const Handle(TCollection_HAsciiString)& aName,
		    const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
		    const Handle(TCollection_HAsciiString)& outdir,
		    const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
		    const Standard_CString)
{
  // before begining, we look if the entity is an Interface...
  //
  if (aMeta->IsInterface(aName)) {
    CPPInt_InterfExtract(aMeta,aName,edlsfullpath,outdir,outfile);
  }
  else {
    ErrorMsg() << "CPPInt_Extract" << aName->ToCString() << " is not an interface" << endm;
    Standard_NoSuchObject::Raise();
  }
  
}

void CPPEng_Extract(const Handle(MS_MetaSchema)& aMeta,
		    const Handle(TCollection_HAsciiString)& aName,
		    const Handle(TColStd_HSequenceOfHAsciiString)& edlsfullpath,
		    const Handle(TCollection_HAsciiString)& outdir,
		    const Handle(TColStd_HSequenceOfHAsciiString)& outfile,
		    const Standard_CString)
{
  // before begining, we look if the entity is an Engine...
  //
  if (aMeta->IsEngine(aName)) {
    CPPInt_EnginExtract(aMeta,aName,edlsfullpath,outdir,outfile);
  }
  else {
    ErrorMsg() << "CPPEng_Extract" << aName->ToCString() << " is not an engine" << endm;
    Standard_NoSuchObject::Raise();
  }
  
}

