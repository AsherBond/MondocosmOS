// File:	WOKernel_Locator.cxx
// Created:	Fri Jan  5 17:14:38 1996
// Author:	Jean GAUTIER
//		<jga@cobrax>

#ifdef HAVE_CONFIG_H
# include <config.h>
#endif

#include <WOKTools_Messages.hxx>

#include <WOKUtils_Path.hxx>

#include <WOKernel_Workshop.hxx>
#include <WOKernel_Session.hxx>
#include <WOKernel_DevUnit.hxx>
#include <WOKernel_FileTypeBase.hxx>
#include <WOKernel_FileType.hxx>
#include <WOKernel_File.hxx>

#include <WOKernel_Locator.ixx>

#ifdef WNT
# include <windows.h>
#endif

#ifdef HAVE_UNISTD_H
# include <unistd.h>
#endif

//---> EUG4JR
extern Standard_Boolean g_fForceLib;
//<--- EUG4JR

//=======================================================================
//function : WOKernel_Locator
//purpose  : 
//=======================================================================
 WOKernel_Locator::WOKernel_Locator(const Handle(WOKernel_Workbench)& awb)
{
  mysession = awb->Session();
  //
  // Recherche de la visibilite
  //
  Handle(TColStd_HSequenceOfHAsciiString) aseq = new TColStd_HSequenceOfHAsciiString;
  Handle(WOKernel_Workbench) abench = awb;
  
  // l'heritage des workbenchs
  while(abench.IsNull() == Standard_False)
    {
      aseq->Append(abench->FullName());
      abench = mysession->GetWorkbench(abench->Father());
    }

  // les parcels
  Handle(WOKernel_Workshop)               ashop     = mysession->GetWorkshop(awb->Nesting());
  Handle(TColStd_HSequenceOfHAsciiString) parcelseq = ashop->ParcelsInUse();

  for(Standard_Integer i=1; i<=parcelseq->Length(); i++)
    {
      aseq->Append(parcelseq->Value(i));
    }
  myvisibility = aseq;
}

//=======================================================================
//function : WOKernel_Locator
//purpose  : 
//=======================================================================
WOKernel_Locator::WOKernel_Locator(const Handle(WOKernel_Session)& asession, 
				   const Handle(TColStd_HSequenceOfHAsciiString)& avisibility)
{
  mysession = asession;
  myvisibility= avisibility;
}

//=======================================================================
//function : Session
//purpose  : 
//=======================================================================
Handle(WOKernel_Session) WOKernel_Locator::Session() const
{
  return mysession;
}

//=======================================================================
//function : Visibility
//purpose  : 
//=======================================================================
Handle(TColStd_HSequenceOfHAsciiString) WOKernel_Locator::Visibility() const
{
  return myvisibility;
}

//=======================================================================
//function : Reset
//purpose  : 
//=======================================================================
void WOKernel_Locator::Reset()
{
  mymap.Clear();
}

//=======================================================================
//function : Check
//purpose  : 
//=======================================================================
void WOKernel_Locator::Check()
{
}

//=======================================================================
//function : Locate
//purpose  : 
//=======================================================================
const Handle(WOKernel_File)& WOKernel_Locator::Locate(const Handle(TCollection_HAsciiString)& , 
					       const Handle(TCollection_HAsciiString)& )
{
  static Handle(WOKernel_File) nullresult;
  return nullresult;
}

//=======================================================================
//function : Locate
//purpose  : 
//=======================================================================
const Handle(WOKernel_File)& WOKernel_Locator::Locate(const Handle(TCollection_HAsciiString)& alocatorname)
{
  static Handle(WOKernel_File) nullresult;
  Handle(WOKernel_File) afile;

  // le Fichier a t'il deja ete localise
  if(mymap.IsBound(alocatorname)) 
    {
      WOK_TRACE {
	VerboseMsg()("WOK_LOCATOR") << "WOKernel_Locator::Locate" 
				  << "Found in cache " << mymap.Find(alocatorname)->UserPathName() << endm;
      }
      return mymap.Find(alocatorname);
    }
  else
    {
      return Locate(alocatorname, alocatorname->Token(":", 1), alocatorname->Token(":", 2), alocatorname->Token(":", 3));
    }
}

//=======================================================================
//Author   : Jean Gautier (jga)
//function : Locate
//purpose  : 
//=======================================================================
const Handle(WOKernel_File)& WOKernel_Locator::Locate(const Handle(TCollection_HAsciiString)& aunitname, 
					       const Handle(TCollection_HAsciiString)& atype, 
					       const Handle(TCollection_HAsciiString)& aname)
{
  Handle(TCollection_HAsciiString) locatorname = new TCollection_HAsciiString;
  
  locatorname->AssignCat(aunitname);
  locatorname->AssignCat(":");
  locatorname->AssignCat(atype);
  locatorname->AssignCat(":");
  locatorname->AssignCat(aname);

  return Locate(locatorname, aunitname, atype, aname);
}

