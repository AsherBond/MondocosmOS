// File:	WOKUtils_SearchIterator.cxx
// Created:	Wed Sep 27 12:05:56 1995
// Author:	Jean GAUTIER
//		<jga@cobrax>

#include <WOKTools_Messages.hxx>

#include <TCollection_HAsciiString.hxx>

#include <OSD_KindFile.hxx>

#include <OSD_File.hxx>
#include <WOKUtils_SearchList.hxx>
#include <WOKUtils_HSequenceOfPath.hxx>

#include <WOKUtils_SearchIterator.ixx>


//=======================================================================
//function : WOKUtils_SearchIterator
//purpose  : 
//=======================================================================
 WOKUtils_SearchIterator::WOKUtils_SearchIterator(const Handle(WOKUtils_SearchList)& alist, const Handle(TCollection_HAsciiString)& afile)
{

  mylist = alist;
  myidx  = 1;
  myfile = afile;
  Next();
}

//=======================================================================
//function : Value
//purpose  : 
//=======================================================================
Handle(WOKUtils_Path) WOKUtils_SearchIterator::Value() const 
{
  return myvalue;
}

//=======================================================================
//function : Next
//purpose  : 
//=======================================================================
void WOKUtils_SearchIterator::Next()
{

  while(myidx <= mylist->List()->Length())
    {
      Handle(WOKUtils_Path)            apath =  mylist->List()->Value(myidx);
      Handle(TCollection_HAsciiString) aname = apath->Name();
      OSD_File afd(OSD_Path(aname->ToCString()));
      
      switch(afd.KindOfFile())
	{
	case OSD_FILE:
	  myvalue = apath;
	  myidx++;
	  return;

	case OSD_DIRECTORY:
	  {
	    Handle(WOKUtils_Path) thepath;
	    
	    thepath = new WOKUtils_Path(aname, myfile);
	    
	    if(thepath->Exists() == Standard_True) 
	      {
		myvalue = thepath;
		myidx++;
		return;
	      }
	  }
	  break;
	default:
	  ErrorMsg() << "WOKUtils_Param::Load" << "Invalid type for file : " << apath->Name() << endm;
	}
      myidx++;
    }
  myvalue = Handle(WOKUtils_Path)();
}

//=======================================================================
//function : More
//purpose  : 
//=======================================================================
Standard_Boolean WOKUtils_SearchIterator::More() const 
{
  if(myvalue.IsNull() == Standard_False) return Standard_True;
  return Standard_False;
}

