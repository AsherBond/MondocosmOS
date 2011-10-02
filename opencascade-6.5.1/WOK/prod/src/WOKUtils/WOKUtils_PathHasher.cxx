// File:	WOKUtils_PathHasher.cxx
// Created:	Mon Jun 26 17:56:47 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <WOKUtils_PathHasher.ixx>

#include <WOKTools_HAsciiStringHasher.hxx>

#include <TCollection_HAsciiString.hxx>

Standard_Integer WOKUtils_PathHasher::HashCode(const Handle(WOKUtils_Path)& apath)
{
  if(apath.IsNull() != Standard_True)
    {
      return WOKTools_HAsciiStringHasher::HashCode(apath->Name());
    }
  else return 0;
}

Standard_Boolean WOKUtils_PathHasher::IsEqual(const Handle(WOKUtils_Path)& path1, const Handle(WOKUtils_Path)& path2)
{
   return path1->Name()->IsSameString(path2->Name());
}