//=======================================================================
//function : Locate
//purpose  : 
//=======================================================================
const Handle(WOKernel_File)& WOKernel_Locator::Locate(const Handle(TCollection_HAsciiString)& locatorname,
						const Handle(TCollection_HAsciiString)& aunitname, 
						const Handle(TCollection_HAsciiString)& atype, 
						const Handle(TCollection_HAsciiString)& aname)
{
  static Handle(WOKernel_File)    nullresult;
  Handle(WOKernel_UnitNesting) anesting;
  Handle(WOKernel_DevUnit) aunit;
  Handle(WOKernel_File)    afile;
  Handle(TCollection_HAsciiString) astr;
  Standard_Integer i;
//---> EUG4JR
  static Handle( TCollection_HAsciiString ) libType = new TCollection_HAsciiString ( "library" );
  Standard_Boolean fLibrary = atype -> IsSameString ( libType ) && g_fForceLib;
//<--- EUG4JR
  WOK_TRACE {
    VerboseMsg()("WOK_LOCATOR") << "WOKernel_Locator::Locate" 
			      << "Searching for " << locatorname << endm;
  }
  
  // le Fichier a t'il deja ete localise
  if(mymap.IsBound(locatorname)) 
    {
      WOK_TRACE {
	VerboseMsg()("WOK_LOCATOR") << "WOKernel_Locator::Locate" 
				  << "Found in cache " << mymap.Find(locatorname)->UserPathName() << endm;
      }
      return mymap.Find(locatorname);
    }

  // Non : le faire

  for(i=1; i<=myvisibility->Length(); i++)
    {
      
      WOK_TRACE {
	VerboseMsg()("WOK_LOCATOR") << "WOKernel_Locator::Locate" 
				  << "Looking in : " << myvisibility->Value(i) << endm;
      }

      anesting = mysession->GetUnitNesting(myvisibility->Value(i));

      if(!anesting.IsNull())
	{
	  if(!anesting->IsOpened()) anesting->Open();

	  aunit = mysession->GetDevUnit(anesting->NestedUniqueName(aunitname));
	  
	  if(aunit.IsNull() == Standard_False)
	    {
	      WOK_TRACE {
		VerboseMsg()("WOK_LOCATOR") << "WOKernel_Locator::Locate" 
					  << aunitname << " is present" << endm;
	      }

	      if(!aunit->IsOpened()) aunit->Open();
	      
	      if(aunit->FileTypeBase()->IsType(atype))
		{
		  astr = aunit->GetFileType(atype)->ComputePath(aunit->Params(), aname);
		  if (!astr.IsNull()) {
#ifndef WNT
		    if(!access(astr->ToCString(), F_OK)||fLibrary)
#else
		      if ( (GetFileAttributes(astr->ToCString()) != 0xFFFFFFFF)||fLibrary )
#endif
			{
			  WOK_TRACE {
			    VerboseMsg()("WOK_LOCATOR") << "WOKernel_Locator::Locate"
						      << "Found " << locatorname << " at " << aunit->UserPathName() << endm;
			  }
			  afile = new WOKernel_File(aname, aunit, aunit->FileTypeBase()->Type(atype));
			  afile->SetPath(new WOKUtils_Path(astr));
			  mymap.Bind(locatorname, afile);
			  return mymap.Find(locatorname);
			}
		  }
		}
	      else
		{
		  WarningMsg() << "WOKernel_Locator::Locate" 
			     << "Attempt to locate inexistent file type " << atype << " in " << aunit->UserPathName() << endm;
		}
	    }
	}
      else
	{
	  WOK_TRACE {
	    VerboseMsg()("WOK_LOCATOR") << "WOKernel_Locator::Locate"
				      << aunitname << " is NOT present" << endm;
	  }
	}
    }
  return nullresult;
}

//=======================================================================
//function : LocateUnit
//purpose  : 
//=======================================================================
Handle(WOKernel_DevUnit) WOKernel_Locator::LocateDevUnit(const Handle(TCollection_HAsciiString)& aunitname)
{
  Standard_Integer i;
  Handle(WOKernel_UnitNesting) anesting;
  Handle(WOKernel_DevUnit) adevunit;

  for(i=1; i<=myvisibility->Length(); i++)
    {
      
      WOK_TRACE {
	VerboseMsg()("WOK_LOCATOR") << "WOKernel_Locator::Locate" 
				  << "Looking in : " << myvisibility->Value(i) << endm;
      }
      
      anesting = mysession->GetUnitNesting(myvisibility->Value(i));
      
      if(!anesting.IsNull())
	{
	  if(!anesting->IsOpened()) anesting->Open();

	  adevunit = mysession->GetDevUnit(anesting->NestedUniqueName(aunitname));

	  if(adevunit.IsNull() == Standard_False)
	    {
	      if(!adevunit->IsOpened()) adevunit->Open();
	      return adevunit;
	    }
	}
    }

  return adevunit;
}

//=======================================================================
//function : ChangeAdd
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_Locator::ChangeAdd(const Handle(WOKernel_File)& afile)
{
  Handle(TCollection_HAsciiString) astr;

  afile->Path()->ResetMDate();
 
  astr = afile->LocatorName();

  if(mymap.IsBound(astr))
    {
      mymap(astr) = afile;
    }
  else
    {
      mymap.Bind(astr, afile);
      return Standard_True;
    }
  return Standard_False;
}

//=======================================================================
//function : ChangeRemove
//purpose  : 
//=======================================================================
Standard_Boolean WOKernel_Locator::ChangeRemove(const Handle(WOKernel_File)& afile)
{
  Handle(TCollection_HAsciiString) astr;

  afile->Path()->ResetMDate();
  
  astr = afile->LocatorName();

  if(mymap.IsBound(astr))
    {
      mymap.UnBind(astr);
      return Standard_True;
    }
  return Standard_False;
}
