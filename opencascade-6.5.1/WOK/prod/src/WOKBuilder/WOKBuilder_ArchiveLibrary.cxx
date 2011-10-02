// File:	WOKBuilder_ArchiveLibrary.cxx
// Created:	Mon Oct 16 17:15:03 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKTools_Messages.hxx>

#include <WOKBuilder_ArchiveLibrary.ixx>

//=======================================================================
//function : WOKBuilder_ArchiveLibrary
//purpose  : 
//=======================================================================
WOKBuilder_ArchiveLibrary::WOKBuilder_ArchiveLibrary(const Handle(WOKUtils_Path)& apath) 
: WOKBuilder_Library(apath)
{
}

//=======================================================================
//function : WOKBuilder_ArchiveLibrary
//purpose  : 
//=======================================================================
 WOKBuilder_ArchiveLibrary::WOKBuilder_ArchiveLibrary(const Handle(TCollection_HAsciiString)& aname, 
						      const Handle(WOKUtils_Path)& adir, 
						      const WOKBuilder_LibReferenceType areftype)
: WOKBuilder_Library(aname,adir,areftype)
{
}


//=======================================================================
//function : GetLibFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_ArchiveLibrary::GetLibFileName(const WOKUtils_Param& params)
{
  Handle(TCollection_HAsciiString) astr;

  params.Set("%LDAR_LibName", Name()->ToCString());

  astr = params.Eval("LDAR_FileName");

  return astr;
}

//=======================================================================
//function : GetLibFileName
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_ArchiveLibrary::GetLibFileName(const WOKUtils_Param& params,
									   const Handle(TCollection_HAsciiString)& aname)
{
  Handle(TCollection_HAsciiString) astr;

  params.Set("%LDAR_LibName", aname->ToCString());

  astr = params.Eval("LDAR_FileName");

  return astr;
}
