// File:	WOKOBJS_EngLinkList.cxx
// Created:	Mon Apr 28 18:03:48 1997
// Author:	Jean GAUTIER
//		<jga@hourax.paris1.matra-dtv.fr>


#include <WOKernel_DevUnit.hxx>
#include <WOKernel_Locator.hxx>
#include <WOKernel_File.hxx>

#include <WOKBuilder_SharedLibrary.hxx>


#include <WOKMake_OutputFile.hxx>

#include <WOKOBJS_EngLinkList.ixx>

#include <WOKTools_Messages.hxx>

//=======================================================================
//function : WOKOBJS_EngLinkList
//purpose  : 
//=======================================================================
WOKOBJS_EngLinkList::WOKOBJS_EngLinkList(const Handle(WOKMake_BuildProcess)& abp, 
					 const Handle(WOKernel_DevUnit)& aunit, 
					 const Handle(TCollection_HAsciiString)& acode, 
					 const Standard_Boolean checked, 
					 const Standard_Boolean hidden) 
: WOKStep_EngLinkList(abp, aunit, acode, checked, hidden)
{
}


//=======================================================================
//Author   : Jean Gautier (jga)
//function : ComputeSchema
//purpose  : 
//=======================================================================
void WOKOBJS_EngLinkList::ComputeSchema(const Handle(WOKernel_DevUnit)& aunit, 
					const Handle(WOKMake_InputFile)& infile)
{
  Handle(TCollection_HAsciiString) astr;
  Handle(WOKernel_File)    libfile;
  static Handle(TCollection_HAsciiString) libtype = new TCollection_HAsciiString("library");
  Handle(WOKMake_OutputFile) outfile;

  astr = WOKBuilder_SharedLibrary::GetLibFileName(aunit->Params(), aunit->Name());
  
  libfile = Locator()->Locate(aunit->Name(), libtype, astr);
  
  if(libfile.IsNull())
    {
      ErrorMsg() << "WOKStep_EngLinkList::Execute"
	<< "Could not locate library file for schema : " << aunit->Name() << endm;
    }
  else
    {
      outfile = new WOKMake_OutputFile(libfile->LocatorName(), libfile, 
				       new WOKBuilder_SharedLibrary(libfile->Path()), libfile->Path());
      outfile->SetLocateFlag(Standard_True);
      outfile->SetReference();
      
      AddExecDepItem(infile,outfile, Standard_True);
    } 
 return;
}
