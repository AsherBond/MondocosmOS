// File:	WOKUtils_SearchList.cxx
// Created:	Wed Sep 27 11:41:30 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>


#include <OSD_KindFile.hxx>

#include <WOKTools_Messages.hxx>

#include <OSD_File.hxx>

#include <WOKUtils_SearchList.ixx>

//=======================================================================
//function : WOKUtils_SearchList
//purpose  : 
//=======================================================================
 WOKUtils_SearchList::WOKUtils_SearchList()
{
  mylist = new WOKUtils_HSequenceOfPath;
}

//=======================================================================
//function : WOKUtils_SearchList
//purpose  : 
//=======================================================================
 WOKUtils_SearchList::WOKUtils_SearchList(const Handle(WOKUtils_SearchList)& another)
{
  mylist = new WOKUtils_HSequenceOfPath;
  mylist->Append(another->List());
}

//=======================================================================
//function : List
//purpose  : 
//=======================================================================
Handle(WOKUtils_HSequenceOfPath)  WOKUtils_SearchList::List() const
{
  return mylist;
}

//=======================================================================
//function : AddPriorPath
//purpose  : 
//=======================================================================
void WOKUtils_SearchList::AddPriorPath(const Handle(WOKUtils_Path)& apath)
{
  mylist->Prepend(apath);
}

//=======================================================================
//function : AddNonPriorPath
//purpose  : 
//=======================================================================
void WOKUtils_SearchList::AddNonPriorPath(const Handle(WOKUtils_Path)& apath)
{
  mylist->Append(apath);
}

//=======================================================================
//function : SearchFile
//purpose  : 
//=======================================================================
Handle(WOKUtils_Path) WOKUtils_SearchList::SearchFile(const Handle(TCollection_HAsciiString)& thefile)
{
  for(Standard_Integer index = 1; index <= mylist->Length() ; index++)
    {
      Handle(WOKUtils_Path)            apath =  mylist->Value(index);
      Handle(TCollection_HAsciiString) aname = apath->Name();
      OSD_File afd(OSD_Path(aname->ToCString()));

      switch(afd.KindOfFile())
	{
	case OSD_FILE:
	  break;
	case OSD_DIRECTORY:
	  {
	    Handle(WOKUtils_Path) thepath;
	    
	    thepath = new WOKUtils_Path(aname, thefile);
	    
	    if(thepath->Exists() == Standard_True) 
	      {
		return thepath;
	      }
	  }
	  break;
	default:
	  ErrorMsg() << "WOKUtils_Param::Load" << "Invalid type for file : " << aname << endm;
	}
      
    }
  return Handle(WOKUtils_Path) ();
}


