// File:	WOKBuilder_Library.cxx
// Created:	Mon Oct 16 17:18:11 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKBuilder_Library.ixx>


//=======================================================================
//function : WOKBuilder_Library
//purpose  : 
//=======================================================================
WOKBuilder_Library::WOKBuilder_Library(const Handle(WOKUtils_Path)& apath)
  : WOKBuilder_Entity(apath), myreftype(WOKBuilder_FullPath)
{
}

//=======================================================================
//function : WOKBuilder_Library
//purpose  : 
//=======================================================================
WOKBuilder_Library::WOKBuilder_Library(const Handle(TCollection_HAsciiString)& aname, 
				       const Handle(WOKUtils_Path)& adir, 
				       const WOKBuilder_LibReferenceType areftype) 
: WOKBuilder_Entity(Handle(WOKUtils_Path)())
{
  myname    = aname;
  mydir     = adir;
  myreftype = areftype;
}

//=======================================================================
//function : SetReferenceType
//purpose  : 
//=======================================================================
void WOKBuilder_Library::SetReferenceType(const WOKBuilder_LibReferenceType atype)
{
  myreftype = atype;
}

//=======================================================================
//function : ReferenceType
//purpose  : 
//=======================================================================
WOKBuilder_LibReferenceType WOKBuilder_Library::ReferenceType() const 
{
  return myreftype;
}

//=======================================================================
//function : SetName
//purpose  : 
//=======================================================================
void WOKBuilder_Library::SetName(const Handle(TCollection_HAsciiString)& aname)
{
  myname    = aname;
}

//=======================================================================
//function : Name
//purpose  : 
//=======================================================================
Handle(TCollection_HAsciiString) WOKBuilder_Library::Name() const 
{
  return myname;
}

//=======================================================================
//function : SetDirectory
//purpose  : 
//=======================================================================
void WOKBuilder_Library::SetDirectory(const Handle(WOKUtils_Path)& adir)
{
  mydir     = adir;
}

//=======================================================================
//function : Directory
//purpose  : 
//=======================================================================
Handle(WOKUtils_Path) WOKBuilder_Library::Directory() const 
{
  return mydir;
}


//=======================================================================
//function : GetPath
//purpose  : 
//=======================================================================
void WOKBuilder_Library::GetPath(const WOKUtils_Param& params) 
{
  Handle(TCollection_HAsciiString) name;
  Handle(WOKUtils_Path)            path;
  
  
  name = GetLibFileName(params);

  path = new WOKUtils_Path(Directory()->Name(), name);

  SetPath(path);
  return;
}
