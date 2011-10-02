#include <WOKStep_DLLink.ixx>


#include <TColStd_HSequenceOfHAsciiString.hxx>

#include <WOKernel_File.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_UnitNesting.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_BasicUnitTypes.hxx>

#include <WOKBuilder_DLLinker.hxx>
#include <WOKBuilder_ImportLibrary.hxx>
#include <WOKBuilder_ExportLibrary.hxx>
#include <WOKBuilder_MSTool.hxx>
#include <WOKBuilder_MSchema.hxx>

#include <WOKUtils_AdmFile.hxx>
#include <WOKUtils_Path.hxx>

#include <WOKMake_InputFile.hxx>
#include <WOKMake_HSequenceOfInputFile.hxx>

#include <WOKTools_Messages.hxx>
#include <WOKTools_MapOfHAsciiString.hxx>

#ifndef WNT
# define FASTCALL 
#else
# define FASTCALL __fastcall
#endif // WNT

Handle(WOKBuilder_Library)FASTCALL _get_unit_library(
                                         Handle(WOKernel_DevUnit       )&,
                                         Handle(TCollection_HAsciiString)&,
                                         Handle(TCollection_HAsciiString)&,
                                         const WOKUtils_Param&,
                                         Standard_Boolean = Standard_False
                                      );

//=======================================================================
//function : WOKStep_DLLink
//purpose  : 
//=======================================================================
WOKStep_DLLink::WOKStep_DLLink(const Handle(WOKMake_BuildProcess)&       abp,
			       const Handle(WOKernel_DevUnit)&           aUnit,
			       const Handle(TCollection_HAsciiString)&   aCode,
			       const Standard_Boolean                    checked,
			       const Standard_Boolean                    hidden
			      )
  : WOKStep_WNTLink(abp, aUnit, aCode, checked, hidden)
{
}

//=======================================================================
//function : ComputeTool
//purpose  : 
//=======================================================================
Handle(WOKBuilder_WNTCollector) WOKStep_DLLink::ComputeTool() 
{
  return new WOKBuilder_DLLinker(new TCollection_HAsciiString("LINK"), Unit()->Params());
}

//=======================================================================
//function : ComputeLibraryList
//purpose  : 
//=======================================================================
Handle(WOKBuilder_HSequenceOfLibrary) WOKStep_DLLink::ComputeLibraryList(const Handle(WOKMake_HSequenceOfInputFile)& execlist)
{
  Standard_Integer                                i;
  Handle(WOKernel_File)                        alib;
  Handle(WOKernel_DevUnit)                    aunit;
  Handle(WOKernel_UnitNesting)             anesting;
  Handle(WOKBuilder_ImportLibrary)           ashlib;
  Handle(TCollection_HAsciiString)          libname;
  Handle(WOKBuilder_HSequenceOfLibrary)        aseq = new WOKBuilder_HSequenceOfLibrary;
  static Handle(TCollection_HAsciiString)   libtype = new TCollection_HAsciiString("library");

  for(i=1; i<=execlist->Length(); i++)
    {
      Handle(WOKMake_InputFile)  infile = execlist->Value(i);
      Handle(WOKBuilder_Entity)  anent  = infile->BuilderEntity();
      Handle(WOKBuilder_Library) library;

      library  = Handle(WOKBuilder_Library)::DownCast(anent);

      if(!library.IsNull())
	{
	  aunit    = Unit()->Session()->GetDevUnit(infile->File()->Nesting());
	  anesting = aunit->Session()->GetUnitNesting(aunit->Nesting());

	  if(!infile->File().IsNull())
	    {
	      libname = infile->File()->Path()->FileName();
	      
	      if(infile->IsLocateAble())
		{
		  alib = Locator()->Locate(aunit->Name(), libtype, libname);
	      
		  if(alib.IsNull())
		    {
		      ErrorMsg() << "WOKStep_Link::ComputeLibraryList" 
			       << "Could not find library in unit : " << aunit->UserPathName() << endm;
		      SetFailed();
		    }
		  
		  ashlib = new WOKBuilder_ImportLibrary(alib->Path());
		}
	    }
	  else
	    {
	      ashlib = new WOKBuilder_ImportLibrary(library->Path());
	    }
	  
	  if(!ashlib.IsNull()) {aseq->Append(ashlib);ashlib.Nullify();}
	  
	}
    }

  return aseq;
} 

Handle(WOKBuilder_Library)FASTCALL _get_unit_library(
                                         Handle(WOKernel_DevUnit       )& unit,
                                         Handle(TCollection_HAsciiString)& libPath,
                                         Handle(TCollection_HAsciiString)& libName,
                                         const WOKUtils_Param&               params,
                                         Standard_Boolean                    fExport
                                     ){
                                          
 WOKBuilder_LibReferenceType refType;

 Handle(WOKBuilder_Library)retVal;
 Handle(WOKernel_UnitNesting)nesting;

 nesting = unit->Session()->GetUnitNesting(unit->Nesting());

 if( nesting->IsKind(STANDARD_TYPE(WOKernel_Workbench)))

  refType = WOKBuilder_FullPath;

 else if( nesting->IsKind(STANDARD_TYPE(WOKernel_Parcel)))
   
  refType = WOKBuilder_LongRef;

 else {

  WarningMsg() << "_get_unit_library"
             << "Unknown nesting for " << unit->UserPathName() << endm;

  return retVal;

 }  // end else

 if(fExport)

  retVal = new WOKBuilder_ExportLibrary(
                libName, new WOKUtils_Path(libPath), refType
             );

 else

  retVal = new WOKBuilder_ImportLibrary(
                libName, new WOKUtils_Path(libPath), refType
             );


 libPath->AssignCat( retVal->GetLibFileName(params));

 retVal->SetPath(new WOKUtils_Path(libPath));

 return retVal;
                                          
}  // end _get_unit_library
