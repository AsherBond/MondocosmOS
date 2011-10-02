// File:	WOKBuilder_SharedLibrary.cxx
// Created:	Mon Oct 16 17:21:13 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_SharedLibrary.ixx>


#include <Standard_ProgramError.hxx>

//=======================================================================
//function : WOKBuilder_SharedLibrary
//purpose  : 
//=======================================================================
WOKBuilder_SharedLibrary::WOKBuilder_SharedLibrary(const Handle(WOKUtils_Path)& apath) 
: WOKBuilder_Library(apath)
{
}

//=======================================================================
//function : WOKBuilder_SharedLibrary
//purpose  : 
//=======================================================================
WOKBuilder_SharedLibrary::WOKBuilder_SharedLibrary(const Handle(TCollection_HAsciiString)& aname, 
						   const Handle(WOKUtils_Path)&            adir, 
						   const WOKBuilder_LibReferenceType       areftype)
: WOKBuilder_Library(aname,adir,areftype)
{
}

//=======================================================================
//function : GetLibFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_SharedLibrary::GetLibFileName(const WOKUtils_Param& params)
{
  Handle(TCollection_HAsciiString) astr;

  params.Set("%LDSHR_LibName", Name()->ToCString());

  astr = params.Eval("LDSHR_FileName", Standard_True);
  
  return astr;
}


//=======================================================================
//function : GetLibFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_SharedLibrary::GetLibFileName(const WOKUtils_Param& params,
									   const Handle(TCollection_HAsciiString)& aname)
{
  Handle(TCollection_HAsciiString) astr;

  params.Set("%LDSHR_LibName", aname->ToCString());

  astr = params.Eval("LDSHR_FileName", Standard_True);
  
  return astr;
}
